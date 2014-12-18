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

package talkeeg.common.util;

import java.util.Calendar;

/**
 * date utils
 * Created by wayerr on 18.12.14.
 */
public final class DateUtils {

    /**
     * simple and fast string representation of date <p/>
     * used only for testing purposes
     * @param millis
     * @return
     */
    public static String toString(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        StringBuilder result = new StringBuilder();
        result.append(cal.get(Calendar.YEAR));
        result.append('.');
        result.append(cal.get(Calendar.MONTH));
        result.append('.');
        result.append(cal.get(Calendar.DAY_OF_MONTH));
        result.append(' ');
        result.append(cal.get(Calendar.HOUR_OF_DAY));
        result.append(':');
        result.append(cal.get(Calendar.MINUTE));
        result.append(':');
        result.append(cal.get(Calendar.SECOND));
        return result.toString();
    }
}
