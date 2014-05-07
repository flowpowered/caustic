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

import com.flowpowered.math.GenericMath;

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
            case LINES:
                drawLines();
                break;
        }
    }

    private void drawPoints() {
        // Get some renderer properties
        final Rectangle viewPort = renderer.getViewPort();
        final boolean clampDepth = renderer.isEnabled(Capability.DEPTH_CLAMP);
        // Get the shader program
        final SoftwareProgram program = renderer.getProgram();
        // Get the vertex shader implementation, and create appropriate in and out buffers
        final ShaderImplementation vertexShader = program.getShader(ShaderType.VERTEX).getImplementation();
        final DataFormat[] vertexOutputFormat = vertexShader.getOutputFormat();
        final ShaderBuffer vertexIn = new ShaderBuffer(attributeFormats);
        final ShaderBuffer vertexOut = new ShaderBuffer(vertexOutputFormat);
        // Get the fragment shader implementation, and create appropriate in and out buffers
        final ShaderImplementation fragmentShader = program.getShader(ShaderType.FRAGMENT).getImplementation();
        final ShaderBuffer fragmentIn = new ShaderBuffer(vertexOutputFormat);
        final ShaderBuffer fragmentOut = new ShaderBuffer(FRAGMENT_OUTPUT);
        // For all indices that need to be drawn
        for (int i = 0; i < count; i++) {
            // Compute the point vertex
            readVertex(vertexShader, vertexIn, vertexOut, i);
            // Read the first 4 floats, which is the vertex position
            float x = Float.intBitsToFloat(vertexOut.readRaw());
            float y = Float.intBitsToFloat(vertexOut.readRaw());
            float z = Float.intBitsToFloat(vertexOut.readRaw());
            float w = Float.intBitsToFloat(vertexOut.readRaw());
            // Perform clipping, ignoring z clipping when depth clamping is active
            if (w == 0 || x < -w || x > w || y < -w || y > w || !clampDepth && (z < -w || z > w)) {
                continue;
            }
            // Compute the NDC coordinates
            final float wInverse = 1 / w;
            x *= wInverse;
            y *= wInverse;
            z *= wInverse;
            // Normalize and convert to window coordinates
            x = (x + 1) / 2 * (viewPort.getWidth() - 1) + viewPort.getX();
            y = (y + 1) / 2 * (viewPort.getHeight() - 1) + viewPort.getY();
            z = SoftwareUtil.clamp((z + 1) / 2, 0, 1);
            // Store 1/w in w to so that the fragment position vector is the same as in OpenGL
            w = wInverse;
            // Clear the fragment in, write the fragment position
            // followed by the output of the vertex shader and flip the buffer
            fragmentIn.clear();
            fragmentIn.writeRaw(Float.floatToIntBits(x));
            fragmentIn.writeRaw(Float.floatToIntBits(y));
            fragmentIn.writeRaw(Float.floatToIntBits(z));
            fragmentIn.writeRaw(Float.floatToIntBits(w));
            fragmentIn.writeRaw(vertexOut);
            fragmentIn.flip();
            // Shade and write the fragment
            writeFragment(fragmentShader, fragmentIn, fragmentOut, (int) x, (int) y, z);
        }
    }

    private void drawLines() {
        // Get some renderer properties
        final Rectangle viewPort = renderer.getViewPort();
        final boolean clampDepth = renderer.isEnabled(Capability.DEPTH_CLAMP);
        // Get the shader program
        final SoftwareProgram program = renderer.getProgram();
        // Get the vertex shader implementation, and create appropriate in and out buffers
        final ShaderImplementation vertexShader = program.getShader(ShaderType.VERTEX).getImplementation();
        final DataFormat[] vertexOutputFormat = vertexShader.getOutputFormat();
        final ShaderBuffer vertexIn = new ShaderBuffer(attributeFormats);
        final ShaderBuffer vertexOut1 = new ShaderBuffer(vertexOutputFormat);
        final ShaderBuffer vertexOut2 = new ShaderBuffer(vertexOutputFormat);
        // Get the fragment shader implementation, and create appropriate in and out buffers
        final ShaderImplementation fragmentShader = program.getShader(ShaderType.FRAGMENT).getImplementation();
        final ShaderBuffer fragmentIn = new ShaderBuffer(vertexOutputFormat);
        final ShaderBuffer fragmentOut = new ShaderBuffer(FRAGMENT_OUTPUT);
        // For all indices that need to be drawn
        for (int i = 0; i < count; i += 2) {
            // Compute the first point
            readVertex(vertexShader, vertexIn, vertexOut1, i);
            // Read the first 4 floats, which is the vertex position
            float x1 = Float.intBitsToFloat(vertexOut1.readRaw());
            float y1 = Float.intBitsToFloat(vertexOut1.readRaw());
            float z1 = Float.intBitsToFloat(vertexOut1.readRaw());
            float w1 = Float.intBitsToFloat(vertexOut1.readRaw());
            // Compute second the point vertex
            readVertex(vertexShader, vertexIn, vertexOut2, i + 1);
            // Read the first 4 floats, which is the vertex position
            float x2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float y2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float z2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float w2 = Float.intBitsToFloat(vertexOut2.readRaw());

            float percent1 = 1, percent2 = 1;

            // Perform clipping on the first point, ignoring z clipping when depth clamping is active
            if (x1 < -w1) {
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w2 - w1;
                float t = (-w2 - x2) / (dx - dw);
                y1 = t * dy + y2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                x1 = -w1;
                percent1 *= t;
            }
            if (x1 > w1) {
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                float t = (w2 - x2) / (dx - dw);
                y1 = t * dy + y2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                x1 = w1;
                percent1 *= t;
            }
            if (y1 < -w1) {
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w2 - w1;
                float t = (-w2 - y2) / (dy - dw);
                x1 = t * dx + x2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                y1 = -w1;
                percent1 *= t;
            }
            if (y1 > w1) {
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                float t = (w2 - y2) / (dy - dw);
                x1 = t * dx + x2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                y1 = w1;
                percent1 *= t;
            }
            if (!clampDepth && z1 < -w1) {
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w2 - w1;
                float t = (-w2 - z2) / (dz - dw);
                x1 = t * dx + x2;
                y1 = t * dy + y2;
                w1 = t * dw + w2;
                z1 = -w1;
                percent1 *= t;
            }
            if (!clampDepth && z1 > w1) {
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                float t = (w2 - z2) / (dz - dw);
                x1 = t * dx + x2;
                y1 = t * dy + y2;
                w1 = t * dw + w2;
                z1 = w1;
                percent1 *= t;
            }

            // Perform clipping on the second point, ignoring z clipping when depth clamping is active
            if (x2 < -w2) {
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w1 - w2;
                float t = (-w1 - x1) / (dx - dw);
                y2 = t * dy + y1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                x2 = -w2;
                percent2 *= t;
            }
            if (x2 > w2) {
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                float t = (w1 - x1) / (dx - dw);
                y2 = t * dy + y1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                x2 = w2;
                percent2 *= t;
            }
            if (y2 < -w2) {
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w1 - w2;
                float t = (-w1 - y1) / (dy - dw);
                x2 = t * dx + x1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                y2 = -w2;
                percent2 *= t;
            }
            if (y2 > w2) {
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                float t = (w1 - y1) / (dy - dw);
                x2 = t * dx + x1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                y2 = w2;
                percent2 *= t;
            }
            if (!clampDepth && z2 < -w2) {
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w1 - w2;
                float t = (-w1 - z1) / (dz - dw);
                x2 = t * dx + x1;
                y2 = t * dy + y1;
                w2 = t * dw + w1;
                z2 = -w2;
                percent2 *= t;
            }
            if (!clampDepth && z2 > w2) {
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                float t = (w1 - z1) / (dz - dw);
                x2 = t * dx + x1;
                y2 = t * dy + y1;
                w2 = t * dw + w1;
                z2 = w2;
                percent2 *= t;
            }

            // A line outside will degenerate to a point
            if (w1 == w2) {
                continue;
            }

            // The end percent is the percentage that was cut off times the percentage left over from clipping
            // the first point; inverted to get the remaining amount instead of the cut amount
            percent2 = 1 - percent1 * (1 - percent2);
            // The start percent is what's remaining from clipping the first point
            percent1 = 1 - percent1;

            // Compute the NDC coordinates of the first point
            final float wInverse1 = 1 / w1;
            x1 *= wInverse1;
            y1 *= wInverse1;
            z1 *= wInverse1;
            // Normalize and convert to window coordinates
            x1 = (x1 + 1) / 2 * (viewPort.getWidth() - 1) + viewPort.getX();
            y1 = (y1 + 1) / 2 * (viewPort.getHeight() - 1) + viewPort.getY();
            z1 = SoftwareUtil.clamp((z1 + 1) / 2, 0, 1);
            // Store 1/w in w to so that the fragment position vector is the same as in OpenGL
            w1 = wInverse1;

            // Compute the NDC coordinates of the second point
            final float wInverse2 = 1 / w2;
            x2 *= wInverse2;
            y2 *= wInverse2;
            z2 *= wInverse2;
            // Normalize and convert to window coordinates
            x2 = (x2 + 1) / 2 * (viewPort.getWidth() - 1) + viewPort.getX();
            y2 = (y2 + 1) / 2 * (viewPort.getHeight() - 1) + viewPort.getY();
            z2 = SoftwareUtil.clamp((z2 + 1) / 2, 0, 1);
            // Store 1/w in w to so that the fragment position vector is the same as in OpenGL
            w2 = wInverse2;

            final float xDiff = x2 - x1;
            final float yDiff = y2 - y1;
            // If the two ends of the line are at the same position, use the closest
            if (xDiff == 0 && yDiff == 0) {
                final float x, y, z, w;
                final ShaderBuffer vertexOut;
                if (z1 < z2) {
                    x = x1;
                    y = y1;
                    z = z1;
                    w = w1;
                    vertexOut = vertexOut1;
                } else {
                    x = x2;
                    y = y2;
                    z = z2;
                    w = w2;
                    vertexOut = vertexOut2;
                }
                // Clear the fragment in
                fragmentIn.clear();
                // Write the fragment position
                fragmentIn.writeRaw(Float.floatToIntBits(x));
                fragmentIn.writeRaw(Float.floatToIntBits(y));
                fragmentIn.writeRaw(Float.floatToIntBits(z));
                fragmentIn.writeRaw(Float.floatToIntBits(w));
                // Write the vertex buffer output
                fragmentIn.writeRaw(vertexOut);
                // Flip the buffer for reading
                fragmentIn.flip();
                // Shade and write the fragment
                writeFragment(fragmentShader, fragmentIn, fragmentOut, (int) x, (int) y, z);
            } else if (Math.abs(xDiff) > Math.abs(yDiff)) {
                final float xMin, xMax;
                if (x1 < x2) {
                    xMin = x1;
                    xMax = x2;
                } else {
                    xMin = x2;
                    xMax = x1;
                }
                // Draw line in terms of the y slope
                final float slope = yDiff / xDiff;
                for (float x = xMin; x <= xMax; x++) {
                    final float dx = x - x1;
                    final float y = y1 + dx * slope;
                    final float percent = dx / xDiff;
                    // Lerp the other position components
                    final float z = GenericMath.lerp(z1, z2, percent);
                    final float w = GenericMath.lerp(w1, w2, percent);
                    // Clear the fragment in
                    fragmentIn.clear();
                    // write the fragment position
                    fragmentIn.writeRaw(Float.floatToIntBits(x));
                    fragmentIn.writeRaw(Float.floatToIntBits(y));
                    fragmentIn.writeRaw(Float.floatToIntBits(z));
                    fragmentIn.writeRaw(Float.floatToIntBits(w));
                    // Write the vertex shader output, using lerp to compute them
                    // Start at position 4 since we already read the position data
                    vertexOut1.position(4);
                    vertexOut2.position(4);
                    SoftwareUtil.lerp(vertexOut1, vertexOut2, GenericMath.lerp(percent1, percent2, percent), 1, fragmentIn);
                    // Flip the buffer for reading
                    fragmentIn.flip();
                    // Shade and write the fragment
                    writeFragment(fragmentShader, fragmentIn, fragmentOut, (int) x, (int) y, z);
                }
            } else {
                final float yMin, yMax;
                if (y1 < y2) {
                    yMin = y1;
                    yMax = y2;
                } else {
                    yMin = y2;
                    yMax = y1;
                }
                // Draw line in terms of the x slope
                final float slope = xDiff / yDiff;
                for (float y = yMin; y <= yMax; y++) {
                    final float dy = y - y1;
                    final float x = x1 + dy * slope;
                    final float percent = dy / yDiff;
                    // Lerp the other position components
                    final float z = GenericMath.lerp(z1, z2, percent);
                    final float w = GenericMath.lerp(w1, w2, percent);
                    // Clear the fragment in
                    fragmentIn.clear();
                    // write the fragment position
                    fragmentIn.writeRaw(Float.floatToIntBits(x));
                    fragmentIn.writeRaw(Float.floatToIntBits(y));
                    fragmentIn.writeRaw(Float.floatToIntBits(z));
                    fragmentIn.writeRaw(Float.floatToIntBits(w));
                    // Write the vertex shader output, using lerp to compute them
                    // Start at position 4 since we already read the position data
                    vertexOut1.position(4);
                    vertexOut2.position(4);
                    SoftwareUtil.lerp(vertexOut1, vertexOut2, GenericMath.lerp(percent1, percent2, percent), 1, fragmentIn);
                    // Flip the buffer for reading
                    fragmentIn.flip();
                    // Shade and write the fragment
                    writeFragment(fragmentShader, fragmentIn, fragmentOut, (int) x, (int) y, z);
                }
            }
        }
    }

    private void readVertex(ShaderImplementation shader, ShaderBuffer in, ShaderBuffer out, int index) {
        // Clear the vertex in buffer and write the data from the vertex array, then flip it
        in.clear();
        for (int i = 0; i < attributeBuffers.length; i++) {
            final ByteBuffer buffer = attributeBuffers[i];
            final DataFormat format = attributeFormats[i];
            final DataType type = format.getType();
            final int size = format.getCount();
            for (int ii = 0; ii < size; ii++) {
                // Here conversion from byte and short to int is implicit
                final int x = readComponent(buffer, type, size, index, ii);
                in.writeRaw(x);
            }
        }
        in.flip();
        // Clear the out buffer, run the vertex shader, and flip the out
        out.clear();
        shader.main(in, out);
        out.flip();
    }

    private void writeFragment(ShaderImplementation shader, ShaderBuffer in, ShaderBuffer out, int x, int y, float z) {
        // Clear the out buffer, run the fragment shader, and flip the out
        out.clear();
        shader.main(in, out);
        out.flip();
        // Retrieve the fragment color vector
        final float r = Float.intBitsToFloat(out.readRaw());
        final float g = Float.intBitsToFloat(out.readRaw());
        final float b = Float.intBitsToFloat(out.readRaw());
        final float a = Float.intBitsToFloat(out.readRaw());
        // Write at the fragment coordinates and depth (converted from [0, 1] to the full short range)
        // the output color packed into an int
        renderer.writePixel(x, y, SoftwareUtil.denormalizeToShort(z), SoftwareUtil.pack(r, g, b, a));
    }

    private int readComponent(ByteBuffer buffer, DataType type, int attributeSize, int index, int offset) {
        return SoftwareUtil.read(buffer, type, SoftwareUtil.read(indicesBuffer, INDICES_TYPE, index + this.offset) * attributeSize + offset);
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.SOFTWARE;
    }
}
