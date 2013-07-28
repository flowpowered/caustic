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

/**
 * Represents a vertex attribute. It has a name, a data type, a size (the number of components) and
 * data.
 */
public abstract class VertexAttribute implements Cloneable {
	protected final String name;
	protected final DataType type;
	protected final int size;
	protected final UploadMode uploadMode;

	protected VertexAttribute(String name, DataType type, int size, UploadMode uploadMode) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.uploadMode = uploadMode;
	}

	/**
	 * Returns the name of the attribute.
	 *
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the data type of the attribute.
	 *
	 * @return The data type
	 */
	public DataType getType() {
		return type;
	}

	/**
	 * Return the size of the attribute.
	 *
	 * @return The size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the upload mode for this attribute.
	 *
	 * @return The upload mode
	 */
	public UploadMode getUploadMode() {
		return uploadMode;
	}

	/**
	 * Returns a new byte buffer filled and ready to read, containing a copy of the attribute data.
	 *
	 * @return The buffer
	 */
	public abstract ByteBuffer getBuffer();

	/**
	 * Clears all of the attribute data.
	 */
	public abstract void clear();

	@Override
	public abstract VertexAttribute clone();

	/**
	 * Represents a vertex attribute of the byte type.
	 */
	public static class ByteVertexAttribute extends VertexAttribute {
		private final TByteList data = new TByteArrayList();

		/**
		 * Constructs a new byte attribute from the name and the size (component count). For
		 * compatibility, the default upload mode is {@link UploadMode#TO_FLOAT}.
		 *
		 * @param name The name
		 * @param size The size
		 */
		public ByteVertexAttribute(String name, int size) {
			this(name, size, UploadMode.TO_FLOAT);
		}

		/**
		 * Constructs a new byte attribute from the name and the size (component count) and upload mode.
		 *
		 * @param name The name
		 * @param size The size
		 * @param uploadMode The upload mode
		 */
		public ByteVertexAttribute(String name, int size, UploadMode uploadMode) {
			super(name, DataType.BYTE, size, uploadMode);
		}

		/**
		 * Returns the data list for this attribute.
		 *
		 * @return The data
		 */
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

		@Override
		public ByteVertexAttribute clone() {
			final ByteVertexAttribute clone = new ByteVertexAttribute(name, size);
			clone.getData().addAll(data);
			return clone;
		}
	}

	/**
	 * Represents a vertex attribute of the short type.
	 */
	public static class ShortVertexAttribute extends VertexAttribute {
		private final TShortList data = new TShortArrayList();

		/**
		 * Constructs a new short attribute from the name and the size (component count). For
		 * compatibility, the default upload mode is {@link UploadMode#TO_FLOAT}.
		 *
		 * @param name The name
		 * @param size The size
		 */
		public ShortVertexAttribute(String name, int size) {
			this(name, size, UploadMode.TO_FLOAT);
		}

		/**
		 * Constructs a new short attribute from the name and the size (component count) and upload mode.
		 *
		 * @param name The name
		 * @param size The size
		 * @param uploadMode The upload mode
		 */
		public ShortVertexAttribute(String name, int size, UploadMode uploadMode) {
			super(name, DataType.SHORT, size, uploadMode);
		}

		/**
		 * Returns the data list for this attribute.
		 *
		 * @return The data
		 */
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

		@Override
		public ShortVertexAttribute clone() {
			final ShortVertexAttribute clone = new ShortVertexAttribute(name, size);
			clone.getData().addAll(data);
			return clone;
		}
	}

	/**
	 * Represents a vertex attribute of the int type.
	 */
	public static class IntVertexAttribute extends VertexAttribute {
		private final TIntList data = new TIntArrayList();

		/**
		 * Constructs a new int attribute from the name and the size (component count). For compatibility,
		 * the default upload mode is {@link UploadMode#TO_FLOAT}.
		 *
		 * @param name The name
		 * @param size The size
		 */
		public IntVertexAttribute(String name, int size) {
			this(name, size, UploadMode.TO_FLOAT);
		}

		/**
		 * Constructs a new int attribute from the name and the size (component count) and upload mode.
		 *
		 * @param name The name
		 * @param size The size
		 * @param uploadMode The upload mode
		 */
		public IntVertexAttribute(String name, int size, UploadMode uploadMode) {
			super(name, DataType.INT, size, uploadMode);
		}

		/**
		 * Returns the data list for this attribute.
		 *
		 * @return The data
		 */
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

		@Override
		public IntVertexAttribute clone() {
			final IntVertexAttribute clone = new IntVertexAttribute(name, size);
			clone.getData().addAll(data);
			return clone;
		}
	}

	/**
	 * Represents a vertex attribute of the float type.
	 */
	public static class FloatVertexAttribute extends VertexAttribute {
		private final TFloatList data = new TFloatArrayList();

		/**
		 * Constructs a new float attribute from the name and the size (component count).
		 *
		 * @param name The name
		 * @param size The size
		 */
		public FloatVertexAttribute(String name, int size) {
			super(name, DataType.FLOAT, size, UploadMode.TO_FLOAT);
		}

		/**
		 * Returns the data list for this attribute.
		 *
		 * @return The data
		 */
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

		@Override
		public FloatVertexAttribute clone() {
			final FloatVertexAttribute clone = new FloatVertexAttribute(name, size);
			clone.getData().addAll(data);
			return clone;
		}
	}

	/**
	 * Represents a vertex attribute of the double type.
	 */
	public static class DoubleVertexAttribute extends VertexAttribute {
		private final TDoubleList data = new TDoubleArrayList();

		/**
		 * Constructs a new double attribute from the name and the size (component count).
		 *
		 * @param name The name
		 * @param size The size
		 */
		public DoubleVertexAttribute(String name, int size) {
			super(name, DataType.DOUBLE, size, UploadMode.TO_FLOAT);
		}

		/**
		 * Returns the data list for this attribute.
		 *
		 * @return The data
		 */
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

		@Override
		public DoubleVertexAttribute clone() {
			final DoubleVertexAttribute clone = new DoubleVertexAttribute(name, size);
			clone.getData().addAll(data);
			return clone;
		}
	}

	/**
	 * Represents an attribute data type.
	 */
	public static enum DataType {
		BYTE(GL11.GL_BYTE, 1, true),
		SHORT(GL11.GL_SHORT, 2, true),
		INT(GL11.GL_INT, 4, true),
		FLOAT(GL11.GL_FLOAT, 4, false),
		DOUBLE(GL11.GL_DOUBLE, 8, false);
		private final int glConstant;
		private final int byteSize;
		private final boolean integer;

		private DataType(int glConstant, int byteSize, boolean integer) {
			this.glConstant = glConstant;
			this.byteSize = byteSize;
			this.integer = integer;
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

		/**
		 * Returns true if the data type is an integer number ({@link DataType#BYTE}, {@link
		 * DataType#SHORT} or {@link DataType#INT}).
		 *
		 * @return Whether or not the data type is an integer
		 */
		public boolean isInteger() {
			return integer;
		}
	}

	/**
	 * The uploading mode. When uploading attribute data to OpenGL, integer data can be either
	 * converted to float or not (the later is only possible with version 3.0+). When converting to
	 * float, the data can be normalized or not. By default, {@link UploadMode#TO_FLOAT} is used as it
	 * provides the best compatibility.
	 */
	public static enum UploadMode {
		TO_FLOAT,
		TO_FLOAT_NORMALIZE,
		/**
		 * Only supported in OpenGL 3.0 and after.
		 */
		KEEP_INT;

		public boolean normalize() {
			return this == TO_FLOAT_NORMALIZE;
		}
	}
}
