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
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.conf.DefaultConfigBackend;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.ipc.IpcModule;
import talkeeg.common.ipc.IpcService;
import talkeeg.common.ipc.IpcServiceLoopback;
import talkeeg.common.util.DaggerServiceLocator;
import talkeeg.common.util.DefaultTempDirProvider;
import talkeeg.common.util.ServiceLocator;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Singleton;
import java.io.File;

/**
 * IoC container configuration module
 * Created by wayerr on 23.12.14.
 */
@Module(
  injects = {
    Config.class,
    ServiceLocator.class,
    CacheDirsService.class,
    IpcServiceLoopback.class,
  },
  includes = {
    CoreModule.class,
    IpcModule.class
  },
  overrides = true
)
public class EnvModule {

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
          .applicationName("talkeeg-test")
          .configDirFunction(new Function<String, File>() {
              @Override
              public File apply(String appName) {
                  return new File(System.getProperty("java.io.tmpdir"), appName);
              }
          })
          .backend(new DefaultConfigBackend(registry, DefaultConfiguration.get()))
          .build();
    }

    @Provides
    @Singleton
    CacheDirsService provideCacheDirManager(Config config) {
        final CacheDirsService.DirectoryProvider provider = new DefaultTempDirProvider(config);
        return new CacheDirsService(provider, provider);
    }

    @Provides
    @Singleton
    IpcService provideCacheDirManager(IpcServiceLoopback serviceLoopback) {
        return serviceLoopback;
    }
}
