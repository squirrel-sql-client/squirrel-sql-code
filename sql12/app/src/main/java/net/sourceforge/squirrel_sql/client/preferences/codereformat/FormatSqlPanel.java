package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import net.sourceforge.squirrel_sql.client.util.codereformat.PieceMarkerSpec;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;

public class FormatSqlPanel extends JPanel
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(FormatSqlPanel.class);


   public static enum KeywordBehaviour
   {
      ALONE_IN_LINE(1, s_stringMgr.getString("codereformat.aloneInLine"), PieceMarkerSpec.TYPE_PIECE_MARKER_IN_OWN_PIECE),
      START_NEW_LINE(2, s_stringMgr.getString("codereformat.startNewLine"), PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN),
      NO_INFLUENCE_ON_NEW_LINE(3, s_stringMgr.getString("codereformat.noInfluenceOnNewLine"), null);

      private String _title;
      private Integer _pieceMarkerSpecType;
      private int _id;

      KeywordBehaviour(int id, String title, Integer pieceMarkerSpecType)
      {
         _id = id;
         _title = title;
         _pieceMarkerSpecType = pieceMarkerSpecType;
      }

      @Override
      public String toString()
      {
         return _title;
      }


      public int getID()
      {
         return _id;
      }

      public static KeywordBehaviour forId(int id)
      {
         for (KeywordBehaviour keywordBehaviour : values())
         {
            if(id == keywordBehaviour.getID())
            {
               return keywordBehaviour;
            }
         }
         throw new IllegalArgumentException("Invalid ID: " + id);
      }

      public Integer getPieceMarkerSpecType()
      {
         return _pieceMarkerSpecType;
      }
   }


   JFormattedTextField txtIndentCount;
   JFormattedTextField txtPreferedLineLength;
   ArrayList<KeywordBehaviourPrefCtrl> keywordBehaviourPrefCtrls = new ArrayList<KeywordBehaviourPrefCtrl>();
   JCheckBox chkDoInsertValuesAlign;

   JTextArea txtExampleSqls = new JTextArea();


   public FormatSqlPanel(KeywordBehaviourPref[] keywordBehaviourPrefs)
   {
      setLayout(new BorderLayout());
      add(createControlsPanel(keywordBehaviourPrefs), BorderLayout.WEST);
      add(new JScrollPane(txtExampleSqls), BorderLayout.CENTER);
   }


   private JPanel createControlsPanel(KeywordBehaviourPref[] keywordBehaviourPrefs)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.indent")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      txtIndentCount = new JFormattedTextField(NumberFormat.getInstance());
      txtIndentCount.setColumns(7);
      ret.add(txtIndentCount, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.preferedLineLen")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      txtPreferedLineLength = new JFormattedTextField(NumberFormat.getInstance());
      txtPreferedLineLength.setColumns(7);
      ret.add(txtPreferedLineLength, gbc);

      gbc = new GridBagConstraints(0,2,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(30,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.keywordBehavior")), gbc);


      int gridy = 2;

      for (KeywordBehaviourPref keywordBehaviourPref : keywordBehaviourPrefs)
      {
         keywordBehaviourPrefCtrls.add(createKeywordBehaviourPrefCtrl(ret, keywordBehaviourPref, ++gridy));
      }

      gbc = new GridBagConstraints(1,++gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      chkDoInsertValuesAlign = new JCheckBox(s_stringMgr.getString("codereformat.FormatSqlPanel.tryAlignInsertValueStatements"));
      ret.add(chkDoInsertValuesAlign, gbc);

      return ret;
   }

   private KeywordBehaviourPrefCtrl createKeywordBehaviourPrefCtrl(JPanel toAddTo, KeywordBehaviourPref keywordBehaviourPref, int gridy)
   {
      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      toAddTo.add(new JLabel(keywordBehaviourPref.getKeyWord()), gbc);

      gbc = new GridBagConstraints(1, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      JComboBox cbo = new JComboBox();
      toAddTo.add(cbo, gbc);
      return new KeywordBehaviourPrefCtrl(cbo, keywordBehaviourPref);
   }

}
