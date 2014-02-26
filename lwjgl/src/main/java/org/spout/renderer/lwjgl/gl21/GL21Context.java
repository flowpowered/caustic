/*
 * This file is part of Caustic LWJGL.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic LWJGL is licensed under the Spout License Version 1.
 *
 * Caustic LWJGL is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic LWJGL is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.lwjgl.gl21;

import java.nio.ByteBuffer;

import com.flowpowered.math.vector.Vector2i;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import org.spout.renderer.api.data.Color;
import org.spout.renderer.api.gl.Context;
import org.spout.renderer.api.gl.Texture.Format;
import org.spout.renderer.api.util.CausticUtil;
import org.spout.renderer.api.util.Rectangle;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.1 implementation of {@link org.spout.renderer.api.gl.Context}.
 *
 * @see org.spout.renderer.api.gl.Context
 */
public class GL21Context extends Context {
    protected GL21Context() {
    }

    @Override
    public void create() {
        checkNotCreated();
        // Attempt to create the display
        try {
            PixelFormat pixelFormat = new PixelFormat();
            if (msaa > 0) {
                pixelFormat = pixelFormat.withSamples(this.msaa);
            }
            Display.create(pixelFormat, createContextAttributes());
        } catch (LWJGLException ex) {
            throw new IllegalStateException("Unable to create OpenGL context: " + ex.getMessage());
        }
        // Check for errors
        LWJGLUtil.checkForGLError();
        // Update the state
        super.create();
    }

    /**
     * Created new context attributes for the version.
     *
     * @return The context attributes
     */
    protected ContextAttribs createContextAttributes() {
        return new ContextAttribs(2, 1);
    }

    @Override
    public void destroy() {
        checkCreated();
        // Display goes after else there's no context in which to check for an error
        LWJGLUtil.checkForGLError();
        Display.destroy();
        super.destroy();
    }

    @Override
    public String getWindowTitle() {
        return Display.getTitle();
    }

    @Override
    public void setWindowTitle(String title) {
        Display.setTitle(title);
    }

    @Override
    public void setWindowSize(Vector2i windowSize) {
        try {
            Display.setDisplayMode(new DisplayMode(windowSize.getX(), windowSize.getY()));
        } catch (LWJGLException ex) {
            throw new IllegalStateException("Unable to set display size: " + ex.getMessage());
        }
    }

    @Override
    public int getWindowWidth() {
        return Display.getWidth();
    }

    @Override
    public int getWindowHeight() {
        return Display.getHeight();
    }

    @Override
    public void updateDisplay() {
        checkCreated();
        Display.update();
    }

    @Override
    public void setClearColor(Color color) {
        checkCreated();
        Color normC = color.normalize();
        GL11.glClearColor(normC.getRed(), normC.getGreen(), normC.getBlue(), normC.getAlpha());
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void clearCurrentBuffer() {
        checkCreated();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void enableCapability(Capability capability) {
        checkCreated();
        GL11.glEnable(capability.getGLConstant());
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void disableCapability(Capability capability) {
        checkCreated();
        GL11.glDisable(capability.getGLConstant());
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setDepthMask(boolean enabled) {
        checkCreated();
        GL11.glDepthMask(enabled);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setBlendingFunctions(int bufferIndex, BlendFunction source, BlendFunction destination) {
        checkCreated();
        GL11.glBlendFunc(source.getGLConstant(), destination.getGLConstant());
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setViewPort(Rectangle viewPort) {
        checkCreated();
        GL11.glViewport(viewPort.getX(), viewPort.getY(), viewPort.getWidth(), viewPort.getHeight());
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public ByteBuffer readCurrentFrame(Rectangle size, Format format) {
        checkCreated();
        final ByteBuffer buffer = CausticUtil.createByteBuffer(size.getArea() * 3);
        GL11.glReadBuffer(GL11.GL_FRONT);
        GL11.glReadPixels(size.getX(), size.getY(), size.getWidth(), size.getHeight(), format.getGLConstant(), GL11.GL_UNSIGNED_BYTE, buffer);
        // Check for errors
        LWJGLUtil.checkForGLError();
        return buffer;
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL21;
    }
}
