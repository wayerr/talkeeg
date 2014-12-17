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

package talkeeg.dc.ui;

import dagger.Module;
import dagger.Provides;
import talkeeg.common.barcode.BarcodeService;
import talkeeg.common.core.AcquaintedClientsService;
import talkeeg.common.core.AcquaintedUsersService;
import talkeeg.common.core.CoreModule;
import talkeeg.common.core.HelloService;
import talkeeg.dc.ui.barcode.BarcodeView;

import javax.inject.Singleton;

/**
 * Created by wayerr on 03.12.14.
 */
@Module(
        injects = {
                BarcodeView.class,
                ContactsModel.class,
                MessagesView.class
        },
        includes = {
                CoreModule.class
        },
        library = true,
        complete = false
)
public class UiModule {

    @Provides
    @Singleton
    BarcodeView provideBarcodeView(BarcodeService service, HelloService helloService) {
        return new BarcodeView(service, helloService);
    }
}
