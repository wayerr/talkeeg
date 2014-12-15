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

/**
 * event of change
 * Created by wayerr on 15.12.14.
 */
public final class ChangeItemEvent<C, I> {
    private final C container;
    private final Modification modification;
    private final I item;

    /**
     * create nev event instance
     * @param container container (service) which contain modified item
     * @param modification type of modification
     * @param item created, changed or removed item
     */
    public ChangeItemEvent(C container, Modification modification, I item) {
        this.container = container;
        this.modification = modification;
        this.item = item;
    }

    /**
     * container (service) which contain modified item
     * @return
     */
    public C getContainer() {
        return container;
    }

    /**
     * type of modification
     * @return
     */
    public Modification getModification() {
        return modification;
    }

    /**
     * created, changed or removed item
     * @return
     */
    public I getItem() {
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ChangeItemEvent)) {
            return false;
        }

        ChangeItemEvent that = (ChangeItemEvent)o;

        if(container != null? !container.equals(that.container) : that.container != null) {
            return false;
        }
        if(item != null? !item.equals(that.item) : that.item != null) {
            return false;
        }
        if(modification != that.modification) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = container != null? container.hashCode() : 0;
        result = 31 * result + (modification != null? modification.hashCode() : 0);
        result = 31 * result + (item != null? item.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangeItemEvent{" +
          "container=" + container +
          ", modification=" + modification +
          ", item=" + item +
          '}';
    }
}
