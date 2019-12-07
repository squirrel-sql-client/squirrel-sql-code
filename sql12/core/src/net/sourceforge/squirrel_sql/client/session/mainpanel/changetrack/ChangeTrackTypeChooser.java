package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.ChangeTrackAction;
import net.sourceforge.squirrel_sql.client.session.action.toolbarbuttonchooser.ToolbarButtonChooserUtil;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.SQLWorksheetTypeEnum;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;

public class ChangeTrackTypeChooser
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackTypeChooser.class);

   private ChangeTrackAction _action;
   private final ButtonChooser _buttonChooser = new ButtonChooser();

   public ChangeTrackTypeChooser(ChangeTrackAction action)
   {
      _action = action;

      initActionAndListeners();

      setEnabled(_action.isEnabled());

      _action.setEnabledListener(b -> setEnabled(b));
   }

   private void setEnabled(boolean enabled)
   {
      _buttonChooser.setChooserEnabled(enabled);
   }

   public JComponent getComponent()
   {
      return _buttonChooser;
   }

   private void initActionAndListeners()
   {
      SquirrelResources rsrc = Main.getApplication().getResources();
      ImageIcon iconTrackManual = rsrc.getIcon(SquirrelResources.IImageNames.CHANGE_TRACK_MANUAL);
      ImageIcon iconTrackFile = rsrc.getIcon(SquirrelResources.IImageNames.CHANGE_TRACK_FILE);
      ImageIcon iconTrackGit = rsrc.getIcon(SquirrelResources.IImageNames.CHANGE_TRACK_GIT);

      String textTrackManual = s_stringMgr.getString("ChangeTrackTypeChooser.manual") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);
      String textTrackFile = s_stringMgr.getString("ChangeTrackTypeChooser.file") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);
      String textTrackGit = s_stringMgr.getString("ChangeTrackTypeChooser.git") + ToolbarButtonChooserUtil.getAcceleratorString(rsrc, _action);


      JButton btnTrackManual = new JButton(textTrackManual, iconTrackManual);
      btnTrackManual.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addButton(btnTrackManual);

      JButton btnTrackFile = new JButton(textTrackFile, iconTrackFile);

      // TODO: Not clickable
      //btnTrackFile.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addButton(btnTrackFile);

      JButton btnTrackGit = new JButton(textTrackGit, iconTrackGit);
      btnTrackGit.addActionListener(e -> _action.actionPerformed(e));
      _buttonChooser.addButton(btnTrackGit);


      switch (ChangeTrackTypeEnum.getSelectedType())
      {
         case MANUAL:
            _buttonChooser.setSelectedButton(btnTrackManual);
            break;
         case FILE:
            _buttonChooser.setSelectedButton(btnTrackFile);
            break;
         case GIT:
            _buttonChooser.setSelectedButton(btnTrackGit);
            break;
         default:
            throw new IllegalStateException("Unknown Type " + SQLWorksheetTypeEnum.getSelectedType());
      }

      _buttonChooser.setButtonSelectedListener((button, formerSelectedButton) -> onButtonSelected(button, btnTrackManual, btnTrackFile, btnTrackGit));
   }

   private void onButtonSelected(AbstractButton button, JButton btnTrackManual, JButton btnTrackFile, JButton btnTrackGit)
   {
      if(button == btnTrackManual)
      {
         ChangeTrackTypeEnum.MANUAL.saveSelected();
      }
      else if(button == btnTrackFile)
      {
         ChangeTrackTypeEnum.FILE.saveSelected();
      }
      else if(button == btnTrackGit)
      {
         ChangeTrackTypeEnum.GIT.saveSelected();
      }
   }
}
