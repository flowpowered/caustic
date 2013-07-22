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

/**
 * Represents a vertex data. A vertex is a collection of attributes, most often attached to a point
 * in space. This class is a data structure which groups together collections of primitives to
 * represent a list of vertices.
 */
public class VertexData {
	private static final TIntFunction DECREMENT = new TIntFunction() {
		@Override
		public int execute(int value) {
			return value - 1;
		}
	};
	// Rendering indices
	private final TIntList indices = new TIntArrayList();
	// Attributes by index
	private final TIntObjectMap<VertexAttribute> attributes = new TIntObjectHashMap<>();
	// Index from name lookup
	private final TObjectIntMap<String> nameToIndex = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);
	// Next available attribute index
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

	/**
	 * Returns the index count.
	 *
	 * @return The number of indices
	 */
	public int getIndicesCount() {
		return indices.size();
	}

	/**
	 * Returns a byte buffer containing all the current indices.
	 *
	 * @return A buffer of the indices
	 */
	public ByteBuffer getIndicesBuffer() {
		final ByteBuffer buffer = BufferUtils.createByteBuffer(indices.size() * 4);
		for (int i = 0; i < indices.size(); i++) {
			buffer.putInt(indices.get(i));
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * Adds an attribute of the byte type. The attribute size defines the number of components. The
	 * index for the new attribute will be {@link #getLastAttributeIndex()} + 1.
	 *
	 * @param name The name of the attribute
	 * @param size The size in components
	 * @return The storage list for the attribute data
	 */
	public TByteList addByteAttribute(String name, int size) {
		final VertexAttribute attribute = VertexAttribute.createByteAttribute(name, size);
		addRawAttribute(attribute);
		return attribute.getByteList();
	}

	/**
	 * Adds an attribute of the short type. The attribute size defines the number of components. The
	 * index for the new attribute will be {@link #getLastAttributeIndex()} + 1.
	 *
	 * @param name The name of the attribute
	 * @param size The size in components
	 * @return The storage list for the attribute data
	 */
	public TShortList addShortAttribute(String name, int size) {
		final VertexAttribute attribute = VertexAttribute.createShortAttribute(name, size);
		addRawAttribute(attribute);
		return attribute.getShortList();
	}

	/**
	 * Adds an attribute of the int type. The attribute size defines the number of components. The
	 * index for the new attribute will be {@link #getLastAttributeIndex()} + 1.
	 *
	 * @param name The name of the attribute
	 * @param size The size in components
	 * @return The storage list for the attribute data
	 */
	public TIntList addIntAttribute(String name, int size) {
		final VertexAttribute attribute = VertexAttribute.createIntAttribute(name, size);
		addRawAttribute(attribute);
		return attribute.getIntList();
	}

	/**
	 * Adds an attribute of the float type. The attribute size defines the number of components. The
	 * index for the new attribute will be {@link #getLastAttributeIndex()} + 1.
	 *
	 * @param name The name of the attribute
	 * @param size The size in components
	 * @return The storage list for the attribute data
	 */
	public TFloatList addFloatAttribute(String name, int size) {
		final VertexAttribute attribute = VertexAttribute.createFloatAttribute(name, size);
		addRawAttribute(attribute);
		return attribute.getFloatList();
	}

	/**
	 * Adds an attribute of the double type. The attribute size defines the number of components. The
	 * index for the new attribute will be {@link #getLastAttributeIndex()} + 1.
	 *
	 * @param name The name of the attribute
	 * @param size The size in components
	 * @return The storage list for the attribute data
	 */
	public TDoubleList addDoubleAttribute(String name, int size) {
		final VertexAttribute attribute = VertexAttribute.createDoubleAttribute(name, size);
		addRawAttribute(attribute);
		return attribute.getDoubleList();
	}

	private void addRawAttribute(VertexAttribute attribute) {
		attributes.put(index, attribute);
		nameToIndex.put(attribute.getName(), index++);
	}

	/**
	 * Returns the {@link VertexAttribute} associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The attribute, or null if none is associated to the index.
	 */
	public VertexAttribute getAttribute(String name) {
		return getAttribute(getAttributeIndex(name));
	}

	/**
	 * Returns the {@link VertexAttribute} at the desired index, or null if none is associated to the
	 * index.
	 *
	 * @param index The index to lookup
	 * @return The attribute, or null if none is associated to the index.
	 */
	public VertexAttribute getAttribute(int index) {
		return attributes.get(index);
	}

	/**
	 * Returns the index associated to the attribute name, or -1 if no attribute has the name.
	 *
	 * @param name The name to lookup
	 * @return The index, or -1 if no attribute has the name
	 */
	public int getAttributeIndex(String name) {
		return nameToIndex.get(name);
	}

	/**
	 * Returns the byte list associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The byte list
	 * @throws IllegalStateException If the found attribute is not of byte type
	 */
	public TByteList getByteAttributeList(String name) {
		return getByteAttributeList(getAttributeIndex(name));
	}

	/**
	 * Returns the byte list at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The byte list
	 * @throws IllegalStateException If the found attribute is not of byte type
	 */
	public TByteList getByteAttributeList(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getByteList();
	}

	/**
	 * Returns the short list associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The short list
	 * @throws IllegalStateException If the found attribute is not of short type
	 */
	public TShortList getShortAttributeList(String name) {
		return getShortAttributeList(getAttributeIndex(name));
	}

	/**
	 * Returns the short list at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The short list
	 * @throws IllegalStateException If the found attribute is not of short type
	 */
	public TShortList getShortAttributeList(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getShortList();
	}

	/**
	 * Returns the int list associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The int list
	 * @throws IllegalStateException If the found attribute is not of int type
	 */
	public TIntList getIntAttributeList(String name) {
		return getIntAttributeList(getAttributeIndex(name));
	}

	/**
	 * Returns the int list at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The int list
	 * @throws IllegalStateException If the found attribute is not of int type
	 */
	public TIntList getIntAttributeList(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getIntList();
	}

	/**
	 * Returns the float list associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The float list
	 * @throws IllegalStateException If the found attribute is not of float type
	 */
	public TFloatList getFloatAttributeList(String name) {
		return getFloatAttributeList(getAttributeIndex(name));
	}

	/**
	 * Returns the float list at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The float list
	 * @throws IllegalStateException If the found attribute is not of float type
	 */
	public TFloatList getFloatAttributeList(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getFloatList();
	}

	/**
	 * Returns the double list associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The double list
	 * @throws IllegalStateException If the found attribute is not of double type
	 */
	public TDoubleList getDoubleAttributeList(String name) {
		return getDoubleAttributeList(getAttributeIndex(name));
	}

	/**
	 * Returns the double list at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The double list
	 * @throws IllegalStateException If the found attribute is not of double type
	 */
	public TDoubleList getDoubleAttributeList(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getDoubleList();
	}

	/**
	 * Returns true if an attribute has the provided name.
	 *
	 * @param name The name to lookup
	 * @return Whether or not an attribute possesses the name
	 */
	public boolean hasAttribute(String name) {
		return nameToIndex.containsKey(name);
	}

	/**
	 * Returns true in an attribute can be found at the provided index.
	 *
	 * @param index The index to lookup
	 * @return Whether or not an attribute is at the index
	 */
	public boolean hasAttribute(int index) {
		return attributes.containsKey(index);
	}

	/**
	 * Removes the attribute associated to the provided name. If no attribute is found, nothing will be
	 * removed.
	 *
	 * @param name The name of the attribute to remove
	 */
	public void removeAttribute(String name) {
		removeAttribute(getAttributeIndex(name));
	}

	/**
	 * Removes the attribute at the provided index. If no attribute is found, nothing will be removed.
	 *
	 * @param index The index of the attribute to remove
	 */
	public void removeAttribute(int index) {
		if (hasAttribute(index)) {
			nameToIndex.remove(getAttributeName(index));
			attributes.remove(index);
			VertexAttribute attribute = attributes.remove(this.index - 1);
			for (int i = this.index - 2; i >= index; i--) {
				attribute = attributes.put(i, attribute);
			}
			nameToIndex.transformValues(DECREMENT);
			this.index--;
		}
	}

	/**
	 * Returns the size of the attribute associated to the provided name.
	 *
	 * @param name The name to lookup
	 * @return The size of the attribute
	 */
	public int getAttributeSize(String name) {
		return getAttributeSize(getAttributeIndex(name));
	}

	/**
	 * Returns the size of the attribute at the provided index, or -1 if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The size of the attribute, or -1 if none can be found
	 */
	public int getAttributeSize(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return -1;
		}
		return attribute.getSize();
	}

	/**
	 * Returns the type of the attribute associated to the provided name, or null if none can be
	 * found.
	 *
	 * @param name The name to lookup
	 * @return The type of the attribute, or null if none can be found
	 */
	public DataType getAttributeType(String name) {
		return getAttributeType(getAttributeIndex(name));
	}

	/**
	 * Returns the type of the attribute at the provided index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The type of the attribute, or null if none can be found
	 */
	public DataType getAttributeType(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getType();
	}

	/**
	 * Returns the name of the attribute at the provided index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The name of the attribute, or null if none can be found
	 */
	public String getAttributeName(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getName();
	}

	/**
	 * Returns the attribute count.
	 *
	 * @return The number of attributes
	 */
	public int getAttributeCount() {
		return index;
	}

	/**
	 * Returns an unmodifiable set of all the attribute names.
	 *
	 * @return A set of all the attribute names
	 */
	public Set<String> getAttributeNames() {
		return Collections.unmodifiableSet(nameToIndex.keySet());
	}

	/**
	 * Returns the buffer for the attribute associated to the provided name, or null if none can be
	 * found. The buffer is returned filled and ready for reading.
	 *
	 * @param name The name to lookup
	 * @return The attribute buffer, filled and flipped
	 */
	public ByteBuffer getAttributeBuffer(String name) {
		return getAttributeBuffer(getAttributeIndex(name));
	}

	/**
	 * Returns the buffer for the attribute at the provided index, or null if none can be found. The
	 * buffer is returned filled and ready for reading.
	 *
	 * @param index The index to lookup
	 * @return The attribute buffer, filled and flipped
	 */
	public ByteBuffer getAttributeBuffer(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (attribute == null) {
			return null;
		}
		return attribute.getBuffer();
	}

	/**
	 * Clears all the vertex data.
	 */
	public void clear() {
		indices.clear();
		for (VertexAttribute attribute : attributes.valueCollection()) {
			attribute.clear();
		}
	}

	/**
	 * Represents a vertex attribute. This is a data list associated to a name, a data type, and a
	 * component count (size).
	 */
	public static class VertexAttribute {
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

		/**
		 * Returns the name of the attribute.
		 *
		 * @return The name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the data list as a byte list. This will fail if the data type isn't {@link
		 * DataType#BYTE}.
		 *
		 * @return The byte list
		 * @throws IllegalStateException If the data type isn't {@link DataType#BYTE}.
		 */
		public TByteList getByteList() {
			if (!(list instanceof TByteList)) {
				throw new IllegalStateException("Attribute at is not of \"byte\" type");
			}
			return (TByteList) list;
		}

		/**
		 * Returns the data list as a short list. This will fail if the data type isn't {@link
		 * DataType#SHORT}.
		 *
		 * @return The short list
		 * @throws IllegalStateException If the data type isn't {@link DataType#SHORT}.
		 */
		public TShortList getShortList() {
			if (!(list instanceof TShortList)) {
				throw new IllegalStateException("Attribute is not of \"short\" type");
			}
			return (TShortList) list;
		}

		/**
		 * Returns the data list as a int list. This will fail if the data type isn't {@link
		 * DataType#INT}.
		 *
		 * @return The int list
		 * @throws IllegalStateException If the data type isn't {@link DataType#INT}.
		 */
		public TIntList getIntList() {
			if (!(list instanceof TIntList)) {
				throw new IllegalStateException("Attribute is not of \"int\" type");
			}
			return (TIntList) list;
		}

		/**
		 * Returns the data list as a float list. This will fail if the data type isn't {@link
		 * DataType#FLOAT}.
		 *
		 * @return The float list
		 * @throws IllegalStateException If the data type isn't {@link DataType#FLOAT}.
		 */
		public TFloatList getFloatList() {
			if (!(list instanceof TFloatList)) {
				throw new IllegalStateException("Attribute is not of \"float\" type");
			}
			return (TFloatList) list;
		}

		/**
		 * Returns the data list as a double list. This will fail if the data type isn't {@link
		 * DataType#DOUBLE}.
		 *
		 * @return The double list
		 * @throws IllegalStateException If the data type isn't {@link DataType#DOUBLE}.
		 */
		public TDoubleList getDoubleList() {
			if (!(list instanceof TDoubleList)) {
				throw new IllegalStateException("Attribute is not of \"double\" type");
			}
			return (TDoubleList) list;
		}

		/**
		 * Returns the data type of this attribute.
		 *
		 * @return The data type
		 */
		public DataType getType() {
			return type;
		}

		/**
		 * Returns the size (component count) of this attribute.
		 *
		 * @return The size
		 */
		public int getSize() {
			return size;
		}

		/**
		 * Returns a byte buffer of the attribute data, filled, and ready for reading.
		 *
		 * @return A filled and flipped byte buffer of the attribute data
		 */
		public ByteBuffer getBuffer() {
			final ByteBuffer buffer;
			if (list instanceof TByteList) {
				final TByteList l = (TByteList) list;
				buffer = BufferUtils.createByteBuffer(l.size() * DataType.BYTE.getByteSize());
				buffer.put(l.toArray());
			} else if (list instanceof TShortList) {
				final TShortList l = (TShortList) list;
				buffer = BufferUtils.createByteBuffer(l.size() * DataType.SHORT.getByteSize());
				for (int i = 0; i < l.size(); i++) {
					buffer.putShort(l.get(i));
				}
			} else if (list instanceof TIntList) {
				final TIntList l = (TIntList) list;
				buffer = BufferUtils.createByteBuffer(l.size() * DataType.INT.getByteSize());
				for (int i = 0; i < l.size(); i++) {
					buffer.putInt(l.get(i));
				}
			} else if (list instanceof TFloatList) {
				final TFloatList l = (TFloatList) list;
				buffer = BufferUtils.createByteBuffer(l.size() * DataType.FLOAT.getByteSize());
				for (int i = 0; i < l.size(); i++) {
					buffer.putFloat(l.get(i));
				}
			} else if (list instanceof TDoubleList) {
				final TDoubleList l = (TDoubleList) list;
				buffer = BufferUtils.createByteBuffer(l.size() * DataType.DOUBLE.getByteSize());
				for (int i = 0; i < l.size(); i++) {
					buffer.putDouble(l.get(i));
				}
			} else {
				throw new IllegalStateException("Unknown attribute data type");
			}
			return (ByteBuffer) buffer.flip();
		}

		/**
		 * Clears all of the attribute data.
		 */
		public void clear() {
			if (list instanceof TByteList) {
				((TByteList) list).clear();
			} else if (list instanceof TShortList) {
				((TShortList) list).clear();
			} else if (list instanceof TIntList) {
				((TIntList) list).clear();
			} else if (list instanceof TFloatList) {
				((TFloatList) list).clear();
			} else if (list instanceof TDoubleList) {
				((TDoubleList) list).clear();
			}
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
