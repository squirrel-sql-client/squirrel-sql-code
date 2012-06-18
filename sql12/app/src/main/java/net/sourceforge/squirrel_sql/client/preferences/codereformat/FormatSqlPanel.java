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
      ALONE_IN_LINE(1, s_stringMgr.getString("editextras.FormatSqlPanel.aloneInLine"), PieceMarkerSpec.TYPE_PIECE_MARKER_IN_OWN_PIECE),
      START_NEW_LINE(2, s_stringMgr.getString("editextras.FormatSqlPanel.startNewLine"), PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN),
      NO_INFLUENCE_ON_NEW_LINE(3, s_stringMgr.getString("editextras.FormatSqlPanel.noInfluenceOnNewLine"), null);

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
   ArrayList<KeywordBehaviourPrefCtrl> _keywordBehaviourPrefCtrls = new ArrayList<KeywordBehaviourPrefCtrl>();


   public FormatSqlPanel(KeywordBehaviourPref[] keywordBehaviourPrefs)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("editextras.FormatSqlPanel.indent")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      txtIndentCount = new JFormattedTextField(NumberFormat.getInstance());
      txtIndentCount.setColumns(7);
      add(txtIndentCount, gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("editextras.FormatSqlPanel.preferedLineLen")), gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      txtPreferedLineLength = new JFormattedTextField(NumberFormat.getInstance());
      txtPreferedLineLength.setColumns(7);
      add(txtPreferedLineLength, gbc);

      gbc = new GridBagConstraints(0,2,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(30,5,5,5),0,0);
      add(new JLabel(s_stringMgr.getString("editextras.FormatSqlPanel.keywordBehavior")), gbc);


      int gridy = 2;

      for (KeywordBehaviourPref keywordBehaviourPref : keywordBehaviourPrefs)
      {
         _keywordBehaviourPrefCtrls.add(createKeywordBehaviourPrefCtrl(keywordBehaviourPref, ++gridy));
      }
   }

   private KeywordBehaviourPrefCtrl createKeywordBehaviourPrefCtrl(KeywordBehaviourPref keywordBehaviourPref, int gridy)
   {
      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      add(new JLabel(keywordBehaviourPref.getKeyWord()), gbc);

      gbc = new GridBagConstraints(1, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      JComboBox cbo = new JComboBox();
      add(cbo, gbc);
      return new KeywordBehaviourPrefCtrl(cbo, keywordBehaviourPref);
   }

}
