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

import java.util.HashSet;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.PixelFormat;

import org.spout.renderer.Camera;
import org.spout.renderer.Model;
import org.spout.renderer.Renderer;
import org.spout.renderer.gl20.OpenGL20Material;
import org.spout.renderer.gl20.OpenGL20Program;
import org.spout.renderer.util.RenderUtil;

/**
 * This is a renderer using OpenGL 3.0. To create a new render window, start by creating a camera
 * and setting it using {@link #setCamera(org.spout.renderer.Camera)}, then use {@link #create()} to
 * create the OpenGL context. To add and remove models, use {@link #addModel(Model)} and {@link
 * #removeModel(Model)}. The camera position and rotation can be modified by accessing it with
 * {@link #getCamera()}. When done, use {@link #destroy()} to destroy the render window.
 */
public class OpenGL30Renderer extends Renderer {
	// Models
	private final Set<OpenGL30Model> models = new HashSet<>();

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Renderer has already been created");
		}
		if (camera == null) {
			throw new IllegalStateException("Camera has not been set");
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
		// Set the clear color, which will be the color of empty screen area
		GL11.glClearColor(backgroundColor.getRed() / 255f, backgroundColor.getGreen() / 255f,
				backgroundColor.getBlue() / 255f, backgroundColor.getAlpha() / 255f);
		// Enable dept testing to properly display depth
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		if (isCullingEnabled()) {
			//Enable culling of the back face
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
		}
		// Enable the depth mask
		GL11.glDepthMask(true);
		RenderUtil.checkForOpenGLError();
		// Update the state
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Destroy models
		for (OpenGL30Model model : models) {
			model.destroy();
			if (model.getMaterial().isCreated()) {
				model.getMaterial().destroy();
			}
		}
		// Clear data
		models.clear();
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
	public void render() {
		checkCreated();
		// Clear the last render
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// Render all the models
		for (OpenGL30Model model : models) {
			if (model.isCreated()) {
				final OpenGL30Material material = model.getMaterial();
				final OpenGL30Program program = material.getProgram();
				// Get the camera, checking in order of priority
				final Camera camera;
				if (model.hasCamera()) {
					camera = model.getCamera();
				} else if (material.hasCamera()) {
					camera = material.getCamera();
				} else {
					camera = this.camera;
				}
				// Upload the camera
				uniforms.getMatrix4("cameraMatrix").set(camera.getMatrix());
				uniforms.getMatrix4("projectionMatrix").set(camera.getProjectionMatrix());
				// Bind the material
				material.bind();
				// Upload the renderer uniforms
				program.upload(uniforms);
				// Upload the material uniforms
				program.upload(material.getUniforms());
				// Upload the model uniforms
				program.upload(model.getUniforms());
				// Render the model
				model.render();
				// Unbind the material
				material.unbind();
			}
		}
		// Check for errors
		RenderUtil.checkForOpenGLError();
		// Update the display, 60 FPS
		Display.sync(60);
		Display.update();
	}

	@Override
	public void addModel(Model model) {
		checkModelVersion(model);
		final OpenGL30Model gl30Model = (OpenGL30Model) model;
		models.add(gl30Model);
	}

	@Override
	public void removeModel(Model model) {
		checkModelVersion(model);
		final OpenGL30Model gl30Model = (OpenGL30Model) model;
		models.remove(gl30Model);
	}

	private void checkModelVersion(Model model) {
		if (!(model instanceof OpenGL30Model)) {
			throw new IllegalArgumentException("Version mismatch: expected OpenGL30Model, got "
					+ model.getClass().getSimpleName());
		}
	}
}
