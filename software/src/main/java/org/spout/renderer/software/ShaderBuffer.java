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

import java.nio.IntBuffer;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector4f;
import com.flowpowered.math.vector.Vector4i;

import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.api.util.CausticUtil;

/**
 *
 */
public class ShaderBuffer implements InBuffer, OutBuffer {
    private final IntBuffer buffer;
    private final DataFormat[] externalFormats;
    private int position = 0;

    ShaderBuffer(DataFormat[] externalFormats) {
        this.externalFormats = new DataFormat[externalFormats.length];
        int capacity = 0;
        for (int i = 0; i < externalFormats.length; i++) {
            final DataFormat format = externalFormats[i];
            final int count = format.getCount();
            this.externalFormats[i] = format.getType().isInteger() ? new DataFormat(DataType.INT, count) : format;
            capacity += count;
        }
        buffer = CausticUtil.createIntBuffer(capacity);
    }

    void clear() {
        buffer.clear();
        position = 0;
    }

    void flip() {
        buffer.flip();
        position = 0;
    }

    int readRaw() {
        return buffer.get();
    }

    void writeRaw(int value) {
        buffer.put(value);
    }

    @Override
    public int readInt() {
        final int i = readInt0();
        position++;
        return i;
    }

    @Override
    public float readFloat() {
        final float f = readFloat0();
        position++;
        return f;
    }

    @Override
    public Vector2i readVector2i() {
        final Vector2i v = new Vector2i(readInt0(), readInt0());
        position++;
        return v;
    }

    @Override
    public Vector3i readVector3i() {
        final Vector3i v = new Vector3i(readInt0(), readInt0(), readInt0());
        position++;
        return v;
    }

    @Override
    public Vector4i readVector4i() {
        final Vector4i v = new Vector4i(readInt0(), readInt0(), readInt0(), readInt0());
        position++;
        return v;
    }

    @Override
    public Vector2f readVector2f() {
        final Vector2f v = new Vector2f(readFloat0(), readFloat0());
        position++;
        return v;
    }

    @Override
    public Vector3f readVector3f() {
        final Vector3f v = new Vector3f(readFloat0(), readFloat0(), readFloat0());
        position++;
        return v;
    }

    @Override
    public Vector4f readVector4f() {
        final Vector4f v = new Vector4f(readFloat0(), readFloat0(), readFloat0(), readFloat0());
        position++;
        return v;
    }

    private int readInt0() {
        final DataType type = externalFormats[position].getType();
        final int i = buffer.get();
        switch (type) {
            case INT:
                return i;
            case FLOAT:
                return (int) Float.intBitsToFloat(i);
            default:
                throw new IllegalStateException("Unsupported type in input buffer: " + type);
        }
    }

    private float readFloat0() {
        final DataType type = externalFormats[position].getType();
        final int i = buffer.get();
        switch (type) {
            case INT:
                return (float) i;
            case FLOAT:
                return Float.intBitsToFloat(i);
            default:
                throw new IllegalStateException("Unsupported type in input buffer: " + type);
        }
    }

    @Override
    public void writeInt(int i) {
        writeInt0(i);
        position++;
    }

    @Override
    public void writeFloat(float f) {
        writeFloat0(f);
        position++;
    }

    @Override
    public void writeVector2i(Vector2i v) {
        writeInt0(v.getX());
        writeInt0(v.getY());
        position++;
    }

    @Override
    public void writeVector3i(Vector3i v) {
        writeInt0(v.getX());
        writeInt0(v.getY());
        writeInt0(v.getZ());
        position++;
    }

    @Override
    public void writeVector4i(Vector4i v) {
        writeInt0(v.getX());
        writeInt0(v.getY());
        writeInt0(v.getZ());
        writeInt0(v.getW());
        position++;
    }

    @Override
    public void writeVector2f(Vector2f v) {
        writeFloat0(v.getX());
        writeFloat0(v.getY());
        position++;
    }

    @Override
    public void writeVector3f(Vector3f v) {
        writeFloat0(v.getX());
        writeFloat0(v.getY());
        writeFloat0(v.getZ());
        position++;
    }

    @Override
    public void writeVector4f(Vector4f v) {
        writeFloat0(v.getX());
        writeFloat0(v.getY());
        writeFloat0(v.getZ());
        writeFloat0(v.getW());
        position++;
    }

    private void writeInt0(int i) {
        final DataType type = externalFormats[position].getType();
        switch (type) {
            case INT:
                buffer.put(i);
                break;
            case FLOAT:
                buffer.put(Float.floatToIntBits((float) i));
                break;
            default:
                throw new IllegalStateException("Unsupported type in output buffer: " + type);
        }
    }

    private void writeFloat0(float f) {
        final DataType type = externalFormats[position].getType();
        switch (type) {
            case INT:
                buffer.put((int) f);
                break;
            case FLOAT:
                buffer.put(Float.floatToIntBits(f));
                break;
            default:
                throw new IllegalStateException("Unsupported type in output buffer: " + type);
        }
    }
}
