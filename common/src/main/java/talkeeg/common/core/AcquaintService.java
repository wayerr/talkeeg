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

import talkeeg.common.ipc.IpcService;
import talkeeg.common.model.*;

/**
 * core service which do acquaint process between clients
 *
 * Created by wayerr on 10.12.14.
 */
public final class AcquaintService {

    private final AcquaintedUsersService acquaintedUsers;
    private final IpcService ipc;
    private final ClientsAddressesService addresses;
    private final OwnedIdentityCardsService ownedIdentityCards;

    AcquaintService(OwnedIdentityCardsService ownedIdentityCards,
                    IpcService ipcService,
                    AcquaintedUsersService acquaintedUsers,
                    ClientsAddressesService addresses) {
        this.ownedIdentityCards = ownedIdentityCards;
        this.ipc = ipcService;
        this.acquaintedUsers = acquaintedUsers;
        this.addresses = addresses;
    }

    /**
     * acquaint process over regular network, with manual verification
     * @param address
     */
    public void beginNetworkAcquaint(ClientAddress address) {
        SingleMessage.Builder builder = SingleMessage.builder();
        buildMessage(builder);
        ipc.push(address, builder.build());
    }

    protected void buildMessage(SingleMessage.Builder builder) {
        builder.setSrc(this.ownedIdentityCards.getClientId());
        builder.setId((short)0);
        builder.setCipherType(MessageCipherType.NONE);
    }

    public void acquaint(Hello hello) {
        final UserIdentityCard identityCard = hello.getIdentityCard();
        this.acquaintedUsers.acquaint(identityCard);
        final ClientAddresses clientAddresses = hello.getAddresses();
        this.addresses.updateAddress(hello.getClientId(), clientAddresses);
        SingleMessage.Builder builder = SingleMessage.builder();
        buildMessage(builder);
        for(ClientAddress address: clientAddresses.getAddresses()) {
            ipc.push(address, builder.build());
        }
    }
}
