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

package talkeeg.common.util;

import talkeeg.common.core.BasicAddressType;

import java.net.Inet4Address;

/**
 * utility for working with `tg:` address format
 * Created by wayerr on 16.12.14.
 */
public final class TgAddress {

    public static final int NO_NETWORK_PREFIX = -1;

    private final int port;
    private final String host;
    private final int networkprefix;

    /**
     * a address representation from TG uri scheme
     * @param host ip address or hostname
     * @param networkprefix length of network prefix
     * @param port port number
     */
    public TgAddress(String host, int networkprefix, int port) {
        this.host = host;
        this.networkprefix = networkprefix;
        this.port = port;
    }

    @Override
    public String toString() {
        return to(this);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getNetworkPrefixLength() {
        return this.networkprefix;
    }

    /**
     * parse address
     * @param string
     * @return
     */
    public static TgAddress from(String string) {
        final int portIndex = string.lastIndexOf(':');
        if(portIndex < 0) {
            throw new RuntimeException("address: '" + string + "' must contains port number");
        }
        final int indexOfColon = string.indexOf(':');
        int networkprefixlen = NO_NETWORK_PREFIX;
        final String hostString;
        if(indexOfColon != portIndex) {//it`s ipv6 address, in this address part must look as '['address']:'port
            if(string.charAt(0) != '[' || string.charAt(portIndex - 1) != ']') {
                throw new RuntimeException("ipv6 address: '" + string + "' must look like  '['address']:'port");
            }
            //we must skip '[' and ']'
            hostString = string.substring(1, portIndex - 1);
        } else {
            hostString = string.substring(0, portIndex);
        }
        final String portString = string.substring(portIndex + 1);
        final int port= Integer.parseInt(portString);
        return new TgAddress(hostString, networkprefixlen, port);
    }

    /**
     * create uri representation of address
     * @param tgAddress
     * @return
     */
    public static String to(TgAddress tgAddress) {
        return to(tgAddress.host, tgAddress.networkprefix, tgAddress.port);
    }

    /**
     * create an address uri without network prefix
     * @param host
     * @param port
     * @return
     */
    public static String to(String host, int port) {
        return to(host, NO_NETWORK_PREFIX, port);
    }

    /**
     *
     * @param host
     * @param networkPrefix
     * @param port
     * @return
     */
    public static String to(String host, int networkPrefix, int port) {
        final String portString = Integer.toString(port);
        final String addressString = "[" + host + "]:" + portString;
        return addressString;
    }
}
