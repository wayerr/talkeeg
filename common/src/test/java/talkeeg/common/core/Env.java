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
import dagger.ObjectGraph;
import talkeeg.bf.Bf;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.conf.DefaultConfigBackend;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.util.DefaultTempDirProvider;
import talkeeg.common.util.Fs;
import talkeeg.common.util.ServiceLocator;
import talkeeg.mb.MessageBusRegistry;

import java.io.File;

/**
 * testing environment
 * Created by wayerr on 28.11.14.
 */
public final class Env implements AutoCloseable {

    private static final Env INSTANCE = new Env();
    private final MessageBusRegistry registry = new MessageBusRegistry();
    private final ObjectGraph objectGraph;
    private final ServiceLocator serviceLocator;

    public Env() {
        EnvModule envModule = new EnvModule();
        this.objectGraph = ObjectGraph.create(envModule);
        envModule.setObjectGraph(this.objectGraph);
        this.serviceLocator = this.objectGraph.get(ServiceLocator.class);
    }

    public static Env getInstance() {
        return INSTANCE;
    }



    @Deprecated
    public Config getConfig() {
        return this.serviceLocator.get(Config.class);
    }

    @Deprecated
    @Override
    public void close() {
        Fs.delete(getConfig().getConfigDir());
    }

    @Deprecated
    public Bf getBf() {
        return this.serviceLocator.get(Bf.class);
    }

    @Deprecated
    public CacheDirsService getCacheDirsService() {
        return this.serviceLocator.get(CacheDirsService.class);
    }
}
