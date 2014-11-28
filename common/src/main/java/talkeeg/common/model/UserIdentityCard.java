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
import talkeeg.common.util.BinaryData;
import talkeeg.common.util.Int128;

import java.util.*;

/**
 * Created by wayerr on 28.11.14.
 */
@StructInfo(id = 11)
public final class UserIdentityCard {

    /**
     * nickname of user
     */
    public static final String ATTR_NICK = "nick";
    /**
     * vcard of user
     */
    public static final String ATTR_VCARD = "vcard";

    public static class Builder {
        private BinaryData key;
        private final Map<String, Object> attrs = new HashMap<>();
        private final Set<Int128> clients = new HashSet<>();

        public BinaryData getKey() {
            return key;
        }

        public void setKey(BinaryData key) {
            this.key = key;
        }

        public Map<String, Object> getAttrs() {
            return attrs;
        }

        public void setAttrs(Map<String, Object> attrs) {
            this.attrs.clear();
            this.attrs.putAll(attrs);
        }

        public Set<Int128> getClients() {
            return clients;
        }

        public void setClients(Set<Int128> clients) {
            this.clients.clear();
            this.clients.addAll(clients);
        }

        public UserIdentityCard build() {
            return new UserIdentityCard(this);
        }
    }

    private final BinaryData key;
    private final Map<String, Object> attrs;
    private final Set<Int128> clients;

    private UserIdentityCard(Builder b) {
        this.key = b.key;
        this.attrs = Collections.unmodifiableMap(new HashMap<>(b.attrs));
        this.clients = Collections.unmodifiableSet(new HashSet<>(b.clients));
    }

    public static Builder builder() {
        return new Builder();
    }

    public BinaryData getKey() {
        return key;
    }

    /**
     * unmodifiable map of attributes
     * @see #ATTR_NICK
     * @see #ATTR_VCARD
     * @return
     */
    public Map<String, Object> getAttrs() {
        return attrs;
    }

    /**
     * unmodifiable set fingerprints of owned clients
     * @return
     */
    public Set<Int128> getClients() {
        return clients;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof UserIdentityCard)) {
            return false;
        }

        final UserIdentityCard that = (UserIdentityCard)o;

        if(attrs != null ? !attrs.equals(that.attrs) : that.attrs != null) {
            return false;
        }
        if(clients != null ? !clients.equals(that.clients) : that.clients != null) {
            return false;
        }
        if(key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (attrs != null ? attrs.hashCode() : 0);
        result = 31 * result + (clients != null ? clients.hashCode() : 0);
        return result;
    }
}
