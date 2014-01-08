/*
 * This file is part of Caustic, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
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
package org.spout.renderer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.spout.math.vector.Vector2f;
import org.spout.math.vector.Vector3f;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexData;

/**
 * Generates various shape meshes of the desired size and stores them to the models.
 */
public class MeshGenerator {
	/*
    ^
	| y
	|
	|     x
	------->
	\
	 \
	  \ z
	   V
	The axis system
	 */

	/**
	 * Generates a crosshairs shaped wireframe in 3D. The center is at the intersection point of the three lines.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param length The length for the three lines
	 * @return The vertex data
	 */
	public static VertexData generateCrosshairs(VertexData destination, float length) {
		/*
		  \ |
		   \|
		----O-----
		    |\
		    | \
		 */
		// Model data buffers
		if (destination == null) {
			destination = new VertexData();
		}
		final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		destination.addAttribute(0, positionsAttribute);
		final TFloatList positions = new TFloatArrayList();
		final TIntList indices = destination.getIndices();
		length /= 2;
		// Add the x axis line
		addAll(positions, -length, 0, 0, length, 0, 0);
		addAll(indices, 0, 1);
		// Add the y axis line
		addAll(positions, 0, -length, 0, 0, length, 0);
		addAll(indices, 2, 3);
		// Add the z axis line
		addAll(positions, 0, 0, -length, 0, 0, length);
		addAll(indices, 4, 5);
		// Put the mesh in the vertex data
		positionsAttribute.setData(positions);
		return destination;
	}

	/**
	 * Generates a cuboid shaped wireframe (the outline of the cuboid). The center is at the middle of the cuboid.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param size The size of the cuboid to generate, on x, y and z
	 * @return The vertex data
	 */
	public static VertexData generateWireCuboid(VertexData destination, Vector3f size) {
		/*
		4------5
		|\     |\
		| 7------6
		| |    | |
		0-|----1 |
		 \|     \|
		  3------2
		 */
		// Corner positions
		final Vector3f p = size.div(2);
		final Vector3f p6 = new Vector3f(p.getX(), p.getY(), p.getZ());
		final Vector3f p0 = p6.negate();
		final Vector3f p7 = new Vector3f(-p.getX(), p.getY(), p.getZ());
		final Vector3f p1 = p7.negate();
		final Vector3f p4 = new Vector3f(-p.getX(), p.getY(), -p.getZ());
		final Vector3f p2 = p4.negate();
		final Vector3f p5 = new Vector3f(p.getX(), p.getY(), -p.getZ());
		final Vector3f p3 = p5.negate();
		// Model data buffers
		if (destination == null) {
			destination = new VertexData();
		}
		final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		destination.addAttribute(0, positionsAttribute);
		final TFloatList positions = new TFloatArrayList();
		final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
		destination.addAttribute(1, normalsAttribute);
		final TFloatList normals = new TFloatArrayList();
		final TIntList indices = destination.getIndices();
		// Add all of the corners
		addVector(positions, p0);
		addVector(normals, p0.normalize());
		addVector(positions, p1);
		addVector(normals, p1.normalize());
		addVector(positions, p2);
		addVector(normals, p2.normalize());
		addVector(positions, p3);
		addVector(normals, p3.normalize());
		addVector(positions, p4);
		addVector(normals, p4.normalize());
		addVector(positions, p5);
		addVector(normals, p5.normalize());
		addVector(positions, p6);
		addVector(normals, p6.normalize());
		addVector(positions, p7);
		addVector(normals, p7.normalize());
		// Face x
		addAll(indices, 1, 2, 2, 6, 6, 5, 5, 1);
		// Face y
		addAll(indices, 4, 5, 5, 6, 6, 7, 7, 4);
		// Face z
		addAll(indices, 2, 3, 3, 7, 7, 6, 6, 2);
		// Face -x
		addAll(indices, 0, 3, 3, 7, 7, 4, 4, 0);
		// Face -y
		addAll(indices, 0, 1, 1, 2, 2, 3, 3, 0);
		// Face -z
		addAll(indices, 0, 1, 1, 5, 5, 4, 4, 0);
		// Put the mesh in the vertex data
		positionsAttribute.setData(positions);
		normalsAttribute.setData(normals);
		return destination;
	}

