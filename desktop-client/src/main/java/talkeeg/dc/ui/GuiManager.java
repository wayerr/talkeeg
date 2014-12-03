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

import com.google.common.io.Resources;
import dagger.ObjectGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GUI manager
 *
 * Created by wayerr on 26.11.14.
 */
public final class GuiManager implements Runnable {
    private static final Logger LOG = Logger.getLogger(GuiManager.class.getName());
    private final ObjectGraph objectGraph;
    private TrayIcon trayIcon;
    private JPopupMenu popupMenu;
    private JFrame frame;
    private ViewsManager viewsManager;

    public GuiManager(ObjectGraph objectGraph) {
        this.objectGraph = objectGraph;
    }

    @Override
    public void run() {

        createTrayIcon();

        this.frame = new JFrame("Talkeeg desktop client");
        if(trayIcon == null) {
            //if tray icon not created then we must show main window
            showMainWindow();
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        final JMenuBar mainMenu = new JMenuBar();
        final JRootPane rootPane = this.frame.getRootPane();
        rootPane.setJMenuBar(mainMenu);
        
        this.viewsManager = new ViewsManager(objectGraph);
        mainMenu.add(this.viewsManager.getMenu());

        rootPane.setContentPane(this.viewsManager.getComponent());
    }

    protected void showMainWindow() {
        UiUtils.setWindowBounds(this.frame);
        this.frame.setVisible(true);
    }

    /**
     * create tray icon if system support this
     */
    protected void createTrayIcon() {
        if(!SystemTray.isSupported()) {
            return;
        }
        try {
            final Image icon = ImageIO.read(Resources.getResource("icon.png"));
            this.trayIcon = new TrayIcon(icon);
            this.trayIcon.setImageAutoSize(true);

            this.popupMenu = new JPopupMenu();
            loadPopupMenuActions();

            final PopupTrayMouseAdapter listener = new PopupTrayMouseAdapter();
            this.trayIcon.addMouseListener(listener);
            this.popupMenu.addPopupMenuListener(listener);

            final SystemTray systemTray = SystemTray.getSystemTray();
            systemTray.add(this.trayIcon);
        } catch(Exception e) {
            LOG.log(Level.SEVERE, "can create tray icon", e);
        }
    }

    private void loadPopupMenuActions() {
        this.popupMenu.add(new SimpleAction("showMainWindow", (t, e) -> this.showMainWindow()).name("Show"));
        this.popupMenu.add(new SimpleAction("exit", (t, e) -> System.exit(0)).name("Exit"));
    }

    /**
     * ugly hack for showing swing popup menu over TrayIcon
     */
    private final class PopupTrayMouseAdapter extends MouseAdapter implements PopupMenuListener {
        private JFrame _helper;

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            hideHelper();
        }


        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            hideHelper();
        }

        protected void hideHelper() {
            if(_helper != null) {
                _helper.setVisible(false);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(popupMenu == null) {
                return;
            }
            if(e.getButton() == MouseEvent.BUTTON3) {
                JFrame helper = getHelper();
                helper.setLocation(e.getX(), e.getY());
                helper.setVisible(true);
                popupMenu.show(helper, 0, 0);
            }
        }

        private JFrame getHelper() {
            if(_helper == null) {
                this._helper = new JFrame();
                this._helper.setUndecorated(true);
                this._helper.setType(Window.Type.POPUP);
                this._helper.setFocusableWindowState(false);
                this._helper.setSize(0, 0);
            }
            return this._helper;
        }
    }
}
