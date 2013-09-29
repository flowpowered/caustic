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
package org.spout.renderer.android.gles20;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import org.spout.renderer.android.AndroidUtil;
import org.spout.renderer.data.Color;
import org.spout.renderer.gl.Context;
import org.spout.renderer.gl.Texture.Format;
import org.spout.renderer.util.CausticUtil;
import org.spout.renderer.util.Rectangle;

/**
 * An OpenGLES 2.0 implementation of {@link org.spout.renderer.gl.Context}.
 *
 * @see org.spout.renderer.gl.Context
 */
public class GLES20Context extends Context implements GLSurfaceView.Renderer {
	protected GLES20Context() {
	}

	@Override
	public void create() {
		if (isCreated()) {
			throw new IllegalStateException("Context has already been created");
		}
		// TODO: Attempt to create the display
		/*
		Display.setDisplayMode(new DisplayMode(windowSize.getFloorX(), windowSize.getFloorY()));
		Display.create(new PixelFormat().withSamples(MSAA), createContextAttributes());
		// Set the title
		Display.setTitle(windowTitle);
		*/
		// Set the default view port
		GLES20.glViewport(0, 0, windowSize.getFloorX(), windowSize.getFloorY());
		// Set the alpha blending function for transparency
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		// Check for errors
		AndroidUtil.checkForGLESError();
		// Update the state
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		// Display goes after else there's no context in which to check for an error
		AndroidUtil.checkForGLESError();
		// TODO: Destroy the display
		// Display.destroy();
		super.destroy();
	}

	@Override
	public void updateDisplay() {
		checkCreated();
		// TODO: Update the display
	}

	@Override
	public void setClearColor(Color color) {
		Color normC = color.normalize();
		GLES20.glClearColor(normC.getRed(), normC.getGreen(), normC.getBlue(), normC.getAlpha());
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public void clearCurrentBuffer() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public void enableCapability(Capability capability) {
		GLES20.glEnable(capability.getGLConstant());
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public void setViewPort(Rectangle viewPort) {
		GLES20.glViewport(viewPort.getX(), viewPort.getY(), viewPort.getWidth(), viewPort.getHeight());
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public ByteBuffer readCurrentFrame(Rectangle size, Format format) {
		ByteBuffer buffer = CausticUtil.createByteBuffer(size.getArea() * 3);
		GLES20.glReadPixels(size.getX(), size.getY(), size.getWidth(), size.getHeight(), format.getGLConstant(), GLES20.GL_UNSIGNED_BYTE, buffer);
		// Check for errors
		AndroidUtil.checkForGLESError();
		return buffer;
	}

	@Override
	public void disableCapability(Capability capability) {
		GLES20.glDisable(capability.getGLConstant());
		// Check for errors
		AndroidUtil.checkForGLESError();
	}

	@Override
	public GLVersion getGLVersion() {
		return GLVersion.GLES20;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		create();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
	}

	@Override
	public void onDrawFrame(GL10 unused) {
		//render();
	}
}
