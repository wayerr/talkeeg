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

/**
 * utilities for message buses
 * Created by wayerr on 12.12.14.
 */
public final class MessageBuses {

    /**
     * create new unregistered instance of message bus
     * @param key identifier of message bus
     * @param <T>
     * @return
     */
    public static <T> MessageBus<T> create(MessageBusKey<T> key) {
        return new MessageBusImpl<>(key, null);
    }
}
