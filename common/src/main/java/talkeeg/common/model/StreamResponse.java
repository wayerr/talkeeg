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

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * list of accepted and forged packages of StreamMessage
 * Created by wayerr on 26.12.14.
 */
@StructInfo(id = 23)
public final class StreamResponse {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static class Builder implements BuilderInterface {
        private final  List<Integer> accepted = new ArrayList<>();
        private final  List<Integer> needed = new ArrayList<>();

        public List<Integer> getAccepted() {
            return accepted;
        }

        public Builder accepted(List<Integer> accepted) {
            setAccepted(accepted);
            return this;
        }

        public void setAccepted(List<Integer> accepted) {
            this.accepted.clear();
            this.accepted.addAll(accepted);
        }

        public List<Integer> getNeeded() {
            return needed;
        }

        public Builder needed(List<Integer> needed) {
            setNeeded(needed);
            return this;
        }

        public void setNeeded(List<Integer> needed) {
            this.needed.clear();
            this.needed.addAll(accepted);
        }

        public StreamResponse build() {
            return new StreamResponse(this);
        }
    }

    private final List<Integer> accepted;
    private final List<Integer> needed;

    private StreamResponse(Builder b) {
        this.accepted = ImmutableList.copyOf(b.accepted);
        this.needed = ImmutableList.copyOf(b.needed);
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Integer> getAccepted() {
        return accepted;
    }

    public List<Integer> getNeeded() {
        return needed;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof StreamResponse)) {
            return false;
        }

        StreamResponse that = (StreamResponse)o;

        if(accepted != null? !accepted.equals(that.accepted) : that.accepted != null) {
            return false;
        }
        if(needed != null? !needed.equals(that.needed) : that.needed != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = accepted != null? accepted.hashCode() : 0;
        result = 31 * result + (needed != null? needed.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StreamResponse{" +
          "accepted=" + accepted +
          ", needed=" + needed +
          '}';
    }
}
