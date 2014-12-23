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

import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.SingleMessage;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by wayerr on 23.12.14.
 */
@Singleton
public final class IpcServiceLoopback implements IpcService {
    public static final ClientAddress LOCALHOST = ClientAddress.builder().value("tg:[127.0.0.1]11661").build();

    private final HandlersRegistry<IpcEntryHandler> registry = new HandlersRegistry<>();
    private final SingleMessageSupport singleMessageSupport;

    @Inject
    public IpcServiceLoopback(SingleMessageSupport singleMessageSupport) {
        this.singleMessageSupport = singleMessageSupport;
    }

    @Override
    public void push(Parcel parcel) {
        try {
            SingleMessage msg = this.singleMessageSupport.build(parcel);
            IpcEntryHandlerContext context = new IpcEntryHandlerContext(this, msg, LOCALHOST);
            this.singleMessageSupport.read(context, msg);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Closeable addIpcHandler(String action, IpcEntryHandler handler) {
        return registry.register(action, handler);
    }
}
