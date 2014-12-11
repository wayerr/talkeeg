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
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.MessageCipherType;
import talkeeg.common.model.SingleMessage;

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
 *
 * Created by wayerr on 26.11.14.
 */
final class TgbfProcessor implements Io {

    private static final Logger LOG = Logger.getLogger(TgbfProcessor.class.getName());
    private final Bf bf;
    private final OwnedIdentityCardsService ownedIdentityCards;


    public TgbfProcessor(Bf bf, OwnedIdentityCardsService ownedIdentityCards) {
        this.bf = bf;
        this.ownedIdentityCards = ownedIdentityCards;
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
        Object message = bf.read(readBuffer);
        if(message instanceof SingleMessage) {
            consume((SingleMessage)message, remote);
        } else {
            LOG.log(Level.SEVERE, "unsupported message type " + message.getClass() + " from " + remote);
        }
    }

    private void consume(SingleMessage message, SocketAddress remote) {
        final Int128 dst = message.getDst();
        if(dst != null && !dst.equals(getClientId())) {
            LOG.log(Level.SEVERE, "SingleMessage.dst == " + dst + " , but expected null or clientId. It came from " + remote);
        }
        List<Object> list = message.getData();
        System.out.println("receive message from " + remote + " to action " + message.getAction());
        for(Object entry: list) {
            System.out.println("\t"+ entry);
        }
    }

    @Override
    public void write(Parcel parcel, DatagramChannel channel) throws Exception {
        final ClientAddress destination = parcel.getAddress();
        final InetSocketAddress socketAddress = IpcUtil.toAddress(destination.getValue());
        SingleMessage.Builder builder = SingleMessage.builder();
        buildMessage(builder);
        builder.setData(parcel.getMessages());
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
        return this.ownedIdentityCards.getClientId();
    }
}
