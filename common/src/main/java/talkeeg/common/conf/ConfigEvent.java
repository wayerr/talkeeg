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
 * configuration event
 *
 * Created by wayerr on 15.12.14.
 */
public final class ConfigEvent {
    private final Config config;
    private final String name;
    private final Object value;

    public ConfigEvent(Config config, String name, Object value) {
        this.config = config;
        this.name = name;
        this.value = value;
    }

    public Config getConfig() {
        return config;
    }

    /**
     * name of changed property, may be null in this case all properties was changed
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * value of changed property
     * @return
     */
    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ConfigEvent)) {
            return false;
        }

        ConfigEvent that = (ConfigEvent)o;

        if(config != null? !config.equals(that.config) : that.config != null) {
            return false;
        }
        if(name != null? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if(value != null? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = config != null? config.hashCode() : 0;
        result = 31 * result + (name != null? name.hashCode() : 0);
        result = 31 * result + (value != null? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ConfigEvent{" +
          "config=" + config +
          ", name='" + name + '\'' +
          ", value=" + value +
          '}';
    }
}
