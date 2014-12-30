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

import org.junit.AfterClass;
import org.junit.Test;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.core.Env;
import talkeeg.common.core.OwnedIdentityCardsService;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * Created by wayerr on 29.12.14.
 */
public class StreamSupportTest {

    @AfterClass
    public static void afterClass() {
        Env.getInstance().close();
    }

    @Test
    public void testStreams() throws Exception {
        Env instance = Env.getInstance();
        final Int128 clientId = instance.get(OwnedIdentityCardsService.class).getClientId();
        final StreamSupport support = instance.get(StreamSupport.class);
        final SampleStreamProvider provider = new SampleStreamProvider();
        final StreamProviderRegistration providerRegistration = support.registerProvider(provider, clientId);
        final StreamConsumerRegistration consumerRegistration = support.registerConsumer(new SampleStreamConsumer(provider), clientId, providerRegistration.getStreamId());
        consumerRegistration.start();
    }

    private static class SampleStreamProvider implements StreamProvider {

        private final String sampleData = "пример utf данных";

        @Override
        public void open(StreamProviderRegistration registration) {

        }

        @Override
        public BinaryData provide(StreamProviderRegistration registration, int size) {
            byte[] bytes = sampleData.getBytes(StandardCharsets.UTF_8);
            byte dst[] = new byte[size];
            for(int i = 0; i < size; ++i) {
                dst[i] = bytes[i % bytes.length];
            }
            return new BinaryData(dst);
        }

        @Override
        public void abort(StreamProviderRegistration registration) {

        }

        @Override
        public long getLength() {
            return 2*1024*sampleData.length();
        }
    }

    private static class SampleStreamConsumer implements StreamConsumer {
        private final SampleStreamProvider provider;

        public SampleStreamConsumer(SampleStreamProvider provider) {
            this.provider = provider;
        }

        @Override
        public void open(StreamConsumerRegistration registration) {
            assertEquals(provider.getLength(), registration.getLength());
        }

        @Override
        public void consume(StreamConsumerRegistration registration, BinaryData data) {
            System.out.println(new String(data.getData(), StandardCharsets.UTF_8));
        }

        @Override
        public void close(StreamConsumerRegistration registration) {

        }
    }
}
