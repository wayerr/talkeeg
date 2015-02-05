/*
 * Copyright (c) 2015, wayerr (radiofun@ya.ru).
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

package talkeeg.common.barcode;

/**
 * configuration of barcode renderer process
 *
 * Created by wayerr on 05.02.15.
 */
public final class BarcodeRenderConfig {

    public static final BarcodeRenderConfig DEFAULT = BarcodeRenderConfig.builder().build();

    public static final class Builder {
        private int factor = 4;
        private int background = 0xffffff;
        private int foreground = 0;

        /**
         * pixel multiplicity factor
         * @return
         */
        public int getFactor() {
            return factor;
        }

        /**
         * pixel multiplicity factor
         * @param factor
         * @return
         */
        public Builder factor(int factor) {
            setFactor(factor);
            return this;
        }

        /**
         * pixel multiplicity factor
         * @param factor
         */
        public void setFactor(int factor) {
            this.factor = factor;
        }

        /**
         * background color, default white <p/>
         * specify in RGB format, for example 0xFF0000 - red
         * @return
         */
        public int getBackground() {
            return background;
        }

        /**
         * background color, default white <p/>
         * specify in RGB format, for example 0xFF0000 - red
         * @param background
         * @return
         */
        public Builder background(int background) {
            setBackground(background);
            return this;
        }

        /**
         * background color, default white <p/>
         * specify in RGB format, for example 0xFF0000 - red
         * @param background
         */
        public void setBackground(int background) {
            this.background = background;
        }

        /**
         * foreground color, default black <p/>
         * specify in RGB format, for example 0xFF0000 - red
         * @return
         */
        public int getForeground() {
            return foreground;
        }

        /**
         * foreground color, default black <p/>
         * specify in RGB format, for example 0xFF0000 - red
         * @param foreground
         * @return
         */
        public Builder foreground(int foreground) {
            setForeground(foreground);
            return this;
        }

        /**
         * foreground color, default black <p/>
         * specify in RGB format, for example 0xFF0000 - red
         * @param foreground
         */
        public void setForeground(int foreground) {
            this.foreground = foreground;
        }

        public BarcodeRenderConfig build() {
            return new BarcodeRenderConfig(this);
        }
    }

    private final int factor;
    private final int background;
    private final int foreground;

    private BarcodeRenderConfig(Builder b) {
        this.factor = b.factor;
        this.background = b.background;
        this.foreground = b.foreground;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * pixel multiplicity factor
     * @return
     */
    public int getFactor() {
        return factor;
    }

    /**
     * background color, default white <p/>
     * specify in RGB format, for example 0xFF0000 - red
     * @return
     */
    public int getBackground() {
        return background;
    }

    /**
     * foreground color, default black <p/>
     * specify in RGB format, for example 0xFF0000 - red
     * @return
     */
    public int getForeground() {
        return foreground;
    }
}
