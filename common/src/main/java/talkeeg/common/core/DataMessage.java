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

import talkeeg.bf.Int128;
import talkeeg.common.ipc.Parcel;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.model.Command;
import talkeeg.common.model.Data;
import talkeeg.common.util.Callback;
import talkeeg.common.util.CallbacksContainer;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * representation of sent data message and it status
 * Created by wayerr on 18.12.14.
 */
public final class DataMessage {

    public enum State {
        INITIAL, FAIL, SUCCESS
    }

    private final Data data;
    private final Int128 clientId;
    private final List<ClientAddress> addresses;
    private final Command command;
    private final DataService dataService;
    private final Object lock = new Object();
    private int addressNumber = 0;
    private int sendCounter = 0;
    private final CallbacksContainer<DataMessage> callbacks = new CallbacksContainer<>();
    private final ScheduledFuture<?> future;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            send();
        }
    };
    private State state = State.INITIAL;

    DataMessage(DataService dataService, Int128 clientId, List<ClientAddress> addresses, Data data) {
        this.dataService = dataService;
        this.data = data;
        this.clientId = clientId;
        this.addresses = addresses;
        this.command = Command.builder()
          .id(this.dataService.getNextId())
          .action(DataService.ACTION_DATA)
          .arg(this.data)
          .build();

        this.future = dataService.scheduledExecutorService.scheduleWithFixedDelay(runnable, 0, DataService.DEFAULT_REPEAT_DELAY, TimeUnit.SECONDS);
    }

    /**
     * do asynchronous sending of command, can be invoked repeatedly <p/>
     * after calling you will need check {@link #getState() }
     */
    private void send() {
        synchronized(this.lock) {
            if(this.state != State.INITIAL) {
                return;
            }
            this.sendCounter++;
            if(sendCounter > DataService.MAX_SEND_ON_ONE_ADDR) {
                this.addressNumber++;
                this.sendCounter = 0;//we must reset counter
            }
            if(this.addresses.size() <= this.addressNumber) {
                DataService.LOG.log(Level.SEVERE, "Fail to send message in " + DataService.MAX_SEND_ON_ONE_ADDR * this.addressNumber + " attempts.");
                this.setState(State.FAIL);
            }
            ClientAddress address = this.addresses.get(this.addressNumber);
            Parcel parcel = new Parcel(clientId, address);
            parcel.setCiphered(true);
            parcel.getMessages().add(this.command);
            this.dataService.ipc.push(parcel);
        }
    }

    /**
     * state of message
     * @return
     */
    public State getState() {
        synchronized(this.lock) {
            return state;
        }
    }

    Command getCommand() {
        return command;
    }

    public Data getData() {
        return data;
    }


    void setState(State state) {
        synchronized(this.lock) {
            if(this.state == state) {
                return;
            }
            if(this.state != State.INITIAL) {
                throw new RuntimeException("can not change non initial state");
            }
            this.state = state;
        }
        if(state == State.FAIL || state == State.SUCCESS) {
            future.cancel(true);
            this.dataService.removeOldMessage(this);
        }
        this.callbacks.call(this);
    }

    public void addCallback(Callback<DataMessage> callback) {
        this.callbacks.add(callback);
        callback.call(this);
    }

    public void removeCallback(Callback<DataMessage> callback) {
        this.callbacks.remove(callback);
    }
}
