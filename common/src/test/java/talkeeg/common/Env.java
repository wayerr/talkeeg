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

package talkeeg.common;

import com.google.common.base.Function;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.util.Fs;

import java.io.File;
import java.nio.file.Files;

/**
 * testing environment
 * Created by wayerr on 28.11.14.
 */
public final class Env implements AutoCloseable {


    private static final Env INSTANCE = new Env();
    private final Config config;

    public Env() {
        this.config = ConfigImpl.builder()
            .applicationName("talkeeg-test")
            .configDirFunction(new Function<String, File>() {
                @Override
                public File apply(String appName) {
                    return new File(System.getProperty("java.io.tmpdir"), appName);
                }
            })
            .putMap("net.port", 11661)
            .putMap("net.publicIpServices", "http://checkip.amazonaws.com http://curlmyip.com http://www.trackip.net/ip http://whatismyip.akamai.com http://ifconfig.me/ip http://ipv4.icanhazip.com http://shtuff.it/myip/text http://cydev.ru/ip")
            .build();
    }

    public static Env getInstance() {
        return INSTANCE;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public void close() {
        Fs.delete(this.config.getConfigDir());
    }
}
