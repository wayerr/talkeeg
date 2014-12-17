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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.google.common.base.Supplier;
import talkeeg.common.util.Printable;

/**
 * list of client addresses
 *
 * Created by wayerr on 01.12.14.
 */
@StructInfo(id = 12)
public final class ClientAddresses implements Printable {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {
        private final List<ClientAddress> addresses = new ArrayList<>();

        public Collection<ClientAddress> getAddresses() {
            return addresses;
        }

        public Builder addAddress(ClientAddress address) {
            this.addresses.add(address);
            return this;
        }

        public Builder addresses(Collection<ClientAddress> addresses) {
            setAddresses(addresses);
            return this;
        }

        public void setAddresses(Collection<ClientAddress> addresses) {
            this.addresses.clear();
            this.addresses.addAll(addresses);
        }

        public ClientAddresses build() {
            return new ClientAddresses(this);
        }
    }

    private final List<ClientAddress> addresses;

    private ClientAddresses(Builder builder) {
        this.addresses = Collections.unmodifiableList(new ArrayList<>(builder.addresses));
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<ClientAddress> getAddresses() {
        return addresses;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ClientAddresses)) {
            return false;
        }

        final ClientAddresses addresses1 = (ClientAddresses)o;

        if(addresses != null ? !addresses.equals(addresses1.addresses) : addresses1.addresses != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return addresses != null ? addresses.hashCode() : 0;
    }

    @Override
    public void print(StringBuilder sb) {
        sb.append("addresses:\n");
        for(ClientAddress clientAddress: this.addresses) {
            sb.append(clientAddress.getValue()).append(' ')
               .append(clientAddress.isExternal() ? "external" : "internal").append('\n');
        }
    }
}
