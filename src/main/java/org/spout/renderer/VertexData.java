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
package org.spout.renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Set;

import gnu.trove.function.TIntFunction;
import gnu.trove.impl.Constants;
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
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class VertexData {
	private static final TIntFunction DECREMENT = new TIntFunction() {
		@Override
		public int execute(int value) {
			return value - 1;
		}
	};
	// rendering indices
	private final TIntList indices = new TIntArrayList();
	// attributes by index
	private final TIntObjectMap<VertexAttribute> attributes = new TIntObjectHashMap<>();
	// index from name lookup
	private final TObjectIntMap<String> nameToIndex = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);
	// next available attribute index
	private int index;

	/**
	 * Returns the list of indices used by OpenGL to pick the vertices to draw the object with in the
	 * correct order. Use it to add mesh data.
	 *
	 * @return The indices list
	 */
	public TIntList getIndices() {
		return indices;
	}

	public int getIndicesCount() {
		return indices.size();
	}

	public IntBuffer getIndicesBuffer() {
		final IntBuffer buffer = BufferUtils.createIntBuffer(indices.size());
		buffer.put(indices.toArray());
		buffer.flip();
		return buffer;
	}

	public int addByteAttribute(String name, int size) {
		return addRawAttribute(VertexAttribute.createByteAttribute(name, size));
	}

	public int addShortAttribute(String name, int size) {
		return addRawAttribute(VertexAttribute.createShortAttribute(name, size));
	}

	public int addIntAttribute(String name, int size) {
		return addRawAttribute(VertexAttribute.createIntAttribute(name, size));
	}

	public int addFloatAttribute(String name, int size) {
		return addRawAttribute(VertexAttribute.createFloatAttribute(name, size));
	}

	public int addDoubleAttribute(String name, int size) {
		return addRawAttribute(VertexAttribute.createDoubleAttribute(name, size));
	}

	private int addRawAttribute(VertexAttribute attribute) {
		attributes.put(index, attribute);
		nameToIndex.put(attribute.getName(), index);
		return index++;
	}

	public int getAttributeIndex(String name) {
		final int index = nameToIndex.get(name);
		if (index == -1) {
			throw new IllegalArgumentException("No attribute for name: " + name);
		}
		return index;
	}

	public TByteList getByteAttributeList(String name) {
		return getByteAttributeList(getAttributeIndex(name));
	}

	public TByteList getByteAttributeList(int index) {
		final Object attribute = getRawAttributeList(index);
		if (!(attribute instanceof TByteList)) {
			throw new IllegalArgumentException("Attribute at index '" + index + "' is not of \"byte\" type");
		}
		return (TByteList) attribute;
	}

	public TShortList getShortAttributeList(String name) {
		return getShortAttributeList(getAttributeIndex(name));
	}

	public TShortList getShortAttributeList(int index) {
		final Object attribute = getRawAttributeList(index);
		if (!(attribute instanceof TShortList)) {
			throw new IllegalArgumentException("Attribute at index '" + index + "' is not of \"short\" type");
		}
		return (TShortList) attribute;
	}

	public TIntList getIntAttributeList(String name) {
		return getIntAttributeList(getAttributeIndex(name));
	}

	public TIntList getIntAttributeList(int index) {
		final Object attribute = getRawAttributeList(index);
		if (!(attribute instanceof TIntList)) {
			throw new IllegalArgumentException("Attribute at index '" + index + "' is not of \"int\" type");
		}
		return (TIntList) attribute;
	}

	public TFloatList getFloatAttributeList(String name) {
		return getFloatAttributeList(getAttributeIndex(name));
	}

	public TFloatList getFloatAttributeList(int index) {
		final Object attribute = getRawAttributeList(index);
		if (!(attribute instanceof TFloatList)) {
			throw new IllegalArgumentException("Attribute at index '" + index + "' is not of \"float\" type");
		}
		return (TFloatList) attribute;
	}

	public TDoubleList getDoubleAttributeList(String name) {
		return getDoubleAttributeList(getAttributeIndex(name));
	}

	public TDoubleList getDoubleAttributeList(int index) {
		final Object attribute = getRawAttributeList(index);
		if (!(attribute instanceof TDoubleList)) {
			throw new IllegalArgumentException("Attribute at index '" + index + "' is not of \"double\" type");
		}
		return (TDoubleList) attribute;
	}

	private Object getRawAttributeList(int index) {
		return getAttribute(index).getList();
	}

	public boolean hasAttribute(String name) {
		return nameToIndex.containsKey(name);
	}

	public boolean hasAttribute(int index) {
		return attributes.containsKey(index);
	}

	public void removeAttribute(String name) {
		removeAttribute(getAttributeIndex(name));
	}

	public void removeAttribute(int index) {
		nameToIndex.remove(getAttributeName(index));
		attributes.remove(index);
		VertexAttribute attribute = attributes.remove(this.index - 1);
		for (int i = this.index - 2; i >= index; i--) {
			attribute = attributes.put(i, attribute);
		}
		nameToIndex.transformValues(DECREMENT);
		this.index--;
	}

	public int getAttributeSize(String name) {
		return getAttributeSize(getAttributeIndex(name));
	}

	public int getAttributeSize(int index) {
		return getAttribute(index).getSize();
	}

	public DataType getAttributeType(String name) {
		return getAttributeType(getAttributeIndex(name));
	}

	public DataType getAttributeType(int index) {
		return getAttribute(index).getType();
	}

	public String getAttributeName(int index) {
		return getAttribute(index).getName();
	}

	public int getAttributeCount() {
		return index;
	}

	public int getLastAttributeIndex() {
		return index - 1;
	}

	public Set<String> getAttributeNames() {
		return Collections.unmodifiableSet(nameToIndex.keySet());
	}

	public ByteBuffer getAttributeBuffer(String name) {
		return getAttributeBuffer(getAttributeIndex(name));
	}

	public ByteBuffer getAttributeBuffer(int index) {
		final Object attribute = getRawAttributeList(index);
		final ByteBuffer buffer;
		if (attribute instanceof TByteList) {
			final TByteList list = (TByteList) attribute;
			buffer = BufferUtils.createByteBuffer(list.size());
			buffer.put(list.toArray());
		} else if (attribute instanceof TShortList) {
			final TShortList list = (TShortList) attribute;
			buffer = BufferUtils.createByteBuffer(list.size() * 2);
			for (int i = 0; i < list.size(); i++) {
				buffer.putShort(list.get(i));
			}
		} else if (attribute instanceof TIntList) {
			final TIntList list = (TIntList) attribute;
			buffer = BufferUtils.createByteBuffer(list.size() * 4);
			for (int i = 0; i < list.size(); i++) {
				buffer.putInt(list.get(i));
			}
		} else if (attribute instanceof TFloatList) {
			final TFloatList list = (TFloatList) attribute;
			buffer = BufferUtils.createByteBuffer(list.size() * 4);
			for (int i = 0; i < list.size(); i++) {
				buffer.putFloat(list.get(i));
			}
		} else if (attribute instanceof TDoubleList) {
			final TDoubleList list = (TDoubleList) attribute;
			buffer = BufferUtils.createByteBuffer(list.size() * 8);
			for (int i = 0; i < list.size(); i++) {
				buffer.putDouble(list.get(i));
			}
		} else {
			throw new IllegalStateException("Unknown attribute data type");
		}
		return (ByteBuffer) buffer.flip();
	}

	public void clear() {
		for (Object attribute : attributes.valueCollection()) {
			if (attribute instanceof TByteList) {
				((TByteList) attribute).clear();
			} else if (attribute instanceof TShortList) {
				((TShortList) attribute).clear();
			} else if (attribute instanceof TIntList) {
				((TIntList) attribute).clear();
			} else if (attribute instanceof TFloatList) {
				((TFloatList) attribute).clear();
			} else if (attribute instanceof TDoubleList) {
				((TDoubleList) attribute).clear();
			}
		}
	}

	private VertexAttribute getAttribute(int index) {
		if (index >= this.index) {
			throw new IllegalArgumentException("No attribute at index: " + index);
		}
		return attributes.get(index);
	}

	private static class VertexAttribute {
		private final String name;
		private final Object list;
		private final DataType type;
		private final int size;

		private VertexAttribute(String name, Object list, DataType type, int size) {
			this.name = name;
			this.list = list;
			this.type = type;
			this.size = size;
		}

		private String getName() {
			return name;
		}

		private Object getList() {
			return list;
		}

		private DataType getType() {
			return type;
		}

		private int getSize() {
			return size;
		}

		private static VertexAttribute createByteAttribute(String name, int size) {
			return new VertexAttribute(name, new TByteArrayList(), DataType.BYTE, size);
		}

		private static VertexAttribute createShortAttribute(String name, int size) {
			return new VertexAttribute(name, new TShortArrayList(), DataType.SHORT, size);
		}

		private static VertexAttribute createIntAttribute(String name, int size) {
			return new VertexAttribute(name, new TIntArrayList(), DataType.INT, size);
		}

		private static VertexAttribute createFloatAttribute(String name, int size) {
			return new VertexAttribute(name, new TFloatArrayList(), DataType.FLOAT, size);
		}

		private static VertexAttribute createDoubleAttribute(String name, int size) {
			return new VertexAttribute(name, new TDoubleArrayList(), DataType.DOUBLE, size);
		}
	}

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

		public int getGLConstant() {
			return glConstant;
		}

		public int getByteSize() {
			return byteSize;
		}
	}
}
