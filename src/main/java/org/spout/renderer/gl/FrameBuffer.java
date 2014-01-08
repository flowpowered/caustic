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

import java.util.EnumMap;
import java.util.Map;

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.util.CausticUtil;

/**
 * Represents an OpenGL frame buffer. A frame buffer can be assigned to a render list. When assigned, all models in the list will be rendered to the frame buffer, instead of the screen. This is meant
 * for advanced rendering techniques such as shadow mapping and screen space ambient occlusion (SSAO).
 */
public abstract class FrameBuffer extends Creatable implements GLVersioned {
    protected int id;
    // The attached texture and render buffers
    protected final Map<AttachmentPoint, Texture> textures = new EnumMap<>(AttachmentPoint.class);
    protected final Map<AttachmentPoint, RenderBuffer> buffers = new EnumMap<>(AttachmentPoint.class);

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
    public void attach(AttachmentPoint point, Texture texture) {
        CausticUtil.checkVersion(this, texture);
        buffers.remove(point);
        textures.put(point, texture);
    }

    /**
     * Attaches the render buffer to the attachment point
     *
     * @param point The attachment point
     * @param buffer The render buffer
     */
    public void attach(AttachmentPoint point, RenderBuffer buffer) {
        CausticUtil.checkVersion(this, buffer);
        textures.remove(point);
        buffers.put(point, buffer);
    }

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
        COLOR0(0x8CE0, true), // GL30.GL_COLOR_ATTACHMENT0
        COLOR1(0x8CE1, true), // GL30.GL_COLOR_ATTACHMENT1
        COLOR2(0x8CE2, true), // GL30.GL_COLOR_ATTACHMENT2
        COLOR3(0x8CE3, true), // GL30.GL_COLOR_ATTACHMENT3
        COLOR4(0x8CE4, true), // GL30.GL_COLOR_ATTACHMENT4
        DEPTH(0x8D00, false), // GL30.GL_DEPTH_ATTACHMENT
        STENCIL(0x8D20, false), // GL30.GL_STENCIL_ATTACHMENT
        DEPTH_STENCIL(0x821A, false); // GL30.GL_DEPTH_STENCIL_ATTACHMENT
        private final int glConstant;
        private final boolean isColor;

        private AttachmentPoint(int glConstant, boolean isColor) {
            this.glConstant = glConstant;
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
         * Returns true if the attachment point is a color attachment.
         *
         * @return Whether or not the attachment is a color attachment
         */
        public boolean isColor() {
            return isColor;
        }
    }
}
