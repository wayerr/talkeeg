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

import static org.junit.Assert.*;

public class IpcUtilTest {

    @org.junit.Test
    public void testNetworkAddress() throws Exception {
        assertEquals("10.11.12.0",  IpcUtil.getNetworkAddress("tg:[10.11.12.13/24]10"));
        assertEquals("10.11.12.0",  IpcUtil.getNetworkAddress("tg:[10.11.12.13/23]10"));
        assertEquals("10.11.8.0",  IpcUtil.getNetworkAddress("tg:[10.11.12.13/21]10"));
        assertEquals(null,  IpcUtil.getNetworkAddress("tg:[2001:0db8:0123:4567:89ab:cdef:1234:5678]10"));
        assertEquals("2001:0:0:4567:89ab:cdef:1000:0",  IpcUtil.getNetworkAddress("tg:[2001::4567:89ab:cdef:1234:5678/100]10"));
        assertEquals("2001:db8:0:0:0:0:0:0",  IpcUtil.getNetworkAddress("tg:[2001:0db8:0123:4567:89ab:cdef:1234:5678/32]10"));
    }
}