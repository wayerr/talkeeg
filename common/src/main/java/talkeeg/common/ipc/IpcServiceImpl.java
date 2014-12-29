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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import talkeeg.common.util.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ipc service implementation
 * Created by wayerr on 26.11.14.
 */
final class IpcServiceImpl implements IpcService {
    private static final Logger LOG = Logger.getLogger(IpcServiceImpl.class.getName());
    private final Whirligig whirligig;
    private final IpcServiceManager sm;
    private final ExecutorService executor;
    private final MessageProcessor messageProcessor;

    IpcServiceImpl(IpcServiceManager serviceManager) {
        this.sm = serviceManager;
        final ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setDaemon(true);
        builder.setNameFormat(getClass().getSimpleName() + "-pool-%d");
        this.executor = Executors.newCachedThreadPool(builder.build());
        this.messageProcessor = this.sm.messageProcessor;
        this.whirligig = new Whirligig(this.sm.config, this.sm.ioProcessor, this);
    }

    @Override
    public void push(Parcel parcel) {
        try {
            IoObject ioObject = this.messageProcessor.send(parcel);
            push(ioObject);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void push(IoObject ioObject) throws Exception {
        this.whirligig.push(ioObject);
    }

    void accept(final IoObject ioObject) {
        if(ioObject == null) {
            throw new NullPointerException("ioObject is null");
        }
        this.executor.submit(new ProcessMessageTask(ioObject));
    }

    @Override
    public Closeable addIpcHandler(String action, IpcEntryHandler handler) {
        return this.sm.messageProcessor.addHandler(action, handler);
    }

    Whirligig getWhirligig() {
        return whirligig;
    }
    
    private final class ProcessMessageTask implements Runnable {
        private final IoObject ioObject;

        public ProcessMessageTask(IoObject ioObject) {
            this.ioObject = ioObject;
        }

        @Override
        public void run() {
            try {
                IpcServiceImpl.this.messageProcessor.receive(IpcServiceImpl.this, ioObject);
            } catch(Exception e) {
                LOG.log(Level.SEVERE, "at message from: " + ioObject.getSrcAddress(), e);
            }
        }
    }
}
