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

package talkeeg.common.conf;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by wayerr on 26.11.14.
 */
public class ConfigImplTest {

    @Test
    public void testNode() throws Exception {
        ConfigImpl config = getConfig();
        Node test = config.getRoot().getNode("test");
        assertNotNull(test);
        assertEquals(new Integer(9876), test.getValue("value", null));
    }

    @Test
    public void testValue() throws Exception {
        ConfigImpl config = getConfig();
        assertEquals(new Integer(1234), config.getValue("test.nonConfiguredvalue", 1234));
        assertEquals(new Integer(9876), config.getValue("test.value", null));
    }

    protected ConfigImpl getConfig() {
        return ConfigImpl.builder()
                .putMap("test.value", 9876)
                .build();
    }
}
