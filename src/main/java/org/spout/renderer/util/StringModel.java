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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gnu.trove.impl.Constants;
import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.map.TCharFloatMap;
import gnu.trove.map.TCharIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TCharFloatHashMap;
import gnu.trove.map.hash.TCharIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.spout.math.GenericMath;
import org.spout.math.vector.Vector2;
import org.spout.renderer.GLVersion;
import org.spout.renderer.Material;
import org.spout.renderer.Model;
import org.spout.renderer.data.Uniform.ColorUniform;
import org.spout.renderer.data.Uniform.Matrix4Uniform;
import org.spout.renderer.data.Uniform.Vector2Uniform;
import org.spout.renderer.data.UniformHolder;
import org.spout.renderer.data.VertexAttribute;
import org.spout.renderer.data.VertexAttribute.DataType;
import org.spout.renderer.data.VertexData;
import org.spout.renderer.gl.Color;
import org.spout.renderer.gl.Program;
import org.spout.renderer.gl.Shader;
import org.spout.renderer.gl.Shader.ShaderType;
import org.spout.renderer.gl.Texture;
import org.spout.renderer.gl.Texture.FilterMode;
import org.spout.renderer.gl.Texture.Format;
import org.spout.renderer.gl.VertexArray;

/**
 * A model for rendering strings with a desired font. This model will work with both OpenGL versions. To render a string, set it with {@link #setString(String)}. Glyphs in the string that have not
 * been declared when constructing the model will be ignored. <p/> Colors are supported. Use <code>#aarrggbb</code>, where <code>aa</code> is the alpha hexadecimal value, <code>rr</code> is the red
 * hexadecimal value, <code>gg</code> is the green hexadecimal value and <code>bb</code> is the blue hexadecimal value. Color codes can be escaped with <code>\</code>. <p/> As for the implementation,
 * this model wraps a model of the desired OpenGL version. The model contains a mesh of tiles, all at (0,0). Each tile has one glyph on it. When rendering, the desired tile is selected, placed at the
 * origin and rendered. The next glyph will be rendered in the same fashion, but offset on the x axis by the width of the last glyph. The mesh and indices are only uploaded once; the renderer uses the
 * indices for the desired glyph.
 */
public class StringModel extends Model {
	private static final int GLYPH_INDEX_COUNT = 6;
	private static final Pattern COLOR_PATTERN = Pattern.compile("#[a-fA-F\\d]{1,8}");
	private final TCharIntMap glyphIndexes = new TCharIntHashMap(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, (char) 0, -1);
	private final TCharFloatMap glyphOffsets = new TCharFloatHashMap();
	private final int glyphPadding;
	private final float worldGlyphPadding;
	private final float worldLineHeight;
	private String rawString;
	private String string;
	private final TIntObjectMap<Color> colorIndices = new TIntObjectHashMap<>();

	/**
	 * Constructs a new string model from the provided one. {@link org.spout.renderer.Model#Model()} defines the copied information in the {@link Model} class. For the string model, the glyph indexes,
	 * offsets, padding and line heights are copied. The string and color information remain empty.
	 *
	 * @param model The model to derive this one from
	 */
	protected StringModel(StringModel model) {
		super(model);
		this.glyphIndexes.putAll(model.glyphIndexes);
		this.glyphOffsets.putAll(model.glyphOffsets);
		this.glyphPadding = model.glyphPadding;
		this.worldGlyphPadding = model.worldGlyphPadding;
		this.worldLineHeight = model.worldLineHeight;
	}

