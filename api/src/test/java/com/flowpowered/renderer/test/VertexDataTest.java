/*
 * This file is part of Caustic API, licensed under the MIT License (MIT).
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
package com.flowpowered.caustic.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gnu.trove.list.TIntList;

import org.junit.Assert;
import org.junit.Test;

import com.flowpowered.caustic.api.data.VertexAttribute;
import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.data.VertexAttribute.UploadMode;
import com.flowpowered.caustic.api.data.VertexData;

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
        vertexData.addAttribute(5, new VertexAttribute("unsigned_byte", DataType.UNSIGNED_BYTE, 6, UploadMode.TO_FLOAT));
        vertexData.addAttribute(6, new VertexAttribute("unsigned_short", DataType.UNSIGNED_SHORT, 7, UploadMode.TO_FLOAT));
        vertexData.addAttribute(7, new VertexAttribute("unsigned_int", DataType.UNSIGNED_INT, 8, UploadMode.TO_FLOAT));
        // Get attribute
        //Byte Attribute
        VertexAttribute byteAttribute;
        byteAttribute = vertexData.getAttribute(0);
        Assert.assertNotNull(byteAttribute);
        byteAttribute = vertexData.getAttribute("byte");
        Assert.assertNotNull(byteAttribute);
        //Short Attribute
        VertexAttribute shortAttribute;
        shortAttribute = vertexData.getAttribute(1);
        Assert.assertNotNull(shortAttribute);
        shortAttribute = vertexData.getAttribute("short");
        Assert.assertNotNull(shortAttribute);
        //Int Attribute
        VertexAttribute intAttribute;
        intAttribute = vertexData.getAttribute(2);
        Assert.assertNotNull(intAttribute);
        intAttribute = vertexData.getAttribute("int");
        Assert.assertNotNull(intAttribute);
        //Float Attribute
        VertexAttribute floatAttribute;
        floatAttribute = vertexData.getAttribute(3);
        Assert.assertNotNull(floatAttribute);
        floatAttribute = vertexData.getAttribute("float");
        Assert.assertNotNull(floatAttribute);
        //Double Attribute
        VertexAttribute doubleAttribute;
        doubleAttribute = vertexData.getAttribute(4);
        Assert.assertNotNull(doubleAttribute);
        doubleAttribute = vertexData.getAttribute("double");
        Assert.assertNotNull(doubleAttribute);
        //Unsigned Byte Attribute
        VertexAttribute uByteAttribute;
        uByteAttribute = vertexData.getAttribute(5);
        Assert.assertNotNull(uByteAttribute);
        uByteAttribute = vertexData.getAttribute("unsigned_byte");
        Assert.assertNotNull(uByteAttribute);
        //Unsigned Short Attribute
        VertexAttribute uShortAttribute;
        uShortAttribute = vertexData.getAttribute(6);
        Assert.assertNotNull(uShortAttribute);
        uShortAttribute = vertexData.getAttribute("unsigned_short");
        Assert.assertNotNull(uShortAttribute);
        //Unsigned Int Attribute
        VertexAttribute uIntAttribute;
        uIntAttribute = vertexData.getAttribute(7);
        Assert.assertNotNull(uIntAttribute);
        uIntAttribute = vertexData.getAttribute("unsigned_int");
        Assert.assertNotNull(uIntAttribute);
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
        index = vertexData.getAttributeIndex("unsigned_byte");
        Assert.assertEquals(5, index);
        index = vertexData.getAttributeIndex("unsigned_short");
        Assert.assertEquals(6, index);
        index = vertexData.getAttributeIndex("unsigned_int");
        Assert.assertEquals(7, index);
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

        size = vertexData.getAttributeSize(5);
        Assert.assertEquals(6, size);
        size = vertexData.getAttributeSize("unsigned_byte");
        Assert.assertEquals(6, size);

        size = vertexData.getAttributeSize(6);
        Assert.assertEquals(7, size);
        size = vertexData.getAttributeSize("unsigned_short");
        Assert.assertEquals(7, size);

        size = vertexData.getAttributeSize(7);
        Assert.assertEquals(8, size);
        size = vertexData.getAttributeSize("unsigned_int");
        Assert.assertEquals(8, size);
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
        name = vertexData.getAttributeName(5);
        Assert.assertEquals("unsigned_byte", name);
        name = vertexData.getAttributeName(6);
        Assert.assertEquals("unsigned_short", name);
        name = vertexData.getAttributeName(7);
        Assert.assertEquals("unsigned_int", name);
        // Get attribute count
        int count = vertexData.getAttributeCount();
        Assert.assertEquals(8, count);
        // Get attribute names
        Set<String> names = vertexData.getAttributeNames();
        Assert.assertEquals(new HashSet<>(Arrays.asList("byte", "short", "int", "float", "double", "unsigned_byte", "unsigned_short", "unsigned_int")), names);
        // Remove attribute
        vertexData.removeAttribute("byte");
        vertexData.removeAttribute("short");
        vertexData.removeAttribute("int");
        vertexData.removeAttribute("float");
        vertexData.removeAttribute("double");
        vertexData.removeAttribute("unsigned_byte");
        vertexData.removeAttribute("unsigned_short");
        vertexData.removeAttribute("unsigned_int");
        count = vertexData.getAttributeCount();
        Assert.assertEquals(0, count);
    }
}
