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
import talkeeg.common.core.CurrentDestinationService;
import talkeeg.common.core.DataMessage;
import talkeeg.common.core.DataService;
import talkeeg.common.model.Constants;
import talkeeg.common.model.Data;
import talkeeg.common.util.Callback;
import talkeeg.common.util.DateUtils;

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
    private final CurrentDestinationService currentDestination;
    private final DataService dataService;
    private final Callback<DataMessage> dataMessageCallback = this::dataMessageChanged;

    @Inject
    public MessagesView(DataService dataService, CurrentDestinationService currentDestination) {
        this.dataService = dataService;
        this.currentDestination = currentDestination;
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
                .addComponent(contactsComponent, 0, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap()
            );
        }
        return this.pane;
    }

    private void sendMessage(SimpleAction simpleAction, ActionEvent actionEvent) {
        final Int128 clientId = this.currentDestination.getClientId();
        if(clientId == null) {
            return;
        }
        BinaryData stringData = new BinaryData(input.getText().getBytes(StandardCharsets.UTF_8));
        Data data = Data.buidler()
          .action(Constants.DATA_ACTION_CHAT)
          .data(stringData)
          .build();
        DataMessage message = this.dataService.push(clientId, data);
        message.addCallback(this.dataMessageCallback);
    }

    private void dataMessageChanged(DataMessage dataMessage) {
        DataMessage.State state = dataMessage.getState();
        if(state == DataMessage.State.INITIAL) {
            return;
        }
        dataMessage.removeCallback(this.dataMessageCallback);
        addMessageToHistory(dataMessage.getData(), state);
    }

    private void receiveMessage(Data data) {
        addMessageToHistory(data, null);
    }

    private void addMessageToHistory(Data data, DataMessage.State state) {
        getComponent();//init UI
        BinaryData binaryData = data.getData();
        String str = new String(binaryData.getData(), StandardCharsets.UTF_8);
        this.history.append(DateUtils.toString(System.currentTimeMillis()) + "\t " +(state == null? "" : state)+ " | " + str + "\n");
    }
}
