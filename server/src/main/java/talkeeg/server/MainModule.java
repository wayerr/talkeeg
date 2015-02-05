/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.server;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.core.CoreModule;
import talkeeg.common.ipc.IpcModule;
import talkeeg.common.util.DaggerServiceLocator;
import talkeeg.common.util.ServiceLocator;
import talkeeg.httpserver.HttpServerConfig;
import talkeeg.httpserver.HttpServerModule;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Singleton;

/**
 * IoC container configuration module
 * <p/>
 * Created by wayerr on 26.11.14.
 */
@Module(
  injects = {
    Config.class,
    ServiceLocator.class,
    BarcodeProvider.class
  },
  includes = {
    CoreModule.class,
    IpcModule.class,
    HttpServerModule.class
  }
)
final class MainModule {

    private ObjectGraph objectGraph;

    /**
     *  wee need that this method will be invoked before any `@Provides` marked methods
     */
    void setObjectGraph(ObjectGraph objectGraph) {
        this.objectGraph = objectGraph;
    }


    @Provides
    @Singleton
    ServiceLocator provideServiceLocator() {
        if(this.objectGraph == null) {
            throw new IllegalStateException("Invoke setObjectGraph before this.");
        }
        return new DaggerServiceLocator(this.objectGraph);
    }

    @Provides
    @Singleton
    Config provideConfg(MessageBusRegistry registry) {
        return ConfigImpl.builder()
          .applicationName("talkeeg-dc")
          .backend(new ConfigBackendBasedOnProperties(registry, DefaultConfiguration.get()))
          .build();
    }

    @Provides
    @Singleton
    HttpServerConfig provideHttpServerConfig() {
        return HttpServerConfig.builder()/** /.useTLS(true)/**/.build();
    }
}
