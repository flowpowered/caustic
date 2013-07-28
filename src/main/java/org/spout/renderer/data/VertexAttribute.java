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

import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TShortIterator;
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
public class VertexAttribute implements Cloneable {
	protected final String name;
	protected final DataType type;
	protected final int size;
	protected final UploadMode uploadMode;

	private ByteBuffer buffer;

	public VertexAttribute(String name, DataType type, int size, UploadMode uploadMode) {
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
	 * Returns a new byte buffer filled and ready to read, containing the attribute data.
	 * This method will {@link java.nio.ByteBuffer#flip()} the buffer before returning it.
	 *
	 * @return The buffer
	 */
	public ByteBuffer getBuffer() {
		if (buffer == null) {
			throw new IllegalStateException("ByteBuffer must have data before it is ready for use.");
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * Clears all of the buffer data.
	 */
	public void clear() {
		if (buffer != null) {
			buffer.clear();
		}
	}
	/**
	 * Replaces the current buffer data with a copy of the given {@link ByteBuffer}
	 * This method arbitrarily creates data for the ByteBuffer regardless of the data type of the vertex attribute.
	 *
	 * @param buffer to put
	 */
	public void put(ByteBuffer buffer) {
		this.buffer = BufferUtils.createByteBuffer(buffer.capacity());
		this.buffer.put(buffer);
	}

	/**
	 * Replaces the current buffer data with the list of bytes in the give {@link TByteList}
	 * This method arbitrarily creates data for the ByteBuffer regardless of the data type of the vertex attribute.
	 *
	 * @param list to put
	 */
	public void put(TByteList list) {
		this.buffer = BufferUtils.createByteBuffer(list.size());
		this.buffer.put(list.toArray());
	}

	/**
	 * Replaces the current buffer data with the list of bytes in the give {@link TShortList}
	 * This method arbitrarily creates data for the ByteBuffer regardless of the data type of the vertex attribute.
	 *
	 * @param list to put
	 */
	public void put(TShortList list) {
		this.buffer = BufferUtils.createByteBuffer(list.size() * DataType.SHORT.byteSize);
		TShortIterator iterator = list.iterator();
		while (iterator.hasNext()) {
			buffer.putShort(iterator.next());
		}
	}

	/**
	 * Replaces the current buffer data with the list of bytes in the give {@link TIntList}
	 * This method arbitrarily creates data for the ByteBuffer regardless of the data type of the vertex attribute.
	 *
	 * @param list to put
	 */
	public void put(TIntList list) {
		this.buffer = BufferUtils.createByteBuffer(list.size() * DataType.INT.byteSize);
		TIntIterator iterator = list.iterator();
		while (iterator.hasNext()) {
			buffer.putInt(iterator.next());
		}
	}

	/**
	 * Replaces the current buffer data with the list of bytes in the give {@link TFloatList}
	 * This method arbitrarily creates data for the ByteBuffer regardless of the data type of the vertex attribute.
	 *
	 * @param list to put
	 */
	public void put(TFloatList list) {
		this.buffer = BufferUtils.createByteBuffer(list.size() * DataType.FLOAT.byteSize);
		TFloatIterator iterator = list.iterator();
		while (iterator.hasNext()) {
			buffer.putFloat(iterator.next());
		}
	}

	/**
	 * Replaces the current buffer data with the list of bytes in the give {@link TDoubleList}
	 * This method arbitrarily creates data for the ByteBuffer regardless of the data type of the vertex attribute.
	 *
	 * @param list to put
	 */
	public void put(TDoubleList list) {
		this.buffer = BufferUtils.createByteBuffer(list.size() * DataType.DOUBLE.byteSize);
		TDoubleIterator iterator = list.iterator();
		while (iterator.hasNext()) {
			buffer.putDouble(iterator.next());
		}
	}

	@Override
	public VertexAttribute clone() {
		VertexAttribute clone = new VertexAttribute(name, type, size, uploadMode);
		clone.put(this.buffer);
		return clone;
	}


	/**
	 * Represents an attribute data type.
	 */
	public static enum DataType {
		BYTE(GL11.GL_BYTE, 1, true),
		UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE, 1, true),
		SHORT(GL11.GL_SHORT, 2, true),
		UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT, 2, true),
		INT(GL11.GL_INT, 4, true),
		UNISGNED_INT(GL11.GL_UNSIGNED_INT, 4, true),
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
