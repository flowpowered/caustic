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
package com.flowpowered.caustic.lwjgl.gl30;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.ContextAttribs;

import com.flowpowered.caustic.api.gl.FrameBuffer;
import com.flowpowered.caustic.api.gl.Program;
import com.flowpowered.caustic.api.gl.RenderBuffer;
import com.flowpowered.caustic.api.gl.Shader;
import com.flowpowered.caustic.api.gl.Texture;
import com.flowpowered.caustic.api.gl.VertexArray;
import com.flowpowered.caustic.lwjgl.gl20.GL20Context;
import com.flowpowered.caustic.lwjgl.gl20.GL20Program;
import com.flowpowered.caustic.lwjgl.gl20.GL20Shader;
import com.flowpowered.caustic.lwjgl.gl20.GL20VertexArray;

/**
 * An OpenGL 3.0 implementation of {@link com.flowpowered.caustic.api.gl.Context}.
 *
 * @see com.flowpowered.caustic.api.gl.Context
 */
public class GL30Context extends GL20Context {
    @Override
    protected ContextAttribs createContextAttributes() {
        return new ContextAttribs(3, 0);
    }

    @Override
    public FrameBuffer newFrameBuffer() {
        return new GL30FrameBuffer();
    }

    @Override
    public Program newProgram() {
        return new GL20Program();
    }

    @Override
    public RenderBuffer newRenderBuffer() {
        return new GL30RenderBuffer();
    }

    @Override
    public Shader newShader() {
        return new GL20Shader();
    }

    @Override
    public Texture newTexture() {
        return new GL30Texture();
    }

    @Override
    public VertexArray newVertexArray() {
        if (LWJGLUtil.getPlatform() == LWJGLUtil.PLATFORM_MACOSX) {
            return new GL20VertexArray();
        }
        return new GL30VertexArray();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL30;
    }
}
