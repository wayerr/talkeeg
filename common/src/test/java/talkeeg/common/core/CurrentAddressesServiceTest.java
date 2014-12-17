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

import com.google.common.base.Suppliers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import talkeeg.common.ipc.IpcServiceManager;

/**
 *
 * Created by wayerr on 01.12.14.
 */
public class CurrentAddressesServiceTest {

    @BeforeClass
    public static void beforeClass() {
        Env.getInstance().getConfig();
    }

    @AfterClass
    public static void afterClass() {
        Env.getInstance().close();
    }

    @Test
    public void test() {
        System.out.println("test");
        CurrentAddressesService service = new CurrentAddressesService(Suppliers.ofInstance(10), new PublicIpService(Env.getInstance().getConfig()));
        System.out.println(service.getAddreses());
    }
}
