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

import talkeeg.bf.Arrays;
import talkeeg.bf.Bf;
import talkeeg.bf.BinaryData;
import talkeeg.common.model.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.ByteBuffer;

/**
 * service which create {@link talkeeg.common.model.Hello hello} message (UIC + CAs), usually it used for barcodes
 *
 * Created by wayerr on 03.12.14.
 */
@Singleton
public final class HelloService {

    private final CurrentAddressesService addressesService;
    private final OwnedIdentityCardsService identityCardsService;
    private final Bf bf;

    @Inject
    HelloService(Bf bf, CurrentAddressesService addressesService, OwnedIdentityCardsService identityCardsService) {
        this.bf = bf;
        this.addressesService = addressesService;
        this.identityCardsService = identityCardsService;
    }

    /**
     * create hello object
     * @return
     */
    public Hello hello() {
        Hello.Builder builder = new Hello.Builder();
        builder.setClientId(this.identityCardsService.getClientId());
        builder.setIdentityCard(this.identityCardsService.getUserIdentityCard());
        builder.setAddresses(this.addressesService.getClientAddreses());
        return builder.build();
    }

    /**
     * serialized list of {@link talkeeg.common.model.UserIdentityCard } and {@link talkeeg.common.model.ClientAddresses } objects
     * @return
     */
    public BinaryData helloAsBinaryData() {
        try {
            ByteBuffer buffer = bf.write(hello());
            return new BinaryData(Arrays.toArray(buffer));
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
