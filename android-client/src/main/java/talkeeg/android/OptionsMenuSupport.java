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

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

/**
 * coomon code for options menu
 * Created by wayerr on 23.12.14.
 */
final class OptionsMenuSupport {

    private final Activity activity;

    public OptionsMenuSupport(Activity activity) {
        this.activity = activity;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.actionAcquairedUsers:
                this.activity.startActivity(new Intent(activity, AcquaintedUsersActivity.class));
                return true;
        }
        return false;
    }
}