	/**
	 * Generates a plane on xy. The center is at the middle of the plane.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param size The size of the plane to generate, on x and y
	 * @return The vertex data
	 */
	public static VertexData generatePlane(VertexData destination, Vector2f size) {
		/*
		2-----3
		|     |
		|     |
		0-----1
		 */
		// Corner positions
		final Vector2f p = size.div(2);
		final Vector3f p3 = new Vector3f(p.getX(), p.getY(), 0);
		final Vector3f p2 = new Vector3f(-p.getX(), p.getY(), 0);
		final Vector3f p1 = new Vector3f(p.getX(), -p.getY(), 0);
		final Vector3f p0 = new Vector3f(-p.getX(), -p.getY(), 0);
		// Normal
		final Vector3f n = new Vector3f(0, 0, 1);
		// Model data buffers
		if (destination == null) {
			destination = new VertexData();
		}
		final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		destination.addAttribute(0, positionsAttribute);
		final TFloatList positions = new TFloatArrayList();
		final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
		destination.addAttribute(1, normalsAttribute);
		final TFloatList normals = new TFloatArrayList();
		final TIntList indices = destination.getIndices();
		// Face
		addVector(positions, p0);
		addVector(normals, n);
		addVector(positions, p1);
		addVector(normals, n);
		addVector(positions, p2);
		addVector(normals, n);
		addVector(positions, p3);
		addVector(normals, n);
		addAll(indices, 0, 3, 2, 0, 1, 3);
		// Put the mesh in the vertex data
		positionsAttribute.setData(positions);
		normalsAttribute.setData(normals);
		return destination;
	}

