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

import talkeeg.bf.Int128;
import talkeeg.common.core.AcquaintedClient;
import talkeeg.common.core.CurrentDestinationService;
import talkeeg.dc.App;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

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
            try {
                this.tree.addTreeSelectionListener(new TreeSelectionListener() {

                    private final CurrentDestinationService currentDestination = App.get(CurrentDestinationService.class);

                    @Override
                    public void valueChanged(TreeSelectionEvent e) {
                        final TreePath path = e.getPath();
                        if(path == null) {
                            return;
                        }
                        final ContactsModel.Node node = (ContactsModel.Node)path.getLastPathComponent();
                        final Object obj = node.getValue();
                        if(obj instanceof AcquaintedClient) {
                            final AcquaintedClient client = (AcquaintedClient)obj;
                            currentDestination.setClientId(client.getId());
                            currentDestination.setUserId(client.getUserId());
                        } else {
                            currentDestination.setClientId(null);
                            currentDestination.setUserId(null);
                        }
                    }
                });
            } catch(Exception e) {
                e.printStackTrace();
            }
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
