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
package org.spout.renderer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.spout.renderer.GLVersioned.GLVersion;
import org.spout.renderer.gl.GLFactory;

/**
 * A manager for the implementations of the various OpenGL and OpenGLES versions.
 */
public final class GLImplementation {
	private static final Logger logger = Logger.getLogger("GLImplementation.logger");
	private static final Map<GLVersion, GLFactory> implementations = new EnumMap<>(GLVersion.class);

	private GLImplementation() {
	}

	static {
		final ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(new BasicFormatter());
		logger.setUseParentHandlers(false);
		logger.addHandler(consoleHandler);
		loadDefaults();
	}

	/**
	 * Registers an implementation for a version. Only one implementation per version is allowed. Any previous one is replaced.
	 *
	 * @param glVersion The GL version of the implementation
	 * @param glFactory The factory for the implementation
	 */
	public static void register(GLVersion glVersion, GLFactory glFactory) {
		implementations.put(glVersion, glFactory);
	}

	/**
	 * Returns a {@link GLFactory} for the {@link GLVersion}, or null if none has been registered.
	 *
	 * @param glVersion The GL version to look up an implementation for
	 * @return The implementation, as a {@link GLFactory}
	 */
	public static GLFactory get(GLVersion glVersion) {
		return implementations.get(glVersion);
	}

	public static Set<GLVersion> getAvailableVersions() {
		return Collections.unmodifiableSet(implementations.keySet());
	}

	private static void loadDefaults() {
		tryLoadClass(GLVersion.GL20, "lwjgl.gl20.GL20GLFactory");
		tryLoadClass(GLVersion.GL30, "lwjgl.gl30.GL30GLFactory");
		tryLoadClass(GLVersion.GLES20, "android.gles20.GLES20GLFactory");
		//tryLoadClass(GLVersion.GLES30, "android.gles30.GLES30GLFactory");
	}

	private static void tryLoadClass(GLVersion version, String localPkg) {
		try {
			Class.forName("org.spout.renderer." + localPkg);
		} catch (ClassNotFoundException ex) {
			logger.log(Level.WARNING, "Default " + version + " implementation \"" + localPkg + "\" not found");
			return;
		}
		logger.log(Level.INFO, "Loaded default " + version + " implementation \"" + localPkg + "\"");
	}

	private static class BasicFormatter extends Formatter {
		private static final String LINE_SEPARATOR = System.getProperty("line.separator");
		private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE hh:mm:ss");

		@Override
		public String format(LogRecord record) {
			final StringBuilder sb = new StringBuilder()
					.append('[').append(DATE_FORMAT.format(new Date(record.getMillis()))).append(']')
					.append('[').append(record.getLevel().getLocalizedName()).append(']')
					.append(' ')
					.append(record.getMessage())
					.append(LINE_SEPARATOR);
			final Throwable throwable = record.getThrown();
			if (throwable != null) {
				final StringWriter stringWriter = new StringWriter();
				throwable.printStackTrace(new PrintWriter(stringWriter, true));
				sb.append(stringWriter.toString());
			}
			return sb.toString();
		}
	}
}
