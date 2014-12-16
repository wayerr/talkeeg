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

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.ClientAddresses;

import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * service which provides current addresses
 *
 * Created by wayerr on 28.11.14.
 */
public final class CurrentAddressesService {

    private static final Logger LOG = Logger.getLogger(CurrentAddressesService.class.getName());
    private final Function<InetAddress, InetAddress> externalIpFunction;
    private final LoadingCache<InetAddress, InetAddress> cache;

    /**
     * construct current addresses service
     * @param externalIpFunction function which return external ip or argument, function <b>must<b/> not return null
     */
    CurrentAddressesService(Function<InetAddress, InetAddress> externalIpFunction) {
        this.externalIpFunction = externalIpFunction;
        if(this.externalIpFunction == null) {
            throw new IllegalArgumentException("ExternalIpFunction is null");
        }
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(CacheLoader.from(this.externalIpFunction));
    }

    public ClientAddresses getClientAddreses() {
        return ClientAddresses.builder()
            .addresses(getAddreses())
            .build();
    }

    Set<ClientAddress> getAddreses() {
        final Set<ClientAddress> addresses = new HashSet<>();
        try {
            final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while(ifaces.hasMoreElements()) {
                final NetworkInterface iface = ifaces.nextElement();
                if(iface.isLoopback()) {
                    // ignore loopback iface
                    continue;
                }
                List<InterfaceAddress> interfaceAddresses = iface.getInterfaceAddresses();
                for(InterfaceAddress interfaceAddress: interfaceAddresses) {
                    final InetAddress address = interfaceAddress.getAddress();
                    addAddress(addresses, address, interfaceAddress.getNetworkPrefixLength());
                }
            }
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "can not retrieve network ifaces", e);
        }
        return addresses;
    }

    protected void addAddress(Collection<ClientAddress> addresses, InetAddress address, int netPrefixLen) {
        addresses.add(new ClientAddress(getAddressType(address), false, address.getHostAddress()));
        try {
            final InetAddress externalAddress = cache.get(address);
            if(externalAddress != null && !address.equals(externalAddress)) {
                final String hostAddress = externalAddress.getHostAddress() + "/" + netPrefixLen;
                addresses.add(new ClientAddress(getAddressType(externalAddress), true, hostAddress));
            }
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "can not retrieve external ip for " + address, e);
        }
    }

    private static BasicAddressType getAddressType(InetAddress address) {
        BasicAddressType type;
        if(address instanceof Inet6Address) {
            type = BasicAddressType.IPV6;
        } else if(address instanceof Inet4Address) {
            type = BasicAddressType.IPV4;
        } else {
            throw new RuntimeException("unsupported address type");
        }
        return type;
    }
}
