package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2004 Gerd Wagner
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
 
 import net.sourceforge.squirrel_sql.fw.util.StringManager;
 import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

 import javax.swing.JOptionPane;
 import java.util.ArrayList;

/**
 * This is an XML prettyprinter.  It takes a string that is in XML format
 * and converts it into a multi-line, indented structure
 * that is easier for the user to read.
 * Prior to calling this method, nothing in Squirrel is "XML-aware",
 * so we do not prevent the user from calling this method if the
 * data is not an XML string.
 * However, we do warn the user if the data is not XML or is improperly formatted.
 */
public class XmlRefomatter
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(XmlRefomatter.class);


	// i18n[xmlRefomatter.unexpectedProblem=Unexpected problem during formatting.]
	private static String DEFAULT_MESSAGE = s_stringMgr.getString("xmlRefomatter.unexpectedProblem");
	private static String _message = DEFAULT_MESSAGE;
   private static boolean _showWarningMessages = true;

   public static String reformatXml(String xml)
	{
		// do a simple check to see if the string might contain XML or not
		if (xml.indexOf("<") == -1 || xml.equals("<null>")) {
			// no tags, so cannot be XML
			JOptionPane.showMessageDialog(null,
				// i18n[xmlRefomatter.noXml=The data does not contain any XML tags.  No reformatting done.]
				s_stringMgr.getString("xmlRefomatter.noXml"),
				// i18n[xmlRefomatter.xmlWarning=XML Warning]
				s_stringMgr.getString("xmlRefomatter.xmlWarning"), JOptionPane.WARNING_MESSAGE);
			return xml;
		}

		try
		{
			StringBuffer ret = new StringBuffer();
			int depth = 0;
			ParseRes parseRes = getParseRes(xml, 0);

			if (parseRes == null)
         {
				// the parse did not find XML, or it was mal-formed
            showWarning(_message);
				return xml;
			}

			xml = xml.trim();

			// GWG XML format check code
			ArrayList<String> tagList = new ArrayList<String>();	

			while(null != parseRes)
			{

				if(ParseRes.BEGIN_TAG == parseRes.type)
				{
					tagList.add(parseRes.item);	// GWG XML format check code

					ret.append(getIndent(depth)).append(parseRes.item);
					ParseRes nextRes = getParseRes(xml, parseRes.pos);

					// see if there was a problem during parsing
					if (nextRes == null) {
						// the parse did not find XML, or it was mal-formed
					    showWarning(_message);
						return xml;
					}

					if(ParseRes.TEXT != nextRes.type)
					{
						ret.append("\n");
					}

					++depth;
				}
				else if(ParseRes.END_TAG == parseRes.type)
				{
					// GWG format check code follows...
					if (tagList.size()> 0 ) {
						String startTag = tagList.remove(tagList.size()-1);
						// Assume that all start tags are "<...>" or include a space
						// after the tag name (e.g. as in "<SOMETAG args>" and all
						// end tags are "</...>".  Remove the syntactic markers,
						// then remove any spaces, and convert to upper case for comparison
						String testableStartTag = startTag.substring(1, startTag.length() -1).trim().toUpperCase();
						if (testableStartTag.indexOf(' ') > -1)
							testableStartTag = testableStartTag.substring(0, testableStartTag.indexOf(' '));
						String endTag = parseRes.item.substring(2, parseRes.item.length()-1).trim().toUpperCase();

						if ( ! testableStartTag.equals(endTag))
                  {
							Object[] args = new Object[]{startTag, parseRes.item};

							// i18n[xmlRefomatter.malformedXml=Possible mal-formed XML:\n   Starting tag was: {0}\nEnding Tag was: {1}\nContinuing with reformatting XML."]
							String msg = s_stringMgr.getString("xmlRefomatter.malformedXml", args);
							showWarning(msg);
						}
					}
					// End GWG format check code

					--depth;
					if(ret.toString().endsWith("\n"))
					{
						ret.append(getIndent(depth));
					}
					ret.append(parseRes.item).append("\n");
				}
				else if(ParseRes.CLOSED_TAG == parseRes.type)
				{
					ret.append(getIndent(depth)).append(parseRes.item).append("\n");
				}
				else if(ParseRes.TEXT == parseRes.type)
				{
					ret.append(parseRes.item);
				}
				parseRes = getParseRes(xml, parseRes.pos);
			}

			return ret.toString();
		}
		catch(Exception e)
		{
			// the parse did not find XML, or it was mal-formed
			JOptionPane.showMessageDialog(null,
				DEFAULT_MESSAGE,
				// i18n[xmlReformatter.xmlWarning2=XML Warning]
				s_stringMgr.getString("xmlReformatter.xmlWarning2"), JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
		}
      finally
      {
         _message = DEFAULT_MESSAGE;
         _showWarningMessages = true;
      }
		return xml;

	}


   private static void showWarning(String message)
   {
      if(false == _showWarningMessages)
      {
         return;
      }

      Object[] options =
			{
				// i18n[xmlReformatter.yes=YES]
				s_stringMgr.getString("xmlReformatter.yes"),
				// i18n[xmlReformatter.no=NO]
				s_stringMgr.getString("xmlReformatter.no")
			};


		int ret = JOptionPane.showOptionDialog(null,
		    		 // i18n[xmlReformatter.seeOtherErrs={0}\nDo you wish to see other errors?"]
					s_stringMgr.getString("xmlReformatter.seeOtherErrs", message),
					 // i18n[xmlReformatter.xmlWarning5=XML Warning]
                s_stringMgr.getString("xmlReformatter.xmlWarning5"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);


      if(0 != ret)
      {
         _showWarningMessages = false;
      }

   }


	private static ParseRes getParseRes(String xml, int pos)
	{
		if(pos >= xml.length())
		{
			return null;
		}

		pos = moveOverWhiteSpaces(xml, pos);

		int ltIndex = xml.indexOf("<", pos);
		int gtIndex = xml.indexOf(">", pos);

		ParseRes ret = new ParseRes();

		if(pos == ltIndex)
		{
			ret.item = xml.substring(ltIndex, gtIndex+1);

			if(xml.length() > ltIndex+1 && xml.charAt(ltIndex+1) == '/')
			{
				ret.type = ParseRes.END_TAG;
			}
			else if(pos < gtIndex-1 && xml.charAt(gtIndex-1) == '/')
			{
				ret.type = ParseRes.CLOSED_TAG;
			}
			else
			{
				ret.type = ParseRes.BEGIN_TAG;
			}
			ret.pos = gtIndex+1;
		}
		else
		{
			//			check for "malformed" XML, or text that happens to contain
			//			a "<" with no corresponding ">"
			if (ltIndex == -1) {
				int lengthToPrint = xml.length() - pos;
				if (lengthToPrint > 40)
					lengthToPrint = 40;

				// i18n[xmlReformatter.malformedXmlAt=Malformed XML.  No ending tag seen for text starting at:\n{0}]
				_message = s_stringMgr.getString("xmlReformatter.malformedXmlAt", xml.substring(pos, pos + lengthToPrint));
				return null;
			}		

			ret.type = ParseRes.TEXT;
			ret.item = xml.substring(pos, ltIndex);
			ret.pos = ltIndex;
		}

		return ret;
	}

	private static int moveOverWhiteSpaces(String xml, int pos)
	{
		int ret = pos;

		while(Character.isWhitespace(xml.charAt(ret)))
		{
			++ret;
		}
		return ret;

	}

	private static String getIndent(int depth)
	{
		StringBuffer ret = new StringBuffer("");
		for(int i=0; i < depth; ++i)
		{
			ret.append("   ");
		}
		return ret.toString();
	}

	static class ParseRes
	{
		public static final int BEGIN_TAG = 0;
		public static final int END_TAG = 1;
		public static final int CLOSED_TAG = 2;
		public static final int TEXT = 3;

		String item;
		int type;
		int pos;
	}
}
