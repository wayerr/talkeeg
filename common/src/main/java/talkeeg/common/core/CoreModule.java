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

import dagger.Module;
import dagger.Provides;
import talkeeg.common.conf.Config;

import javax.inject.Singleton;

/**
 * module for configure instances of services
 * <p>
 * Created by wayerr on 28.11.14.
 */
@Module(
    library = true, complete = false,
    injects = {
        CryptoService.class,
        OwnedIdentityCardsService.class
    }
)
public final class CoreModule {

    @Provides
    @Singleton
    CryptoService provideCryptoService(Config config) {
        return new CryptoService(config);
    }

    @Provides
    @Singleton
    OwnedIdentityCardsService provideOwnedIdentityCardsService(Config config, CryptoService cryptoService) {
        return new OwnedIdentityCardsService(config, cryptoService);
    }

    @Provides
    @Singleton
    CurrentAddressesService provideCurrentAddressesService(Config config) {
        final PublicIpService externalIpFunction = new PublicIpService(config);
        return new CurrentAddressesService(externalIpFunction);
    }
}
