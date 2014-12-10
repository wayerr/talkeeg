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

package talkeeg.common.core;

import talkeeg.bf.Int128;
import talkeeg.common.model.ClientAddresses;

import java.util.HashMap;
import java.util.Map;

/**
 * service which provide addresses of clients (not only acquainted) <p/>
 * in future this service must load addresses from server
 * Created by wayerr on 10.12.14.
 */
public final class ClientsAddressesService {
    private final Map<Int128, ClientAddresses> map = new HashMap<>();

    public void updateAddress(Int128 clientId, ClientAddresses addresses) {
        this.map.put(clientId, addresses);
    }

    /**
     * addresses of client
     * @param clientId
     * @return
     */
    public ClientAddresses getAddresses(Int128 clientId) {
        return map.get(clientId);
    }
}
