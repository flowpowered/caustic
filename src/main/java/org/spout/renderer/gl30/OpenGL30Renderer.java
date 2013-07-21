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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.PixelFormat;

import org.spout.renderer.Renderer;
import org.spout.renderer.data.Uniform.Matrix4Uniform;
import org.spout.renderer.gl20.OpenGL20Material;
import org.spout.renderer.util.RenderUtil;

/**
 * This is a renderer using OpenGL 3.0. To create a new render window, start by creating a camera
 * and setting it using {@link #setCamera(org.spout.renderer.Camera)}, then use {@link #create()} to
 * create the OpenGL context. To add and remove models, use {@link #addModel(OpenGL30Model)} and
 * {@link #removeModel(OpenGL30Model)}. The camera position and rotation can be modified by
 * accessing it with {@link #getCamera()}. When done, use {@link #destroy()} to destroy the render
 * window.
 */
public class OpenGL30Renderer extends Renderer {
	// Models
	private final Set<OpenGL30Model> models = new HashSet<>();
	private final Map<OpenGL20Material, Set<OpenGL30Model>> modelsForMaterial = new HashMap<>();
	// Properties
	private Color backgroundColor = new Color(0.2f, 0.2f, 0.2f, 0);

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Renderer has already been created");
		}
		if (camera == null) {
			throw new IllegalStateException("Camera cannot has not been set");
		}
		final PixelFormat pixelFormat = new PixelFormat();
		final ContextAttribs contextAttributes = new ContextAttribs(3, 2).withProfileCore(true);
		try {
			Display.setDisplayMode(new DisplayMode(windowWidth, windowHeight));
			Display.create(pixelFormat, contextAttributes);
		} catch (LWJGLException ex) {
			throw new RuntimeException(ex);
		}
		Display.setTitle(windowTitle);
		GL11.glViewport(0, 0, windowWidth, windowHeight);
		GL11.glClearColor(backgroundColor.getRed() / 255f, backgroundColor.getGreen() / 255f,
				backgroundColor.getBlue() / 255f, backgroundColor.getAlpha() / 255f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		GL11.glDepthMask(true);
		RenderUtil.checkForOpenGLError();
		uniforms.add(new Matrix4Uniform("projectionMatrix", camera.getProjectionMatrix()));
		uniforms.add(new Matrix4Uniform("cameraMatrix", camera.getMatrix()));
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Destroy models
		for (OpenGL30Model model : models) {
			model.destroy();
		}
		// Destroy materials
		for (OpenGL20Material material : modelsForMaterial.keySet()) {
			material.destroy();
		}
		// Clear data
		models.clear();
		modelsForMaterial.clear();
		uniforms.clear();
		// Destroy display
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		RenderUtil.checkForOpenGLError();
		// Display goes after else there's no context in which to check for an error
		Display.destroy();
		camera = null;
		super.destroy();
	}

	private void checkCreated() {
		if (!created) {
			throw new IllegalStateException("Renderer has not been created yet");
		}
	}

	/**
	 * Draws all the models that have been created to the screen.
	 */
	public void render() {
		checkCreated();
		// Clear the last render
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// Update the constant renderer uniforms
		uniforms.getMatrix4("cameraMatrix").set(camera.getMatrix());
		uniforms.getMatrix4("projectionMatrix").set(camera.getProjectionMatrix());
		// Render all models, by material
		for (Entry<OpenGL20Material, Set<OpenGL30Model>> entry : modelsForMaterial.entrySet()) {
			// Get the material and the model list
			final OpenGL20Material material = entry.getKey();
			final Set<OpenGL30Model> models = entry.getValue();
			// Bind the material (binds the program)
			material.bind();
			// Upload the renderer uniforms
			material.getProgram().upload(uniforms);
			// Upload the material uniforms
			material.uploadUniforms();
			// Iterate the model list and render each one, skipping uncreated models
			for (OpenGL30Model model : models) {
				if (!model.isCreated()) {
					continue;
				}
				model.render();
			}
			// Unbind the material
			material.unbind();
		}
		// Check for errors
		RenderUtil.checkForOpenGLError();
		// Update the display, 60 FPS
		Display.sync(60);
		Display.update();
	}

	/**
	 * Adds a model to the list. If a non-created model is added to the list, it will not be rendered
	 * until it is created.
	 *
	 * @param model The model to add
	 */
	public void addModel(OpenGL30Model model) {
		models.add(model);
		final OpenGL20Material material = model.getMaterial();
		final Set<OpenGL30Model> modelSet = modelsForMaterial.get(material);
		if (modelSet == null) {
			final Set<OpenGL30Model> set = new HashSet<>();
			set.add(model);
			modelsForMaterial.put(material, set);
			return;
		}
		modelSet.add(model);
	}

	/**
	 * Removes a model from the list.
	 *
	 * @param model The model to remove
	 */
	public void removeModel(OpenGL30Model model) {
		models.remove(model);
		final OpenGL20Material material = model.getMaterial();
		final Set<OpenGL30Model> modelSet = modelsForMaterial.get(material);
		if (modelSet == null) {
			return;
		}
		modelSet.remove(model);
		if (modelSet.isEmpty()) {
			modelsForMaterial.remove(material);
		}
	}

	/**
	 * Gets the background color.
	 *
	 * @return The background color
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * Sets the background color.
	 *
	 * @param color The background color
	 */
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}
}
