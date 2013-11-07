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

import org.spout.math.vector.Vector2f;

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
	public Rectangle(Vector2f size) {
		this(size.getFloorX(), size.getFloorY());
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
	public Rectangle(Vector2f position, Vector2f size) {
		this(position.getFloorX(), position.getFloorY(), size.getFloorX(), size.getFloorY());
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
