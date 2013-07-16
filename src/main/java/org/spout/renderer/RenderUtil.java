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

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.util.glu.GLU;

public class RenderUtil {
	/**
	 * Converts a float list to a float buffer.
	 *
	 * @param floats The float list to convert
	 * @return The float buffer for the list
	 */
	public static FloatBuffer toBuffer(TFloatList floats) {
		final FloatBuffer floatsBuffer = BufferUtils.createFloatBuffer(floats.size());
		floatsBuffer.put(floats.toArray());
		floatsBuffer.flip();
		return floatsBuffer;
	}

	/**
	 * Converts an integer list to an integer buffer.
	 *
	 * @param ints The integer list to convert
	 * @return The integer buffer for the list
	 */
	public static IntBuffer toBuffer(TIntList ints) {
		final IntBuffer intsBuffer = BufferUtils.createIntBuffer(ints.size());
		intsBuffer.put(ints.toArray());
		intsBuffer.flip();
		return intsBuffer;
	}

	public static void checkForOpenGLError() {
		final int errorValue = GL11.glGetError();
		if (errorValue != GL11.GL_NO_ERROR) {
			throw new OpenGLException("OPEN GL ERROR: " + GLU.gluErrorString(errorValue));
		}
	}
}
