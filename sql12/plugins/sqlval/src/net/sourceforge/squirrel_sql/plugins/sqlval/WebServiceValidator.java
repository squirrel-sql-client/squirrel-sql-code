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

import com.mimer.ws.validateSQL.ValidatorResult;

public class WebServiceValidator
{
	/** The session that this validator is connected to. */
	private final WebServiceSession _webServiceSession;

	/** Preferences. */
	private final WebServiceSessionProperties _prefs;

	/**
	 * Ctor specifying the session.
	 * 
	 * @param	webServiceSession	The session to the web service that this
	 *								will use.
	 * @param	perfs				Preferences
	 * 
	 * @throws	IllegalArgumentException
	 *			Thrown if null WebServiceSession or null WebServicePreferences
	 * 			objects passed.
	 */
	public WebServiceValidator(WebServiceSession webServiceSession,
							WebServiceSessionProperties prefs)
	{
		super();
		if (webServiceSession == null)
		{
			throw new IllegalArgumentException("WebServiceSession == null");
		}
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_webServiceSession = webServiceSession;
		_prefs = prefs;
	}

	public ValidatorResult validate(String sql) throws ServiceException, RemoteException
	{
		Service l_service = new Service();
		Call l_call = (Call)l_service.createCall();

		//Set the target server and name space
		l_call.setTargetEndpointAddress(_webServiceSession.getTargetURL());
		l_call.setOperationName(new QName("SQL99Validator", "validateSQL"));

		//Add the parameter names and types
		//Use the session Id you got from the openSession call here
		l_call.addParameter("a_sessionId", XMLType.XSD_INT, ParameterMode.IN);

		//Use the session key you got from the openSession call here
		l_call.addParameter("a_sessionKey", XMLType.XSD_INT, ParameterMode.IN);

		//The SQL statement to be validated against the standard
		l_call.addParameter("a_sqlStatement", XMLType.XSD_STRING, ParameterMode.IN);

		//The format of the result. This must be "text" or "html".
		//Hopefully some type of XML format will be available as well
		l_call.addParameter("a_resultType", XMLType.XSD_STRING, ParameterMode.IN);

		QName l_qn = new QName(IWebServiceURL.REQUEST_URL, "ValidatorResult");

		l_call.registerTypeMapping(ValidatorResult.class, l_qn,
			new org.apache.axis.encoding.ser.BeanSerializerFactory(
				ValidatorResult.class,
				l_qn),
			new org.apache.axis.encoding.ser.BeanDeserializerFactory(
				ValidatorResult.class,
				l_qn)
			);

		//Set the return type
		l_call.setReturnType(l_qn);

		// Parameters for call.
		final Object[] parms = new Object[]
		{
			Integer.valueOf(_webServiceSession.getSessionID()),
			Integer.valueOf(_webServiceSession.getSessionKey()),
			sql, "text"
		};

		// Execute validator and return results.
		return (ValidatorResult)l_call.invoke(parms);
	}
}

