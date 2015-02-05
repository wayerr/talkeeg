/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.httpserver.fs;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ResourceFileSystemTest {

    @Test
    public void test() throws Exception {
        ResourceFileSystem fs = new ResourceFileSystem("/prefix/");
        ResourceFileSystem.ResourceFile f = fs.fromPath("/");
        assertTrue(f.isDirectory());
        assertEquals(-1, f.getSize());

        f = fs.fromPath("/first.txt");
        assertFalse(f.isDirectory());
        assertEquals(1, f.getSize());

        assertEquals(2, fs.fromPath("/dir/one.txt").getSize());
        assertEquals(3, fs.fromPath("/dir/two.txt").getSize());
    }
    
}