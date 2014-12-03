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

import dagger.ObjectGraph;
import talkeeg.dc.ui.barcode.BarcodeView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * manager of {@link talkeeg.dc.ui.ComponentOwner views}
 *
 * Created by wayerr on 03.12.14.
 */
class ViewsManager implements ComponentOwner {
    private final List<View> views = new ArrayList<>();
    private JMenu menu;
    private final JPanel panel;
    private View currentView;

    ViewsManager(ObjectGraph objectGraph) {
        views.add(objectGraph.get(BarcodeView.class));
        this.panel = new JPanel(new BorderLayout());
    }

    JMenu getMenu() {
        if(this.menu == null) {
            this.menu = new JMenu("views");
            for(View owner : views) {
                this.menu.add(new SimpleAction(owner.toString(), (thiz, e) -> {
                    setCurrentView(owner);
                }).name(owner.getTitle()));
            }
        }
        return this.menu;
    }

    private void setCurrentView(View currentView) {
        if(this.currentView == currentView) {
            return;
        }
        this.currentView = currentView;
        this.panel.removeAll();
        if(this.currentView != null) {
            this.panel.add(this.currentView.getComponent(), BorderLayout.CENTER);
            this.panel.validate();
        }
    }

    @Override
    public JComponent getComponent() {
        //this temporally, after adding additional views we must implement a way for choosing default view
        if(!this.views.isEmpty()) {
            setCurrentView(this.views.get(0));
        }
        return this.panel;
    }
}
