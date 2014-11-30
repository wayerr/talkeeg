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

import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Created by wayerr on 30.11.14.
 */
public class StringUtilsTest {
    @Test
    public void testSplitTo() throws Exception {
        assertEquals(java.util.Arrays.asList("one", "two", "three"), StringUtils.splitTo(new ArrayList<String>(), " one two three", ' '));
        assertTrue(StringUtils.splitTo(new ArrayList<String>(), "   ", ' ').isEmpty());
        assertTrue(StringUtils.splitTo(new ArrayList<String>(), "", ' ').isEmpty());
        assertEquals(java.util.Arrays.asList("one"), StringUtils.splitTo(new ArrayList<String>(), " one ", ' '));
        assertEquals(java.util.Arrays.asList("one"), StringUtils.splitTo(new ArrayList<String>(), "one", ' '));
        assertEquals(java.util.Arrays.asList("one"), StringUtils.splitTo(new ArrayList<String>(), "one ", ' '));

    }
}
