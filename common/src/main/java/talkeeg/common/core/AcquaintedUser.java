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

import talkeeg.bf.BinaryData;
import talkeeg.common.model.UserIdentityCard;
import talkeeg.bf.Int128;

import java.security.PublicKey;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * acquianted user
 *
 * Created by wayerr on 28.11.14.
 */
public final class AcquaintedUser {
    /**
     * truncated to 128bit SHA1 fingerprint of public key
     */
    private final Int128 id;
    private final PublicKey publicKey;
    private final ConcurrentMap<Int128, AcquaintedClient> clients = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private BinaryData sign;
    private UserIdentityCard identityCard;

    AcquaintedUser(Int128 id, PublicKey publicKey) {
        this.publicKey = publicKey;
        this.id = id;
    }

    public Int128 getId() {
        return id;
    }

    public BinaryData getKeyData() {
        return new BinaryData(this.publicKey.getEncoded());
    }

    public BinaryData getSign() {
        synchronized(lock) {
            return sign;
        }
    }

    public void setSign(BinaryData sign) {
        synchronized(lock) {
            if(Objects.equals(this.sign, sign)) {
                return;
            }
            this.sign = sign;
        }
    }

    public UserIdentityCard getIdentityCard() {
        synchronized(lock) {
            return identityCard;
        }
    }

    /**
     * get UserIdentityCard or create it temporary instance and return it
     * @return
     */
    public UserIdentityCard getOrCreateIdentityCard() {
        UserIdentityCard uic = getIdentityCard();
        if(uic == null) {
            uic = UserIdentityCard.builder()
              .key(getKeyData())
              .build();
        }
        return uic;
    }

    public void setIdentityCard(UserIdentityCard identityCard) {
        synchronized(lock) {
            if(Objects.equals(this.identityCard, identityCard)) {
                return;
            }
            this.identityCard = identityCard;
        }
    }
}
