/*
 * This file is part of Caustic Software, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flowpowered.caustic.software;

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

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import com.flowpowered.caustic.api.gl.Context.Capability;
import com.flowpowered.caustic.api.util.CausticUtil;
import com.flowpowered.caustic.api.util.Rectangle;

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
    private final TIntObjectMap<SoftwareTexture> textures = new TIntObjectHashMap<>();

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

    void bindTexture(int unit, SoftwareTexture texture) {
        textures.put(unit, texture);
    }

    void unbindTexture(int unit) {
        textures.remove(unit);
    }

    SoftwareTexture getTexture(int unit) {
        return textures.get(unit);
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

    int readPixelColor(int x, int y) {
        checkBounds(x, y);
        return pixels[x + y * width];
    }

    int readPixelDepth(int x, int y) {
        checkBounds(x, y);
        return depths[x + y * width];
    }

    boolean testDepth(int x, int y, short z) {
        final int i = x + y * width;
        return !isEnabled(Capability.DEPTH_TEST) || z <= depths[i];
    }

    void writePixel(int x, int y, short z, int color) {
        checkBounds(x, y);
        final int i = x + y * width;
        pixels[i] = color;
        if (isEnabled(Capability.DEPTH_TEST) && depthWriting) {
            depths[i] = z;
        }
    }

    private void checkBounds(int x, int y) {
        if (CausticUtil.isDebugEnabled() && (x < 0 || x >= width || y < 0 || y >= width)) {
            throw new IllegalArgumentException("(" + x + ", " + y + ") not within (0, 0) to (" + (width - 1) + ", " + (height - 1) + ")");
        }
    }

    private class WindowCloseListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent event) {
            closeRequested = true;
        }
    }
}
