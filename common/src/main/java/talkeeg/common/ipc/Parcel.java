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
import talkeeg.common.model.ClientAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * an representation of message with destination address
 *
 * Created by wayerr on 26.11.14.
 */
public final class Parcel {
    private final String action;
    private final Int128 destinationId;
    private final ClientAddress address;
    private final List<Object> messages = new ArrayList<>();

    /**
     * create instance of parcel
     * @param destinationId id of target client, maybe null
     * @param address
     */
    public Parcel(String action, Int128 destinationId, ClientAddress address) {
        this.action = action;
        this.destinationId = destinationId;
        this.address = address;
    }

    /**
     * method which used for procession this parcel
     * @see talkeeg.common.model.Constants
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * id of target client, maybe null
     * @return
     */
    public Int128 getDestinationId() {
        return destinationId;
    }

    ClientAddress getAddress() {
        return address;
    }

    public List<Object> getMessages() {
        return messages;
    }
}
