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
package org.spout.renderer.android.gles20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import org.spout.math.matrix.Matrix4;
import org.spout.renderer.Material;
import org.spout.renderer.android.AndroidUtil;
import org.spout.renderer.data.Color;
import org.spout.renderer.data.RenderList;
import org.spout.renderer.gl.Capability;
import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Renderer;
import org.spout.renderer.gl.Texture.Format;
import org.spout.renderer.model.Model;
import org.spout.renderer.util.CausticUtil;
import org.spout.renderer.util.Rectangle;

/**
 * An OpenGLES 2.0 implementation of {@link org.spout.renderer.gl.Renderer}.
 *
 * @see org.spout.renderer.gl.Renderer
 */
public class GLES20Renderer extends Renderer implements GLSurfaceView.Renderer {
	protected GLES20Renderer() {
	}

	@Override
	public void create() {
		if (isCreated()) {
			throw new IllegalStateException("Renderer has already been created");
		}
		// TODO: Attempt to create the display
		/*
		Display.setDisplayMode(new DisplayMode(windowWidth, windowHeight));
		Display.create(new PixelFormat().withSamples(MSAA), createContextAttributes());
		// Set the title
		Display.setTitle(windowTitle);
		*/
		// Set the alpha blending function for transparency
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// Check for errors
		AndroidUtil.checkForGLESError();
		// Update the state
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Display goes after else there's no context in which to check for an error
		AndroidUtil.checkForGLESError();
		// TODO: Destroy the display
		// Display.destroy();
		super.destroy();
	}

	@Override
	public void setClearColor(Color color) {
		Color normC = color.normalize();
		GLES20.glClearColor(normC.getRed(), normC.getGreen(), normC.getBlue(), normC.getAlpha());
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public void enable(Capability capability) {
		GLES20.glEnable(capability.getGLConstant());
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public ByteBuffer readCurrentFrame(Format format) {
		ByteBuffer buffer = CausticUtil.createByteBuffer(windowWidth * windowHeight * 3);
		GLES20.glReadPixels(0, 0, windowWidth, windowHeight, format.getGLConstant(), GLES20.GL_UNSIGNED_BYTE, buffer);
		// Check for errors
		AndroidUtil.checkForGLESError();
		return buffer;
	}

	@Override
	public void disable(Capability capability) {
		GLES20.glDisable(capability.getGLConstant());
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public void uploadUniforms(Program program) {
		program.upload(uniforms);
	}

	@Override
	public void render() {
		checkCreated();
		// Clear the last render on the screen buffer
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		// Keep track of all cleared frame buffers so we don't clear one twice
		final Set<FrameBuffer> clearedFrameBuffers = new HashSet<>();
		// Set the default view port
		GLES20.glViewport(0, 0, windowWidth, windowHeight);
		// Render all the created models
		for (RenderList renderList : renderLists) {
			if (!renderList.isActive()) {
				continue;
			}
			// Update the context capabilities
			setCapabilities(renderList.getCapabilities());
			// Bind the frame buffer if present
			final FrameBuffer frameBuffer = renderList.getFrameBuffer();
			if (frameBuffer != null) {
				frameBuffer.bind();
				if (!clearedFrameBuffers.contains(frameBuffer)) {
					// Clear the last render on the frame buffer
					GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
					clearedFrameBuffers.add(frameBuffer);
				}
				// Set the view port if necessary
				if (frameBuffer.hasViewPort()) {
					final Rectangle viewPort = frameBuffer.getViewPort();
					GLES20.glViewport(viewPort.getX(), viewPort.getY(), viewPort.getWidth(), viewPort.getHeight());
				}
			}
			for (Model model : renderList) {
				final Material material = model.getMaterial();
				final Program program = material.getProgram();
				// Bind the material
				material.bind();
				// Upload the renderer uniforms
				uploadUniforms(program);
				// Generate the normal matrix for the model
				final Matrix4 modelMatrix = model.getMatrix();
				final Matrix4 viewMatrix = renderList.getCamera().getViewMatrix();
				final Matrix4 normalMatrix = viewMatrix.mul(modelMatrix).invert().transpose();
				renderList.getUniforms().getMatrix4("normalMatrix").set(normalMatrix);
				// Upload the render list uniforms
				renderList.uploadUniforms(program);
				// Upload the material uniforms
				material.uploadUniforms();
				// Upload the model uniforms
				model.uploadUniforms();
				// Render the model
				model.render();
				// Unbind the material
				material.unbind();
			}
			if (frameBuffer != null) {
				frameBuffer.unbind();
				// Reset the view port if necessary
				if (frameBuffer.hasViewPort()) {
					GLES20.glViewport(0, 0, this.windowWidth, this.windowHeight);
				}
			}
		}
		// Check for errors
		AndroidUtil.checkForGLESError();
		// Update the display

		// TODO: updated the display: Display.update();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GLES20;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		create();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		render();
	}
}
