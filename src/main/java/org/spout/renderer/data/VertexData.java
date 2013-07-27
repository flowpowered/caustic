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

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.lwjgl.BufferUtils;

import org.spout.renderer.data.VertexAttribute.ByteVertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexAttribute.DoubleVertexAttribute;
import org.spout.renderer.data.VertexAttribute.FloatVertexAttribute;
import org.spout.renderer.data.VertexAttribute.IntVertexAttribute;
import org.spout.renderer.data.VertexAttribute.ShortVertexAttribute;

/**
 * Represents a vertex data. A vertex is a collection of attributes, most often attached to a point
 * in space. This class is a data structure which groups together collections of primitives to
 * represent a list of vertices.
 */
public class VertexData {
	// Rendering indices
	private final TIntList indices = new TIntArrayList();
	// Attributes by index
	private final TIntObjectMap<VertexAttribute> attributes = new TIntObjectHashMap<>();
	// Index from name lookup
	private final TObjectIntMap<String> nameToIndex = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, -1);

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
	 * Adds an attribute.
	 *
	 * @param attribute The attribute to add
	 */
	public void addAttribute(int index, VertexAttribute attribute) {
		attributes.put(index, attribute);
		nameToIndex.put(attribute.getName(), index);
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
	 * Returns the byte attribute associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The byte attribute
	 */
	public ByteVertexAttribute getByteAttribute(String name) {
		return getByteAttribute(getAttributeIndex(name));
	}

	/**
	 * Returns the byte attribute at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The byte attribute
	 */
	public ByteVertexAttribute getByteAttribute(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (!(attribute instanceof ByteVertexAttribute)) {
			return null;
		}
		return (ByteVertexAttribute) attribute;
	}

	/**
	 * Returns the short list associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The short list
	 */
	public ShortVertexAttribute getShortAttribute(String name) {
		return getShortAttribute(getAttributeIndex(name));
	}

	/**
	 * Returns the short attribute at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The short attribute
	 */
	public ShortVertexAttribute getShortAttribute(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (!(attribute instanceof ShortVertexAttribute)) {
			return null;
		}
		return (ShortVertexAttribute) attribute;
	}

	/**
	 * Returns the int attribute associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The int attribute
	 */
	public IntVertexAttribute getIntAttribute(String name) {
		return getIntAttribute(getAttributeIndex(name));
	}

	/**
	 * Returns the int attribute at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The int attribute
	 */
	public IntVertexAttribute getIntAttribute(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (!(attribute instanceof IntVertexAttribute)) {
			return null;
		}
		return (IntVertexAttribute) attribute;
	}

	/**
	 * Returns the float attribute associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The float attribute
	 */
	public FloatVertexAttribute getFloatAttribute(String name) {
		return getFloatAttribute(getAttributeIndex(name));
	}

	/**
	 * Returns the float attribute at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The float attribute
	 */
	public FloatVertexAttribute getFloatAttribute(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (!(attribute instanceof FloatVertexAttribute)) {
			return null;
		}
		return (FloatVertexAttribute) attribute;
	}

	/**
	 * Returns the double attribute associated to the name, or null if none can be found.
	 *
	 * @param name The name to lookup
	 * @return The double attribute
	 */
	public DoubleVertexAttribute getDoubleAttribute(String name) {
		return getDoubleAttribute(getAttributeIndex(name));
	}

	/**
	 * Returns the double attribute at the index, or null if none can be found.
	 *
	 * @param index The index to lookup
	 * @return The double attribute
	 */
	public DoubleVertexAttribute getDoubleAttribute(int index) {
		final VertexAttribute attribute = getAttribute(index);
		if (!(attribute instanceof DoubleVertexAttribute)) {
			return null;
		}
		return (DoubleVertexAttribute) attribute;
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
		attributes.remove(index);
		nameToIndex.remove(getAttributeName(index));
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
		return attributes.size();
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
		attributes.clear();
		nameToIndex.clear();
	}

	/**
	 * Replaces the contents of this vertex data by the provided one. This is a deep copy. The vertex
	 * attribute are each individually cloned.
	 *
	 * @param data The data to copy.
	 */
	public void copy(VertexData data) {
		clear();
		indices.addAll(data.indices);
		final TIntObjectIterator<VertexAttribute> iterator = data.attributes.iterator();
		while (iterator.hasNext()) {
			iterator.advance();
			attributes.put(iterator.key(), iterator.value().clone());
		}
		nameToIndex.putAll(data.nameToIndex);
	}
}
