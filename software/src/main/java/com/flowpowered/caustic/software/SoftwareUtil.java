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
package com.flowpowered.caustic.software;

import java.nio.ByteBuffer;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.GLImplementation;
import com.flowpowered.caustic.api.GLVersioned.GLVersion;
import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.util.CausticUtil;

/**
 *
 */
public final class SoftwareUtil {
    private static final float BYTE_RANGE = Byte.MAX_VALUE - Byte.MIN_VALUE;
    private static final float SHORT_RANGE = Short.MAX_VALUE - Short.MIN_VALUE;
    private static final float INT_RANGE = (float) Integer.MAX_VALUE - Integer.MIN_VALUE;
    private static final int BYTE_MASK = 0xFF;
    private static final int SHORT_MASK = 0xFFFF;
    private static final long INT_MASK = 0xFFFFFFFFl;
    public static final GLImplementation SOFT_IMPL = new GLImplementation(GLVersion.SOFTWARE, SoftwareContext.class.getName());

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
            case UNSIGNED_BYTE:
                return data.get();
            case SHORT:
            case UNSIGNED_SHORT:
                return data.getShort();
            case INT:
            case UNSIGNED_INT:
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
            case UNSIGNED_BYTE:
                return data.get(i);
            case SHORT:
            case UNSIGNED_SHORT:
                return data.getShort(i);
            case INT:
            case UNSIGNED_INT:
            case FLOAT:
                return data.getInt(i);
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static void write(ByteBuffer data, DataType type, int value) {
        switch (type) {
            case BYTE:
            case UNSIGNED_BYTE:
                data.put((byte) (value & BYTE_MASK));
                break;
            case SHORT:
            case UNSIGNED_SHORT:
                data.putShort((short) (value & SHORT_MASK));
                break;
            case INT:
            case UNSIGNED_INT:
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
            case UNSIGNED_BYTE:
                data.put(i, (byte) (value & BYTE_MASK));
                break;
            case SHORT:
            case UNSIGNED_SHORT:
                data.putShort(i, (short) (value & SHORT_MASK));
                break;
            case INT:
            case UNSIGNED_INT:
            case FLOAT:
                data.putInt(i, value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static float readAsFloat(ByteBuffer data, DataType type, int i) {
        i <<= type.getMultiplyShift();
        switch (type) {
            case BYTE:
                return (data.get(i) - Byte.MIN_VALUE) / BYTE_RANGE;
            case UNSIGNED_BYTE:
                return ((short) data.get(i) & BYTE_MASK) / BYTE_RANGE;
            case SHORT:
                return (data.getShort(i) - Short.MIN_VALUE) / SHORT_RANGE;
            case UNSIGNED_SHORT:
                return ((int) data.getShort(i) & SHORT_MASK) / SHORT_RANGE;
            case INT:
                return (data.getInt(i) - Integer.MIN_VALUE) / INT_RANGE;
            case UNSIGNED_INT:
                return ((long) data.getInt(i) & INT_MASK) / INT_RANGE;
            case FLOAT:
                return data.getFloat(i);
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
        final float f;
        switch (type) {
            case BYTE:
                f = (byte) (value & BYTE_MASK);
                return normalize ? (f - Byte.MIN_VALUE) / BYTE_RANGE : f;
            case UNSIGNED_BYTE:
                f = (short) value & BYTE_MASK;
                return normalize ? f / BYTE_RANGE : f;
            case SHORT:
                f = (short) (value & SHORT_MASK);
                return normalize ? (f - Short.MIN_VALUE) / SHORT_RANGE : f;
            case UNSIGNED_SHORT:
                f = value & SHORT_MASK;
                return normalize ? f / SHORT_RANGE : f;
            case INT:
                f = value;
                return normalize ? (f - Integer.MIN_VALUE) / INT_RANGE : f;
            case UNSIGNED_INT:
                f = (long) value & INT_MASK;
                return normalize ? f / INT_RANGE : f;
            case FLOAT:
                f = Float.intBitsToFloat(value);
                return f;
            default:
                throw new IllegalArgumentException("Unsupported data type: " + type);
        }
    }

    static int pack(Vector4f v) {
        return pack(v.getX(), v.getY(), v.getZ(), v.getW());
    }

    static int pack(float r, float g, float b, float a) {
        return ((int) (clamp(a, 0, 1) * BYTE_RANGE) & 0xFF) << 24
                | ((int) (clamp(r, 0, 1) * BYTE_RANGE) & 0xFF) << 16
                | ((int) (clamp(g, 0, 1) * BYTE_RANGE) & 0xFF) << 8
                | (int) (clamp(b, 0, 1) * BYTE_RANGE) & 0xFF;
    }

    static float clamp(float f, float low, float high) {
        if (f < low) {
            return low;
        }
        if (f > high) {
            return high;
        }
        return f;
    }

    static short denormalizeToShort(float f) {
        return (short) (clamp(f, 0, 1) * SHORT_RANGE + Short.MIN_VALUE);
    }

    static void lerp(ShaderBuffer inA, ShaderBuffer inB, float percent, int start, ShaderBuffer out) {
        final DataFormat[] formats = inA.getFormat();
        for (int i = start; i < formats.length; i++) {
            final DataFormat format = formats[i];
            final DataType type = format.getType();
            final int count = format.getCount();
            for (int ii = 0; ii < count; ii++) {
                switch (type) {
                    case INT:
                        out.writeRaw((int) GenericMath.lerp(inA.readRaw(), inB.readRaw(), percent));
                        break;
                    case FLOAT:
                        out.writeRaw(Float.floatToIntBits(GenericMath.lerp(Float.intBitsToFloat(inA.readRaw()), Float.intBitsToFloat(inB.readRaw()), percent)));
                        break;
                }
            }
        }
    }

    static float baryLerp(float a, float b, float c, float r, float s, float t) {
        return r * a + s * b + t * c;
    }

    static void baryLerp(ShaderBuffer inA, ShaderBuffer inB, ShaderBuffer inC, float r, float s, float t, int start, ShaderBuffer out) {
        final DataFormat[] formats = inA.getFormat();
        for (int i = start; i < formats.length; i++) {
            final DataFormat format = formats[i];
            final DataType type = format.getType();
            final int count = format.getCount();
            for (int ii = 0; ii < count; ii++) {
                switch (type) {
                    case INT:
                        out.writeRaw((int) (r * inA.readRaw() + s * inB.readRaw() + t * inC.readRaw()));
                        break;
                    case FLOAT:
                        out.writeRaw(Float.floatToIntBits(r * Float.intBitsToFloat(inA.readRaw()) + s * Float.intBitsToFloat(inB.readRaw()) + t * Float.intBitsToFloat(inC.readRaw())));
                        break;
                }
            }
        }
    }
}
