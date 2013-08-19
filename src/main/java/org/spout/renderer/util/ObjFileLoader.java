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
package org.spout.renderer.util;

import java.io.InputStream;
import java.util.Scanner;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;

import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexData;

/**
 * A static loading class for standard .obj model files. This class will load positions, normals can texture coordinates. Missing normals are not calculated. Normals are expected to be of unit length.
 * Models should be triangulated.
 */
public class ObjFileLoader {
	private static final String COMPONENT_SEPARATOR = " ";
	private static final String INDEX_SEPARATOR = "/";
	private static final String POSITION_LIST_PREFIX = "v";
	private static final String TEXTURE_LIST_PREFIX = "vt";
	private static final String NORMAL_LIST_PREFIX = "vn";
	private static final String INDEX_LIST_PREFIX = "f";

	/**
	 * Loads a .obj file, storing the data in the VertexData. Position attributes will be added with the name "positions", normals with "normals" and texture coordinate with "textureCoords", in this
	 * order. After loading, the input stream will be closed.
	 *
	 * @param stream The input stream for the .obj file
	 * @return The vertex data, filed with the loaded vertices
	 * @throws MalformedObjFileException If any errors occur during loading
	 */
	public static VertexData load(InputStream stream) throws MalformedObjFileException {
		return load(null, stream);
	}

	/**
	 * Loads a .obj file, storing the data in the VertexData. Position attributes will be added with the name "positions", normals with "normals" and texture coordinate with "textureCoords", in this
	 * order. If destination is null, a new one is created and returned. After loading, the input stream will be closed.
	 *
	 * @param destination The destination for the vertex data. Can be null
	 * @param stream The input stream for the .obj file
	 * @return The vertex data, filed with the loaded vertices
	 * @throws MalformedObjFileException If any errors occur during loading
	 */
	public static VertexData load(VertexData destination, InputStream stream) throws MalformedObjFileException {
		if (destination == null) {
			destination = new VertexData();
		}
		final TFloatList positionComponents = new TFloatArrayList();
		int positionSize = -1;
		final TFloatList textureCoordComponents = new TFloatArrayList();
		int textureCoordSize = -1;
		final TFloatList normalComponents = new TFloatArrayList();
		int normalSize = -1;
		final TIntList positionIndices = new TIntArrayList();
		final TIntList textureCoordIndices = new TIntArrayList();
		final TIntList normalIndices = new TIntArrayList();
		String line = null;
		try (Scanner scanner = new Scanner(stream)) {
			while (scanner.hasNextLine()) {
				line = scanner.nextLine();
				if (line.startsWith(POSITION_LIST_PREFIX + COMPONENT_SEPARATOR)) {
					parseComponents(positionComponents, line);
					if (positionSize == -1) {
						positionSize = positionComponents.size();
					}
				} else if (line.startsWith(TEXTURE_LIST_PREFIX + COMPONENT_SEPARATOR)) {
					parseComponents(textureCoordComponents, line);
					if (textureCoordSize == -1) {
						textureCoordSize = textureCoordComponents.size();
					}
				} else if (line.startsWith(NORMAL_LIST_PREFIX + COMPONENT_SEPARATOR)) {
					parseComponents(normalComponents, line);
					if (normalSize == -1) {
						normalSize = normalComponents.size();
					}
				} else if (line.startsWith(INDEX_LIST_PREFIX + COMPONENT_SEPARATOR)) {
					parseIndices(positionIndices, textureCoordIndices, normalIndices, line);
				}
			}
			line = null;
			final VertexAttribute positionAttribute = new VertexAttribute("positions", DataType.FLOAT, positionSize);
			destination.addAttribute(0, positionAttribute);
			final TFloatList textureCoords;
			final TFloatList normals;
			final VertexAttribute textureCoordsAttribute;
			final VertexAttribute normalAttribute;
			if (!textureCoordIndices.isEmpty() && !textureCoordComponents.isEmpty()) {
				textureCoordsAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, textureCoordSize);
				destination.addAttribute(2, textureCoordsAttribute);
				textureCoords = new TFloatArrayList();
				textureCoords.fill(0, positionComponents.size() / positionSize * textureCoordSize, 0);
			} else {
				textureCoords = null;
				textureCoordsAttribute = null;
			}
			if (!normalIndices.isEmpty() && !normalComponents.isEmpty()) {
				normalAttribute = new VertexAttribute("normals", DataType.FLOAT, normalSize);
				destination.addAttribute(1, normalAttribute);
				normals = new TFloatArrayList();
				normals.fill(0, positionComponents.size() / positionSize * normalSize, 0);
			} else {
				normals = null;
				normalAttribute = null;
			}
			destination.getIndices().addAll(positionIndices);
			positionAttribute.setData(positionComponents);
			if (textureCoords != null) {
				for (int i = 0; i < textureCoordIndices.size(); i++) {
					final int textureCoordIndex = textureCoordIndices.get(i) * textureCoordSize;
					final int positionIndex = positionIndices.get(i) * textureCoordSize;
					for (int ii = 0; ii < textureCoordSize; ii++) {
						textureCoords.set(positionIndex + ii, textureCoordComponents.get(textureCoordIndex + ii));
					}
				}
				textureCoordsAttribute.setData(textureCoords);
			}
			if (normals != null) {
				for (int i = 0; i < normalIndices.size(); i++) {
					final int normalIndex = normalIndices.get(i) * normalSize;
					final int positionIndex = positionIndices.get(i) * normalSize;
					for (int ii = 0; ii < normalSize; ii++) {
						normals.set(positionIndex + ii, normalComponents.get(normalIndex + ii));
					}
				}
				normalAttribute.setData(normals);
			}
		} catch (Exception ex) {
			throw new MalformedObjFileException(line, ex);
		}
		return destination;
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
