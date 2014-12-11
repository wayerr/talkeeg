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
import talkeeg.common.ipc.IpcEntry;
import talkeeg.common.ipc.IpcService;
import talkeeg.common.ipc.Parcel;
import talkeeg.common.ipc.TgbfHandler;
import talkeeg.common.model.*;

import java.net.SocketAddress;
import java.util.List;

/**
 * core service which do acquaint process between clients
 *
 * Created by wayerr on 10.12.14.
 */
public final class AcquaintService {

    private final AcquaintedUsersService acquaintedUsers;
    private final AcquaintedClientsService acquaintedClients;
    private final IpcService ipc;
    private final ClientsAddressesService addresses;
    private final OwnedIdentityCardsService ownedIdentityCards;
    private final CurrentAddressesService currentAddresses;
    final TgbfHandler handlerHello = new TgbfHandler() {
        @Override
        public void handle(SocketAddress srcAddress, Command command) {
            List<Object> args = command.getArgs();
            //see createParcel() for order of arguments
            UserIdentityCard userIdentityCard = (UserIdentityCard)args.get(0);
            ClientIdentityCard clientIdentityCard = (ClientIdentityCard)args.get(1);
            ClientAddresses clientAddresses = (ClientAddresses)args.get(2);
            acquaintedUsers.acquaint(userIdentityCard);
            acquaintedClients.acquaint(clientIdentityCard);
            addresses.updateAddress(clientIdentityCard.getUserId(), clientAddresses);
            //TODO response acquaint(new ClientAddress());
        }

    };


    AcquaintService(IpcService ipcService,
                    AcquaintedUsersService acquaintedUsers,
                    AcquaintedClientsService acquaintedClients,
                    ClientsAddressesService addresses,
                    OwnedIdentityCardsService ownedIdentityCards,
                    CurrentAddressesService currentAddresses) {
        this.ipc = ipcService;
        this.acquaintedUsers = acquaintedUsers;
        this.acquaintedClients = acquaintedClients;
        this.addresses = addresses;
        this.ownedIdentityCards = ownedIdentityCards;
        this.currentAddresses = currentAddresses;

        this.ipc.addIpcHandler(Constants.ACTION_HELLO, handlerHello);
    }

    /**
     * acquaint process over regular network, with manual verification
     * @param address
     */
    public void acquaint(ClientAddress address) {
        ipc.push(createParcel(null, address));
    }

    private Parcel createParcel(Int128 dstClientId, ClientAddress address) {
        Parcel parcel = new Parcel(dstClientId, address);
        Command command = Command.builder()
          .action(Constants.ACTION_HELLO)
          .addArg(this.ownedIdentityCards.getUserIdentityCard())
          .addArg(this.ownedIdentityCards.getClientIdentityCard())
          .addArg(this.currentAddresses.getClientAddreses())
          .build();
        parcel.getMessages().add(command);
        return parcel;
    }

    public void acquaint(Hello hello) {
        final UserIdentityCard identityCard = hello.getIdentityCard();
        final ClientAddresses clientAddresses = hello.getAddresses();
        final Int128 clientId = hello.getClientId();
        this.acquaintedUsers.acquaint(identityCard);
        this.addresses.updateAddress(clientId, clientAddresses);
        for(ClientAddress address: clientAddresses.getAddresses()) {
            Parcel parcel = createParcel(clientId, address);
            ipc.push(parcel);
        }
    }
}
