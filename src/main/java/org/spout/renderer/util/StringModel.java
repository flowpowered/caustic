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
package org.spout.renderer.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gnu.trove.impl.Constants;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TCharIntMap;
import gnu.trove.map.hash.TCharFloatHashMap;
import gnu.trove.map.hash.TCharIntHashMap;

import org.spout.math.GenericMath;
import org.spout.renderer.GLVersion;
import org.spout.renderer.data.Uniform.ColorUniform;
import org.spout.renderer.data.Uniform.FloatUniform;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexData;
import org.spout.renderer.gl.Material;
import org.spout.renderer.gl.Model;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Shader.ShaderType;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.gl.Texture.FilterMode;
import org.spout.renderer.gl.Texture.ImageFormat;
import org.spout.renderer.gl.VertexArray;

/**
 * A model for rendering strings with a desired font. This model will work with both OpenGL
 * versions. After construction, set the OpenGL version with {@link #setGLVersion(org.spout.renderer.GLVersion)}.
 * Next, set the glyphs that the model should support (the character set) with {@link
 * #setGlyphs(char...)} or {@link #setGlyphs(String)}. Then, set the font with {@link
 * #setFont(java.awt.Font)}. Finally, set the window width with {@link #setWindowWidth(int)}. This
 * is used to calculate the dimensions of pixels so that the font appears as smooth as possible on a
 * GUI. The model can now be created with {@link #create()}, and added to a renderer. To render a
 * string, set it with {@link #setString(String)}. Glyphs in the string that have not been declared
 * when setting the glyphs will be ignored. When done, use {@link #destroy()} to release the model
 * resources. Please note that altering the OpenGL version, glyphs or font after creation has no
 * effect. The model needs to be recreated. The only exception is the font color.
 * <p/>
 * As for the implementation, this model wraps a model of the desired OpenGL version. The model
 * contains a mesh of tiles, all at (0,0). Each tile has one glyph on it. When rendering, the
 * desired tile is selected, placed at the origin and rendered. The next glyph will be rendered in
 * the same fashion, but offset on the x axis by the width of the last glyph. The mesh and indices
 * are only uploaded once; the renderer uses the indices for the desired glyph.
 */
public class StringModel extends Model {
	private static final int GLYPH_INDEX_COUNT = 6;
	private GLVersion glVersion;
	private char[] glyphs;
	private Font font;
	private int windowWidth = -1;
	private Model model;
	private final TCharIntMap glyphIndexes = new TCharIntHashMap(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, (char) 0, -1);
	private final TCharFloatMap glyphOffsets = new TCharFloatHashMap();
	private int glyphPadding = 6;
	private float normalizedGlyphPadding;
	private String string;

