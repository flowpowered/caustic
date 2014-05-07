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
package org.spout.renderer.api.util;

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

import gnu.trove.list.TFloatList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TFloatArrayList;

import org.spout.renderer.api.GLVersioned;
import org.spout.renderer.api.GLVersioned.GLVersion;
import org.spout.renderer.api.data.VertexAttribute.DataType;
import org.spout.renderer.api.gl.Texture.Format;

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
     * Gets the {@link java.io.InputStream}'s data as a {@link java.nio.ByteBuffer}. The image data reading is done according to the {@link org.spout.renderer.api.gl.Texture.Format}. The image size is
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
     * Gets the {@link java.awt.image.BufferedImage}'s data as a {@link java.nio.ByteBuffer}. The image data reading is done according to the {@link org.spout.renderer.api.gl.Texture.Format}. The
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
     * Sets the texture's image data. The image data reading is done according to the given {@link org.spout.renderer.api.gl.Texture.Format}. The returned buffer is flipped an ready for reading.
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

    /**
     * Generate the normals for the positions, according to the indices. This assumes that the positions have 3 components, in the x, y, z order. The normals are stored as a 3 component vector, in the
     * x, y, z order.
     *
     * @param positions The position components
     * @param indices The indices
     * @return The normals
     */
    public static TFloatList generateNormals(TFloatList positions, TIntList indices) {
        final TFloatList normals = new TFloatArrayList();
        generateNormals(positions, indices, normals);
        return normals;
    }

    /**
     * Generate the normals for the positions, according to the indices. This assumes that the positions have 3 components, in the x, y, z order. The normals are stored as a 3 component vector, in the
     * x, y, z order.
     *
     * @param positions The position components
     * @param indices The indices
     * @param normals The list in which to store the normals
     */
    public static void generateNormals(TFloatList positions, TIntList indices, TFloatList normals) {
        // Initialize normals to (0, 0, 0)
        normals.fill(0, positions.size(), 0);
        // Iterate over the entire mesh
        for (int i = 0; i < indices.size(); i += 3) {
            // Triangle position indices
            final int pos0 = indices.get(i) * 3;
            final int pos1 = indices.get(i + 1) * 3;
            final int pos2 = indices.get(i + 2) * 3;
            // First triangle vertex position
            final float x0 = positions.get(pos0);
            final float y0 = positions.get(pos0 + 1);
            final float z0 = positions.get(pos0 + 2);
            // Second triangle vertex position
            final float x1 = positions.get(pos1);
            final float y1 = positions.get(pos1 + 1);
            final float z1 = positions.get(pos1 + 2);
            // Third triangle vertex position
            final float x2 = positions.get(pos2);
            final float y2 = positions.get(pos2 + 1);
            final float z2 = positions.get(pos2 + 2);
            // First edge position difference
            final float x10 = x1 - x0;
            final float y10 = y1 - y0;
            final float z10 = z1 - z0;
            // Second edge position difference
            final float x20 = x2 - x0;
            final float y20 = y2 - y0;
            final float z20 = z2 - z0;
            // Cross both edges to obtain the normal
            final float nx = y10 * z20 - z10 * y20;
            final float ny = z10 * x20 - x10 * z20;
            final float nz = x10 * y20 - y10 * x20;
            // Add the normal to the first vertex
            normals.set(pos0, normals.get(pos0) + nx);
            normals.set(pos0 + 1, normals.get(pos0 + 1) + ny);
            normals.set(pos0 + 2, normals.get(pos0 + 2) + nz);
            // Add the normal to the second vertex
            normals.set(pos1, normals.get(pos1) + nx);
            normals.set(pos1 + 1, normals.get(pos1 + 1) + ny);
            normals.set(pos1 + 2, normals.get(pos1 + 2) + nz);
            // Add the normal to the third vertex
            normals.set(pos2, normals.get(pos2) + nx);
            normals.set(pos2 + 1, normals.get(pos2 + 1) + ny);
            normals.set(pos2 + 2, normals.get(pos2 + 2) + nz);
        }
        // Iterate over all the normals
        for (int i = 0; i < indices.size(); i++) {
            // Index for the normal
            final int nor = indices.get(i) * 3;
            // Get the normal
            float nx = normals.get(nor);
            float ny = normals.get(nor + 1);
            float nz = normals.get(nor + 2);
            // Length of the normal
            final float l = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            // Normalize the normal
            nx /= l;
            ny /= l;
            nz /= l;
            // Update the normal
            normals.set(nor, nx);
            normals.set(nor + 1, ny);
            normals.set(nor + 2, nz);
        }
    }

    /**
     * Generate the tangents for the positions, normals and texture coords, according to the indices. This assumes that the positions and normals have 3 components, in the x, y, z order, and that the
     * texture coords have 2, in the u, v (or s, t) order. The tangents are stored as a 4 component vector, in the x, y, z, w order. The w component represents the handedness for the bi-tangent
     * computation, which must be computed with B = T_w * (N x T).
     *
     * @param positions The position components
     * @param normals The normal components
     * @param textures The texture coord components
     * @param indices The indices
     * @return The tangents
     */
    public static TFloatList generateTangents(TFloatList positions, TFloatList normals, TFloatList textures, TIntList indices) {
        final TFloatList tangents = new TFloatArrayList();
        generateTangents(positions, normals, textures, indices, tangents);
        return tangents;
    }

    /**
     * Generate the tangents for the positions, normals and texture coords, according to the indices. This assumes that the positions and normals have 3 components, in the x, y, z order, and that the
     * texture coords have 2, in the u, v (or s, t) order. The tangents are stored as a 4 component vector, in the x, y, z, w order. The w component represents the handedness for the bi-tangent
     * computation, which must be computed with B = T_w * (N x T).
     *
     * @param positions The position components
     * @param normals The normal components
     * @param textures The texture coord components
     * @param indices The indices
     * @param tangents The list in which to store the tangents
     */
    public static void generateTangents(TFloatList positions, TFloatList normals, TFloatList textures, TIntList indices, TFloatList tangents) {
        // Adapted from: http://www.terathon.com/code/tangent.html
        // Size of the tangent list (without the handedness value)
        final int size = normals.size();
        // Initialize all tangents to (0, 0, 0, 0)
        tangents.fill(0, size / 3 * 4, 0);
        // Storage for the derivatives in respect to u and v
        final float[] du = new float[size];
        final float[] dv = new float[size];
        // Iterate over the entire mesh
        for (int i = 0; i < indices.size(); i += 3) {
            // Triangle position indices
            final int pos0 = indices.get(i) * 3;
            final int pos1 = indices.get(i + 1) * 3;
            final int pos2 = indices.get(i + 2) * 3;
            // First triangle vertex position
            final float x0 = positions.get(pos0);
            final float y0 = positions.get(pos0 + 1);
            final float z0 = positions.get(pos0 + 2);
            // Second triangle vertex position
            final float x1 = positions.get(pos1);
            final float y1 = positions.get(pos1 + 1);
            final float z1 = positions.get(pos1 + 2);
            // Third triangle vertex position
            final float x2 = positions.get(pos2);
            final float y2 = positions.get(pos2 + 1);
            final float z2 = positions.get(pos2 + 2);
            // Triangle texture coord indices
            final int tex0 = indices.get(i) * 2;
            final int tex1 = indices.get(i + 1) * 2;
            final int tex2 = indices.get(i + 2) * 2;
            // First triangle vertex texture coord
            final float u0 = textures.get(tex0);
            final float v0 = textures.get(tex0 + 1);
            // Second triangle vertex texture coord
            final float u1 = textures.get(tex1);
            final float v1 = textures.get(tex1 + 1);
            // Third triangle vertex texture coord
            final float u2 = textures.get(tex2);
            final float v2 = textures.get(tex2 + 1);
            // First edge position difference
            final float x10 = x1 - x0;
            final float y10 = y1 - y0;
            final float z10 = z1 - z0;
            // Second edge position difference
            final float x20 = x2 - x0;
            final float y20 = y2 - y0;
            final float z20 = z2 - z0;
            // First edge texture coord difference
            final float u10 = u1 - u0;
            final float v10 = v1 - v0;
            // Second edge texture coord difference
            final float u20 = u2 - u0;
            final float v20 = v2 - v0;
            //  Coefficient for derivative calculation
            float r = 1 / (u10 * v20 - u20 * v10);
            // Derivative in respect to U
            final float dux = (v20 * x10 - v10 * x20) * r;
            final float duy = (v20 * y10 - v10 * y20) * r;
            final float duz = (v20 * z10 - v10 * z20) * r;
            // Derivative in respect to V
            final float dvx = (u10 * x20 - u20 * x10) * r;
            final float dvy = (u10 * y20 - u20 * y10) * r;
            final float dvz = (u10 * z20 - u20 * z10) * r;
            // Add the derivative in respect to U to the first vertex
            du[pos0] += dux;
            du[pos0 + 1] += duy;
            du[pos0 + 2] += duz;
            // Add the derivative in respect to U to the second vertex
            du[pos1] += dux;
            du[pos1 + 1] += duy;
            du[pos1 + 2] += duz;
            // Add the derivative in respect to U to the third vertex
            du[pos2] += dux;
            du[pos2 + 1] += duy;
            du[pos2 + 2] += duz;
            // Add the derivative in respect to V to the first vertex
            dv[pos0] += dvx;
            dv[pos0 + 1] += dvy;
            dv[pos0 + 2] += dvz;
            // Add the derivative in respect to V to the second vertex
            dv[pos1] += dvx;
            dv[pos1 + 1] += dvy;
            dv[pos1 + 2] += dvz;
            // Add the derivative in respect to V to the first vertex
            dv[pos2] += dvx;
            dv[pos2 + 1] += dvy;
            dv[pos2 + 2] += dvz;
        }
        // Iterate over all the tangents
        for (int i = 0; i < indices.size(); i++) {
            // Index for the normal
            final int nor = indices.get(i) * 3;
            // Get the normal
            final float nx = normals.get(nor);
            final float ny = normals.get(nor + 1);
            final float nz = normals.get(nor + 2);
            // Get the derivative in respect to U
            final float dux = du[nor];
            final float duy = du[nor + 1];
            final float duz = du[nor + 2];
            // Get the derivative in respect to V
            final float dvx = dv[nor];
            final float dvy = dv[nor + 1];
            final float dvz = dv[nor + 2];
            // Dot the normal and derivative in respect to U
            final float d = nx * dux + ny * duy + nz * duz;
            // Calculate the tangent using Gram-Schmidt
            float tx = (dux - nx * d);
            float ty = (duy - ny * d);
            float tz = (duz - nz * d);
            // Length of the tangent
            final float l = (float) Math.sqrt(tx * tx + ty * ty + tz * tz);
            // Normalize the tangent
            tx /= l;
            ty /= l;
            tz /= l;
            // Index for the tangent
            final int tan = indices.get(i) * 4;
            // Set the tangent coordinates
            tangents.set(tan, tx);
            tangents.set(tan + 1, ty);
            tangents.set(tan + 2, tz);
            // Cross the normal and the derivative in respect to U
            final float cx = ny * duz - nz * duy;
            final float cy = nz * dux - nx * duz;
            final float cz = nx * duy - ny * dux;
            // Dot this cross product with the derivative in respect to V
            final float d2 = cx * dvx + cy * dvy + cz * dvz;
            // Determine the handedness
            final float h = d2 < 0 ? -1 : 1;
            // Set the handedness value
            tangents.set(tan + 3, h);
        }
    }
}
