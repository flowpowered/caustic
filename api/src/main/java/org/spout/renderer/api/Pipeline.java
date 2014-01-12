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
package org.spout.renderer.api;

import java.util.List;

import org.spout.renderer.api.Action.BindFrameBufferAction;
import org.spout.renderer.api.Action.ClearBufferAction;
import org.spout.renderer.api.Action.DisableCapabilitiesAction;
import org.spout.renderer.api.Action.EnableCapabilitiesAction;
import org.spout.renderer.api.Action.RenderModelsAction;
import org.spout.renderer.api.Action.SetBlendingFunctions;
import org.spout.renderer.api.Action.SetCameraAction;
import org.spout.renderer.api.Action.SetClearColorAction;
import org.spout.renderer.api.Action.SetDepthMaskAction;
import org.spout.renderer.api.Action.SetViewPortAction;
import org.spout.renderer.api.Action.UnbindFrameBufferAction;
import org.spout.renderer.api.Action.UpdateDisplayAction;
import org.spout.renderer.api.data.Color;
import org.spout.renderer.api.gl.Context;
import org.spout.renderer.api.gl.Context.BlendFunction;
import org.spout.renderer.api.gl.Context.Capability;
import org.spout.renderer.api.gl.FrameBuffer;
import org.spout.renderer.api.model.Model;
import org.spout.renderer.api.util.Rectangle;

/**
 * Represents a rendering pipeline. A pipeline is built with a {@link PipelineBuilder}, as a series of actions. Running the pipeline results in the actions being executed in the order they were
 * added.
 */
public class Pipeline {
    private final Action first;

    /**
     * Constructs a new pipeline from the first action to execute the chain from. The next action is linked in the first action and so on.
     *
     * @param first The first action of the chain
     */
    protected Pipeline(Action first) {
        if (first == null) {
            throw new IllegalArgumentException("First action cannot be null");
        }
        this.first = first;
    }

    /**
     * Runs the pipeline using the provided context.
     *
     * @param context The context to use.
     */
    public void run(Context context) {
        first.executeChain(context);
    }

    /**
     * Used to built a pipeline through chained calls.
     */
    public static class PipelineBuilder {
        private Action first = null;
        private Action current;

        /**
         * Builds the next action in the chain. The action sets the clear color to use when clearing color buffers.
         *
         * @param color The color to use for clearing buffers
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder useClearColor(Color color) {
            return doAction(new SetClearColorAction(color));
        }

        /**
         * Builds the next action in the chain. The action clears the current active buffer (the bound frame buffer, or if none bound, the front (screen) buffer).
         *
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder clearBuffer() {
            return doAction(new ClearBufferAction());
        }

        /**
         * Builds the next action in the chain. The action sets the view port to use when rendering to a buffer.
         *
         * @param viewPort The rendering view port
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder useViewPort(Rectangle viewPort) {
            return doAction(new SetViewPortAction(viewPort));
        }

        /**
         * Builds the next action in the chain. The action enables the desired capabilities in the context.
         *
         * @param capabilities The capabilities to enable
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder enableCapabilities(Capability... capabilities) {
            return doAction(new EnableCapabilitiesAction(capabilities));
        }

        /**
         * Builds the next action in the chain. The action disables the desired capabilities in the context.
         *
         * @param capabilities The capabilities to disable
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder disableCapabilities(Capability... capabilities) {
            return doAction(new DisableCapabilitiesAction(capabilities));
        }

        /**
         * Builds the next action in the chain. The action binds the desired frame buffer to the context.
         *
         * @param frameBuffer The frame buffer to bind
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder bindFrameBuffer(FrameBuffer frameBuffer) {
            return doAction(new BindFrameBufferAction(frameBuffer));
        }

        /**
         * Builds the next action in the chain. The action unbinds the desired frame buffer from the context.
         *
         * @param frameBuffer The frame buffer to unbind
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder unbindFrameBuffer(FrameBuffer frameBuffer) {
            return doAction(new UnbindFrameBufferAction(frameBuffer));
        }

        /**
         * Builds the next action in the chain. The action sets the camera to use when rendering the models.
         *
         * @param camera The rendering camera
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder useCamera(Camera camera) {
            return doAction(new SetCameraAction(camera));
        }

        /**
         * Builds the next action in the chain. The action enables the depth mask.
         *
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder enableDepthMask() {
            return doAction(new SetDepthMaskAction(true));
        }

        /**
         * Builds the next action in the chain. The action disables the depth mask.
         *
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder disableDepthMask() {
            return doAction(new SetDepthMaskAction(false));
        }

        /**
         * Builds the next action in the chain. The sets the blending functions.
         *
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder setBlendingFunctions(BlendFunction source, BlendFunction destination) {
            return doAction(new SetBlendingFunctions(source, destination));
        }

        /**
         * Builds the next action in the chain. The actions renders the model list. The models will be reordered to be grouped by material. This reordering is done via a stable sort ({@link
         * java.util.Collections#sort(java.util.List)}) so that models with the same materials are not reordered. This grouping improves performance by reducing the amount of rendering calls.
         *
         * @param models The models to render
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder renderModels(List<Model> models) {
            return doAction(new RenderModelsAction(models));
        }

        /**
         * Builds the next action in the chain. The action updates the context's display.
         *
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder updateDisplay() {
            return doAction(new UpdateDisplayAction());
        }

        /**
         * Builds the next action in the chain.
         *
         * @param action The action
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder doAction(Action action) {
            if (first == null) {
                first = action;
            } else {
                current.setNext(action);
            }
            current = action;
            return this;
        }

        /**
         * Builds the pipeline, returning it.
         *
         * @return The built pipeline
         */
        public Pipeline build() {
            return new Pipeline(first);
        }
    }
}