	/**
	 * Creates a new string model, from the OpenGL version, the glyphs to support, the font to render with and the window width (used to get scale for the model).
	 *
	 * @param glVersion The OpenGL version
	 * @param glyphs The glyphs
	 * @param font The font
	 * @param windowWidth The window with
	 */
	public StringModel(GLVersion glVersion, CharSequence glyphs, Font font, int windowWidth) {
		if (glVersion == null) {
			throw new IllegalStateException("GL version cannot be null");
		}
		if (glyphs == null) {
			throw new IllegalStateException("Glyphs cannot be null");
		}
		if (font == null) {
			throw new IllegalStateException("Font cannot be null");
		}
		if (windowWidth <= 0) {
			throw new IllegalStateException("The window width must be greater than zero");
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
		for (int i = 0; i < glyphs.length(); i++) {
			final char glyph = glyphs.charAt(i);
			widths.put(glyph, fontMetrics.charWidth(glyph));
		}
		// Set the glyph padding to half the mean width of the first 256 characters
		glyphPadding = GenericMath.mean(fontMetrics.getWidths()) / 2;
		// Dispose of the temporary resources
		graphics.dispose();
		// Create the texture
		final int width = (int) Math.ceil(size.getWidth()) + glyphs.length() * glyphPadding * 2;
		final int height = (int) Math.ceil(size.getHeight());
		final Texture texture = generateTexture(glVersion, glyphs, widths, font, width, height);
		// Set the normalized glyph padding, needs to be subtracted to the initial glyph offset
		worldGlyphPadding = (float) glyphPadding / windowWidth;
		// Set the line height, for new lines
		worldLineHeight = (float) fontMetrics.getHeight() / windowWidth;
		// Create the material
		final Material material = generateMaterial(glVersion);
		material.addTexture(0, texture);
		setMaterial(material);
		// Create the model mesh
		final VertexArray vertexArray = generateMesh(glVersion, glyphs, windowWidth, widths, width, height);
		// Only render one glyph per render call
		vertexArray.setIndicesCount(GLYPH_INDEX_COUNT);
		// Set the vertex array
		setVertexArray(vertexArray);
		// Add a uniform for the glyph position offset
		final UniformHolder uniforms = getUniforms();
		uniforms.add(new Vector2Uniform("glyphOffset", Vector2.ZERO));
		// Add a uniform for the font color
		uniforms.add(new ColorUniform("fontColor", Color.WHITE));
	}

	@Override
	public void uploadUniforms() {
		final Matrix4Uniform modelMatrixUniform = getUniforms().getMatrix4("modelMatrix");
		modelMatrixUniform.set(getMatrix());
		getMaterial().getProgram().upload(modelMatrixUniform);
	}

	@Override
	public void render() {
		final Program program = getMaterial().getProgram();
		final UniformHolder uniforms = getUniforms();
		final ColorUniform colorUniform = uniforms.getColor("fontColor");
		colorUniform.set(Color.WHITE);
		program.upload(colorUniform);
		final VertexArray vertexArray = getVertexArray();
		final Vector2Uniform glyphOffset = uniforms.getVector2("glyphOffset");
		// Remove the padding for the first glyph
		Vector2 offset = new Vector2(-worldGlyphPadding, 0);
		final char[] glyphs = string.toCharArray();
		for (int i = 0; i < glyphs.length; i++) {
			final char glyph = glyphs[i];
			// Move the glyph offset to the next line for the new line character
			if (glyph == '\n') {
				offset = new Vector2(-worldGlyphPadding, offset.getY() - worldLineHeight);
				continue;
			}
			// Look for a color code
			final Color color = colorIndices.get(i);
			if (color != null) {
				// Upload the color
				colorUniform.set(color);
				program.upload(colorUniform);
			}
			// Get the glyph start index
			final int glyphIndex = glyphIndexes.get(glyph);
			// Skip glyphs missing in the texture
			if (glyphIndex == -1) {
				continue;
			}
			// Set rendering indices offset for the glyph
			vertexArray.setIndicesOffset(glyphIndex);
			// Offset the glyph in the string
			glyphOffset.set(offset);
			program.upload(glyphOffset);
			// Offset for the next glyph
			offset = offset.add(glyphOffsets.get(glyph), 0);
			// Render the model
			vertexArray.draw();
		}
	}

	/**
	 * Sets the string to render.
	 *
	 * @param string The string to render
	 */
	public void setString(String string) {
		rawString = string;
		colorIndices.clear();
		// Search for color codes
		final StringBuilder stringBuilder = new StringBuilder(string);
		final Matcher matcher = COLOR_PATTERN.matcher(string);
		int removedCount = 0;
		while (matcher.find()) {
			final int index = matcher.start() - removedCount;
			// Ignore escaped color codes
			if (index > 0 && stringBuilder.charAt(index - 1) == '\\') {
				// Remove the escape character
				stringBuilder.deleteCharAt(index - 1);
				removedCount++;
				continue;
			}
			// Add the color for the index and delete it from the string
			final String colorCode = matcher.group();
			colorIndices.put(index, new Color(Long.decode(colorCode).intValue(), true));
			final int length = colorCode.length();
			stringBuilder.delete(index, index + length);
			removedCount += length;
		}
		// Color code free string
		this.string = stringBuilder.toString();
	}

	/**
	 * Returns the model's string.
	 *
	 * @return The string
	 */
	public String getString() {
		return rawString;
	}

	private VertexArray generateMesh(GLVersion version, CharSequence glyphs, int windowWidth, TCharIntMap glyphWidths, int textureWidth, int textureHeight) {
		final VertexData data = new VertexData();
		// Add the positions and texture coordinates attributes
		final VertexAttribute positionAttribute = new VertexAttribute("positions", DataType.FLOAT, 2);
		data.addAttribute(0, positionAttribute);
		final TFloatList positions = new TFloatArrayList();
		final VertexAttribute textureCoordsAttribute = new VertexAttribute("textureCoords", DataType.FLOAT, 2);
		data.addAttribute(1, textureCoordsAttribute);
		final TFloatList textureCoords = new TFloatArrayList();
		// Get the indices
		final TIntList indices = data.getIndices();
		/*
		Generate a pile of small rectangles, each having one glyph on it
		Rendering a sequence of glyphs means rendering the sequence of rectangles with the correct glyphs
		Offsetting them correctly
		1--3
		|\ |
		| \|
		0--2
	 	*/
		float x = 0;
		int index = 0;
		int renderIndex = 0;
		final float glyphHeight = (float) textureHeight / windowWidth;
		for (int i = 0; i < glyphs.length(); i++) {
			final char glyph = glyphs.charAt(i);
			final int glyphWidth = glyphWidths.get(glyph);
			float paddedGlyphWidth = glyphWidth + glyphPadding * 2;
			add(positions, 0, 0, 0, glyphHeight);
			add(textureCoords, x, 0, x, 1);
			x += paddedGlyphWidth / textureWidth;
			paddedGlyphWidth /= windowWidth;
			add(positions, paddedGlyphWidth, 0, paddedGlyphWidth, glyphHeight);
			add(textureCoords, x, 0, x, 1);
			add(indices, index, index + 2, index + 1, index + 2, index + 3, index + 1);
			index += 4;
			glyphIndexes.put(glyph, renderIndex);
			glyphOffsets.put(glyph, (float) glyphWidth / windowWidth);
			renderIndex += 6;
		}
		positionAttribute.setData(positions);
		textureCoordsAttribute.setData(textureCoords);
		// Set the vertex data in the model
		final VertexArray vertexArray = version.createVertexArray();
		vertexArray.setData(data);
		vertexArray.create();
		return vertexArray;
	}

	private Texture generateTexture(GLVersion glVersion, CharSequence glyphs, TCharIntMap glyphWidths, Font font, int width, int height) {
		final Texture texture = glVersion.createTexture();
		// Create an image for the texture
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics graphics = image.getGraphics();
		// Draw the glyphs in white on a transparent background
		graphics.setColor(java.awt.Color.WHITE);
		graphics.setFont(font);
		((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		final FontMetrics fontMetrics = graphics.getFontMetrics();
		int x = 0;
		final int y = fontMetrics.getAscent();
		for (int i = 0; i < glyphs.length(); i++) {
			final char glyph = glyphs.charAt(i);
			x += glyphPadding;
			graphics.drawString(String.valueOf(glyph), x, y);
			x += glyphWidths.get(glyph) + glyphPadding;
		}
		// Dispose of the image graphics
		graphics.dispose();
		// Generate the texture
		texture.setFormat(Format.RGBA);
		texture.setImageData(RenderUtil.getImageData(image, Format.RGBA), image.getWidth(), image.getHeight());
		texture.setMagFilter(FilterMode.LINEAR);
		texture.setMinFilter(FilterMode.LINEAR);
		texture.create();
		return texture;
	}

	private Material generateMaterial(GLVersion version) {
		final Program program = version.createProgram();
		final String shaderPath = "/shaders/" + version.toString().toLowerCase() + "/";
		final Shader vertShader = version.createShader();
		vertShader.setSource(StringModel.class.getResourceAsStream(shaderPath + "font.vert"));
		vertShader.setType(ShaderType.VERTEX);
		vertShader.create();
		program.addShader(vertShader);
		final Shader fragShader = version.createShader();
		fragShader.setSource(StringModel.class.getResourceAsStream(shaderPath + "font.frag"));
		fragShader.setType(ShaderType.FRAGMENT);
		fragShader.create();
		program.addShader(fragShader);
		if (version == GLVersion.GL20) {
			program.addAttributeLayout("position", 0);
			program.addAttributeLayout("textureCoords", 1);
		}
		program.addTextureLayout("diffuse", 0);
		program.create();
		return new Material(program);
	}

	private static void add(TFloatList list, float... f) {
		list.add(f);
	}

	private static void add(TIntList list, int... f) {
		list.add(f);
	}
}
