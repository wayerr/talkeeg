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

package talkeeg.common.model;

import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

import java.util.function.Supplier;

/**
 * hello struct, used for exposing {@link talkeeg.common.model.UserIdentityCard UIC} and {@link talkeeg.common.model.ClientAddresses CAs}
 * Created by wayerr on 03.12.14.
 */
@StructInfo(id = 14)
public final class Hello {
    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {

        private ClientAddresses addresses;
        private UserIdentityCard identityCard;

        public ClientAddresses getAddresses() {
            return addresses;
        }

        public void setAddresses(ClientAddresses addresses) {
            this.addresses = addresses;
        }

        public UserIdentityCard getIdentityCard() {
            return identityCard;
        }

        public void setIdentityCard(UserIdentityCard identityCard) {
            this.identityCard = identityCard;
        }

        @Override
        public Hello build() {
            return new Hello(this);
        }
    }

    private final UserIdentityCard identityCard;
    private final ClientAddresses addresses;

    public Hello(Builder b) {
        this.identityCard = b.identityCard;
        this.addresses = b.addresses;
    }

    public UserIdentityCard getIdentityCard() {
        return identityCard;
    }

    public ClientAddresses getAddresses() {
        return addresses;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof Hello)) {
            return false;
        }

        final Hello hello = (Hello)o;

        if(addresses != null ? !addresses.equals(hello.addresses) : hello.addresses != null) {
            return false;
        }
        if(identityCard != null ? !identityCard.equals(hello.identityCard) : hello.identityCard != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = identityCard != null ? identityCard.hashCode() : 0;
        result = 31 * result + (addresses != null ? addresses.hashCode() : 0);
        return result;
    }
}
