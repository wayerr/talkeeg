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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.FrameLayout;

/**
 * Created by wayerr on 25.12.14.
 */
public final class CheckableLayout extends FrameLayout implements Checkable {
    private final View layout;
    private final Checkable checkableView;

    public CheckableLayout(Context context, int resource, int checkableId) {
        super(context);

        final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = inflater.inflate(resource, this, true);
        View view = this.layout.findViewById(checkableId);
        //checkable view must only display state, but can-not modify it
        view.setFocusable(false);
        view.setClickable(false);
        this.checkableView = (Checkable)view;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checkableView.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return this.checkableView.isChecked();
    }

    @Override
    public void toggle() {
        this.checkableView.toggle();
    }
}
