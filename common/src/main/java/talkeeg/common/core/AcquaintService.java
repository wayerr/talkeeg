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
import talkeeg.common.ipc.*;
import talkeeg.common.model.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * core service which do acquaint process between clients
 *
 * Created by wayerr on 10.12.14.
 */
@Singleton
public final class AcquaintService {
    private static final String ACTION_ACQUAINT = "tg.acquaint";
    private static final String ACTION_ACQUAINT_RESPONSE = "tg.acquaintResponse";

    /**
     * structure for represent acquaint command data
     */
    public static final class AcquaintData {
        private final UserIdentityCard userIdentityCard;
        private final ClientIdentityCard clientIdentityCard;
        private final ClientAddresses clientAddresses;
        private final Command command;

        @SuppressWarnings("unchecked")
        private AcquaintData(Command entry) {
            this.command = entry;
            List<Object> args = (List<Object>)command.getArg();
            //see buildAcquaintCommand() for order of arguments
            this.userIdentityCard = (UserIdentityCard)args.get(0);
            this.clientIdentityCard = (ClientIdentityCard)args.get(1);
            this.clientAddresses = (ClientAddresses)args.get(2);
        }

        public ClientAddresses getClientAddresses() {
            return clientAddresses;
        }

        public ClientIdentityCard getClientIdentityCard() {
            return clientIdentityCard;
        }

        public UserIdentityCard getUserIdentityCard() {
            return userIdentityCard;
        }

        String getAction() {
            return this.command.getAction();
        }
    }

    private final AcquaintedUsersService acquaintedUsers;
    private final AcquaintedClientsService acquaintedClients;
    private final IpcService ipc;
    private final ClientsAddressesService addresses;
    private final OwnedIdentityCardsService ownedIdentityCards;
    private final CurrentAddressesService currentAddresses;
    private final IpcEntryHandler handlerAcquaint = new IpcEntryHandler() {
        @Override
        public void handle(IpcEntryHandlerContext context, IpcEntry entry) {
            final AcquaintData acquaintData = toAcquaintData(entry);
            final AcquaintedClient acquaintedClient = acquaintedClients.acquaint(acquaintData.getClientIdentityCard());
            final Int128 clientId = acquaintedClient.getId();
            acquaintProcess(acquaintData.getUserIdentityCard(), acquaintData.getClientAddresses(), clientId);
            //TODO move sender address up in acquainted client addresses
            if(ACTION_ACQUAINT.equals(acquaintData.getAction())) {
                //response acquaint
                final Parcel parcel = new Parcel(clientId, context.getSrcClientAddress());
                parcel.getMessages().add(buildAcquaintCommand(Command.builder().action(ACTION_ACQUAINT_RESPONSE)).build());
                ipc.push(parcel);
            }
        }

    };

    @Inject
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

        this.ipc.addIpcHandler(ACTION_ACQUAINT, handlerAcquaint);
        this.ipc.addIpcHandler(ACTION_ACQUAINT_RESPONSE, handlerAcquaint);
    }

    /**
     * acquaint process over regular network, with manual verification
     * @param address
     */
    public void acquaint(ClientAddress address) {
        ipc.push(createParcel(null, address));
    }

    /**
     * extract acquaint data from IpcEntry <p/>
     * @param entry
     * @return acquaint data or null if entry is not command
     */
    public static AcquaintData toAcquaintData(IpcEntry entry) {
        if(!(entry instanceof Command)) {
            return null;
        }
        return new AcquaintData((Command)entry);
    }

    private Parcel createParcel(Int128 dstClientId, ClientAddress address) {
        Parcel parcel = new Parcel(dstClientId, address);
        Command.Builder builder = Command.builder()
          .action(ACTION_ACQUAINT);
        buildAcquaintCommand(builder);
        parcel.getMessages().add(builder.build());
        parcel.setUserSigned(true);
        return parcel;
    }

    private Command.Builder buildAcquaintCommand(Command.Builder builder) {
        builder.setArg(Arrays.asList(this.ownedIdentityCards.getUserIdentityCard(),
          this.ownedIdentityCards.getClientIdentityCard(),
          this.currentAddresses.getClientAddresses()));
        return builder;
    }

    public void acquaint(Hello hello) {
        final UserIdentityCard identityCard = hello.getIdentityCard();
        final ClientAddresses clientAddresses = hello.getAddresses();
        final Int128 clientId = hello.getClientId();
        acquaintProcess(identityCard, clientAddresses, clientId);
        for(ClientAddress address: clientAddresses.getAddresses()) {
            Parcel parcel = createParcel(clientId, address);
            ipc.push(parcel);
        }
    }

    private void acquaintProcess(UserIdentityCard userIdentityCard, ClientAddresses clientAddresses, Int128 clientId) {
        acquaintedUsers.acquaint(userIdentityCard);
        addresses.setAddresses(clientId, clientAddresses.getAddresses());
    }
}
