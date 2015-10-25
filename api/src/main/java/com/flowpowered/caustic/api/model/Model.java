/*
 * This file is part of Caustic API, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 Flow Powered <https://flowpowered.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.flowpowered.caustic.api.model;

import java.util.HashSet;
import java.util.Set;

import com.flowpowered.caustic.api.Material;
import com.flowpowered.caustic.api.data.UniformHolder;
import com.flowpowered.caustic.api.gl.VertexArray;
import com.flowpowered.math.imaginary.Quaternionf;
import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;

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
    // Optional parent model
    private Model parent = null;
    private final Set<Model> children = new HashSet<>();
    private Matrix4f lastParentMatrix = null;
    private Matrix4f childMatrix = null;

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
            final Matrix4f matrix = Matrix4f.createScaling(scale.toVector4(1)).rotate(rotation).translate(position);
            if (parent == null) {
                this.matrix = matrix;
            } else {
                childMatrix = matrix;
            }
            updateMatrix = false;
        }
        if (parent != null) {
            final Matrix4f parentMatrix = parent.getMatrix();
            if (parentMatrix != lastParentMatrix) {
                matrix = parentMatrix.mul(childMatrix);
                lastParentMatrix = parentMatrix;
            }
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
     * @return The instanced model
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

    /**
     * Returns the parent model. This model's position, rotation and scale are relative to that model if not null.
     *
     * @return The parent model, can be null for no parent
     */
    public Model getParent() {
        return parent;
    }

    /**
     * Returns the children models.
     *
     * @return The children models
     */
    public Set<Model> getChildren() {
        return children;
    }

    /**
     * Sets the parent model. This model's position, rotation and scale will be relative to that model if not null.
     *
     * @param parent The parent model, or null for no parent
     */
    public void setParent(Model parent) {
        if (parent == this) {
            throw new IllegalArgumentException("The model can't be its own parent");
        }
        if (parent == null) {
            this.parent.children.remove(this);
        } else {
            parent.children.add(this);
        }
        this.parent = parent;
    }

    @Override
    public int compareTo(Model that) {
        return material.compareTo(that.material);
    }
}
