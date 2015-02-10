/*
 * This file is part of Caustic LWJGL, licensed under the MIT License (MIT).
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
package com.flowpowered.caustic.lwjgl.gl30;

import java.nio.IntBuffer;
import java.util.Arrays;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.flowpowered.caustic.api.gl.FrameBuffer;
import com.flowpowered.caustic.api.gl.RenderBuffer;
import com.flowpowered.caustic.api.gl.Texture;
import com.flowpowered.caustic.api.util.CausticUtil;
import com.flowpowered.caustic.lwjgl.LWJGLUtil;

/**
 * An OpenGL 3.0 implementation of {@link FrameBuffer}.
 *
 * @see FrameBuffer
 */
public class GL30FrameBuffer extends FrameBuffer {
    private final TIntSet outputBuffers = new TIntHashSet();

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
        // Delete the frame buffer
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
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, point.getGLConstant(), GL11.GL_TEXTURE_2D, texture.getID(), 0);
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
        return GLVersion.GL30;
    }
}
