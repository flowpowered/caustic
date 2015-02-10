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

import com.flowpowered.math.vector.Vector2i;

/**
 * A simple mutable rectangle object, positioned in 2D space.
 */
public class Rectangle {
    private int x, y;
    private int width, height;

    /**
     * Constructs a new rectangle at (0, 0) of width and height 0.
     */
    public Rectangle() {
        this(0, 0);
    }

    /**
     * Constructs a new rectangle at (0, 0) of the desired width and height.
     *
     * @param size The (width, height) vector
     */
    public Rectangle(Vector2i size) {
        this(size.getX(), size.getY());
    }

    /**
     * Constructs a new rectangle at (0, 0) of the desired width and height.
     *
     * @param width The desired width
     * @param height The desired height
     */
    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    /**
     * Constructs a new rectangle of the desired dimension at the desired location.
     *
     * @param position The (x, y) vector
     * @param size The (width, height) vector
     */
    public Rectangle(Vector2i position, Vector2i size) {
        this(position.getX(), position.getY(), size.getX(), size.getY());
    }

    /**
     * Constructs a new rectangle of the desired dimension at the desired location.
     *
     * @param x The x coordinate of the position
     * @param y The y coordinate of the position
     * @param width The width
     * @param height The height
     */
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the x position component.
     *
     * @return The x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y position component.
     *
     * @return The y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the position.
     *
     * @return The position
     */
    public Vector2i getPosition() {
        return new Vector2i(x, y);
    }

    /**
     * Returns the width.
     *
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height.
     *
     * @return The height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the dimensions.
     *
     * @return The size
     */
    public Vector2i getSize() {
        return new Vector2i(width, height);
    }

    /**
     * Sets the x position component.
     *
     * @param x The x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y position component.
     *
     * @param y The y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the width.
     *
     * @param width The width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the height.
     *
     * @param height The height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the position coordinates.
     *
     * @param position The position
     */
    public void setPosition(Vector2i position) {
        setPosition(position.getX(), position.getY());
    }

    /**
     * Sets the position coordinates.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }

    /**
     * Sets the dimensions.
     *
     * @param size The size
     */
    public void setSize(Vector2i size) {
        setSize(size.getX(), size.getY());
    }

    /**
     * Sets the dimensions.
     *
     * @param width The width
     * @param height The height
     */
    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    /**
     * Sets all the values.
     *
     * @param position The position
     * @param size The size
     */
    public void set(Vector2i position, Vector2i size) {
        set(position.getX(), position.getY(), size.getX(), size.getY());
    }

    /**
     * Sets all the values.
     *
     * @param x The x coordinate of the position
     * @param y The y coordinate of the position
     * @param width The width
     * @param height The height
     */
    public void set(int x, int y, int width, int height) {
        setPosition(x, y);
        setSize(width, height);
    }

    /**
     * Sets the rectangle to be an exact copy of the provided one.
     *
     * @param rectangle The rectangle to copy
     */
    public void set(Rectangle rectangle) {
        set(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    /**
     * Returns the area of the rectangle.
     *
     * @return The rectangle area
     */
    public int getArea() {
        return width * height;
    }
}
