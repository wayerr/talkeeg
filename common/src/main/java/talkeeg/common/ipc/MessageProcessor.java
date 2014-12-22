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

package talkeeg.common.ipc;

import talkeeg.bf.Int128;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.*;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * do low level processing of messages
 *
 * Created by wayerr on 22.12.14.
 */
final class MessageProcessor {
    private static final Logger LOG = Logger.getLogger(MessageProcessor.class.getName());

    private final HandlersRegistry<IpcEntryHandler> handlers = new HandlersRegistry<>();
    private final SingleMessageSupport singleMessageSupport;
    private final OwnedIdentityCardsService ownedIdentityCards;

    @Inject
    MessageProcessor(OwnedIdentityCardsService ownedIdentityCards,
                     SingleMessageSupport singleMessageSupport) {
        this.ownedIdentityCards = ownedIdentityCards;
        this.singleMessageSupport = singleMessageSupport;
    }


    /**
     * register handler for action
     * @param action
     * @param handler
     * @return closeable instance which can unregister handler from this processor
     */
    public Closeable addHandler(String action, IpcEntryHandler handler) {
        return this.handlers.register(action, handler);
    }


    void receive(IoObject ro) throws Exception {
        Object message = ro.getMessage();
        ClientAddress address = ro.getSrcAddress();
        if(message instanceof SingleMessage) {
           // final IpcEntryHandlerContext ipcEntryHandlerContext = new IpcEntryHandlerContext(service, (SingleMessage)message, address);
           // consume(ipcEntryHandlerContext);
        } else {
            logConsumeError(address, "unsupported message type " + message.getClass());
        }
    }

    void send(Parcel parcel) throws Exception {
        final ClientAddress destination = parcel.getAddress();
        final InetSocketAddress socketAddress = IpcUtil.toAddress(destination.getValue());
        SingleMessage singleMessage = this.singleMessageSupport.build(parcel);
    }

    private void consume(IpcEntryHandlerContext context) throws Exception {
        final SingleMessage message = context.getMessage();
        final ClientAddress address = context.getSrcClientAddress();
        final Int128 dst = message.getDst();
        if(dst != null && !dst.equals(getClientId())) {
            logConsumeError(address, "SingleMessage.dst == " + dst + " , but expected null or clientId.");
        }
        final ReadResult<SingleMessage> result = this.singleMessageSupport.read(context, message);
        if(!result.isVerified()) {
            logConsumeError(address, "SingleMessage has errors:" + result.getErrors());
            final ResponseCode responseCode = result.getResponseCode();
            if(responseCode != null) {
                Parcel parcel = new Parcel(message.getSrc(), address);
                //parcel.getMessages().add(CommandResult.builder()
                //  .action(ACTION_ACQUAINT)
                //  .code(responseCode)
                //  .build());
                context.getService().push(parcel);
            }
            return;
        }
        final List<?> objects = result.getEntries();
        for(Object obj: objects) {
            if(!(obj instanceof IpcEntry)) {
                logConsumeError(address, "unsupported IpcEntry type '" + obj.getClass() + "'.");
                continue;
            }
            IpcEntry entry = (IpcEntry)obj;
            final String action = entry.getAction();
            if(action == null) {//is processor action
                handle(context, entry);
            }
            IpcEntryHandler handler = this.handlers.get(action);
            if(handler == null) {
                logConsumeError(address, "No handler for '" + action + "'.");
            } else {
                handler.handle(context, entry);
            }
        }
    }

    private void handle(IpcEntryHandlerContext context, IpcEntry entry) {
        if(!(entry instanceof CommandResult)) {
            logConsumeError(context.getSrcClientAddress(), "unknown processor entry: " + entry);
            return;
        }
        CommandResult commandResult = (CommandResult)entry;
        ResponseCode code = commandResult.getCode();
        switch(code) {
            case NOT_AC: {
                Parcel parcel = new Parcel(context.getSrcClientId(), context.getSrcClientAddress());
                //parcel.getMessages().add(Command.builder()
                //  .action("test")
                //  .addArg()
                //  .build());
                //context.getService().push(parcel);
            }
            break;
            default:
                logConsumeError(context.getSrcClientAddress(), " response code: " + code);
        }
    }

    private void logConsumeError(ClientAddress remoteClientAddress, String message) {
        LOG.log(Level.SEVERE, "Message from " + remoteClientAddress.getValue() + ".\n" + message);
    }

    private Int128 getClientId() {
        return this.ownedIdentityCards.getClientId();
    }
}
