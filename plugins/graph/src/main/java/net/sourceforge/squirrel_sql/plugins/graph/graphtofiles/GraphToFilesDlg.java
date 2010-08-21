package net.sourceforge.squirrel_sql.plugins.graph.graphtofiles;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphToFilesDlg extends JDialog
{

   JTabbedPane tabPages;
   JButton btnSaveToFile;
   JButton btnClose;

   JLabel[] lblPages;

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(GraphToFilesCtrlr.class);


   public GraphToFilesDlg(Frame owner, BufferedImage[] images)
      throws HeadlessException
   {
      super(owner);

      // i18n[graphToClipboard.title=Copy graph image pages to clipboard]
      setTitle(s_stringMgr.getString("graphToFile.title"));

      buildGui();


      lblPages = new JLabel[images.length];
      for (int i = 0; i < images.length; i++)
      {
         lblPages[i] = new JLabel(new ImageIcon(images[i]));
         lblPages[i].setTransferHandler(new ImageSelection());
         tabPages.addTab("" + (i+1), new JScrollPane(lblPages[i]));
      }

      setSize(500, 450);
      setVisible(true);

      GUIUtils.centerWithinParent(this);
   }

   private void buildGui()
   {
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      tabPages = new JTabbedPane();
      gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(tabPages, gbc);


      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel();

      GridBagConstraints gbc;

      // i18n[graphToClipboard.copyButton=Copy image from selected tab]
      btnSaveToFile = new JButton(s_stringMgr.getString("graphToFile.saveFilesTo"));
      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnSaveToFile, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5,5,5,5), 0,0);
      ret.add(new JPanel(), gbc);
      
      // i18n[graphToClipboard.closeButton=Close]
      btnClose = new JButton(s_stringMgr.getString("graphToClipboard.closeButton"));
      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      ret.add(btnClose, gbc);

      return ret;

   }
}
