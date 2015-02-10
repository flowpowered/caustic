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

import java.nio.IntBuffer;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.Vector4f;
import com.flowpowered.math.vector.Vector4i;

import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.util.CausticUtil;

/**
 *
 */
class ShaderBuffer implements InBuffer, OutBuffer {
    private final IntBuffer buffer;
    private final DataFormat[] formats;
    private int position = 0;
    private int count = 0;

    ShaderBuffer(DataFormat[] formats) {
        this.formats = new DataFormat[formats.length];
        int capacity = 0;
        for (int i = 0; i < formats.length; i++) {
            final DataFormat format = formats[i];
            final int count = format.getCount();
            this.formats[i] = format.getType().isInteger() ? new DataFormat(DataType.INT, count) : format;
            capacity += count;
        }
        buffer = CausticUtil.createIntBuffer(capacity);
    }

    DataFormat[] getFormat() {
        return formats;
    }

    void clear() {
        buffer.clear();
        position = 0;
    }

    void flip() {
        buffer.flip();
        position = 0;
    }

    void rewind() {
        buffer.rewind();
    }

    int position() {
        return buffer.position();
    }

    void position(int position) {
        buffer.position(position);
    }

    int remaining() {
        return buffer.remaining();
    }

    int readRaw() {
        return buffer.get();
    }

    void writeRaw(int value) {
        buffer.put(value);
    }

    void writeRaw(ShaderBuffer buffer) {
        while (buffer.remaining() > 0) {
            writeRaw(buffer.readRaw());
        }
    }

    @Override
    public int readInt() {
        final int i = readInt0();
        advance();
        return i;
    }

    @Override
    public Vector2i readVector2i() {
        final Vector2i v = new Vector2i(readInt0(), readInt0());
        advance();
        return v;
    }

    @Override
    public Vector3i readVector3i() {
        final Vector3i v = new Vector3i(readInt0(), readInt0(), readInt0());
        advance();
        return v;
    }

    @Override
    public Vector4i readVector4i() {
        final Vector4i v = new Vector4i(readInt0(), readInt0(), readInt0(), readInt0());
        advance();
        return v;
    }

    @Override
    public float readFloat() {
        final float f = readFloat0();
        advance();
        return f;
    }

    @Override
    public Vector2f readVector2f() {
        final Vector2f v = new Vector2f(readFloat0(), readFloat0());
        advance();
        return v;
    }

    @Override
    public Vector3f readVector3f() {
        final Vector3f v = new Vector3f(readFloat0(), readFloat0(), readFloat0());
        advance();
        return v;
    }

    @Override
    public Vector4f readVector4f() {
        final Vector4f v = new Vector4f(readFloat0(), readFloat0(), readFloat0(), readFloat0());
        advance();
        return v;
    }

    @Override
    public void skip() {
        advance();
    }

    @Override
    public void writeInt(int i) {
        writeInt0(i);
        advance();
    }

    @Override
    public void writeVector2i(Vector2i v) {
        writeInt0(v.getX());
        writeInt0(v.getY());
        advance();
    }

    @Override
    public void writeVector3i(Vector3i v) {
        writeInt0(v.getX());
        writeInt0(v.getY());
        writeInt0(v.getZ());
        advance();
    }

    @Override
    public void writeVector4i(Vector4i v) {
        writeInt0(v.getX());
        writeInt0(v.getY());
        writeInt0(v.getZ());
        writeInt0(v.getW());
        advance();
    }

    @Override
    public void writeFloat(float f) {
        writeFloat0(f);
        advance();
    }

    @Override
    public void writeVector2f(Vector2f v) {
        writeFloat0(v.getX());
        writeFloat0(v.getY());
        advance();
    }

    @Override
    public void writeVector3f(Vector3f v) {
        writeFloat0(v.getX());
        writeFloat0(v.getY());
        writeFloat0(v.getZ());
        advance();
    }

    @Override
    public void writeVector4f(Vector4f v) {
        writeFloat0(v.getX());
        writeFloat0(v.getY());
        writeFloat0(v.getZ());
        writeFloat0(v.getW());
        advance();
    }

    private int readInt0() {
        final DataFormat format = formats[position];
        if (++count > format.getCount()) {
            return 0;
        }
        final int i = buffer.get();
        switch (format.getType()) {
            case INT:
                return i;
            case FLOAT:
                return (int) Float.intBitsToFloat(i);
            default:
                throw new IllegalStateException("Unsupported type in input buffer: " + format.getType());
        }
    }

    private float readFloat0() {
        final DataFormat format = formats[position];
        if (++count > format.getCount()) {
            return 0;
        }
        final int i = buffer.get();
        switch (format.getType()) {
            case INT:
                return (float) i;
            case FLOAT:
                return Float.intBitsToFloat(i);
            default:
                throw new IllegalStateException("Unsupported type in input buffer: " + format.getType());
        }
    }

    private void writeInt0(int i) {
        final DataFormat format = formats[position];
        if (++count > format.getCount()) {
            return;
        }
        switch (format.getType()) {
            case INT:
                buffer.put(i);
                break;
            case FLOAT:
                buffer.put(Float.floatToIntBits((float) i));
                break;
            default:
                throw new IllegalStateException("Unsupported type in output buffer: " + format.getType());
        }
    }

    private void writeFloat0(float f) {
        final DataFormat format = formats[position];
        if (++count > format.getCount()) {
            return;
        }
        switch (format.getType()) {
            case INT:
                buffer.put((int) f);
                break;
            case FLOAT:
                buffer.put(Float.floatToIntBits(f));
                break;
            default:
                throw new IllegalStateException("Unsupported type in output buffer: " + format.getType());
        }
    }

    private void advance() {
        final DataFormat format = formats[position];
        int n = Math.max(format.getCount() - count, 0);
        buffer.position(buffer.position() + n);
        position++;
        count = 0;
    }
}
