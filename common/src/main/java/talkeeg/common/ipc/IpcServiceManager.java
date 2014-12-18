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

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import talkeeg.bf.Bf;
import talkeeg.common.conf.Config;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.util.TgAddress;
import talkeeg.mb.MessageBusKey;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * IPC service lifecycle manager
 *
 * Created by wayerr on 26.11.14.
 */
@Singleton
public final class IpcServiceManager {
    public static final MessageBusKey<IpcLifecycleEvent> MB_SERVICE_LIFECYCLE = MessageBusKey.create("tg.IpcService.lifecycle", IpcLifecycleEvent.class);
    private final Predicate<ClientAddress> filter = new Predicate<ClientAddress>() {
        @Override
        public boolean apply(ClientAddress input) {
            String value = input.getValue();
            //actually we support any 'tg:' addresses
            TgAddress tgAddress = TgAddress.from(value);
            if(tgAddress == null) {
                return false;
            }
            if(service.getWhirligig().isIpv6Supported()) {
                return true;
            }
            // horrible test for ipv6 address
            return !tgAddress.getHost().contains(":");
        }
    };
    private final Thread serviceThread;
    private final IpcServiceImpl service;
    final MessageBusRegistry messageBusregistry;
    final OwnedIdentityCardsService ownedIdentityCards;
    final Bf bf;
    final Config config;
    final TgbfProcessor processor;

    @Inject
    IpcServiceManager(Config config, MessageBusRegistry messageBusRegistry, Bf bf, OwnedIdentityCardsService ownedIdentityCards) {
        this.config = config;
        this.bf = bf;
        this.messageBusregistry = messageBusRegistry;
        Preconditions.checkNotNull(this.bf, "bf is null");
        this.ownedIdentityCards = ownedIdentityCards;
        Preconditions.checkNotNull(this.ownedIdentityCards, "ownedIdentityCards is null");
        this.processor = new TgbfProcessor(this);
        this.service = new IpcServiceImpl(this);
        this.serviceThread = new Thread(service.getWhirligig(), config.getApplicationName() + "-ipc-service-thread");
    }

    public void start() {
        this.serviceThread.start();
        this.messageBusregistry.getOrCreateBus(MB_SERVICE_LIFECYCLE).listen(new IpcLifecycleEvent(IpcLifecycleEvent.Type.START, this.service));
    }

    public void stop() {
        this.messageBusregistry.getOrCreateBus(MB_SERVICE_LIFECYCLE).listen(new IpcLifecycleEvent(IpcLifecycleEvent.Type.STOP, this.service));
        this.serviceThread.interrupt();
    }

    public IpcService getIpc() {
        return this.service;
    }

    /**
     * configured local port for ipc
     * @return
     */
    public int getPort() {
        return this.service.getWhirligig().getPort();
    }

    /**
     * function which return true on supported addresses
     * @return
     */
    public Predicate<ClientAddress> getSupportedAddressFilter() {
        return filter;
    }
}
