package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.awt.event.ActionEvent;
import java.util.StringTokenizer;

import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.gui.action.MakeEditableCommand;
/**
 * A popup menu useful for a editable text area.
 * 
 * @author		Gerd Wagner
 */
public class SessionTextEditPopupMenu extends TextPopupMenu
{
	/** Current session. */
	private final ISession _session;

	private MakeEditableAction _makeEditable = new MakeEditableAction();
//	private InQuotesAction _inQuotes = new InQuotesAction();
//	private InSbAppendAction _inSbAppend = new InSbAppendAction();
//	private RemoveQuotesAction _removeQuotes = new RemoveQuotesAction();
//	private ReformatCodeAction _reformatCode = new ReformatCodeAction();

	// The following pointer is needed to allow the "Make Editable button
	// to tell the application to set up an editable display panel
	private IDataSetUpdateableModel _updateableModel = null;

	/**
	 * Ctor specifying session.
	 * 
	 * @param	session		Current session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public SessionTextEditPopupMenu(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;
		addMenuEntries(false);
	}

	/**
	 * Constructor used when caller wants to be able to make data editable.
	 * We need both parameters because there is at least one case where the
	 * underlying data model is updateable, but we do not want to allow the
	 * user to enter editing mode because they are already in edit mode.
	 * While that case only applys to the TablePopupMenu, we use the same interface
	 * for both table and text for consistancy.
	 * The caller needs to determine whether or not to allow a request for edit mode.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public SessionTextEditPopupMenu(ISession session, boolean allowEditing,
									IDataSetUpdateableModel updateableModel)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;

		// save the pointer needed to enable editing of data on-demand
		_updateableModel = updateableModel;

		addMenuEntries(allowEditing);
	}

	private void addMenuEntries(boolean allowEditing)
	{
		if (allowEditing)
		{
			addSeparator();
			add(_makeEditable);
			addSeparator();
		}
//		add(_inQuotes);
//		add(_inSbAppend);
//		add(_removeQuotes);
	}

	private void performeRemoveQuotesAction()             
	{                                                     
		final JTextComponent comp = getTextComponent();
		String textToUnquote = comp.getSelectedText();   
		boolean isSelection = true;                       
		if(null == textToUnquote)                         
		{                                                 
			textToUnquote = comp.getText();              
			isSelection = false;                          
		}                                                 
		if(null == textToUnquote)                         
		{                                                 
			return;                                       
		}                                                 
                                                      
		String unquotedText = unquoteText(textToUnquote); 
                                                      
		if(isSelection)                                   
		{                                                 
			comp.replaceSelection(unquotedText);         
		}                                                 
		else                                              
		{                                                 
			comp.setText(unquotedText);                  
		}                                                 
	}                                                     

//	private String quoteText(String textToQuote, boolean sbAppend)                                             
//	{                                                                                                          
//		if(null == textToQuote)                                                                                
//		{                                                                                                      
//			throw new IllegalArgumentException("textToQuote can not be null");                                 
//		}                                                                                                      
//                                                                                                           
//		String[] lines = textToQuote.split("\n");                                                              
//                                                                                                           
//		StringBuffer ret = new StringBuffer();                                                                 
//                                                                                                           
//		if(sbAppend)                                                                                           
//		{                                                                                                      
//			ret.append("sb.append(\"").append(   trimRight(lines[0].replaceAll("\"", "\\\\\""))   );           
//		}                                                                                                      
//		else                                                                                                   
//		{                                                                                                      
//			ret.append("\"").append(   trimRight(lines[0].replaceAll("\"", "\\\\\""))   );                     
//		}                                                                                                      
//                                                                                                           
//		for(int i=1; i < lines.length; ++i)                                                                    
//		{                                                                                                      
//			if(sbAppend)                                                                                       
//			{                                                                                                  
//				ret.append(" \"); \nsb.append(\"").append(  trimRight(lines[i].replaceAll("\"", "\\\\\""))  ); 
//			}                                                                                                  
//			else                                                                                               
//			{                                                                                                  
//				ret.append(" \" +\n\"").append(   trimRight(lines[i].replaceAll("\"", "\\\\\""))   );          
//			}                                                                                                  
//		}                                                                                                      
//                                                                                                           
//		if(sbAppend)                                                                                           
//		{                                                                                                      
//			ret.append(" \");");                                                                               
//		}                                                                                                      
//		else                                                                                                   
//		{                                                                                                      
//			ret.append(" \";");                                                                                
//		}                                                                                                      
//                                                                                                           
//		return ret.toString();                                                                                 
//	}                                                                                                          

	/**
	 * textToUnquote is seen as a tokens separated by quotes. All tokens
	 * that contain a new line character are left out.
	 *
	 * @param	textToUnquote	Text to be unquoted.
	 * 
	 * @return	The unquoted text.
	 */
	private String unquoteText(String textToUnquote)                                               
	{                                                                                              
		// new line to the begining so that sb.append( will be removed                             
		// new line to the end so that a semi colon at the end will be removed.                    
		textToUnquote = "\n" + textToUnquote + "\n";                                               
                                                                                               
		StringTokenizer st = new StringTokenizer(textToUnquote, "\"");                             
                                                                                               
		StringBuffer ret = new StringBuffer();                                                     
		while(st.hasMoreTokens())                                                                  
		{                                                                                          
			String token = st.nextToken();                                                         
			String trimmedToken = token;                                                           
			if(0 != token.trim().length() && -1 == token.indexOf('\n'))                            
			{                                                                                      
				if(trimmedToken.endsWith("\\n"))                                                   
				{                                                                                  
					// Some people put new line characters in their SQL to have nice debug output. 
					// Remove these new line characters too.                                       
					trimmedToken = trimmedToken.substring(0, trimmedToken.length() - 2);           
				}                                                                                  
                                                                                               
				if(trimmedToken.endsWith("\\"))                                                    
				{                                                                                  
					ret.append(trimmedToken.substring(0, trimmedToken.length() - 1)).append("\""); 
				}                                                                                  
				else                                                                               
				{                                                                                  
					ret.append(trimmedToken).append("\n");                                         
				}                                                                                  
			}                                                                                      
		}                                                                                          
		if(ret.toString().endsWith("\n"))                                                          
		{                                                                                          
			ret.setLength(ret.length() - 1);                                                       
		}                                                                                          
		return ret.toString();                                                                     
	}                                                                                              

//	private void performeInSbAppendAction()                                                            
//	{                                                                                                  
//		final JTextComponent comp = getTextComponent();
//		String textToQuote = comp.getSelectedText();                                                  
//		boolean isSelection = true;                                                                    
//		if(null == textToQuote)                                                                        
//		{                                                                                              
//			textToQuote = comp.getText();                                                             
//			isSelection = false;                                                                       
//		}                                                                                              
//		if(null == textToQuote)                                                                        
//		{                                                                                              
//			return;                                                                                    
//		}                                                                                              
//                                                                                                   
//		String quotedText = quoteText(textToQuote, true);                                              
//                                                                                                   
//		if(isSelection)                                                                                
//		{                                                                                              
//			comp.replaceSelection(quotedText);                                                        
//		}                                                                                              
//		else                                                                                           
//		{                                                                                              
//			comp.setText(quotedText);                                                                 
//		}                                                                                              
//	}                                                                                                  

//	private void performeInQuotesAction()                  
//	{                                                      
//		final JTextComponent comp = getTextComponent();
//		String textToQuote = comp.getSelectedText();      
//		boolean isSelection = true;                        
//		if(null == textToQuote)                            
//		{                                                  
//			textToQuote = comp.getText();                 
//			isSelection = false;                           
//		}                                                  
//		if(null == textToQuote)                            
//		{                                                  
//			return;                                        
//		}                                                  
//                                                       
//		String quotedText = quoteText(textToQuote, false); 
//                                                       
//		if(isSelection)                                    
//		{                                                  
//			comp.replaceSelection(quotedText);            
//		}                                                  
//		else                                               
//		{                                                  
//			comp.setText(quotedText);                     
//		}                                                  
//	}                                                      

//	private void preformReformatoCode()
//	{
//		String textToReformat = _comp.getSelectedText();
//		boolean isSelection = true;
//		if(null == textToReformat)
//		{
//			textToReformat = _comp.getText();
//			isSelection = false;
//		}
//		if(null == textToReformat)
//		{
//			return;
//		}
//
//		CommentSpec[] commentSpecs =
//			new CommentSpec[]
//			{
//				new CommentSpec("/*", "*/"),
//				new CommentSpec("--", "\n")
//			};
//
//		///////////////////////////////////////////////////////////////////////////////////////////
//		// TODO in this code the Session should be made available
//		// TODO and this should be replaced by session.getProperties().getSQLStatementSeparator();
//		String statementSep =  _session.getProperties().getSQLStatementSeparator();
//		//
//		///////////////////////////////////////////////////////////////////////////////////////////
//
//		CodeReformator cr = new CodeReformator(statementSep, commentSpecs);
//
//		String reformatedText = cr.reformat(textToReformat);
//
//		if(isSelection)
//		{
//			_comp.replaceSelection(reformatedText);
//		}
//		else
//		{
//			_comp.setText(reformatedText);
//		}
//	}

