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
import dagger.Module;
import dagger.Provides;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.core.CacheDirsService;
import talkeeg.common.core.CoreModule;
import talkeeg.common.ipc.IpcService;
import talkeeg.common.ipc.IpcServiceManager;

import javax.inject.Singleton;

/**
 * Created by wayerr on 03.12.14.
 */
@Module(
        injects = {
                Config.class,
                IpcServiceManager.class,
                App.class,
                CacheDirsService.class
        },
        includes = {
                CoreModule.class
        }
)
final class MainModule {

    private Application app;

    MainModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Config provideConfg() {
        return ConfigImpl.builder()
                .applicationName("talkeeg-android")
                .configDirFunction(new AndroidConfigDirFunction(this.app))
                .putMap("net.port", 11661)
                .putMap("net.publicIpServices", "http://checkip.amazonaws.com http://curlmyip.com http://www.trackip.net/ip http://whatismyip.akamai.com http://ifconfig.me/ip http://ipv4.icanhazip.com http://shtuff.it/myip/text http://cydev.ru/ip")
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
    IpcServiceManager provideIpcServiceManager(Config config) {
        return new IpcServiceManager(config);
    }

}
