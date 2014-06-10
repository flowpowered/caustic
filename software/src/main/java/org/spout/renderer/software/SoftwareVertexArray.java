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
    private PolygonMode polygonMode = PolygonMode.FILL;
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
    public void setPolygonMode(PolygonMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Polygon mode cannot be null");
        }
        polygonMode = mode;
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
            case TRIANGLES:
                drawTriangles();
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
            // Compute the point
            readVertex(vertexShader, vertexIn, vertexOut, i);
            // Read the first 4 floats, which is the vertex position
            float x = Float.intBitsToFloat(vertexOut.readRaw());
            float y = Float.intBitsToFloat(vertexOut.readRaw());
            float z = Float.intBitsToFloat(vertexOut.readRaw());
            float w = Float.intBitsToFloat(vertexOut.readRaw());
            // Perform clipping, ignoring z clipping when depth clamping is active
            if (!isInside(x, y, z, w, clampDepth)) {
                continue;
            }
            // Compute the NDC coordinates
            final float wInverse = 1 / w;
            x *= wInverse;
            y *= -wInverse;
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
            // Compute the second point
            readVertex(vertexShader, vertexIn, vertexOut2, i + 1);
            // Read the first 4 floats, which is the vertex position
            float x2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float y2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float z2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float w2 = Float.intBitsToFloat(vertexOut2.readRaw());
            // Track the percentage of the line remaining after clipping on both ends
            float percent1 = 1, percent2 = 1;
            // Perform clipping on the first point, ignoring z clipping when depth clamping is active
            if (x1 < -w1) {
                if (x2 < -w2) {
                    continue;
                }
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                float t = (-w2 - x2) / (dx + dw);
                y1 = t * dy + y2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                x1 = -w1;
                percent1 *= t;
            }
            if (x1 > w1) {
                if (x2 > w2) {
                    continue;
                }
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                float t = (w2 - x2) / (dx - dw);
                y1 = t * dy + y2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                x1 = w1;
                percent1 *= t;
            }
            if (y1 < -w1) {
                if (y2 < -w2) {
                    continue;
                }
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                float t = (-w2 - y2) / (dy + dw);
                x1 = t * dx + x2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                y1 = -w1;
                percent1 *= t;
            }
            if (y1 > w1) {
                if (y2 > w2) {
                    continue;
                }
                final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                float t = (w2 - y2) / (dy - dw);
                x1 = t * dx + x2;
                z1 = t * dz + z2;
                w1 = t * dw + w2;
                y1 = w1;
                percent1 *= t;
            }
            if (!clampDepth) {
                if (z1 < -w1) {
                    if (z2 < -w2) {
                        continue;
                    }
                    final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                    float t = (-w2 - z2) / (dz + dw);
                    x1 = t * dx + x2;
                    y1 = t * dy + y2;
                    w1 = t * dw + w2;
                    z1 = -w1;
                    percent1 *= t;
                }
                if (z1 > w1) {
                    if (z2 > w2) {
                        continue;
                    }
                    final float dx = x1 - x2, dy = y1 - y2, dz = z1 - z2, dw = w1 - w2;
                    float t = (w2 - z2) / (dz - dw);
                    x1 = t * dx + x2;
                    y1 = t * dy + y2;
                    w1 = t * dw + w2;
                    z1 = w1;
                    percent1 *= t;
                }
            }
            // Perform clipping on the second point, ignoring z clipping when depth clamping is active
            if (x2 < -w2) {
                if (x1 < -w1) {
                    continue;
                }
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                float t = (-w1 - x1) / (dx + dw);
                y2 = t * dy + y1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                x2 = -w2;
                percent2 *= t;
            }
            if (x2 > w2) {
                if (x1 > w1) {
                    continue;
                }
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                float t = (w1 - x1) / (dx - dw);
                y2 = t * dy + y1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                x2 = w2;
                percent2 *= t;
            }
            if (y2 < -w2) {
                if (y1 < -w1) {
                    continue;
                }
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                float t = (-w1 - y1) / (dy + dw);
                x2 = t * dx + x1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                y2 = -w2;
                percent2 *= t;
            }
            if (y2 > w2) {
                if (y1 > w1) {
                    continue;
                }
                final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                float t = (w1 - y1) / (dy - dw);
                x2 = t * dx + x1;
                z2 = t * dz + z1;
                w2 = t * dw + w1;
                y2 = w2;
                percent2 *= t;
            }
            if (!clampDepth) {
                if (z2 < -w2) {
                    if (z1 < -w1) {
                        continue;
                    }
                    final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                    float t = (-w1 - z1) / (dz + dw);
                    x2 = t * dx + x1;
                    y2 = t * dy + y1;
                    w2 = t * dw + w1;
                    z2 = -w2;
                    percent2 *= t;
                }
                if (z2 > w2) {
                    if (z1 > w1) {
                        continue;
                    }
                    final float dx = x2 - x1, dy = y2 - y1, dz = z2 - z1, dw = w2 - w1;
                    float t = (w1 - z1) / (dz - dw);
                    x2 = t * dx + x1;
                    y2 = t * dy + y1;
                    w2 = t * dw + w1;
                    z2 = w2;
                    percent2 *= t;
                }
            }
            // The end percent is the percentage that was cut off times the percentage left over from clipping
            // the first point; inverted to get the remaining amount instead of the cut amount
            percent2 = 1 - percent1 * (1 - percent2);
            // The start percent is what's remaining from clipping the first point
            percent1 = 1 - percent1;
            // Compute the NDC coordinates of the first point
            final float wInverse1 = 1 / w1;
            x1 *= wInverse1;
            y1 *= -wInverse1;
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
            y2 *= -wInverse2;
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

    private void drawTriangles() {
        // Get some renderer properties
        final Rectangle viewPort = renderer.getViewPort();
        final boolean clampDepth = renderer.isEnabled(Capability.DEPTH_CLAMP);
        final boolean cullFace = renderer.isEnabled(Capability.CULL_FACE);
        // Get the shader program
        final SoftwareProgram program = renderer.getProgram();
        // Get the vertex shader implementation, and create appropriate in and out buffers
        final ShaderImplementation vertexShader = program.getShader(ShaderType.VERTEX).getImplementation();
        final DataFormat[] vertexOutputFormat = vertexShader.getOutputFormat();
        final ShaderBuffer vertexIn = new ShaderBuffer(attributeFormats);
        final ShaderBuffer vertexOut1 = new ShaderBuffer(vertexOutputFormat);
        final ShaderBuffer vertexOut2 = new ShaderBuffer(vertexOutputFormat);
        final ShaderBuffer vertexOut3 = new ShaderBuffer(vertexOutputFormat);
        // Get the fragment shader implementation, and create appropriate in and out buffers
        final ShaderImplementation fragmentShader = program.getShader(ShaderType.FRAGMENT).getImplementation();
        final ShaderBuffer fragmentIn = new ShaderBuffer(vertexOutputFormat);
        final ShaderBuffer fragmentOut = new ShaderBuffer(FRAGMENT_OUTPUT);
        // Arrays for storing the vertices for clipping
        final float[] inVertices = new float[6 * 4];
        final float[] outVertices = new float[6 * 4];
        final float[] tempVertex = new float[4];
        // For all indices that need to be drawn
        for (int i = 0; i < count; i += 3) {
            // Compute the first point
            readVertex(vertexShader, vertexIn, vertexOut1, i);
            // Read the first 4 floats, which is the vertex position
            float x1 = Float.intBitsToFloat(vertexOut1.readRaw());
            float y1 = Float.intBitsToFloat(vertexOut1.readRaw());
            float z1 = Float.intBitsToFloat(vertexOut1.readRaw());
            float w1 = Float.intBitsToFloat(vertexOut1.readRaw());
            // Compute the second point
            readVertex(vertexShader, vertexIn, vertexOut2, i + 1);
            // Read the first 4 floats, which is the vertex position
            float x2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float y2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float z2 = Float.intBitsToFloat(vertexOut2.readRaw());
            float w2 = Float.intBitsToFloat(vertexOut2.readRaw());
            // Compute the third point
            readVertex(vertexShader, vertexIn, vertexOut3, i + 2);
            // Read the first 4 floats, which is the vertex position
            float x3 = Float.intBitsToFloat(vertexOut3.readRaw());
            float y3 = Float.intBitsToFloat(vertexOut3.readRaw());
            float z3 = Float.intBitsToFloat(vertexOut3.readRaw());
            float w3 = Float.intBitsToFloat(vertexOut3.readRaw());
            // Cull back facing triangles if needed
            if (cullFace) {
                // Get the edge vectors
                final float dx31 = x3 / w3 - x1 / w1;
                final float dy31 = y3 / w3 - y1 / w1;
                final float dx21 = x2 / w2 - x1 / w1;
                final float dy21 = y2 / w2 - y1 / w1;
                // Compute the z component of the cross product
                if (dy31 * dx21 - dx31 * dy21 <= GenericMath.FLT_EPSILON) {
                    // A zero or negative value is back facing
                    continue;
                }
            }
            // Copy the vertices to the list
            outVertices[0] = x1;
            outVertices[1] = y1;
            outVertices[2] = z1;
            outVertices[3] = w1;
            outVertices[4] = x2;
            outVertices[5] = y2;
            outVertices[6] = z2;
            outVertices[7] = w2;
            outVertices[8] = x3;
            outVertices[9] = y3;
            outVertices[10] = z3;
            outVertices[11] = w3;
            int outSize = 3;
            // Perform clipping using the Sutherland–Hodgman algorithm
            for (int p = 0; p < 6; p++) {
                System.arraycopy(outVertices, 0, inVertices, 0, outSize * 4);
                final int inSize = outSize;
                outSize = 0;
                if (inSize == 0) {
                    break;
                }
                final int lastInIndex = (inSize - 1) * 4;
                float sx = inVertices[lastInIndex];
                float sy = inVertices[lastInIndex + 1];
                float sz = inVertices[lastInIndex + 2];
                float sw = inVertices[lastInIndex + 3];
                for (int e = 0; e < inSize; e++) {
                    final int ei = e * 4;
                    final float ex = inVertices[ei];
                    final float ey = inVertices[ei + 1];
                    final float ez = inVertices[ei + 2];
                    final float ew = inVertices[ei + 3];
                    if (isInside(ex, ey, ez, ew, p)) {
                        if (!isInside(sx, sy, sz, sw, p)) {
                            computeIntersection(sx, sy, sz, sw, ex, ey, ez, ew, p, tempVertex);
                            System.arraycopy(tempVertex, 0, outVertices, outSize * 4, 4);
                            outSize++;
                        }
                        final int nextOutIndex = outSize * 4;
                        outVertices[nextOutIndex] = ex;
                        outVertices[nextOutIndex + 1] = ey;
                        outVertices[nextOutIndex + 2] = ez;
                        outVertices[nextOutIndex + 3] = ew;
                        outSize++;
                    } else if (isInside(sx, sy, sz, sw, p)) {
                        computeIntersection(sx, sy, sz, sw, ex, ey, ez, ew, p, tempVertex);
                        System.arraycopy(tempVertex, 0, outVertices, outSize * 4, 4);
                        outSize++;
                    }
                    sx = ex;
                    sy = ey;
                    sz = ez;
                    sw = ew;
                }
            }
            // If the out list is empty the triangle is completely clipped
            if (outSize < 3) {
                continue;
            }
            // Convert all the vertices to NDC and then window coordinates
            for (int v = 0; v < outSize; v++) {
                // Get the vertex components
                final int vi = v * 4;
                float x = outVertices[vi];
                float y = outVertices[vi + 1];
                float z = outVertices[vi + 2];
                float w = outVertices[vi + 3];
                // Compute the NDC coordinates of the point
                final float wInverse = 1 / w;
                x *= wInverse;
                y *= -wInverse;
                z *= wInverse;
                // Normalize and convert to window coordinates
                outVertices[vi] = (x + 1) / 2 * (viewPort.getWidth() - 1) + viewPort.getX();
                outVertices[vi + 1] = (y + 1) / 2 * (viewPort.getHeight() - 1) + viewPort.getY();
                outVertices[vi + 2] = SoftwareUtil.clamp((z + 1) / 2, 0, 1);
                // Store 1/w in w to so that the fragment position vector is the same as in OpenGL
                outVertices[vi + 3] = wInverse;
            }
            // Draw the triangles
            final int triangleCount = outSize - 2;
            for (int n = 0; n < triangleCount; n++) {
                // Grab the vertices, having a new triangle every two vertices
                int vi = n * 8;
                x1 = outVertices[vi];
                y1 = outVertices[vi + 1];
                z1 = outVertices[vi + 2];
                w1 = outVertices[vi + 3];
                vi += 4;
                x2 = outVertices[vi];
                y2 = outVertices[vi + 1];
                z2 = outVertices[vi + 2];
                w2 = outVertices[vi + 3];
                vi = (vi + 4) % (outSize * 4);
                x3 = outVertices[vi];
                y3 = outVertices[vi + 1];
                z3 = outVertices[vi + 2];
                w3 = outVertices[vi + 3];
                // Draw the triangle
                drawTriangle(
                        vertexOut1, x1, y1, z1, w1,
                        vertexOut2, x2, y2, z2, w2,
                        vertexOut3, x3, y3, z3, w3,
                        fragmentShader, fragmentIn, fragmentOut);
            }
        }
    }

    private boolean isInside(float x, float y, float z, float w, int plane) {
        switch (plane) {
            case 0:
                return x >= -w;
            case 1:
                return x <= w;
            case 2:
                return y >= -w;
            case 3:
                return y <= w;
            case 4:
                return z >= -w;
            case 5:
                return z <= w;
            default:
                throw new IllegalArgumentException("Unknown plane: " + plane);
        }
    }

    private void computeIntersection(float sx, float sy, float sz, float sw, float ex, float ey, float ez, float ew, int plane, float[] intersection) {
        final float dx = ex - sx, dy = ey - sy, dz = ez - sz, dw = ew - sw;
        final float t;
        switch (plane) {
            case 0:
                t = (-sw - sx) / (dx + dw);
                break;
            case 1:
                t = (sw - sx) / (dx - dw);
                break;
            case 2:
                t = (-sw - sy) / (dy + dw);
                break;
            case 3:
                t = (sw - sy) / (dy - dw);
                break;
            case 4:
                t = (-sw - sz) / (dz + dw);
                break;
            case 5:
                t = (sw - sz) / (dz - dw);
                break;
            default:
                throw new IllegalArgumentException("Unknown plane: " + plane);
        }
        intersection[0] = t * dx + sx;
        intersection[1] = t * dy + sy;
        intersection[2] = t * dz + sz;
        intersection[3] = t * dw + sw;
    }

    // Based on http://devmaster.net/posts/6145/advanced-rasterization
    private void drawTriangle(ShaderBuffer out1, float x1, float y1, float z1, float w1,
                              ShaderBuffer out2, float x2, float y2, float z2, float w2,
                              ShaderBuffer out3, float x3, float y3, float z3, float w3,
                              ShaderImplementation fragmentShader, ShaderBuffer fragmentIn, ShaderBuffer fragmentOut) {
        // (28).(4) (in bits) fixed-point coordinates
        final int fy1 = Math.round(y1 * 16);
        final int fy2 = Math.round(y2 * 16);
        final int fy3 = Math.round(y3 * 16);
        final int fx1 = Math.round(x1 * 16);
        final int fx2 = Math.round(x2 * 16);
        final int fx3 = Math.round(x3 * 16);
        // Deltas
        final int dx12 = fx1 - fx2;
        final int dx23 = fx2 - fx3;
        final int dx31 = fx3 - fx1;
        final int dy12 = fy1 - fy2;
        final int dy23 = fy2 - fy3;
        final int dy31 = fy3 - fy1;
        // Fixed-point deltas
        final int fdx12 = dx12 << 4;
        final int fdx23 = dx23 << 4;
        final int fdx31 = dx31 << 4;
        final int fdy12 = dy12 << 4;
        final int fdy23 = dy23 << 4;
        final int fdy31 = dy31 << 4;
        // Block size, standard 8x8 (must be power of two), here 2^3
        final int block = 1 << 3;
        // Bounding rectangle, start in the corner of the 8x8 block
        final int minX = Math.min(fx1, Math.min(fx2, fx3)) + 0xf >> 4 & ~(block - 1);
        final int maxX = Math.max(fx1, Math.max(fx2, fx3)) + 0xf >> 4;
        final int minY = Math.min(fy1, Math.min(fy2, fy3)) + 0xf >> 4 & ~(block - 1);
        final int maxY = Math.max(fy1, Math.max(fy2, fy3)) + 0xf >> 4;
        // Determinant of deltas to compute the normalized barycentric coordinates for interpolation
        final float det = dx23 * dy12 - dy23 * dx12;
        // Barycentric coordinates
        int c1 = dy12 * fx1 - dx12 * fy1;
        int c2 = dy23 * fx2 - dx23 * fy2;
        int c3 = dy31 * fx3 - dx31 * fy3;
        // Correct for fill convention
        if (dy12 < 0 || dy12 == 0 && dx12 > 0) {
            c1++;
        }
        if (dy23 < 0 || dy23 == 0 && dx23 > 0) {
            c2++;
        }
        if (dy31 < 0 || dy31 == 0 && dx31 > 0) {
            c3++;
        }
        // Loop through blocks
        for (int by = minY; by < maxY; by += block) {
            for (int bx = minX; bx < maxX; bx += block) {
                // Corners of block
                final int bx0 = bx << 4;
                final int bx1 = bx + block - 1 << 4;
                final int by0 = by << 4;
                final int by1 = by + block - 1 << 4;
                // Evaluate half-space functions
                final boolean a00 = c1 + dx12 * by0 - dy12 * bx0 <= 0;
                final boolean a10 = c1 + dx12 * by0 - dy12 * bx1 <= 0;
                final boolean a01 = c1 + dx12 * by1 - dy12 * bx0 <= 0;
                final boolean a11 = c1 + dx12 * by1 - dy12 * bx1 <= 0;
                final boolean b00 = c2 + dx23 * by0 - dy23 * bx0 <= 0;
                final boolean b10 = c2 + dx23 * by0 - dy23 * bx1 <= 0;
                final boolean b01 = c2 + dx23 * by1 - dy23 * bx0 <= 0;
                final boolean b11 = c2 + dx23 * by1 - dy23 * bx1 <= 0;
                final boolean c00 = c3 + dx31 * by0 - dy31 * bx0 <= 0;
                final boolean c10 = c3 + dx31 * by0 - dy31 * bx1 <= 0;
                final boolean c01 = c3 + dx31 * by1 - dy31 * bx0 <= 0;
                final boolean c11 = c3 + dx31 * by1 - dy31 * bx1 <= 0;
                // Skip the block when outside an edge
                if (a00 && a10 && a01 && a11 || b00 && b10 && b01 && b11 || c00 && c10 && c01 && c11) {
                    continue;
                }
                // Compute the barycentric coordinates
                int cy1 = c1 + dx12 * by0 - dy12 * bx0;
                int cy2 = c2 + dx23 * by0 - dy23 * bx0;
                int cy3 = c3 + dx31 * by0 - dy31 * bx0;
                // Iterate the block
                for (int y = by; y < by + block; y++) {
                    int cx1 = cy1;
                    int cx2 = cy2;
                    int cx3 = cy3;
                    for (int x = bx; x < bx + block; x++) {
                        // Only draw pixels inside the triangle
                        if (cx1 > 0 && cx2 > 0 && cx3 > 0) {
                            // Compute the normalized barycentric coordinates
                            final float t = cx1 / det;
                            final float r = cx2 / det;
                            final float s = 1 - t - r;
                            // Lerp the rest of the data using the barycentric coordinates
                            final float z = SoftwareUtil.baryLerp(z1, z2, z3, r, s, t);
                            final float w = SoftwareUtil.baryLerp(w1, w2, w3, r, s, t);
                            fragmentIn.clear();
                            fragmentIn.writeRaw(Float.floatToIntBits(x));
                            fragmentIn.writeRaw(Float.floatToIntBits(y));
                            fragmentIn.writeRaw(Float.floatToIntBits(z));
                            fragmentIn.writeRaw(Float.floatToIntBits(w));
                            out1.position(4);
                            out2.position(4);
                            out3.position(4);
                            SoftwareUtil.baryLerp(out1, out2, out3, r, s, t, 1, fragmentIn);
                            fragmentIn.flip();
                            // Shade and write the fragment
                            writeFragment(fragmentShader, fragmentIn, fragmentOut, x, y, z);
                        }
                        cx1 -= fdy12;
                        cx2 -= fdy23;
                        cx3 -= fdy31;
                    }
                    cy1 += fdx12;
                    cy2 += fdx23;
                    cy3 += fdx31;
                }
            }
        }
    }

    private boolean isInside(float x, float y, float z, float w, boolean clampDepth) {
        return w != 0 && x >= -w && x <= w && y >= -w && y <= w && (clampDepth || z >= -w && z <= w);
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
                // Here conversion from byte or short to int is implicit
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
        // TODO: do depth test before shading
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
