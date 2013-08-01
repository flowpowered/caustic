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

import org.spout.renderer.data.RenderList;
import org.spout.renderer.data.UniformHolder;

/**
 * Represents an OpenGL renderer. Creating the renderer also created the OpenGL context, and so must
 * be done before any other OpenGL object. To add models to render, add them to a render list, then
 * use {@link #addRenderList(org.spout.renderer.data.RenderList)} to add the list.
 */
public abstract class Renderer extends Creatable implements GLVersioned {
	// Window title
	protected String windowTitle = "Caustic renderer";
	// Window size
	protected int windowWidth = 640;
	protected int windowHeight = 480;
	// Properties
	protected Color backgroundColor = new Color(0.2f, 0.2f, 0.2f, 0);
	//Culling enabled
	protected boolean cullingEnabled;
	// Renderer uniforms
	protected final UniformHolder uniforms = new UniformHolder();

	/**
	 * Uploads the renderer uniforms to the desired program.
	 *
	 * @param program The program to upload to
	 */
	public abstract void uploadUniforms(Program program);

	/**
	 * Draws all the models that have been created to the screen.
	 */
	public abstract void render();

	public abstract RenderList getRenderList(String name);

	public abstract boolean hasRenderList(String name);

	public abstract void addRenderList(RenderList list);

	public abstract void removeRenderList(String name);

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
	 * Sets the background color.
	 *
	 * @param color The background color
	 */
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
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
