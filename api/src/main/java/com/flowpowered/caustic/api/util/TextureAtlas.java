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
package com.flowpowered.caustic.api.util;

import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.flowpowered.caustic.api.gl.Texture;
import com.flowpowered.caustic.api.gl.Texture.Format;

/**
 * A utility class used to stitch together multiple textures as to reduce the textures used.
 */
public class TextureAtlas {
    private final Map<String, RegionData> regions = new HashMap<>();
    private final BufferedImage image;

    public TextureAtlas(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Sets the image data of the provided {@link Texture} with the image data found in this {@link TextureAtlas}. This should be called after all textures have been added to the {@link
     * TextureAtlas}.
     *
     * @param texture Texture to set image data
     */
    public void attachTo(Texture texture) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        texture.setImageData(CausticUtil.getImageData(image, Format.RGBA), width, height);
    }

    /**
     * Retrieves the {@link RegionData} which is stored under the provided name.
     *
     * @param name Name of the region
     * @return The region data
     */
    public RegionData getTextureRegion(String name) {
        return regions.get(name);
    }

    /**
     * Adds the provided texture from the {@link java.io.InputStream} into this {@link TextureAtlas}.
     *
     * @param name The name of this {@link Texture}
     * @param input The {@link java.io.InputStream} of the texture
     * @throws com.flowpowered.caustic.api.util.TextureAtlas.TextureTooBigException
     * @throws java.io.IOException
     */
    public void addTexture(String name, InputStream input) throws TextureTooBigException, IOException {
        addTexture(name, ImageIO.read(input));
        input.close();
    }

    /**
     * Adds the provided texture from the {@link java.awt.image.BufferedImage} into this {@link TextureAtlas}.
     *
     * @param name The name of this {@link Texture}
     * @param image The {@link java.awt.image.BufferedImage} of the texture
     * @throws com.flowpowered.caustic.api.util.TextureAtlas.TextureTooBigException
     */
    public void addTexture(String name, BufferedImage image) throws TextureTooBigException {
        final RegionData data = findUsableRegion(image.getWidth(), image.getHeight());
        if (data == null) {
            throw new TextureTooBigException();
        }
        this.regions.put(name, data);
        final int[] pixels = new int[data.width * data.height];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        this.image.setRGB(data.x, data.y, data.width, data.height, pixels, 0, data.width);
    }

    /**
     * Attempts to find a usable region of this {@link TextureAtlas}
     *
     * @param width Width of the region
     * @param height Height of the region
     * @return The data for a valid region, null if none found.
     */
    private RegionData findUsableRegion(int width, int height) {
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();
        for (int y = 0; y < imageHeight - height; y++) {
            for (int x = 0; x < imageWidth - width; x++) {
                final RegionData data = new RegionData(x, y, width, height);
                if (!intersectsOtherTexture(data)) {
                    return data;
                }
            }
        }
        return null;
    }

    /**
     * Checks to see if the provided {@link RegionData} intersects with any other region currently used by another texture.
     *
     * @param data {@link RegionData}
     * @return true if it intersects another texture region
     */
    private boolean intersectsOtherTexture(RegionData data) {
        final Rectangle rec1 = new Rectangle(data.x, data.y, data.width, data.height);
        for (RegionData other : regions.values()) {
            final Rectangle rec2 = new Rectangle(other.x, other.y, other.width, other.height);
            if (rec1.intersects(rec2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stores data for a region in the {@link TextureAtlas}
     */
    public class RegionData {
        private final int x, y, width, height;

        public RegionData(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        /**
         * Gets the x coordinate of this texture region
         *
         * @return The x coordinate
         */
        public int getX() {
            return x;
        }

        /**
         * Gets the y coordinate of this texture region
         *
         * @return The y coordinate
         */
        public int getY() {
            return y;
        }

        /**
         * Gets the width of this texture region
         *
         * @return Texture region width
         */
        public int getWidth() {
            return width;
        }

        /**
         * Gets the height of this texture region
         *
         * @return Texture region height
         */
        public int getHeight() {
            return height;
        }
    }

    /**
     * If the texture attempting to be added to this {@link TextureAtlas} is too big or there is no space left for it, then this exception will be thrown.
     */
    private static final class TextureTooBigException extends Exception {
        private static final long serialVersionUID = 1L;

        private TextureTooBigException() {
            super("Texture is too big for this TextureAtlas or there isn't enough space");
        }
    }
}
