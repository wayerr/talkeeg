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

/**
 * service for inter process communication over tgbf protocol
 *
 * Created by wayerr on 26.11.14.
 */
public interface IpcService {

    /**
     * pushes parcel to queue for delivering to client
     * @param parcel
     */
    void push(Parcel parcel);

    /**
     * push low level protocol object into ipc
     * @param ioObject
     * @throws Exception
     */
    void push(IoObject ioObject) throws Exception;

    /**
     * register handler for specific IPC layer action
     * @param action
     * @param handler
     * @return a closeable instance which used for cancelling registration
     */
    Closeable addIpcHandler(String action, IpcEntryHandler handler);
}
