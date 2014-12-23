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

import talkeeg.bf.Int128;
import talkeeg.common.conf.Config;
import talkeeg.common.model.ClientIdentityCard;
import talkeeg.common.model.UserIdentityCard;
import talkeeg.bf.BinaryData;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 *
 * Created by wayerr on 28.11.14.
 */
@Singleton
public final class OwnedIdentityCardsService {
    private final Object lock = new Object();
    private final CryptoService cryptoService;
    private final Config config;
    private final ClientNameService clientNameService;
    private Int128 clientId;
    private Int128 userId;
    private UserIdentityCard user;
    private ClientIdentityCard client;

    @Inject
    OwnedIdentityCardsService(Config config, CryptoService cryptoService, ClientNameService clientNameService) {
        this.config = config;
        this.cryptoService = cryptoService;
        this.clientNameService = clientNameService;
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
        //TODO load attributes and client list from configuration file
        Object nick = this.config.getValue("user.nick", null);
        if(nick != null) {
            builder.putAttr(UserIdentityCard.ATTR_NICK, nick);
        }
    }

    private void load(ClientIdentityCard.Builder builder) {
        builder.putAttr(ClientIdentityCard.ATTR_NAME, this.clientNameService.get());
    }

    /**
     * immutable identity card of user
     * @return
     */
    public ClientIdentityCard getClientIdentityCard() {
        synchronized(lock) {
            if(this.client == null) {
                ClientIdentityCard.Builder builder = ClientIdentityCard.builder();
                load(builder);
                builder.setUserId(getUserId());
                //load public key in CIC
                final OwnedKeysManager keysManager = cryptoService.getOwnedKeysManager();
                builder.setKey(new BinaryData(keysManager.getPublicKey(OwnedKeyType.CLIENT).getEncoded()));
                this.client = builder.build();
            }
            return this.client;
        }
    }

    /**
     * client id, in another word fingerprint of client public key
     * @return
     */
    public Int128 getClientId() {
        synchronized(lock) {
            if(this.clientId == null) {
                this.clientId = this.cryptoService.getFingerprint(getClientIdentityCard().getKey());
            }
            return clientId;
        }
    }

    /**
     * user id, in another word fingerprint of user public key
     * @return
     */
    public Int128 getUserId() {
        synchronized(lock) {
            if(this.userId == null) {
                this.userId = this.cryptoService.getFingerprint(getUserIdentityCard().getKey());
            }
            return userId;
        }
    }
}
