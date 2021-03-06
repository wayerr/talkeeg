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

import talkeeg.bf.Int128;
import talkeeg.common.model.BaseMessage;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.MessageCipherType;
import talkeeg.common.model.SingleMessage;

/**
 * context for IpcEntryHandler
 *
 * Created by wayerr on 18.12.14.
 */
public final class IpcEntryHandlerContext<T extends BaseMessage> {
    private final T message;
    private final ClientAddress srcClientAddress;
    private final IpcService service;

    IpcEntryHandlerContext(IpcService service, T message, ClientAddress srcClientAddress) {
        this.service = service;
        this.message = message;
        this.srcClientAddress = srcClientAddress;
    }

    public IpcService getService() {
        return service;
    }

    public Int128 getSrcClientId() {
        return this.message.getSrc();
    }

    public ClientAddress getSrcClientAddress() {
        return srcClientAddress;
    }

    public T getMessage() {
        return message;
    }
}
