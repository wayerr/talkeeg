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

package talkeeg.common.util;

import com.google.common.base.Preconditions;
import talkeeg.bf.Bf;
import talkeeg.bf.TgbfUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * utility for storing Bf data in file
 *
 * Created by wayerr on 10.12.14.
 */
public final class FileData {
    private final Bf bf;
    private final File file;
    private final Lock lock = new ReentrantLock();

    public FileData(Bf bf, File file) {
        this.bf = bf;
        this.file = file;
        Preconditions.checkNotNull(this.bf, "bf is null");
        Preconditions.checkNotNull(this.file, "file is null");
    }

    public void read(Callback<Object> messageCallback) {
        RandomAccessFile fis = null;
        FileChannel channel = null;
        if(!lock.tryLock()) {
            throw new RuntimeException(this + " already locked");
        }
        try {
            fis = new RandomAccessFile(this.file, "rw");
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while(channel.position() < channel.size()) {
                channel.read(buffer);
                buffer.flip();
                final int length = TgbfUtils.getEntryLength(buffer, null);
                if(length > buffer.limit()) {
                    buffer.position(buffer.limit());
                    continue;
                }
                buffer.limit(buffer.position() + length);
                final Object readed = bf.read(buffer);
                messageCallback.call(readed);
                buffer.compact();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Closeables.close(channel);
                Closeables.close(fis);
            } finally {
                lock.unlock();
            }
        }
    }

    public void write(Iterable<?> messages) {
        RandomAccessFile raf = null;
        FileChannel channel = null;
        if(!lock.tryLock()) {
            throw new RuntimeException(this + " already locked");
        }
        try {
            raf = new RandomAccessFile(this.file, "rw");
            channel = raf.getChannel();
            for(Object message: messages) {
                ByteBuffer buffer = bf.write(message);
                channel.write(buffer);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                Closeables.close(channel);
                Closeables.close(raf);
            } finally {
                lock.unlock();
            }
        }
    }
}
