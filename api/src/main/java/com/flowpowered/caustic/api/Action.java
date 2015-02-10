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
package com.flowpowered.caustic.api;

import java.util.Arrays;
import java.util.Collection;

import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.gl.Context;
import com.flowpowered.caustic.api.gl.Context.BlendFunction;
import com.flowpowered.caustic.api.gl.Context.Capability;
import com.flowpowered.caustic.api.gl.FrameBuffer;
import com.flowpowered.caustic.api.gl.Program;
import com.flowpowered.caustic.api.model.Model;
import com.flowpowered.caustic.api.util.Rectangle;

/**
 * Represents a rendering action that can be executed with {@link #execute(com.flowpowered.caustic.api.gl.Context)}.
 */
public abstract class Action {
    /**
     * Executes this actions with the desired rendering context.
     *
     * @param context The rendering context
     */
    public abstract void execute(Context context);

    /**
     * An action that sets the color for clearing color buffers in the context.
     */
    public static class SetClearColorAction extends Action {
        private Vector4f color;

        /**
         * Constructs a clear color setting action with the desired clearing color.
         *
         * @param color The clearing color
         */
        public SetClearColorAction(Vector4f color) {
            this.color = color;
        }

        /**
         * Returns the clearing color.
         *
         * @return The color
         */
        public Vector4f getClearColor() {
            return color;
        }

        /**
         * Sets the clearing color.
         *
         * @param color The clearing color
         */
        public void setClearColor(Vector4f color) {
            this.color = color;
        }

        @Override
        public void execute(Context context) {
            context.setClearColor(color);
        }
    }

    /**
     * An action that clear the buffer currently bound to the context.
     */
    public static class ClearBufferAction extends Action {
        @Override
        public void execute(Context context) {
            context.clearCurrentBuffer();
        }
    }

    /**
     * An action that sets the view port in the context.
     */
    public static class SetViewPortAction extends Action {
        private Rectangle viewPort;

        /**
         * Constructs a view port setting action with the desired view port.
         *
         * @param viewPort The view port
         */
        public SetViewPortAction(Rectangle viewPort) {
            this.viewPort = viewPort;
        }

        /**
         * Returns the view port.
         *
         * @return The view port
         */
        public Rectangle getViewPort() {
            return viewPort;
        }

        /**
         * Sets the view port.
         *
         * @param viewPort The view port
         */
        public void setViewPort(Rectangle viewPort) {
            this.viewPort = viewPort;
        }

        @Override
        public void execute(Context context) {
            context.setViewPort(viewPort);
        }
    }

    /**
     * An action that enables capabilities in the context.
     */
    public static class EnableCapabilitiesAction extends Action {
        private Capability[] capabilities;

        /**
         * Constructs a capability enabling action with the desired capabilities to enable.
         *
         * @param capabilities The capabilities
         */
        public EnableCapabilitiesAction(Capability... capabilities) {
            this.capabilities = capabilities;
        }

        /**
         * Returns the enabled capabilities.
         *
         * @return The capabilities
         */
        public Capability[] getCapabilities() {
            return capabilities;
        }

        /**
         * Sets the capabilities to enable.
         *
         * @param capabilities The capabilities
         */
        public void setCapabilities(Capability[] capabilities) {
            this.capabilities = capabilities;
        }

        @Override
        public void execute(Context context) {
            for (Capability capability : capabilities) {
                context.enableCapability(capability);
            }
        }
    }

    /**
     * An action that disables capabilities in the context.
     */
    public static class DisableCapabilitiesAction extends Action {
        private Capability[] capabilities;

        /**
         * Constructs a capability disabling action with the desired capabilities to disable.
         *
         * @param capabilities The capabilities
         */
        public DisableCapabilitiesAction(Capability... capabilities) {
            this.capabilities = capabilities;
        }

        /**
         * Returns the disabled capabilities.
         *
         * @return The capabilities
         */
        public Capability[] getCapabilities() {
            return capabilities;
        }

        /**
         * Sets the capabilities to enable.
         *
         * @param capabilities The capabilities
         */
        public void setCapabilities(Capability[] capabilities) {
            this.capabilities = capabilities;
        }

        @Override
        public void execute(Context context) {
            for (Capability capability : capabilities) {
                context.disableCapability(capability);
            }
        }
    }

    /**
     * An action that binds a frame buffer to the context.
     */
    public static class BindFrameBufferAction extends Action {
        private FrameBuffer frameBuffer;

        /**
         * Constructs a frame buffer binding action with the desired frame buffer to bind.
         *
         * @param frameBuffer The frame buffer
         */
        public BindFrameBufferAction(FrameBuffer frameBuffer) {
            this.frameBuffer = frameBuffer;
        }

        /**
         * Returns the frame buffer.
         *
         * @return The frame buffer
         */
        public FrameBuffer getFrameBuffer() {
            return frameBuffer;
        }

        /**
         * Sets the frame buffer to bind
         *
         * @param frameBuffer The frame buffer
         */
        public void setFrameBuffer(FrameBuffer frameBuffer) {
            this.frameBuffer = frameBuffer;
        }

        @Override
        public void execute(Context context) {
            frameBuffer.bind();
        }
    }

    /**
     * An action that unbinds a frame buffer from the context.
     */
    public static class UnbindFrameBufferAction extends Action {
        private FrameBuffer frameBuffer;

        /**
         * Constructs a frame buffer unbinding action with the desired frame buffer to unbind.
         *
         * @param frameBuffer The frame buffer
         */
        public UnbindFrameBufferAction(FrameBuffer frameBuffer) {
            this.frameBuffer = frameBuffer;
        }

