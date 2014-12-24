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
 * message bus iface
 * Created by wayerr on 12.12.14.
 */
public interface MessageBus<T> extends Listener<T> {
    /**
     * unique identified of bus in registry
     * @return
     */
    MessageBusKey<T> getKey();

    /**
     * send specified event to subscribed listeners
     * @param event
     */
    @Override
    void listen(T event);

    /**
     * register listener
     * @param listener
     */
    void register(Listener<? super T> listener);

    /**
     * unregister listener
     * @param listener
     */
    void unregister(Listener<? super T> listener);
}
