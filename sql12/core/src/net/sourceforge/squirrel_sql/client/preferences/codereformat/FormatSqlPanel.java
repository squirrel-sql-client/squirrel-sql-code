package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.client.util.codereformat.PieceMarkerSpec;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;

public class FormatSqlPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FormatSqlPanel.class);


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

   JRadioButton radUseSquirrelInternalFormatter = new JRadioButton(s_stringMgr.getString("codereformat.FormatSqlPanel.use.squirrel.formatter"));
   JRadioButton radUseVerticalBlankFormatter = new JRadioButton(s_stringMgr.getString("codereformat.FormatSqlPanel.use.vertical.blank.formatter"));


   JFormattedTextField txtIndentCount;
   JCheckBox chkIndentSections = new JCheckBox(s_stringMgr.getString("codereformat.FormatSqlPanel.indent.sections"));

   JRadioButton radCommasAtLineBeginYes;
   JRadioButton radCommasAtLineBeginNo;


   JFormattedTextField txtPreferedLineLength;
   ArrayList<KeywordBehaviourPrefCtrl> keywordBehaviourPrefCtrls = new ArrayList<KeywordBehaviourPrefCtrl>();
   JCheckBox chkDoInsertValuesAlign;

   JCheckBox _chkLineBreakFor_AND_OR_in_FROM_clause = new JCheckBox(s_stringMgr.getString("codereformat.FormatSqlPanel.checkbox.lineBreakFor_AND_OR_in_FROM_clause"));


   JTextArea txtExampleSqls = new JTextArea();

   JComboBox _cboColumnListSplitMode = new JComboBox();


   public FormatSqlPanel(KeywordBehaviourPref[] keywordBehaviourPrefs)
   {
      setLayout(new BorderLayout());

      JScrollPane scr = new JScrollPane(createControlsPanel(keywordBehaviourPrefs));
      scr.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(scr, BorderLayout.WEST);

      add(new JScrollPane(txtExampleSqls), BorderLayout.CENTER);
   }


   private JPanel createControlsPanel(KeywordBehaviourPref[] keywordBehaviourPrefs)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(createFormatterSelectionPanel(), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);
      ret.add(GUIUtils.createHorizontalSeparatorPanel(), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(createFormatterConfigPanel(keywordBehaviourPrefs), gbc);

      return ret;
   }

   private JPanel createFormatterSelectionPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel (s_stringMgr.getString("codereformat.FormatSqlPanel.choose.formatter")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5),0,0);
      ret.add(radUseSquirrelInternalFormatter, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(radUseVerticalBlankFormatter, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radUseSquirrelInternalFormatter);
      bg.add(radUseVerticalBlankFormatter);

      gbc = new GridBagConstraints(0,3,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new MultipleLineLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.vertical.blank.indent.only.note")), gbc);

      return ret;
   }

   private JPanel createFormatterConfigPanel(KeywordBehaviourPref[] keywordBehaviourPrefs)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      int gridy = 0;

      gbc = new GridBagConstraints(0,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.config.title")), gbc);

      ++gridy;
      int additionalRightInsetForScrollbar = 10;
      gbc = new GridBagConstraints(0,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.indentCount")), gbc);

      gbc = new GridBagConstraints(1,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5 + additionalRightInsetForScrollbar),0,0);
      txtIndentCount = new JFormattedTextField(NumberFormat.getInstance());
      txtIndentCount.setColumns(7);
      ret.add(txtIndentCount, gbc);


      ++gridy;
      gbc = new GridBagConstraints(1,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5 + additionalRightInsetForScrollbar),0,0);
      ret.add(chkIndentSections, gbc);

      ++gridy;
      gbc = new GridBagConstraints(1,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,10,5 + additionalRightInsetForScrollbar),0,0);
      ret.add(createCommasPanel(), gbc);


      ++gridy;
      gbc = new GridBagConstraints(0,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5 + additionalRightInsetForScrollbar),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.preferedLineLen")), gbc);

      gbc = new GridBagConstraints(1,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      txtPreferedLineLength = new JFormattedTextField(NumberFormat.getInstance());
      txtPreferedLineLength.setColumns(7);
      ret.add(txtPreferedLineLength, gbc);


      ++gridy;
      gbc = new GridBagConstraints(0,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(10,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.column.list.behavior")), gbc);

      gbc = new GridBagConstraints(1,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10,5,5,5 + additionalRightInsetForScrollbar),0,0);
      ret.add(_cboColumnListSplitMode, gbc);



      ++gridy;
      gbc = new GridBagConstraints(0,gridy,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,5,5,5 + additionalRightInsetForScrollbar),0,0);
      ret.add(new JLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.keywordBehavior")), gbc);

      for (KeywordBehaviourPref keywordBehaviourPref : keywordBehaviourPrefs)
      {
         keywordBehaviourPrefCtrls.add(createKeywordBehaviourPrefCtrl(ret, keywordBehaviourPref, ++gridy, additionalRightInsetForScrollbar));
      }

      ++gridy;
      gbc = new GridBagConstraints(1,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5 + additionalRightInsetForScrollbar),0,0);
      chkDoInsertValuesAlign = new JCheckBox(s_stringMgr.getString("codereformat.FormatSqlPanel.tryAlignInsertValueStatements"));
      ret.add(chkDoInsertValuesAlign, gbc);

      ++gridy;
      // When AND/OR keywords occur in from clauses you can choose to ignore the start new line setting from above.
      // This may be considered to to lead to more readable JOIN clauses.
      MultilineLabel lblLineBreakFor_AND_OR_in_FROM_clause = new MultilineLabel(s_stringMgr.getString("codereformat.FormatSqlPanel.explain.lineBreakFor_AND_OR_in_FROM_clause"));
      lblLineBreakFor_AND_OR_in_FROM_clause.setText(s_stringMgr.getString("codereformat.FormatSqlPanel.explain.lineBreakFor_AND_OR_in_FROM_clause"));

      gbc = new GridBagConstraints(0,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(lblLineBreakFor_AND_OR_in_FROM_clause, gbc);

      gbc = new GridBagConstraints(1,gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5 + additionalRightInsetForScrollbar),0,0);
      ret.add(_chkLineBreakFor_AND_OR_in_FROM_clause, gbc);

      return ret;
   }

   private JPanel createCommasPanel()
   {
      JPanel ret = new JPanel(new GridLayout(2,1,5,0));

      ret.setBorder(BorderFactory.createEtchedBorder());

      radCommasAtLineBeginNo = new JRadioButton(s_stringMgr.getString("codereformat.FormatSqlPanel.commas.at.line.begin.no"));
      ret.add(radCommasAtLineBeginNo);

      radCommasAtLineBeginYes = new JRadioButton(s_stringMgr.getString("codereformat.FormatSqlPanel.commas.at.line.begin.yes"));
      ret.add(radCommasAtLineBeginYes);


      ButtonGroup bg = new ButtonGroup();
      bg.add(radCommasAtLineBeginYes);
      bg.add(radCommasAtLineBeginNo);

      return ret;
   }

   private KeywordBehaviourPrefCtrl createKeywordBehaviourPrefCtrl(JPanel toAddTo, KeywordBehaviourPref keywordBehaviourPref, int gridy, int additionalRightInsetForScrollbar)
   {
      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      toAddTo.add(new JLabel(keywordBehaviourPref.getKeyWord()), gbc);

      gbc = new GridBagConstraints(1, gridy,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5 + + additionalRightInsetForScrollbar),0,0);
      JComboBox cbo = new JComboBox();
      toAddTo.add(cbo, gbc);
      return new KeywordBehaviourPrefCtrl(cbo, keywordBehaviourPref);
   }

}
