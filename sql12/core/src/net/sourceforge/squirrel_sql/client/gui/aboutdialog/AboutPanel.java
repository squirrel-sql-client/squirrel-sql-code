package net.sourceforge.squirrel_sql.client.gui.aboutdialog;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.VersionPane;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.*;
import java.awt.*;

final class AboutPanel extends JPanel
{
   AboutPanel(IApplication app)
   {
      final SquirrelResources rsrc = app.getResources();
      setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      setLayout(new BorderLayout());
      setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));
      Icon icon = rsrc.getIcon(SquirrelResources.IImageNames.SPLASH_SCREEN_LESS_HIGH);
      add(BorderLayout.CENTER, new JLabel(icon));

      VersionPane versionPane = new VersionPane();
      versionPane.setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND, true));
      versionPane.setForeground(Color.black);
      versionPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      add(BorderLayout.SOUTH, versionPane);
   }
}
