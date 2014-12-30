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

import talkeeg.bf.Bf;
import talkeeg.bf.Int128;
import talkeeg.common.core.AcquaintedClientsService;
import talkeeg.common.core.ClientsAddressesService;
import talkeeg.common.core.CryptoService;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.StreamMessage;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * supporting of stream
 *
 * Created by wayerr on 29.12.14.
 */
@Singleton
final class StreamSupport implements MessageReader<StreamMessage> {

    private final ConcurrentMap<Short, StreamProviderRegistration> providersMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Short, StreamConsumerRegistration> consumersMap = new ConcurrentHashMap<>();
    final CryptoService cryptoService;
    final AcquaintedClientsService clientsService;
    final Bf bf;
    private final OwnedIdentityCardsService ownedIdentityCardsService;
    private final ClientsAddressesService clientsAddressesService;
    private final Provider<IpcService> ipcServiceProvider;

    @Inject
    StreamSupport(Bf bf,
                  CryptoService cryptoService,
                  AcquaintedClientsService clientsService,
                  OwnedIdentityCardsService ownedIdentityCardsService,
                  ClientsAddressesService clientsAddressesService,
                  Provider<IpcService> ipcServiceProvider) {
        this.bf = bf;
        this.cryptoService = cryptoService;
        this.clientsService = clientsService;
        this.ownedIdentityCardsService = ownedIdentityCardsService;
        this.clientsAddressesService = clientsAddressesService;
        this.ipcServiceProvider = ipcServiceProvider;
    }

    /**
     * register stream provider
     * @param streamProvider
     * @return
     */
    public StreamProviderRegistration registerProvider(StreamProvider streamProvider, Int128 otherClientId) {
        final StreamProviderRegistration registration = new StreamProviderRegistration(this, streamProvider, otherClientId);
        final short streamId = registration.getStreamId();
        final StreamProviderRegistration old = this.providersMap.putIfAbsent(streamId, registration);
        if(old != null) {
            throw new RuntimeException("we already has stream provider for " + streamId + ": " + old);
        }
        return registration;
    }

    /**
     * remove specified registration of provider
     * @param registration
     */
    void unregisterProvider(StreamProviderRegistration registration) {
        this.providersMap.remove(registration.getStreamId(), registration);
    }

    /**
     * register stream consumer
     * @param streamConsumer
     * @param streamId id of stream which consumer must handle
     * @return
     */
    public StreamConsumerRegistration registerConsumer(StreamConsumer streamConsumer, Int128 otherClientId, short streamId) {
        final StreamConsumerRegistration registration = new StreamConsumerRegistration(this, streamConsumer, otherClientId, streamId);
        final StreamConsumerRegistration old = this.consumersMap.putIfAbsent(streamId, registration);
        if(old != null) {
            throw new RuntimeException("we already has stream consumer for " + streamId + ": " + old);
        }
        return registration;
    }

    /**
     * remove specified registration of consumer
     * @param registration
     */
    void unregisterConsumer(StreamConsumerRegistration registration) {
        this.consumersMap.remove(registration.getStreamId(), registration);
    }

    @Override
    public ReadResult<StreamMessage> read(IpcEntryHandlerContext<StreamMessage> context) throws Exception {
        final StreamMessage message = context.getMessage();
        final short streamId = message.getStreamId();
        final StreamConsumerRegistration registration = this.consumersMap.get(streamId);
        if(registration == null) {
            throw new RuntimeException("No registered consumer for streamId=" + streamId);
        }
        registration.process(context);
        return null;
    }

    Int128 getOwnClientId() {
        return this.ownedIdentityCardsService.getClientId();
    }

    void send(StreamMessage streamMessage) throws Exception {
        // TODO message was sent to one specified address
        List<ClientAddress> suitableAddress = this.clientsAddressesService.getSuitableAddress(streamMessage.getDst());
        final IpcService ipcService = this.ipcServiceProvider.get();
        for(ClientAddress clientAddress: suitableAddress) {
            IoObject ioObject = new IoObject(streamMessage, clientAddress);
            ipcService.push(ioObject);
        }
    }
}
