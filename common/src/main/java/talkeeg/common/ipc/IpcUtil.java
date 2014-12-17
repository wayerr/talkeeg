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
import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import talkeeg.common.core.BasicAddressType;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.util.TgAddress;

import java.net.*;

/**
 * utilities for ipc
 * Created by wayerr on 10.12.14.
 */
public final class IpcUtil {
    /**
     * get network address like `a.b.c.d` or `XX:XX:..:XX` from  `tg:[XX:XX:..:XX/net-prefix-len]:port` representation
     * @param address
     * @return
     */
    public static String getNetworkAddress(String address) {
        if(Strings.isNullOrEmpty(address)) {
            return null;
        }
        final TgAddress tgAddress = TgAddress.from(address);
        if(tgAddress == null) {
            return null;
        }
        final int prefixLen = tgAddress.getNetworkPrefixLength();
        if(prefixLen == TgAddress.NO_NETWORK_PREFIX || prefixLen == 0) {
            return null;
        }
        String host = tgAddress.getHost();
        try {
            final InetAddress inetAddress = InetAddress.getByName(host);
            final byte[] value = inetAddress.getAddress();
            final int addressLenInBits = value.length * 8;
            if(addressLenInBits < prefixLen) {
                throw new RuntimeException("network prefix length " + prefixLen + "bits greater than address " + addressLenInBits + "bits");
            }
            //create network address from host address
            final int lastNetByte = prefixLen / 8;
            final int lastNetworkByteBits = prefixLen % 8;
            if(lastNetworkByteBits != 0) {
                value[lastNetByte] = (byte)(value[lastNetByte] & (0xff << (8 - lastNetworkByteBits)));
            } else {
                value[lastNetByte] = 0;
            }
            for(int i = lastNetByte + 1; i < value.length; ++i) {
                value[i] = 0;
            }
            return InetAddress.getByAddress(value).getHostAddress();
        } catch(UnknownHostException e) {
            throw new RuntimeException(" at address: " + address, e);
        }
    }

    public static InetSocketAddress toAddress(String string) {
        try {
            TgAddress tgaddress = TgAddress.from(string);
            final InetAddress inetAddress = InetAddress.getByName(tgaddress.getHost());
            final InetSocketAddress address = new InetSocketAddress(inetAddress, tgaddress.getPort());
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
        final BasicAddressType type = inetSocketAddress.getAddress() instanceof Inet4Address? BasicAddressType.IPV4 : BasicAddressType.IPV6;
        return new ClientAddress(type, false, TgAddress.to(inetSocketAddress.getHostString(), inetSocketAddress.getPort()));
    }
}
