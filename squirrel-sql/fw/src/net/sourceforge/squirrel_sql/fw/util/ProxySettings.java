package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2001 Colin Bell
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
/**
 * Proxy server settings.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ProxySettings implements Cloneable
{
	/** If <TT>true</TT> use a HTTP proxy server. */
	private boolean _httpUseProxy;

	/** Name of HTTP Proxy server. */
	private String _httpProxyServer;

	/** Port for HTTP Proxy server. */
	private String _httpProxyPort;

	/** User name for HTTP Proxy server. */
	private String _httpProxyUser;

	/** Password for HTTP Proxy server. */
	private String _httpProxyPassword;

	/** If <TT>true</TT> use a SOCKS proxy server. */
	private boolean _socksUseProxy;

	/** Name of SOCKS Proxy server. */
	private String _socksProxyServer;

	/** Port for SOCKS Proxy server. */
	private String _socksProxyPort;

	/**
	 * Return a copy of this object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage());   // Impossible.
		}
	}

	public boolean getHttpUseProxy()
	{
		return _httpUseProxy;
	}

	public synchronized void setHttpUseProxy(boolean data)
	{
		_httpUseProxy = data;
	}

	public String getHttpProxyServer()
	{
		return _httpProxyServer;
	}

	public synchronized void setHttpProxyServer(String data)
	{
		final String oldValue = _httpProxyServer;
		_httpProxyServer = data;
	}

	public String getHttpProxyPort()
	{
		return _httpProxyPort;
	}

	public synchronized void setHttpProxyPort(String data)
	{
		final String oldValue = _httpProxyPort;
		_httpProxyPort = data;
	}

	public String getHttpProxyUser()
	{
		return _httpProxyUser;
	}

	public synchronized void setHttpProxyUser(String data)
	{
		final String oldValue = _httpProxyUser;
		_httpProxyUser = data;
	}

	public String getHttpProxyPassword()
	{
		return _httpProxyPassword;
	}

	public synchronized void setHttpProxyPassword(String data)
	{
		final String oldValue = _httpProxyPassword;
		_httpProxyPassword = data;
	}

	public boolean getSocksUseProxy()
	{
		return _socksUseProxy;
	}

	public synchronized void setSocksUseProxy(boolean data)
	{
		final boolean oldValue = _socksUseProxy;
		_socksUseProxy = data;
	}

	public String getSocksProxyServer()
	{
		return _socksProxyServer;
	}

	public synchronized void setSocksProxyServer(String data)
	{
		final String oldValue = _socksProxyServer;
		_socksProxyServer = data;
	}

	public String getSocksProxyPort()
	{
		return _socksProxyPort;
	}

	public synchronized void setSocksProxyPort(String data)
	{
		final String oldValue = _socksProxyPort;
		_socksProxyPort = data;
	}
}
