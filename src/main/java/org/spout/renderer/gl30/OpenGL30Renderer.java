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
import org.spout.renderer.Camera;
import org.spout.renderer.Model;
import org.spout.renderer.util.RenderUtil;
import org.spout.renderer.gl20.OpenGL20Program;

/**
 * This is a  renderer using the OpenGL 3.2 graphics API. To create a new render window, use {@link
 * #create(String, int, int, float)}. To add and remove models, use {@link
 * #addModel(org.spout.renderer.Model)} and {@link #removeModel(org.spout.renderer.Model)}. The
 * camera position and rotation can be modified. The light diffuse intensity, specular intensity,
 * ambient intensity, position and attenuation can also be controlled. When done, use {@link
 * #destroy()} to destroy the render window.
 */
public class OpenGL30Renderer {
	// States
	private boolean created = false;
	// Window size
	private int windowWidth;
	private int windowHeight;
	// Model data
	private final List<OpenGL30Solid> solids = new ArrayList<>();
	private final List<OpenGL30Wireframe> wireframes = new ArrayList<>();
	// Shaders
	private final OpenGL20Program solidShaders = new OpenGL20Program();
	private final OpenGL20Program wireframeShaders = new OpenGL20Program();
	// Camera
	private Camera camera;
	// Lighting
	private Vector3 lightPosition = new Vector3(0, 0, 0);
	private float diffuseIntensity = 0.8f;
	private float specularIntensity = 0.2f;
	private float ambientIntensity = 0.3f;
	private Color backgroundColor = new Color(0.2f, 0.2f, 0.2f, 0);
	private float lightAttenuation = 0.03f;

	/**
	 * Creates the render window and basic resources. This excludes the models.
	 *
	 * @param title The title of the render window
	 * @param windowWidth The width of the render window
	 * @param windowHeight The height of the render window
	 * @param fieldOfView The field of view in degrees. 75 is suggested
	 */
	public void create(String title, int windowWidth, int windowHeight, float fieldOfView)
			throws LWJGLException {
		if (created) {
			throw new IllegalStateException("Renderer has already been created.");
		}
		createDisplay(title, windowWidth, windowHeight);
		createProjection(fieldOfView);
		createShaders();
		created = true;
	}

	/**
	 * Destroys the render window and the models.
	 */
	public void destroy() {
		if (!created) {
			throw new IllegalStateException("Renderer has not been created yet.");
		}
		destroyModels();
		destroyShaders();
		destroyDisplay();
		created = false;
	}

	private void createDisplay(String title, int width, int height) throws LWJGLException {
		windowWidth = width;
		windowHeight = height;
		final PixelFormat pixelFormat = new PixelFormat();
		final ContextAttribs contextAttributes = new ContextAttribs(3, 2).withProfileCore(true);
		Display.setDisplayMode(new DisplayMode(width, height));
		Display.setTitle(title);
		Display.create(pixelFormat, contextAttributes);
		GL11.glViewport(0, 0, width, height);
		GL11.glClearColor(backgroundColor.getRed() / 255f, backgroundColor.getGreen() / 255f,
				backgroundColor.getBlue() / 255f, backgroundColor.getAlpha() / 255f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL32.GL_DEPTH_CLAMP);
		GL11.glDepthMask(true);
		RenderUtil.checkForOpenGLError();
	}

	private void createProjection(float fieldOfView) {
		camera = Camera.createPerspective(fieldOfView, windowWidth, windowHeight, 0.001f, 1000);
	}

	private void createShaders() {
		solidShaders.create(OpenGL30Renderer.class.getResourceAsStream("/solid.vert"),
				OpenGL30Renderer.class.getResourceAsStream("/solid.frag"));
		wireframeShaders.create(OpenGL30Renderer.class.getResourceAsStream("/wireframe.vert"),
				OpenGL30Renderer.class.getResourceAsStream("/wireframe.frag"));
	}

