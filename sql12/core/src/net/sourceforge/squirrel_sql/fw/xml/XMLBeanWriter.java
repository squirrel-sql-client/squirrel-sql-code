package net.sourceforge.squirrel_sql.fw.xml;

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

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public final class XMLBeanWriter
{
	//private IXMLElement _rootElement;
	private Element _rootElement;
	private Document _dom;

	public XMLBeanWriter() throws XMLException
	{
		this(null);
	}

	public XMLBeanWriter(Object bean) throws XMLException
	{
		try
		{
			//_rootElement = new XMLElement(XMLConstants.ROOT_ELEMENT_NAME);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			_dom = db.newDocument();
			_rootElement = _dom.createElement("Beans");

			if (bean != null)
			{
				addToRoot(bean);
			}

			_dom.appendChild(_rootElement);
		}
		catch (ParserConfigurationException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	public void addIteratorToRoot(Iterator it) throws XMLException
	{
		while (it.hasNext())
		{
			addToRoot(it.next());
		}
	}

	public void addToRoot(Object bean) throws XMLException
	{
		try
		{
			_rootElement.appendChild(createElement(bean, null));
		}
		catch (Exception ex)
		{
			throw new XMLException(ex);
		}
	}

	/**
	 * Saves this xml bean to the specified FileWrapper
	 * 
	 * @param file the FileWrapper that wraps the file to be written to
	 * 
	 * @throws IOException if an error occurs while writing the bean to the specified FileWrapper
	 */
	public void save(FileWrapper file) throws IOException
	{
		try (FileOutputStream fileOutputStream = file.getFileOutputStream())
		{
			save(fileOutputStream);
		}
	}

	public void save(String fileName) throws IOException
	{
		try (FileOutputStream fos = new FileOutputStream(fileName))
		{
			save(fos);
		}
	}

	public void save(File file) throws IOException
	{
		try (FileOutputStream fos = new FileOutputStream(file))
		{
			save(fos);
		}
	}

	private void save(FileOutputStream fos) throws IOException
	{
		saveToOutputStream(fos);
	}

	public void saveToOutputStream(OutputStream os)
	{
		try
		{
			Transformer tr = createTransFormer();
			tr.transform(new DOMSource(_rootElement), new StreamResult(os));

			// XMLWriter wtr = new XMLWriter(bos);
			// wtr.write(_rootElement, true);
		}
		catch (Exception e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	private Transformer createTransFormer()
	{
		try
		{
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
			//tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
			//tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			return tr;
		}
		catch (TransformerConfigurationException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}

	public String getAsString()
	{
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			Transformer tr = createTransFormer();
			tr.transform(new DOMSource(_rootElement), new StreamResult(bos));

			//new XMLWriter(bos).write(_rootElement, true);

			return bos.toString(StandardCharsets.UTF_8);
		}
		catch (TransformerException e)
		{
			throw Utilities.wrapRuntime(e);
		}
	}


	private Element createElement(Object bean, String name) throws XMLException
	{
		Element elem = null;
		BeanInfo info = null;
		try
		{
			if (bean != null)
			{
				info = Introspector.getBeanInfo(bean.getClass(), Object.class);
			}
		}
		catch (IntrospectionException ex)
		{
			throw new XMLException(ex);
		}

		// elem = new XMLElement(name != null ? name : XMLConstants.BEAN_ELEMENT_NAME);
		elem = _dom.createElement(name != null ? name : XMLConstants.BEAN_ELEMENT_NAME);

		if (info != null)
		{
			if (bean instanceof IXMLAboutToBeWritten)
			{
				((IXMLAboutToBeWritten) bean).aboutToBeWritten();
			}
			PropertyDescriptor[] propDesc = info.getPropertyDescriptors();

			// elem = new XMLElement(name != null ? name : XMLConstants.BEAN_ELEMENT_NAME);
			elem = _dom.createElement(name != null ? name : XMLConstants.BEAN_ELEMENT_NAME);

			elem.setAttribute(XMLConstants.CLASS_ATTRIBUTE_NAME, bean.getClass().getName());

			for (int i = 0; i < propDesc.length; ++i)
			{
				processProperty(propDesc[i], bean, elem);
			}
		}

		return elem;
	}

	private void processProperty(PropertyDescriptor propDescr, Object bean, Element beanElem)
		throws XMLException
	{
		final Method getter = propDescr.getReadMethod();
		if (getter != null)
		{
			try
			{
				final String propName = propDescr.getName();
				Class returnType = getter.getReturnType();
				if (returnType.isArray())
				{
					final boolean isStringArray = returnType.getName().equals("[Ljava.lang.String;");
					Object[] props = (Object[]) getter.invoke(bean, (Object[]) null);
					if (props != null)
					{
						//IXMLElement indexElem = new XMLElement(propName);
						Element indexElem = _dom.createElement(propName);

						indexElem.setAttribute(XMLConstants.INDEXED, "true");
						beanElem.appendChild(indexElem);
						for (int i = 0; i < props.length; ++i)
						{
							if (isStringArray)
							{
								StringWrapper sw = new StringWrapper((String) props[i]);
								indexElem.appendChild(createElement(sw, XMLConstants.BEAN_ELEMENT_NAME));
							}
							else
							{
								indexElem.appendChild(createElement(props[i], XMLConstants.BEAN_ELEMENT_NAME));
							}
						}
					}
				}
				else if (returnType == boolean.class || returnType == int.class || returnType == short.class
					|| returnType == long.class || returnType == float.class || returnType == double.class
					|| returnType == char.class)
				{
					//IXMLElement propElem = new XMLElement(propName);
					Element propElem = _dom.createElement(propName);

					// propElem.setContent("" + getter.invoke(bean, (Object[]) null));
					propElem.appendChild(_dom.createTextNode("" + getter.invoke(bean, (Object[]) null)));

					beanElem.appendChild(propElem);
				}
				else if (returnType == String.class)
				{
					//IXMLElement propElem = new XMLElement(propName);
					Element propElem = _dom.createElement(propName);

					//propElem.setContent((String) getter.invoke(bean, (Object[]) null));
					final String getterRes = (String) getter.invoke(bean, (Object[]) null);
					if (null != getterRes)
					{
						propElem.appendChild(_dom.createTextNode(getterRes));
					}

					beanElem.appendChild(propElem);
				}
				else
				{
					beanElem.appendChild(createElement(getter.invoke(bean, (Object[]) null), propName));
				}
			}
			catch (Exception ex)
			{
				throw new XMLException(ex);
			}
		}
	}
}
