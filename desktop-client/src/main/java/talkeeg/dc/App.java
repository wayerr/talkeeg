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

package talkeeg.dc;

import dagger.ObjectGraph;
import talkeeg.common.conf.Config;
import talkeeg.common.core.CoreModule;
import talkeeg.common.core.CryptoService;
import talkeeg.common.ipc.IpcServiceManager;
import talkeeg.common.util.ServiceLocator;
import talkeeg.dc.ui.GuiManager;

import java.awt.*;

/**
 * Created by wayerr on 10.12.14.
 */
public final class App {

    private static final App INTANCE = new App();

    private final ObjectGraph graph;
    private final IpcServiceManager serviceManager;
    private final GuiManager guiManager;
    private final CryptoService cryptoService;
    private final Config config;

    private App() {
        final MainModule mainModule = new MainModule();
        this.graph = ObjectGraph.create(mainModule);
        mainModule.setObjectGraph(this.graph);
        this.serviceManager = this.graph.get(IpcServiceManager.class);
        this.cryptoService = this.graph.get(CryptoService.class);
        this.config = this.graph.get(Config.class);
        this.guiManager = new GuiManager(this.graph);
    }

    static App getInstance() {
        return INTANCE;
    }

    void start() {
        CoreModule.init(this.graph.get(ServiceLocator.class));
        EventQueue.invokeLater(this.guiManager);
    }

    void stop() {
        serviceManager.stop();
        this.config.save();
    }

    public static <T> T get(Class<T> clazz) {
        return INTANCE.graph.get(clazz);
    }
}
