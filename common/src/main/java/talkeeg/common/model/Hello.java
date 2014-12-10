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

import com.google.common.base.Preconditions;
import talkeeg.bf.Int128;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;
import com.google.common.base.Supplier;
import talkeeg.common.util.Printable;

import java.util.Map;

/**
 * hello struct, used for exposing {@link talkeeg.common.model.UserIdentityCard UIC} and {@link talkeeg.common.model.ClientAddresses CAs}
 * Created by wayerr on 03.12.14.
 */
@StructInfo(id = 14)
public final class Hello implements Printable {
    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {

        private Int128 clientId;
        private ClientAddresses addresses;
        private UserIdentityCard identityCard;

        public Int128 getClientId() {
            return clientId;
        }

        public Builder clientId(Int128 clientId) {
            setClientId(clientId);
            return this;
        }

        public void setClientId(Int128 clientId) {
            this.clientId = clientId;
        }

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

    private final Int128 clientId;
    private final UserIdentityCard identityCard;
    private final ClientAddresses addresses;

    public Hello(Builder b) {
        this.clientId = b.clientId;
        Preconditions.checkNotNull(this.clientId, "clientId is null");
        this.identityCard = b.identityCard;
        Preconditions.checkNotNull(this.identityCard, "identityCard is null");
        this.addresses = b.addresses;
        Preconditions.checkNotNull(this.addresses, "addresses is null");
    }

    public Int128 getClientId() {
        return clientId;
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

    @Override
    public void print(StringBuilder sb) {
        sb.append("hello:\n");
        if(this.addresses != null) {
            this.addresses.print(sb);
        }
        if(this.identityCard != null) {
            this.identityCard.print(sb);
        }
    }
}
