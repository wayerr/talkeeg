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

package talkeeg.dc;

import dagger.Module;
import dagger.Provides;
import talkeeg.common.conf.Config;
import talkeeg.common.conf.ConfigImpl;
import talkeeg.common.core.CoreModule;
import talkeeg.common.ipc.IpcServiceManager;
import talkeeg.dc.ui.UiModule;

import javax.inject.Singleton;

/**
 * IoC container configuration module
 *
 * Created by wayerr on 26.11.14.
 */
@Module(
    injects = {
        Config.class,
        IpcServiceManager.class
    },
    includes = {
        CoreModule.class,
        UiModule.class
    }
)
final class MainModule {

    @Provides
    @Singleton
    Config provideConfg() {
        return ConfigImpl.builder()
                .applicationName("talkeeg-dc")
                .putMap("net.port", 11661)
                .putMap("net.publicIpServices", "http://checkip.amazonaws.com http://curlmyip.com http://www.trackip.net/ip http://whatismyip.akamai.com http://ifconfig.me/ip http://ipv4.icanhazip.com http://shtuff.it/myip/text http://cydev.ru/ip")
                .build();
    }

    @Provides
    @Singleton
    IpcServiceManager provideIpcServiceManager(Config config) {
        return new IpcServiceManager(config);
    }
}
