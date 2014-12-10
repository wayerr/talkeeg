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
import talkeeg.common.core.CryptoService;
import talkeeg.common.ipc.IpcServiceManager;
import talkeeg.dc.ui.GuiManager;

import java.awt.*;

/**
 * the main class for desktop client
 *
 * Created by wayerr on 26.11.14.
 */
public final class Main {
    private final ObjectGraph graph;
    private final IpcServiceManager serviceManager;
    private final GuiManager guiManager;
    private final CryptoService cryptoService;

    private Main() {
        this.graph = ObjectGraph.create(new MainModule());
        this.serviceManager = this.graph.get(IpcServiceManager.class);
        this.cryptoService = this.graph.get(CryptoService.class);
        this.guiManager = new GuiManager(this.graph);
    }

    public static void main(String args[]) throws Exception {
        Main main = new Main();
        main.start();

        final Thread atShutdownThread = new Thread(main::stop);
        Runtime.getRuntime().addShutdownHook(atShutdownThread);
    }

    private void stop() {
        serviceManager.stop();
    }

    private void start() {
        serviceManager.start();
        cryptoService.init();
        EventQueue.invokeLater(this.guiManager);
    }
}
