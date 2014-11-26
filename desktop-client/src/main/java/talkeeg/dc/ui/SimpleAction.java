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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * simple action for using lamda (see {@link  talkeeg.dc.ui.SimpleAction.ActionListener} ) and inline configuring
 *
 * Created by wayerr on 26.11.14.
 */
public final class SimpleAction extends AbstractAction {
    /**
     * iface for lambda called in this action <p/>
     * @see #actionPerformed(java.awt.event.ActionEvent)
     */
    @FunctionalInterface
    public interface ActionListener {

        /**
         * method which called from {@link talkeeg.dc.ui.SimpleAction#actionPerformed(java.awt.event.ActionEvent)}
         * @param thiz link to SimpleAction instance which invoke this method
         * @param e action event
         */
        public void actionPerformed(SimpleAction thiz, ActionEvent e);
    }


    private final ActionListener listener;

    /**
     * construct simple action with id ana action listener
     * @param id is an {@link #ACTION_COMMAND_KEY } value
     * @param listener
     */
    public SimpleAction(String id, ActionListener listener) {
        putValue(ACTION_COMMAND_KEY, id);
        if(listener == null) {
           throw new IllegalArgumentException("listener is null");
        }
        this.listener = listener;
    }

    /**
     * set Action.NAME value on this action
     * @param name
     * @return
     */
    public SimpleAction name(String name) {
        putValue(NAME, name);
        return this;
    }

    /**
     * set Action.SHORT_DESCRIPTION value on this action
     * @param name
     * @return
     */
    public SimpleAction shortDescription(String name) {
        putValue(SHORT_DESCRIPTION, name);
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        listener.actionPerformed(this, e);
    }
}