        /**
         * Returns the frame buffer.
         *
         * @return The frame buffer
         */
        public FrameBuffer getFrameBuffer() {
            return frameBuffer;
        }

        /**
         * Sets the frame buffer to unbind
         *
         * @param frameBuffer The frame buffer
         */
        public void setFrameBuffer(FrameBuffer frameBuffer) {
            this.frameBuffer = frameBuffer;
        }

        @Override
        public void execute(Context context) {
            frameBuffer.unbind();
        }
    }

    /**
     * An action that sets the camera in the context.
     */
    public static class SetCameraAction extends Action {
        private Camera camera;

        /**
         * Constructs a camera setting action with the desired camera.
         *
         * @param camera The camera
         */
        public SetCameraAction(Camera camera) {
            this.camera = camera;
        }

        /**
         * Returns the camera.
         *
         * @return The camera
         */
        public Camera getCamera() {
            return camera;
        }

        /**
         * Sets the camera to use.
         *
         * @param camera The camera
         */
        public void setCamera(Camera camera) {
            this.camera = camera;
        }

        @Override
        public void execute(Context context) {
            context.setCamera(camera);
        }
    }

    /**
     * An action that enables the depth mask.
     */
    public static class SetDepthMaskAction extends Action {
        private boolean enabled;

        /**
         * Constructs a new depth mask setting action with the desired depth mask status.
         *
         * @param enabled The depth mask status
         */
        public SetDepthMaskAction(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * Returns the depth mask value for the action.
         *
         * @return The depth mask value
         */
        public boolean getEnabled() {
            return enabled;
        }

        /**
         * Sets the depth mask value for the action.
         *
         * @param enabled The depth mask value
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public void execute(Context context) {
            context.setDepthMask(enabled);
        }
    }

    /**
     * An action that set the blending functions.
     */
    public static class SetBlendingFunctions extends Action {
        private BlendFunction source, destination;

        /**
         * Constructs a new blending function setting action with the desired source and destination blending functions.
         *
         * @param source The source blending function
         * @param destination The destination blending function
         */
        public SetBlendingFunctions(BlendFunction source, BlendFunction destination) {
            this.source = source;
            this.destination = destination;
        }

        /**
         * Returns the source blending function.
         *
         * @return The source blending function
         */
        public BlendFunction getSource() {
            return source;
        }

        /**
         * Returns the destination blending function.
         *
         * @return The destination blending function
         */
        public BlendFunction getDestination() {
            return destination;
        }

        /**
         * Sets the blending functions.
         *
         * @param source The source blending function
         * @param destination The destination blending function
         */
        public void setSource(BlendFunction source, BlendFunction destination) {
            this.source = source;
            this.destination = destination;
        }

        @Override
        public void execute(Context context) {
            context.setBlendingFunctions(source, destination);
        }
    }

    /**
     * An action that renders the models to the bound buffer. The models will be reordered to be grouped by material. This reordering is done via a stable sort ({@link
     * java.util.Arrays#sort(Object[])}) so that models with the same materials are not reordered. This grouping improves performance by reducing the amount of rendering calls.
     */
    public static class RenderModelsAction extends Action {
        private Collection<? extends Model> models;

        /**
         * Constructs a model rendering action with the models to render
         *
         * @param models The models
         */
        public RenderModelsAction(Collection<? extends Model> models) {
            this.models = models;
        }

        /**
         * Returns the models to render.
         *
         * @return The models
         */
        public Collection<? extends Model> getModels() {
            return models;
        }

        /**
         * Sets the models to render.
         *
         * @param models The models
         */
        public void setModels(Collection<? extends Model> models) {
            this.models = models;
        }

        @Override
        public void execute(Context context) {
            // Get the model array
            final Model[] models = this.models.toArray(new Model[this.models.size()]);
            // Batch the models with the same materials together
            Arrays.sort(models);
            // Current material
            Material current = null;
            for (Model model : models) {
                final Material material = model.getMaterial();
                if (material == null) {
                    throw new IllegalStateException("Null material");
                }
                // If we switched material
                if (current != material) {
                    // Unbind the old material if any
                    if (current != null) {
                        current.unbind();
                    }
                    // Update the current material
                    current = material;
                    // Bind it
                    current.bind();
                    // Upload the camera matrices
                    uploadCameraMatrices(context.getCamera(), current.getProgram());
                    // Upload the context uniforms
                    context.uploadUniforms(current.getProgram());
                    // Upload the material uniforms
                    material.uploadUniforms();
                }
                // Upload the model and normal matrices
                uploadModelMatrices(model, context.getCamera(), current.getProgram());
                // Upload the model uniforms
                model.uploadUniforms();
                // Render the model
                model.render();
            }
        }

        private static void uploadCameraMatrices(Camera camera, Program program) {
            program.setUniform("projectionMatrix", camera.getProjectionMatrix());
            program.setUniform("viewMatrix", camera.getViewMatrix());
        }

        private static void uploadModelMatrices(Model model, Camera camera, Program program) {
            program.setUniform("modelMatrix", model.getMatrix());
            program.setUniform("normalMatrix", camera.getViewMatrix().mul(model.getMatrix()).invert().transpose());
        }
    }

    /**
     * An action that updates the context's display.
     */
    public static class UpdateDisplayAction extends Action {
        @Override
        public void execute(Context context) {
            context.updateDisplay();
        }
    }
}
