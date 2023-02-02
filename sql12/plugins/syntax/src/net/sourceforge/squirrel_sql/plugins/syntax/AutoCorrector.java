package net.sourceforge.squirrel_sql.plugins.syntax;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AutoCorrector
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AutoCorrector.class);


   private JTextComponent _txtComp;
   private SyntaxPlugin _plugin;

   private int _autocorrectionsCount = 0;
   private static final String PREFS_KEY_AUTO_COORECTIONS_COUNT = "squirrelSql_syntax_autocorrections_count";


   public AutoCorrector(JTextComponent txtComp, SyntaxPlugin plugin)
   {
      _txtComp = txtComp;
      _plugin = plugin;
      _txtComp.getDocument().addDocumentListener(new DocumentListener()
      {
         public void changedUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }

         public void insertUpdate(DocumentEvent e)
         {
            onInsertUpdate(e);
         }

         public void removeUpdate(DocumentEvent e)
         {
            //To change body of implemented methods use File | Settings | File Templates.
         }
      });
      _autocorrectionsCount = Props.getInt(PREFS_KEY_AUTO_COORECTIONS_COUNT, 0);

   }

   private void onInsertUpdate(DocumentEvent e)
   {
      try
      {
         if(1 != e.getLength())
         {
            return;
         }

         final String insertChar = e.getDocument().getText(e.getOffset(), 1);

         if (' ' == insertChar.charAt(0))
         {
            String autoCorrCandidate = getStringBeforeWhiteSpaceOrOpeningBraket(e.getOffset()).toUpperCase();
            final String corr = _plugin.getAutoCorrectProviderImpl().getAutoCorrects().get(autoCorrCandidate);
            if(null != corr)
            {

               if(10 > _autocorrectionsCount)
               {
						String[] params = new String[]{autoCorrCandidate, corr};
                  _plugin.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("syntax.hasBeenAutocorr", params));
                  Props.putInt(PREFS_KEY_AUTO_COORECTIONS_COUNT, ++_autocorrectionsCount);
               }

               _txtComp.setSelectionStart(e.getOffset() - autoCorrCandidate.length());
               _txtComp.setSelectionEnd(e.getOffset());
               SwingUtilities.invokeLater(() -> _txtComp.replaceSelection(corr + insertChar));
            }
         }
      }
      catch (BadLocationException ex)
      {
         throw new RuntimeException(ex);

      }
   }

   private String getStringBeforeWhiteSpaceOrOpeningBraket(int offset)
   {
      try
      {
         String text = _txtComp.getDocument().getText(0, offset);


         String ret = null;
         int begPos = text.length();
         for(int i=text.length()-1; 0 <= i; --i)
         {
            if(Character.isWhitespace(text.charAt(i)) || '(' == text.charAt(i))
            {
               break;
            }
            --begPos;
         }

         ret = text.substring(begPos, text.length());

         return ret;

      }
      catch (BadLocationException e)
      {
         throw new RuntimeException(e);
      }
   }

}
