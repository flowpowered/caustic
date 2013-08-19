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

import org.spout.renderer.android.gles20.GLES20FrameBuffer;
import org.spout.renderer.android.gles20.GLES20Program;
import org.spout.renderer.android.gles20.GLES20RenderBuffer;
import org.spout.renderer.android.gles20.GLES20Renderer;
import org.spout.renderer.android.gles20.GLES20Shader;
import org.spout.renderer.android.gles20.GLES20Texture;
import org.spout.renderer.android.gles20.GLES20VertexArray;
import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.RenderBuffer;
import org.spout.renderer.gl.Renderer;
import org.spout.renderer.gl.Shader;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.gl.VertexArray;
import org.spout.renderer.lwjgl.gl20.GL20FrameBuffer;
import org.spout.renderer.lwjgl.gl20.GL20Program;
import org.spout.renderer.lwjgl.gl20.GL20RenderBuffer;
import org.spout.renderer.lwjgl.gl20.GL20Renderer;
import org.spout.renderer.lwjgl.gl20.GL20Shader;
import org.spout.renderer.lwjgl.gl20.GL20Texture;
import org.spout.renderer.lwjgl.gl20.GL20VertexArray;
import org.spout.renderer.lwjgl.gl30.GL30FrameBuffer;
import org.spout.renderer.lwjgl.gl30.GL30Program;
import org.spout.renderer.lwjgl.gl30.GL30RenderBuffer;
import org.spout.renderer.lwjgl.gl30.GL30Renderer;
import org.spout.renderer.lwjgl.gl30.GL30Texture;
import org.spout.renderer.lwjgl.gl30.GL30VertexArray;

/**
 * An enum of the supported OpenGL versions. Use this class to generate rendering objects compatible with the version.
 */
public enum GLVersion {
	GL20,
	GL30,
	GLES20,
	GLES30;

	/**
	 * Creates a new renderer for the version.
	 *
	 * @return A new renderer
	 */
	public Renderer createRenderer() {
		switch (this) {
			case GL20:
				return new GL20Renderer();
			case GL30:
				return new GL30Renderer();
			case GLES20:
				return new GLES20Renderer();
			default:
				return null;
		}
	}

	/**
	 * Creates a new vertex array for the version.
	 *
	 * @return A new vertex array
	 */
	public VertexArray createVertexArray() {
		switch (this) {
			case GL20:
				return new GL20VertexArray();
			case GL30:
				return new GL30VertexArray();
			case GLES20:
				return new GLES20VertexArray();
			default:
				return null;
		}
	}

	/**
	 * Creates a new program for the version.
	 *
	 * @return A new program
	 */
	public Program createProgram() {
		switch (this) {
			case GL20:
				return new GL20Program();
			case GL30:
				return new GL30Program();
			case GLES20:
				return new GLES20Program();
			default:
				return null;
		}
	}

	/**
	 * Creates a new shader for the version.
	 *
	 * @return A new shader
	 */
	public Shader createShader() {
		switch (this) {
			case GL20:
			case GL30:
				return new GL20Shader();
			case GLES20:
			case GLES30:
				return new GLES20Shader();
			default:
				return null;
		}
	}

	/**
	 * Creates a new texture for the version.
	 *
	 * @return A new texture
	 */
	public Texture createTexture() {
		switch (this) {
			case GL20:
				return new GL20Texture();
			case GL30:
				return new GL30Texture();
			case GLES20:
				return new GLES20Texture();
			default:
				return null;
		}
	}

	/**
	 * Creates a new texture for the version.
	 *
	 * @return A new texture
	 */
	public FrameBuffer createFrameBuffer() {
		switch (this) {
			case GL20:
				return new GL20FrameBuffer();
			case GL30:
				return new GL30FrameBuffer();
			case GLES20:
				return new GLES20FrameBuffer();
			default:
				return null;
		}
	}

	/**
	 * Creates a new texture for the version.
	 *
	 * @return A new texture
	 */
	public RenderBuffer createRenderBuffer() {
		switch (this) {
			case GL20:
				return new GL20RenderBuffer();
			case GL30:
				return new GL30RenderBuffer();
			case GLES20:
				return new GLES20RenderBuffer();
			default:
				return null;
		}
	}
}