	private String trimRight(String toTrim)                   
	{                                                         
		if( 0 >= toTrim.length())                             
		{                                                     
			return toTrim;                                    
		}                                                     
                                                          
		int i;                                                
		for(i=toTrim.length(); i > 0; --i)                    
		{                                                     
			if( !Character.isWhitespace(toTrim.charAt(i-1)) ) 
			{                                                 
				break;                                        
			}                                                 
		}                                                     
                                                          
		return toTrim.substring(0, i);                        
	}                                                         

	protected class MakeEditableAction extends BaseAction
	{
		MakeEditableAction()
		{
			super("Make Editable");
		}

		public void actionPerformed(ActionEvent evt)
		{
			if (_updateableModel != null)
			{
				new MakeEditableCommand(_updateableModel).execute();
			}
		}
	}

//	private class InQuotesAction extends BaseAction
//	{
//		InQuotesAction()
//		{
//			super("In quotes");
//		}
//
//		public void actionPerformed(ActionEvent evt)
//		{
//			performeInQuotesAction();
//		}
//	}

//	private class InSbAppendAction extends BaseAction
//	{
//		InSbAppendAction()
//		{
//			super("In sb.append()");
//		}
//
//		public void actionPerformed(ActionEvent evt)
//		{
//			performeInSbAppendAction();
//		}
//	}
//
//	private class RemoveQuotesAction extends BaseAction
//	{
//		RemoveQuotesAction()
//		{
//			super("Remove quotes");
//		}
//
//		public void actionPerformed(ActionEvent evt)
//		{
//			performeRemoveQuotesAction();
//		}
//	}

//	private class ReformatCodeAction extends BaseAction
//	{
//		ReformatCodeAction()
//		{
//			super("Reformat code");
//		}
//
//		public void actionPerformed(ActionEvent evt)
//		{
//			preformReformatoCode();
//		}
//	}

}
