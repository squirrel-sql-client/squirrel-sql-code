package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class JumpToObjectTreeConfigCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JumpToObjectTreeConfigCtrl.class);

   private JCheckBox _allowCtrlBJumpToObjectTreeChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.allowJumpToObjectTreeByCtrlB"));

   private JCheckBox _allowCtrlMouseClickJumpToObjectTree = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.allowJumpToObjectTreeByCtrlMouseClick"));

   public void loadData(SessionProperties props)
   {
      _allowCtrlBJumpToObjectTreeChk.setSelected(props.getAllowCtrlBJumpToObjectTree());
      _allowCtrlMouseClickJumpToObjectTree.setSelected(props.getAllowCtrlMouseClickJumpToObjectTree());
   }

   public boolean isAllowCtrlBJumpToObjectTree()
   {
      return _allowCtrlBJumpToObjectTreeChk.isSelected();
   }

   public boolean isAllowCtrlMouseClickJumpToObjectTree()
   {
      return _allowCtrlMouseClickJumpToObjectTree.isSelected();
   }

   public JPanel createJumpToObjectTreeConfigPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new MultipleLineLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.jumpToObjectTreeLabel")), gbc);


      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(_allowCtrlBJumpToObjectTreeChk, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,3,5),0,0);
      ret.add(_allowCtrlMouseClickJumpToObjectTree, gbc);

//      gbc = new GridBagConstraints(0,3,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,3,5),0,0);
//      ret.add(new JPanel(), gbc);



      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.jumpToObjectTree")));
      return ret;
   }


}
