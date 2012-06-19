package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import javax.swing.*;
import java.awt.event.ActionListener;

public class KeywordBehaviourPrefCtrl
{
   private final JComboBox _cbo;
   private final KeywordBehaviourPref _keywordBehaviourPref;

   public KeywordBehaviourPrefCtrl(JComboBox cbo, KeywordBehaviourPref keywordBehaviourPref)
   {
      _cbo = cbo;

      for (FormatSqlPanel.KeywordBehaviour keywordBehaviour : FormatSqlPanel.KeywordBehaviour.values())
      {
         cbo.addItem(keywordBehaviour);
      }
      cbo.setSelectedItem(FormatSqlPanel.KeywordBehaviour.forId(keywordBehaviourPref.getKeywordBehaviourId()));

      _keywordBehaviourPref = keywordBehaviourPref;
   }

   public void applyChanges()
   {
      FormatSqlPanel.KeywordBehaviour keywordBehaviour = (FormatSqlPanel.KeywordBehaviour) _cbo.getSelectedItem();
      _keywordBehaviourPref.setKeywordBehaviourId(keywordBehaviour.getID());
   }

   public KeywordBehaviourPref getKeywordBehaviourPref()
   {
      return _keywordBehaviourPref;
   }

   public void addKeyWordBehaviourChangedListener(ActionListener l)
   {
      _cbo.addActionListener(l);
   }

   public void setBehaviour(FormatSqlPanel.KeywordBehaviour keywordBehaviour)
   {
      _cbo.setSelectedItem(keywordBehaviour);
   }

   public void setEnabled(boolean b)
   {
      _cbo.setEnabled(b);
   }
}
