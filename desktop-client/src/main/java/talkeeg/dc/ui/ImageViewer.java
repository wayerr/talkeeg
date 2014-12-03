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
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * component which can display images
 *
 * Created by wayerr on 03.12.14.
 */
public final class ImageViewer extends JComponent {
    private Image image;

    public ImageViewer() {
        setOpaque(true);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        if(this.image == image) {
            return;
        }
        final Image old = this.image;
        this.image = image;
        invalidate();
        repaint();
        firePropertyChange("image", old, image);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Image image = this.image;
        if(image == null) {
            return;
        }
        final int imageWidth = image.getWidth(this);
        final int imageHeight = image.getHeight(this);
        int x = (getWidth() - imageWidth)/2;
        int y = (getHeight() - imageHeight)/2;
        g.drawImage(image, x, y, this);
    }

    @Override
    public Dimension getMinimumSize() {
        if(this.image != null) {
            return new Dimension(this.image.getWidth(this), this.image.getHeight(this));
        }
        return super.getMinimumSize();
    }
}
