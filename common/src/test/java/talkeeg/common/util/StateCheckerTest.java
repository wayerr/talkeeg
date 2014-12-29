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

import static org.junit.Assert.*;

public class StateCheckerTest {
    @Test
    public void testChecker() throws Exception {
        StateChecker.Graph<Integer> graph = StateChecker.<Integer>builder()
          .transit(0, 1, 2, 3, 4, 5)
          .transit(1, 2, 3, 4, 5)
          .transit(2, 3, 4, 5)
          .transit(3, 4, 5)
          .transit(4, 5)
          .transit(5, 0)
          .build();
        assertTrue(graph.isPossible(0, 1));
        assertTrue(graph.isPossible(3, 4));
        assertFalse(graph.isPossible(1, 1));
        assertFalse(graph.isPossible(4, 2));
        assertTrue(graph.isPossible(5, 0));
        assertTrue(graph.isPossible(0, 5));
    }
}