package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.VersionPane;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

final class AboutPanel extends JPanel
{
   AboutPanel(IApplication app)
   {
      final SquirrelResources rsrc = app.getResources();
      setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      setLayout(new BorderLayout());
      setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));
      Icon icon = rsrc.getIcon(SquirrelResources.IImageNames.SPLASH_SCREEN);
      add(BorderLayout.CENTER, new JLabel(icon));

      VersionPane versionPane = new VersionPane();
      versionPane.setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND, true));
      versionPane.setForeground(Color.black);
      versionPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      add(BorderLayout.SOUTH, versionPane);
   }
}
