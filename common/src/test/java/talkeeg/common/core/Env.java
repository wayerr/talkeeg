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

package talkeeg.common.core;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import talkeeg.bf.Bf;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.conf.DefaultConfigBackend;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.util.DefaultTempDirProvider;
import talkeeg.common.util.Fs;
import talkeeg.mb.MessageBusRegistry;

import java.io.File;

/**
 * testing environment
 * Created by wayerr on 28.11.14.
 */
public final class Env implements AutoCloseable {


    private static final Env INSTANCE = new Env();
    private final Config config;
    private final Bf bf;
    private final CacheDirsService cacheDirsService;
    private final MessageBusRegistry registry = new MessageBusRegistry();

    public Env() {
        CoreModule coreModule = new CoreModule();
        this.config = ConfigImpl.builder()
          .applicationName("talkeeg-test")
          .configDirFunction(new Function<String, File>() {
              @Override
              public File apply(String appName) {
                  return new File(System.getProperty("java.io.tmpdir"), appName);
              }
          })
          .backend(new DefaultConfigBackend(registry, DefaultConfiguration.get()))
          .build();

        this.bf = coreModule.provideBf();
        CacheDirsService.DirectoryProvider provider = new DefaultTempDirProvider(this.config);
        this.cacheDirsService = new CacheDirsService(provider, provider);
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

    public Bf getBf() {
        return bf;
    }

    public CacheDirsService getCacheDirsService() {
        return cacheDirsService;
    }
}
