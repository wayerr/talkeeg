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
import talkeeg.common.core.CryptoService;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.StreamMessage;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * supporting of stream
 *
 * Created by wayerr on 29.12.14.
 */
@Singleton
final class StreamSupport implements MessageReader<StreamMessage> {
    private final ConcurrentMap<StreamKey, StreamBasicRegistration> streams = new ConcurrentHashMap<>();
    final CryptoService cryptoService;
    final AcquaintedClientsService clientsService;
    final Bf bf;
    private final OwnedIdentityCardsService ownedIdentityCardsService;
    private final Provider<IpcService> ipcServiceProvider;

    @Inject
    StreamSupport(Bf bf,
                  CryptoService cryptoService,
                  AcquaintedClientsService clientsService,
                  OwnedIdentityCardsService ownedIdentityCardsService,
                  Provider<IpcService> ipcServiceProvider) {
        this.bf = bf;
        this.cryptoService = cryptoService;
        this.clientsService = clientsService;
        this.ownedIdentityCardsService = ownedIdentityCardsService;
        this.ipcServiceProvider = ipcServiceProvider;
    }

    /**
     * register stream provider
     * @param streamProvider
     * @return
     */
    public StreamProviderRegistration registerProvider(StreamProvider streamProvider, StreamConfig config) {
        final StreamProviderRegistration registration = new StreamProviderRegistration(this, streamProvider, config);
        registerStream(registration);
        return registration;
    }

    /**
     * register stream consumer
     * @param streamConsumer
     * @return
     */
    public StreamConsumerRegistration registerConsumer(StreamConsumer streamConsumer, StreamConfig config) {
        final StreamConsumerRegistration registration = new StreamConsumerRegistration(this, streamConsumer, config);
        registerStream(registration);
        return registration;
    }

    private void registerStream(StreamBasicRegistration registration) {
        final StreamKey key = registration.getStreamKey();
        final StreamBasicRegistration old = this.streams.putIfAbsent(key, registration);
        if(old != null) {
            throw new RuntimeException("we already has stream with id " + key + ": \n" + old);
        }
    }

    /**
     * remove specified registration
     * @param registration
     */
    void unregister(StreamBasicRegistration registration) {
        this.streams.remove(registration.getStreamId(), registration);
    }

    @Override
    public ReadResult<StreamMessage> read(IpcEntryHandlerContext<StreamMessage> context) throws Exception {
        final StreamMessage message = context.getMessage();
        final StreamKey key = new StreamKey(message.getSrc(), message.getStreamId());
        final StreamBasicRegistration registration = this.streams.get(key);
        if(registration == null) {
            throw new RuntimeException("No registered consumer for streamKey=" + key);
        }
        registration.process(context);
        return null;
    }

    Int128 getOwnClientId() {
        return this.ownedIdentityCardsService.getClientId();
    }

    void send(StreamMessage streamMessage, ClientAddress clientAddress) throws Exception {
        final IpcService ipcService = this.ipcServiceProvider.get();
        IoObject ioObject = new IoObject(streamMessage, clientAddress);
        ipcService.push(ioObject);
    }
}
