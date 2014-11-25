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

package talkeeg.bf;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by wayerr on 25.11.14.
 */
public class TgbfUtilsTest {

    @Test
    public void testGetMinimalSize() {
        System.out.println("test getMinimalSize");
        assertEquals(1, TgbfUtils.getMinimalSize(0));
        assertEquals(1, TgbfUtils.getMinimalSize(0xff-1));
        assertEquals(2, TgbfUtils.getMinimalSize(0xfff-1));
        assertEquals(2, TgbfUtils.getMinimalSize(0xffff-1));
        assertEquals(4, TgbfUtils.getMinimalSize(0xfffff-1));
    }
}
