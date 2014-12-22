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
import talkeeg.common.model.ClientAddress;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

/**
 * tgbf datagrams processor
 * <p/>
 * Created by wayerr on 26.11.14.
 */
@Singleton
final class TgbfProcessor implements Io {

    private static final Logger LOG = Logger.getLogger(TgbfProcessor.class.getName());

    private final Bf bf;

    @Inject
    TgbfProcessor(Bf bf) {
        this.bf = bf;
    }

    @Override
    public IoObject read(DatagramChannel channel) throws Exception {
        final Integer bufferSize = channel.socket().getReceiveBufferSize();
        ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
        final SocketAddress remote = channel.receive(readBuffer);
        if(remote == null) {
            return null;
        }
        readBuffer.flip();
        final Object message = this.bf.read(readBuffer);
        return new IoObject(message, IpcUtil.toClientAddress(remote));
    }


    @Override
    public void write(IoObject object, DatagramChannel channel) throws Exception {

        //TODO reuse buffer
        final ByteBuffer buffer = this.bf.write(object.getMessage());
        final ClientAddress address = object.getSrcAddress();
        final InetSocketAddress socketAddress = IpcUtil.toAddress(address.getValue());
        channel.send(buffer, socketAddress);
    }
}
