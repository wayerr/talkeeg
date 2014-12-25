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

package talkeeg.android.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import talkeeg.android.R;

/**
 * action which display confirmation dialog on click
 * Created by wayerr on 25.12.14.
 */
public final class ConfirmAction implements View.OnClickListener {

    private DialogInterface.OnClickListener onYes;
    private DialogInterface.OnClickListener onNo;
    private AlertDialog dialog;
    private int messageResource;

    public ConfirmAction() {
    }

    public ConfirmAction onYes(DialogInterface.OnClickListener onYes) {
        setOnYes(onYes);
        return this;
    }
    public void setOnYes(DialogInterface.OnClickListener onYes) {
        this.onYes = onYes;
    }

    public ConfirmAction onNo(DialogInterface.OnClickListener onNo) {
        setOnNo(onNo);
        return this;
    }

    public void setOnNo(DialogInterface.OnClickListener onNo) {
        this.onNo = onNo;
    }

    public ConfirmAction messageResource(int resource) {
        setMessageResource(resource);
        return this;
    }

    public void setMessageResource(int resource) {
        this.messageResource = resource;
    }

    @Override
    public void onClick(View v) {
        if(this.dialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setCancelable(false);
            builder.setTitle(R.string.dialog_confirm_title);
            builder.setMessage(this.messageResource);
            builder.setPositiveButton(R.string.button_yes, this.onYes);
            builder.setNegativeButton(R.string.button_no, this.onNo);
            this.dialog = builder.create();
        }
        this.dialog.show();
    }
}
