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

import java.nio.ByteBuffer;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4f;

import org.spout.renderer.api.gl.Context;
import org.spout.renderer.api.gl.FrameBuffer;
import org.spout.renderer.api.gl.Program;
import org.spout.renderer.api.gl.RenderBuffer;
import org.spout.renderer.api.gl.Shader;
import org.spout.renderer.api.gl.Texture;
import org.spout.renderer.api.gl.Texture.InternalFormat;
import org.spout.renderer.api.gl.VertexArray;
import org.spout.renderer.api.util.Rectangle;

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
        return new SoftwareTexture();
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
