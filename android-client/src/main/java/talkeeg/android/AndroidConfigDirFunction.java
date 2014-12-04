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
import android.content.Context;
import com.google.common.base.Function;

import java.io.File;

/**
 * function to get config dir in andriod OS
 *
 * Created by wayerr on 04.12.14.
 */
final class AndroidConfigDirFunction implements Function<String, File> {

    private final Application application;

    AndroidConfigDirFunction(Application application) {
        this.application = application;
    }

    @Override
    public File apply(String input) {
        final Context context = application.getApplicationContext();
        return context.getFilesDir();
    }
}
