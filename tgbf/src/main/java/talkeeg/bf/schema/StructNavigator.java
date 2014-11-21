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

package talkeeg.bf.schema;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * cursor for iterating over struct internal structure tree <p/>
 * support iteration between levels of structures tree, and iterating over structure fields on current level
 * Created by wayerr on 21.11.14.
 */
public final class StructNavigator {
    private final Struct struct;
    private Deque<SchemaEntry> stack = new ArrayDeque<>();
    private SchemaEntry current;
    /**
     * field index on current level
     */
    private int index = -1;

    public StructNavigator(Struct struct) {
        this.struct = struct;
    }

    /**
     * move cursor to next element in current level
     * @return
     */
    public SchemaEntry next() {
        if(!hasNext()) {
            throw new NoSuchElementException("No more elements at current level");
        }
        index += 1;
        CompositeSchemaEntry root = getCurrentRoot();
        current = root.getChilds().get(index);
        return current;
    }

    /**
     * parent element of current level
     * @return
     */
    public CompositeSchemaEntry getCurrentRoot() {
        SchemaEntry currentLevelRoot = stack.peekLast();
        if(!(currentLevelRoot instanceof CompositeSchemaEntry)) {
            throw new RuntimeException("current level root " + currentLevelRoot + " is not an instance of " + CompositeSchemaEntry.class);
        }
        return (CompositeSchemaEntry) currentLevelRoot;
    }

    /**
     * check possibility of moving to {@link #next() next element in current level}
     * @return
     */
    public boolean hasNext() {
        SchemaEntry currentLevelRoot = stack.peekLast();
        if(!(currentLevelRoot instanceof CompositeSchemaEntry)) {
            return false;
        }

        CompositeSchemaEntry root = (CompositeSchemaEntry) currentLevelRoot;
        return index < root.getChilds().size();
    }
}
