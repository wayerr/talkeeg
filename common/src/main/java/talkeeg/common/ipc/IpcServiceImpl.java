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
import talkeeg.common.conf.Config;
import talkeeg.common.model.ClientAddress;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * ipc service implementation
 * Created by wayerr on 26.11.14.
 */
final class IpcServiceImpl implements IpcService {
    private final Whirligig whirligig;
    private final TgbfProcessor processor;

    IpcServiceImpl(Config config, Bf bf) {
        this.processor = new TgbfProcessor(bf);
        this.whirligig = new Whirligig(config, this.processor);
    }

    @Override
    public void push(ClientAddress address, Object message) {
        this.whirligig.push(new Parcel(address, message));
    }

    @Override
    public void addHandler(String handler, IpcCallback callback) {

    }

    Whirligig getWhirligig() {
        return whirligig;
    }
}
