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
import talkeeg.common.core.AcquaintService;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.*;
import talkeeg.common.util.Closeable;
import talkeeg.common.util.HandlersRegistry;
import talkeeg.common.util.ServiceLocator;
import javax.inject.Inject;
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
    private final ServiceLocator serviceLocator;
    private final StreamSupport streamSupport;

    @Inject
    MessageProcessor(OwnedIdentityCardsService ownedIdentityCards,
                     SingleMessageSupport singleMessageSupport,
                     StreamSupport streamSupport,
                     ServiceLocator serviceLocator) {
        this.ownedIdentityCards = ownedIdentityCards;
        this.singleMessageSupport = singleMessageSupport;
        this.streamSupport = streamSupport;
        this.serviceLocator = serviceLocator;
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


    void receive(IpcService service, IoObject ro) throws Exception {
        Object message = ro.getMessage();
        ClientAddress address = ro.getSrcAddress();
        if(message instanceof SingleMessage) {
            consumeSingleMessage(new IpcEntryHandlerContext<>(service, (SingleMessage)message, address));
        } else if(message instanceof SingleMessage) {
            consumeStreamMessage(new IpcEntryHandlerContext<>(service, (StreamMessage)message, address));
        } else {
            logConsumeError(address, "unsupported message type " + message.getClass());
        }
    }

    private void consumeStreamMessage(IpcEntryHandlerContext<StreamMessage> context) throws Exception {
        this.streamSupport.read(context);
    }

    IoObject send(Parcel parcel) throws Exception {
        final ClientAddress destination = parcel.getAddress();
        SingleMessage singleMessage = this.singleMessageSupport.build(parcel);
        return new IoObject(singleMessage, destination);
    }

    private void consumeSingleMessage(IpcEntryHandlerContext<SingleMessage> context) throws Exception {
        final SingleMessage message = context.getMessage();
        final ClientAddress address = context.getSrcClientAddress();
        final Int128 dst = message.getDst();
        if(dst != null && !dst.equals(getClientId())) {
            logConsumeError(address, "SingleMessage.dst == " + dst + " , but expected null or clientId.");
        }
        final StatusCode status = message.getStatus();
        if(status != null && status != StatusCode.OK) {
            processMessageWithStatus(context, status);
            return;
        }
        final ReadResult<SingleMessage> result = this.singleMessageSupport.read(context);
        if(!result.isVerified()) {
            logConsumeError(address, "SingleMessage has errors:" + result.getErrors());
            final StatusCode statusCode = result.getStatusCode();
            if(statusCode != null) {
                Parcel parcel = new Parcel(message.getSrc(), address);
                parcel.setCode(statusCode);
                context.getService().push(parcel);
            }
            return;
        }
        Object arg = result.getArg();
        if(arg instanceof List) {
            final List<?> objects = (List<?>)arg;
            for(Object obj: objects) {
                if(!(obj instanceof IpcEntry)) {
                    logConsumeError(address, "unsupported IpcEntry type '" + obj.getClass() + "'.");
                    continue;
                }
                IpcEntry entry = (IpcEntry)obj;
                final String action = entry.getAction();
                IpcEntryHandler handler = this.handlers.get(action);
                if(handler == null) {
                    logConsumeError(address, "No handler for '" + action + "'.");
                } else {
                    handler.handle(context, entry);
                }
            }
        } else {
            logConsumeError(address, "Unsupported command argument type: '" + (arg == null? "null" : arg.getClass()) + "'.");
        }
    }

    private void processMessageWithStatus(IpcEntryHandlerContext context, StatusCode status) {
        switch(status) {
            case NOT_AC: {
                final AcquaintService acquaintService = this.serviceLocator.get(AcquaintService.class);
                acquaintService.acquaint(context.getSrcClientAddress());
            }
            break;
            default:
                logConsumeError(context.getSrcClientAddress(), " unsupported response code: " + status);
        }
    }

    private void logConsumeError(ClientAddress remoteClientAddress, String message) {
        LOG.log(Level.SEVERE, "Message from " + remoteClientAddress.getValue() + ".\n" + message);
    }

    private Int128 getClientId() {
        return this.ownedIdentityCards.getClientId();
    }
}
