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

package talkeeg.common.conf;

/**
 * interface for configuration node <p/>
 *
 * Created by wayerr on 26.11.14.
 */
public interface Node {
    Node getNode(String name);

    /**
     * retrieve specified value from configuration, if value is not configured then return defaultValue
     * @param name
     * @param defaultValue
     * @param <T>
     * @return
     */
    <T> T getValue(String name, T defaultValue);

    /**
     * set configuration value, change event will be posted into {@link Config#MB_KEY bus specified by key}
     * @param name
     * @param value
     * @param <T>
     */
    <T> void setValue(String name, T value);
}
