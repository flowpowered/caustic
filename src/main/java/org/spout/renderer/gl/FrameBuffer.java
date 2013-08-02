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

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL30;

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;

/**
 * Represents an OpenGL frame buffer. A frame buffer can be assigned to a render list. When
 * assigned, all models in the list will be rendered to the frame buffer, instead of the screen.
 * This is meant for advanced rendering techniques such as shadow mapping and screen space ambient
 * occlusion (SSAO).
 */
public abstract class FrameBuffer extends Creatable implements GLVersioned {
	protected int id;

	@Override
	public void destroy() {
		id = 0;
		super.destroy();
	}

	/**
	 * Binds the frame buffer to the OpenGL context.
	 */
	public abstract void bind();

	/**
	 * Unbinds the frame buffer from the OpenGL context.
	 */
	public abstract void unbind();

	/**
	 * Attaches the texture to the frame buffer attachment point.
	 *
	 * @param point The attachment point
	 * @param texture The texture to attach
	 */
	public abstract void attach(AttachmentPoint point, Texture texture);

	/**
	 * Attaches the render buffer to the attachment point
	 *
	 * @param point The attachment point
	 * @param buffer The render buffer
	 */
	public abstract void attach(AttachmentPoint point, RenderBuffer buffer);

	/**
	 * Gets the ID for this frame buffer as assigned by OpenGL.
	 *
	 * @return The ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * An enum of the possible frame buffer attachment points.
	 */
	public static enum AttachmentPoint {
		// TODO: remove color from enum and support n color attachments
		COLOR0(GL30.GL_COLOR_ATTACHMENT0, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, true),
		COLOR1(GL30.GL_COLOR_ATTACHMENT1, EXTFramebufferObject.GL_COLOR_ATTACHMENT1_EXT, true),
		COLOR2(GL30.GL_COLOR_ATTACHMENT2, EXTFramebufferObject.GL_COLOR_ATTACHMENT2_EXT, true),
		COLOR3(GL30.GL_COLOR_ATTACHMENT3, EXTFramebufferObject.GL_COLOR_ATTACHMENT3_EXT, true),
		DEPTH(GL30.GL_DEPTH_ATTACHMENT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, false),
		STENCIL(GL30.GL_STENCIL_ATTACHMENT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, false),
		DEPTH_STENCIL(GL30.GL_DEPTH_STENCIL_ATTACHMENT, -1, false);
		private final int glConstant;
		private final int extConstant;
		private final boolean isColor;

		private AttachmentPoint(int glConstant, int extConstant, boolean isColor) {
			this.glConstant = glConstant;
			this.extConstant = extConstant;
			this.isColor = isColor;
		}

		/**
		 * Gets the OpenGL constant for this attachment point.
		 *
		 * @return The OpenGL Constant
		 */
		public int getGLConstant() {
			return glConstant;
		}

		/**
		 * Gets the EXT constant for this attachment point.
		 *
		 * @return The EXT Constant
		 */
		public int getEXTConstant() {
			if (extConstant == -1) {
				throw new UnsupportedOperationException("This constant is not supported by EXT");
			}
			return extConstant;
		}

		/**
		 * Returns true if the attachment point is a color attachment.
		 *
		 * @return Whether or not the attachment is a color attachment
		 */
		public boolean isColor() {
			return isColor;
		}
	}
}
