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

import talkeeg.common.conf.Config;
import talkeeg.common.model.UserIdentityCard;
import talkeeg.common.util.BinaryData;

import java.io.File;

/**
 *
 * Created by wayerr on 28.11.14.
 */
public final class OwnedIdentityCardsService {
    private final File icdir;
    private final Object lock = new Object();
    private final CryptoService cryptoService;
    private UserIdentityCard user;

    public OwnedIdentityCardsService(Config config, CryptoService cryptoService) {
        this.cryptoService = cryptoService;
        this.icdir = new File(config.getConfigDir(), "idcards");
        this.icdir.mkdirs();//create dirs if need
    }

    /**
     * get immutable identity card if current user
     * @return
     */
    public UserIdentityCard getUserIdentityCard() {
        synchronized(lock) {
            if(this.user == null) {
                UserIdentityCard.Builder builder = UserIdentityCard.builder();
                load(builder);
                //load public key in UIC
                final OwnedKeysManager keysManager = cryptoService.getOwnedKeysManager();
                builder.setKey(new BinaryData(keysManager.getPublicKey(OwnedKeyType.USER).getEncoded()));
                this.user = builder.build();
            }
            return this.user;
        }
    }

    private void load(UserIdentityCard.Builder builder) {
        //TODO load attributes and client list from confiuration file
    }
}
