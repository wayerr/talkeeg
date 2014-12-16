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
import talkeeg.common.ipc.IpcUtil;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.ClientAddresses;
import talkeeg.common.util.TgAddress;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
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
        private List<ClientAddress> addresses;

        private Entry(Int128 clientId) {
            this.clientId = clientId;
        }

        private synchronized void setAddresses(List<ClientAddress> addresses) {
            this.addresses = ImmutableList.copyOf(addresses);
        }

        private synchronized List<ClientAddress> getAddresses() {
            if(this.addresses == null) {//we don`t must return null
                return Collections.emptyList();
            }
            return addresses;
        }

        public List<ClientAddress> getSuitableAddress(Set<ClientAddress> currentAddresses) {
            final List<ClientAddress> addresses = getAddresses();
            final Set<String> networks = new HashSet<>();
            final List<ClientAddress> suitableAddreses = new ArrayList<>();
            for(ClientAddress currentAddress: currentAddresses) {
                final String networkAddress = IpcUtil.getNetworkAddress(currentAddress.getValue());
                networks.add(networkAddress);
            }
            for(ClientAddress address: addresses) {
                if(address.isExternal()) {
                    suitableAddreses.add(address);
                } else {
                    final String networkAddress = IpcUtil.getNetworkAddress(address.getValue());
                    if(networkAddress != null && networks.contains(networkAddress)) {
                        suitableAddreses.add(address);
                    }
                }
            }

            return suitableAddreses;
        }
    }

    private final ConcurrentMap<Int128, Entry> map = new ConcurrentHashMap<>();
    private final CurrentAddressesService currentAddresses;

    @Inject
    ClientsAddressesService(CurrentAddressesService currentAddresses) {
        this.currentAddresses = currentAddresses;
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
     * most suitable addresses of client, list ordered by suitability <p/>
     * suitability resolved by analyzing current client ip and history
     * @param clientId
     * @return
     */
    public List<ClientAddress> getSuitableAddress(Int128 clientId) {
        Entry entry = map.get(clientId);
        Set<ClientAddress> currentAddresses = this.currentAddresses.getAddreses();
        return entry.getSuitableAddress(currentAddresses);
    }
}
