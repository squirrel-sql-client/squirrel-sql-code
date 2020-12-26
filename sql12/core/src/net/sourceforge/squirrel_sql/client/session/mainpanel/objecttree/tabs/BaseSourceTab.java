package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

/*
 * Copyright (C) 2002 Colin Bell
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseSourceTab extends BaseObjectTab
{
   /**
    * Internationalized strings for this class.
    */
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(BaseSourceTab.class);

   /**
    * Logger for this class.
    */
   private final static ILogger s_log = LoggerController.createLogger(BaseSourceTab.class);

   /**
    * Hint to display for tab.
    */
   private final String _hint;

   /**
    * Title of the tab
    */
   private String _title;

   /**
    * Component to display in tab.
    */
   private BaseSourcePanel _comp;

   /**
    * Scrolling pane for <TT>_comp.
    */
   private JScrollPane _scroller;

   public BaseSourceTab(String hint)
   {
      this(null, hint);
   }

   public BaseSourceTab(String title, String hint)
   {
      if (title != null)
      {
         _title = title;
      }
      else
      {
         // i18n[BaseSourceTab.title=Source]
         _title = s_stringMgr.getString("BaseSourceTab.title");
      }

      _hint = hint != null ? hint : _title;
   }

   /**
    * Return the title for the tab.
    *
    * @return The title for the tab.
    */
   public String getTitle()
   {
      return _title;
   }

   /**
    * Return the hint for the tab.
    *
    * @return The hint for the tab.
    */
   public String getHint()
   {
      return _hint;
   }

   public void clear()
   {
   }

   public Component getComponent()
   {


      if (_scroller == null)
      {
         if (_comp == null)
         {
            _comp = createSourcePanel();
         }
         _scroller = new JScrollPane(_comp);
         LineNumber lineNumber = new LineNumber(_comp);
         _scroller.setRowHeaderView(lineNumber);
         _scroller.getVerticalScrollBar().setUnitIncrement(10);
      }
      return _scroller;
   }

   /**
    * Subclasses can use this to override the default behavior provided by the DefaultSourcePanel, with a
    * subclass of BaseSourcePanel.
    *
    * @param panel
    * @deprecated Use {@link #createSourcePanel()} as callback method.
    */
   public void setSourcePanel(BaseSourcePanel panel)
   {
      _comp = panel;
   }

   protected void refreshComponent()
   {
      ISession session = getSession();
      if (session == null)
      {
         throw new IllegalStateException("Null ISession");
      }

      if (_comp == null)
      {
         _comp = createSourcePanel();
      }

      try
      {
         PreparedStatement pstmt = createStatement();
         try
         {
            _comp.load(getSession(), pstmt);
         }
         finally
         {
            SQLUtilities.closeStatement(pstmt);
         }
      }
      catch (SQLException ex)
      {
         s_log.error(ex);
         session.showErrorMessage(ex);
      }
   }

   /**
    * Create a instance of {@link BaseSourcePanel}.
    * Per default, a {@link DefaultSourcePanel} is used.
    * Subclasses can use this to override the default behavior provided by the DefaultSourcePanel, with a
    * subclass of BaseSourcePanel.
    *
    * @return The source panel to use.
    */
   protected BaseSourcePanel createSourcePanel()
   {
      return new BaseSourcePanel(getSession())
      {
         @Override
         public void load(ISession session, PreparedStatement stmt)
         {
            onLoad(getTextArea(), getSourceCode(session, stmt));
         }
      };
   }

   /**
    * Sub-classes should override this method to return a PreparedStatement which will yield the source code
    * of the object returned by getDatabaseObjectInfo.
    *
    * OR
    *
    * Sub-classes should simply override {@link #getSourceCode(ISession, PreparedStatement)} and ignore the PreparedStatement parameter.
    *
    * @return a PreparedStatement already with bound variables, ready to be executed
    * @throws SQLException if any error occurs.
    */
   protected PreparedStatement createStatement() throws SQLException
   {
      return null;
   }


   private void onLoad(JTextComponent textArea, String sourceCode)
   {
      textArea.setText(sourceCode);
      textArea.setCaretPosition(0);
   }

   /**
    * Overriding this method and ignoring the PreparedStatement is the simplest way to provide source code.
    */
   protected String getSourceCode(ISession session, PreparedStatement stmt)
   {
      if(null == stmt)
      {
         String msg =
               "BaseSourceTab.createStatement() must be overridden to return non null when this base method is supposed to be used. " +
               "You may as well simply override this method and return the source code any way you want";
         throw new IllegalStateException(msg);
      }


      StringBuilder buf = new StringBuilder(4096);
      try(ResultSet rs = stmt.executeQuery())
      {
         while (rs.next())
         {
            buf.append(rs.getString(1));
         }
      }
      catch (SQLException ex)
      {
         session.showErrorMessage(ex);
      }
      return buf.toString();
   }
}
