package net.sourceforge.squirrel_sql.fw.xml;

/*
 * Copyright (C) 2001-2004 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class XMLBeanReader implements Iterable<Object>
{
	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(XMLBeanReader.class);

	private String[][] _fixStrings =
		new String[][] {
				{ "com.bigfoot.colbell.squirrel", "net.sourceforge.squirrel_sql.client" },
				{ "com.bigfoot.colbell.fw", "net.sourceforge.squirrel_sql.fw" },
				{ "net.sourceforge.squirrel_sql.client.mainframe.MainFrameWindowState",
						"net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrameWindowState" } };

	private ClassLoader _cl;

	private final List<Object> _beanColl = new ArrayList<Object>();

	public XMLBeanReader()
	{
		super();
	}

	public void load(FileWrapper xmlFileWrapper) throws FileNotFoundException, XMLException
	{
		load(xmlFileWrapper.getAbsolutePath());
	}

	public void load(File xmlFile) throws FileNotFoundException, XMLException
	{
		load(xmlFile, null);
	}

	public void load(FileWrapper xmlFile, ClassLoader cl) throws FileNotFoundException, XMLException
	{
		if (!xmlFile.exists())
		{
			throw new FileNotFoundException(xmlFile.getName());
		}
		load(xmlFile.getAbsolutePath(), cl);
	}

	public void load(File xmlFile, ClassLoader cl) throws FileNotFoundException, XMLException
	{
		if (!xmlFile.exists())
		{
			throw new FileNotFoundException(xmlFile.getName());
		}
		load(xmlFile.getAbsolutePath(), cl);
	}

	public void load(String xmlFileName) throws FileNotFoundException, XMLException
	{
		load(xmlFileName, null);
	}

	public synchronized void load(String xmlFileName, ClassLoader cl) throws FileNotFoundException,
		IllegalArgumentException, XMLException
	{
		if (xmlFileName == null)
		{
			throw new IllegalArgumentException("Null xmlFileName passed");
		}

		_cl = cl;
		_beanColl.clear();


		try(FileInputStream fis = new FileInputStream(xmlFileName))
		{
			load(fis, cl);
		}
		catch (IOException ex)
		{
			s_log.error("Error closing FileReader", ex);
		}
	}

	public void load(InputStream is) throws XMLException
	{
		load(is, null);
	}

	public void load(InputStream is, ClassLoader cl) throws XMLException
	{
		try
		{
			_cl = cl;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);

			// Bug 2942351 (Program doesn't launch)
			// looking at the source for StdXMLBuilder, it appears that parser.parse() could possibly return
			// null. So check for null here and skip if necessary.
			if (doc != null)
			{
				final NodeList beans = doc.getDocumentElement().getChildNodes();

				for (int i = 0; i < beans.getLength(); i++)
				{
					final Node node = beans.item(i);

					if(Node.ELEMENT_NODE != node.getNodeType())
					{
						continue;
					}

					if (isBeanElement(node))
					{
						_beanColl.add(loadBean(node));
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw new XMLException(ex);
		}
	}

	public Iterator<Object> iterator()
	{
		return _beanColl.iterator();
	}

	public <T> List<T> getBeans()
	{
		return (List<T>) _beanColl;
	}

	private Object loadBean(Node beanNode) throws XMLException
	{
		String beanClassName = null;
		try
		{
			beanClassName = getClassNameFromElement(beanNode);
			beanClassName = fixClassName(beanClassName);
			Class beanClass = null;
			if (_cl == null)
			{
				beanClass = Class.forName(beanClassName);
			}
			else
			{
				beanClass = Class.forName(beanClassName, true, _cl);
			}
			Object bean = beanClass.getDeclaredConstructor().newInstance();
			BeanInfo info = Introspector.getBeanInfo(bean.getClass(), Introspector.USE_ALL_BEANINFO);
			PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
			Map<String, PropertyDescriptor> props = new HashMap<>();
			for (int i = 0; i < propDesc.length; ++i)
			{
				props.put(propDesc[i].getName(), propDesc[i]);
			}
			final NodeList children = beanNode.getChildNodes();
			for (int i = 0; i < children.getLength(); i++)
			{
				final Node propNode = children.item(i);

				if(Node.ELEMENT_NODE != propNode.getNodeType())
				{
					continue;
				}

				final PropertyDescriptor curProp = props.get(propNode.getNodeName());
				if (curProp != null)
				{
					loadProperty(bean, curProp, propNode);
				}
			}

			return bean;
		}
		catch (Exception ex)
		{
			s_log.error("Unexpected exception while attempting to load xml bean " + ex.getMessage(), ex);
			throw new XMLException(ex);
		}
	}

	private void loadProperty(Object bean, PropertyDescriptor propDescr, Node propNode)
		throws XMLException
	{
		final Method setter = propDescr.getWriteMethod();
		if (setter != null)
		{
			final Class parmType = setter.getParameterTypes()[0];
			final Class arrayType = parmType.getComponentType();

			String value = null;

			if (0 < propNode.getChildNodes().getLength())
			{
				value = propNode.getChildNodes().item(0).getNodeValue();
			}

			if (value == null && (parmType.isPrimitive()))
			{
				s_log.warn("Parameter type was primitive (" + parmType + "), but the value was null.  "
					+ "Skipping invokation of method: " + setter.getName() + " in declaring class: "
					+ setter.getDeclaringClass());
				return;
			}

			if (isIndexedElement(propNode))
			{
				Object[] data = loadIndexedProperty(propNode);
				try
				{
					// Arrays of Strings are a special case.
					// In XMLBeanWriter method ProcessProperty an array of
					// Strings is turned into a list of StringWrapper objects
					// in the XML (presumably so that when reading them back
					// we have a class that we can call setters on). Thus,
					// when reading back an array of Strings we actually read
					// an array of StringWrappers, which gives a type mis-match
					// in the following arrayCopy. Therefore we need to convert
					// the data that is currently in the StringWrapper objects
					// into actual Strings.
					if (arrayType.getName().equals("java.lang.String"))
					{
						// convert data from StringWrappers to Strings
						Object[] stringData = new Object[data.length];
						for (int i = 0; i < data.length; i++)
							stringData[i] = ((StringWrapper) data[i]).getString();
						data = stringData;
					}

					Object obj = Array.newInstance(arrayType, data.length);
					System.arraycopy(data, 0, obj, 0, data.length);
					setter.invoke(bean, new Object[] { obj });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (isBeanElement(propNode))
			{
				Object data = loadBean(propNode);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == boolean.class)
			{
				Object data = Boolean.valueOf(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == int.class)
			{
				Object data = Integer.valueOf(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == short.class)
			{
				Object data = Short.valueOf(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == long.class)
			{
				Object data = Long.valueOf(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == float.class)
			{
				Object data = Float.valueOf(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == double.class)
			{
				Object data = Double.valueOf(value);
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else if (parmType == char.class)
			{
				Object data;
				if (value != null && value.length() > 0)
				{
					data = Character.valueOf(value.charAt(0));
				}
				else
				{
					data = Character.valueOf(' ');
				}
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
			else
			{
				Object data = value;
				try
				{
					setter.invoke(bean, new Object[] { data });
				}
				catch (Exception ex)
				{
					throw new XMLException(ex);
				}
			}
		}
	}

	private Object[] loadIndexedProperty(Node beanElement) throws XMLException
	{
		final List<Object> beans = new ArrayList<>();
		final NodeList children = beanElement.getChildNodes();

		for (int i = 0; i < children.getLength(); i++)
		{
			final Node child = children.item(i);

			if(Node.ELEMENT_NODE != child.getNodeType())
			{
				continue;
			}

			beans.add(loadBean(child));
		}
		return beans.toArray(new Object[beans.size()]);
	}

	private boolean isBeanElement(Node node)
	{
		return node.getAttributes().getNamedItem(XMLConstants.CLASS_ATTRIBUTE_NAME) != null;
	}

	private boolean isIndexedElement(Node elem)
	{
		final Node indexed = elem.getAttributes().getNamedItem(XMLConstants.INDEXED);

		if(null == indexed)
		{
			return false;
		}

		String att = indexed.getNodeValue();
		return att != null && att.equals("true");
	}

	private String getClassNameFromElement(Node elem)
	{
		return elem.getAttributes().getNamedItem(XMLConstants.CLASS_ATTRIBUTE_NAME).getNodeValue();
	}

	private String fixClassName(String className)
	{
		for (int i = 0; i < _fixStrings.length; ++i)
		{
			String from = _fixStrings[i][0];
			if (className.startsWith(from))
			{
				className = _fixStrings[i][1] + className.substring(from.length());
				break;
			}
		}
		return className;
	}
}
