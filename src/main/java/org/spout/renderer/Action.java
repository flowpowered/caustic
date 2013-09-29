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

import java.util.Collections;
import java.util.List;

import org.spout.renderer.data.Color;
import org.spout.renderer.gl.Context;
import org.spout.renderer.gl.Context.Capability;
import org.spout.renderer.gl.FrameBuffer;
import org.spout.renderer.gl.Program;
import org.spout.renderer.model.Model;
import org.spout.renderer.util.Rectangle;

/**
 * Represents an action that can be executed with {@link #execute(org.spout.renderer.gl.Context)}. Actions can be chained using {@link #setNext(Action)}. Calling {@link
 * #executeChain(org.spout.renderer.gl.Context)} will execute this action, and all of the chained actions, in order.
 */
public abstract class Action {
	private Action next;

	/**
	 * Executes this actions with the desired rendering context.
	 *
	 * @param context The rendering context
	 */
	public abstract void execute(Context context);

	/**
	 * Executes this action followed by the rest of the chain, in order, with the desired rendering context.
	 *
	 * @param context The rendering context.
	 */
	public void executeChain(Context context) {
		execute(context);
		if (next != null) {
			next.executeChain(context);
		}
	}

	/**
	 * Sets the action immediately following this one.
	 *
	 * @param next The next action
	 */
	public void setNext(Action next) {
		if (next == null) {
			throw new IllegalArgumentException("Next action cannot be null");
		}
		if (next == this) {
			throw new IllegalArgumentException("Next action cannot be the action itself");
		}
		this.next = next;
	}

	/**
	 * An action that sets the color for clearing color buffers in the context.
	 */
	public static class SetClearColorAction extends Action {
		private final Color color;

		/**
		 * Constructs a clear color setting action with the desired clearing color.
		 *
		 * @param color The clearing color
		 */
		public SetClearColorAction(Color color) {
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
		private final Rectangle viewPort;

		/**
		 * Constructs a view port setting action with the desired view port.
		 *
		 * @param viewPort The view port
		 */
		public SetViewPortAction(Rectangle viewPort) {
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
		private final Capability[] capabilities;

		/**
		 * Constructs a capability enabling action with the desired capabilities to enable.
		 *
		 * @param capabilities The capabilities
		 */
		public EnableCapabilitiesAction(Capability... capabilities) {
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
		private final Capability[] capabilities;

		/**
		 * Constructs a capability disabling action with the desired capabilities to disable.
		 *
		 * @param capabilities The capabilities
		 */
		public DisableCapabilitiesAction(Capability... capabilities) {
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
		private final FrameBuffer frameBuffer;

		/**
		 * Constructs a frame buffer binding action with the desired frame buffer to bind.
		 *
		 * @param frameBuffer The frame buffer
		 */
		public BindFrameBufferAction(FrameBuffer frameBuffer) {
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
		private final FrameBuffer frameBuffer;

		/**
		 * Constructs a frame buffer unbinding action with the desired frame buffer to unbind.
		 *
		 * @param frameBuffer The frame buffer
		 */
		public UnbindFrameBufferAction(FrameBuffer frameBuffer) {
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
		private final Camera camera;

		/**
		 * Constructs a camera setting action with the desired camera.
		 *
		 * @param camera The camera
		 */
		public SetCameraAction(Camera camera) {
			this.camera = camera;
		}

		@Override
		public void execute(Context context) {
			context.setCamera(camera);
		}
	}

	/**
	 * An action that renders the models to the bound buffer. The models will be reordered to be grouped by material. This reordering is done via a stable sort ({@link
	 * java.util.Collections#sort(java.util.List)}) so that models with the same materials are not reordered. This grouping improves performance by reducing the amount of rendering calls.
	 */
	public static class RenderModelsAction extends Action {
		private final List<Model> models;

		/**
		 * Constructs a model rendering action with the models to render
		 *
		 * @param models The models
		 */
		public RenderModelsAction(List<Model> models) {
			this.models = models;
		}

		@Override
		public void execute(Context context) {
			// Batch the models with the same materials together
			Collections.sort(models);
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
