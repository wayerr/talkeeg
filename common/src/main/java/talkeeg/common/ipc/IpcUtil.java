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

import com.google.common.base.Preconditions;
import talkeeg.common.core.BasicAddressType;
import talkeeg.common.model.ClientAddress;

import java.net.*;

/**
 * utilities for ipc
 * Created by wayerr on 10.12.14.
 */
public final class IpcUtil {
    public static InetSocketAddress toAddress(String string) {
        final int portIndex = string.lastIndexOf(':');
        if(portIndex < 0) {
            throw new RuntimeException("address: '" + string + "' must contains port number");
        }
        final int indexOfColon = string.indexOf(':');
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
        try {
            final InetAddress inetAddress = InetAddress.getByName(hostString);
            final InetSocketAddress address = new InetSocketAddress(inetAddress, port);
            return address;
        } catch(UnknownHostException e) {
            throw new RuntimeException(" at address: " + string, e);
        }
    }

    public static ClientAddress toClientAddress(SocketAddress address) {
        Preconditions.checkNotNull(address, "address is null");
        if(!(address instanceof InetSocketAddress)) {
            throw new RuntimeException("Unsupported socket address type: " + address.getClass());
        }
        final InetSocketAddress inetSocketAddress = (InetSocketAddress)address;
        final BasicAddressType type;
        final String portString = Integer.toString(inetSocketAddress.getPort());
        final String hostString = inetSocketAddress.getHostString();
        final String addressString;
        if(inetSocketAddress.getAddress() instanceof Inet4Address) {
            type = BasicAddressType.IPV4;
            addressString = hostString + ':' + portString;
        } else {
            type = BasicAddressType.IPV6;
            addressString = "[" + hostString + "]:" + portString;
        }
        return new ClientAddress(type, false, addressString);
    }
}
