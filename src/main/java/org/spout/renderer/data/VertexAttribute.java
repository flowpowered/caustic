/*
 * This file is part of Caustic.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic is licensed under the Spout License Version 1.
 *
 * Caustic is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.data;

import java.nio.ByteBuffer;

import gnu.trove.list.TByteList;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.TShortList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TShortArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public abstract class VertexAttribute {
	protected final String name;
	protected final DataType type;
	protected final int size;

	protected VertexAttribute(String name, DataType type, int size) {
		this.name = name;
		this.type = type;
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public DataType getType() {
		return type;
	}

	public int getSize() {
		return size;
	}

	public abstract ByteBuffer getBuffer();

	public abstract void clear();

	public static class ByteVertexAttribute extends VertexAttribute {
		private final TByteList data = new TByteArrayList();

		public ByteVertexAttribute(String name, int size) {
			super(name, DataType.BYTE, size);
		}

		public TByteList getData() {
			return data;
		}

		@Override
		public ByteBuffer getBuffer() {
			final ByteBuffer buffer = BufferUtils.createByteBuffer(data.size() * DataType.BYTE.getByteSize());
			buffer.put(data.toArray());
			buffer.flip();
			return buffer;
		}

		@Override
		public void clear() {
			data.clear();
		}
	}

	public static class ShortVertexAttribute extends VertexAttribute {
		private final TShortList data = new TShortArrayList();

		public ShortVertexAttribute(String name, int size) {
			super(name, DataType.SHORT, size);
		}

		public TShortList getData() {
			return data;
		}

		@Override
		public ByteBuffer getBuffer() {
			final ByteBuffer buffer = BufferUtils.createByteBuffer(data.size() * DataType.SHORT.getByteSize());
			for (int i = 0; i < data.size(); i++) {
				buffer.putShort(data.get(i));
			}
			buffer.flip();
			return buffer;
		}

		@Override
		public void clear() {
			data.clear();
		}
	}

	public static class IntVertexAttribute extends VertexAttribute {
		private final TIntList data = new TIntArrayList();

		public IntVertexAttribute(String name, int size) {
			super(name, DataType.INT, size);
		}

		public TIntList getData() {
			return data;
		}

		@Override
		public ByteBuffer getBuffer() {
			final ByteBuffer buffer = BufferUtils.createByteBuffer(data.size() * DataType.INT.getByteSize());
			for (int i = 0; i < data.size(); i++) {
				buffer.putInt(data.get(i));
			}
			buffer.flip();
			return buffer;
		}

		@Override
		public void clear() {
			data.clear();
		}
	}

	public static class FloatVertexAttribute extends VertexAttribute {
		private final TFloatList data = new TFloatArrayList();

		public FloatVertexAttribute(String name, int size) {
			super(name, DataType.FLOAT, size);
		}

		public TFloatList getData() {
			return data;
		}

		@Override
		public ByteBuffer getBuffer() {
			final ByteBuffer buffer = BufferUtils.createByteBuffer(data.size() * DataType.FLOAT.getByteSize());
			for (int i = 0; i < data.size(); i++) {
				buffer.putFloat(data.get(i));
			}
			buffer.flip();
			return buffer;
		}

		@Override
		public void clear() {
			data.clear();
		}
	}

	public static class DoubleVertexAttribute extends VertexAttribute {
		private final TDoubleList data = new TDoubleArrayList();

		public DoubleVertexAttribute(String name, int size) {
			super(name, DataType.DOUBLE, size);
		}

		public TDoubleList getData() {
			return data;
		}

		@Override
		public ByteBuffer getBuffer() {
			final ByteBuffer buffer = BufferUtils.createByteBuffer(data.size() * DataType.DOUBLE.getByteSize());
			for (int i = 0; i < data.size(); i++) {
				buffer.putDouble(data.get(i));
			}
			buffer.flip();
			return buffer;
		}

		@Override
		public void clear() {
			data.clear();
		}
	}

	/**
	 * Represents an attribute data type.
	 */
	public static enum DataType {
		BYTE(GL11.GL_BYTE, 1),
		SHORT(GL11.GL_SHORT, 2),
		INT(GL11.GL_INT, 4),
		FLOAT(GL11.GL_FLOAT, 4),
		DOUBLE(GL11.GL_DOUBLE, 8);
		private final int glConstant;
		private final int byteSize;

		private DataType(int glConstant, int byteSize) {
			this.glConstant = glConstant;
			this.byteSize = byteSize;
		}

		/**
		 * Returns the OpenGL constant for the data type.
		 *
		 * @return The OpenGL constant
		 */
		public int getGLConstant() {
			return glConstant;
		}

		/**
		 * Returns the size in bytes of the data type.
		 *
		 * @return The size in bytes
		 */
		public int getByteSize() {
			return byteSize;
		}
	}
}
