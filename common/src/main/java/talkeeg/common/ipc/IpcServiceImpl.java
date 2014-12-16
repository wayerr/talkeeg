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

import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;

/**
 * ipc service implementation
 * Created by wayerr on 26.11.14.
 */
final class IpcServiceImpl implements IpcService {
    private final Whirligig whirligig;
    private final IpcServiceManager sm;

    IpcServiceImpl(IpcServiceManager serviceManager) {
        this.sm = serviceManager;
        this.whirligig = new Whirligig(this.sm.config, this.sm.processor);
    }

    @Override
    public void push(Parcel parcel) {
        this.whirligig.push(parcel);
    }

    @Override
    public Closeable addIpcHandler(String action, TgbfHandler handler) {
        return this.sm.processor.addHandler(action, handler);
    }

    Whirligig getWhirligig() {
        return whirligig;
    }
}
