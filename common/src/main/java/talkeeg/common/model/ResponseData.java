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
import talkeeg.bf.StructInfo;
import talkeeg.bf.StructureBuilder;

/**
 * common response data
 * Created by wayerr on 25.12.14.
 */
@StructInfo(id = 19)
public final class ResponseData {

    public static final Supplier<StructureBuilder> STRUCT_BUILDER_FACTORY = new Supplier<StructureBuilder>() {
        @Override
        public StructureBuilder get() {
            return new ImmutableStructureBuilder(new Builder());
        }
    };

    public static final class Builder implements BuilderInterface {
        private StatusCode status;
        private String message;

        public String getMessage() {
            return message;
        }

        public Builder message(String message) {
            setMessage(message);
            return this;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public StatusCode getStatus() {
            return status;
        }

        public Builder status(StatusCode status) {
            setStatus(status);
            return this;
        }

        public void setStatus(StatusCode status) {
            this.status = status;
        }

        public ResponseData build() {
            return new ResponseData(this);
        }
    }

    private final StatusCode status;
    private final String message;

    private ResponseData(Builder b) {
        this.status = b.status;
        this.message = b.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public StatusCode getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(!(o instanceof ResponseData)) {
            return false;
        }

        ResponseData that = (ResponseData)o;

        if(message != null? !message.equals(that.message) : that.message != null) {
            return false;
        }
        if(status != that.status) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = status != null? status.hashCode() : 0;
        result = 31 * result + (message != null? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResponseData{" +
          "status=" + status +
          ", message='" + message + '\'' +
          '}';
    }
}
