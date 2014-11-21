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

import java.util.NoSuchElementException;

/**
 * cursor for iterating over struct internal structure tree <p/>
 * support iteration between levels of structures tree, and iterating over structure fields on current level <p/>
 * using of visitor pattern for this task seemed to me inappropriate
 * Created by wayerr on 21.11.14.
 */
public final class StructNavigator {
    /**
     * this class encapsulate state of iterating on some level
     */
    private static final class LevelIterator {
        private final LevelIterator parent;
        private final CompositeSchemaEntry root;
        private SchemaEntry current;
        /**
         * field index on current level
         */
        private int index;

        private LevelIterator(LevelIterator parent, CompositeSchemaEntry root) {
            this.parent = parent;
            this.root = root;
        }

        private boolean canDescend() {
            return current instanceof CompositeSchemaEntry;
        }

        private LevelIterator getDescendingIterator() {
            if(!canDescend()) {
                throw new RuntimeException("can not descend into " + current);
            }
            CompositeSchemaEntry newRoot = (CompositeSchemaEntry) current;
            return new LevelIterator(this, newRoot);
        }

        private SchemaEntry next() {
            if(!hasNext()) {
                throw new NoSuchElementException("No more elements at current level");
            }
            this.index += 1;
            this.current = this.root.getChilds().get(index);
            return this.current;
        }

        private boolean hasNext() {
            return this.index < this.root.getChilds().size();
        }
    }

    private final Struct struct;
    private LevelIterator iterator;

    public StructNavigator(Struct struct) {
        this.struct = struct;

        this.iterator = new LevelIterator(null, this.struct);
    }

    /**
     * check possibility of {@link #descend() descending} into current entry
     * @return
     */
    public boolean canDescend() {
        return this.iterator.canDescend();
    }

    /**
     * change current levelRoot to current element
     * @see #canDescend()
     */
    public void descend() {
        this.iterator = this.iterator.getDescendingIterator();
    }

    /**
     * check possibility of {@link #ascend() ascending } to parent entry.
     * in other words it checks that parent level is available
     * @return
     */
    public boolean canAscend() {
      return this.iterator.parent != null;
    }

    public void ascend() {
        LevelIterator newIterator = this.iterator.parent;
        if(newIterator == null) {
            throw new RuntimeException("can not ascend to parent, because it is null");
        }
        this.iterator = newIterator;
    }

    /**
     * move cursor to next element in current level
     * @return
     */
    public SchemaEntry next() {
        return iterator.next();
    }

    /**
     * check possibility of moving to {@link #next() next element in current level}
     * @return
     */
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
