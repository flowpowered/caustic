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
package com.flowpowered.caustic.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.TrigMath;
import com.flowpowered.math.matrix.Matrix3f;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4i;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import com.flowpowered.caustic.api.data.VertexAttribute;
import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.data.VertexData;

/**
 * A utility class for generating and modifying meshes.
 */
public class MeshGenerator {
    private MeshGenerator() {
    }

    /*
     * ^
     * | y
     * |
     * |     x
     * ------->
     * \
     *  \
     *   \ z
     *    V
     * The axis system
     */

    /**
     * Builds the mesh information into a {@link com.flowpowered.caustic.api.data.VertexData} which can then be uploaded and rendered. The {@code sizes} parameter is used to control which information to
     * use and generate. The component represents the number of float components per vertex for the attribute.
     * <p/>
     * The position are added if the x component of {@code sizes} is non zero and list isn't null.
     * <p/>
     * The normals are added if the y component of {@code sizes} is non zero and the list isn't null. If the list is null but the component is non zero, they are generated based on the position
     * information if it's available (the list is not null) and then added. If the y component is zero, they are ignored. See {@link #generateNormals(gnu.trove.list.TFloatList,
     * gnu.trove.list.TIntList, gnu.trove.list.TFloatList)}.
     * <p/>
     * The texture coordinates are added if the z component of {@code sizes} is non zero and list isn't null.
     * <p/>
     * Tangents are generated from all the previous information if it's available (lists are not null) and the w component of {@code sizes} is non zero. See {@link
     * #generateTangents(gnu.trove.list.TFloatList, gnu.trove.list.TFloatList, gnu.trove.list.TFloatList, gnu.trove.list.TIntList, gnu.trove.list.TFloatList)}.
     * <p/>
     * Indices are always added and are required.
     *
     * @param sizes Each component represents the number of float components per vertex for the attribute, with x for positions, y for normals, z for texture coords, and w for tangents
     * @param positions The list of position data
     * @param normals The list of normal data
     * @param textureCoords The list of texture coordinate data
     * @param indices The list of indices
     * @return The vertex data
     */
    public static VertexData buildMesh(Vector4i sizes, TFloatList positions, TFloatList normals, TFloatList textureCoords, TIntList indices) {
        final VertexData vertexData = new VertexData();
        int index = 0;
        // Positions
        if (positions != null && sizes.getX() > 0) {
            final VertexAttribute positionAttribute = new VertexAttribute("positions", DataType.FLOAT, sizes.getX());
            positionAttribute.setData(positions);
            vertexData.addAttribute(index++, positionAttribute);
        }
        // Normals
        if (sizes.getY() > 0) {
            if (normals != null) {
                final VertexAttribute normalAttribute = new VertexAttribute("normals", DataType.FLOAT, sizes.getY());
                normalAttribute.setData(normals);
                vertexData.addAttribute(index++, normalAttribute);
            } else if (positions != null) {
                final VertexAttribute normalAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
                normals = new TFloatArrayList();
                generateNormals(positions, indices, normals);
                normalAttribute.setData(normals);
                vertexData.addAttribute(index++, normalAttribute);
            }
        }
        // Texture coordinates
        if (textureCoords != null && sizes.getZ() > 0) {
            final VertexAttribute textureCoordAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, sizes.getZ());
            textureCoordAttribute.setData(textureCoords);
            vertexData.addAttribute(index++, textureCoordAttribute);
        }
        // Tangents
        if (positions != null && textureCoords != null && normals != null && sizes.getW() > 0) {
            final VertexAttribute tangentAttribute = new VertexAttribute("tangents", DataType.FLOAT, 4);
            tangentAttribute.setData(generateTangents(positions, normals, textureCoords, indices));
            vertexData.addAttribute(index, tangentAttribute);
        }
        // Indices
        vertexData.getIndices().addAll(indices);
        return vertexData;
    }

    /**
     * Generate the normals for the positions, according to the indices. This assumes that the positions have 3 components, in the x, y, z order. The normals are stored as a 3 component vector, in the
     * x, y, z order.
     *
     * @param positions The position components
     * @param indices The indices
     * @return The normals
     */
    public static TFloatList generateNormals(TFloatList positions, TIntList indices) {
        final TFloatList normals = new TFloatArrayList();
        generateNormals(positions, indices, normals);
        return normals;
    }

    /**
     * Generate the normals for the positions, according to the indices. This assumes that the positions have 3 components, in the x, y, z order. The normals are stored as a 3 component vector, in the
     * x, y, z order.
     *
     * @param positions The position components
     * @param indices The indices
     * @param normals The list in which to store the normals
     */
    public static void generateNormals(TFloatList positions, TIntList indices, TFloatList normals) {
        // Initialize normals to (0, 0, 0)
        normals.fill(0, positions.size(), 0);
        // Iterate over the entire mesh
        for (int i = 0; i < indices.size(); i += 3) {
            // Triangle position indices
            final int pos0 = indices.get(i) * 3;
            final int pos1 = indices.get(i + 1) * 3;
            final int pos2 = indices.get(i + 2) * 3;
            // First triangle vertex position
            final float x0 = positions.get(pos0);
            final float y0 = positions.get(pos0 + 1);
            final float z0 = positions.get(pos0 + 2);
            // Second triangle vertex position
            final float x1 = positions.get(pos1);
            final float y1 = positions.get(pos1 + 1);
            final float z1 = positions.get(pos1 + 2);
            // Third triangle vertex position
            final float x2 = positions.get(pos2);
            final float y2 = positions.get(pos2 + 1);
            final float z2 = positions.get(pos2 + 2);
            // First edge position difference
            final float x10 = x1 - x0;
            final float y10 = y1 - y0;
            final float z10 = z1 - z0;
            // Second edge position difference
            final float x20 = x2 - x0;
            final float y20 = y2 - y0;
            final float z20 = z2 - z0;
            // Cross both edges to obtain the normal
            final float nx = y10 * z20 - z10 * y20;
            final float ny = z10 * x20 - x10 * z20;
            final float nz = x10 * y20 - y10 * x20;
            // Add the normal to the first vertex
            normals.set(pos0, normals.get(pos0) + nx);
            normals.set(pos0 + 1, normals.get(pos0 + 1) + ny);
            normals.set(pos0 + 2, normals.get(pos0 + 2) + nz);
            // Add the normal to the second vertex
            normals.set(pos1, normals.get(pos1) + nx);
            normals.set(pos1 + 1, normals.get(pos1 + 1) + ny);
            normals.set(pos1 + 2, normals.get(pos1 + 2) + nz);
            // Add the normal to the third vertex
            normals.set(pos2, normals.get(pos2) + nx);
            normals.set(pos2 + 1, normals.get(pos2 + 1) + ny);
            normals.set(pos2 + 2, normals.get(pos2 + 2) + nz);
        }
        // Iterate over all the normals
        for (int i = 0; i < indices.size(); i++) {
            // Index for the normal
            final int nor = indices.get(i) * 3;
            // Get the normal
            float nx = normals.get(nor);
            float ny = normals.get(nor + 1);
            float nz = normals.get(nor + 2);
            // Length of the normal
            final float l = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            // Normalize the normal
            nx /= l;
            ny /= l;
            nz /= l;
            // Update the normal
            normals.set(nor, nx);
            normals.set(nor + 1, ny);
            normals.set(nor + 2, nz);
        }
    }

    /**
     * Generate the tangents for the positions, normals and texture coords, according to the indices. This assumes that the positions and normals have 3 components, in the x, y, z order, and that the
     * texture coords have 2, in the u, v (or s, t) order. The tangents are stored as a 4 component vector, in the x, y, z, w order. The w component represents the handedness for the bi-tangent
     * computation, which must be computed with B = T_w * (N x T).
     *
     * @param positions The position components
     * @param normals The normal components
     * @param textures The texture coord components
     * @param indices The indices
     * @return The tangents
     */
    public static TFloatList generateTangents(TFloatList positions, TFloatList normals, TFloatList textures, TIntList indices) {
        final TFloatList tangents = new TFloatArrayList();
        generateTangents(positions, normals, textures, indices, tangents);
        return tangents;
    }

    /**
     * Generate the tangents for the positions, normals and texture coords, according to the indices. This assumes that the positions and normals have 3 components, in the x, y, z order, and that the
     * texture coords have 2, in the u, v (or s, t) order. The tangents are stored as a 4 component vector, in the x, y, z, w order. The w component represents the handedness for the bi-tangent
     * computation, which must be computed with B = T_w * (N x T).
     *
     * @param positions The position components
     * @param normals The normal components
     * @param textures The texture coord components
     * @param indices The indices
     * @param tangents The list in which to store the tangents
     */
    public static void generateTangents(TFloatList positions, TFloatList normals, TFloatList textures, TIntList indices, TFloatList tangents) {
        // Adapted from: http://www.terathon.com/code/tangent.html
        // Size of the tangent list (without the handedness value)
        final int size = normals.size();
        // Initialize all tangents to (0, 0, 0, 0)
        tangents.fill(0, size / 3 * 4, 0);
        // Storage for the derivatives in respect to u and v
        final float[] du = new float[size];
        final float[] dv = new float[size];
        // Iterate over the entire mesh
        for (int i = 0; i < indices.size(); i += 3) {
            // Triangle position indices
            final int pos0 = indices.get(i) * 3;
            final int pos1 = indices.get(i + 1) * 3;
            final int pos2 = indices.get(i + 2) * 3;
            // First triangle vertex position
            final float x0 = positions.get(pos0);
            final float y0 = positions.get(pos0 + 1);
            final float z0 = positions.get(pos0 + 2);
            // Second triangle vertex position
            final float x1 = positions.get(pos1);
            final float y1 = positions.get(pos1 + 1);
            final float z1 = positions.get(pos1 + 2);
            // Third triangle vertex position
            final float x2 = positions.get(pos2);
            final float y2 = positions.get(pos2 + 1);
            final float z2 = positions.get(pos2 + 2);
            // Triangle texture coord indices
            final int tex0 = indices.get(i) * 2;
            final int tex1 = indices.get(i + 1) * 2;
            final int tex2 = indices.get(i + 2) * 2;
            // First triangle vertex texture coord
            final float u0 = textures.get(tex0);
            final float v0 = textures.get(tex0 + 1);
            // Second triangle vertex texture coord
            final float u1 = textures.get(tex1);
            final float v1 = textures.get(tex1 + 1);
            // Third triangle vertex texture coord
            final float u2 = textures.get(tex2);
            final float v2 = textures.get(tex2 + 1);
            // First edge position difference
            final float x10 = x1 - x0;
            final float y10 = y1 - y0;
            final float z10 = z1 - z0;
            // Second edge position difference
            final float x20 = x2 - x0;
            final float y20 = y2 - y0;
            final float z20 = z2 - z0;
            // First edge texture coord difference
            final float u10 = u1 - u0;
            final float v10 = v1 - v0;
            // Second edge texture coord difference
            final float u20 = u2 - u0;
            final float v20 = v2 - v0;
            //  Coefficient for derivative calculation
            float r = 1 / (u10 * v20 - u20 * v10);
            // Derivative in respect to U
            final float dux = (v20 * x10 - v10 * x20) * r;
            final float duy = (v20 * y10 - v10 * y20) * r;
            final float duz = (v20 * z10 - v10 * z20) * r;
            // Derivative in respect to V
            final float dvx = (u10 * x20 - u20 * x10) * r;
            final float dvy = (u10 * y20 - u20 * y10) * r;
            final float dvz = (u10 * z20 - u20 * z10) * r;
            // Add the derivative in respect to U to the first vertex
            du[pos0] += dux;
            du[pos0 + 1] += duy;
            du[pos0 + 2] += duz;
            // Add the derivative in respect to U to the second vertex
            du[pos1] += dux;
            du[pos1 + 1] += duy;
            du[pos1 + 2] += duz;
            // Add the derivative in respect to U to the third vertex
            du[pos2] += dux;
            du[pos2 + 1] += duy;
            du[pos2 + 2] += duz;
            // Add the derivative in respect to V to the first vertex
            dv[pos0] += dvx;
            dv[pos0 + 1] += dvy;
            dv[pos0 + 2] += dvz;
            // Add the derivative in respect to V to the second vertex
            dv[pos1] += dvx;
            dv[pos1 + 1] += dvy;
            dv[pos1 + 2] += dvz;
            // Add the derivative in respect to V to the first vertex
            dv[pos2] += dvx;
            dv[pos2 + 1] += dvy;
            dv[pos2 + 2] += dvz;
        }
        // Iterate over all the tangents
        for (int i = 0; i < indices.size(); i++) {
            // Index for the normal
            final int nor = indices.get(i) * 3;
            // Get the normal
            final float nx = normals.get(nor);
            final float ny = normals.get(nor + 1);
            final float nz = normals.get(nor + 2);
            // Get the derivative in respect to U
            final float dux = du[nor];
            final float duy = du[nor + 1];
            final float duz = du[nor + 2];
            // Get the derivative in respect to V
            final float dvx = dv[nor];
            final float dvy = dv[nor + 1];
            final float dvz = dv[nor + 2];
            // Dot the normal and derivative in respect to U
            final float d = nx * dux + ny * duy + nz * duz;
            // Calculate the tangent using Gram-Schmidt
            float tx = (dux - nx * d);
            float ty = (duy - ny * d);
            float tz = (duz - nz * d);
            // Length of the tangent
            final float l = (float) Math.sqrt(tx * tx + ty * ty + tz * tz);
            // Normalize the tangent
            tx /= l;
            ty /= l;
            tz /= l;
            // Index for the tangent
            final int tan = indices.get(i) * 4;
            // Set the tangent coordinates
            tangents.set(tan, tx);
            tangents.set(tan + 1, ty);
            tangents.set(tan + 2, tz);
            // Cross the normal and the derivative in respect to U
            final float cx = ny * duz - nz * duy;
            final float cy = nz * dux - nx * duz;
            final float cz = nx * duy - ny * dux;
            // Dot this cross product with the derivative in respect to V
            final float d2 = cx * dvx + cy * dvy + cz * dvz;
            // Determine the handedness
            final float h = d2 < 0 ? -1 : 1;
            // Set the handedness value
            tangents.set(tan + 3, h);
        }
    }

    /**
     * Converts a standard triangle model to a wireframe model, with the option to perform de-triangulation (convert faces of triangles back to a polygon). This assumes that the positions have 3
     * components, in the x, y, z order. No excess data is kept. The final model will have no duplicate vertices or edges.
     *
     * @param positions The position list
     * @param indices The indices
     * @param deTriangulation Merge triangles into faces when possible
     */
    public static void toWireframe(TFloatList positions, TIntList indices, boolean deTriangulation) {
        int indicesSize = indices.size();
        int positionsSize = positions.size();
        // Remove duplicate vertices
        for (int i = 0; i < positionsSize; i += 3) {
            final float x = positions.get(i);
            final float y = positions.get(i + 1);
            final float z = positions.get(i + 2);
            // Search for a duplicate
            for (int ii = i + 3; ii < positionsSize; ii += 3) {
                final float ox = positions.get(ii);
                final float oy = positions.get(ii + 1);
                final float oz = positions.get(ii + 2);
                if (x == ox && y == oy && z == oz) {
                    // If one is removed, we need to fix the indices
                    for (int iii = 0; iii < indicesSize; iii++) {
                        final int index = indices.get(iii);
                        if (index == ii / 3) {
                            // Any index referring to it is replaced by the original
                            indices.replace(iii, i / 3);
                        } else if (index > ii / 3) {
                            // Any index above is decremented
                            indices.replace(iii, index - 1);
                        }
                    }
                    // Then we can remove it properly
                    positions.remove(ii, 3);
                    positionsSize -= 3;
                    ii -= 3;
                }
            }
        }
        // Next we remove duplicate edges using a hash set
        final Set<Vector2i> edges = new HashSet<>();
        final Set<Vector2i> cancelled = new HashSet<>();
        for (int i = 0; i < indicesSize; i += 3) {
            final int i0 = indices.get(i);
            final int i1 = indices.get(i + 1);
            final int i2 = indices.get(i + 2);
            // If we need to remove unnecessary edges
            if (deTriangulation) {
                // Get the points of the triangle
                final Vector3f p00 = new Vector3f(positions.get(i0 * 3), positions.get(i0 * 3 + 1), positions.get(i0 * 3 + 2));
                final Vector3f p01 = new Vector3f(positions.get(i1 * 3), positions.get(i1 * 3 + 1), positions.get(i1 * 3 + 2));
                final Vector3f p02 = new Vector3f(positions.get(i2 * 3), positions.get(i2 * 3 + 1), positions.get(i2 * 3 + 2));
                // Test with all the other triangles
                for (int ii = i + 3; ii < indicesSize; ii += 3) {
                    // Get the indices of the other triangle
                    final int ii0 = indices.get(ii);
                    final int ii1 = indices.get(ii + 1);
                    final int ii2 = indices.get(ii + 2);
                    // Get the vertices of the other triangle
                    final Vector3f p10 = new Vector3f(positions.get(ii0 * 3), positions.get(ii0 * 3 + 1), positions.get(ii0 * 3 + 2));
                    final Vector3f p11 = new Vector3f(positions.get(ii1 * 3), positions.get(ii1 * 3 + 1), positions.get(ii1 * 3 + 2));
                    final Vector3f p12 = new Vector3f(positions.get(ii2 * 3), positions.get(ii2 * 3 + 1), positions.get(ii2 * 3 + 2));
                    // Test for a common edge
                    final Vector2i edge = getCommonEdge(p00, p01, p02, p10, p11, p12);
                    if (edge != null) {
                        // If we have one, add it to the cancelled list to be removed later since we can't do that now
                        final int c00 = indices.get(i + edge.getX());
                        final int c01 = indices.get(i + (edge.getX() + 1) % 3);
                        final int c10 = indices.get(ii + edge.getY());
                        final int c11 = indices.get(ii + (edge.getY() + 1) % 3);
                        cancelled.add(new Vector2i(Math.min(c00, c01), Math.max(c00, c01)));
                        cancelled.add(new Vector2i(Math.min(c10, c11), Math.max(c10, c11)));
                    }
                }
            }
            // Sorting the indices for the edges ensure the equalities will work correctly
            edges.add(new Vector2i(Math.min(i0, i1), Math.max(i0, i1)));
            edges.add(new Vector2i(Math.min(i1, i2), Math.max(i1, i2)));
            edges.add(new Vector2i(Math.min(i2, i0), Math.max(i2, i0)));
        }
        // Removed any edge that was flagged as unnecessary by the de-triangulation
        edges.removeAll(cancelled);
        // Finally, clear the indices and re-add them from the now unique edges
        indices.clear();
        for (Vector2i edge : edges) {
            indices.add(edge.getX());
            indices.add(edge.getY());
        }
    }

    /**
     * Generates a crosshairs shaped wireframe in 3D. The center is at the intersection point of the three lines.
     *
     * @param length The length for the three lines
     * @return The vertex data
     */
    public static VertexData generateCrosshairs(float length) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        // Generate the mesh
        generateCrosshairs(positions, indices, length);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        return destination;
    }

    /**
     * Generates a crosshairs shaped wireframe in 3D. The center is at the intersection point of the three lines.
     *
     * @param positions Where to save the position information
     * @param indices Where to save the indices
     * @param length The length for the three lines
     */
    public static void generateCrosshairs(TFloatList positions, TIntList indices, float length) {
        /*
         *   \ |
         *    \|
         * ----O-----
         *     |\
         *     | \
         */
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
    }

    /**
     * Generates a cuboid shaped wireframe (the outline of the cuboid). The center is at the middle of the cuboid.
     *
     * @param size The size of the cuboid to generate, on x, y and z
     * @return The vertex data
     */
    public static VertexData generateWireCuboid(Vector3f size) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        // Generate the mesh
        generateWireCuboid(positions, indices, size);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        return destination;
    }

    /**
     * Generates a cuboid shaped wireframe (the outline of the cuboid). The center is at the middle of the cuboid.
     *
     * @param positions Where to save the position information
     * @param indices Where to save the indices
     * @param size The size of the cuboid to generate, on x, y and z
     */
    public static void generateWireCuboid(TFloatList positions, TIntList indices, Vector3f size) {
        /*
         * 4------5
         * |\     |\
         * | 7------6
         * | |    | |
         * 0-|----1 |
         *  \|     \|
         *   3------2
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
        // Add all of the corners
        addVector(positions, p0);
        addVector(positions, p1);
        addVector(positions, p2);
        addVector(positions, p3);
        addVector(positions, p4);
        addVector(positions, p5);
        addVector(positions, p6);
        addVector(positions, p7);
        // Top face
        addAll(indices, 4, 5, 5, 6, 6, 7, 7, 4);
        // Bottom face
        addAll(indices, 0, 1, 1, 2, 2, 3, 3, 0);
        // Sides
        addAll(indices, 6, 2, 7, 3, 4, 0, 5, 1);
    }

    /**
     * Generates a plane on xy. The center is at the middle of the plane.
     *
     * @param size The size of the plane to generate, on x and y
     * @return The vertex data
     */
    public static VertexData generatePlane(Vector2f size) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
        destination.addAttribute(1, normalsAttribute);
        final TFloatList normals = new TFloatArrayList();
        final VertexAttribute textureCoordsAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, 2);
        destination.addAttribute(2, textureCoordsAttribute);
        final TFloatList textureCoords = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        // Generate the mesh
        generatePlane(positions, normals, textureCoords, indices, size);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        normalsAttribute.setData(normals);
        textureCoordsAttribute.setData(textureCoords);
        return destination;
    }

    /**
     * Generates a textured plane on xy. The center is at the middle of the plane.
     *
     * @param positions Where to save the position information
     * @param normals Where to save the normal information, can be null to ignore the attribute
     * @param textureCoords Where to save the texture coordinate information, can be null to ignore the attribute
     * @param indices Where to save the indices
     * @param size The size of the plane to generate, on x and y
     */
    public static void generatePlane(TFloatList positions, TFloatList normals, TFloatList textureCoords, TIntList indices, Vector2f size) {
        /*
         * 2-----3
         * |     |
         * |     |
         * 0-----1
         */
        // Corner positions
        final Vector2f p = size.div(2);
        final Vector3f p3 = new Vector3f(p.getX(), p.getY(), 0);
        final Vector3f p2 = new Vector3f(-p.getX(), p.getY(), 0);
        final Vector3f p1 = new Vector3f(p.getX(), -p.getY(), 0);
        final Vector3f p0 = new Vector3f(-p.getX(), -p.getY(), 0);
        // Normal
        final Vector3f n = new Vector3f(0, 0, 1);
        // Face
        addVector(positions, p0);
        addVector(normals, n);
        addAll(textureCoords, 0, 0);
        addVector(positions, p1);
        addVector(normals, n);
        addAll(textureCoords, 1, 0);
        addVector(positions, p2);
        addVector(normals, n);
        addAll(textureCoords, 0, 1);
        addVector(positions, p3);
        addVector(normals, n);
        addAll(textureCoords, 1, 1);
        addAll(indices, 0, 3, 2, 0, 1, 3);
    }

    /**
     * Generates a solid cuboid mesh. This mesh includes the positions, normals, texture coords and tangents. The center is at the middle of the cuboid.
     *
     * @param size The size of the cuboid to generate, on x, y and z
     * @return The vertex data
     */
    public static VertexData generateCuboid(Vector3f size) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
        destination.addAttribute(1, normalsAttribute);
        final TFloatList normals = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        final VertexAttribute textureCoordsAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, 2);
        destination.addAttribute(2, textureCoordsAttribute);
        final TFloatList texturesCoords = new TFloatArrayList();
        // Generate the mesh
        generateCuboid(positions, normals, texturesCoords, indices, size);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        normalsAttribute.setData(normals);
        textureCoordsAttribute.setData(texturesCoords);
        return destination;
    }

    /**
     * Generates a solid cuboid mesh. This mesh includes the positions, normals, texture coords and tangents. The center is at the middle of the cuboid.
     *
     * @param positions Where to save the position information
     * @param normals Where to save the normal information, can be null to ignore the attribute
     * @param textureCoords Where to save the texture coordinate information, can be null to ignore the attribute
     * @param indices Where to save the indices
     * @param size The size of the cuboid to generate, on x, y and z
     */
    public static void generateCuboid(TFloatList positions, TFloatList normals, TFloatList textureCoords, TIntList indices, Vector3f size) {
        /*
         * 4------5
         * |\     |\
         * | 7------6
         * | |    | |
         * 0-|----1 |
         *  \|     \|
         *   3------2
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
        // Face x
        addVector(positions, p2);
        addVector(normals, nx);
        addAll(textureCoords, 0, 0);
        addVector(positions, p6);
        addVector(normals, nx);
        addAll(textureCoords, 0, 1);
        addVector(positions, p5);
        addVector(normals, nx);
        addAll(textureCoords, 1, 1);
        addVector(positions, p1);
        addVector(normals, nx);
        addAll(textureCoords, 1, 0);
        addAll(indices, 0, 2, 1, 0, 3, 2);
        // Face y
        addVector(positions, p4);
        addVector(normals, ny);
        addAll(textureCoords, 0, 0);
        addVector(positions, p5);
        addVector(normals, ny);
        addAll(textureCoords, 0, 1);
        addVector(positions, p6);
        addVector(normals, ny);
        addAll(textureCoords, 1, 1);
        addVector(positions, p7);
        addVector(normals, ny);
        addAll(textureCoords, 1, 0);
        addAll(indices, 4, 6, 5, 4, 7, 6);
        // Face z
        addVector(positions, p3);
        addVector(normals, nz);
        addAll(textureCoords, 0, 0);
        addVector(positions, p7);
        addVector(normals, nz);
        addAll(textureCoords, 0, 1);
        addVector(positions, p6);
        addVector(normals, nz);
        addAll(textureCoords, 1, 1);
        addVector(positions, p2);
        addVector(normals, nz);
        addAll(textureCoords, 1, 0);
        addAll(indices, 8, 10, 9, 8, 11, 10);
        // Face -x
        addVector(positions, p0);
        addVector(normals, nxN);
        addAll(textureCoords, 0, 0);
        addVector(positions, p4);
        addVector(normals, nxN);
        addAll(textureCoords, 0, 1);
        addVector(positions, p7);
        addVector(normals, nxN);
        addAll(textureCoords, 1, 1);
        addVector(positions, p3);
        addVector(normals, nxN);
        addAll(textureCoords, 1, 0);
        addAll(indices, 12, 14, 13, 12, 15, 14);
        // Face -y
        addVector(positions, p0);
        addVector(normals, nyN);
        addAll(textureCoords, 0, 0);
        addVector(positions, p3);
        addVector(normals, nyN);
        addAll(textureCoords, 0, 1);
        addVector(positions, p2);
        addVector(normals, nyN);
        addAll(textureCoords, 1, 1);
        addVector(positions, p1);
        addVector(normals, nyN);
        addAll(textureCoords, 1, 0);
        addAll(indices, 16, 18, 17, 16, 19, 18);
        // Face -z
        addVector(positions, p1);
        addVector(normals, nzN);
        addAll(textureCoords, 0, 0);
        addVector(positions, p5);
        addVector(normals, nzN);
        addAll(textureCoords, 0, 1);
        addVector(positions, p4);
        addVector(normals, nzN);
        addAll(textureCoords, 1, 1);
        addVector(positions, p0);
        addVector(normals, nzN);
        addAll(textureCoords, 1, 0);
        addAll(indices, 20, 22, 21, 20, 23, 22);
    }

    /**
     * Generates a solid spherical mesh. The center is at the middle of the sphere.
     *
     * @param radius The radius of the sphere
     * @return The vertex data
     */
    public static VertexData generateSphere(float radius) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
        destination.addAttribute(1, normalsAttribute);
        final TFloatList normals = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        // Generate the mesh
        generateSphere(positions, normals, indices, radius);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        normalsAttribute.setData(normals);
        return destination;
    }

    /**
     * Generates a solid spherical mesh. The center is at the middle of the sphere.
     *
     * @param positions Where to save the position information
     * @param normals Where to save the normal information, can be null to ignore the attribute
     * @param indices Where to save the indices
     * @param radius The radius of the sphere
     */
    public static void generateSphere(TFloatList positions, TFloatList normals, TIntList indices, float radius) {
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
                newTriangles.addAll(Arrays.asList(triangle.subdivide()));
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
            triangle.setV0(triangle.getV0().normalize().mul(radius));
            triangle.setV1(triangle.getV1().normalize().mul(radius));
            triangle.setV2(triangle.getV2().normalize().mul(radius));
        }
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
    }

    /**
     * Generates a cylindrical solid mesh. The center is at middle of the of the cylinder.
     *
     * @param radius The radius of the base and top
     * @param height The height (distance from the base to the top)
     * @return The vertex data
     */
    public static VertexData generateCylinder(float radius, float height) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
        destination.addAttribute(1, normalsAttribute);
        final TFloatList normals = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        // Generate the mesh
        generateCylinder(positions, normals, indices, radius, height);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        normalsAttribute.setData(normals);
        return destination;
    }

    /**
     * Generates a cylindrical solid mesh. The center is at middle of the of the cylinder.
     *
     * @param positions Where to save the position information
     * @param normals Where to save the normal information, can be null to ignore the attribute
     * @param indices Where to save the indices
     * @param radius The radius of the base and top
     * @param height The height (distance from the base to the top)
     */
    public static void generateCylinder(TFloatList positions, TFloatList normals, TIntList indices, float radius, float height) {
        // 0,0,0 will be halfway up the cylinder in the middle
        final float halfHeight = height / 2;
        // The positions at the rims of the cylinders
        final List<Vector3f> rims = new ArrayList<>();
        for (int angle = 0; angle < 360; angle += 15) {
            final double angleRads = Math.toRadians(angle);
            rims.add(new Vector3f(
                    radius * TrigMath.cos(angleRads),
                    halfHeight,
                    radius * -TrigMath.sin(angleRads)));
        }
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
    }

    /**
     * Generates a conical solid mesh. The center is at the middle of the cone.
     *
     * @param radius The radius of the base
     * @param height The height (distance from the base to the apex)
     * @return The vertex data
     */
    public static VertexData generateCone(float radius, float height) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
        destination.addAttribute(1, normalsAttribute);
        final TFloatList normals = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        // Generate the mesh
        generateCone(positions, normals, indices, radius, height);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        normalsAttribute.setData(normals);
        return destination;
    }

    /**
     * Generates a conical solid mesh. The center is at the middle of the cone.
     *
     * @param positions Where to save the position information
     * @param normals Where to save the normal information, can be null to ignore the attribute
     * @param indices Where to save the indices
     * @param radius The radius of the base
     * @param height The height (distance from the base to the apex)
     */
    public static void generateCone(TFloatList positions, TFloatList normals, TIntList indices, float radius, float height) {
        // 0,0,0 will be halfway up the cone in the middle
        final float halfHeight = height / 2;
        // The positions at the bottom rim of the cone
        final List<Vector3f> rim = new ArrayList<>();
        for (int angle = 0; angle < 360; angle += 15) {
            final double angleRads = Math.toRadians(angle);
            rim.add(new Vector3f(
                    radius * TrigMath.cos(angleRads),
                    -halfHeight,
                    radius * -TrigMath.sin(angleRads)));
        }
        // Apex of the cone
        final Vector3f top = new Vector3f(0, halfHeight, 0);
        // The normal for the triangle of the bottom face
        final Vector3f bottomNormal = new Vector3f(0, -1, 0);
        // Add the bottom face center vertex
        addVector(positions, new Vector3f(0, -halfHeight, 0));// 0
        addVector(normals, bottomNormal);
        // The square of the radius of the cone on the xy plane
        final float radiusSquared = radius * radius / 4;
        // Add all the faces section by section, turning around the y axis
        final int rimSize = rim.size();
        for (int i = 0; i < rimSize; i++) {
            // Get the bottom vertex position and the side normal
            final Vector3f b = rim.get(i);
            final Vector3f bn = new Vector3f(b.getX() / radiusSquared, halfHeight - b.getY(), b.getZ() / radiusSquared).normalize();
            // Average the current and next normal to get the top normal
            final int nextI = i == rimSize - 1 ? 0 : i + 1;
            final Vector3f nextB = rim.get(nextI);
            final Vector3f nextBN = new Vector3f(nextB.getX() / radiusSquared, halfHeight - nextB.getY(), nextB.getZ() / radiusSquared).normalize();
            final Vector3f tn = bn.add(nextBN).normalize();
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
    }

    /**
     * Generates a capsule shape solid mesh. The center is at the middle of the capsule. A capsule is two aligned and mirrored hemispheres joined by a cylinder.
     *
     * @param radius The radius of the cap sphere
     * @param height The height (distance between the centers of the two spheres)
     * @return The vertex data
     */
    public static VertexData generateCapsule(float radius, float height) {
        final VertexData destination = new VertexData();
        final VertexAttribute positionsAttribute = new VertexAttribute("positions", DataType.FLOAT, 3);
        destination.addAttribute(0, positionsAttribute);
        final TFloatList positions = new TFloatArrayList();
        final VertexAttribute normalsAttribute = new VertexAttribute("normals", DataType.FLOAT, 3);
        destination.addAttribute(1, normalsAttribute);
        final TFloatList normals = new TFloatArrayList();
        final TIntList indices = destination.getIndices();
        // Generate the mesh
        generateCapsule(positions, normals, indices, radius, height);
        // Put the mesh in the vertex data
        positionsAttribute.setData(positions);
        normalsAttribute.setData(normals);
        return destination;
    }

    /**
     * Generates a capsule shape solid mesh. The center is at the middle of the capsule. A capsule is two aligned and mirrored hemispheres joined by a cylinder.
     *
     * @param positions Where to save the position information
     * @param normals Where to save the normal information, can be null to ignore the attribute
     * @param indices Where to save the indices
     * @param radius The radius of the cap sphere
     * @param height The height (distance between the centers of the two spheres)
     */
    public static void generateCapsule(TFloatList positions, TFloatList normals, TIntList indices, float radius, float height) {

        // Start with the top hemisphere
        final float halfHeight = height / 2;
        final Vector3f top = new Vector3f(0, halfHeight + radius, 0);
        // Add the point for the top of the hemisphere
        addVector(positions, top);
        addVector(normals, top.normalize());
        // Next use spherical coordinates to separate the surface of the hemisphere into a grid
        final int thetaSections = 30, phiSections = 10;
        final int thetaIncrement = 360 / thetaSections, phiIncrement = 90 / phiSections;
        // For every point on that grid
        for (int phi = phiIncrement; phi <= 90; phi += phiIncrement) {
            for (int theta = thetaIncrement / 2; theta <= 360; theta += thetaIncrement) {
                // Compute the point in cartesian coordinates
                final double radPhi = Math.toRadians(phi);
                final double radTheta = Math.toRadians(theta);
                final float sinPhi = TrigMath.sin(radPhi);
                final Vector3f p = new Vector3f(
                        radius * sinPhi * TrigMath.sin(radTheta),
                        radius * TrigMath.cos(radPhi),
                        radius * sinPhi * TrigMath.cos(radTheta));
                // Add it to the positions
                addVector(positions, p.add(0, halfHeight, 0));
                addVector(normals, p.normalize());
                // Add the indices
                final int i0 = computeIndex(phiIncrement, phi, thetaSections, thetaIncrement, theta);
                final int i1 = computeIndex(phiIncrement, phi, thetaSections, thetaIncrement, theta + thetaIncrement);
                final int i2 = computeIndex(phiIncrement, phi - phiIncrement, thetaSections, thetaIncrement, theta);
                final int i3 = computeIndex(phiIncrement, phi - phiIncrement, thetaSections, thetaIncrement, theta + thetaIncrement);
                // Special case for the top apex vertex
                if (i2 == i3) {
                    addAll(indices, i0, i1, i3);
                } else {
                    addAll(indices, i0, i1, i3, i0, i3, i2);
                }
            }
        }
        // Next do the bottom hemisphere
        final Vector3f bottom = top.negate();
        // Get the offset in the positions of the bottom hemisphere
        final int bottomIndexOffset = positions.size() / 3;
        // Add the point for the top of the hemisphere
        addVector(positions, bottom);
        addVector(normals, bottom.normalize());
        // For every point on the grid
        for (int phi = phiIncrement; phi <= 90; phi += phiIncrement) {
            for (int theta = thetaIncrement / 2; theta <= 360; theta += thetaIncrement) {
                // Compute the point in cartesian coordinates
                final double radPhi = Math.toRadians(phi);
                final double radTheta = Math.toRadians(theta);
                final float sinPhi = TrigMath.sin(radPhi);
                final Vector3f p = new Vector3f(
                        radius * sinPhi * TrigMath.sin(radTheta),
                        -radius * TrigMath.cos(radPhi),
                        radius * sinPhi * TrigMath.cos(radTheta));
                // Add it to the positions
                addVector(positions, p.sub(0, halfHeight, 0));
                addVector(normals, p.normalize());
                // Add the indices
                final int i0 = computeIndex(phiIncrement, phi, thetaSections, thetaIncrement, theta) + bottomIndexOffset;
                final int i1 = computeIndex(phiIncrement, phi, thetaSections, thetaIncrement, theta + thetaIncrement) + bottomIndexOffset;
                final int i2 = computeIndex(phiIncrement, phi - phiIncrement, thetaSections, thetaIncrement, theta) + bottomIndexOffset;
                final int i3 = computeIndex(phiIncrement, phi - phiIncrement, thetaSections, thetaIncrement, theta + thetaIncrement) + bottomIndexOffset;
                // Special case for the bottom apex vertex
                if (i2 == i3) {
                    addAll(indices, i0, i3, i1);
                } else {
                    addAll(indices, i0, i3, i1, i0, i2, i3);
                }
            }
        }
        // Finally join both hemispheres
        for (int theta = thetaIncrement / 2, i = 0; theta <= 360; theta += thetaIncrement, i += 2) {
            // Add the indices
            final int i0 = computeIndex(phiIncrement, 90, thetaSections, thetaIncrement, theta);
            final int i1 = computeIndex(phiIncrement, 90, thetaSections, thetaIncrement, theta + thetaIncrement);
            final int i2 = computeIndex(phiIncrement, 90, thetaSections, thetaIncrement, theta) + bottomIndexOffset;
            final int i3 = computeIndex(phiIncrement, 90, thetaSections, thetaIncrement, theta + thetaIncrement) + bottomIndexOffset;
            addAll(indices, i1, i0, i2, i1, i2, i3);
        }
    }

    private static int computeIndex(int phiIncrement, int phi, int thetaSections, int thetaIncrement, int theta) {
        if (phi == 0) {
            return 0;
        }
        return 1 + (phi / phiIncrement - 1) * thetaSections + (theta % 360) / thetaIncrement;
    }

    private static Vector2i getCommonEdge(Vector3f p00, Vector3f p01, Vector3f p02, Vector3f p10, Vector3f p11, Vector3f p12) {
         /*
            We need to check if the triangles are on the same plane as each other first.

            triangle 1:
                p00, p01, p02
            triangle 2:
                p10, p11, p12

            triangle 1 edges:
                v00 = p01 - p00
                v01 = p02 - p00
            triangle 2 edges:
                v10 = p11 - p10
                v11 = p12 - p10

            Triangle 1 and triangle 2 are on the same plane if the edge  vectors of the second
            triangle are a linear combination of the edge vectors from the first one. I.E.:

                   |v00x v01x v10|
                det|v00y v01y v10| = 0
                   |v00z v01z v10|

                and

                   |v00x v01x v11|
                det|v00y v01y v11| = 0
                   |v00z v01z v11|

            If the conditions are met, we look for an edge from the first triangle that overlaps
            one in the second triangle. This is the case if they are the same infinite line and
            overlap in their finite form.

            For two parallel vectors, we have, by the properties of the cross product,

                v0 x v1 = 0

            This parallelism test must be done between both vectors first, then with the vector
            between the two starting points and one of the two edge vectors, to ensure that both
            infinite lines are the same.

            We can determine if the finite lines overlap if the starting point of the second vector
            can be expressed as the first vector multiplied by a factor plus its starting point

                p1 = p0 + tv0

            This applies for infinite lines. Since the edges aren't infinite, we have to restrict

                0 <= t <= 1

            This works because the length of v0 is that of the edge. So we have

                (p1 - p0) / v0 = t

            Where t must satisfy

                0 <= t <= 1

            This might fail depending on how the edges are setup, so we also need to check the case
            for v1. If any pass the edges are overlapping.

            If no overlap is found, the triangles don't touch, and we can't merge them.
         */
        final Vector3f v00 = p01.sub(p00);
        final Vector3f v01 = p02.sub(p01);
        final Vector3f v10 = p11.sub(p10);
        final Vector3f v11 = p12.sub(p11);
        final Matrix3f m0 = new Matrix3f(
                v00.getX(), v01.getX(), v10.getX(),
                v00.getY(), v01.getY(), v10.getY(),
                v00.getZ(), v01.getZ(), v10.getZ());
        final float d0 = m0.determinant();
        final Matrix3f m1 = new Matrix3f(
                v00.getX(), v01.getX(), v11.getX(),
                v00.getY(), v01.getY(), v11.getY(),
                v00.getZ(), v01.getZ(), v11.getZ());
        final float d1 = m1.determinant();
        if (Math.abs(d0) > GenericMath.FLT_EPSILON * 10 || Math.abs(d1) > GenericMath.FLT_EPSILON * 10) {
            return null;
        }
        final Vector3f v02 = p00.sub(p02);
        final Vector3f v12 = p10.sub(p12);
        if (isZero(v00.cross(v10)) && checkForOverlap(p00, v00, p10, v10)) {
            return new Vector2i(0, 0);
        } else if (isZero(v00.cross(v11)) && checkForOverlap(p00, v00, p11, v11)) {
            return new Vector2i(0, 1);
        } else if (isZero(v00.cross(v12)) && checkForOverlap(p00, v00, p12, v12)) {
            return new Vector2i(0, 2);
        } else if (isZero(v01.cross(v10)) && checkForOverlap(p01, v01, p10, v10)) {
            return new Vector2i(1, 0);
        } else if (isZero(v01.cross(v11)) && checkForOverlap(p01, v01, p11, v11)) {
            return new Vector2i(1, 1);
        } else if (isZero(v01.cross(v12)) && checkForOverlap(p01, v01, p12, v12)) {
            return new Vector2i(1, 2);
        } else if (isZero(v02.cross(v10)) && checkForOverlap(p02, v02, p10, v10)) {
            return new Vector2i(2, 0);
        } else if (isZero(v02.cross(v11)) && checkForOverlap(p02, v02, p11, v11)) {
            return new Vector2i(2, 1);
        } else if (isZero(v02.cross(v12)) && checkForOverlap(p02, v02, p12, v12)) {
            return new Vector2i(2, 2);
        } else {
            return null;
        }
    }

    private static boolean isZero(Vector3f v) {
        return Math.abs(v.getX()) <= GenericMath.FLT_EPSILON && Math.abs(v.getY()) <= GenericMath.FLT_EPSILON && Math.abs(v.getZ()) <= GenericMath.FLT_EPSILON;
    }

    private static boolean checkForOverlap(Vector3f p0, Vector3f v0, Vector3f p1, Vector3f v1) {
        final Vector3f p10 = p1.sub(p0);
        if (!isZero(p10.cross(v0))) {
            return false;
        }
        float t = getNonNaN(p10.div(v0));
        if (t >= 0 && t <= 1) {
            return true;
        }
        t = getNonNaN(p0.sub(p1).div(v1));
        return t >= 0 && t <= 1;
    }

    private static float getNonNaN(Vector3f v) {
        if (!Float.isNaN(v.getX())) {
            return v.getX();
        }
        if (!Float.isNaN(v.getY())) {
            return v.getY();
        }
        if (!Float.isNaN(v.getZ())) {
            return v.getZ();
        }
        throw new IllegalArgumentException("All components are NaN");
    }

    private static void addVector(TFloatList to, Vector3f v) {
        if (to != null) {
            to.add(v.getX());
            to.add(v.getY());
            to.add(v.getZ());
        }
    }

    private static void addAll(TIntList to, int... f) {
        if (to != null) {
            to.add(f);
        }
    }

    private static void addAll(TFloatList to, float... f) {
        if (to != null) {
            to.add(f);
        }
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

        private void setV0(Vector3f v0) {
            this.v0 = v0;
        }

        private Vector3f getV1() {
            return v1;
        }

        private void setV1(Vector3f v1) {
            this.v1 = v1;
        }

        private Vector3f getV2() {
            return v2;
        }

        private void setV2(Vector3f v2) {
            this.v2 = v2;
        }

        private Triangle[] subdivide() {
            final Vector3f e0 = v1.sub(v0).div(2);
            final Vector3f va = v0.add(e0);
            final Vector3f e1 = v2.sub(v1).div(2);
            final Vector3f vb = v1.add(e1);
            final Vector3f e2 = v0.sub(v2).div(2);
            final Vector3f vc = v2.add(e2);
            return new Triangle[]{
                    new Triangle(v0, va, vc),
                    new Triangle(va, v1, vb),
                    new Triangle(vc, vb, v2),
                    new Triangle(va, vb, vc)
            };
        }
    }
}
