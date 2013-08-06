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
package org.spout.renderer.gl;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;
import org.spout.renderer.data.RenderList;
import org.spout.renderer.data.UniformHolder;

/**
 * Represents an OpenGL renderer. Creating the renderer also created the OpenGL context, and so must be done before any other OpenGL object. To add models to render, add them to a render list, then
 * use {@link #addRenderList(org.spout.renderer.data.RenderList)} to add the list. All models in a render list are rendered together, using the list's properties.
 */
public abstract class Renderer extends Creatable implements GLVersioned {
	// Window title
	protected String windowTitle = "Caustic renderer";
	// Window size
	protected int windowWidth = 640;
	protected int windowHeight = 480;
	// MSAA value
	protected int MSAA = 0;
	// Properties
	protected final Set<Capability> capabilities = EnumSet.noneOf(Capability.class);
	// Renderer uniforms
	protected final UniformHolder uniforms = new UniformHolder();
	// Models
	protected final Set<RenderList> renderLists = new TreeSet<>();
	protected final Map<String, RenderList> renderListsByName = new HashMap<>();

	@Override
	public void destroy() {
		capabilities.clear();
		uniforms.clear();
		renderLists.clear();
		renderListsByName.clear();
		super.destroy();
	}

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

	/**
	 * Sets the renderer buffer clear color. This can be interpreted as the background color.
	 *
	 * @param color The clear color
	 */
	public abstract void setClearColor(Color color);

	/**
	 * Disables the capability.
	 *
	 * @param capability The capability to disable
	 */
	protected abstract void disable(Capability capability);

	/**
	 * Enables the capability.
	 *
	 * @param capability The capability to enable
	 */
	protected abstract void enable(Capability capability);

	/**
	 * Sets the renderer capabilities to only the provided ones.
	 *
	 * @param capabilities The capabilities to set
	 */
	protected void setCapabilities(Set<Capability> capabilities) {
		for (Capability oldCapability : this.capabilities) {
			if (!capabilities.contains(oldCapability)) {
				disable(oldCapability);
				this.capabilities.remove(oldCapability);
			}
		}
		for (Capability newCapability : capabilities) {
			if (!this.capabilities.contains(newCapability)) {
				enable(newCapability);
				this.capabilities.add(newCapability);
			}
		}
	}

	/**
	 * Returns the render list for the name.
	 *
	 * @param name the name for lookup
	 * @return The render list, or null if non can be found
	 */
	public RenderList getRenderList(String name) {
		return renderListsByName.get(name);
	}

	/**
	 * Returns true if the render has a render list for the name.
	 *
	 * @param name The name to lookup
	 * @return Whether or not a list of that name is present
	 */
	public boolean hasRenderList(String name) {
		return renderListsByName.containsKey(name);
	}

	/**
	 * Adds a render list to the renderer.
	 *
	 * @param list The list to add
	 */
	public void addRenderList(RenderList list) {
		renderLists.add(list);
		renderListsByName.put(list.getName(), list);
	}

	/**
	 * Removes the render list with the provided name from the renderer.
	 *
	 * @param name The name of the list to remove
	 */
	public void removeRenderList(String name) {
		renderLists.remove(renderListsByName.remove(name));
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
	 * Sets the MSAA value. Must be greater or equal to zero. Zero means no MSAA.
	 *
	 * @param value The MSAA value, greater or equal to zero
	 */
	public void setMSAA(int value) {
		if (MSAA < 0) {
			throw new IllegalArgumentException("MSAA value must be greater or equal to zero");
		}
		this.MSAA = value;
	}

	/**
	 * Returns the Uniforms for this renderer
	 *
	 * @return The renderer uniforms
	 */
	public UniformHolder getUniforms() {
		return uniforms;
	}

	/**
	 * An enum of the renderer capabilities.
	 */
	public static enum Capability {
		BLEND(GL11.GL_BLEND),
		CULL_FACE(GL11.GL_CULL_FACE),
		DEPTH_CLAMP(GL32.GL_DEPTH_CLAMP),
		DEPTH_TEST(GL11.GL_DEPTH_TEST),
		LINE_SMOOTH(GL11.GL_LINE_SMOOTH),
		POLYGON_SMOOTH(GL11.GL_POLYGON_SMOOTH);
		private final int glConstant;

		private Capability(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Returns the OpenGL constant associated to the capability.
		 *
		 * @return The OpenGL constant
		 */
		public int getGLConstant() {
			return glConstant;
		}
	}
}
