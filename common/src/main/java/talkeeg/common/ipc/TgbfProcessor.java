/*
 * Copyright (c) 2014, wayerr (radiofun@ya.ru).
 *
 *      This file is part of talkeeg-parent.
 *
 *      talkeeg-parent is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      talkeeg-parent is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with talkeeg-parent.  If not, see <http://www.gnu.org/licenses/>.
 */

package talkeeg.common.ipc;

import talkeeg.bf.Bf;
import talkeeg.bf.Int128;
import talkeeg.common.core.CryptoService;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.*;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * tgbf datagrams processor
 * <p/>
 * Created by wayerr on 26.11.14.
 */
@Singleton
final class TgbfProcessor implements Io {

    private static final Logger LOG = Logger.getLogger(TgbfProcessor.class.getName());
    private final HandlersRegistry<IpcEntryHandler> handlers = new HandlersRegistry<>();
    private final SingleMessageSupport singleMessageSupport;
    private final OwnedIdentityCardsService ownedIdentityCards;
    private final Bf bf;
    private final CryptoService cryptoService;

    @Inject
    TgbfProcessor(Bf bf,
                  CryptoService cryptoService,
                  OwnedIdentityCardsService ownedIdentityCards,
                  SingleMessageSupport singleMessageSupport) {
        this.bf = bf;
        this.cryptoService = cryptoService;
        this.ownedIdentityCards = ownedIdentityCards;
        this.singleMessageSupport = singleMessageSupport;
    }

    /**
     * register handler for action
     * @param action
     * @param handler
     * @return closeable instance which can unregister handler from this processor
     */
    public Closeable addHandler(String action, IpcEntryHandler handler) {
        return this.handlers.register(action, handler);
    }

    @Override
    public void read(DatagramChannel channel) throws Exception {
        final Integer bufferSize = channel.socket().getReceiveBufferSize();
        ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
        final SocketAddress remote = channel.receive(readBuffer);
        if(remote == null) {
            return;
        }
        final ClientAddress address = IpcUtil.toClientAddress(remote);
        readBuffer.flip();
        Object message = this.bf.read(readBuffer);
        if(message instanceof SingleMessage) {
            consume((SingleMessage)message, address);
        } else {
            logConsumeError(address, "unsupported message type " + message.getClass());
        }
    }

    private void consume(SingleMessage message, ClientAddress address) throws Exception {
        final Int128 dst = message.getDst();
        if(dst != null && !dst.equals(getClientId())) {
            logConsumeError(address, "SingleMessage.dst == " + dst + " , but expected null or clientId.");
        }
        final IpcEntryHandlerContext ipcEntryHandlerContext = new IpcEntryHandlerContext(message, address);
        final ReadResult<SingleMessage> result = this.singleMessageSupport.read(ipcEntryHandlerContext, message);
        if(!result.isVerified()) {
            logConsumeError(address, "SingleMessage has errors:" + result.getErrors());
            return;
        }
        final List<?> objects = result.getEntries();
        for(Object obj: objects) {
            if(!(obj instanceof IpcEntry)) {
                logConsumeError(address, "unsupported IpcEntry type '" + obj.getClass() + "'.");
                continue;
            }
            IpcEntry entry = (IpcEntry)obj;
            final String action = entry.getAction();
            IpcEntryHandler handler = this.handlers.get(action);
            if(handler == null) {
                logConsumeError(address, "No handler for '" + action + "'.");
            } else {
                handler.handle(ipcEntryHandlerContext, entry);
            }
        }
    }

    private void logConsumeError(ClientAddress remoteClientAddress, String message) {
        LOG.log(Level.SEVERE, "Message from " + remoteClientAddress.getValue() + ".\n" + message);
    }

    @Override
    public void write(Parcel parcel, DatagramChannel channel) throws Exception {

        final ClientAddress destination = parcel.getAddress();
        final InetSocketAddress socketAddress = IpcUtil.toAddress(destination.getValue());
        SingleMessage singleMessage = this.singleMessageSupport.build(parcel);
        //TODO reuse buffer
        final ByteBuffer buffer = this.bf.write(singleMessage);
        channel.send(buffer, socketAddress);
    }

    private Int128 getClientId() {
        return this.ownedIdentityCards.getClientId();
    }
}