	/**
	 * Generates a textured plane on xy. The center is at the middle of the plane.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param size The size of the plane to generate, on x and y
	 * @return The vertex data
	 */
	public static VertexData generateTexturedPlane(VertexData destination, Vector2f size) {
		destination = generatePlane(destination, size);
		final VertexAttribute textureAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, 2);
		destination.addAttribute(2, textureAttribute);
		final TFloatList texture = new TFloatArrayList();
		// Face
		addAll(texture, 0, 0);
		addAll(texture, 1, 0);
		addAll(texture, 0, 1);
		addAll(texture, 1, 1);
		// Put the mesh in the vertex data
		textureAttribute.setData(texture);
		return destination;
	}

	/**
	 * Generates a solid cuboid mesh. This mesh includes the positions, normals, texture coords and tangents. The center is at the middle of the cuboid.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param size The size of the cuboid to generate, on x, y and z
	 * @return The vertex data
	 */
	public static VertexData generateCuboid(VertexData destination, Vector3f size) {
		/*
		4------5
		|\     |\
		| 7------6
		| |    | |
		0-|----1 |
		 \|     \|
		  3------2
		 */
		// Corner positions
		final Vector3f p = size.div(2);
		final Vector3f p6 = new Vector3f(p.getX(), p.getY(), p.getZ());
		final Vector3f p0 = p6.negate();
		final Vector3f p7 = new Vector3f(-p.getX(), p.getY(), p.getZ());
		final Vector3f p1 = p7.negate();
		final Vector3f p4 = new Vector3f(-p.getX(), p.getY(), -p.getZ());
		final Vector3f p2 = p4.negate();
		final Vector3f p5 = new Vector3f(p.getX(), p.getY(), -p.getZ());
		final Vector3f p3 = p5.negate();
		// Face normals
		final Vector3f nx = new Vector3f(1, 0, 0);
		final Vector3f ny = new Vector3f(0, 1, 0);
		final Vector3f nz = new Vector3f(0, 0, 1);
		final Vector3f nxN = new Vector3f(-1, 0, 0);
		final Vector3f nyN = new Vector3f(0, -1, 0);
		final Vector3f nzN = new Vector3f(0, 0, -1);
		// Create a destination if missing
		if (destination == null) {
			destination = new VertexData();
		}
		// Positions
		final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		destination.addAttribute(0, positionsAttribute);
		final TFloatList positions = new TFloatArrayList();
		// Normals
		final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
		destination.addAttribute(1, normalsAttribute);
		final TFloatList normals = new TFloatArrayList();
		final TIntList indices = destination.getIndices();
		// Texture coords
		final VertexAttribute textureAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, 2);
		destination.addAttribute(2, textureAttribute);
		final TFloatList textures = new TFloatArrayList();
		// Tangents
		final VertexAttribute tangentAttribute = new VertexAttribute("tangents", DataType.FLOAT, 4);
		destination.addAttribute(3, tangentAttribute);
		final TFloatList tangents = new TFloatArrayList();
		// Face x
		addVector(positions, p2);
		addVector(normals, nx);
		addAll(textures, 0, 0);
		addVector(positions, p6);
		addVector(normals, nx);
		addAll(textures, 0, 1);
		addVector(positions, p5);
		addVector(normals, nx);
		addAll(textures, 1, 1);
		addVector(positions, p1);
		addVector(normals, nx);
		addAll(textures, 1, 0);
		addAll(indices, 0, 2, 1, 0, 3, 2);
		// Face y
		addVector(positions, p4);
		addVector(normals, ny);
		addAll(textures, 0, 0);
		addVector(positions, p5);
		addVector(normals, ny);
		addAll(textures, 0, 1);
		addVector(positions, p6);
		addVector(normals, ny);
		addAll(textures, 1, 1);
		addVector(positions, p7);
		addVector(normals, ny);
		addAll(textures, 1, 0);
		addAll(indices, 4, 6, 5, 4, 7, 6);
		// Face z
		addVector(positions, p3);
		addVector(normals, nz);
		addAll(textures, 0, 0);
		addVector(positions, p7);
		addVector(normals, nz);
		addAll(textures, 0, 1);
		addVector(positions, p6);
		addVector(normals, nz);
		addAll(textures, 1, 1);
		addVector(positions, p2);
		addVector(normals, nz);
		addAll(textures, 1, 0);
		addAll(indices, 8, 10, 9, 8, 11, 10);
		// Face -x
		addVector(positions, p0);
		addVector(normals, nxN);
		addAll(textures, 0, 0);
		addVector(positions, p4);
		addVector(normals, nxN);
		addAll(textures, 0, 1);
		addVector(positions, p7);
		addVector(normals, nxN);
		addAll(textures, 1, 1);
		addVector(positions, p3);
		addVector(normals, nxN);
		addAll(textures, 1, 0);
		addAll(indices, 12, 14, 13, 12, 15, 14);
		// Face -y
		addVector(positions, p0);
		addVector(normals, nyN);
		addAll(textures, 0, 0);
		addVector(positions, p3);
		addVector(normals, nyN);
		addAll(textures, 0, 1);
		addVector(positions, p2);
		addVector(normals, nyN);
		addAll(textures, 1, 1);
		addVector(positions, p1);
		addVector(normals, nyN);
		addAll(textures, 1, 0);
		addAll(indices, 16, 18, 17, 16, 19, 18);
		// Face -z
		addVector(positions, p1);
		addVector(normals, nzN);
		addAll(textures, 0, 0);
		addVector(positions, p5);
		addVector(normals, nzN);
		addAll(textures, 0, 1);
		addVector(positions, p4);
		addVector(normals, nzN);
		addAll(textures, 1, 1);
		addVector(positions, p0);
		addVector(normals, nzN);
		addAll(textures, 1, 0);
		addAll(indices, 20, 22, 21, 20, 23, 22);
		// Automatically generate the tangents
		CausticUtil.generateTangents(positions, normals, textures, indices, tangents);
		// Put the mesh in the vertex data
		positionsAttribute.setData(positions);
		normalsAttribute.setData(normals);
		textureAttribute.setData(textures);
		tangentAttribute.setData(tangents);
		return destination;
	}

	/**
	 * Generates a solid spherical mesh. The center is at the middle of the sphere.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param radius The radius of the sphere
	 * @return The vertex data
	 */
	public static VertexData generateSphere(VertexData destination, float radius) {
		// Octahedron positions
		final Vector3f v0 = new Vector3f(0.0f, -1.0f, 0.0f);
		final Vector3f v1 = new Vector3f(1.0f, 0.0f, 0.0f);
		final Vector3f v2 = new Vector3f(0.0f, 0.0f, 1.0f);
		final Vector3f v3 = new Vector3f(-1.0f, 0.0f, 0.0f);
		final Vector3f v4 = new Vector3f(0.0f, 0.0f, -1.0f);
		final Vector3f v5 = new Vector3f(0.0f, 1.0f, 0.0f);
		// Build a list of triangles
		final List<Triangle> triangles = new ArrayList<>();
		triangles.addAll(Arrays.asList(
				new Triangle(v0, v1, v2),
				new Triangle(v0, v2, v3),
				new Triangle(v0, v3, v4),
				new Triangle(v0, v4, v1),
				new Triangle(v1, v5, v2),
				new Triangle(v2, v5, v3),
				new Triangle(v3, v5, v4),
				new Triangle(v4, v5, v1)));
		// List to store the subdivided triangles
		final List<Triangle> newTriangles = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			// Subdivide all of the triangles by splitting the edges
			for (Triangle triangle : triangles) {
				newTriangles.addAll(Arrays.asList(subdivide(triangle)));
			}
			// Store the new triangles in the main list
			triangles.clear();
			triangles.addAll(newTriangles);
			// Clear the new triangles for the next run
			newTriangles.clear();
		}
		// Normalize the positions so they are all the same distance from the center
		// then scale them to the appropriate radius
		for (Triangle triangle : triangles) {
			triangle.getV0().normalize().mul(radius);
			triangle.getV1().normalize().mul(radius);
			triangle.getV2().normalize().mul(radius);
		}
		// Model data buffers
		if (destination == null) {
			destination = new VertexData();
		}
		final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		destination.addAttribute(0, positionsAttribute);
		final TFloatList positions = new TFloatArrayList();
		final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
		destination.addAttribute(1, normalsAttribute);
		final TFloatList normals = new TFloatArrayList();
		final TIntList indices = destination.getIndices();
		// Add the triangle faces to the data buffers
		int index = 0;
		// Keep track of already added vertices, so we can reuse them for a smaller mesh
		final TObjectIntMap<Vector3f> addedVertices = new TObjectIntHashMap<>();
		for (Triangle triangle : triangles) {
			final Vector3f vt0 = triangle.getV0();
			final Vector3f vt1 = triangle.getV1();
			final Vector3f vt2 = triangle.getV2();
			if (addedVertices.containsKey(vt0)) {
				addAll(indices, addedVertices.get(vt0));
			} else {
				addVector(positions, vt0);
				addVector(normals, vt0.normalize());
				addedVertices.put(vt0, index);
				addAll(indices, index++);
			}
			if (addedVertices.containsKey(vt1)) {
				addAll(indices, addedVertices.get(vt1));
			} else {
				addVector(positions, vt1);
				addVector(normals, vt1.normalize());
				addedVertices.put(vt1, index);
				addAll(indices, index++);
			}
			if (addedVertices.containsKey(vt2)) {
				addAll(indices, addedVertices.get(vt2));
			} else {
				addVector(positions, vt2);
				addVector(normals, vt2.normalize());
				addedVertices.put(vt2, index);
				addAll(indices, index++);
			}
		}
		// Put the mesh in the vertex data
		positionsAttribute.setData(positions);
		normalsAttribute.setData(normals);
		return destination;
	}

	/**
	 * Generates a cylindrical solid mesh. The center is at middle of the of the cylinder.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param radius The radius of the base and top
	 * @param height The height (distance from the base to the top)
	 * @return The vertex data
	 */
	public static VertexData generateCylinder(VertexData destination, float radius, float height) {
		// 0,0,0 will be halfway up the cylinder in the middle
		final float halfHeight = height / 2;
		// The positions at the rims of the cylinders
		final List<Vector3f> rims = new ArrayList<>();
		for (int angle = 0; angle < 360; angle += 15) {
			final double angleRads = Math.toRadians(angle);
			rims.add(new Vector3f(
					radius * (float) Math.cos(angleRads),
					halfHeight,
					radius * (float) -Math.sin(angleRads)));
		}
		// Model data buffers
		if (destination == null) {
			destination = new VertexData();
		}
		final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		destination.addAttribute(0, positionsAttribute);
		final TFloatList positions = new TFloatArrayList();
		final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
		destination.addAttribute(1, normalsAttribute);
		final TFloatList normals = new TFloatArrayList();
		final TIntList indices = destination.getIndices();
		// The normals for the triangles of the top and bottom faces
		final Vector3f topNormal = new Vector3f(0, 1, 0);
		final Vector3f bottomNormal = new Vector3f(0, -1, 0);
		// Add the top and bottom face center vertices
		addVector(positions, new Vector3f(0, halfHeight, 0));// 0
		addVector(normals, topNormal);
		addVector(positions, new Vector3f(0, -halfHeight, 0));// 1
		addVector(normals, bottomNormal);
		// Add all the faces section by section, turning around the y axis
		final int rimsSize = rims.size();
		for (int i = 0; i < rimsSize; i++) {
			// Get the top and bottom vertex positions and the side normal
			final Vector3f t = rims.get(i);
			final Vector3f b = new Vector3f(t.getX(), -t.getY(), t.getZ());
			final Vector3f n = new Vector3f(t.getX(), 0, t.getZ()).normalize();
			// Top face vertex
			addVector(positions, t);// index
			addVector(normals, topNormal);
			// Bottom face vertex
			addVector(positions, b);// index + 1
			addVector(normals, bottomNormal);
			// Side top vertex
			addVector(positions, t);// index + 2
			addVector(normals, n);
			// Side bottom vertex
			addVector(positions, b);// index + 3
			addVector(normals, n);
			// Get the current index for our vertices
			final int currentIndex = i * 4 + 2;
			// Get the index for the next iteration, wrapping around at the end
			final int nextIndex = (i == rimsSize - 1 ? 0 : i + 1) * 4 + 2;
			// Add the 4 triangles (1 top, 1 bottom, 2 for the side)
			addAll(indices, 0, currentIndex, nextIndex);
			addAll(indices, 1, nextIndex + 1, currentIndex + 1);
			addAll(indices, currentIndex + 2, currentIndex + 3, nextIndex + 2);
			addAll(indices, currentIndex + 3, nextIndex + 3, nextIndex + 2);
		}
		// Put the mesh in the vertex data
		positionsAttribute.setData(positions);
		normalsAttribute.setData(normals);
		return destination;
	}

	/**
	 * Generates a conical solid mesh. The center is at the middle of the cone.
	 *
	 * @param destination Where to save the mesh (can be null)
	 * @param radius The radius of the base
	 * @param height The height (distance from the base to the apex)
	 * @return The vertex data
	 */
	public static VertexData generateCone(VertexData destination, float radius, float height) {
		// 0,0,0 will be halfway up the cone in the middle
		final float halfHeight = height / 2;
		// The positions at the bottom rim of the cone
		final List<Vector3f> rim = new ArrayList<>();
		for (int angle = 0; angle < 360; angle += 15) {
			final double angleRads = Math.toRadians(angle);
			rim.add(new Vector3f(
					radius * (float) Math.cos(angleRads),
					-halfHeight,
					radius * (float) -Math.sin(angleRads)));
		}
		// Model data buffers
		if (destination == null) {
			destination = new VertexData();
		}
		final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
		destination.addAttribute(0, positionsAttribute);
		final TFloatList positions = new TFloatArrayList();
		final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
		destination.addAttribute(1, normalsAttribute);
		final TFloatList normals = new TFloatArrayList();
		final TIntList indices = destination.getIndices();
		// Apex of the cone
		final Vector3f top = new Vector3f(0, halfHeight, 0);
		// The normal for the triangle of the bottom face
		final Vector3f bottomNormal = new Vector3f(0, -1, 0);
		// Add the bottom face center vertex
		addVector(positions, new Vector3f(0, -halfHeight, 0));// 0
		addVector(normals, bottomNormal);
		// Add all the faces section by section, turning around the y axis
		final int rimSize = rim.size();
		for (int i = 0; i < rimSize; i++) {
			// Get the bottom vertex position and the side normal
			final Vector3f b = rim.get(i);
			final Vector3f bn = new Vector3f(b.getX(), 0, b.getZ()).normalize();
			// Average the current and next normal to get the top normal
			final int nextI = i == rimSize - 1 ? 0 : i + 1;
			final Vector3f nextB = rim.get(nextI);
			final Vector3f tn = mean(bn, new Vector3f(nextB.getX(), 0, nextB.getZ()).normalize());
			// Top side vertex
			addVector(positions, top);// index
			addVector(normals, tn);
			// Bottom side vertex
			addVector(positions, b);// index + 1
			addVector(normals, bn);
			// Bottom face vertex
			addVector(positions, b);// index + 2
			addVector(normals, bottomNormal);
			// Get the current index for our vertices
			final int currentIndex = i * 3 + 1;
			// Get the index for the next iteration, wrapping around at the end
			final int nextIndex = nextI * 3 + 1;
			// Add the 2 triangles (1 side, 1 bottom)
			addAll(indices, currentIndex, currentIndex + 1, nextIndex + 1);
			addAll(indices, currentIndex + 2, 0, nextIndex + 2);
		}
		// Put the mesh in the vertex data
		positionsAttribute.setData(positions);
		normalsAttribute.setData(normals);
		return destination;
	}

	private static Vector3f mean(Vector3f v0, Vector3f v1) {
		return new Vector3f(
				(v0.getX() + v1.getX()) / 2,
				(v0.getY() + v1.getY()) / 2,
				(v0.getZ() + v1.getZ()) / 2);
	}

	private static void addVector(TFloatList to, Vector3f v) {
		to.add(v.getX());
		to.add(v.getY());
		to.add(v.getZ());
	}

	private static void addAll(TIntList to, int... f) {
		to.add(f);
	}

	private static void addAll(TFloatList to, float... f) {
		to.add(f);
	}

	private static Triangle[] subdivide(Triangle triangle) {
		final Vector3f e0 = triangle.v1.sub(triangle.v0).div(2);
		final Vector3f va = triangle.v0.add(e0);
		final Vector3f e1 = triangle.v2.sub(triangle.v1).div(2);
		final Vector3f vb = triangle.v1.add(e1);
		final Vector3f e2 = triangle.v0.sub(triangle.v2).div(2);
		final Vector3f vc = triangle.v2.add(e2);
		return new Triangle[]{
				new Triangle(triangle.v0, va, vc),
				new Triangle(va, triangle.v1, vb),
				new Triangle(vc, vb, triangle.v2),
				new Triangle(va, vb, vc)
		};
	}

	private static class Triangle {
		private Vector3f v0;
		private Vector3f v1;
		private Vector3f v2;

		private Triangle(Vector3f v0, Vector3f v1, Vector3f v2) {
			this.v0 = v0;
			this.v1 = v1;
			this.v2 = v2;
		}

		private Vector3f getV0() {
			return v0;
		}

		private Vector3f getV1() {
			return v1;
		}

		private Vector3f getV2() {
			return v2;
		}
	}
}
