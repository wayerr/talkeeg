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

import talkeeg.mb.MessageBusRegistry;

import java.util.Map;
import java.util.Objects;

/**
 * basic config backend, which return specified defaults values
 * Created by wayerr on 15.12.14.
 */
public class DefaultConfigBackend implements ConfigBackend {
    protected final MessageBusRegistry registry;
    protected final Map<String, ?> defaults;
    protected final Object lock = new Object();

    /**
     *
     * @param registry
     * @param defaults inner code is use this map directly, therefore you must take care about thread safety and immutability of this map
     */
    public DefaultConfigBackend(MessageBusRegistry registry, Map<String, ?> defaults) {
        this.registry = registry;
        this.defaults = defaults;
    }

    @Override
    public void load(Config config) {

    }

    @Override
    public void save(Config config) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(Config config, String name, T defaultValue) {
        synchronized(this.lock) {
            Object value = get(name);
            //we check that value is not configured, but not configured as null
            if(value == null && !contains(name)) {
                if(defaultValue == null && this.defaults != null) {
                    return (T)this.defaults.get(name);
                }
                return defaultValue;
            }
            return (T)value;
        }
    }

    /**
     * you must override this method
     * @param name
     * @return
     */
    protected boolean contains(String name) {
        return false;
    }

    /**
     * you must override this method
     * @param name
     * @return
     */
    protected Object get(String name) {
        return null;
    }

    /**
     * you must override this method
     * @param name
     * @param value
     * @param <T>
     * @return
     */
    protected <T> Object put(String name, T value) {
        // we return value for suppressing of sending change events
        return value;
    }

    @Override
    public <T> void setValue(Config config, String name, T value) {
        Object old;
        synchronized(this.lock) {
            old = put(name, value);
        }
        if(!Objects.equals(old, value)) {
            this.registry.getOrCreateBus(Config.MB_KEY).listen(new ConfigEvent(config, name, value));
        }
    }

}
