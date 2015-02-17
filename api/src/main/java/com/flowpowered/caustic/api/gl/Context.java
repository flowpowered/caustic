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

import java.nio.ByteBuffer;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.Camera;
import com.flowpowered.caustic.api.Creatable;
import com.flowpowered.caustic.api.GLVersioned;
import com.flowpowered.caustic.api.data.UniformHolder;
import com.flowpowered.caustic.api.gl.Texture.InternalFormat;
import com.flowpowered.caustic.api.util.CausticUtil;
import com.flowpowered.caustic.api.util.Rectangle;

/**
 * Represents an OpenGL context. Creating context must be done before any other OpenGL object.
 */
public abstract class Context extends Creatable implements GLVersioned {
    // MSAA value
    protected int msaa = -1;
    // Context uniforms
    protected final UniformHolder uniforms = new UniformHolder();
    // Camera
    protected Camera camera;

    @Override
    public void destroy() {
        uniforms.clear();
        super.destroy();
    }

    /**
     * Creates a new frame buffer.
     *
     * @return A new frame buffer
     */
    public abstract FrameBuffer newFrameBuffer();

    /**
     * Creates a new program.
     *
     * @return A new program
     */
    public abstract Program newProgram();

    /**
     * Creates a new render buffer.
     *
     * @return A new render buffer
     */
    public abstract RenderBuffer newRenderBuffer();

    /**
     * Creates a new shader.
     *
     * @return A new shader
     */
    public abstract Shader newShader();

    /**
     * Creates a new texture.
     *
     * @return A new texture
     */
    public abstract Texture newTexture();

    /**
     * Creates a new vertex array.
     *
     * @return A new vertex array
     */
    public abstract VertexArray newVertexArray();

    /**
     * Returns the window title.
     *
     * @return The window title
     */
    public abstract String getWindowTitle();

    /**
     * Sets the window title to the desired one.
     *
     * @param title The window title
     */
    public abstract void setWindowTitle(String title);

    /**
     * Sets if the window can be re-sized.
     * @param resizable Whether or not the window can be re-sized
     */
    public abstract void setResizable(boolean resizable);

    /**
     * Sets the window size.
     *
     * @param width The width
     * @param height The height
     */
    public void setWindowSize(int width, int height) {
        setWindowSize(new Vector2i(width, height));
    }

    /**
     * Sets the window size.
     *
     * @param windowSize The window size
     */
    public abstract void setWindowSize(Vector2i windowSize);

    /**
     * Returns the window width.
     *
     * @return The window width
     */
    public abstract int getWindowWidth();

    /**
     * Returns the window height.
     *
     * @return The window height
     */
    public abstract int getWindowHeight();

    /**
     * Returns the window size, which is the dimensions of the window.
     *
     * @return The window size
     */
    public Vector2i getWindowSize() {
        return new Vector2i(getWindowWidth(), getWindowHeight());
    }

    /**
     * Updates the display with the current front (screen) buffer.
     */
    public abstract void updateDisplay();

    /**
     * Sets the renderer buffer clear color. This can be interpreted as the background color.
     *
     * @param color The clear color
     */
    public abstract void setClearColor(Vector4f color);

    /**
     * Clears the currently bound buffer (either a frame buffer, or the front (screen) buffer if none are bound).
     */
    public abstract void clearCurrentBuffer();

    /**
     * Disables the capability.
     *
     * @param capability The capability to disable
     */
    public abstract void disableCapability(Capability capability);

    /**
     * Enables the capability.
     *
     * @param capability The capability to enable
     */
    public abstract void enableCapability(Capability capability);

    /**
     * Enables or disables writing into the depth buffer.
     *
     * @param enabled Whether or not to write into the depth buffer.
     */
    public abstract void setDepthMask(boolean enabled);

    /**
     * Sets the blending functions for the source and destination buffers, for all buffers. Blending must be enabled with {@link #enableCapability(com.flowpowered.caustic.api.gl.Context.Capability)}.
     *
     * @param source The source function
     * @param destination The destination function
     */
    public void setBlendingFunctions(BlendFunction source, BlendFunction destination) {
        setBlendingFunctions(-1, source, destination);
    }

    /**
     * Sets the blending functions for the source and destination buffer at the index. Blending must be enabled with {@link #enableCapability(com.flowpowered.caustic.api.gl.Context.Capability)}.
     * <p/>
     * Support for specifying the buffer index is only available in GL40.
     *
     * @param bufferIndex The index of the target buffer
     * @param source The source function
     * @param destination The destination function
     */
    public abstract void setBlendingFunctions(int bufferIndex, BlendFunction source, BlendFunction destination);

