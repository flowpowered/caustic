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
package org.spout.renderer.gl20;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import org.spout.renderer.Model.DrawMode;
import org.spout.renderer.VertexArray;
import org.spout.renderer.data.VertexData;
import org.spout.renderer.util.RenderUtil;

/**
 * Represents an OpenGL 2.0 vertex array. After constructing it, set the vertex data source with
 * {@link #setVertexData(org.spout.renderer.data.VertexData)}. It can then be created in the OpenGL
 * context with {@link #create()}. To dispose of it, use {@link #destroy()}.
 */
public class OpenGL20VertexArray extends VertexArray {

	private int indicesBufferID = 0;
	private int[] attributeBufferIDs;

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("VertexArray has already been created");
		}
		if (vertexData == null) {
			throw new IllegalStateException("Vertex data has not been set");
		}

		//Create and bind buffer
		id = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);

		indicesBufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, vertexData.getIndicesBuffer(), GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

		renderingIndicesCount = vertexData.getIndicesCount();
		attributeBufferIDs = new int[vertexData.getAttributeCount()];

		for (int i = 0 ; i < vertexData.getAttributeCount() ; i++) {
			final VertexData.VertexAttribute attribute = vertexData.getAttribute(i);
			final int bufferID = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, attribute.getBuffer(), GL15.GL_STATIC_DRAW);
			GL20.glVertexAttribPointer(i, attribute.getSize(), attribute.getType().getGLConstant(), false, 0, 0);
			attributeBufferIDs[i] = bufferID;
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		super.create();
		RenderUtil.checkForOpenGLError();
	}

	@Override
	public void destroy() {
		checkCreated();

		//Unbind and delete indices buffer
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(indicesBufferID);

		//Bind vertex buffer for deletion
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);

		for (int i = 0 ; i < vertexData.getAttributeCount() ; i++) {
			GL20.glDisableVertexAttribArray(i);
			//GL15.glDeleteBuffers(attributeBufferIDs[i]);
		}

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL15.glDeleteBuffers(id);

		indicesBufferID = 0;
		//attributeBufferIDs = null;

		super.destroy();
		RenderUtil.checkForOpenGLError();
	}

	public void render(DrawMode mode) {
		checkCreated();

		for (int x = 0 ; x < attributeBufferIDs.length ; x++) {
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, attributeBufferIDs[x]);
			for (int i = 0 ; i < vertexData.getAttributeCount() ; i++) {
				GL20.glEnableVertexAttribArray(i);
			}
			//Bind the indices buffer
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
			//Draw all indices with the provided mode
			GL11.glDrawElements(mode.getGLConstant(), renderingIndicesCount, GL11.GL_UNSIGNED_INT, 0);
			//Unbind the indices buffer
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
			//Disable all attributes
			for (int i = 0 ; i < vertexData.getAttributeCount() ; i++) {
				GL20.glDisableVertexAttribArray(i);
			}
			RenderUtil.checkForOpenGLError();
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		/*GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
		 for (int i = 0 ; i < vertexData.getAttributeCount() ; i++) {
		 GL20.glEnableVertexAttribArray(i);
		 }
		 //Bind the indices buffer
		 GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBufferID);
		 //Draw all indices with the provided mode
		 GL11.glDrawElements(mode.getGLConstant(), renderingIndicesCount, GL11.GL_UNSIGNED_INT, 0);
		 //Unbind the indices buffer
		 GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		 //Disable all attributes
		 for (int i = 0 ; i < vertexData.getAttributeCount() ; i++) {
		 GL20.glDisableVertexAttribArray(i);
		 }
		 //Unbind the vertex buffer
		 GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		 //Check for errors
		 RenderUtil.checkForOpenGLError();*/
	}
}