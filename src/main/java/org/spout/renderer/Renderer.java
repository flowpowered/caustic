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

import java.awt.Color;

import org.spout.renderer.data.Uniform.Matrix4Uniform;
import org.spout.renderer.data.UniformHolder;

/**
 * Represents an OpenGL renderer. The renderer should never be created before setting the camera
 * using {@link #setCamera(Camera)}.
 */
public abstract class Renderer extends Creatable {
	// Window title
	protected String windowTitle = "Caustic renderer";
	// Window size
	protected int windowWidth = 640;
	protected int windowHeight = 480;
	// Properties
	protected Color backgroundColor = new Color(0.2f, 0.2f, 0.2f, 0);
	// Camera
	protected Camera camera;
	//Culling enabled
	protected boolean cullingEnabled;
	// Renderer uniforms
	protected final UniformHolder uniforms = new UniformHolder();

	@Override
	public void create() {
		// Add the constant renderer uniforms
		uniforms.add(new Matrix4Uniform("projectionMatrix", camera.getProjectionMatrix()));
		uniforms.add(new Matrix4Uniform("cameraMatrix", camera.getMatrix()));
		// Update the state
		super.create();
	}

	@Override
	public void destroy() {
		camera = null;
		// Update the state
		super.destroy();
	}

	/**
	 * Draws all the models that have been created to the screen.
	 */
	public abstract void render();

	/**
	 * Adds a model to the list. If a non-created model is added to the list, it will not be rendered
	 * until it is created.
	 *
	 * @param model The model to add
	 */
	public abstract void addModel(Model model);

	/**
	 * Removes a model from the list.
	 *
	 * @param model The model to remove
	 */
	public abstract void removeModel(Model model);

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
	 * Returns the window width.
	 *
	 * @return The window width
	 */
	public int getWindowWidth() {
		return windowWidth;
	}

	/**
	 * Sets the window width.
	 *
	 * @param width The window width
	 */
	public void setWindowWidth(int width) {
		windowWidth = width;
	}

	/**
	 * Returns the window height.
	 *
	 * @return The window height
	 */
	public int getWindowHeight() {
		return windowHeight;
	}

	/**
	 * Sets the window height.
	 *
	 * @param height The window height
	 */
	public void setWindowHeight(int height) {
		windowHeight = height;
	}

	/**
	 * Sets the window size parameters.
	 *
	 * @param windowWidth The window width
	 * @param windowHeight the window height
	 */
	public void setWindowSize(int windowWidth, int windowHeight) {
		setWindowWidth(windowWidth);
		setWindowHeight(windowHeight);
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
	 * Sets the camera.
	 *
	 * @param camera The camera
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 * If the back face of the shape should be culled
	 *
	 * @return If culling is enabled
	 */
	public boolean isCullingEnabled() {
		return cullingEnabled;
	}

	/**
	 * Sets if culling is enabled or not
	 *
	 * @param cullingEnabled If it is enabled
	 */
	public void setCullingEnabled(boolean cullingEnabled) {
		this.cullingEnabled = cullingEnabled;
	}

	/**
	 * Returns the Uniforms for this renderer
	 *
	 * @return The renderer uniforms
	 */
	public UniformHolder getUniforms() {
		return uniforms;
	}
}
