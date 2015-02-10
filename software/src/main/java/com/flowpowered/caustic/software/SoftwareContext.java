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

import java.nio.ByteBuffer;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.gl.Context;
import com.flowpowered.caustic.api.gl.FrameBuffer;
import com.flowpowered.caustic.api.gl.Program;
import com.flowpowered.caustic.api.gl.RenderBuffer;
import com.flowpowered.caustic.api.gl.Shader;
import com.flowpowered.caustic.api.gl.Texture;
import com.flowpowered.caustic.api.gl.Texture.InternalFormat;
import com.flowpowered.caustic.api.gl.VertexArray;
import com.flowpowered.caustic.api.util.Rectangle;

/**
 *
 */
public class SoftwareContext extends Context {
    private final SoftwareRenderer renderer = new SoftwareRenderer();

    @Override
    public void create() {
        super.create();
        renderer.init();
    }

    @Override
    public void destroy() {
        renderer.dispose();
        super.destroy();
    }

    @Override
    public FrameBuffer newFrameBuffer() {
        return new SoftwareFrameBuffer();
    }

    @Override
    public Program newProgram() {
        return new SoftwareProgram(renderer);
    }

    @Override
    public RenderBuffer newRenderBuffer() {
        return null;
    }

    @Override
    public Shader newShader() {
        return new SoftwareShader();
    }

    @Override
    public Texture newTexture() {
        return new SoftwareTexture(renderer);
    }

    @Override
    public VertexArray newVertexArray() {
        return new SoftwareVertexArray(renderer);
    }

    @Override
    public String getWindowTitle() {
        return renderer.getWindowTitle();
    }

    @Override
    public void setWindowTitle(String title) {
        renderer.setWindowTitle(title);
    }

    @Override
    public void setWindowSize(Vector2i windowSize) {
        renderer.setWindowSize(windowSize.getX(), windowSize.getY());
    }

    @Override
    public int getWindowWidth() {
        return renderer.getWindowWidth();
    }

    @Override
    public int getWindowHeight() {
        return renderer.getWindowHeight();
    }

    @Override
    public void updateDisplay() {
        renderer.render();
    }

    @Override
    public void setClearColor(Vector4f color) {
        renderer.setClearColor(SoftwareUtil.pack(color));
    }

    @Override
    public void clearCurrentBuffer() {
        renderer.clearPixels();
    }

    @Override
    public void disableCapability(Capability capability) {
        renderer.setCapabilityEnabled(capability, false);
    }

    @Override
    public void enableCapability(Capability capability) {
        renderer.setCapabilityEnabled(capability, true);
    }

    @Override
    public void setDepthMask(boolean enabled) {
        renderer.enableDepthWriting(enabled);
    }

    @Override
    public void setBlendingFunctions(int bufferIndex, BlendFunction source, BlendFunction destination) {

    }

    @Override
    public void setViewPort(Rectangle viewPort) {
        renderer.setViewPort(viewPort);
    }

    @Override
    public ByteBuffer readFrame(Rectangle size, InternalFormat format) {
        return null;
    }

    @Override
    public boolean isWindowCloseRequested() {
        return renderer.isCloseRequested();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.SOFTWARE;
    }
}
