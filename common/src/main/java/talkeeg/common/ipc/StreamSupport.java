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

import talkeeg.common.model.StreamMessage;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * supporting of stream
 *
 * Created by wayerr on 29.12.14.
 */
@Singleton
final class StreamSupport implements MessageReader<StreamMessage>, MessageBuilder<StreamMessage> {

    private final ConcurrentMap<Short, StreamProviderRegistration> providersMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Short, StreamConsumer> consumersMap = new ConcurrentHashMap<>();

    @Inject
    StreamSupport() {
    }

    public StreamProviderRegistration provideStream(StreamProvider streamProvider) {
        final StreamProviderRegistration registration = new StreamProviderRegistration(this, streamProvider);
        final short streamId = registration.getStreamId();
        final StreamProviderRegistration old = this.providersMap.putIfAbsent(streamId, registration);
        if(old != null) {
            throw new RuntimeException("we already has stream provider for " + streamId + ": " + old);
        }
        return registration;
    }

    /**
     * remove specified registration
     * @param registration
     */
    void unregister(StreamProviderRegistration registration) {
        this.providersMap.remove(registration.getStreamId(), registration);
    }

    @Override
    public StreamMessage build(Parcel parcel) throws Exception {
        return null;
    }

    @Override
    public ReadResult<StreamMessage> read(IpcEntryHandlerContext context) throws Exception {
        return null;
    }
}
