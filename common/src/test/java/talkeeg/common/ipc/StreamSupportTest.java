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
import org.junit.BeforeClass;
import org.junit.Test;
import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.conf.DefaultConfiguration;
import talkeeg.common.core.CurrentAddressesService;
import talkeeg.common.core.Env;
import talkeeg.common.core.OwnedIdentityCardsService;
import talkeeg.common.model.ClientAddress;
import talkeeg.common.util.TgAddress;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by wayerr on 29.12.14.
 */
public class StreamSupportTest {

    private static Env secondEnv;
    private static Env firstEnv;

    @BeforeClass
    public static void beforeClass() {
        firstEnv = new Env("first", DefaultConfiguration.builder().put("net.port", 11665).build());
        secondEnv = new Env("second", DefaultConfiguration.builder().put("net.port", 11664).build());
    }

    @AfterClass
    public static void afterClass() {
        firstEnv.close();
        secondEnv.close();
    }

    @Test
    public void testStreams() throws Exception {
        System.out.println("testStreams");
        final Int128 secondClientId = secondEnv.get(OwnedIdentityCardsService.class).getClientId();
        final SampleStreamProvider provider = new SampleStreamProvider();
        StreamConfig.Builder builder = StreamConfig.builder()
          .streamId((short)1);

        configureBuilder(builder, firstEnv);
        final StreamConsumerRegistration consumerRegistration = secondEnv.get(StreamSupport.class)
          .registerConsumer(new SampleStreamConsumer(provider), builder.build());
        configureBuilder(builder, secondEnv);
        firstEnv.get(StreamSupport.class).registerProvider(provider, builder.build());
        System.out.println("testStreams start");
        consumerRegistration.start();

        Thread.sleep(TimeUnit.SECONDS.toMillis(30));
    }

    protected void configureBuilder(StreamConfig.Builder builder, Env env) {
        builder.setOtherClientId(env.get(OwnedIdentityCardsService.class).getClientId());
        builder.setOtherClientAddress(new ClientAddress(false, TgAddress.to("127.0.0.1", env.get(IpcServiceManager.class).getPort())));
    }

    private static class SampleStreamProvider implements StreamProvider {

        private final String sampleData = "пример utf данных";

        @Override
        public void open(StreamProviderRegistration registration) {
            System.out.println("provider: open");
        }

        @Override
        public BinaryData provide(StreamProviderRegistration registration, int size) {
            System.out.println("provider: provide");
            byte[] bytes = sampleData.getBytes(StandardCharsets.UTF_8);
            byte dst[] = new byte[size];
            for(int i = 0; i < size; ++i) {
                dst[i] = bytes[i % bytes.length];
            }
            return new BinaryData(dst);
        }

        @Override
        public void abort(StreamProviderRegistration registration) {
            System.out.println("provider: abort");
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
            System.out.println("consumer: open");
            assertEquals(provider.getLength(), registration.getLength());
        }

        @Override
        public void consume(StreamConsumerRegistration registration, BinaryData data) {
            System.out.println("consumer: consume");
            System.out.println(new String(data.getData(), StandardCharsets.UTF_8));
        }

        @Override
        public void close(StreamConsumerRegistration registration) {
            System.out.println("consumer: close");

        }
    }
}
