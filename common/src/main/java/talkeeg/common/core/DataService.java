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
import talkeeg.common.ipc.IpcService;
import talkeeg.common.ipc.Parcel;
import talkeeg.common.ipc.TgbfHandler;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.ClientAddresses;
import talkeeg.common.model.Command;
import talkeeg.common.model.Data;
import talkeeg.common.util.Callback;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.SocketAddress;
import java.util.List;
import java.util.logging.Logger;

/**
 * service of data messaging
 * Created by wayerr on 16.12.14.
 */
@Singleton
public final class DataService {
    private static final Logger LOG = Logger.getLogger(DataService.class.getName());
    public static final String ACTION_DATA = "tg.data";
    private final IpcService ipc;
    private final HandlersRegistry<Callback<Data>> registry = new HandlersRegistry<>();
    private final ClientsAddressesService clientsAddresses;

    @Inject
    DataService(IpcService ipc, ClientsAddressesService clientsAddresses) {
        this.ipc = ipc;
        this.clientsAddresses = clientsAddresses;
        this.ipc.addIpcHandler(ACTION_DATA, new TgbfHandler() {
            @Override
            public void handle(SocketAddress srcAddress, Command command) {
                List<Object> args = command.getArgs();
                for(Object arg: args) {
                    if(!(arg instanceof Data)) {
                        LOG.warning("Expected Data argument, but receive: " + arg);
                        continue;
                    }
                    final Data data = (Data)arg;
                    final String action = data.getAction();
                    final Callback<Data> callback = registry.get(action);
                    if(callback == null) {
                        LOG.warning("no callback for action: " + action);
                        continue;
                    }
                    callback.call(data);
                }
            }
        });
    }

    public Closeable addHandler(String key, Callback<Data> callback) {
        return this.registry.register(key, callback);
    }

    public void push(Int128 clientId, Data data) {
        List<ClientAddress> addresses = this.clientsAddresses.getSuitableAddress(clientId);
        if(addresses.isEmpty()) {
            LOG.severe("no addresses for client: " + clientId);
            return;
        }
        for(ClientAddress address: addresses) {
            Parcel parcel = new Parcel(clientId, address);
            parcel.getMessages().add(Command.builder()
              .action(ACTION_DATA)
              .addArg(data)
              .build());
            this.ipc.push(parcel);
        }
    }
}
