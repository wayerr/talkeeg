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
import talkeeg.bf.Int128;
import talkeeg.common.model.ClientIdentityCard;

import java.security.PublicKey;

/**
 * representation of acquainted user
 *
 * Created by wayerr on 28.11.14.
 */
public final class AcquaintedClient {
    private final Int128 id;
    private final PublicKey publicKey;
    private final Object lock = new Object();
    private ClientIdentityCard identityCard;

    AcquaintedClient(Int128 id, PublicKey publicKey) {
        this.id = id;
        this.publicKey = publicKey;
    }

    public Int128 getId() {
        return id;
    }

    /**
     * set identity card for current client
     * @param identityCard
     */
    public void setIdentityCard(ClientIdentityCard identityCard) {
        synchronized(lock) {
            this.identityCard = identityCard;
        }
    }

    /**
     * get identity card of current client
     * @return
     */
    public ClientIdentityCard getIdentityCard() {
        ClientIdentityCard ic;
        synchronized(lock) {
            ic = this.identityCard;
        }
        if(ic == null) {
            ic = ClientIdentityCard.builder().key(getKeyData()).build();
        }
        return ic;
    }

    /**
     * public key as binary data
     * @return
     */
    public BinaryData getKeyData() {
        return new BinaryData(publicKey.getEncoded());
    }
}
