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
package com.flowpowered.caustic.api.data;

import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import com.flowpowered.caustic.api.gl.Shader.ShaderType;

/**
 * Represents the source of a shader. This class can be used to load a source from an input stream, and provides pre-compilation functionality such as parsing shader type, attribute layout and texture
 * layout tokens. These tokens can be used to declare various parameters directly in the shader code instead of in the software code, which simplifies loading.
 */
public class ShaderSource {
    private static final char TOKEN_SYMBOL = '$';
    private static final String SHADER_TYPE_TOKEN = "shader_type";
    private static final Pattern SHADER_TYPE_TOKEN_PATTERN = Pattern.compile("\\" + TOKEN_SYMBOL + SHADER_TYPE_TOKEN + " *: *(\\w+)");
    private static final String ATTRIBUTE_LAYOUT_TOKEN = "attrib_layout";
    private static final String TEXTURE_LAYOUT_TOKEN = "texture_layout";
    private static final Pattern LAYOUT_TOKEN_PATTERN = Pattern.compile("\\" + TOKEN_SYMBOL + "(" + ATTRIBUTE_LAYOUT_TOKEN + "|" + TEXTURE_LAYOUT_TOKEN + ") *: *(\\w+) *= *(\\d+)");
    private final CharSequence source;
    private ShaderType type;
    private final TObjectIntMap<String> attributeLayouts = new TObjectIntHashMap<>();
    private final TIntObjectMap<String> textureLayouts = new TIntObjectHashMap<>();

    /**
     * Constructs a new shader source from the input stream.
     *
     * @param source The source input stream
     */
    public ShaderSource(InputStream source) {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        final StringBuilder stringSource = new StringBuilder();
        try (Scanner reader = new Scanner(source)) {
            while (reader.hasNextLine()) {
                stringSource.append(reader.nextLine()).append('\n');
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Unreadable shader source", ex);
        }
        this.source = stringSource;
        parse();
    }

    /**
     * Constructs a new shader source from the character sequence.
     *
     * @param source The shader source
     */
    public ShaderSource(CharSequence source) {
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        this.source = source;
        parse();
    }

    private void parse() {
        // Look for layout tokens
        // Used for setting the shader type automatically.
        // Also replaces the GL30 "layout(location = x)" and GL42 "layout(binding = x) features missing from GL20 and/or GL30
        final String[] lines = source.toString().split("\n");
        for (String line : lines) {
            Matcher matcher = SHADER_TYPE_TOKEN_PATTERN.matcher(line);
            while (matcher.find()) {
                try {
                    type = ShaderType.valueOf(matcher.group(1).toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Unknown shader type token value", ex);
                }
            }
            matcher = LAYOUT_TOKEN_PATTERN.matcher(line);
            while (matcher.find()) {
                final String token = matcher.group(1);
                switch (token) {
                    case ATTRIBUTE_LAYOUT_TOKEN:
                        attributeLayouts.put(matcher.group(2), Integer.parseInt(matcher.group(3)));
                        break;
                    case TEXTURE_LAYOUT_TOKEN:
                        textureLayouts.put(Integer.parseInt(matcher.group(3)), matcher.group(2));
                        break;
                }
            }
        }
    }

    /**
     * Returns true if the shader source is complete and ready to be used in a {@link com.flowpowered.caustic.api.gl.Shader} object, false if otherwise. If this method returns false, than information such
     * as the type is missing.
     *
     * @return Whether or not the shader source is complete
     */
    public boolean isComplete() {
        return type != null;
    }

    /**
     * Returns the raw character sequence source of this shader source.
     *
     * @return The raw source
     */
    public CharSequence getSource() {
        return source;
    }

    /**
     * Returns the type of this shader. If the type was declared in the source using a shader type token, it will have been loaded from it. Else this returns null and it must be set manually using
     * {@link #setType(com.flowpowered.caustic.api.gl.Shader.ShaderType)}.
     *
     * @return The shader type, or null if not set
     */
    public ShaderType getType() {
        return type;
    }

    /**
     * Sets the shader type. It's not necessary to do this manually if it was declared in the source using a shader type token.
     *
     * @param type The shader type
     */
    public void setType(ShaderType type) {
        this.type = type;
    }

    /**
     * Returns the attribute layouts, either parsed from the source or set manually using {@link #setAttributeLayout(String, int)}.
     *
     * @return The attribute layouts
     */
    public TObjectIntMap<String> getAttributeLayouts() {
        return TCollections.unmodifiableMap(attributeLayouts);
    }

    /**
     * Returns the texture layouts, either parsed from the source or set manually using {@link #setTextureLayout(int, String)}.
     *
     * @return The texture layouts
     */
    public TIntObjectMap<String> getTextureLayouts() {
        return TCollections.unmodifiableMap(textureLayouts);
    }

    /**
     * Sets an attribute layout.
     *
     * @param attribute The name of the attribute
     * @param layout The layout for the attribute
     */
    public void setAttributeLayout(String attribute, int layout) {
        attributeLayouts.put(attribute, layout);
    }

    /**
     * Sets a texture layout.
     *
     * @param unit The unit for the sampler
     * @param sampler The sampler name
     */
    public void setTextureLayout(int unit, String sampler) {
        textureLayouts.put(unit, sampler);
    }
}
