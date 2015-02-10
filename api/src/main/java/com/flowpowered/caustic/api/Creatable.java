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
package com.flowpowered.caustic.api;

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
     * Throws an exception if the resource hasn't been created yet.
     *
     * @throws IllegalStateException if the resource hasn't been created
     */
    public void checkCreated() {
        if (!isCreated()) {
            throw new IllegalStateException("Resource has not been created yet");
        }
    }

    /**
     * Throws an exception if the resource has been created already.
     *
     * @throws IllegalStateException if the resource has been created
     */
    public void checkNotCreated() {
        if (isCreated()) {
            throw new IllegalStateException("Resource has been created already");
        }
    }
}
