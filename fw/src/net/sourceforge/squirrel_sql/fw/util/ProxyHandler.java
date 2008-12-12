package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2002-2003 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This class will apply the settings from a <TT>ProxySettings</TT>
 * object to a <TT>Properties</TT> object.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ProxyHandler
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ProxyHandler.class);

	public ProxyHandler()
	{
		super();
	}

	public void apply(IProxySettings proxy)
	{
		apply(proxy, System.getProperties());
	}

	public void apply(IProxySettings proxy, Properties props)
	{
		if (proxy == null)
		{
			throw new IllegalArgumentException("ProxySettings == null");
		}

		final boolean http = proxy.getHttpUseProxy();
		if (http)
		{
			applySetting(props, "proxySet", "true");
			applySetting(props, "http.proxyHost", proxy.getHttpProxyServer());
			applySetting(props, "http.proxyPort", proxy.getHttpProxyPort());
			applySetting(props, "http.nonProxyHosts", proxy.getHttpNonProxyHosts());
			final String user = proxy.getHttpProxyUser();
			String password = proxy.getHttpProxyPassword();
			if (password == null)
			{
				password = "";
			}
			if (user != null && user.length() > 0)
			{
				s_log.debug("Using HTTP proxy with security");
				Authenticator.setDefault(new MyAuthenticator(user, password));
			}
			else
			{
				s_log.debug("Using HTTP proxy without security");
				Authenticator.setDefault(null);
			}
		}
		else
		{
			s_log.debug("Not using HTTP proxy");
			props.remove("proxySet");
			props.remove("http.proxyHost");
			props.remove("http.proxyPort");
			props.remove("http.nonProxyHosts");
			Authenticator.setDefault(null);
		}

		final boolean socks = proxy.getSocksUseProxy();
		if (socks)
		{
			applySetting(props, "socksProxyHost", proxy.getSocksProxyServer());
			applySetting(props, "socksProxyPort", proxy.getSocksProxyPort());
		}
		else
		{
			props.remove("socksProxyHost");
			props.remove("socksProxyPort");
		}
	}

	private void applySetting(Properties props, String key, String value)
	{
		if (value != null && value.length() > 0)
		{
			props.put(key, value);
		}
		else
		{
			props.remove(key);
		}
	}

	private final static class MyAuthenticator extends Authenticator
	{
		private final PasswordAuthentication _password;

		public MyAuthenticator(String user, String password)
		{
			super();
			_password = new PasswordAuthentication(user, password.toCharArray());

		}

		protected PasswordAuthentication getPasswordAuthentication()
		{
			return _password;
		}
	}
}
