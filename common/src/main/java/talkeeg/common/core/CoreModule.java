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
import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import talkeeg.bf.*;
import talkeeg.bf.schema.PrimitiveEntry;
import talkeeg.bf.schema.Schema;
import talkeeg.bf.schema.SchemaSource;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.conf.Config;
import talkeeg.common.ipc.IpcLifecycleEvent;
import talkeeg.common.ipc.IpcService;
import talkeeg.common.ipc.IpcServiceManager;
import talkeeg.common.model.*;
import talkeeg.common.util.WakeUpAtEvent;
import talkeeg.mb.MessageBusRegistry;

import javax.inject.Singleton;

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
        IpcServiceManager.class,
        IpcService.class,
        MessageBusRegistry.class
    }
)
public final class CoreModule {

    @Provides
    @Singleton
    CurrentAddressesService provideCurrentAddressesService(Config config) {
        final PublicIpService externalIpFunction = new PublicIpService(config);
        return new CurrentAddressesService(externalIpFunction);
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
            .putType(CommandResult.class, CommandResult.STRUCT_BUILDER_FACTORY)
            .putType(UserIdentityCard.class, UserIdentityCard.STRUCT_BUILDER_FACTORY)
            .putType(ClientIdentityCard.class, ClientIdentityCard.STRUCT_BUILDER_FACTORY)
            .putType(ClientAddresses.class, ClientAddresses.STRUCT_BUILDER_FACTORY)
            .putType(ClientAddress.class, ClientAddress.STRUCT_BUILDER_FACTORY)
            .putType(Hello.class, Hello.STRUCT_BUILDER_FACTORY)
            .build();
    }

    @Provides
    @Singleton
    IpcService provideIpcService(IpcServiceManager ipcServiceManager) {
        return ipcServiceManager.getIpc();
    }

    @Provides
    @Singleton
    MessageBusRegistry provideMessageBusRegistry() {
        return new MessageBusRegistry();
    }

    /**
     * an ugly workaround for binding some wakeup-r on event buses <p/>
     * in future we must bind services on remote events instead of lifecycle event (for example, by using {@link dagger.Lazy})
     * @param objectGraph
     */
    public static void init(ObjectGraph objectGraph) {
        final MessageBusRegistry registry = objectGraph.get(MessageBusRegistry.class);
        registry.getOrCreateBus(IpcServiceManager.MB_SERVICE_LIFECYCLE).register(new WakeUpAtEvent<>(objectGraph, AcquaintService.class));
    }
}
