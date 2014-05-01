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

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector4d;
import com.flowpowered.math.vector.Vector4f;
import com.flowpowered.math.vector.Vector4i;

import org.spout.renderer.api.data.VertexAttribute.DataType;

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
