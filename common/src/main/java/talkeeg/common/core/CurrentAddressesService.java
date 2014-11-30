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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;
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
    private final Cache<InetAddress, InetAddress> cache;

    CurrentAddressesService(Function<InetAddress, InetAddress> externalIpFunction) {
        this.externalIpFunction = externalIpFunction;
        if(this.externalIpFunction == null) {
            throw new IllegalArgumentException("ExternalIpFunction is null");
        }
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(CacheLoader.from(this.externalIpFunction));
    }

    public List<InetAddress> getAddreses() {
        final List<InetAddress> addresses = new ArrayList<>();
        try {
            final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while(ifaces.hasMoreElements()) {
                final NetworkInterface iface = ifaces.nextElement();
                final Enumeration<InetAddress> ifaceAddresses = iface.getInetAddresses();
                while(ifaceAddresses.hasMoreElements()) {
                    final InetAddress address = ifaceAddresses.nextElement();
                    addAddress(addresses, address);
                }
            }
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "can not retrieve network ifaces", e);
        }
        return addresses;
    }

    protected void addAddress(List<InetAddress> addresses, InetAddress address) {
        addresses.add(address);
        try {
            final InetAddress externalAddress = cache.get(address, null);
            if(externalAddress != null) {
                addresses.add(externalAddress);
            }
        } catch(ExecutionException e) {
            LOG.log(Level.SEVERE, "can not retrieve external ip for " + address, e);
        }
    }
}
