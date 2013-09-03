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

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import org.spout.renderer.Creatable;
import org.spout.renderer.GLVersioned;

/**
 * Represents an OpenGL shader. The shader source and type must be set with {@link #setSource(java.io.InputStream)} and {@link #setType(Shader.ShaderType)} respectively before it can be created.
 */
public abstract class Shader extends Creatable implements GLVersioned {
	private static final char TOKEN_SYMBOL = '$';
	private static final String ATTRIBUTE_LAYOUT_TOKEN = "attrib_layout";
	private static final String TEXTURE_LAYOUT_TOKEN = "texture_layout";
	private static final Pattern LAYOUT_TOKEN_PATTERN = Pattern.compile("\\" + TOKEN_SYMBOL + "(" + ATTRIBUTE_LAYOUT_TOKEN + "|" + TEXTURE_LAYOUT_TOKEN + ") *: *(\\w+) *= *(\\d+)");
	protected int id;
	protected CharSequence source;
	protected ShaderType type;
	private TObjectIntMap<String> attributeLayouts;
	private TIntObjectMap<String> textureLayouts;

	@Override
	public void create() {
		// Release the shader source
		source = null;
		super.create();
	}

	@Override
	public void destroy() {
		id = 0;
		type = null;
		attributeLayouts = null;
		textureLayouts = null;
		super.destroy();
	}

	/**
	 * Gets the ID for this shader as assigned by OpenGL.
	 *
	 * @return The ID
	 */
	public int getID() {
		return id;
	}

	/**
	 * Sets the shader source input stream.
	 *
	 * @param source The source input stream
	 */
	public void setSource(InputStream source) {
		final StringBuilder stringSource = new StringBuilder();
		try (Scanner reader = new Scanner(source)) {
			while (reader.hasNextLine()) {
				stringSource.append(reader.nextLine()).append('\n');
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Unreadable shader source", ex);
		}
		setSource(stringSource);
	}

	/**
	 * Sets the shader source.
	 *
	 * @param source The shader source
	 */
	public void setSource(CharSequence source) {
		this.source = source;
		// Look for layout tokens
		// This replaces the GL30 "layout(location = x)" and GL42 "layout(binding = x) features missing from GL20
		final String[] lines = source.toString().split("\n");
		for (String line : lines) {
			final Matcher matcher = LAYOUT_TOKEN_PATTERN.matcher(line);
			while (matcher.find()) {
				final String token = matcher.group(1);
				if (token.equals(ATTRIBUTE_LAYOUT_TOKEN)) {
					if (attributeLayouts == null) {
						attributeLayouts = new TObjectIntHashMap<>();
					}
					attributeLayouts.put(matcher.group(2), Integer.parseInt(matcher.group(3)));
				} else if (token.equals(TEXTURE_LAYOUT_TOKEN)) {
					if (textureLayouts == null) {
						textureLayouts = new TIntObjectHashMap<>();
					}
					textureLayouts.put(Integer.parseInt(matcher.group(3)), matcher.group(2));
				}
			}
		}
	}

	/**
	 * Sets the shader type.
	 *
	 * @param type The shader type
	 */
	public void setType(ShaderType type) {
		this.type = type;
	}

	/**
	 * Gets the shader type.
	 *
	 * @return The shader type
	 */
	public ShaderType getType() {
		return type;
	}

	/**
	 * Returns the attribute layouts parsed from the tokens in the shader source.
	 *
	 * @return A map of the attribute name to the layout index.
	 */
	protected TObjectIntMap<String> getAttributeLayouts() {
		return attributeLayouts;
	}

	/**
	 * Returns the texture layouts parsed from the tokens in the shader source.
	 *
	 * @return A map of the texture name to the layout index.
	 */
	protected TIntObjectMap<String> getTextureLayouts() {
		return textureLayouts;
	}

	/**
	 * Represents a shader type.
	 */
	public static enum ShaderType {
		FRAGMENT(0x8B30), // GL20.GL_FRAGMENT_SHADER
		VERTEX(0x8B31), // GL20.GL_VERTEX_SHADER
		GEOMETRY(0x8DD9), // GL32.GL_GEOMETRY_SHADER
		TESS_EVALUATION(0x8E87), // GL40.GL_TESS_EVALUATION_SHADER
		TESS_CONTROL(0x8E88), // GL40.GL_TESS_CONTROL_SHADER
		COMPUTE(0x91B9); // GL43.GL_COMPUTE_SHADER
		private final int glConstant;

		private ShaderType(int glConstant) {
			this.glConstant = glConstant;
		}

		/**
		 * Returns the OpenGL constant associated to the shader type.
		 *
		 * @return The OpenGL constant
		 */
		public int getGLConstant() {
			return glConstant;
		}
	}
}
