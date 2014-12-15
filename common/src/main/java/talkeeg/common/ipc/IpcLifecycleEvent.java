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

/**
 * event of ipc service lifecycle
 * Created by wayerr on 15.12.14.
 */
public final class IpcLifecycleEvent {
    public enum Type {
        /**
         * event of this type appear after service was started
         */
        START,
        /**
         * event of this type appear before service will be stopped
         */
        STOP
    }
    private final Type type;
    private final IpcService service;

    public IpcLifecycleEvent(Type type, IpcService service) {
        this.type = type;
        this.service = service;
    }

    public Type getType() {
        return type;
    }

    public IpcService getService() {
        return service;
    }

    @Override
    public String toString() {
        return "IpcLifecycleEvent{" +
          "type=" + type +
          ", service=" + service +
          '}';
    }
}
