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

import com.google.common.collect.ImmutableList;
import talkeeg.bf.Int128;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.ClientAddresses;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * service which provide addresses of clients (not only acquainted) <p/>
 * in future this service must load addresses from server
 * Created by wayerr on 10.12.14.
 */
@Singleton
public final class ClientsAddressesService {
    private static class Entry {
        private final Int128 clientId;
        private final List<ClientAddress> addresses = new ArrayList<>();

        private Entry(Int128 clientId) {
            this.clientId = clientId;
        }

        private void setAddresses(List<ClientAddress> addresses) {
            synchronized(this.addresses) {
                this.addresses.clear();
                this.addresses.addAll(addresses);
            }
        }

        private List<ClientAddress> getAddresses() {
            synchronized(this.addresses) {
                return ImmutableList.copyOf(addresses);
            }
        }
    }
    private final ConcurrentMap<Int128, Entry> map = new ConcurrentHashMap<>();

    @Inject
    ClientsAddressesService() {
    }

    public void setAddresses(Int128 clientId, List<ClientAddress> addresses) {
        Entry entry = new Entry(clientId);
        Entry oldEntry = this.map.putIfAbsent(clientId, entry);
        if(oldEntry != null) {
            entry = oldEntry;
        }
        entry.setAddresses(addresses);
    }

    /**
     * addresses of client
     * @param clientId
     * @return
     */
    public List<ClientAddress> getAddresses(Int128 clientId) {
        Entry entry = map.get(clientId);
        return entry.getAddresses();
    }

    /**
     * most suitable address of client <p/>
     * suitability resolved by analyzing current client ip and history
     * @param clientId
     * @return
     */
    public ClientAddress getSuitableAddress(Int128 clientId) {
        return null;
    }
}
