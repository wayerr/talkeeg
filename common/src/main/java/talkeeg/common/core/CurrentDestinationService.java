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

    public static final MessageBusKey<CurrentDestinationService.Event> MB_KEY = MessageBusKey.create("tg.CurrentDestinationService", Event.class);

    private final AcquaintedClientsService acquaintedClients;
    private final AcquaintedUsersService acquaintedUsers;
    private final Listener<ChangeItemEvent<AcquaintedClientsService, AcquaintedClient>> acquaintedClientsListener =
      new Listener<ChangeItemEvent<AcquaintedClientsService, AcquaintedClient>>() {
        @Override
        public void listen(ChangeItemEvent<AcquaintedClientsService, AcquaintedClient> event) throws Exception {
            if(event.getModification() == Modification.DELETE) {
                selectedClientId.compareAndSet(event.getItem().getId(), null);
            }
        }
    };
    private final Listener<ChangeItemEvent<AcquaintedUsersService, AcquaintedUser>> acquaintedUserdListener =
      new Listener<ChangeItemEvent<AcquaintedUsersService, AcquaintedUser>>() {
        @Override
        public void listen(ChangeItemEvent<AcquaintedUsersService, AcquaintedUser> event) throws Exception {
            if(event.getModification() == Modification.DELETE) {
                selectedUserId.compareAndSet(event.getItem().getId(), null);
            }
        }
    };
    private final MessageBusRegistry registry;
    private final ThreadSafeRef<Int128> selectedUserId = ThreadSafeRef.create();
    private final ThreadSafeRef<Int128> selectedClientId = ThreadSafeRef.create();


    @Inject
    public CurrentDestinationService(MessageBusRegistry registry, AcquaintedClientsService acquaintedClients, AcquaintedUsersService acquaintedUsers) {
        this.registry = registry;
        this.acquaintedClients = acquaintedClients;
        this.acquaintedUsers = acquaintedUsers;
        registry.getOrCreateBus(AcquaintedClientsService.MB_KEY).register(acquaintedClientsListener);
        registry.getOrCreateBus(AcquaintedUsersService.MB_KEY).register(acquaintedUserdListener);
    }

    public void setSelectedUserId(Int128 selectedUserId) {
        this.selectedUserId.set(selectedUserId);
    }

    public Int128 getSelectedUserId() {
        return this.selectedUserId.get();
    }

    public AcquaintedUser getSelectedUser() {
        final Int128 local = getSelectedUserId();
        return this.acquaintedUsers.getUser(local);
    }

    public void setSelectedClientId(Int128 selectedClientId) {
        this.selectedClientId.set(selectedClientId);
    }

    public Int128 getSelectedClientId() {
        return this.selectedClientId.get();
    }

    public AcquaintedClient getSelectedClient() {
        final Int128 local = this.getSelectedClientId();
        return this.acquaintedClients.getClient(local);
    }

    public static class Event {
    }
}
