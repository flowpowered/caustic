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
package org.spout.renderer.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gnu.trove.list.TIntList;

import org.junit.Assert;
import org.junit.Test;

import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexAttribute.UploadMode;
import org.spout.renderer.data.VertexData;

public class VertexDataTest {
	@Test
	public void test() {
		VertexData vertexData = new VertexData();
		// Get Indices
		TIntList indices = vertexData.getIndices();
		Assert.assertNotNull(indices);
		// Add attribute
		vertexData.addAttribute(0, new VertexAttribute("byte", DataType.BYTE, 1, UploadMode.TO_FLOAT));
		vertexData.addAttribute(1, new VertexAttribute("short", DataType.SHORT, 2, UploadMode.TO_FLOAT));
		vertexData.addAttribute(2, new VertexAttribute("int", DataType.INT, 3, UploadMode.TO_FLOAT));
		vertexData.addAttribute(3, new VertexAttribute("float", DataType.FLOAT, 4, UploadMode.TO_FLOAT));
		vertexData.addAttribute(4, new VertexAttribute("double", DataType.DOUBLE, 5, UploadMode.TO_FLOAT));
		// Get attribute
		VertexAttribute byteAttribute;
		byteAttribute = vertexData.getAttribute(0);
		Assert.assertNotNull(byteAttribute);
		byteAttribute = vertexData.getAttribute("byte");
		Assert.assertNotNull(byteAttribute);
		VertexAttribute shortAttribute;
		shortAttribute = vertexData.getAttribute(1);
		Assert.assertNotNull(shortAttribute);
		shortAttribute = vertexData.getAttribute("short");
		Assert.assertNotNull(shortAttribute);
		VertexAttribute intAttribute;
		intAttribute = vertexData.getAttribute(2);
		Assert.assertNotNull(intAttribute);
		intAttribute = vertexData.getAttribute("int");
		Assert.assertNotNull(intAttribute);
		VertexAttribute floatAttribute;
		floatAttribute = vertexData.getAttribute(3);
		Assert.assertNotNull(floatAttribute);
		floatAttribute = vertexData.getAttribute("float");
		Assert.assertNotNull(floatAttribute);
		VertexAttribute doubleAttribute;
		doubleAttribute = vertexData.getAttribute(4);
		Assert.assertNotNull(doubleAttribute);
		doubleAttribute = vertexData.getAttribute("double");
		Assert.assertNotNull(doubleAttribute);
		// Get attribute index
		int index;
		index = vertexData.getAttributeIndex("byte");
		Assert.assertEquals(0, index);
		index = vertexData.getAttributeIndex("short");
		Assert.assertEquals(1, index);
		index = vertexData.getAttributeIndex("int");
		Assert.assertEquals(2, index);
		index = vertexData.getAttributeIndex("float");
		Assert.assertEquals(3, index);
		index = vertexData.getAttributeIndex("double");
		Assert.assertEquals(4, index);
		// Get attribute size
		int size;
		size = vertexData.getAttributeSize(0);
		Assert.assertEquals(1, size);
		size = vertexData.getAttributeSize("byte");
		Assert.assertEquals(1, size);
		size = vertexData.getAttributeSize(1);
		Assert.assertEquals(2, size);
		size = vertexData.getAttributeSize("short");
		Assert.assertEquals(2, size);
		size = vertexData.getAttributeSize(2);
		Assert.assertEquals(3, size);
		size = vertexData.getAttributeSize("int");
		Assert.assertEquals(3, size);
		size = vertexData.getAttributeSize(3);
		Assert.assertEquals(4, size);
		size = vertexData.getAttributeSize("float");
		Assert.assertEquals(4, size);
		size = vertexData.getAttributeSize(4);
		Assert.assertEquals(5, size);
		size = vertexData.getAttributeSize("double");
		Assert.assertEquals(5, size);
		// Get attribute name
		String name;
		name = vertexData.getAttributeName(0);
		Assert.assertEquals("byte", name);
		name = vertexData.getAttributeName(1);
		Assert.assertEquals("short", name);
		name = vertexData.getAttributeName(2);
		Assert.assertEquals("int", name);
		name = vertexData.getAttributeName(3);
		Assert.assertEquals("float", name);
		name = vertexData.getAttributeName(4);
		Assert.assertEquals("double", name);
		// Get attribute count
		int count = vertexData.getAttributeCount();
		Assert.assertEquals(5, count);
		// Get attribute names
		Set<String> names = vertexData.getAttributeNames();
		Assert.assertEquals(new HashSet<>(Arrays.asList("byte", "short", "int", "float", "double")), names);
		// Remove attribute
		vertexData.removeAttribute("byte");
		vertexData.removeAttribute("short");
		vertexData.removeAttribute("int");
		vertexData.removeAttribute("float");
		vertexData.removeAttribute("double");
		count = vertexData.getAttributeCount();
		Assert.assertEquals(0, count);
	}
}
