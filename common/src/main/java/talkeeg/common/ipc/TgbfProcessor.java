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

import com.google.common.base.Preconditions;
import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.model.*;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;

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
final class TgbfProcessor implements Io {

    private static final Logger LOG = Logger.getLogger(TgbfProcessor.class.getName());
    private final HandlersRegistry<IpcEntryHandler> handlers = new HandlersRegistry<>();
    private final IpcServiceManager manager;

    TgbfProcessor(IpcServiceManager manager) {
        this.manager = manager;
        Preconditions.checkNotNull(this.manager, "manager is null");
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
        final Integer bufferSize = channel.getOption(StandardSocketOptions.SO_RCVBUF);
        ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
        final SocketAddress remote = channel.receive(readBuffer);
        if(remote == null) {
            return;
        }
        readBuffer.flip();
        //TODO check sign on decoding
        Object message = getBf().read(readBuffer);
        if(message instanceof SingleMessage) {
            consume((SingleMessage)message, remote);
        } else {
            LOG.log(Level.SEVERE, "unsupported message type " + message.getClass() + " from " + remote);
        }
    }

    private void consume(SingleMessage message, SocketAddress remote) throws Exception {
        final Int128 dst = message.getDst();
        if(dst != null && !dst.equals(getClientId())) {
            LOG.log(Level.SEVERE, "SingleMessage.dst == " + dst + " , but expected null or clientId. It came from " + remote);
        }
        final BinaryData data = message.getData();
        final ClientAddress remoteClientAddress = IpcUtil.toClientAddress(remote);
        final IpcEntryHandlerContext ipcEntryHandlerContext = new IpcEntryHandlerContext(message.getSrc(), remoteClientAddress);
        final List<?> objects = (List<?>)getBf().read(ByteBuffer.wrap(data.getData()));
        for(Object obj: objects) {
            if(obj instanceof IpcEntry) {
                LOG.log(Level.SEVERE, "unsupported IpcEntry type '" + obj.getClass() + "'. It came from " + remote);
                continue;
            }
            IpcEntry entry = (IpcEntry)obj;
            final String action = entry.getAction();
            IpcEntryHandler handler = this.handlers.get(action);
            if(handler == null) {
                LOG.log(Level.SEVERE, "No handler for '" + action + "'. It came from " + remote);
            } else {
                handler.handle(ipcEntryHandlerContext, entry);
            }
        }
    }

    private Bf getBf() {
        return this.manager.bf;
    }

    @Override
    public void write(Parcel parcel, DatagramChannel channel) throws Exception {
        final Bf bf = getBf();

        ByteBuffer data = bf.write(parcel.getMessages());

        final ClientAddress destination = parcel.getAddress();
        final InetSocketAddress socketAddress = IpcUtil.toAddress(destination.getValue());

        SingleMessage.Builder builder = SingleMessage.builder();
        buildMessage(builder);
        builder.setData(new BinaryData(data));
        //TODO reuse buffer
        final ByteBuffer buffer = bf.write(builder.build());
        channel.send(buffer, socketAddress);
    }

    protected void buildMessage(SingleMessage.Builder builder) {
        builder.setSrc(getClientId());
        builder.setId((short)0);
        builder.setCipherType(MessageCipherType.NONE);
    }

    private Int128 getClientId() {
        return this.manager.ownedIdentityCards.getClientId();
    }
}
