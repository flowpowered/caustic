/*
 * This file is part of Caustic API.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic API is licensed under the Spout License Version 1.
 *
 * Caustic API is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic API is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.api.util;

import java.io.InputStream;
import java.util.Scanner;

import com.flowpowered.math.vector.Vector3f;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

/**
 * A static loading class for standard .obj model files. This class will load positions, normals can texture coordinates. Missing normals are not calculated. Normals are expected to be of unit length.
 * Models should be triangulated.
 */
public final class ObjFileLoader {
    private ObjFileLoader() {
    }

    private static final String COMPONENT_SEPARATOR = " ";
    private static final String INDEX_SEPARATOR = "/";
    private static final String POSITION_LIST_PREFIX = "v";
    private static final String TEXTURE_LIST_PREFIX = "vt";
    private static final String NORMAL_LIST_PREFIX = "vn";
    private static final String INDEX_LIST_PREFIX = "f";

    /**
     * Loads a .obj file, storing the data in the provided lists. After loading, the input stream will be closed.The number of components for each attribute is returned in a Vector3, x being the
     * number of position components, y the number of texture coord components and z the number of normal components. Note that normal and/or texture coord attributes might be missing from the .obj
     * file. If this is the case, their lists will be empty. The indices are stored in the indices list.
     *
     * @param stream The input stream for the .obj file
     * @param positions The list in which to store the positions
     * @param textureCoords The list in which to store the texture coords
     * @param normals The list in which to store the normals
     * @param indices The list in which to store the indices
     * @return A Vector3 containing, in order, the number of components for the positions, texture coords and normals
     * @throws MalformedObjFileException If any errors occur during loading
     */
    public static Vector3f load(InputStream stream, TFloatList positions, TFloatList textureCoords, TFloatList normals, TIntList indices) {
        int positionSize = -1;
        final TFloatList rawTextureCoords = new TFloatArrayList();
        int textureCoordSize = -1;
        final TFloatList rawNormalComponents = new TFloatArrayList();
        int normalSize = -1;
        final TIntList textureCoordIndices = new TIntArrayList();
        final TIntList normalIndices = new TIntArrayList();
        String line = null;
        try (Scanner scanner = new Scanner(stream)) {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.startsWith(POSITION_LIST_PREFIX + COMPONENT_SEPARATOR)) {
                    parseComponents(positions, line);
                    if (positionSize == -1) {
                        positionSize = positions.size();
                    }
                } else if (line.startsWith(TEXTURE_LIST_PREFIX + COMPONENT_SEPARATOR)) {
                    parseComponents(rawTextureCoords, line);
                    if (textureCoordSize == -1) {
                        textureCoordSize = rawTextureCoords.size();
                    }
                } else if (line.startsWith(NORMAL_LIST_PREFIX + COMPONENT_SEPARATOR)) {
                    parseComponents(rawNormalComponents, line);
                    if (normalSize == -1) {
                        normalSize = rawNormalComponents.size();
                    }
                } else if (line.startsWith(INDEX_LIST_PREFIX + COMPONENT_SEPARATOR)) {
                    parseIndices(indices, textureCoordIndices, normalIndices, line);
                }
            }
            line = null;
            final boolean hasTextureCoords;
            final boolean hasNormals;
            if (!textureCoordIndices.isEmpty() && !rawTextureCoords.isEmpty()) {
                textureCoords.fill(0, positions.size() / positionSize * textureCoordSize, 0);
                hasTextureCoords = true;
            } else {
                hasTextureCoords = false;
            }
            if (!normalIndices.isEmpty() && !rawNormalComponents.isEmpty()) {
                normals.fill(0, positions.size() / positionSize * normalSize, 0);
                hasNormals = true;
            } else {
                hasNormals = false;
            }
            if (hasTextureCoords) {
                for (int i = 0; i < textureCoordIndices.size(); i++) {
                    final int textureCoordIndex = textureCoordIndices.get(i) * textureCoordSize;
                    final int positionIndex = indices.get(i) * textureCoordSize;
                    for (int ii = 0; ii < textureCoordSize; ii++) {
                        textureCoords.set(positionIndex + ii, rawTextureCoords.get(textureCoordIndex + ii));
                    }
                }
            }
            if (hasNormals) {
                for (int i = 0; i < normalIndices.size(); i++) {
                    final int normalIndex = normalIndices.get(i) * normalSize;
                    final int positionIndex = indices.get(i) * normalSize;
                    for (int ii = 0; ii < normalSize; ii++) {
                        normals.set(positionIndex + ii, rawNormalComponents.get(normalIndex + ii));
                    }
                }
            }
        } catch (Exception ex) {
            throw new MalformedObjFileException(line, ex);
        }
        return new Vector3f(positionSize, textureCoordSize, normalSize).max(0, 0, 0);
    }

    private static void parseComponents(TFloatList destination, String line) {
        final String[] components = line.split(COMPONENT_SEPARATOR);
        for (int i = 1; i < components.length; i++) {
            destination.add(Float.parseFloat(components[i]));
        }
    }

    private static void parseIndices(TIntList positions, TIntList textureCoords, TIntList normals, String line) {
        final String[] indicesGroup = line.split(COMPONENT_SEPARATOR);
        for (int i = 1; i < indicesGroup.length; i++) {
            final String[] indices = indicesGroup[i].split(INDEX_SEPARATOR);
            positions.add(Integer.parseInt(indices[0]) - 1);
            if (indices.length > 1 && !indices[1].isEmpty()) {
                textureCoords.add(Integer.parseInt(indices[1]) - 1);
            }
            if (indices.length > 2) {
                normals.add(Integer.parseInt(indices[2]) - 1);
            }
        }
    }

    /**
     * An exception throw by the {@link ObjFileLoader} during loading if any errors are encountered.
     */
    public static class MalformedObjFileException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new exception from the line at which the error occurred and the cause. If the error did not occur on a line, the variable can be passed as null.
         *
         * @param line The line of origin, or null, if not on a line
         * @param cause The original exception
         */
        public MalformedObjFileException(String line, Throwable cause) {
            super(line != null ? "for line \"" + line + "\"" : null, cause);
        }
    }
}
