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
    private final TIntSet outputBuffers = new TIntHashSet();

    protected GL32FrameBuffer() {
    }

    @Override
    public void create() {
        checkNotCreated();
        // Generate and bind the frame buffer
        id = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Disable input buffers
        GL11.glReadBuffer(GL11.GL_NONE);
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
        // Clear output buffers
        outputBuffers.clear();
        // Update the state
        super.destroy();
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void attach(AttachmentPoint point, Texture texture) {
        checkCreated();
        CausticUtil.checkVersion(this, texture);
        texture.checkCreated();
        // Bind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Attach the texture
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, point.getGLConstant(), texture.getID(), 0);
        // Add it to the color outputs if it's a color type
        if (point.isColor()) {
            outputBuffers.add(point.getGLConstant());
        }
        // Update the list of output buffers
        updateOutputBuffers();
        // Unbind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void attach(AttachmentPoint point, RenderBuffer buffer) {
        checkCreated();
        CausticUtil.checkVersion(this, buffer);
        buffer.checkCreated();
        // Bind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Attach the render buffer
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, point.getGLConstant(), GL30.GL_RENDERBUFFER, buffer.getID());
        // Add it to the color outputs if it's a color type
        if (point.isColor()) {
            outputBuffers.add(point.getGLConstant());
        }
        // Update the list of output buffers
        updateOutputBuffers();
        // Unbind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void detach(AttachmentPoint point) {
        checkCreated();
        // Bind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Detach the render buffer
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, point.getGLConstant(), GL30.GL_RENDERBUFFER, 0);
        // Remove it from the color outputs if it's a color type
        if (point.isColor()) {
            outputBuffers.remove(point.getGLConstant());
        }
        // Update the list of output buffers
        updateOutputBuffers();
        // Unbind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    private void updateOutputBuffers() {
        // Set the output to the proper buffers
        if (outputBuffers.isEmpty()) {
            // No color to output
            GL20.glDrawBuffers(GL11.GL_NONE);
        } else {
            // Keep track of the buffers to output
            final int[] outputBuffersArray = outputBuffers.toArray();
            // Sorting the array ensures that attachments are in order n, n + 1, n + 2...
            // This is important!
            Arrays.sort(outputBuffersArray);
            final IntBuffer buffer = CausticUtil.createIntBuffer(outputBuffers.size());
            buffer.put(outputBuffersArray);
            buffer.flip();
            GL20.glDrawBuffers(buffer);
        }
    }

    @Override
    public boolean isComplete() {
        checkCreated();
        // Bind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Fetch the status and compare to the complete enum value
        final boolean complete = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
        // Unbind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
        return complete;
    }

    @Override
    public void bind() {
        checkCreated();
        // Bind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, id);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public void unbind() {
        checkCreated();
        // Unbind the frame buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        // Check for errors
        LWJGLUtil.checkForGLError();
    }

    @Override
    public GLVersion getGLVersion() {
        return GLVersion.GL32;
    }
}
