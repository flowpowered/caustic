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
