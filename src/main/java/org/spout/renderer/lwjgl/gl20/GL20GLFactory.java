/*
 * This file is part of Caustic.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic is licensed under the Spout License Version 1.
 *
 * Caustic is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.lwjgl.gl20;

import org.spout.renderer.GLImplementation;
import org.spout.renderer.gl.Context;
import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.gl.GLFactory;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.RenderBuffer;
import org.spout.renderer.gl.Shader;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.gl.VertexArray;

/**
 * An OpenGL 2.0 implementation of {@link GLFactory}.
 *
 * @see GLFactory
 */
public class GL20GLFactory implements GLFactory {
    static {
        GLImplementation.register(GLVersion.GL20, new GL20GLFactory());
    }

    private GL20GLFactory() {
    }

    @Override
    public FrameBuffer createFrameBuffer() {
        return new GL20FrameBuffer();
    }

    @Override
    public Program createProgram() {
        return new GL20Program();
    }

    @Override
    public RenderBuffer createRenderBuffer() {
        return new GL20RenderBuffer();
    }

    @Override
    public Context createContext() {
        return new GL20Context();
    }

    @Override
    public Shader createShader() {
        return new GL20Shader();
    }

    @Override
    public Texture createTexture() {
        return new GL20Texture();
    }

    @Override
    public VertexArray createVertexArray() {
        return new GL20VertexArray();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL20;
    }
}
