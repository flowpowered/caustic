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
package org.spout.renderer.gl30;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import org.spout.renderer.GLVersion;
import org.spout.renderer.data.RenderList;
import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.Material;
import org.spout.renderer.Model;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Renderer;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 3.0 implementation of {@link Renderer}.
 *
 * @see Renderer
 */
public class OpenGL30Renderer extends Renderer {
	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Renderer has already been created");
		}
		// Attempt to create the display
		try {
			Display.setDisplayMode(new DisplayMode(windowWidth, windowHeight));
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true));
		} catch (LWJGLException ex) {
			throw new RuntimeException(ex);
		}
		// Set the title
		Display.setTitle(windowTitle);
		// Set the view port to the window
		GL11.glViewport(0, 0, windowWidth, windowHeight);
		// Set the alpha blending function for transparency
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// Check for errors
		RenderUtil.checkForOpenGLError();
		// Update the state
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Display goes after else there's no context in which to check for an error
		RenderUtil.checkForOpenGLError();
		Display.destroy();
		super.destroy();
	}

	@Override
	public void setClearColor(Color color) {
		GL11.glClearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void enable(Capability capability) {
		GL11.glEnable(capability.getGLConstant());
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void disable(Capability capability) {
		GL11.glDisable(capability.getGLConstant());
		// Check for errors
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void uploadUniforms(Program program) {
		program.upload(uniforms);
	}

	@Override
	public void render() {
		checkCreated();
		// Clear the last render on the screen buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// Keep track of all cleared frame buffers so we don't clear one twice
		final Set<FrameBuffer> clearedFrameBuffers = new HashSet<>();
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
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
					clearedFrameBuffers.add(frameBuffer);
				}
			}
			for (Model model : renderList) {
				final Material material = model.getMaterial();
				final Program program = material.getProgram();
				// Bind the material
				material.bind();
				// Upload the renderer uniforms
				uploadUniforms(program);
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
			}
		}
		// Check for errors
		RenderUtil.checkForOpenGLError();
		// Update the display
		Display.update();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL30;
	}
}
