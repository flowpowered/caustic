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

import org.spout.renderer.api.gl.Context;
import org.spout.renderer.api.gl.FrameBuffer;
import org.spout.renderer.api.gl.GLFactory;
import org.spout.renderer.api.gl.Program;
import org.spout.renderer.api.gl.RenderBuffer;
import org.spout.renderer.api.gl.Shader;
import org.spout.renderer.api.gl.Texture;
import org.spout.renderer.api.gl.VertexArray;

/**
 * An OpenGL 2.1 implementation of {@link GLFactory}.
 *
 * @see GLFactory
 */
public class GL21GLFactory implements GLFactory {
    private GL21GLFactory() {
    }

    @Override
    public FrameBuffer createFrameBuffer() {
        return new GL21FrameBuffer();
    }

    @Override
    public Program createProgram() {
        return new GL21Program();
    }

    @Override
    public RenderBuffer createRenderBuffer() {
        return new GL21RenderBuffer();
    }

    @Override
    public Context createContext() {
        return new GL21Context();
    }

    @Override
    public Shader createShader() {
        return new GL21Shader();
    }

    @Override
    public Texture createTexture() {
        return new GL21Texture();
    }

    @Override
    public VertexArray createVertexArray() {
        return new GL21VertexArray();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL20;
    }
}
