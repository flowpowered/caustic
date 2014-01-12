/*
 * This file is part of Caustic Android.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic Android is licensed under the Spout License Version 1.
 *
 * Caustic Android is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic Android is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.android.gles20;

import java.nio.ByteBuffer;

import android.opengl.GLES20;

import org.spout.renderer.android.AndroidUtil;
import org.spout.renderer.api.gl.Texture;

/**
 * An OpenGLES 2.0 implementation of {@link org.spout.renderer.api.gl.Texture}.
 *
 * @see org.spout.renderer.api.gl.Texture
 */
public class GLES20Texture extends Texture {
    protected GLES20Texture() {
    }

    @Override
    public void create() {
        // Get the context capabilities for the graphics hardware
        //final ContextCapabilities contextCaps = GLContext.getCapabilities();
        //if (!contextCaps.GL_ARB_texture_non_power_of_two && (!GenericMath.isPowerOfTwo(width) || !GenericMath.isPowerOfTwo(height))) {
        //	TODO: Resize images. Also, this only really matters for mipmaps
        //}
        // Generate and bind the texture in the unit
        int[] params = new int[1];
        GLES20.glGenTextures(1, params, 0);
        id = params[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
        // Upload the texture to the GPU
        uploadTexture(imageData, width, height);
        // Set the vertical and horizontal texture wraps (in the texture parameters)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapT.getGLConstant());
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapS.getGLConstant());
        // Set the min and max texture filters (in the texture parameters)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minFilter.getGLConstant());
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magFilter.getGLConstant());
        // TODO: Set the anisotropic filtering value, if any
        /*
        if (anisotropicFiltering > 0) {
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAX_ANISOTROPY, anisotropicFiltering);
		}
		*/
        // TODO: Set the compare mode, if any
		/*if (compareMode != null) {
			// Note: GL14.GL_COMPARE_R_TO_TEXTURE and GL30.GL_COMPARE_REF_TO_TEXTURE are the same, just a different name
			// No need for a different call in the GL30 implementation
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, compareMode.getGLConstant());
		}*/
        // TODO: Set the border color, if any
        /*if (borderColor != null) {
            GL11.glTexParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, (FloatBuffer) CausticUtil.createFloatBuffer(4).put(borderColor.toArray()).flip());
        }*/
        // Unbind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // Update the state
        super.create();
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    /**
     * Uploads the texture to the graphics card. This method has been separated from the create method for GL30 integrated mipmap support.
     *
     * @param buffer The buffer containing the image data
     * @param width The width of the image
     * @param height The height of the image
     */
    protected void uploadTexture(ByteBuffer buffer, int width, int height) {
        if (minFilter.needsMipMaps() && buffer != null) {
            // TODO: Fix mip-mapping
            // Build mipmaps if using mip mapped filters
            // GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D, internalFormat != null ? internalFormat.getGLConstant() : format.getGLConstant(), width, height, format.getGLConstant(), type.getGLConstant(), buffer);
        } else {
            // Else just make it a normal texture
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, internalFormat != null ? internalFormat.getGLConstant() : format.getGLConstant(), width, height, 0, format.getGLConstant(), type.getGLConstant(), buffer);
        }
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Unbind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // Delete the texture
        GLES20.glDeleteTextures(1, new int[]{id}, 0);
        // Reset the data
        super.destroy();
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void bind(int unit) {
        checkCreated();
        if (unit != -1) {
            // Activate the texture unit
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
        }
        // Bind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public void unbind() {
        checkCreated();
        // Unbind the texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        // Check for errors
        AndroidUtil.checkForGLESError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GLES20;
    }
}