	private void destroyDisplay() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		RenderUtil.checkForOpenGLError();
		Display.destroy();
	}

	private void destroyShaders() {
		GL20.glUseProgram(0);
		RenderUtil.checkForOpenGLError();
		solidShaders.destroy();
		wireframeShaders.destroy();
	}

	private void destroyModels() {
		for (OpenGL30Solid solid : solids) {
			solid.destroy();
		}
		solids.clear();
		for (OpenGL30Wireframe wireframe : wireframes) {
			wireframe.destroy();
		}
		wireframes.clear();
	}

	private void sendWireframeShadersData() {
		wireframeShaders.setUniform("cameraMatrix", camera.getMatrix());
		wireframeShaders.setUniform("projectionMatrix", camera.getProjectionMatrix());
	}

	private void sendSolidShadersData() {
		solidShaders.setUniform("cameraMatrix", camera.getMatrix());
		solidShaders.setUniform("projectionMatrix", camera.getProjectionMatrix());
		solidShaders.setUniform("diffuseIntensity", diffuseIntensity);
		solidShaders.setUniform("specularIntensity", specularIntensity);
		solidShaders.setUniform("ambientIntensity", ambientIntensity);
		solidShaders.setUniform("lightPosition", lightPosition);
		solidShaders.setUniform("lightAttenuation", lightAttenuation);
	}

	private void sendWireframeShadersData(OpenGL30Wireframe wireframe) {
		wireframeShaders.setUniform("modelMatrix", wireframe.getMatrix());
		wireframeShaders.setUniform("modelColor", wireframe.getColor());
	}

	private void sendSolidShadersData(OpenGL30Solid solid) {
		solidShaders.setUniform("modelMatrix", solid.getMatrix());
		solidShaders.setUniform("modelColor", solid.getColor());
	}

	/**
	 * Draws all the models that have been created to the screen.
	 */
	public void render() {
		if (!created) {
			throw new IllegalStateException("Display needs to be created first.");
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL20.glUseProgram(wireframeShaders.getID());
		sendWireframeShadersData();
		for (OpenGL30Wireframe wireframe : wireframes) {
			if (!wireframe.isCreated()) {
				continue;
			}
			sendWireframeShadersData(wireframe);
			wireframe.render();
		}
		GL20.glUseProgram(solidShaders.getID());
		sendSolidShadersData();
		for (OpenGL30Solid solid : solids) {
			if (!solid.isCreated()) {
				continue;
			}
			sendSolidShadersData(solid);
			solid.render();
		}
		GL20.glUseProgram(0);
		RenderUtil.checkForOpenGLError();
		Display.sync(60);
		Display.update();
	}

	/**
	 * Returns true if the render display has been created.
	 *
	 * @return True if the display and rendering resources have been creates, false if other wise.
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * Adds a model to the list. If a non-created model is added to the list, it will not be rendered
	 * until it is created.
	 *
	 * @param model The model to add
	 */
	public void addModel(Model model) {
		if (model instanceof OpenGL30Solid) {
			if (!solids.contains(model)) {
				solids.add((OpenGL30Solid) model);
			}
		} else if (model instanceof OpenGL30Wireframe) {
			if (!wireframes.contains(model)) {
				wireframes.add((OpenGL30Wireframe) model);
			}
		} else {
			throw new IllegalArgumentException("Unknown model type. Valid types: solid, wireframe");
		}
	}

	/**
	 * Removes a model from the list.
	 *
	 * @param model The model to remove
	 */
	public void removeModel(Model model) {
		if (model instanceof OpenGL30Solid) {
			solids.remove(model);
		} else if (model instanceof OpenGL30Wireframe) {
			wireframes.remove(model);
		} else {
			throw new IllegalArgumentException("Unknown model type. Valid types: solid, wireframe");
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

	/**
	 * Gets the renderer camera. Use this to move the view around.
	 *
	 * @return The camera
	 */
	public Camera getCamera() {
		return camera;
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
