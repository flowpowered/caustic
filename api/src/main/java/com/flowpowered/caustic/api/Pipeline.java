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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.Action.BindFrameBufferAction;
import com.flowpowered.caustic.api.Action.ClearBufferAction;
import com.flowpowered.caustic.api.Action.DisableCapabilitiesAction;
import com.flowpowered.caustic.api.Action.EnableCapabilitiesAction;
import com.flowpowered.caustic.api.Action.RenderModelsAction;
import com.flowpowered.caustic.api.Action.SetBlendingFunctions;
import com.flowpowered.caustic.api.Action.SetCameraAction;
import com.flowpowered.caustic.api.Action.SetClearColorAction;
import com.flowpowered.caustic.api.Action.SetDepthMaskAction;
import com.flowpowered.caustic.api.Action.SetViewPortAction;
import com.flowpowered.caustic.api.Action.UnbindFrameBufferAction;
import com.flowpowered.caustic.api.Action.UpdateDisplayAction;
import com.flowpowered.caustic.api.gl.Context;
import com.flowpowered.caustic.api.gl.Context.BlendFunction;
import com.flowpowered.caustic.api.gl.Context.Capability;
import com.flowpowered.caustic.api.gl.FrameBuffer;
import com.flowpowered.caustic.api.model.Model;
import com.flowpowered.caustic.api.util.Rectangle;

/**
 * Represents a rendering pipeline. A pipeline is built with a {@link PipelineBuilder}, as a series of actions. Running the pipeline results in the actions being executed in the order they were
 * added.
 */
public class Pipeline {
    private final List<Action> actions;

    /**
     * Constructs a new pipeline from the list of actions.
     *
     * @param actions The list of actions
     */
    protected Pipeline(List<Action> actions) {
        this.actions = actions;
    }

    /**
     * Runs the pipeline using the provided context.
     *
     * @param context The context to use.
     */
    public void run(Context context) {
        for (Action action : actions) {
            action.execute(context);
        }
    }

    /**
     * Used to built a pipeline through chained calls.
     */
    public static class PipelineBuilder {
        private final List<Action> actions = new LinkedList<>();

        /**
         * Builds the next action in the chain. The action sets the clear color to use when clearing color buffers.
         *
         * @param color The color to use for clearing buffers
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder useClearColor(Vector4f color) {
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
         * java.util.Arrays#sort(java.lang.Object[])}) so that models with the same materials are not reordered. This grouping improves performance by reducing the amount of rendering calls.
         *
         * @param models The models to render
         * @return The builder itself, for chained calls
         */
        public PipelineBuilder renderModels(Collection<? extends Model> models) {
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
            actions.add(action);
            return this;
        }

        /**
         * Builds the pipeline, returning it.
         *
         * @return The built pipeline
         */
        public Pipeline build() {
            return new Pipeline(actions);
        }
    }
}
