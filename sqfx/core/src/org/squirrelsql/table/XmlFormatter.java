package org.squirrelsql.table;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

public class XmlFormatter
{

   private String _toFormat;
   private String _formattedXml;

   public XmlFormatter(String toFormat)
   {
      try
      {
         _toFormat = toFormat;

         if(null == _toFormat)
         {
            return;
         }


         int indent = 3;
         DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
         Document document = documentBuilder.parse(new InputSource(new StringReader(_toFormat)));

         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
         if (indent > 0)
         {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indent));
         }
         ByteArrayOutputStream bos = new ByteArrayOutputStream();

         Result result = new StreamResult(bos);
         Source source = new DOMSource(document);
         transformer.transform(source, result);

         _formattedXml = bos.toString();
      }
      catch (SAXException e)
      {
         // It's just not a xml
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean success()
   {
      return null != _formattedXml;
   }



   public String getFormattedXml()
   {
      return _formattedXml;
   }

   public static void main(String[] args) throws Exception
   {
      String xmlString = "<hello><from>ME</from></hello>";
      System.out.println(new XmlFormatter(xmlString).getFormattedXml());
   }

}