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
import talkeeg.common.util.ChangeItemEvent;
import talkeeg.common.util.FileData;
import talkeeg.common.util.Modification;
import talkeeg.mb.MessageBusKey;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * storage for acquainted users and his signed identity cards
 *
 * Created by wayerr on 28.11.14.
 */
@Singleton
public final class AcquaintedUsersService {

    public static final MessageBusKey<ChangeItemEvent<AcquaintedUsersService, AcquaintedUser>> MB_KEY =
      MessageBusKey.create("tg.AcquaintedUsersService", ChangeItemEvent.class);
    private final MessageBusRegistry registry;
    private final CryptoService cryptoService;
    private final ConcurrentMap<Int128, AcquaintedUser> users = new ConcurrentHashMap<>();
    private final KeyLoader keyLoader;
    private final FileData fileData;

    @Inject
    AcquaintedUsersService(MessageBusRegistry registry, Config config, Bf bf, CryptoService cryptoService, KeyLoader keyLoader) {
        this.registry = registry;
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
            UserIdentityCard identityCard = user.getOrCreateIdentityCard();
            userIdentityCards.add(identityCard);
        }
        this.fileData.write(userIdentityCards);
    }

    /**
     * acquaint with user by his {@link talkeeg.common.model.UserIdentityCard }
     * @param identityCard
     * @return
     */
    public AcquaintedUser acquaint(UserIdentityCard identityCard) {
        Preconditions.checkNotNull(identityCard, "identityCard is null");
        final BinaryData userPublicKey = identityCard.getKey();
        final Int128 id = this.cryptoService.getFingerprint(userPublicKey);
        final PublicKey publicKey = this.keyLoader.loadPublic(userPublicKey.getData());
        AcquaintedUser user = new AcquaintedUser(id, publicKey);
        final AcquaintedUser oldUser = users.putIfAbsent(user.getId(), user);
        if(oldUser != null) {
            user = oldUser;
        }
        if(oldUser != user || !Objects.equals(user.getIdentityCard(), identityCard)) {
            user.setIdentityCard(identityCard);
            //acquainted users changed, we need save it
            save();
            registry.getOrCreateBus(MB_KEY).listen(new ChangeItemEvent<>(this, Modification.CREATE, user));
        }
        return user;
    }

    /**
     * return immutable copy of acquainted users
     * @return
     */
    public List<AcquaintedUser> getAcquaintedUsers() {
        return ImmutableList.copyOf(this.users.values());
    }

    public AcquaintedUser getUser(Int128 userId) {
        return this.users.get(userId);
    }

    /**
     * remove user specified by id
     * @param id
     * @return removed user or null
     */
    public AcquaintedUser remove(Int128 id) {
        final AcquaintedUser removed = this.users.remove(id);
        if(removed != null) {
            save();
        }
        registry.getOrCreateBus(MB_KEY).listen(new ChangeItemEvent<>(this, Modification.DELETE, removed));
        return removed;
    }
}
