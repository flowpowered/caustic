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
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.PixelFormat;

import org.spout.math.vector.Vector3;
import org.spout.renderer.Renderer;
import org.spout.renderer.gl20.OpenGL20Program;
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
	// Model data
	private final List<OpenGL30Model> models = new ArrayList<>();
	// Shaders
	private final OpenGL20Program shaders = new OpenGL20Program();
	// Uniforms
	private Vector3 lightPosition = new Vector3(0, 0, 0);
	private float diffuseIntensity = 0.8f;
	private float specularIntensity = 0.2f;
	private float ambientIntensity = 0.3f;
	private Color backgroundColor = new Color(0.2f, 0.2f, 0.2f, 0);
	private float lightAttenuation = 0.03f;

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Renderer has already been created.");
		}
		if (camera == null) {
			throw new IllegalStateException("Camera cannot be null");
		}
		createDisplay();
		createShaders();
		super.create();
	}

	@Override
	public void destroy() {
		if (!created) {
			throw new IllegalStateException("Renderer has not been created yet.");
		}
		destroyModels();
		destroyShaders();
		destroyDisplay();
		camera = null;
		super.destroy();
	}

	private void createDisplay() {
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
	}

	private void createShaders() {
		shaders.setVertexShaderSource(OpenGL30Renderer.class.getResourceAsStream("/basic.vert"));
		shaders.setFragmentShaderSource(OpenGL30Renderer.class.getResourceAsStream("/basic.frag"));
		shaders.create();
	}

	private void destroyDisplay() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		RenderUtil.checkForOpenGLError();
		Display.destroy();
	}

	private void destroyShaders() {
		GL20.glUseProgram(0);
		shaders.destroy();
	}

	private void destroyModels() {
		for (OpenGL30Model solid : models) {
			solid.destroy();
		}
		models.clear();
	}

	private void sendShaderData() {
		shaders.setUniform("cameraMatrix", camera.getMatrix());
		shaders.setUniform("projectionMatrix", camera.getProjectionMatrix());
		shaders.setUniform("diffuseIntensity", diffuseIntensity);
		shaders.setUniform("specularIntensity", specularIntensity);
		shaders.setUniform("ambientIntensity", ambientIntensity);
		shaders.setUniform("lightPosition", lightPosition);
		shaders.setUniform("lightAttenuation", lightAttenuation);
	}

	private void sendShaderData(OpenGL30Model solid) {
		shaders.setUniform("modelMatrix", solid.getMatrix());
		shaders.setUniform("modelColor", solid.getColor());
	}

	/**
	 * Draws all the models that have been created to the screen.
	 */
	public void render() {
		if (!created) {
			throw new IllegalStateException("Display needs to be created first.");
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL20.glUseProgram(shaders.getID());
		sendShaderData();
		for (OpenGL30Model solid : models) {
			if (!solid.isCreated()) {
				continue;
			}
			sendShaderData(solid);
			solid.render();
		}
		GL20.glUseProgram(0);
		RenderUtil.checkForOpenGLError();
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
		if (!models.contains(model)) {
			models.add(model);
		}
	}

	/**
	 * Removes a model from the list.
	 *
	 * @param model The model to remove
	 */
	public void removeModel(OpenGL30Model model) {
		models.remove(model);
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

	/**
	 * Gets the light position.
	 *
	 * @return The light position
	 */
	public Vector3 getLightPosition() {
		return lightPosition;
	}

	/**
	 * Sets the light position.
	 *
	 * @param position The light position
	 */
	public void setLightPosition(Vector3 position) {
		lightPosition = position;
	}

	/**
	 * Sets the diffuse intensity.
	 *
	 * @param intensity The diffuse intensity
	 */
	public void setDiffuseIntensity(float intensity) {
		diffuseIntensity = intensity;
	}

	/**
	 * Gets the diffuse intensity.
	 *
	 * @return The diffuse intensity
	 */
	public float getDiffuseIntensity() {
		return diffuseIntensity;
	}

	/**
	 * Sets the specular intensity.
	 *
	 * @param intensity specular The intensity
	 */
	public void setSpecularIntensity(float intensity) {
		specularIntensity = intensity;
	}

	/**
	 * Gets specular intensity.
	 *
	 * @return The specular intensity
	 */
	public float getSpecularIntensity() {
		return specularIntensity;
	}

	/**
	 * Sets the ambient intensity.
	 *
	 * @param intensity The ambient intensity
	 */
	public void setAmbientIntensity(float intensity) {
		ambientIntensity = intensity;
	}

	/**
	 * Gets the ambient intensity.
	 *
	 * @return The ambient intensity
	 */
	public float getAmbientIntensity() {
		return ambientIntensity;
	}

	/**
	 * Gets the light distance attenuation factor. In other terms, how much distance affects light
	 * intensity. Larger values affect it more. 0.03 is the default value.
	 *
	 * @return The light distance attenuation factor
	 */
	public float getLightAttenuation() {
		return lightAttenuation;
	}

	/**
	 * Sets the light distance attenuation factor. In other terms, how much distance affects light
	 * intensity. Larger values affect it more. 0.03 is the default value.
	 *
	 * @param attenuation The light distance attenuation factor
	 */
	public void setLightAttenuation(float attenuation) {
		lightAttenuation = attenuation;
	}
}
