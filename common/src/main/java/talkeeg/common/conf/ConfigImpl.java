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

import java.util.HashMap;
import java.util.Map;

/**
 * {@link Config} impelmentation
 * Created by wayerr on 26.11.14.
 */
public final class ConfigImpl implements Config {

    public static class Builder {
        private String applicationName;
        private final Map<String, Object> map = new HashMap<>();

        public String getApplicationName() {
            return applicationName;
        }

        public Builder applicationName(String applicationName) {
            setApplicationName(applicationName);
            return this;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public Map<String, Object> getMap() {
            return map;
        }

        public Builder putMap(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public void getMap(Map<String, Object> map) {
            this.map.clear();
            this.map.putAll(map);
        }


        public ConfigImpl build() {
            return new ConfigImpl(this);
        }
    }

    private final String applicationName;
    private final Map<String, Object> map = new HashMap<>();
    private final NodeImpl root;

    private ConfigImpl(Builder b) {
        this.applicationName = b.applicationName;
        this.map.putAll(b.map);
        this.root = new NodeImpl(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * application specific name in [a-z.-_] letters <p/>
     * usualy used as prefix for files, tmp dir names and etc.
     * @return
     */
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public <T> T getValue(String name, T defaultValue) {
        Object value = map.get(name);
        //we check that value is not configured, but not configured as null
        if(value == null && !map.containsKey(name)) {
            return defaultValue;
        }
        return (T)value;
    }

    @Override
    public Node getRoot() {
        return root;
    }
}
