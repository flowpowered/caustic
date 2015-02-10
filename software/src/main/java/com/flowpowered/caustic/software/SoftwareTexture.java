/*
 * This file is part of Caustic Software, licensed under the MIT License (MIT).
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
package com.flowpowered.caustic.software;

import java.nio.ByteBuffer;

import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.gl.Texture;
import com.flowpowered.caustic.api.util.CausticUtil;

/**
 *
 */
public class SoftwareTexture extends Texture {
    private final SoftwareRenderer renderer;
    private int unit = -1;
    private ByteBuffer data;
    private int width, height;
    private InternalFormat format;

    SoftwareTexture(SoftwareRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void bind(int unit) {
        renderer.bindTexture(unit, this);
        this.unit = unit;
    }

    @Override
    public void unbind() {
        if (unit >= 0) {
            renderer.unbindTexture(unit);
            unit = -1;
        }
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

    Vector4f sample(float x, float y) {
        x *= width - 1;
        y *= height - 1;
        final DataType type = format.getComponentType();
        final int i = ((int) x + (int) y * width) * format.getComponentCount();
        final float r, g, b, a;
        if (format.hasRed()) {
            r = SoftwareUtil.readAsFloat(data, type, i);
        } else {
            r = 0;
        }
        if (format.hasGreen()) {
            g = SoftwareUtil.readAsFloat(data, type, i + 1);
        } else {
            g = 0;
        }
        if (format.hasBlue()) {
            b = SoftwareUtil.readAsFloat(data, type, i + 2);
        } else {
            b = 0;
        }
        if (format.hasAlpha()) {
            a = SoftwareUtil.readAsFloat(data, type, i + 3);
        } else {
            a = 1;
        }
        return new Vector4f(r, g, b, a);
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.SOFTWARE;
    }
}
