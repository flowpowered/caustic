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

import org.spout.renderer.Model;

/**
 * Represents an instance of another model. Model instancing can be used to reduce the amount of vertex data on the GPU, by reusing the same geometry for multiple models. To use this class, simply
 * construct a new instance using the model to instance (the main model), create it, and add it to the renderer. This class should work with any model type as long as it has been implemented
 * correctly. Note that the vertex array and material are shared amongst the main model and all of its instances. Any changes to these will be reflected across all models.
 */
public class InstancedModel extends Model {
	/**
	 * Constructs a new instanced model from the main model.
	 *
	 * @param main The main model
	 */
	public InstancedModel(Model main) {
		super(main);
	}
}
