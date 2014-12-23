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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * service of data messaging
 * Created by wayerr on 16.12.14.
 */
@Singleton
public final class DataService {

    static final Logger LOG = Logger.getLogger(DataService.class.getName());
    static final int MAX_SEND_ON_ONE_ADDR = 3;
    public static final String ACTION_DATA = "tg.data";
    public static final String ACTION_DATA_RESPONSE = "tg.dataResponse";
    final IpcService ipc;
    private final HandlersRegistry<Callback<Data>> registry = new HandlersRegistry<>();
    private final ClientsAddressesService clientsAddresses;
    private final ConcurrentMap<Integer, DataMessage> sentMessages = new ConcurrentHashMap<>();
    private final IdSequenceGenerator commandIdGenerator = IdSequenceGenerator.shortIdGenerator();

    @Inject
    DataService(IpcService ipc, ClientsAddressesService clientsAddresses) {
        this.ipc = ipc;
        this.clientsAddresses = clientsAddresses;
        IpcEntryHandler handler = new IpcEntryHandler() {
            @Override
            public void handle(IpcEntryHandlerContext context, IpcEntry entry) {
                if(!(entry instanceof Command)) {
                    throw new RuntimeException("Unsupported IpcEntry type: " + entry);
                }
                processCommand(context, (Command)entry);
            }
        };
        this.ipc.addIpcHandler(ACTION_DATA, handler);
        this.ipc.addIpcHandler(ACTION_DATA_RESPONSE, handler);
    }

    short getNextId() {
        return this.commandIdGenerator.next();
    }

    private void processCommand(IpcEntryHandlerContext context, Command command) {
        if(ACTION_DATA_RESPONSE.equals(command.getAction())) {
            final DataMessage message = this.sentMessages.get(command.getId());
            if(message != null) {
                final StatusCode code = (StatusCode)command.getArg();
                final DataMessage.State state = (code == null || code == StatusCode.OK)?
                    DataMessage.State.SUCCESS :
                    DataMessage.State.FAIL;
                message.setState(state);
            }
            return;
        }
        Command.Builder builder = Command.builder()
          .id(getNextId())
          .sequenceId(command.getSequenceId())
          .action(ACTION_DATA_RESPONSE);
        final Object arg = command.getArg();
        final Data data = (Data)arg;
        final String action = data.getAction();
        final Callback<Data> callback = registry.get(action);
        if(callback == null) {
            LOG.warning("no callback for action: " + action);
            builder.setArg(StatusCode.ERROR);
        }
        if(callback != null) {
            try {
                callback.call(data);
            } catch(Exception e) {
                LOG.log(Level.SEVERE, "fail callback", e);
                builder.setArg(StatusCode.ERROR);
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

    /**
     * send message into specified client
     * @param clientId
     * @param data
     * @return message management object
     */
    public DataMessage push(Int128 clientId, Data data) {
        List<ClientAddress> addresses = this.clientsAddresses.getSuitableAddress(clientId);
        if(addresses.isEmpty()) {
            throw new RuntimeException("no addresses for client: " + clientId);
        }
        final DataMessage message = new DataMessage(this, clientId, addresses, data);
        this.sentMessages.put(message.getId(), message);
        message.send();
        return message;
    }
}
