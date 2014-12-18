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
import talkeeg.common.util.Callback;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * service of data messaging
 * Created by wayerr on 16.12.14.
 */
@Singleton
public final class DataService {

    private static final Logger LOG = Logger.getLogger(DataService.class.getName());
    static final int MAX_SEND_ON_ONE_ADDR = 3;
    public static final String ACTION_DATA = "tg.data";
    final IpcService ipc;
    private final HandlersRegistry<Callback<Data>> registry = new HandlersRegistry<>();
    private final ClientsAddressesService clientsAddresses;
    private final ConcurrentMap<Integer, DataMessage> sended = new ConcurrentHashMap<>();
    private final AtomicInteger commandIdGenerator = new AtomicInteger();

    @Inject
    DataService(IpcService ipc, ClientsAddressesService clientsAddresses) {
        this.ipc = ipc;
        this.clientsAddresses = clientsAddresses;
        this.ipc.addIpcHandler(ACTION_DATA, new IpcEntryHandler() {
            @Override
            public void handle(IpcEntryHandlerContext context, IpcEntry entry) {
                if(entry instanceof CommandResult) {
                    processCommandResult((CommandResult)entry);
                } else if(entry instanceof Command) {
                    processCommand(context, (Command)entry);
                } else {
                    throw new RuntimeException("Unsupported IpcEntry type: " + entry);
                }
            }
        });
    }

    int getNextId() {
        return this.commandIdGenerator.getAndIncrement();
    }

    private void processCommandResult(CommandResult commandResult) {
        DataMessage message = this.sended.get(commandResult.getId());
        message.success();
    }

    private void processCommand(IpcEntryHandlerContext context, Command command) {
        CommandResult.Builder builder = CommandResult.builder()
          .id(command.getId())
          .action(command.getAction());
        final List<Object> args = command.getArgs();
        final Object arg = args.get(0);
        final Data data = (Data)arg;
        final String action = data.getAction();
        final Callback<Data> callback = registry.get(action);
        if(callback == null) {
            LOG.warning("no callback for action: " + action);
            builder.setCode(ResponseCode.ERROR);
        }
        if(callback != null) {
            try {
                callback.call(data);
            } catch(Exception e) {
                LOG.log(Level.SEVERE, "fail callback", e);
                builder.setCode(ResponseCode.ERROR);
            }
        }

        //send response
        Parcel parcel = new Parcel(context.getSrcClientId(), context.getSrcClientAddress());
        parcel.getMessages().add(builder.build());
        this.ipc.push(parcel);
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
        final DataMessage message = new DataMessage(this, clientId, addresses, data);
        this.sended.put(message.getId(), message);
        message.send();
    }
}
