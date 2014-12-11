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
import talkeeg.bf.Bf;
import talkeeg.common.conf.Config;
import talkeeg.common.core.OwnedIdentityCardsService;

/**
 * IPC service lifecycle manager
 *
 * Created by wayerr on 26.11.14.
 */
public final class IpcServiceManager {
    private final Thread serviceThread;
    private final IpcServiceImpl service;
    final OwnedIdentityCardsService ownedIdentityCards;
    final Bf bf;
    final Config config;
    final TgbfProcessor processor;

    public IpcServiceManager(Config config, Bf bf, OwnedIdentityCardsService ownedIdentityCards) {
        this.config = config;
        this.bf = bf;
        Preconditions.checkNotNull(this.bf, "bf is null");
        this.ownedIdentityCards = ownedIdentityCards;
        Preconditions.checkNotNull(this.ownedIdentityCards, "ownedIdentityCards is null");
        this.processor = new TgbfProcessor(this);
        this.service = new IpcServiceImpl(this);
        this.serviceThread = new Thread(service.getWhirligig(), config.getApplicationName() + "-ipc-service-thread");
    }

    public void start() {
        serviceThread.start();
    }

    public void stop() {
        serviceThread.interrupt();
    }

    public IpcService getIpc() {
        return service;
    }
}
