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

import java.awt.*;

/**
 * ui utils
 *
 * Created by wayerr on 03.12.14.
 */
public final class UiUtils {

    private static final int MIN_SZIE = 100;

    private UiUtils() {

    }

    /**
     * set window position and size by screen and content size
     * @param window
     */
    public static void setWindowBounds(Container window) {
        final Dimension minimumSize = window.getMinimumSize();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = minimumSize.width;
        int height = minimumSize.height;
        final float factor = .3f;
        if(width < MIN_SZIE) {
            width = (int)(screenSize.width * factor);
        }
        if(height < MIN_SZIE) {
            height = (int)(screenSize.height * factor);
        }
        final int x = (screenSize.width - width)/2;
        final int y = (screenSize.height - height)/2;
        window.setBounds(x, y, width, height);
    }
}
