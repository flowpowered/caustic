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

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector4d;
import com.flowpowered.math.vector.Vector4f;
import com.flowpowered.math.vector.Vector4i;

import com.flowpowered.caustic.api.data.VertexAttribute.DataType;

/**
 *
 */
public class DataFormat {
    private static final int MAX_LOOKUP_HASH = 1 << 6;
    private static final Class<?>[] FORMAT_CLASS_LOOKUP = new Class<?>[MAX_LOOKUP_HASH];
    private final DataType type;
    private final int count;

    static {
        // Integer
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.INT, 1)] = int.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.INT, 2)] = Vector2i.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.INT, 3)] = Vector3i.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.INT, 4)] = Vector4i.class;
        // Float
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.FLOAT, 1)] = float.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.FLOAT, 2)] = Vector2f.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.FLOAT, 3)] = Vector3f.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.FLOAT, 4)] = Vector4f.class;
        // Double
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.DOUBLE, 1)] = double.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.DOUBLE, 2)] = Vector2d.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.DOUBLE, 3)] = Vector3d.class;
        FORMAT_CLASS_LOOKUP[lookupHash(DataType.DOUBLE, 4)] = Vector4d.class;
    }

    public DataFormat(DataType type, int count) {
        this.type = type;
        this.count = count;
    }

    public DataType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public int getByteSize() {
        return count << type.getMultiplyShift();
    }

    public Class<?> getTypeClass() {
        return FORMAT_CLASS_LOOKUP[lookupHash(type, count)];
    }

    @Override
    public String toString() {
        return type + "-" + count;
    }

    private static byte lookupHash(DataType type, int count) {
        // Format: 00MM ISCC
        // M: multiply shift, I: integer, S: signed, C: count
        return (byte) ((type.getMultiplyShift() & 0b11) << 4
                | ((type.isInteger() ? 1 : 0) & 0b1) << 3
                | ((type.isSigned() ? 1 : 0) & 0b1) << 2
                | count & 0b11);
    }
}