    /**
     * Sets the render view port, which is the dimensions and position of the frame inside the window.
     *
     * @param viewPort The view port
     */
    public abstract void setViewPort(Rectangle viewPort);

    /**
     * Reads the current frame pixels and returns it as a byte buffer of the desired format. The size of the returned image data is the same as the current window dimensions.
     *
     * @param size The size of the frame to read
     * @param format The image format to return
     * @return The byte buffer containing the pixel data, according to the provided format
     */
    public abstract ByteBuffer readFrame(Rectangle size, InternalFormat format);

    /**
     * Returns true if an external process (such as the user) is requesting for the window to be closed. This value is reset once this method has been called.
     *
     * @return Whether or not the window is being requested to close
     */
    public abstract boolean isWindowCloseRequested();

    /**
     * Uploads the renderer uniforms to the desired program.
     *
     * @param program The program to upload to
     */
    public void uploadUniforms(Program program) {
        CausticUtil.checkVersion(this, program);
        program.upload(uniforms);
    }

    /**
     * Sets the render camera. Will be use for all subsequent render calls, until changed again.
     *
     * @param camera The camera to use
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Returns the current renderer camera.
     *
     * @return The camera
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Sets the MSAA value. Must be greater or equal to zero. Zero means no MSAA.
     *
     * @param value The MSAA value, greater or equal to zero
     */
    public void setMSAA(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("MSAA value must be greater or equal to zero");
        }
        this.msaa = value;
    }

    /**
     * Returns the Uniforms for this renderer
     *
     * @return The renderer uniforms
     */
    public UniformHolder getUniforms() {
        return uniforms;
    }

    /**
     * An enum of the renderer capabilities.
     */
    public static enum Capability {
        BLEND(0xBE2), // GL11.GL_BLEND
        CULL_FACE(0xB44), // GL11.GL_CULL_FACE
        DEPTH_CLAMP(0x864F), // GL32.GL_DEPTH_CLAMP
        DEPTH_TEST(0xB71); // GL11.GL_DEPTH_TEST
        private final int glConstant;

        private Capability(int glConstant) {
            this.glConstant = glConstant;
        }

        /**
         * Returns the OpenGL constant associated to the capability.
         *
         * @return The OpenGL constant
         */
        public int getGLConstant() {
            return glConstant;
        }
    }

    /**
     * An enum of the blending functions.
     */
    public static enum BlendFunction {
        GL_ZERO(0x0), // GL11.GL_ZERO
        GL_ONE(0x1), // GL11.GL_ONE
        GL_SRC_COLOR(0x300), // GL11.GL_SRC_COLOR
        GL_ONE_MINUS_SRC_COLOR(0x301), // GL11.GL_ONE_MINUS_SRC_COLOR
        GL_DST_COLOR(0x306), // GL11.GL_DST_COLOR
        GL_ONE_MINUS_DST_COLOR(0x307), // GL11.GL_ONE_MINUS_DST_COLOR
        GL_SRC_ALPHA(0x302), // GL11.GL_SRC_ALPHA
        GL_ONE_MINUS_SRC_ALPHA(0x303), // GL11.GL_ONE_MINUS_SRC_ALPHA
        GL_DST_ALPHA(0x304), // GL11.GL_DST_ALPHA
        GL_ONE_MINUS_DST_ALPHA(0x305), // GL11.GL_ONE_MINUS_DST_ALPHA
        GL_CONSTANT_COLOR(0x8001), // GL11.GL_CONSTANT_COLOR
        GL_ONE_MINUS_CONSTANT_COLOR(0x8002), // GL11.GL_ONE_MINUS_CONSTANT_COLOR
        GL_CONSTANT_ALPHA(0x8003), // GL11.GL_CONSTANT_ALPHA
        GL_ONE_MINUS_CONSTANT_ALPHA(0x8004), // GL11.GL_ONE_MINUS_CONSTANT_ALPHA
        GL_SRC_ALPHA_SATURATE(0x308), // GL11.GL_SRC_ALPHA_SATURATE
        GL_SRC1_COLOR(0x88F9), // GL33.GL_SRC1_COLOR
        GL_ONE_MINUS_SRC1_COLOR(0x88FA), // GL33.GL_ONE_MINUS_SRC1_COLOR
        GL_SRC1_ALPHA(0x8589), // GL33.GL_SRC1_ALPHA
        GL_ONE_MINUS_SRC1_ALPHA(0x88FB); // GL33.GL_ONE_MINUS_SRC1_ALPHA
        private final int glConstant;

        private BlendFunction(int glConstant) {
            this.glConstant = glConstant;
        }

        /**
         * Returns the OpenGL constant associated to the blending function.
         *
         * @return The OpenGL constant
         */
        public int getGLConstant() {
            return glConstant;
        }
    }
}
