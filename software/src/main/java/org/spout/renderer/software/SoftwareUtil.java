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

import org.spout.renderer.api.GLImplementation;
import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.api.util.CausticUtil;

/**
 *
 */
public final class SoftwareUtil {
    private static final int BYTE_MASK = 0xFF;
    private static final int SHORT_MASK = 0xFFFF;
    private static final float BYTE_MAX = Byte.MAX_VALUE;
    private static final float SHORT_MAX = Byte.MAX_VALUE;
    private static final float INT_MAX = Byte.MAX_VALUE;
    public static final GLImplementation SOFT_IMPL = new GLImplementation(null, SoftwareContext.class.getName());

    private SoftwareUtil() {
    }

    static ByteBuffer create(ByteBuffer buffer, int newCapacity, float threshold) {
        final int oldCapacity = buffer != null ? buffer.capacity() : 0;
        if (buffer == null || newCapacity > oldCapacity || newCapacity <= oldCapacity * threshold) {
            return CausticUtil.createByteBuffer(newCapacity);
        } else {
            buffer.clear();
            return buffer;
        }
    }

    static ByteBuffer set(ByteBuffer buffer, ByteBuffer newData, float threshold) {
        final int newCapacity = newData.remaining();
        buffer = create(buffer, newCapacity, threshold);
        buffer.put(newData);
        buffer.flip();
        return buffer;
    }

    static ByteBuffer setAsFloat(ByteBuffer buffer, ByteBuffer newData, DataType type, float threshold, boolean normalize) {
        final int elementCount = newData.remaining() / type.getByteSize();
        final int newCapacity = elementCount << DataType.FLOAT.getMultiplyShift();
        buffer = create(buffer, newCapacity, threshold);
        for (int i = 0; i < elementCount; i++) {
            buffer.putFloat(toFloat(type, read(newData, type), normalize));
        }
        buffer.flip();
        return buffer;
    }

    static int read(ByteBuffer data, DataType type) {
        switch (type) {
            case BYTE:
                return data.get();
            case SHORT:
                return data.getShort();
            case INT:
            case FLOAT:
                return data.getInt();
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static int read(ByteBuffer data, DataType type, int i) {
        i <<= type.getMultiplyShift();
        switch (type) {
            case BYTE:
                return data.get(i);
            case SHORT:
                return data.getShort(i);
            case INT:
            case FLOAT:
                return data.getInt(i);
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static void write(ByteBuffer data, DataType type, int value) {
        switch (type) {
            case BYTE:
                data.put((byte) (value & BYTE_MASK));
                break;
            case SHORT:
                data.putShort((short) (value & SHORT_MASK));
                break;
            case INT:
            case FLOAT:
                data.putInt(value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static void write(ByteBuffer data, DataType type, int value, int i) {
        i <<= type.getMultiplyShift();
        switch (type) {
            case BYTE:
                data.put(i, (byte) (value & BYTE_MASK));
                break;
            case SHORT:
                data.putShort(i, (short) (value & SHORT_MASK));
                break;
            case INT:
            case FLOAT:
                data.putInt(i, value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static void advance(ByteBuffer data, DataType type) {
        advance(data, type, 1);
    }

    static void advance(ByteBuffer data, DataType type, int n) {
        data.position(data.position() + n << type.getMultiplyShift());
    }

    static void copy(ByteBuffer source, DataType sourceType, ByteBuffer destination, DataType destinationType) {
        write(destination, destinationType, read(source, sourceType));
    }

    static void copy(ByteBuffer source, DataType sourceType, int is, ByteBuffer destination, DataType destinationType, int id) {
        write(destination, destinationType, read(source, sourceType, id), is);
    }

    static float toFloat(DataType type, int value, boolean normalize) {
        switch (type) {
            case BYTE:
                final byte b = (byte) (value & BYTE_MASK);
                return normalize ? b / BYTE_MAX : b;
            case SHORT:
                final short s = (short) (value & SHORT_MASK);
                return normalize ? s / SHORT_MAX : s;
            case INT:
                return normalize ? value / INT_MAX : value;
            case FLOAT:
                return Float.intBitsToFloat(value);
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static int pack(Vector4f v) {
        return pack(v.getX(), v.getY(), v.getZ(), v.getW());
    }

    static int pack(float r, float g, float b, float a) {
        return ((int) (a * 255) & 0xFF) << 24 | ((int) (r * 255) & 0xFF) << 16 | ((int) (g * 255) & 0xFF) << 8 | (int) (b * 255) & 0xFF;
    }
}
