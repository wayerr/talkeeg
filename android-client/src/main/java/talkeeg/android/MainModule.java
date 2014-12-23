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

package talkeeg.android;

import android.app.Application;
import android.os.Build;
import com.google.common.base.Supplier;
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.conf.DefaultConfigBackend;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.core.CacheDirsService;
import talkeeg.common.core.ClientNameService;
import talkeeg.common.core.CoreModule;
import talkeeg.common.ipc.IpcModule;
import talkeeg.common.util.DaggerServiceLocator;
import talkeeg.common.util.OS;
import talkeeg.common.util.ServiceLocator;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Singleton;

/**
 * Created by wayerr on 03.12.14.
 */
@Module(
  injects = {
    Config.class,
    App.class,
    CacheDirsService.class,
    ServiceLocator.class
  },
  includes = {
    CoreModule.class,
    IpcModule.class
  },
  overrides = true
)
final class MainModule {

    private App app;

    MainModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    ServiceLocator provideServiceLocator() {
        return new DaggerServiceLocator(this.app.getObjectGraph());
    }

    @Provides
    @Singleton
    Config provideConfg(MessageBusRegistry registry) {
        return ConfigImpl.builder()
          .applicationName("talkeeg-android")
          .configDirFunction(new AndroidConfigDirFunction(this.app))
          .backend(new DefaultConfigBackend(registry, DefaultConfiguration.get()))
          .build();
    }

    @Provides
    @Singleton
    CacheDirsService provideCacheDirManager(final App app) {
        final AndroidCacheDirectoryProvider provider = new AndroidCacheDirectoryProvider(this.app);
        return new CacheDirsService(provider, provider.getTempProvider());
    }


    @Provides
    @Singleton
    ClientNameService provideClientNameService(Config config) {
        return new ClientNameService(config, new Supplier<String>() {
            @Override
            public String get() {
                return Build.MANUFACTURER + " " + Build.PRODUCT;
            }
        });
    }
}
