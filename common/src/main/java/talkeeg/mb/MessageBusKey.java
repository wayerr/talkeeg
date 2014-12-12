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

package talkeeg.mb;

import java.io.Serializable;

/**
 * key for message bus
 * Created by wayerr on 12.12.14.
 */
public final class MessageBusKey<T> implements Serializable {
    private final String id;
    private final Class<T> type;

    /**
     * create key
     * @param id string identifier of key
     * @param type type of events accepted by associated bus
     */
    public MessageBusKey(String id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public static <T> MessageBusKey<T> create(String id, Class<T> type) {
        return new MessageBusKey<>(id, type);
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return type;
    }

    /**
     * check that specified object can be casted to type of this key
     * @param object
     */
    public void check(T object) {
        this.type.cast(object);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof MessageBusKey)) {
            return false;
        }

        MessageBusKey that = (MessageBusKey)o;

        if(id != null? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if(type != null? !type.equals(that.type) : that.type != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null? id.hashCode() : 0;
        result = 31 * result + (type != null? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MessageBusKey{" +
          "id='" + id + '\'' +
          ", type=" + type +
          '}';
    }
}
