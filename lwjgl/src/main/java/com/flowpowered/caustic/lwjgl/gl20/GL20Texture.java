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
package com.flowpowered.caustic.lwjgl.gl20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.gl.Texture;
import com.flowpowered.caustic.api.util.CausticUtil;
import com.flowpowered.caustic.lwjgl.LWJGLUtil;
import com.flowpowered.math.vector.Vector4f;

import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;

/**
 * An OpenGL 2.0 implementation of {@link Texture}.
 *
 * @see Texture
 */
public class GL20Texture extends Texture {
    // The format
    protected Format format = Format.RGB;
    protected InternalFormat internalFormat = null;
    // The min filter, to check if we need mip maps
    protected FilterMode minFilter = FilterMode.NEAREST_MIPMAP_LINEAR;
    // Texture image dimensions
    protected int width = 1;
    protected int height = 1;

    @Override
    public void create() {
        checkNotCreated();
        // Generate the texture
        id = GL11.glGenTextures();
        // Update the state
        super.create();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Delete the texture
        GL11.glDeleteTextures(id);
        // Reset the data
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setFormat(Format format, InternalFormat internalFormat) {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        this.format = format;
        this.internalFormat = internalFormat;
    }

    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public InternalFormat getInternalFormat() {
        return internalFormat;
    }

    @Override
    public void setAnisotropicFiltering(float value) {
        checkCreated();
        if (value <= 0) {
            throw new IllegalArgumentException("Anisotropic filtering value must be greater than zero");
        }
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Set the anisotropic filtering value
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, value);
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setWraps(WrapMode horizontalWrap, WrapMode verticalWrap) {
        checkCreated();
        if (horizontalWrap == null) {
            throw new IllegalArgumentException("Horizontal wrap cannot be null");
        }
        if (verticalWrap == null) {
            throw new IllegalArgumentException("Vertical wrap cannot be null");
        }
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Set the vertical and horizontal texture wraps
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, horizontalWrap.getGLConstant());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, verticalWrap.getGLConstant());
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setFilters(FilterMode minFilter, FilterMode magFilter) {
        checkCreated();
        if (minFilter == null) {
            throw new IllegalArgumentException("Min filter cannot be null");
        }
        if (magFilter == null) {
            throw new IllegalArgumentException("Mag filter cannot be null");
        }
        if (magFilter.needsMipMaps()) {
            throw new IllegalArgumentException("Mag filter cannot require mipmaps");
        }
        this.minFilter = minFilter;
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Set the min and max texture filters
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter.getGLConstant());
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter.getGLConstant());
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setCompareMode(CompareMode compareMode) {
        checkCreated();
        if (compareMode == null) {
            throw new IllegalArgumentException("Compare mode cannot be null");
        }
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Note: GL14.GL_COMPARE_R_TO_TEXTURE and GL30.GL_COMPARE_REF_TO_TEXTURE are the same, just a different name
        // No need for a different call in the GL30 implementation
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
        // Set the compare mode
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, compareMode.getGLConstant());
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void setBorderColor(Vector4f borderColor) {
        checkCreated();
        if (borderColor == null) {
            throw new IllegalArgumentException("Border color cannot be null");
        }
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Set the border color
        GL11.glTexParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_BORDER_COLOR, (FloatBuffer) CausticUtil.createFloatBuffer(4).put(borderColor.toArray()).flip());
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
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
        // Back up the old values
        int oldWidth = this.width;
        int oldHeight = this.height;
        // Update the texture width and height
        this.width = width;
        this.height = height;
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Upload the texture to the GPU
        final boolean hasInternalFormat = internalFormat != null;
        if (minFilter.needsMipMaps() && imageData != null) {
            // Build mipmaps if using mip mapped filters
            GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, hasInternalFormat ? internalFormat.getGLConstant() : format.getGLConstant(), width, height, format.getGLConstant(),
                    hasInternalFormat ? internalFormat.getComponentType().getGLConstant() : DataType.UNSIGNED_BYTE.getGLConstant(), imageData);
        } else {
            // Else just make it a normal texture, use byte alignment
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
            // Check if we can only upload without reallocating
            if (imageData != null && width == oldWidth && height == oldHeight) {
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, width, height, format.getGLConstant(),
                        hasInternalFormat ? internalFormat.getComponentType().getGLConstant() : DataType.UNSIGNED_BYTE.getGLConstant(), imageData);
            } else {
                // Reallocate and upload the image
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, hasInternalFormat ? internalFormat.getGLConstant() : format.getGLConstant(), width, height, 0, format.getGLConstant(),
                        hasInternalFormat ? internalFormat.getComponentType().getGLConstant() : DataType.UNSIGNED_BYTE.getGLConstant(), imageData);
            }
        }
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public ByteBuffer getImageData(InternalFormat format) {
        checkCreated();
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Create the image buffer
        final boolean formatNotNull = format != null;
        final ByteBuffer imageData = CausticUtil.createByteBuffer(width * height * (formatNotNull ? format.getBytes() : this.format.getComponentCount() * DataType.UNSIGNED_BYTE.getByteSize()));
        // Use byte alignment
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        // Get the image data
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, formatNotNull ? format.getFormat().getGLConstant() : this.format.getGLConstant(),
                formatNotNull ? format.getComponentType().getGLConstant() : DataType.UNSIGNED_BYTE.getGLConstant(), imageData);
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
        return imageData;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void bind(int unit) {
        checkCreated();
        if (unit != -1) {
            // Activate the texture unit
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit);
        }
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void unbind() {
        checkCreated();
        // Unbind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL20;
    }
}
