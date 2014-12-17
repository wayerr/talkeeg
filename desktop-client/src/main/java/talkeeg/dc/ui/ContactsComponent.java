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

import talkeeg.common.core.AcquaintedClient;
import talkeeg.dc.App;

import javax.swing.*;

/**
 * GUI component with list (or three) of acquaint contacts
 * Created by wayerr on 17.12.14.
 */
public final class ContactsComponent implements ComponentOwner {
    private ContactsModel contactsModel;
    private JScrollPane treeScrollPane;
    private JTree tree;

    public ContactsComponent() {

    }

    @Override
    public JComponent getComponent() {
        if(this.treeScrollPane == null) {
            this.contactsModel = App.get(ContactsModel.class);
            this.tree = new JTree(contactsModel);
            this.tree.setRootVisible(false);
            this.tree.setShowsRootHandles(true);
            this.treeScrollPane = new JScrollPane(this.tree);
        }
        return this.treeScrollPane;
    }

    public AcquaintedClient getSelectedClient() {
        Object obj = tree.getLastSelectedPathComponent();
        if(obj == null) {
            return null;
        }
        ContactsModel.Node node = (ContactsModel.Node)obj;
        Object value = node.getValue();
        if(value instanceof AcquaintedClient) {
            return (AcquaintedClient)value;
        }
        return null;
    }
}
