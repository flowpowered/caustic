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
package org.spout.renderer.gl;

public final class GL {

	/**
	 * Used for {@link FrameBuffer.AttachmentPoint}s
	 */
	public static final int GL_COLOR_ATTACHMENT0 = 0x8CE0,
			GL_COLOR_ATTACHMENT1 = 0x8CE1,
			GL_COLOR_ATTACHMENT2 = 0x8CE2,
			GL_COLOR_ATTACHMENT3 = 0x8CE3,
			GL_DEPTH_ATTACHMENT = 0x8D00,
			GL_STENCIL_ATTACHMENT = 0x8D20,
			GL_DEPTH_STENCIL_ATTACHMENT = 0x821A;

	/**
	 * Used for {@link Texture.Format}s.
	 */
	public static final int GL_RED = 0x1903,
			GL_RGB = 0x1907,
			GL_RGBA = 0x1908,
			GL_DEPTH_COMPONENT = 0x1902,
			GL_RG = 0x8227,
			GL_DEPTH_STENCIL = 0x84F9;

	/**
	 * Used for {@link org.spout.renderer.data.VertexAttribute}s
	 */
	public static final int GL_BYTE = 0x1400,
			GL_UNSIGNED_BYTE = 0x1401,
			GL_SHORT = 0x1402,
			GL_UNSIGNED_SHORT = 0x1403,
			GL_INT = 0x1404,
			GL_UNSIGNED_INT = 0x1405,
			GL_FLOAT = 0x1406,
			GL_DOUBLE = 0x140A;
}
