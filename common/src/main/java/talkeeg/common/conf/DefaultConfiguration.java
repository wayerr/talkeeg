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

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

/**
 * provider of default configureation <p/>
 * in future we may load default configuration from jar-resource
 *
 * Created by wayerr on 15.12.14.
 */
public final class DefaultConfiguration {

    public static class Builder {

        private final Map<String, Object> map = new HashMap<>();

        public Builder put(String key, Object val) {
            this.map.put(key, val);
            return this;
        }

        public Map<String, Object> build() {
            return ImmutableMap.copyOf(this.map);
        }
    }

    public static Map<String, Object> get() {
        return builder().build();
    }

    public static Builder builder() {
        Builder builder = new Builder();
        builder.put("net.port", 11661);
        builder.put("net.publicIpServices", "http://checkip.amazonaws.com http://curlmyip.com http://www.trackip.net/ip http://whatismyip.akamai.com http://ifconfig.me/ip http://ipv4.icanhazip.com http://shtuff.it/myip/text http://cydev.ru/ip");
        return builder;
    }
}
