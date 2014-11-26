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

import talkeeg.common.util.Arrays;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * tgbd datagrams processor
 *
 * Created by wayerr on 26.11.14.
 */
final class TgbfProcessor implements Io {

    private static final int INITIAL_BUFFER_SIZE = 1024;

    @Override
    public void read(DatagramChannel channel) throws Exception {
        ByteBuffer readBuffer = ByteBuffer.allocate(INITIAL_BUFFER_SIZE);
        final SocketAddress remote = channel.receive(readBuffer);
        if(remote == null) {
            return;
        }
        System.out.println("remote " + remote);
        try {
            channel.connect(remote);
            int res = 1;
            while(res > 0) {
                res = channel.read(readBuffer);
                if(res == 0 && readBuffer.limit() == readBuffer.position()) {//buffer is full
                    ByteBuffer newBuffer = ByteBuffer.allocate(readBuffer.capacity() * 2);
                    readBuffer.flip();
                    newBuffer.put(readBuffer);
                    readBuffer = newBuffer;
                }
            }
        } finally {
            channel.disconnect();
        }
        System.out.println("  data " + Arrays.toHexString((ByteBuffer)readBuffer.duplicate().flip()));

    }

    @Override
    public void write(DatagramChannel channel) throws Exception {

    }
}
