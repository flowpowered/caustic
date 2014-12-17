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
package com.flowpowered.caustic.api.util;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Logger;

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.vector.Vector4f;

import com.flowpowered.caustic.api.GLVersioned;
import com.flowpowered.caustic.api.GLVersioned.GLVersion;
import com.flowpowered.caustic.api.data.VertexAttribute.DataType;
import com.flowpowered.caustic.api.gl.Texture.Format;

/**
 * Utility methods for Caustic and rendering.
 */
public final class CausticUtil {
    private static boolean debug = true;
    private static final Logger logger = Logger.getLogger("Caustic");
    public static final Vector4f WHITE = new Vector4f(1, 1, 1, 1);
    public static final Vector4f LIGHT_GRAY = new Vector4f(192 / 255f, 192 / 255f, 192 / 255f, 1);
    public static final Vector4f GRAY = new Vector4f(128 / 255f, 128 / 255f, 128 / 255f, 1);
    public static final Vector4f DARK_GRAY = new Vector4f(64 / 255f, 64 / 255f, 64 / 255f, 1);
    public static final Vector4f BLACK = new Vector4f(0, 0, 0, 1);
    public static final Vector4f RED = new Vector4f(1, 0, 0, 1);
    public static final Vector4f PINK = new Vector4f(1, 175 / 255f, 175 / 255f, 1);
    public static final Vector4f ORANGE = new Vector4f(1, 200 / 255f, 0, 1);
    public static final Vector4f YELLOW = new Vector4f(1, 1, 0, 1);
    public static final Vector4f GREEN = new Vector4f(0, 1, 0, 1);
    public static final Vector4f MAGENTA = new Vector4f(1, 0, 1, 1);
    public static final Vector4f CYAN = new Vector4f(0, 1, 1, 1);
    public static final Vector4f BLUE = new Vector4f(0, 0, 1, 1);

    private CausticUtil() {
    }

    /**
     * Sets the caustic renderer in debug mode.
     *
     * @param enabled If debug should be enabled
     */
    public static void setDebugEnabled(boolean enabled) {
        debug = enabled;
    }

    /**
     * Returns true if debug mode is enabled, false if otherwise.
     *
     * @return Whether or not debug mode is enabled.
     */
    public static boolean isDebugEnabled() {
        return debug;
    }

    /**
     * Returns the logger used by the Caustic classes.
     *
     * @return The Caustic logger
     */
    public static Logger getCausticLogger() {
        return logger;
    }

    /**
     * Checks if two OpenGL versioned object have compatible version. Throws an exception if that's not the case. A version is determined to be compatible with another is it's lower than the said
     * version. This isn't always true when deprecation is involved, but it's an acceptable way of doing this in most implementations.
     *
     * @param required The required version
     * @param object The object to check the version of
     * @throws IllegalStateException If the object versions are not compatible
     */
    public static void checkVersion(GLVersioned required, GLVersioned object) {
        if (!debug) {
            return;
        }
        final GLVersion requiredVersion = required.getGLVersion();
        final GLVersion objectVersion = object.getGLVersion();
        if (objectVersion.getMajor() > requiredVersion.getMajor() && objectVersion.getMinor() > requiredVersion.getMinor()) {
            throw new IllegalStateException("Versions not compatible: expected " + requiredVersion + " or lower, got " + objectVersion);
        }
    }

    /**
     * Gets the {@link java.io.InputStream}'s data as a {@link java.nio.ByteBuffer}. The image data reading is done according to the {@link com.flowpowered.caustic.api.gl.Texture.Format}. The image size is
     * stored in the passed {@link Rectangle} instance. The returned buffer is flipped an ready for reading.
     *
     * @param source The image input stream to extract the data from
     * @param format The format of the image data
     * @param size The rectangle to store the size in
     * @return The flipped buffer containing the decoded image data
     */
    public static ByteBuffer getImageData(InputStream source, Format format, Rectangle size) {
        try {
            final BufferedImage image = ImageIO.read(source);
            size.setSize(image.getWidth(), image.getHeight());
            return getImageData(image, format);
        } catch (IOException ex) {
            throw new IllegalStateException("Unreadable texture image data", ex);
        }
    }

