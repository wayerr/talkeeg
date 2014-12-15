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

import talkeeg.mb.MessageBus;
import talkeeg.mb.MessageBusKey;

import java.io.File;

/**
 * iface for encapsulate configured parameters
 *
 * Created by wayerr on 26.11.14.
 */
public interface Config {
    /**
     * key for bus of configuration events
     */
    public static final MessageBusKey<ConfigEvent> MB_KEY = MessageBusKey.create("tg.config", ConfigEvent.class);

    /**
     * application specific name in [a-z.-_] letters <p/>
     * usualy used as prefix for files, tmp dir names and etc.
     * @return
     */
    String getApplicationName();

    /**
     * retrieve specified value from configuration, if value is not configured then return defaultValue
     * @return
     */
    <T> T getValue(String name, T defaultValue);

    /**
     * set configuration value, change event will be posted into {@link #MB_KEY bus specified by key}
     * @param name
     * @param value
     * @param <T>
     */
    <T> void setValue(String name, T value);

    /**
     * root configuration node, it`s tree like mapping (by interpreting '.' as node-delimiter) for plain values
     * accessed from {@link #getValue(String, Object)} }
     * @return
     */
    Node getRoot();

    /**
     * directory where configuration will be stored
     * @return
     */
    File getConfigDir();

    /**
     * do writing config data into persistent storage
     */
    void save();
}
