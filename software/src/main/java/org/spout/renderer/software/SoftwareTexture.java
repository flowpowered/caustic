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

import com.flowpowered.math.vector.Vector4f;

import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.api.gl.Texture;
import org.spout.renderer.api.util.CausticUtil;

/**
 *
 */
public class SoftwareTexture extends Texture {
    private ByteBuffer data;
    private int width, height;
    private InternalFormat format;

    @Override
    public void bind(int unit) {

    }

    @Override
    public void unbind() {

    }

    @Override
    public void setFormat(Format format, InternalFormat internalFormat) {
        if (internalFormat != null) {
            this.format = internalFormat;
        } else {
            switch (format) {
                case RED:
                    this.format = InternalFormat.R8;
                    break;
                case RG:
                    this.format = InternalFormat.RG8;
                    break;
                case RGB:
                    this.format = InternalFormat.RGB8;
                    break;
                case RGBA:
                    this.format = InternalFormat.RGBA8;
                    break;
                case DEPTH:
                    this.format = InternalFormat.DEPTH_COMPONENT16;
                    break;
                case DEPTH_STENCIL:
                    this.format = InternalFormat.DEPTH_COMPONENT32;
                    break;
            }
        }
    }

    @Override
    public Format getFormat() {
        return format.getFormat();
    }

    @Override
    public InternalFormat getInternalFormat() {
        return format;
    }

    @Override
    public void setAnisotropicFiltering(float value) {

    }

    @Override
    public void setWraps(WrapMode horizontalWrap, WrapMode verticalWrap) {

    }

    @Override
    public void setFilters(FilterMode minFilter, FilterMode magFilter) {

    }

    @Override
    public void setCompareMode(CompareMode compareMode) {

    }

    @Override
    public void setBorderColor(Vector4f borderColor) {

    }

    @Override
    public void setImageData(ByteBuffer imageData, int width, int height) {
        checkCreated();
        this.width = width;
        this.height = height;
        data = SoftwareUtil.set(data, imageData, 0.5f);
    }

    @Override
    public ByteBuffer getImageData(InternalFormat format) {
        checkCreated();
        if (format == null) {
            format = this.format;
        }
        final ByteBuffer imageData = CausticUtil.createByteBuffer(width * height * format.getBytes());
        final DataType sourceType = this.format.getComponentType();
        final DataType destinationType = format.getComponentType();
        data.rewind();
        while (data.remaining() > 0) {
            if (this.format.hasRed()) {
                if (format.hasRed()) {
                    SoftwareUtil.copy(data, sourceType, imageData, destinationType);
                } else {
                    SoftwareUtil.advance(data, sourceType);
                }
            } else {
                if (format.hasRed()) {
                    SoftwareUtil.write(imageData, destinationType, 0);
                }
            }
            if (this.format.hasGreen()) {
                if (format.hasGreen()) {
                    SoftwareUtil.copy(data, sourceType, imageData, destinationType);
                } else {
                    SoftwareUtil.advance(data, sourceType);
                }
            } else {
                if (format.hasGreen()) {
                    SoftwareUtil.write(imageData, destinationType, 0);
                }
            }
            if (this.format.hasBlue()) {
                if (format.hasBlue()) {
                    SoftwareUtil.copy(data, sourceType, imageData, destinationType);
                } else {
                    SoftwareUtil.advance(data, sourceType);
                }
            } else {
                if (format.hasBlue()) {
                    SoftwareUtil.write(imageData, destinationType, 0);
                }
            }
            if (this.format.hasAlpha()) {
                if (format.hasAlpha()) {
                    SoftwareUtil.copy(data, sourceType, imageData, destinationType);
                } else {
                    SoftwareUtil.advance(data, sourceType);
                }
            } else {
                if (format.hasAlpha()) {
                    SoftwareUtil.write(imageData, destinationType, 0xFFFFFFFF);
                }
            }
        }
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
    public GLVersion getGLVersion() {
        return GLVersion.SOFTWARE;
    }
}
