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

package talkeeg.android;

import android.app.Application;
import dagger.ObjectGraph;
import talkeeg.common.core.CryptoService;
import javax.inject.Inject;

/**
 * a main singleton which represents application state
 * Created by wayerr on 03.12.14.
 */
public class App extends Application {

    private static volatile App INTANCE;

    private ObjectGraph objectGraph;

    @Inject
    CryptoService cryptoService;

    @Override
    public void onCreate() {
        super.onCreate();
        this.objectGraph = ObjectGraph.create(new MainModule(this));
        this.objectGraph.inject(this);
        this.cryptoService.init();
        INTANCE = this;//share app instance
    }

    /**
     * invoke {@link dagger.ObjectGraph#get(Class)}
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T get(Class<T> type) {
        return INTANCE.objectGraph.get(type);
    }
}
