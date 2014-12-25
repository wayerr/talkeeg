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
import talkeeg.android.barcode.CreateBarcodeActivity;
import talkeeg.android.barcode.ReadBarcodeActivity;

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
        Class<? extends Activity> activityClass;
        switch(item.getItemId()) {
            case R.id.actionShowBarcode:
                activityClass = CreateBarcodeActivity.class;
                break;
            case R.id.actionReadBarcode:
                activityClass = ReadBarcodeActivity.class;
                break;
            //case R.id.actionMessages:
            //    activityClass = MessagesActivity.class;
            //    break;
            case R.id.actionAcquaintedUsers:
                activityClass = AcquaintedUsersActivity.class;
                break;
            default:
                activityClass = null;
        }
        if(activityClass == null) {
            return false;
        }
        Intent intent = new Intent(this.activity, activityClass);
        this.activity.startActivity(intent);
        return true;
    }
}
