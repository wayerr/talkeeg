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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Booleans;
import talkeeg.bf.Int128;
import talkeeg.common.ipc.IpcServiceManager;
import talkeeg.common.ipc.IpcUtil;
import talkeeg.common.model.ClientAddress;

import javax.inject.Inject;
import javax.inject.Provider;
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
    private static final Comparator<? super ClientAddress> ADDRESS_COMPARATOR = new Comparator<ClientAddress>() {
        @Override
        public int compare(ClientAddress lhs, ClientAddress rhs) {
            //internal addresses is must be higher than external
            return Booleans.compare(lhs.isExternal(), rhs.isExternal());
        }
    };
    private final class Entry {
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

        private synchronized void update(ClientAddress clientAddress) {
            if(this.addresses == null) {
                this.addresses = ImmutableList.of(clientAddress);
            } else {
                final int pos = this.addresses.indexOf(clientAddress);
                if(pos == 0) {
                    return;
                }
                final ArrayList<ClientAddress> temp = new ArrayList<>(this.addresses);
                // add current address to list
                temp.add(0, clientAddress);
                if(pos != -1) {
                    //if list already has address then we need remove it
                    temp.remove(pos);
                }
            }
        }
    }

    private final ConcurrentMap<Int128, Entry> map = new ConcurrentHashMap<>();
    private final Provider<CurrentAddressesService> currentAddressesProvider;
    private final Provider<IpcServiceManager> serviceManagerProvider;

    @Inject
    ClientsAddressesService(Provider<CurrentAddressesService> currentAddressesProvider, Provider<IpcServiceManager> serviceManagerProvider) {
        this.currentAddressesProvider = currentAddressesProvider;
        this.serviceManagerProvider = serviceManagerProvider;
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
        if(entry == null) {
            return Collections.emptyList();
        }
        return entry.getAddresses();
    }

    /**
     * most suitable addresses of client, list ordered by suitability <p/>
     * suitability resolved by analyzing current client ip and history
     * @param clientId
     * @return
     */
    public List<ClientAddress> getSuitableAddress(Int128 clientId) {
        Preconditions.checkNotNull(clientId, "clientId is null");
        Entry entry = map.get(clientId);
        if(entry == null) {
            return Collections.emptyList();
        }

        final List<ClientAddress> clientAddresses = entry.getAddresses();
        final Set<String> networks = new HashSet<>();
        final List<ClientAddress> suitableAddresses = new ArrayList<>();

        final Predicate<ClientAddress> filter = serviceManagerProvider.get().getSupportedAddressFilter();

        final Set<ClientAddress> currentAddressesSet = currentAddressesProvider.get().getAddresses();
        for(ClientAddress currentAddress: currentAddressesSet) {
            if(!filter.apply(currentAddress)) {
                continue;
            }
            final String networkAddress = IpcUtil.getNetworkAddress(currentAddress.getValue());
            networks.add(networkAddress);
        }

        for(ClientAddress address: clientAddresses) {
            if(!filter.apply(address)) {
                continue;
            }
            if(address.isExternal()) {
                suitableAddresses.add(address);
            } else {
                final String networkAddress = IpcUtil.getNetworkAddress(address.getValue());
                // we must add address if it from our network or its network can not be detected
                if(networkAddress == null || networks.contains(networkAddress)) {
                    suitableAddresses.add(address);
                }
            }
        }
        Collections.sort(suitableAddresses, ADDRESS_COMPARATOR);
        return suitableAddresses;
    }

    /**
     * add address to known client addresses, and move it to top of suitable
     * @param clientId
     * @param clientAddress
     */
    public void update(Int128 clientId, ClientAddress clientAddress) {
        Entry entry = map.get(clientId);
        if(entry == null) {
            final Entry newEntry = new Entry(clientId);
            entry = map.putIfAbsent(clientId, newEntry);
            if(entry == null) {
                entry = newEntry;
            }
        }
        entry.update(clientAddress);
    }
}
