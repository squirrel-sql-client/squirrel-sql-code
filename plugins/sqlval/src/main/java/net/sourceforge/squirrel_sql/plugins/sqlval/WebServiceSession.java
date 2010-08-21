package net.sourceforge.squirrel_sql.plugins.sqlval;
/*
 * Copyright (C) 2002-2003 Colin Bell and Olof Edlund
 * colbell@users.sourceforge.net
 *
 * This code is based on the example web service client code originally written
 * by Olof Edlund.
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
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import com.mimer.ws.validateSQL.SessionData;

public class WebServiceSession
{
	/** Preferences. */
	private final WebServicePreferences _prefs;

	/** Session properties. */
	private final WebServiceSessionProperties _sessionProps;

	private SessionData _sessionData;

	public WebServiceSession(WebServicePreferences prefs, WebServiceSessionProperties sessionProps)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties == null");
		}
		_prefs = prefs;
		_sessionProps = sessionProps;
	}

	public boolean isOpen()
	{
		return _sessionData != null;
	}

	/**
	 * Open a sesion to the web service.
	 */
	public void open() throws RemoteException, ServiceException
	{
		Service l_service = new Service();
		Call l_call = (Call)l_service.createCall();

		//Set the target server and name space
		l_call.setTargetEndpointAddress(IWebServiceURL.WEB_SERVICE_URL);
		l_call.setOperationName(new QName("SQL99Validator", "openSession"));

		// Supply the user name. If you use anonymous you will be logged in and
		// the pw will be ignored
		l_call.addParameter("a_userName", XMLType.XSD_STRING, ParameterMode.IN);

		// The pw. If user name is anonymous this can be anything. But it has to
		// be supplied anyway.
		l_call.addParameter("a_password", XMLType.XSD_STRING, ParameterMode.IN);

		//The name of the calling client program.
		//This is optional. If you don't want to give out this info, please enter "N/A"
		l_call.addParameter("a_callingProgram", XMLType.XSD_STRING, ParameterMode.IN);

		//And the version of the calling program.
		//This is optional. If you don't want to give out this info, please enter "N/A"
		l_call.addParameter("a_callingProgramVersion", XMLType.XSD_STRING, ParameterMode.IN);

		//The target DBMS, could be Mimer SQL Engine, Oracle, ...
		//This is optional. If you don't want to give out this info, please enter "N/A"
		l_call.addParameter("a_targetDbms", XMLType.XSD_STRING, ParameterMode.IN);

		//The version of the target DBMS
		//This is optional. If you don't want to give out this info, please enter "N/A"
		l_call.addParameter("a_targetDbmsVersion", XMLType.XSD_STRING, ParameterMode.IN);

		//The connection Technology used, could be ODBC, JDBC, ADO
		//This is optional. If you don't want to give out this info, please enter "N/A"
		l_call.addParameter("a_connectionTechnology", XMLType.XSD_STRING, ParameterMode.IN);

		//Version
		//This is optional. If you don't want to give out this info, please enter "N/A"
		l_call.addParameter("a_connectionTechnologyVersion", XMLType.XSD_STRING, ParameterMode.IN);

		//Set this to 1 if your application is interactive where the user enters queries and then runs them
		//Set it to 2 if it is non interactive, such as for instance a JDBC Bridge driver that intercepts SQL
		l_call.addParameter("a_interactive", XMLType.XSD_INT, ParameterMode.IN);

		QName l_qn = new QName(IWebServiceURL.REQUEST_URL, "SessionData");

		l_call.registerTypeMapping(SessionData.class, l_qn,
			new org.apache.axis.encoding.ser.BeanSerializerFactory(SessionData.class, l_qn),
			new org.apache.axis.encoding.ser.BeanDeserializerFactory(SessionData.class, l_qn));

		//Set the return type
		l_call.setReturnType(l_qn);

		// Open the session.
		final boolean anonLogon = _prefs.getUseAnonymousLogon();
		final boolean anonClient = _prefs.getUseAnonymousClient();
		final boolean anonDBMS = _sessionProps.getUseAnonymousDBMS();

		final Object[] parms = new Object[]
		{
			anonLogon ? "anonymous" : _prefs.getUserName(),
			anonLogon ? "N/A" : _prefs.retrievePassword(),
			anonClient ? "N/A" : _prefs.getClientName(),
			anonClient ? "N/A" : _prefs.getClientVersion(),
			anonDBMS ? "N/A" : _sessionProps.getTargetDBMSName(),
			anonDBMS ? "N/A" : _sessionProps.getTargetDBMSVersion(),
			anonDBMS ? "N/A" : _sessionProps.getConnectionTechnology(),
			anonDBMS ? "N/A" : _sessionProps.getConnectionTechnologyVersion(),
			Integer.valueOf(1)	// 1 = interactive, 0 = batch
		};
		_sessionData = (SessionData)l_call.invoke(parms);
	}

	/**
	 * Close the session.
	 */
	public void close()
	{
		_sessionData = null;
	}

	/**
	 * Return the target URL to the open session.
	 * 
	 * @return	Target URL.
	 * 
	 * @throws	IllegalStateException
	 * 			Thrown if connection has not yet been opened.
	 */
	String getTargetURL()
	{
		validateState();
		return _sessionData.getTarget();
	}

	/**
	 * Return the ID of the web service session.
	 * 
	 * @return	session ID
	 * 
	 * @throws	IllegalStateException
	 * 			Thrown if connection has not yet been opened.
	 */
	int getSessionID()
	{
		validateState();
		return _sessionData.getSessionId();
	}

	/**
	 * Return the key of the web service session.
	 * 
	 * @return	session key
	 * 
	 * @throws	IllegalStateException
	 * 			Thrown if connection has not yet been opened.
	 */
	int getSessionKey()
	{
		validateState();
		return _sessionData.getSessionKey();
	}

	private void validateState()
	{
		if (_sessionData == null)
		{
			throw new IllegalStateException("Connection to web service has not been opened");
		}
	}
}

