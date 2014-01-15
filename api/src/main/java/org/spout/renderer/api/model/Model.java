/*
 * This file is part of Caustic API.
 *
 * Copyright (c) 2013 Spout LLC <http://www.spout.org/>
 * Caustic API is licensed under the Spout License Version 1.
 *
 * Caustic API is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Caustic API is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.renderer.api.model;

import com.flowpowered.math.imaginary.Quaternionf;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;

import org.spout.renderer.api.Material;
import org.spout.renderer.api.data.UniformHolder;
import org.spout.renderer.api.gl.VertexArray;

/**
 * Represents a model. Each model has it's own position and rotation and set of uniforms. The vertex array provides the vertex data (mesh), while the material provides uniforms and textures for the
 * shader.
 */
public class Model implements Comparable<Model> {
    // Vertex array
    private VertexArray vertexArray;
    // Material
    private Material material;
    // Position and rotation properties
    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);
    private Quaternionf rotation = new Quaternionf();
    private Matrix4f matrix = new Matrix4f();
    private boolean updateMatrix = true;
    // Model uniforms
    private final UniformHolder uniforms = new UniformHolder();

    /**
     * An empty constructor for child classes only.
     */
    protected Model() {
    }

    /**
     * Constructs a new model from the provided one. The vertex array and material are reused. No information is copied.
     *
     * @param model The model to derive this one from
     */
    protected Model(Model model) {
        this.vertexArray = model.getVertexArray();
        this.material = model.getMaterial();
        uniforms.addAll(model.uniforms);
    }

    /**
     * Constructs a new model from the vertex array and material.
     *
     * @param vertexArray The vertex array
     * @param material The material
     */
    public Model(VertexArray vertexArray, Material material) {
        if (vertexArray == null) {
            throw new IllegalArgumentException("Vertex array cannot be null");
        }
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        vertexArray.checkCreated();
        this.vertexArray = vertexArray;
        this.material = material;
    }

    /**
     * Uploads the model's uniforms to its material's program.
     */
    public void uploadUniforms() {
        material.getProgram().upload(uniforms);
    }

    /**
     * Draws the model to the screen.
     */
    public void render() {
        if (vertexArray == null) {
            throw new IllegalStateException("Vertex array has not been set");
        }
        vertexArray.draw();
    }

    /**
     * Returns the model's vertex array.
     *
     * @return The vertex array
     */
    public VertexArray getVertexArray() {
        return vertexArray;
    }

    /**
     * Sets the vertex array for this model.
     *
     * @param vertexArray The vertex array to use
     */
    public void setVertexArray(VertexArray vertexArray) {
        if (vertexArray == null) {
            throw new IllegalArgumentException("Vertex array cannot be null");
        }
        vertexArray.checkCreated();
        this.vertexArray = vertexArray;
    }

    /**
     * Returns the model's material.
     *
     * @return The material
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the model's material.
     *
     * @param material The material
     */
    public void setMaterial(Material material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        this.material = material;
    }

    /**
     * Returns the transformation matrix that represent the model's current scale, rotation and position.
     *
     * @return The transformation matrix
     */
    public Matrix4f getMatrix() {
        if (updateMatrix) {
            matrix = Matrix4f.createScaling(scale.toVector4(1)).rotate(rotation).translate(position);
            updateMatrix = false;
        }
        return matrix;
    }

    /**
     * Gets the model position.
     *
     * @return The model position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Sets the model position.
     *
     * @param position The model position
     */
    public void setPosition(Vector3f position) {
        this.position = position;
        updateMatrix = true;
    }

    /**
     * Gets the model rotation.
     *
     * @return The model rotation
     */
    public Quaternionf getRotation() {
        return rotation;
    }

    /**
     * Sets the model rotation.
     *
     * @param rotation The model rotation
     */
    public void setRotation(Quaternionf rotation) {
        this.rotation = rotation;
        updateMatrix = true;
    }

    /**
     * Gets the model scale.
     *
     * @return The model scale
     */
    public Vector3f getScale() {
        return scale;
    }

    /**
     * Sets the model scale.
     *
     * @param scale The model scale
     */
    public void setScale(Vector3f scale) {
        this.scale = scale;
        updateMatrix = true;
    }

    /**
     * Returns an instance of this model. The model shares the same vertex array and material as the original one, but different position information and uniform holder.
     *
     * @return The instanced  model
     */
    public Model getInstance() {
        return new Model(this);
    }

    /**
     * Returns the model's uniforms
     *
     * @return The uniforms
     */
    public UniformHolder getUniforms() {
        return uniforms;
    }

    @Override
    public int compareTo(Model that) {
        return material.compareTo(that.material);
    }
}
