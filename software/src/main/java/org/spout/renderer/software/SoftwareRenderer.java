/*
 * This file is part of Caustic Software.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic Software is licensed under the Spout License Version 1.
 *
 * Caustic Software is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic Software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.renderer.software;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import org.spout.renderer.api.gl.Context.Capability;
import org.spout.renderer.api.util.Rectangle;

/**
 *
 */
class SoftwareRenderer extends Canvas {
    private final JFrame frame;
    private int width, height;
    private int scale = 1;
    private boolean initialized = false;
    private volatile boolean closeRequested = false;
    private int capabilities = 0;
    private final Rectangle viewPort = new Rectangle(width, height);
    private int clearColor;
    private BufferedImage image;
    private int[] pixels;
    private short[] depths;
    private boolean depthWriting = true;
    private SoftwareProgram program;

    SoftwareRenderer() {
        frame = new JFrame("Caustic");
        final JPanel panel = new JPanel(new BorderLayout());
        panel.add(this, BorderLayout.CENTER);
        frame.setContentPane(panel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowCloseListener());
    }

    int getWindowHeight() {
        return height;
    }

    int getWindowWidth() {
        return width;
    }

    void setWindowSize(int width, int height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            if (initialized) {
                updateImage();
            }
        }
    }

    void setWindowTitle(String title) {
        frame.setTitle(title);
    }

    String getWindowTitle() {
        return frame.getTitle();
    }

    boolean isCloseRequested() {
        final boolean oldCloseRequested = closeRequested;
        closeRequested = false;
        return oldCloseRequested;
    }

    Rectangle getViewPort() {
        return viewPort;
    }

    void setViewPort(Rectangle viewPort) {
        this.viewPort.set(viewPort);
    }

    void setCapabilityEnabled(Capability capability, boolean enabled) {
        if (enabled) {
            capabilities |= 1 << capability.ordinal();
        } else {
            capabilities &= ~(1 << capability.ordinal());
        }
    }

    boolean isEnabled(Capability capability) {
        return (capabilities & 1 << capability.ordinal()) != 0;
    }

    void setClearColor(int clearColor) {
        this.clearColor = clearColor;
    }

    void enableDepthWriting(boolean enabled) {
        depthWriting = enabled;
    }

    SoftwareProgram getProgram() {
        return program;
    }

    void setProgram(SoftwareProgram program) {
        this.program = program;
    }

    void init() {
        updateImage();
        frame.setVisible(true);
        createBufferStrategy(3);
        viewPort.setSize(width, height);
        initialized = true;
    }

    private void updateImage() {
        final Dimension size = new Dimension(width * scale, height * scale);
        setSize(size);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        depths = new short[width * height];
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    void dispose() {
        frame.dispose();
        image = null;
        pixels = null;
        depths = null;
        program = null;
        initialized = false;
    }

    void render() {
        final BufferStrategy bufferStrategy = getBufferStrategy();
        final Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.drawImage(image, 0, 0, width * scale, height * scale, null);
        graphics.dispose();
        bufferStrategy.show();
    }

    void clearPixels() {
        Arrays.fill(pixels, clearColor);
        Arrays.fill(depths, Short.MAX_VALUE);
    }

    void writePixel(int x, int y, short z, int color) {
        if (x < 0 || x >= width || y < 0 || y >= width) {
            throw new IllegalArgumentException("(" + x + ", " + y + ") not within (0, 0) to (" + (width - 1) + ", " + (height - 1) + ")");
        }
        final int i = x + y * width;
        if (isEnabled(Capability.DEPTH_TEST)) {
            if (z < depths[i]) {
                pixels[i] = color;
                if (depthWriting) {
                    depths[i] = z;
                }
            }
        } else {
            pixels[i] = color;
        }
    }

    private class WindowCloseListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent event) {
            closeRequested = true;
        }
    }
}
