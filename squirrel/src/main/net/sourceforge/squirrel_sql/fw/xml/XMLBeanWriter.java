package net.sourceforge.squirrel_sql.fw.xml;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.Iterator;

/*
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
*/
import net.n3.nanoxml.*;

public final class XMLBeanWriter {

    //private Document _doc;
    private IXMLElement _rootElement;

    public XMLBeanWriter() throws XMLException {
        this(null);
    }

    public XMLBeanWriter(Object bean) throws XMLException {
        super();
//      _doc = new Document(new Element(XMLConstants.ROOT_ELEMENT_NAME));
        _rootElement = new XMLElement(XMLConstants.ROOT_ELEMENT_NAME);
        if (bean != null) {
            addToRoot(bean);
        }
    }

    public void addToRoot(Iterator it) throws XMLException {
        while(it.hasNext()) {
            addToRoot(it.next());
        }
    }

    public void addToRoot(Object bean) throws XMLException {
        try {
//          _doc.getRootElement().addChild(createElement(bean, null));
            _rootElement.addChild(createElement(bean, null));
        } catch(Exception ex) {
            throw new XMLException(ex);
        }
    }

    public void save(String fileName) throws IOException, XMLException {
        save(new File(fileName));
    }

    public void save(File file) throws IOException, XMLException {
        BufferedOutputStream os = new BufferedOutputStream(
                                            new FileOutputStream(file));
        try {
//          new XMLOutputter().output(_rootElement, os);
            XMLWriter wtr = new XMLWriter(os);
            wtr.write(_rootElement, true);
        } finally {
            os.close();
        }
    }

    private IXMLElement createElement(Object bean, String name) throws XMLException {
        IXMLElement elem = null;
        BeanInfo info = null;
        try {
            info = Introspector.getBeanInfo(bean.getClass(), Object.class);
//          info = Introspector.getBeanInfo(bean.getClass(), Introspector.USE_ALL_BEANINFO);
        } catch (IntrospectionException ex) {
            throw new XMLException(ex);
        }
        if (info != null) {
            if (bean instanceof IXMLAboutToBeWritten) {
                ((IXMLAboutToBeWritten)bean).aboutToBeWritten();
            }
            PropertyDescriptor[] propDesc = info.getPropertyDescriptors();
            elem = new XMLElement(name != null ? name : XMLConstants.BEAN_ELEMENT_NAME);
//          elem.addAttribute(new Attribute(XMLConstants.CLASS_ATTRIBUTE_NAME, bean.getClass().getName()));
            elem.setAttribute(XMLConstants.CLASS_ATTRIBUTE_NAME, bean.getClass().getName());
            for( int i = 0; i < propDesc.length; ++i ) {
                processProperty(propDesc[i], bean, elem);
            }
        }
        return elem;
    }

    private void processProperty(PropertyDescriptor propDescr, Object bean,
                                IXMLElement beanElem) throws XMLException {
        final Method getter = propDescr.getReadMethod();
        if (getter != null) {
            try {
                final String propName = propDescr.getName();
                String data = null;
                Class returnType = getter.getReturnType();
                if (returnType.isArray()) {
                    Object[] props = (Object[])getter.invoke(bean, null);
                    if (props != null) {
                        IXMLElement indexElem = new XMLElement(propName);
//                      indexElem.addAttribute(new Attribute(XMLConstants.INDEXED, "true"));
                        indexElem.setAttribute(XMLConstants.INDEXED, "true");
                        beanElem.addChild(indexElem);
                        for (int i = 0; i < props.length; ++i) {
                            indexElem.addChild(createElement(props[i], XMLConstants.BEAN_ELEMENT_NAME/*propName*/));
                        }
                    }
                } else if (returnType == boolean.class || returnType == int.class
                        || returnType == short.class || returnType == long.class
                        || returnType == float.class || returnType == double.class
                        || returnType == char.class) {
                    IXMLElement propElem = new XMLElement(propName);
                    propElem.setContent("" + getter.invoke(bean, null));
                    beanElem.addChild(propElem);
                } else if (returnType == String.class) {
                    IXMLElement propElem = new XMLElement(propName);
                    propElem.setContent((String)getter.invoke(bean, null));
                    beanElem.addChild(propElem);
                } else {
                    beanElem.addChild(createElement(getter.invoke(bean, null), propName));
                }
            } catch(Exception ex) {
                throw new XMLException(ex);
            }
        }
    }
}


