package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooserOrientation;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class FindInObjectTreePanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindInObjectTreePanel.class);


   JButton btnFind;
   JToggleButton btnApplyAsFilter;

   ButtonChooser chooserAppendWildcard;
   private JButton btnAppendWildcard;
   private JButton btnDontAppendWildcard;

   public FindInObjectTreePanel(JTextComponent textComponent, SquirrelResources resources)
   {
      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2,2,2,0), 0,0);
      add(textComponent, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,0,2,0), 0,0);
      btnFind = new JButton(resources.getIcon(SquirrelResources.IImageNames.FIND));
      btnFind.setBorder(BorderFactory.createEtchedBorder());
      btnFind.setToolTipText(s_stringMgr.getString("FindInObjectTreePanel.findNew"));
      add(btnFind, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,0,2,0), 0,0);
      btnApplyAsFilter = new JToggleButton(resources.getIcon(SquirrelResources.IImageNames.FILTER));
      btnApplyAsFilter.setBorder(BorderFactory.createEtchedBorder());
      btnApplyAsFilter.setToolTipText(s_stringMgr.getString("FindInObjectTreePanel.applyAsFilter"));
      add(btnApplyAsFilter, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,1,2,0), 0,0);
      chooserAppendWildcard = createWildcardChooser(resources);
      chooserAppendWildcard.setPreferedHight(btnFind.getPreferredSize().height);
      add(chooserAppendWildcard.getComponent(), gbc);

      GUIUtils.setPreferredHeight(textComponent, btnFind.getPreferredSize().height);

      textComponent.setBorder(BorderFactory.createEtchedBorder());

   }

   private ButtonChooser createWildcardChooser(SquirrelResources resources)
   {
      ButtonChooser ret = new ButtonChooser(false, ButtonChooserOrientation.LEFT);

      btnAppendWildcard = new JButton(s_stringMgr.getString("FindInObjectTreePanel.appendWildcard"), resources.getIcon(SquirrelResources.IImageNames.PERCENT));
      //btnAppendWildcard.setToolTipText(s_stringMgr.getString("FindInObjectTreePanel.appendWildcard.tooltip"));
      btnAppendWildcard.setBorder(BorderFactory.createEmptyBorder());
      ret.addUnclickableButton(btnAppendWildcard);

      btnDontAppendWildcard = new JButton(s_stringMgr.getString("FindInObjectTreePanel.dontAppendWildcard"), resources.getIcon(SquirrelResources.IImageNames.PERCENT_NEGATED));
      btnDontAppendWildcard.setBorder(BorderFactory.createEmptyBorder());
      ret.addUnclickableButton(btnDontAppendWildcard);

      ret.styleAsToolbarButton();
      return ret;
   }

   boolean isAppendWildCard()
   {
      return chooserAppendWildcard.getSelectedButton() == btnAppendWildcard;
   }

   public void setAppendWildcard(boolean appendWildcard)
   {
      if(appendWildcard)
      {
         chooserAppendWildcard.setSelectedButton(btnAppendWildcard);
      }
      else
      {
         chooserAppendWildcard.setSelectedButton(btnDontAppendWildcard);
      }
   }
}
