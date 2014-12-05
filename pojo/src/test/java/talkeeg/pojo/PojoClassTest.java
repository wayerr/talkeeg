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

package talkeeg.pojo;

import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class PojoClassTest {

    public static final class Bean {
        private String data;
        private boolean flag;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }
    }

    @Test
    public void testPojo() throws Exception {
        PojoClass pojoClass = PojoManager.getInstance().getPojoClass(Bean.class);
        Bean bean = new Bean();
        bean.setData("sadsadas");
        bean.setFlag(true);
        final Property data = pojoClass.getProperty("data");
        final Property flag = pojoClass.getProperty("flag");
        assertEquals(bean.getData(), data.get(bean));
        assertEquals(bean.isFlag(), flag.get(bean));
        flag.set(bean, false);
        assertEquals(false, flag.get(bean));

    }
}