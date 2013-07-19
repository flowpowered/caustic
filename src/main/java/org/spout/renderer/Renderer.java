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

/**
 * Represents an OpenGL renderer. The renderer should never be created before setting the camera
 * using {@link #setCamera(Camera)}.
 */
public class Renderer extends Creatable {
	protected String windowTitle = "Caustic renderer";
	// Window size
	protected int windowWidth = 640;
	protected int windowHeight = 480;
	// Camera
	protected Camera camera;

	protected Renderer() {
	}

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
}
