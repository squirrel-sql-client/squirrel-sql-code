package net.sourceforge.squirrel_sql.fw.util;

import java.io.Serializable;

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
/**
 * Proxy server settings.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ProxySettings implements Cloneable, Serializable, IProxySettings
{
	private static final long serialVersionUID = 6435632924688921646L;

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

	/**
	 * List of hosts (separated by a '|') that we don't use a HTTP proxy for.
	 */
	private String _httpNonProxyHosts;

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
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	public boolean getHttpUseProxy()
	{
		return _httpUseProxy;
	}

	public void setHttpUseProxy(boolean data)
	{
		_httpUseProxy = data;
	}

	public String getHttpProxyServer()
	{
		return _httpProxyServer;
	}

	public void setHttpProxyServer(String data)
	{
		_httpProxyServer = data;
	}

	public String getHttpProxyPort()
	{
		return _httpProxyPort;
	}

	public void setHttpProxyPort(String data)
	{
		_httpProxyPort = data;
	}

	public String getHttpProxyUser()
	{
		return _httpProxyUser;
	}

	public void setHttpProxyUser(String data)
	{
		_httpProxyUser = data;
	}

	public String getHttpProxyPassword()
	{
		return _httpProxyPassword;
	}

	public void setHttpProxyPassword(String data)
	{
		_httpProxyPassword = data;
	}

	public String getHttpNonProxyHosts()
	{
		return _httpNonProxyHosts;
	}

	public void setHttpNonProxyHosts(String data)
	{
		_httpNonProxyHosts = data;
	}

	public boolean getSocksUseProxy()
	{
		return _socksUseProxy;
	}

	public void setSocksUseProxy(boolean data)
	{
		_socksUseProxy = data;
	}

	public String getSocksProxyServer()
	{
		return _socksProxyServer;
	}

	public void setSocksProxyServer(String data)
	{
		_socksProxyServer = data;
	}

	public String getSocksProxyPort()
	{
		return _socksProxyPort;
	}

	public void setSocksProxyPort(String data)
	{
		_socksProxyPort = data;
	}
}