    /**
     * Gets the {@link java.awt.image.BufferedImage}'s data as a {@link java.nio.ByteBuffer}. The image data reading is done according to the {@link com.flowpowered.caustic.api.gl.Texture.Format}. The
     * returned buffer is flipped an ready for reading.
     *
     * @param image The image to extract the data from
     * @param format The format of the image data
     * @return The flipped buffer containing the decoded image data
     */
    public static ByteBuffer getImageData(BufferedImage image, Format format) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        final int type = image.getType();
        final int[] pixels;
        if (type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB) {
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        } else {
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        }
        return getImageData(pixels, format, width, height);
    }

    /**
     * Sets the texture's image data. The image data reading is done according to the given {@link com.flowpowered.caustic.api.gl.Texture.Format}. The returned buffer is flipped an ready for reading.
     *
     * @param pixels The image pixels
     * @param width The width of the image
     * @param height the height of the image
     * @return The flipped buffer containing the decoded image data
     */
    public static ByteBuffer getImageData(int[] pixels, Format format, int width, int height) {
        final ByteBuffer data = CausticUtil.createByteBuffer(width * height * format.getComponentCount());
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                final int pixel = pixels[x + y * width];
                if (format.hasRed()) {
                    data.put((byte) (pixel >> 16 & 0xff));
                }
                if (format.hasGreen()) {
                    data.put((byte) (pixel >> 8 & 0xff));
                }
                if (format.hasBlue()) {
                    data.put((byte) (pixel & 0xff));
                }
                if (format.hasAlpha()) {
                    data.put((byte) (pixel >> 24 & 0xff));
                }
            }
        }
        data.flip();
        return data;
    }

    /**
     * Converts a byte buffer of image data to a flat integer array integer where each pixel is and integer in the ARGB format. Input data is expected to be 8 bits per component. If the format has no
     * alpha, 0xFF is written as the value.
     *
     * @param imageData The source image data
     * @param format The format of the image data, 8 bits per component
     * @param size The size of the image
     * @return The packed pixel array
     */
    public static int[] getPackedPixels(ByteBuffer imageData, Format format, Rectangle size) {
        final int[] pixels = new int[size.getArea()];
        final int width = size.getWidth();
        final int height = size.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int srcIndex = (x + y * width) * format.getComponentCount();
                final int destIndex = x + (height - y - 1) * width;
                if (format.hasRed()) {
                    pixels[destIndex] |= (imageData.get(srcIndex) & 0xff) << 16;
                }
                if (format.hasGreen()) {
                    pixels[destIndex] |= (imageData.get(srcIndex + 1) & 0xff) << 8;
                }
                if (format.hasBlue()) {
                    pixels[destIndex] |= imageData.get(srcIndex + 2) & 0xff;
                }
                if (format.hasAlpha()) {
                    pixels[destIndex] |= (imageData.get(srcIndex + 3) & 0xff) << 24;
                } else {
                    pixels[destIndex] |= 0xff000000;
                }
            }
        }
        return pixels;
    }

    /**
     * Converts a byte buffer of image data to a buffered image in the ARGB format. Input data is expected to be 8 bits per component. If the format has no alpha, 0xFF is written as the value.
     *
     * @param imageData The source image data
     * @param format The format of the image data, 8 bits per component
     * @param size The size of the image
     * @return The buffered image
     */
    public static BufferedImage getImage(ByteBuffer imageData, Format format, Rectangle size) {
        final int width = size.getWidth();
        final int height = size.getHeight();
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int srcIndex = (x + y * width) * format.getComponentCount();
                final int destIndex = x + (height - y - 1) * width;
                if (format.hasRed()) {
                    pixels[destIndex] |= (imageData.get(srcIndex) & 0xff) << 16;
                }
                if (format.hasGreen()) {
                    pixels[destIndex] |= (imageData.get(srcIndex + 1) & 0xff) << 8;
                }
                if (format.hasBlue()) {
                    pixels[destIndex] |= imageData.get(srcIndex + 2) & 0xff;
                }
                if (format.hasAlpha()) {
                    pixels[destIndex] |= (imageData.get(srcIndex + 3) & 0xff) << 24;
                } else {
                    pixels[destIndex] |= 0xff000000;
                }
            }
        }
        return image;
    }

    /**
     * Decodes a normalized float color from a packed ARGB int in the 0xAARRGGBB format.
     *
     * @param packed The packed color data
     * @return The color as a 4 float vector
     */
    public static Vector4f fromPackedARGB(int packed) {
        return fromIntRGBA(packed >> 16, packed >> 8, packed, packed >> 24);
    }

    /**
     * Converts 4 byte color components to a normalized float color.
     *
     * @param r The red component
     * @param b The blue component
     * @param g The green component
     * @param a The alpha component
     * @return The color as a 4 float vector
     */
    public static Vector4f fromIntRGBA(int r, int g, int b, int a) {
        return new Vector4f((r & 0xff) / 255f, (g & 0xff) / 255f, (b & 0xff) / 255f, (a & 0xff) / 255f);
    }

    /**
     * Converts an AWT {@link java.awt.Color} into a normalized float color.
     *
     * @param color The AWT color to convert
     * @return The color as a 4 float vector
     */
    public static Vector4f fromAWTColor(Color color) {
        return new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    /**
     * Converts the components of a color, as specified by the HSB model, to an equivalent set of values for the default RGB model. The <code>saturation</code> and <code>brightness</code> components
     * should be floating-point values between zero and one (numbers in the range [0, 1]). The <code>hue</code> component can be any floating-point number. Alpha will be 1.
     *
     * @param hue The hue component of the color
     * @param saturation The saturation of the color
     * @param brightness The brightness of the color
     * @return The color as a 4 float vector
     */
    public static Vector4f fromHSB(float hue, float saturation, float brightness) {
        float r = 0;
        float g = 0;
        float b = 0;
        if (saturation == 0) {
            r = g = b = brightness;
        } else {
            final float h = (hue - GenericMath.floor(hue)) * 6;
            final float f = h - GenericMath.floor(h);
            final float p = brightness * (1 - saturation);
            final float q = brightness * (1 - saturation * f);
            final float t = brightness * (1 - saturation * (1 - f));
            switch ((int) h) {
                case 0:
                    r = brightness;
                    g = t;
                    b = p;
                    break;
                case 1:
                    r = q;
                    g = brightness;
                    b = p;
                    break;
                case 2:
                    r = p;
                    g = brightness;
                    b = t;
                    break;
                case 3:
                    r = p;
                    g = q;
                    b = brightness;
                    break;
                case 4:
                    r = t;
                    g = p;
                    b = brightness;
                    break;
                case 5:
                    r = brightness;
                    g = p;
                    b = q;
                    break;
            }
        }
        return new Vector4f(r, g, b, 1);
    }

    /**
     * Converts a {@link com.flowpowered.math.vector.Vector4f} to a {@link java.awt.Color}.
     *
     * @param c The Caustic color to convert
     * @return The AWT color
     */
    public static Color toAWTColor(Vector4f c) {
        return new Color(c.getX(), c.getY(), c.getZ(), c.getW());
    }

    /**
     * Creates a byte buffer of the desired capacity.
     *
     * @param capacity The capacity
     * @return The byte buffer
     */
    public static ByteBuffer createByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity * DataType.BYTE.getByteSize()).order(ByteOrder.nativeOrder());
    }

    /**
     * Creates a short buffer of the desired capacity.
     *
     * @param capacity The capacity
     * @return The short buffer
     */
    public static ShortBuffer createShortBuffer(int capacity) {
        return createByteBuffer(capacity * DataType.SHORT.getByteSize()).asShortBuffer();
    }

    /**
     * Creates an int buffer of the desired capacity.
     *
     * @param capacity The capacity
     * @return The int buffer
     */
    public static IntBuffer createIntBuffer(int capacity) {
        return createByteBuffer(capacity * DataType.INT.getByteSize()).asIntBuffer();
    }

    /**
     * Creates a double buffer of the desired capacity.
     *
     * @param capacity The capacity
     * @return The double buffer
     */
    public static FloatBuffer createFloatBuffer(int capacity) {
        return createByteBuffer(capacity * DataType.FLOAT.getByteSize()).asFloatBuffer();
    }

    /**
     * Creates a float buffer of the desired capacity.
     *
     * @param capacity The capacity
     * @return The float buffer
     */
    public static DoubleBuffer createDoubleBuffer(int capacity) {
        return createByteBuffer(capacity * DataType.DOUBLE.getByteSize()).asDoubleBuffer();
    }
}
