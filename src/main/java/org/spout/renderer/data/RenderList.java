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
package org.spout.renderer.data;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.spout.renderer.Camera;
import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.gl.Model;
import org.spout.renderer.gl.Program;
import org.spout.renderer.data.Uniform.Matrix4Uniform;

/**
 * Represents a named collection of models for rendering. The collection shares the same camera and
 * a set of uniforms. Optionally, they may share a frame buffer for the render output. A render list
 * can be deactivated and reactivated as necessary. Inactive lists will not be rendered. List has an
 * index which is used to sort them by priority. The index doesn't need to be unique or positive.
 * The smaller the index, the higher the priority.
 */
public class RenderList implements Collection<Model>, Comparable<RenderList> {
	private final UniformHolder uniforms = new UniformHolder();
	private final String name;
	private final Set<Model> models = new HashSet<>();
	private Camera camera;
	private final int index;
	private boolean active = true;
	private FrameBuffer frameBuffer;

	/**
	 * Creates a new render list from the name, the camera and the priority index.
	 *
	 * @param name The name
	 * @param camera The camera
	 * @param index The index
	 */
	public RenderList(String name, Camera camera, int index) {
		this.name = name;
		this.camera = camera;
		this.index = index;
		uniforms.add(new Matrix4Uniform("projectionMatrix", camera.getProjectionMatrix()));
		uniforms.add(new Matrix4Uniform("cameraMatrix", camera.getMatrix()));
	}

	/**
	 * Returns the name of the render list.
	 *
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the camera.
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
		if (camera == null) {
			throw new IllegalArgumentException("Camera cannot be null");
		}
		this.camera = camera;
	}

	/**
	 * Returns the index of the render list.
	 *
	 * @return The index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Returns true if the render list is active
	 *
	 * @return Whether or not the list is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the activity status of the list
	 *
	 * @param active The activity status: true for active, false for inactive
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Toggles the render list's activity status.
	 */
	public void toggleActive() {
		active ^= true;
	}

	/**
	 * Returns true if the render list has a frame buffer.
	 *
	 * @return Whether or not this list has a frame buffer
	 */
	public boolean hasFrameBuffer() {
		return frameBuffer != null;
	}

	/**
	 * Returns the render list's frame buffer, or null is none has been assigned yet.
	 *
	 * @return The frame buffer, may be null
	 * @see #hasFrameBuffer()
	 */
	public FrameBuffer getFrameBuffer() {
		return frameBuffer;
	}

	/**
	 * Sets the frame buffer to use. All rendering with this list will have it's output redirected to
	 * it.
	 *
	 * @param frameBuffer The frame buffer to render to
	 */
	public void setFrameBuffer(FrameBuffer frameBuffer) {
		this.frameBuffer = frameBuffer;
	}

	/**
	 * Returns the uniform holder for this material.
	 *
	 * @return The uniforms
	 */
	public UniformHolder getUniforms() {
		return uniforms;
	}

	/**
	 * Uploads the render list uniforms to the desired program.
	 *
	 * @param program The program to upload to
	 */
	public void uploadUniforms(Program program) {
		uniforms.getMatrix4("projectionMatrix").set(camera.getProjectionMatrix());
		uniforms.getMatrix4("cameraMatrix").set(camera.getMatrix());
		program.upload(uniforms);
	}

	@Override
	public int size() {
		return models.size();
	}

	@Override
	public boolean isEmpty() {
		return models.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return models.contains(o);
	}

	@Override
	public Iterator<Model> iterator() {
		return models.iterator();
	}

	@Override
	public Object[] toArray() {
		return models.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return models.toArray(a);
	}

	@Override
	public boolean add(Model model) {
		return models.add(model);
	}

	@Override
	public boolean remove(Object o) {
		return models.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return models.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Model> c) {
		return models.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return models.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return models.retainAll(c);
	}

	@Override
	public void clear() {
		models.clear();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof RenderList)) {
			return false;
		}
		final RenderList models1 = (RenderList) o;
		if (camera != null ? !camera.equals(models1.camera) : models1.camera != null) {
			return false;
		}
		if (!models.equals(models1.models)) {
			return false;
		}
		if (!name.equals(models1.name)) {
			return false;
		}
		return uniforms.equals(models1.uniforms);
	}

	@Override
	public int hashCode() {
		int result = uniforms.hashCode();
		result = 31 * result + name.hashCode();
		result = 31 * result + models.hashCode();
		result = 31 * result + camera.hashCode();
		return result;
	}

	@Override
	public int compareTo(RenderList o) {
		return o.getIndex() - index;
	}
}
