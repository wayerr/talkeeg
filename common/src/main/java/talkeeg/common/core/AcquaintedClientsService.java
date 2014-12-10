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

import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.conf.Config;
import talkeeg.common.model.ClientIdentityCard;
import talkeeg.common.util.Callback;
import talkeeg.common.util.FileData;
import java.io.File;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * service which save list of acquainted clients
 *
 * Created by wayerr on 10.12.14.
 */
public final class AcquaintedClientsService {

    private final ConcurrentMap<Int128, AcquaintedClient> map = new ConcurrentHashMap<>();
    private final CryptoService cryptoService;
    private final KeyLoader keyLoader;
    private final FileData fileData;

    AcquaintedClientsService(Config config, Bf bf, CryptoService cryptoService, KeyLoader keyLoader) {
        this.cryptoService = cryptoService;
        this.keyLoader = keyLoader;

        final File file = new File(config.getConfigDir(), "acquainted_clients.tgbf");
        this.fileData = new FileData(bf, file);
        load();
    }

    private void load() {
        this.fileData.read(new Callback<Object>() {
            @Override
            public void call(Object value) {
                acquaint((ClientIdentityCard)value);
            }
        });
    }

    public AcquaintedClient acquaint(ClientIdentityCard cic) {
        BinaryData clientPublicKey = cic.getKey();
        final Int128 clientId = this.cryptoService.getFingerprint(clientPublicKey);
        final PublicKey publicKey = this.keyLoader.loadPublic(clientPublicKey.getData());
        final AcquaintedClient client = new AcquaintedClient(clientId, publicKey);
        final AcquaintedClient oldClient = map.putIfAbsent(clientId, client);
        if(oldClient != null) {
            return oldClient;
        } else {
            save();
        }
        return client;
    }



    private void save() {

    }
}
