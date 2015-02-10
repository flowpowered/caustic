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

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.lwjgl.LWJGLUtil;
import com.flowpowered.caustic.lwjgl.gl20.GL20Texture;

/**
 * An OpenGL 3.0 implementation of {@link com.flowpowered.caustic.api.gl.Texture}.
 *
 * @see com.flowpowered.caustic.api.gl.Texture
 */
public class GL30Texture extends GL20Texture {
    @Override
    public void setImageData(ByteBuffer imageData, int width, int height) {
        checkCreated();
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be greater than zero");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than zero");
        }
        this.width = width;
        this.height = height;
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Use byte alignment
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        // Upload the texture
        final boolean hasInternalFormat = internalFormat != null;
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, hasInternalFormat ? internalFormat.getGLConstant() : format.getGLConstant(), width, height, 0, format.getGLConstant(),
                hasInternalFormat ? internalFormat.getComponentType().getGLConstant() : DataType.UNSIGNED_BYTE.getGLConstant(), imageData);
        // Generate mipmaps if necessary
        if (minFilter.needsMipMaps() && imageData != null) {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        }
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL30;
    }
}
