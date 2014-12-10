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
import com.google.common.collect.ImmutableList;
import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.conf.Config;
import talkeeg.common.model.UserIdentityCard;
import talkeeg.common.util.Callback;
import talkeeg.common.util.FileData;

import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
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
    private final KeyLoader keyLoader;
    private final FileData fileData;

    AcquaintedUsersService(Config config, Bf bf, CryptoService cryptoService, KeyLoader keyLoader) {
        this.cryptoService = cryptoService;
        this.keyLoader = keyLoader;
        final File file = new File(config.getConfigDir(), "acquainted_users.tgbf");
        this.fileData = new FileData(bf, file);
        load();
    }

    private void load() {
        this.fileData.read(new Callback<Object>() {
            @Override
            public void call(Object value) {
                acquaint((UserIdentityCard)value);
            }
        });
    }

    private void save() {
        List<UserIdentityCard> userIdentityCards = new ArrayList<>();
        for(AcquaintedUser user: this.users.values()) {
            UserIdentityCard identityCard = user.getIdentityCard();
            if(identityCard == null) {
                identityCard = UserIdentityCard.builder()
                        .key(user.getKeyData())
                        .build();
            }
            userIdentityCards.add(identityCard);
        }
        this.fileData.write(userIdentityCards);
    }

    /**
     * acquaint with user by his public key
     * @param userPublicKey
     * @return
     */
    public AcquaintedUser acquaint(BinaryData userPublicKey) {
        Preconditions.checkNotNull(userPublicKey, "userPublicKey is null");
        final PublicKey publicKey = this.keyLoader.loadPublic(userPublicKey.getData());
        final AcquaintedUser user = new AcquaintedUser(this.cryptoService, publicKey);
        final AcquaintedUser oldUser = users.putIfAbsent(user.getId(), user);
        if(oldUser != null) {
            return oldUser;
        } else {
            //acquainted users changed, we need save it
            save();
        }
        return user;
    }

    /**
     * acquaint with user by his {@link talkeeg.common.model.UserIdentityCard }
     * @param identityCard
     * @return
     */
    public AcquaintedUser acquaint(UserIdentityCard identityCard) {
        final AcquaintedUser acquaint = acquaint(identityCard.getKey());
        acquaint.setIdentityCard(identityCard);
        return acquaint;
    }

    /**
     * return immutable copy of acquainted users
     * @return
     */
    public List<AcquaintedUser> getAcquaintedUsers() {
        return ImmutableList.copyOf(this.users.values());
    }
}
