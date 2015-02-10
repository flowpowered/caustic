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
package com.flowpowered.caustic.api.gl;

import com.flowpowered.caustic.api.Creatable;
import com.flowpowered.caustic.api.GLVersioned;

/**
 * Represents an OpenGL frame buffer. A frame buffer can be bound before rendering to redirect the output to textures instead of the screen. This is meant for advanced rendering techniques such as
 * shadow mapping and screen space ambient occlusion (SSAO).
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
     * Detaches the texture or render buffer from the attachment point
     *
     * @param point The attachment point
     */
    public abstract void detach(AttachmentPoint point);

    /**
     * Returns true if the frame buffer is complete, false if otherwise.
     *
     * @return Whether or not the frame buffer is complete
     */
    public abstract boolean isComplete();

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
