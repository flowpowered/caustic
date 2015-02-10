/*
 * This file is part of Caustic LWJGL, licensed under the MIT License (MIT).
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
package com.flowpowered.caustic.lwjgl.gl20;

import java.nio.ByteBuffer;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4f;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.flowpowered.caustic.api.gl.Context;
import com.flowpowered.caustic.api.gl.FrameBuffer;
import com.flowpowered.caustic.api.gl.Program;
import com.flowpowered.caustic.api.gl.RenderBuffer;
import com.flowpowered.caustic.api.gl.Shader;
import com.flowpowered.caustic.api.gl.Texture;
import com.flowpowered.caustic.api.gl.Texture.InternalFormat;
import com.flowpowered.caustic.api.gl.VertexArray;
import com.flowpowered.caustic.api.util.CausticUtil;
import com.flowpowered.caustic.api.util.Rectangle;
import com.flowpowered.caustic.lwjgl.LWJGLUtil;

/**
 * An OpenGL 2.0 implementation of {@link com.flowpowered.caustic.api.gl.Context}.
 *
 * @see com.flowpowered.caustic.api.gl.Context
 */
public class GL20Context extends Context {
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
            throw new IllegalStateException("Unable to create OpenGL context", ex);
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
        return new ContextAttribs(2, 0);
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
    public FrameBuffer newFrameBuffer() {
        return new GL20FrameBuffer();
    }

    @Override
    public Program newProgram() {
        return new GL20Program();
    }

    @Override
    public RenderBuffer newRenderBuffer() {
        return new GL20RenderBuffer();
    }

    @Override
    public Shader newShader() {
        return new GL20Shader();
    }

    @Override
    public Texture newTexture() {
        return new GL20Texture();
    }

    @Override
    public VertexArray newVertexArray() {
        return new GL20VertexArray();
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
    public void setClearColor(Vector4f color) {
        checkCreated();
        GL11.glClearColor(color.getX(), color.getY(), color.getZ(), color.getW());
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
    public ByteBuffer readFrame(Rectangle size, InternalFormat format) {
        checkCreated();
        // Create the image buffer
        final ByteBuffer buffer = CausticUtil.createByteBuffer(size.getArea() * format.getBytes());
        // Read from the front buffer
        GL11.glReadBuffer(GL11.GL_FRONT);
        // Use byte alignment
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        // Read the pixels
        GL11.glReadPixels(size.getX(), size.getY(), size.getWidth(), size.getHeight(), format.getFormat().getGLConstant(), format.getComponentType().getGLConstant(), buffer);
        // Check for errors
        LWJGLUtil.checkForGLError();
        return buffer;
    }

    @Override
    public boolean isWindowCloseRequested() {
        return Display.isCloseRequested();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL20;
    }
}
