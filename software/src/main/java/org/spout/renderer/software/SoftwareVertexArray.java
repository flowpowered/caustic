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

import org.spout.renderer.api.data.VertexAttribute;
import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.api.data.VertexAttribute.UploadMode;
import org.spout.renderer.api.data.VertexData;
import org.spout.renderer.api.gl.Context.Capability;
import org.spout.renderer.api.gl.Shader.ShaderType;
import org.spout.renderer.api.gl.VertexArray;
import org.spout.renderer.api.util.Rectangle;

/**
 *
 */
public class SoftwareVertexArray extends VertexArray {
    private static final DataType INDICES_TYPE = DataType.INT;
    private static final DataFormat[] FRAGMENT_OUTPUT = {new DataFormat(DataType.FLOAT, 4)};
    private final SoftwareRenderer renderer;
    private ByteBuffer[] attributeBuffers;
    private DataFormat[] attributeFormats;
    private ByteBuffer indicesBuffer;
    private DrawingMode mode = DrawingMode.TRIANGLES;
    private int offset = 0, count = -1, totalCount = 0;

    public SoftwareVertexArray(SoftwareRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void setData(VertexData vertexData) {
        checkCreated();
        // If the new count is greater than or 50% smaller than the old one, we'll reallocate the memory
        // In the first case because we need more space, in the other to save space
        indicesBuffer = SoftwareUtil.set(indicesBuffer, vertexData.getIndicesBuffer(), 0.5f);
        // Update the total indices count
        totalCount = vertexData.getIndicesCount();
        // Ensure the count fits under the total one
        count = count <= 0 ? totalCount : Math.min(count, totalCount);
        // Ensure that the indices offset and count fits inside the valid part of the buffer
        offset = Math.min(offset, count - 1);
        count -= offset;
        // Create a new array of attribute buffers of the correct size
        final int attributeCount = vertexData.getAttributeCount();
        final ByteBuffer[] newAttributeBuffers = new ByteBuffer[attributeCount];
        // Copy all the old buffer that will fit in the new array so we can reuse them
        if (attributeBuffers != null) {
            System.arraycopy(attributeBuffers, 0, newAttributeBuffers, 0, Math.min(attributeBuffers.length, newAttributeBuffers.length));
        }
        // Update the arrays
        attributeBuffers = newAttributeBuffers;
        attributeFormats = new DataFormat[attributeCount];
        // Set the new vertex data
        for (int i = 0; i < attributeCount; i++) {
            final VertexAttribute attribute = vertexData.getAttribute(i);
            final ByteBuffer attributeData = attribute.getData();
            // If the new count is greater than or 50% smaller than the old one, we'll reallocate the memory
            // Set the data, converting it to float if necessary
            final DataType type = attribute.getType();
            final UploadMode uploadMode = attribute.getUploadMode();
            switch (uploadMode) {
                case KEEP_INT:
                    attributeBuffers[i] = SoftwareUtil.set(attributeBuffers[i], attributeData, 0.5f);
                    break;
                default:
                    attributeBuffers[i] = SoftwareUtil.setAsFloat(attributeBuffers[i], attributeData, type, 0.5f, uploadMode.normalize());
            }
            // Save the attribute format
            attributeFormats[i] = new DataFormat(uploadMode.toFloat() ? DataType.FLOAT : type, attribute.getSize());
        }
    }

    @Override
    public void setDrawingMode(DrawingMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Drawing mode cannot be null");
        }
        this.mode = mode;
    }

    @Override
    public void setIndicesOffset(int offset) {
        offset = Math.min(offset, totalCount - 1);
        count = Math.min(count, totalCount - offset);
    }

    @Override
    public void setIndicesCount(int count) {
        this.count = count <= 0 ? totalCount : count;
        this.count = Math.min(count, totalCount - offset);
    }

    @Override
    public void draw() {
        switch (mode) {
            case POINTS:
                drawPoints();
                break;
        }
    }

