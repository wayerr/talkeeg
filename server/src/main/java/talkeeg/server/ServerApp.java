/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.server;

import dagger.ObjectGraph;
import org.apache.http.nio.protocol.UriHttpAsyncRequestHandlerMapper;
import talkeeg.common.conf.Config;
import talkeeg.common.core.CoreModule;
import talkeeg.common.core.CryptoService;
import talkeeg.common.ipc.IpcServiceManager;
import talkeeg.common.util.ServiceLocator;
import talkeeg.httpserver.HandlerPrefixLenFunctions;
import talkeeg.httpserver.HttpServer;
import talkeeg.httpserver.fs.HttpFileHandler;
import talkeeg.httpserver.fs.ResourceFileSystem;

/**
 * Server application
 * Created by wayerr on 10.12.14.
 */
public final class ServerApp {

    private static final ServerApp INTANCE = new ServerApp();

    private final ObjectGraph graph;
    private final IpcServiceManager serviceManager;
    private final CryptoService cryptoService;
    private final Config config;
    private HttpServer httpServer;

    private ServerApp() {
        final MainModule mainModule = new MainModule();
        this.graph = ObjectGraph.create(mainModule);
        mainModule.setObjectGraph(this.graph);
        this.serviceManager = this.graph.get(IpcServiceManager.class);
        this.cryptoService = this.graph.get(CryptoService.class);
        this.config = this.graph.get(Config.class);
    }

    static ServerApp getInstance() {
        return INTANCE;
    }

    void start() {
        this.httpServer = this.graph.get(HttpServer.class);
        UriHttpAsyncRequestHandlerMapper registry = this.httpServer.getRegistry();
        registerHttpHandlers(registry);
        CoreModule.init(this.graph.get(ServiceLocator.class));
        this.httpServer.run();
    }

    private void registerHttpHandlers(UriHttpAsyncRequestHandlerMapper registry) {
        registry.register("/res/*", new HttpFileHandler(HandlerPrefixLenFunctions.from("/res/"), new ResourceFileSystem("/web/")));
        registry.register("/barcode.png", this.graph.get(BarcodeProvider.class));
    }

    void stop() {
        serviceManager.stop();
        this.config.save();
    }

    public static <T> T get(Class<T> clazz) {
        return INTANCE.graph.get(clazz);
    }
}
