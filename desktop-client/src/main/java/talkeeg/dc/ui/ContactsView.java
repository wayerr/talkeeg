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

import talkeeg.dc.App;

import javax.swing.*;

/**
 * view of contacts
 * Created by wayerr on 11.12.14.
 */
final class ContactsView implements View {

    private JPanel panel;
    private JTree tree;
    private JTextPane infoPane;
    private ContactsModel contactsModel;

    @Override
    public String getTitle() {
        return "contacts";
    }

    @Override
    public JComponent getComponent() {
        if(panel == null) {
            panel = new JPanel();
            infoPane = new JTextPane();
            contactsModel = App.get(ContactsModel.class);
            tree = new JTree(contactsModel);
            JScrollPane treeScrollPane = new JScrollPane(tree);

            GroupLayout gl = new GroupLayout(panel);
            panel.setLayout(gl);

            gl.setVerticalGroup(gl.createSequentialGroup()
                .addContainerGap()
                .addComponent(treeScrollPane)
                .addGap(UiUtils.GAP)
                .addComponent(infoPane, 0, GroupLayout.DEFAULT_SIZE, 100)
                .addContainerGap()
            );

            gl.setHorizontalGroup(gl.createSequentialGroup()
                .addContainerGap()
                .addGroup(gl.createParallelGroup()
                    .addComponent(treeScrollPane)
                    .addComponent(infoPane)
                )
                .addContainerGap()
            );
        }
        return panel;
    }

}
