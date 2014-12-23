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

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import talkeeg.common.conf.Config;

/**
 * client name service
 * Created by wayerr on 23.12.14.
 */
public final class ClientNameService implements Supplier<String> {

    private final Supplier<String> nameProvider;
    private final Config config;

    /**
     *
     * @param nameProvider defaut name provider
     */
    public ClientNameService(Config config, Supplier<String> nameProvider) {
        Preconditions.checkNotNull(nameProvider, "nameProvider is null");
        this.nameProvider = nameProvider;
        this.config = config;
    }

    @Override
    public String get() {
        return this.config.getValue("client.name", this.nameProvider.get());
    }
}
