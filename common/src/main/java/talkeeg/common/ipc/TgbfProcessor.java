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

import talkeeg.bf.Arrays;
import talkeeg.bf.Bf;
import talkeeg.common.model.ClientAddress;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * tgbd datagrams processor
 *
 * Created by wayerr on 26.11.14.
 */
final class TgbfProcessor implements Io {

    private final Bf bf;

    public TgbfProcessor(Bf bf) {
        this.bf = bf;
    }

    @Override
    public void read(DatagramChannel channel) throws Exception {
        final Integer bufferSize = channel.getOption(StandardSocketOptions.SO_RCVBUF);
        ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
        final SocketAddress remote = channel.receive(readBuffer);
        if(remote == null) {
            return;
        }
        System.out.println("receive " + remote);
        System.out.println("  data " + Arrays.toHexString((ByteBuffer)readBuffer.duplicate().flip()));
    }

    @Override
    public void write(Parcel parcel, DatagramChannel channel) throws Exception {
        final ClientAddress destination = parcel.getDestination();
        final InetSocketAddress socketAddress = IpcUtil.toAddress(destination.getValue());
        //TODO reuse buffer
        final ByteBuffer buffer = bf.write(parcel.getMessage());
        channel.send(buffer, socketAddress);
    }
}