    private void drawPoints() {
        // Get some renderer properties
        final Rectangle viewPort = renderer.getViewPort();
        final boolean clampDepth = renderer.isEnabled(Capability.DEPTH_CLAMP);
        // Get the shader program
        final SoftwareProgram program = renderer.getProgram();

        final ShaderImplementation vertexShader = program.getShader(ShaderType.VERTEX).getImplementation();
        final DataFormat[] vertexOutputFormat = vertexShader.getOutputFormat();
        final ShaderBuffer vertexIn = new ShaderBuffer(attributeFormats);
        final ShaderBuffer vertexOut = new ShaderBuffer(vertexOutputFormat);

        final ShaderImplementation fragmentShader = program.getShader(ShaderType.FRAGMENT).getImplementation();
        final ShaderBuffer fragmentIn = new ShaderBuffer(vertexOutputFormat);
        final ShaderBuffer fragmentOut = new ShaderBuffer(FRAGMENT_OUTPUT);

        for (int i = 0; i < count; i++) {
            vertexIn.clear();
            for (int ii = 0; ii < attributeBuffers.length; ii++) {
                final ByteBuffer buffer = attributeBuffers[ii];
                final DataFormat format = attributeFormats[ii];
                final DataType type = format.getType();
                final int size = format.getCount();
                for (int iii = 0; iii < size; iii++) {
                    final int x = readComponent(buffer, type, size, i, iii);
                    vertexIn.writeRaw(x);
                }
            }
            vertexIn.flip();

            vertexOut.clear();
            vertexShader.main(vertexIn, vertexOut);
            vertexOut.flip();

            float x = Float.intBitsToFloat(vertexOut.readRaw());
            float y = Float.intBitsToFloat(vertexOut.readRaw());
            float z = Float.intBitsToFloat(vertexOut.readRaw());
            float w = Float.intBitsToFloat(vertexOut.readRaw());
            final float wInverse = 1 / w;
            x *= wInverse;
            y *= wInverse;
            z *= wInverse;
            if (x < -1 || x > 1 || y < -1 || y > 1 || !clampDepth && (z < -1 || z > 1)) {
                continue;
            }
            x = (x + 1) / 2 * viewPort.getWidth() + viewPort.getX();
            y = (y + 1) / 2 * viewPort.getHeight() + viewPort.getY();
            z = (z + 1) / 2;
            w = wInverse;

            fragmentIn.clear();
            fragmentIn.writeRaw(Float.floatToIntBits(x));
            fragmentIn.writeRaw(Float.floatToIntBits(y));
            fragmentIn.writeRaw(Float.floatToIntBits(z));
            fragmentIn.writeRaw(Float.floatToIntBits(w));
            for (int ii = 1; ii < vertexOutputFormat.length; ii++) {
                final DataFormat format = vertexOutputFormat[ii];
                for (int iii = 0; iii < format.getCount(); iii++) {
                    fragmentIn.writeRaw(vertexOut.readRaw());
                }
            }
            fragmentIn.flip();

            fragmentOut.clear();
            fragmentShader.main(fragmentIn, fragmentOut);
            fragmentOut.flip();

            final float r = Float.intBitsToFloat(fragmentOut.readRaw());
            final float g = Float.intBitsToFloat(fragmentOut.readRaw());
            final float b = Float.intBitsToFloat(fragmentOut.readRaw());
            final float a = Float.intBitsToFloat(fragmentOut.readRaw());
            renderer.writePixel((int) x, (int) y, SoftwareUtil.denormalizeToShort(z), SoftwareUtil.pack(r, g, b, a));
        }
    }

    private int readComponent(ByteBuffer buffer, DataType type, int attributeSize, int index, int offset) {
        return SoftwareUtil.read(buffer, type, SoftwareUtil.read(indicesBuffer, INDICES_TYPE, index + this.offset) * attributeSize + offset);
    }

    @Override
    public GLVersion getGLVersion() {
        return null;
    }
}