	@Override
	public void create() {
		if (created) {
			throw new IllegalStateException("Model has already been created");
		}
		if (glVersion == null) {
			throw new IllegalStateException("GL version has not been set");
		}
		if (glyphs == null) {
			throw new IllegalStateException("Glyphs to have not been set");
		}
		if (font == null) {
			throw new IllegalStateException("Font has not been set");
		}
		if (windowWidth == -1) {
			throw new IllegalStateException("The window width has not been set");
		}
		// Create temporary graphics, font metrics and render context
		final Graphics graphics = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR).getGraphics();
		graphics.setFont(font);
		final FontMetrics fontMetrics = graphics.getFontMetrics();
		final FontRenderContext fontRenderContext = fontMetrics.getFontRenderContext();
		// Obtain the glyph string size
		final Rectangle2D size = font.getStringBounds(String.valueOf(glyphs), fontRenderContext);
		// Obtain the width of each glyph
		final TCharIntMap widths = new TCharIntHashMap();
		for (char glyph : glyphs) {
			widths.put(glyph, fontMetrics.charWidth(glyph));
		}
		// Set the glyph padding to half the max width of the first 256 characters
		glyphPadding = GenericMath.mean(fontMetrics.getWidths()) / 2;
		// Dispose of the temporary resources
		graphics.dispose();
		// Create the texture
		int width = (int) Math.ceil(size.getWidth());
		final int height = (int) Math.ceil(size.getHeight());
		final Texture texture = glVersion.createTexture();
		width = generateTexture(texture, glyphs, widths, font, width, height);
		texture.create();
		// Set the normalized glyph padding, needs to be subtracted to the initial glyph offset
		normalizedGlyphPadding = glyphPadding / width;
		// Create the material
		final Material material = glVersion.createMaterial();
		generateMaterial(material, glVersion);
		material.addTexture(texture);
		material.create();
		// Create the model mesh
		model = glVersion.createModel();
		generateMesh(model, glyphs, widths, width, height);
		model.setMaterial(material);
		model.create();
		// Only render one glyph per render call
		model.getVertexArray().setIndicesCount(GLYPH_INDEX_COUNT);
		// Add a uniform for the glyph position offset
		uniforms.add(new FloatUniform("glyphOffset", 0));
		// Add a uniform for the font color
		uniforms.add(new ColorUniform("fontColor", Color.WHITE));
		// Release some resources
		glVersion = null;
		glyphs = null;
		font = null;
		windowWidth = -1;
		// Update the state
		super.create();
	}

	@Override
	public void destroy() {
		checkCreated();
		model.destroy();
		model = null;
		glyphIndexes.clear();
		glyphOffsets.clear();
		string = null;
		super.destroy();
	}

	@Override
	public void uploadUniforms() {
		super.uploadUniforms();
		model.getMaterial().getProgram().upload(uniforms.getMatrix4("modelMatrix"));
	}

	@Override
	public void render() {
		checkCreated();
		final Program program = model.getMaterial().getProgram();
		program.upload(uniforms.getColor("fontColor"));
		final VertexArray vertexArray = model.getVertexArray();
		final FloatUniform glyphOffset = uniforms.getFloat("glyphOffset");
		// Remove the padding for the first glyph
		float totalGlyphOffset = -normalizedGlyphPadding;
		for (char glyph : string.toCharArray()) {
			// Get the glyph start index
			final int glyphIndex = glyphIndexes.get(glyph);
			// Skip glyphs missing in the texture
			if (glyphIndex == -1) {
				continue;
			}
			// Set rendering indices offset for the glyph
			vertexArray.setIndicesOffset(glyphIndex);
			// Offset the glyph in the string
			glyphOffset.set(totalGlyphOffset);
			program.upload(glyphOffset);
			// Offset for the next glyph
			totalGlyphOffset += glyphOffsets.get(glyph);
			// Render the model
			model.render();
		}
	}

	@Override
	public Material getMaterial() {
		return model != null ? model.getMaterial() : null;
	}

	@Override
	public void setMaterial(Material material) {
		throw new UnsupportedOperationException("Unsupported by string model");
	}

	@Override
	public VertexArray getVertexArray() {
		throw new UnsupportedOperationException("Unsupported by string model");
	}

	@Override
	public GLVersion getGLVersion() {
		return model != null ? model.getGLVersion() : null;
	}

	/**
	 * Sets the OpenGL version for the model.
	 *
	 * @param version The version
	 */
	public void setGLVersion(GLVersion version) {
		this.glVersion = version;
	}

	/**
	 * Sets the glyphs that compose the character set for the model.
	 *
	 * @param glyphs The glyphs
	 */
	public void setGlyphs(String glyphs) {
		setGlyphs(glyphs.toCharArray());
	}

	/**
	 * Sets the glyphs that compose the character set for the model.
	 *
	 * @param glyphs The glyphs
	 */
	public void setGlyphs(char... glyphs) {
		this.glyphs = glyphs;
	}

	/**
	 * Sets the font to renderer the glyphs with.
	 *
	 * @param font The font
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Sets the window width.
	 *
	 * @param width The window width
	 */
	public void setWindowWidth(int width) {
		this.windowWidth = width;
	}

	/**
	 * Sets the string to render.
	 *
	 * @param string The string to render
	 */
	public void setString(String string) {
		this.string = string;
	}

	/**
	 * Sets the font color.
	 *
	 * @param color The font color
	 */
	public void setColor(Color color) {
		uniforms.getColor("fontColor").set(color);
	}

	private void generateMesh(Model destination, char[] glyphs, TCharIntMap glyphWidths, int textureWidth, int textureHeight) {
		// Add the positions and texture coordinates attributes
		final VertexData data = new VertexData();
		final VertexAttribute positionAttribute = new VertexAttribute("positions", DataType.FLOAT, 2);
		final VertexAttribute textureCoordsAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, 2);
		final TFloatList positions = new TFloatArrayList();
		final TFloatList textureCoords = new TFloatArrayList();
		data.addAttribute(0, positionAttribute);
		data.addAttribute(1, textureCoordsAttribute);
		// Get the indices
		final TIntList indices = data.getIndices();
		// Generate a pile of small rectangles, each having one glyph on it
		// Rendering a sequence of glyphs means rendering the sequence of rectangles with the correct glyphs
		// Offsetting them correctly
		/*
			1--3
			|\ |
			| \|
			0--2
	 	*/
		float x = 0;
		int index = 0;
		int i = 0;
		// This ratio corrects the size so that pixels correspond as perfectly as possible
		final float widthRatio = (float) textureWidth / windowWidth;
		final float glyphHeight = ((float) textureHeight / textureWidth) * widthRatio;
		for (char glyph : glyphs) {
			final int glyphWidth = glyphWidths.get(glyph);
			final float paddedGlyphWidth = (float) (glyphWidth + glyphPadding * 2) / textureWidth;
			add(positions, 0, 0, 0, glyphHeight);
			add(textureCoords, x, 0, x, 1);
			x += paddedGlyphWidth;
			add(positions, paddedGlyphWidth * widthRatio, 0, paddedGlyphWidth * widthRatio, glyphHeight);
			add(textureCoords, x, 0, x, 1);
			add(indices, index, index + 2, index + 1, index + 2, index + 3, index + 1);
			index += 4;
			glyphIndexes.put(glyph, i);
			glyphOffsets.put(glyph, ((float) glyphWidth / textureWidth) * widthRatio);
			i += 6;
		}
		positionAttribute.setData(positions);
		textureCoordsAttribute.setData(textureCoords);
		// Set the vertex data in the model
		destination.getVertexArray().setData(data);
	}

	private int generateTexture(Texture destination, char[] glyphs, TCharIntMap glyphWidths, Font font, int width, int height) {
		// Create an image for the texture
		width += glyphs.length * glyphPadding * 2;
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = image.getGraphics();
		// Draw the glyphs in white on a transparent background
		graphics.setColor(Color.white);
		graphics.setFont(font);
		((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		final FontMetrics fontMetrics = graphics.getFontMetrics();
		int x = 0;
		final int y = fontMetrics.getAscent();
		for (char glyph : glyphs) {
			x += glyphPadding;
			graphics.drawString(String.valueOf(glyph), x, y);
			x += glyphWidths.get(glyph) + glyphPadding;
		}
		// Dispose of the image graphics
		graphics.dispose();
		// Generate the texture
		destination.setFormat(ImageFormat.RGBA);
		destination.setImageData(image);
		destination.setMagFilter(FilterMode.LINEAR);
		destination.setMinFilter(FilterMode.LINEAR);
		destination.setUnit(0);
		return width;
	}

	private static void add(TFloatList list, float... f) {
		list.add(f);
	}

	private static void add(TIntList list, int... f) {
		list.add(f);
	}

	private static void generateMaterial(Material destination, GLVersion version) {
		final Program program = destination.getProgram();
		final String shaderPath = "/shaders/" + version.toString().toLowerCase() + "/";
		program.addShaderSource(ShaderType.VERTEX, StringModel.class.getResourceAsStream(shaderPath + "font.vert"));
		program.addShaderSource(ShaderType.FRAGMENT, StringModel.class.getResourceAsStream(shaderPath + "font.frag"));
		if (version == GLVersion.GL20) {
			program.addAttributeLayout("position", 0);
			program.addAttributeLayout("textureCoords", 1);
		}
		program.addTextureLayout("diffuse", 0);
	}
}
