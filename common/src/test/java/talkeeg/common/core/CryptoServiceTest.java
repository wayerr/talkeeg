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

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.BeforeClass;
import talkeeg.common.Env;

/**
 * Created by wayerr on 28.11.14.
 */
public class CryptoServiceTest {

    private static CryptoService service;

    @BeforeClass
    public static void setup() {
        service = new CryptoService(Env.getInstance().getConfig());
        service.init();
    }

    @AfterClass
    public static void close() {
        Env.getInstance().close();
    }

    @Test
    public void testInit() throws Exception {
        System.out.println("test");
    }
}
