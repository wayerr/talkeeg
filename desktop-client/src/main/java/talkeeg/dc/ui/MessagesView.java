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

import talkeeg.bf.BinaryData;
import talkeeg.bf.Int128;
import talkeeg.common.core.AcquaintedClient;
import talkeeg.common.core.DataService;
import talkeeg.common.model.Constants;
import talkeeg.common.model.Data;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;

/**
 * view of data messages
 * <p/>
 * Created by wayerr on 17.12.14.
 */
final class MessagesView implements View {
    private JPanel pane;
    private JButton send;
    private JTextField input;
    private JTextArea history;
    private ContactsComponent contacts;
    private final DataService dataService;

    @Inject
    public MessagesView(DataService dataService) {
        this.dataService = dataService;
        this.dataService.addHandler(Constants.DATA_ACTION_CHAT, this::receiveMessage);
    }

    @Override
    public String getTitle() {
        return "data messages";
    }

    @Override
    public JComponent getComponent() {
        if(this.pane == null) {
            this.pane = new JPanel();
            GroupLayout gl = new GroupLayout(this.pane);
            this.pane.setLayout(gl);

            this.contacts = new ContactsComponent();

            this.history = new JTextArea();
            this.history.setEditable(false);
            JScrollPane historyPane = new JScrollPane(this.history);
            this.send = new JButton(new SimpleAction("sendMessage", this::sendMessage).name("send"));
            this.input = new JTextField();

            JComponent contactsComponent = contacts.getComponent();
            gl.setVerticalGroup(gl.createSequentialGroup()
                .addContainerGap()
                .addGroup(gl.createParallelGroup()
                    .addComponent(contactsComponent)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(historyPane)
                        .addGap(UiUtils.GAP)
                        .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(input)
                            .addComponent(send)
                        )
                    )
                )
                .addContainerGap()
            );

            gl.setHorizontalGroup(gl.createSequentialGroup()
                .addContainerGap()
                .addGroup(gl.createParallelGroup()
                    .addComponent(historyPane)
                    .addGroup(gl.createSequentialGroup()
                        .addComponent(input)
                        .addGap(UiUtils.GAP)
                        .addComponent(send)
                    )
                )
                .addGap(UiUtils.GAP)
                .addComponent(contactsComponent)
                .addContainerGap()
            );
        }
        return this.pane;
    }

    private void sendMessage(SimpleAction simpleAction, ActionEvent actionEvent) {
        AcquaintedClient selectedClient = this.contacts.getSelectedClient();
        if(selectedClient == null) {
            return;
        }
        Int128 clientId = selectedClient.getId();
        BinaryData stringData = new BinaryData(input.getText().getBytes(StandardCharsets.UTF_8));
        Data data = Data.buidler()
          .action(Constants.DATA_ACTION_CHAT)
          .data(stringData)
          .build();
        this.dataService.push(clientId, data);
    }

    private void receiveMessage(Data data) {
        getComponent();//init UI
        BinaryData binaryData = data.getData();
        String str = new String(binaryData.getData(), StandardCharsets.UTF_8);
        this.history.append(str);
        this.history.append("\n");
    }
}
