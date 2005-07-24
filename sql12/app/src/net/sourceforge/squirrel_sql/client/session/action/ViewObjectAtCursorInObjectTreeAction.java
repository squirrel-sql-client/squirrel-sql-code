package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;


public class ViewObjectAtCursorInObjectTreeAction extends SquirrelAction
   implements ISQLPanelAction
{

   /**
    * Current panel.
    */
   private ISQLPanelAPI _panel;


   /**
    * Ctor specifying Application API.
    *
    * @param	app	Application API.
    */
   public ViewObjectAtCursorInObjectTreeAction(IApplication app)
   {
      super(app);
   }

   public void setSQLPanel(ISQLPanelAPI panel)
   {
      _panel = panel;
      setEnabled(null != _panel && _panel.isInMainSessionWindow());
   }

   /**
    * View the Object at cursor in the Object Tree
    *
    * @param	evt		Event being executed.
    */
   public synchronized void actionPerformed(ActionEvent evt)
   {
      if (_panel == null)
      {
         return;
      }

      ObjectCandidates candidates = getObjectCandidates();

      IObjectTreeAPI objectTree = _panel.getSession().getObjectTreeAPIOfActiveSessionWindow();

      boolean success = false;
      while (candidates.hasNext())
      {
         String[] catSchemObj = candidates.next();
         success = objectTree.selectInObjectTree(catSchemObj[0], catSchemObj[1], catSchemObj[2]);
         if (success)
         {
            _panel.getSession().selectMainTab(ISession.IMainPanelTabIndexes.OBJECT_TREE_TAB);
            break;
         }

      }

      if (false == success)
      {
         String msg = "Could not locate the database object '" + candidates.getSearchString() + "' in Object tree";
         JOptionPane.showMessageDialog(_panel.getSession().getApplication().getMainFrame(), msg);
      }

   }

   private ObjectCandidates getObjectCandidates()
   {
      String stringAtCursor = getStringAtCursor();

      ObjectCandidates ret = new ObjectCandidates(stringAtCursor);

      String[] splits = stringAtCursor.split("\\.");


      for (int i = splits.length-1; i >=0 ; i--)
      {
         String object = null;
         String schema = null;
         String catalog = null;

         object = splits[i];

         if (i+1 < splits.length)
         {
            schema = splits[i+1];
         }

         if (i+2 < splits.length)
         {
            catalog = splits[i+2];
         }

         ret.add(catalog, schema, object);
      }

      return ret;
   }

   private String getStringAtCursor()
   {
      String text = _panel.getSQLEntryPanel().getText();
      int caretPos = _panel.getSQLEntryPanel().getCaretPosition();

      int lastIndexOfText = Math.max(0,text.length()-1);
      int beginPos = Math.min(caretPos, lastIndexOfText); // The Math.min is for the Caret at the end of the text
      while(0 < beginPos && false == isParseStop(text.charAt(beginPos), false))
      {
         --beginPos;
      }

      int endPos = caretPos;
      while(endPos < text.length() && false == isParseStop(text.charAt(endPos), true))
      {
         ++endPos;
      }

      return text.substring(beginPos, endPos).trim();


   }

   private boolean isParseStop(char c, boolean treatDotAsStop)
   {
      return
         '(' == c ||
         ')' == c ||
         '\'' == c ||
         Character.isWhitespace(c) ||
         (treatDotAsStop && '.' == c);

   }

   private static class ObjectCandidates
   {
      ArrayList _candidates = new ArrayList();

      int _curIndex = 0;
      private String _searchString;

      public ObjectCandidates(String searchString)
      {
         _searchString = searchString;
      }


      public boolean hasNext()
      {
         return _curIndex < _candidates.size();
      }

      public String[] next()
      {
         ArrayList candidate =(ArrayList) _candidates.get(_curIndex++);
         return (String[]) candidate.toArray(new String[candidate.size()]);
      }

      public String getSearchString()
      {
         return _searchString;
      }

      public void add(String catalog, String schema, String object)
      {
         ArrayList candidate = new ArrayList(3);
         candidate.add(catalog);
         candidate.add(schema);
         candidate.add(object);
         _candidates.add(candidate);
      }
   }

}
