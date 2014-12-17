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

import com.google.common.base.Strings;
import talkeeg.common.core.AcquaintService;
import talkeeg.common.model.ClientAddress;
import talkeeg.dc.App;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by wayerr on 10.12.14.
 */
final class AcquaintView implements View {

    private JPanel panel;
    private JTextField addressBox;
    private JButton acquaintButton;

    @Override
    public String getTitle() {
        return "acquaint";
    }

    @Override
    public JComponent getComponent() {
        if(panel == null) {
            panel = new JPanel();
            addressBox = new JTextField();
            acquaintButton = new JButton(new SimpleAction("doAcquaintByAddress", this::doAcquaintByAddress).name("acquaint"));
            GroupLayout gl = new GroupLayout(panel);
            panel.setLayout(gl);
            gl.setVerticalGroup(gl.createSequentialGroup()
                .addContainerGap()
                .addComponent(addressBox, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(UiUtils.GAP)
                .addComponent(acquaintButton)
                .addContainerGap()
            );
            gl.setHorizontalGroup(gl.createSequentialGroup()
                .addContainerGap()
                .addGroup(gl.createParallelGroup()
                    .addComponent(addressBox, 0, GroupLayout.DEFAULT_SIZE, Integer.MAX_VALUE)
                    .addComponent(acquaintButton, GroupLayout.Alignment.TRAILING)
                )
                .addContainerGap()
            );
        }
        return panel;
    }

    private void doAcquaintByAddress(SimpleAction simpleAction, ActionEvent actionEvent) {
        final String addressString = this.addressBox.getText();
        if(Strings.isNullOrEmpty(addressString)) {
            return;
        }
        ClientAddress address = new ClientAddress(false, addressString);
        AcquaintService service = App.get(AcquaintService.class);
        service.acquaint(address);
    }
}
