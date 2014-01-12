/*
 * This file is part of Caustic LWJGL.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic LWJGL is licensed under the Spout License Version 1.
 *
 * Caustic LWJGL is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic LWJGL is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.lwjgl.gl32;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Map.Entry;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import org.spout.renderer.api.gl.FrameBuffer;
import org.spout.renderer.api.gl.RenderBuffer;
import org.spout.renderer.api.gl.Texture;
import org.spout.renderer.api.util.CausticUtil;
import org.spout.renderer.lwjgl.LWJGLUtil;

/**
 * An OpenGL 3.2 implementation of {@link FrameBuffer}.
 *
 * @see FrameBuffer
 */
public class GL32FrameBuffer extends FrameBuffer {
    protected GL32FrameBuffer() {
    }

    @Override
    public void create() {
        // Generate and bind the frame buffer
        id = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Track the color attachments to output for later use
        final TIntSet outputBuffers = new TIntHashSet();
        // Attach the textures
        for (Entry<AttachmentPoint, Texture> entry : textures.entrySet()) {
            final AttachmentPoint point = entry.getKey();
            final Texture texture = entry.getValue();
            texture.checkCreated();
            GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, point.getGLConstant(), texture.getID(), 0);
            if (point.isColor()) {
                outputBuffers.add(point.getGLConstant());
            }
        }
        // Attach the render buffers
        for (Entry<AttachmentPoint, RenderBuffer> entry : buffers.entrySet()) {
            final AttachmentPoint point = entry.getKey();
            final RenderBuffer buffer = entry.getValue();
            buffer.checkCreated();
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, point.getGLConstant(), GL30.GL_RENDERBUFFER, buffer.getID());
            if (point.isColor()) {
                outputBuffers.add(point.getGLConstant());
            }
        }
        // Set the output to the proper buffers
        if (outputBuffers.isEmpty()) {
            // No color to output
            GL20.glDrawBuffers(GL11.GL_NONE);
        } else {
            // Keep track of the buffers to output
            final IntBuffer buffer = CausticUtil.createIntBuffer(outputBuffers.size());
            final int[] outputBuffersArray = outputBuffers.toArray();
            // Sorting the array ensures that attachments are in order n, n + 1, n + 2...
            // This is important!
            Arrays.sort(outputBuffersArray);
            buffer.put(outputBuffersArray);
            buffer.flip();
            GL20.glDrawBuffers(buffer);
        }
        // Disable input buffers
        GL11.glReadBuffer(GL11.GL_NONE);
        // Check for success
        if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Failed to create the frame buffer");
        }
        // Unbind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        // Update the state
        super.create();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void destroy() {
        checkCreated();
        // Unbind and delete the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL30.glDeleteFramebuffers(id);
        // Release some resources
        textures.clear();
        buffers.clear();
        // Update the state
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void bind() {
        checkCreated();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void unbind() {
        checkCreated();
        // Bind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL30;
    }
}
