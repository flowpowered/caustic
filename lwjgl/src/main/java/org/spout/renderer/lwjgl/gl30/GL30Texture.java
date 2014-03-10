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

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.lwjgl.LWJGLUtil;
import org.spout.renderer.lwjgl.gl20.GL20Texture;

/**
 * An OpenGL 3.0 implementation of {@link org.spout.renderer.api.gl.Texture}.
 *
 * @see org.spout.renderer.api.gl.Texture
 */
public class GL30Texture extends GL20Texture {
    protected GL30Texture() {
    }

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
