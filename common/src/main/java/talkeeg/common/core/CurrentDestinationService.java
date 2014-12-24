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
import talkeeg.common.util.ChangeItemEvent;
import talkeeg.common.util.ChangeValueCallback;
import talkeeg.common.util.Modification;
import talkeeg.common.util.ThreadSafeRef;
import talkeeg.mb.Listener;
import talkeeg.mb.MessageBusKey;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * storage for current data message destination
 * Created by wayerr on 23.12.14.
 */
@Singleton
public final class CurrentDestinationService {

    public static final class Event {
        private final Int128 userId;
        private final Int128 clientId;

        private Event(Int128 userId, Int128 clientId) {
            this.userId = userId;
            this.clientId = clientId;
        }

        /**
         * current userId
         * @return
         */
        public Int128 getUserId() {
            return userId;
        }

        /**
         * current clientIdRef
         * @return
         */
        public Int128 getClientId() {
            return clientId;
        }

        @Override
        public String toString() {
            return "Event{" +
              "userId=" + userId +
              ", clientId=" + clientId +
              '}';
        }
    }

    public static final MessageBusKey<CurrentDestinationService.Event> MB_KEY = MessageBusKey.create("tg.CurrentDestinationService", Event.class);

    private final AcquaintedClientsService acquaintedClients;
    private final AcquaintedUsersService acquaintedUsers;
    private final Listener<ChangeItemEvent<AcquaintedClientsService, AcquaintedClient>> acquaintedClientsListener =
      new Listener<ChangeItemEvent<AcquaintedClientsService, AcquaintedClient>>() {
        @Override
        public void listen(ChangeItemEvent<AcquaintedClientsService, AcquaintedClient> event) throws Exception {
            if(event.getModification() == Modification.DELETE) {
                clientIdRef.compareAndSet(event.getItem().getId(), null);
            }
        }
    };
    private final Listener<ChangeItemEvent<AcquaintedUsersService, AcquaintedUser>> acquaintedUserdListener =
      new Listener<ChangeItemEvent<AcquaintedUsersService, AcquaintedUser>>() {
        @Override
        public void listen(ChangeItemEvent<AcquaintedUsersService, AcquaintedUser> event) throws Exception {
            if(event.getModification() == Modification.DELETE) {
                userIdRef.compareAndSet(event.getItem().getId(), null);
            }
        }
    };
    private final MessageBusRegistry registry;
    private final ChangeValueCallback<ThreadSafeRef<Int128>, Int128> callback = new ChangeValueCallback<ThreadSafeRef<Int128>, Int128>() {
        @Override
        public void valueChanged(ThreadSafeRef<Int128> callSource, Int128 oldValue, Int128 newValue) {
            registry.getOrCreateBus(MB_KEY).listen(new Event(getUserId(), getClientId()));
        }
    };
    private final ThreadSafeRef<Int128> userIdRef = ThreadSafeRef.createWithCallback(this.callback);
    private final ThreadSafeRef<Int128> clientIdRef = ThreadSafeRef.createWithCallback(this.callback);


    @Inject
    public CurrentDestinationService(MessageBusRegistry registry, AcquaintedClientsService acquaintedClients, AcquaintedUsersService acquaintedUsers) {
        this.registry = registry;
        this.acquaintedClients = acquaintedClients;
        this.acquaintedUsers = acquaintedUsers;
        registry.getOrCreateBus(AcquaintedClientsService.MB_KEY).register(acquaintedClientsListener);
        registry.getOrCreateBus(AcquaintedUsersService.MB_KEY).register(acquaintedUserdListener);
    }

    public void setUserId(Int128 userId) {
        this.userIdRef.set(userId);
    }

    public Int128 getUserId() {
        return this.userIdRef.get();
    }

    public AcquaintedUser getUser() {
        final Int128 local = getUserId();
        return this.acquaintedUsers.getUser(local);
    }

    public void setClientId(Int128 clientId) {
        this.clientIdRef.set(clientId);
    }

    public Int128 getClientId() {
        return this.clientIdRef.get();
    }

    public AcquaintedClient getClient() {
        final Int128 local = this.getClientId();
        return this.acquaintedClients.getClient(local);
    }

}
