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

import dagger.ObjectGraph;
import talkeeg.bf.Bf;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.util.Fs;
import talkeeg.common.util.ServiceLocator;
import talkeeg.mb.MessageBusRegistry;

import java.util.Map;

/**
 * testing environment
 * Created by wayerr on 28.11.14.
 */
public final class Env implements AutoCloseable, ServiceLocator {

    private static final class IN {
        private static final Env STANCE = new Env("main", DefaultConfiguration.get());
    }
    private final MessageBusRegistry registry = new MessageBusRegistry();
    private final ObjectGraph objectGraph;
    private final ServiceLocator serviceLocator;

    public Env(String appName, Map<String, ?> defaults) {
        EnvModule envModule = new EnvModule(appName, defaults);
        this.objectGraph = ObjectGraph.create(envModule);
        envModule.setObjectGraph(this.objectGraph);
        CoreModule.init(this.objectGraph.get(ServiceLocator.class));
        this.serviceLocator = this.objectGraph.get(ServiceLocator.class);
    }

    public static Env getInstance() {
        return IN.STANCE;
    }

    @Override
    public void inject(Object thiz) {
        this.serviceLocator.inject(thiz);
    }

    @Override
    public <T> T get(Class<T> clazz) {
        return this.serviceLocator.get(clazz);
    }

    @Deprecated
    public Config getConfig() {
        return this.serviceLocator.get(Config.class);
    }

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
