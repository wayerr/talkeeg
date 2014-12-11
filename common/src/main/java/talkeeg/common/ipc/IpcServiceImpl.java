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

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ipc service implementation
 * Created by wayerr on 26.11.14.
 */
final class IpcServiceImpl implements IpcService {
    private final Whirligig whirligig;
    private final TgbfProcessor processor;
    private final IpcServiceManager sm;
    private final Map<String, IpcCallback> handlers = new HashMap<>();

    IpcServiceImpl(IpcServiceManager serviceManager) {
        this.sm = serviceManager;
        this.processor = new TgbfProcessor(this.sm.bf, this.sm.ownedIdentityCards);
        this.whirligig = new Whirligig(this.sm.config, this.processor);
    }

    @Override
    public void push(Parcel parcel) {
        this.whirligig.push(parcel);
    }

    @Override
    public Closeable addHandler(final String action, final IpcCallback callback) {
        synchronized(this.handlers) {
            IpcCallback oldCallback = this.handlers.get(action);
            if(oldCallback != null) {
                throw new RuntimeException("can not replace " + oldCallback + " with " + callback + " on action " + action);
            }
            this.handlers.put(action, callback);
            return new Closeable() {
                @Override
                public void close() {
                    synchronized(IpcServiceImpl.this.handlers) {
                        final IpcCallback oldCallback = IpcServiceImpl.this.handlers.get(action);
                        if(oldCallback == callback) {
                            IpcServiceImpl.this.handlers.remove(action);
                        }
                    }
                }
            };
        }
    }

    Whirligig getWhirligig() {
        return whirligig;
    }
}
