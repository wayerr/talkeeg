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
 * common iface for any IPC entry like COMMAND or COMAND_RESULT
 *
 * Created by wayerr on 11.12.14.
 */
public interface IpcEntry {
    /**
     * unique per client id of entry (for result entry command_result.id == command.id)
     * @return id of entry
     */
    int getId();

    /**
     * action on which is registered handler
     * @see talkeeg.common.ipc.IpcService#addIpcHandler(String, talkeeg.common.ipc.IpcEntryHandler)
     * @return
     */
    String getAction();
}
