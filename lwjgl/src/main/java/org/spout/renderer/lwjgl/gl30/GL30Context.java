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
package org.spout.renderer.lwjgl.gl30;

import org.lwjgl.opengl.ContextAttribs;

import org.spout.renderer.api.gl.FrameBuffer;
import org.spout.renderer.api.gl.Program;
import org.spout.renderer.api.gl.RenderBuffer;
import org.spout.renderer.api.gl.Shader;
import org.spout.renderer.api.gl.Texture;
import org.spout.renderer.api.gl.VertexArray;
import org.spout.renderer.lwjgl.gl20.GL20Context;

/**
 * An OpenGL 3.0 implementation of {@link org.spout.renderer.api.gl.Context}.
 *
 * @see org.spout.renderer.api.gl.Context
 */
public class GL30Context extends GL20Context {
    protected GL30Context() {
    }

    @Override
    protected ContextAttribs createContextAttributes() {
        return new ContextAttribs(3, 0).withForwardCompatible(true);
    }

    @Override
    public FrameBuffer newFrameBuffer() {
        return new GL30FrameBuffer();
    }

    @Override
    public Program newProgram() {
        return new GL30Program();
    }

    @Override
    public RenderBuffer newRenderBuffer() {
        return new GL30RenderBuffer();
    }

    @Override
    public Shader newShader() {
        return new GL30Shader();
    }

    @Override
    public Texture newTexture() {
        return new GL30Texture();
    }

    @Override
    public VertexArray newVertexArray() {
        return new GL30VertexArray();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL30;
    }
}
