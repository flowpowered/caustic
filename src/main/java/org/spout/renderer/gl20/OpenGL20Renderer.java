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
import org.lwjgl.opengl.PixelFormat;

import org.spout.renderer.Model;
import org.spout.renderer.Renderer;
import org.spout.renderer.data.Uniform.Matrix4Uniform;
import org.spout.renderer.util.RenderUtil;

/**
 * This is a renderer using OpenGL 2.0. To create a new render window, start by creating a camera
 * and setting it using {@link #setCamera(org.spout.renderer.Camera)}, then use {@link #create()} to
 * create the OpenGL context. To add and remove models, use {@link #addModel(org.spout.renderer.Model)}
 * and {@link #removeModel(org.spout.renderer.Model)}. The camera position and rotation can be
 * modified by accessing it with {@link #getCamera()}. When done, use {@link #destroy()} to destroy
 * the render window.
 */
public class OpenGL20Renderer extends Renderer {
	// Models
	private final Set<OpenGL20Model> models = new HashSet<>();
	private final Map<OpenGL20Material, Set<OpenGL20Model>> modelsForMaterial = new HashMap<>();

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Renderer has already been created");
		}
		if (camera == null) {
			throw new IllegalStateException("Camera has not been set");
		}
		final PixelFormat pixelFormat = new PixelFormat();
		final ContextAttribs contextAttributes = new ContextAttribs(2, 1);
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
		for (OpenGL20Model model : models) {
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
		// Display goes after else there's no context in which to check for an error
		RenderUtil.checkForOpenGLError();
		Display.destroy();
		camera = null;
		super.destroy();
	}

	@Override
	public void render() {
		checkCreated();
		// Clear the last render
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		// Update the constant renderer uniforms
		uniforms.getMatrix4("cameraMatrix").set(camera.getMatrix());
		uniforms.getMatrix4("projectionMatrix").set(camera.getProjectionMatrix());
		// Render all models, by material
		for (Entry<OpenGL20Material, Set<OpenGL20Model>> entry : modelsForMaterial.entrySet()) {
			final OpenGL20Material material = entry.getKey();
			final Set<OpenGL20Model> models = entry.getValue();
			// Bind the material
			material.bind();
			// Upload the renderer uniforms
			material.getProgram().upload(uniforms);
			// Upload the material uniforms
			material.uploadUniforms();
			// Iterate the model list and render each one
			for (OpenGL20Model model : models) {
				if (model.isCreated()) {
					model.render();
				}
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

	@Override
	public void addModel(Model model) {
		checkModelVersion(model);
		final OpenGL20Model gl20Model = (OpenGL20Model) model;
		models.add(gl20Model);
		final OpenGL20Material material = gl20Model.getMaterial();
		final Set<OpenGL20Model> modelSet = modelsForMaterial.get(material);
		if (modelSet == null) {
			final Set<OpenGL20Model> set = new HashSet<>();
			set.add(gl20Model);
			modelsForMaterial.put(material, set);
			return;
		}
		modelSet.add(gl20Model);
	}

	@Override
	public void removeModel(Model model) {
		checkModelVersion(model);
		final OpenGL20Model gl20Model = (OpenGL20Model) model;
		models.remove(gl20Model);
		final OpenGL20Material material = gl20Model.getMaterial();
		final Set<OpenGL20Model> modelSet = modelsForMaterial.get(material);
		if (modelSet == null) {
			return;
		}
		modelSet.remove(gl20Model);
		if (modelSet.isEmpty()) {
			modelsForMaterial.remove(material);
		}
	}

	private void checkModelVersion(Model model) {
		if (!(model instanceof OpenGL20Model)) {
			throw new IllegalArgumentException("Version mismatch: expected OpenGL20Model, got "
					+ model.getClass().getSimpleName());
		}
	}
}
