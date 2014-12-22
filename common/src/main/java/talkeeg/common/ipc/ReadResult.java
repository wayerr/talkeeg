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

import com.google.common.collect.ImmutableList;
import talkeeg.common.model.ResponseCode;

import java.util.ArrayList;
import java.util.List;

/**
 * result of message verification
 * Created by wayerr on 19.12.14.
 */
public class ReadResult<T> {


    public static class Builder<T> {
        private T message;
        private final List<String> errors = new ArrayList<>();
        private final List <IpcEntry> entries = new ArrayList<>();
        private ResponseCode responseCode;

        public T getMessage() {
            return message;
        }

        public void setMessage(T message) {
            this.message = message;
        }

        public void addError(String error) {
            this.errors.add(error);
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<IpcEntry> getEntries() {
            return entries;
        }

        /**
         * response code, which sent in {@link talkeeg.common.model.CommandResult}
         * @return
         */
        public ResponseCode getResponseCode() {
            return responseCode;
        }

        /**
         * response code, which sent in {@link talkeeg.common.model.CommandResult}
         * @param responseCode
         * @return
         */
        public Builder responseCode(ResponseCode responseCode) {
            setResponseCode(responseCode);
          return this;
        }

        /**
         * response code, which sent in {@link talkeeg.common.model.CommandResult}
         * @param responseCode
         */
        public void setResponseCode(ResponseCode responseCode) {
            this.responseCode = responseCode;
        }

        public ReadResult<T> build() {
            return new ReadResult<>(this);
        }

    }

    private final T message;
    private final List<String> errors;
    private final List<IpcEntry> entries;
    private final ResponseCode responseCode;

    private ReadResult(Builder<T> b) {
        this.message = b.message;
        this.errors = ImmutableList.copyOf(b.errors);
        this.entries = ImmutableList.copyOf(b.entries);
        this.responseCode = b.responseCode;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public T getMessage() {
        return message;
    }

    public boolean isVerified() {
        return this.errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    public List<IpcEntry> getEntries() {
        return this.entries;
    }

    /**
     * response code, which sent in {@link talkeeg.common.model.CommandResult}
     * @return
     */
    public ResponseCode getResponseCode() {
        return responseCode;
    }

    @Override
    public String toString() {
        return "VerifyResult{" +
          "message=" + message +
          ", errors=" + errors +
          '}';
    }
}
