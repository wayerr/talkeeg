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

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import dagger.Module;
import dagger.Provides;
import talkeeg.bf.*;
import talkeeg.bf.schema.PrimitiveEntry;
import talkeeg.bf.schema.Schema;
import talkeeg.bf.schema.SchemaSource;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.conf.Config;
import talkeeg.common.ipc.IpcServiceManager;
import talkeeg.common.model.*;
import talkeeg.common.util.*;
import talkeeg.mb.MessageBusRegistry;
import javax.inject.Singleton;
import java.util.List;

/**
 * module for configure instances of services
 * <p>
 * Created by wayerr on 28.11.14.
 */
@Module(
    library = true, complete = false,
    injects = {
        Bf.class,
        CryptoService.class,
        OwnedIdentityCardsService.class,
        BarcodeService.class,
        HelloService.class,
        AcquaintedUsersService.class,
        AcquaintedClientsService.class,
        AcquaintService.class,
        MessageBusRegistry.class,
        ClientsAddressesService.class,
        DataService.class,
        Stringifiers.class,
        CurrentDestinationService.class
    }
)
public final class CoreModule {

    @Provides
    @Singleton
    CurrentAddressesService provideCurrentAddressesService(Config config, final IpcServiceManager ipc) {
        final PublicIpService externalIpFunction = new PublicIpService(config);
        return new CurrentAddressesService(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return ipc.getPort();
            }
        }, externalIpFunction);
    }

    @Provides
    @Singleton
    Bf provideBf() {
        final Schema schema;
        try {
            schema = SchemaSource.fromResource("protocol.xml");
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        return Bf.build()
            .schema(schema)
            .resolver(MetaTypeResolver.builder()
              .putFactory(MetaTypes.BLOB, new Function<TranslatorStaticContext, Translator>() {
                  @Override
                  public Translator apply(TranslatorStaticContext context) {
                      return new BlobTranslator((PrimitiveEntry)context.getEntry(), BlobTranslator.ADAPTER_BINARY_DATA);
                  }
              })
              .build())
            .putType(SingleMessage.class, SingleMessage.STRUCT_BUILDER_FACTORY)
            .putType(Command.class, Command.STRUCT_BUILDER_FACTORY)
            .putType(UserIdentityCard.class, UserIdentityCard.STRUCT_BUILDER_FACTORY)
            .putType(ClientIdentityCard.class, ClientIdentityCard.STRUCT_BUILDER_FACTORY)
            .putType(ClientAddresses.class, ClientAddresses.STRUCT_BUILDER_FACTORY)
            .putType(ClientAddress.class, ClientAddress.STRUCT_BUILDER_FACTORY)
            .putType(Hello.class, Hello.STRUCT_BUILDER_FACTORY)
            .putType(Data.class, Data.STRUCT_BUILDER_FACTORY)
            .putType(ResponseData.class, ResponseData.STRUCT_BUILDER_FACTORY)
            .putType(StreamMessage.class, StreamMessage.STRUCT_BUILDER_FACTORY)
            .putType(StreamRequest.class, StreamRequest.STRUCT_BUILDER_FACTORY)
            .putType(StreamHead.class, StreamHead.STRUCT_BUILDER_FACTORY)
            .putType(StreamResponse.class, StreamResponse.STRUCT_BUILDER_FACTORY)
            .putType(StreamOffer.class, StreamOffer.STRUCT_BUILDER_FACTORY)
            .build();
    }

    @Provides
    @Singleton
    Stringifiers provideStringifiers(final ServiceLocator serviceLocator) {
        ImmutableMap.Builder<Class<?>, Stringifier<?>> builder = ImmutableMap.builder();
        builder.put(AcquaintedUser.class, new Stringifier<AcquaintedUser>() {
            @Override
            public void toString(AcquaintedUser user, StringBuilder sb) {
                UserIdentityCard identityCard = user.getIdentityCard();
                Object string = identityCard.getAttrs().get(UserIdentityCard.ATTR_NICK);
                if(string == null) {
                    string = Arrays.toHexString(user.getId().getData());
                }
                sb.append(string);
            }
        });

        builder.put(AcquaintedClient.class, new Stringifier<AcquaintedClient>() {
            @Override
            public void toString(AcquaintedClient client, StringBuilder sb) {
                final Int128 id = client.getId();
                ClientIdentityCard cic = client.getIdentityCard();
                boolean hasName = false;
                if(cic != null) {
                    String name = StringUtils.toString(cic.getAttrs().get(ClientIdentityCard.ATTR_NAME));
                    if(name != null) {
                        sb.append(name).append(" \n");
                        hasName = true;
                    }
                }
                final List<ClientAddress> addresses = serviceLocator.get(ClientsAddressesService.class).getSuitableAddress(id);
                if(addresses != null && !addresses.isEmpty()) {
                    sb.append(addresses.get(0).getValue());
                    hasName = true;
                }
                if(!hasName) {
                    sb.append(Arrays.toHexString(id.getData()));
                }
            }
        });
        return new Stringifiers(builder.build());
    }

    @Provides
    @Singleton
    MessageBusRegistry provideMessageBusRegistry() {
        return new MessageBusRegistry();
    }

    @Provides
    @Singleton
    ClientNameService provideClientNameService(Config config) {
        return new ClientNameService(config, new Supplier<String>() {
            @Override
            public String get() {
                return OS.getInstance().getHostName();
            }
        });
    }

    /**
     * an ugly workaround for binding some wakeup-r on event buses <p/>
     * in future we must bind services on remote events instead of lifecycle event (for example, by using {@link dagger.Lazy})
     * @param locator
     */
    public static void init(ServiceLocator locator) {
        final MessageBusRegistry registry = locator.get(MessageBusRegistry.class);
        registry.getOrCreateBus(IpcServiceManager.MB_SERVICE_LIFECYCLE).register(new WakeUpAtEvent<>(locator, AcquaintService.class));
        locator.get(IpcServiceManager.class).start();
        locator.get(CryptoService.class).init();
    }
}
