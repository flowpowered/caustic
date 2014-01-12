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
package org.spout.renderer.api;

/**
 * Represents a resource that can be created and destroyed.
 */
public abstract class Creatable {
    //private static final AtomicInteger createdCount = new AtomicInteger(0);
    //private static final AtomicInteger destroyedCount = new AtomicInteger(0);
    private boolean created = false;

    /**
     * Creates the resources. It can now be used.
     */
    public void create() {
        created = true;
        //final int count = createdCount.incrementAndGet();
        //System.out.println(count - destroyedCount.get());
    }

    /**
     * Releases the resource. It can not longer be used.
     */
    public void destroy() {
        created = false;
        //final int count = destroyedCount.incrementAndGet();
        //System.out.println(createdCount.get() - count);
    }

    /**
     * Returns true if the resource was created and is ready for use, false if otherwise.
     *
     * @return Whether or not the resource has been created
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * Throws an exception is the resource hasn't been created yet.
     *
     * @throws IllegalStateException if the resource hasn't been created
     */
    public void checkCreated() {
        if (!isCreated()) {
            throw new IllegalStateException("Resource has not been created yet");
        }
    }
}
