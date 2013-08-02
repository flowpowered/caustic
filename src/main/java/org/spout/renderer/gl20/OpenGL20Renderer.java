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
package org.spout.renderer.gl20;

import java.util.Map;
import java.util.TreeMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.GLVersion;
import org.spout.renderer.gl.Material;
import org.spout.renderer.gl.Model;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Renderer;
import org.spout.renderer.data.RenderList;
import org.spout.renderer.util.RenderUtil;

/**
 * An OpenGL 2.0 implementation of {@link Renderer}.
 *
 * @see Renderer
 */
public class OpenGL20Renderer extends Renderer {
	// Models
	private final Map<String, RenderList> renderLists = new TreeMap<>();

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Renderer has already been created");
		}
		// Attempt to create the display
		try {
			Display.setDisplayMode(new DisplayMode(windowWidth, windowHeight));
			Display.create(new PixelFormat(), new ContextAttribs(2, 1));
		} catch (LWJGLException ex) {
			throw new RuntimeException(ex);
		}
		// Set the title
		Display.setTitle(windowTitle);
		// Set the view port to the window
		GL11.glViewport(0, 0, windowWidth, windowHeight);
		// Set the clear color, which will be the color of empty screen area
		GL11.glClearColor(backgroundColor.getRed() / 255f, backgroundColor.getGreen() / 255f,
				backgroundColor.getBlue() / 255f, backgroundColor.getAlpha() / 255f);
		// Enable dept testing to properly display depth
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		if (cullingEnabled) {
			// Enable culling of the back face
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
		}
		// Enable the depth mask
		GL11.glDepthMask(true);
		// Check for errors
		RenderUtil.checkForOpenGLError();
		// Update the state
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Destroy models and materials
		for (RenderList renderList : renderLists.values()) {
			for (Model model : renderList) {
				if (model.isCreated()) {
					model.destroy();
				}
				final Material material = model.getMaterial();
				if (material != null && material.isCreated()) {
					model.getMaterial().destroy();
				}
			}
			final FrameBuffer frameBuffer = renderList.getFrameBuffer();
			if (frameBuffer != null && frameBuffer.isCreated()) {
				frameBuffer.destroy();
			}
		}
		// Clear data
		renderLists.clear();
		uniforms.clear();
		// Destroy display
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		// Display goes after else there's no context in which to check for an error
		RenderUtil.checkForOpenGLError();
		Display.destroy();
		super.destroy();
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
		// Render all the created models
		for (RenderList renderList : renderLists.values()) {
			if (!renderList.isActive()) {
				continue;
			}
			final FrameBuffer frameBuffer = renderList.getFrameBuffer();
			if (frameBuffer != null) {
				frameBuffer.bind();
				// Clear the last render on the frame buffer
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			}
			for (Model model : renderList) {
				if (!model.isCreated()) {
					continue;
				}
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
	public RenderList getRenderList(String name) {
		return renderLists.get(name);
	}

	@Override
	public boolean hasRenderList(String name) {
		return renderLists.containsKey(name);
	}

	@Override
	public void addRenderList(RenderList list) {
		renderLists.put(list.getName(), list);
	}

	@Override
	public void removeRenderList(String name) {
		renderLists.remove(name);
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GL20;
	}
}
