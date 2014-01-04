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

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Set;

import org.spout.math.vector.Vector2f;
import org.spout.renderer.Camera;
import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.data.Color;
import org.spout.renderer.data.UniformHolder;
import org.spout.renderer.gl.Texture.Format;
import org.spout.renderer.util.CausticUtil;
import org.spout.renderer.util.Rectangle;

/**
 * Represents an OpenGL context. Creating context must be done before any other OpenGL object.
 */
public abstract class Context extends Creatable implements GLVersioned {
	// Window title
	protected String windowTitle = "Caustic";
	// Window size
	protected Vector2f windowSize;
	// MSAA value
	protected int msaa = 0;
	// Properties
	protected final Set<Capability> capabilities = EnumSet.noneOf(Capability.class);
	// Context uniforms
	protected final UniformHolder uniforms = new UniformHolder();
	// Camera
	protected Camera camera;

	@Override
	public void destroy() {
		capabilities.clear();
		uniforms.clear();
		super.destroy();
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
	public abstract void setClearColor(Color color);

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
     * Sets the blending functions for the source and destination buffers, for all buffers. Blending must be enabled with {@link #enableCapability(org.spout.renderer.gl.Context.Capability)}.
     *
     * @param source The source function
     * @param destination The destination function
     */
    public void setBlendingFunctions(BlendFunction source, BlendFunction destination) {
        setBlendingFunctions(-1, source, destination);
    }

    /**
     * Sets the blending functions for the source and destination buffer at the index. Blending must be enabled with {@link #enableCapability(org.spout.renderer.gl.Context.Capability)}.
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
	public abstract ByteBuffer readCurrentFrame(Rectangle size, Format format);

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
	 * Returns the window title.
	 *
	 * @return The window title
	 */
	public String setWindowTitle() {
		return windowTitle;
	}

	/**
	 * Sets the window title to the desired one.
	 *
	 * @param title The window title
	 */
	public void setWindowTitle(String title) {
		this.windowTitle = title;
	}

	/**
	 * Sets the window size.
	 *
	 * @param width The width
	 * @param height The height
	 */
	public void setWindowSize(int width, int height) {
		setWindowSize(new Vector2f(width, height));
	}

	/**
	 * Sets the window size.
	 *
	 * @param windowSize The window size (floored)
	 */
	public void setWindowSize(Vector2f windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * Returns the window width.
	 *
	 * @return The window width
	 */
	public int getWindowWidth() {
		return windowSize.getFloorX();
	}

	/**
	 * Returns the window height.
	 *
	 * @return The window height
	 */
	public int getWindowHeight() {
		return windowSize.getFloorY();
	}

	/**
	 * Returns the window size, which is the dimensions of the window.
	 *
	 * @return The window size
	 */
	public Vector2f getWindowSize() {
		return windowSize;
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
