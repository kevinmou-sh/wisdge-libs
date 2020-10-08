package com.wisdge.commons.resources;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class ServletContextBundleControl extends ResourceBundle.Control {
	protected final ServletContext context;

	public ServletContextBundleControl(ServletContext context) {
		this.context = context;
	}
	
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {

		final String path = toBundleName(baseName, locale) + ".properties";
		final PrivilegedResourceLoad privileged = new PrivilegedResourceLoad(path);

		try (InputStream stream = privileged.perform()) {
			return stream != null ? new PropertyResourceBundle(stream) : null;
		}

	}

	public class PrivilegedResourceLoad implements PrivilegedExceptionAction<InputStream> {
		protected final String resource;
		
		public PrivilegedResourceLoad(String resource) {
			this.resource = resource;
		}

		@Override
		public InputStream run() throws IOException {
			return context.getResourceAsStream(resource);
		}

		public InputStream perform() throws IOException {
			try {
				return AccessController.doPrivileged(this);
			} catch (PrivilegedActionException error) {
				throw (IOException) error.getCause();
			}
		}
	}

	@Override
	public long getTimeToLive(String baseName, Locale locale) {
		return TTL_DONT_CACHE;
	}

}