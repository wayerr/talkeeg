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

import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * storage for acquainted users and his signed identity cards
 *
 * Created by wayerr on 28.11.14.
 */
public final class AcquaintedUsersService {
    private final CryptoService cryptoService;
    private final ConcurrentMap<Int128, AcquaintedUser> users = new ConcurrentHashMap<>();

    AcquaintedUsersService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    /**
     * acquaint with user by his public key
     * @param userPublicKey
     * @return
     */
    public AcquaintedUser acquaint(PublicKey userPublicKey) {
        final AcquaintedUser user = new AcquaintedUser(this.cryptoService, userPublicKey);
        final AcquaintedUser oldUser = users.putIfAbsent(user.getId(), user);
        if(oldUser != null) {
            return oldUser;
        }
        return user;
    }
}
