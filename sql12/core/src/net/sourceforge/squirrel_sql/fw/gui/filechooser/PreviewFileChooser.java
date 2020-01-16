package net.sourceforge.squirrel_sql.fw.gui.filechooser;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JSplitPane;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionListener;

public class PreviewFileChooser extends JDialog
{
   private static final String PREF_KEY_SPLIT_DIVIDER_LOCATION = "Squirrel.PreviewFileChooser.split.divider.location";

   private static int _option = JFileChooser.ERROR_OPTION;


   public PreviewFileChooser(Window owner, String title)
   {
      super(owner, title, ModalityType.APPLICATION_MODAL);
   }

   public static int showOpenDialog(Component parent, JFileChooser fileChooser)
   {


      PreviewFileChooser previewFileChooser = new PreviewFileChooser(GUIUtils.getOwningWindow(parent), fileChooser.getDialogTitle());

      previewFileChooser.getContentPane().setLayout(new GridLayout(1, 1));

      JSplitPane splitPane = new JSplitPane();
      previewFileChooser.getContentPane().add(splitPane);


      ActionListener fileChooserButtonActionListener = e -> onFileChooserButtonClicked(previewFileChooser, fileChooser, e.getActionCommand());
      fileChooser.addActionListener(fileChooserButtonActionListener);

      splitPane.setLeftComponent(fileChooser);
      ChooserPreviewer chooserPreviewer = new ChooserPreviewer(fileChooser);
      splitPane.setRightComponent(chooserPreviewer);


      Dimension size = GUIUtils.initLocation(previewFileChooser, 800, 500);

      GUIUtils.enableCloseByEscape(previewFileChooser);

      initSplitDividerLocation(splitPane, size);

      previewFileChooser.setVisible(true);

      chooserPreviewer.cleanup();
      Props.putInt(PREF_KEY_SPLIT_DIVIDER_LOCATION, splitPane.getDividerLocation());
      fileChooser.removeActionListener(fileChooserButtonActionListener);

      previewFileChooser.dispose();

      return _option;
   }

   private static void onFileChooserButtonClicked(PreviewFileChooser previewFileChooser, JFileChooser fileChooser, String actionCommand)
   {
      if(JFileChooser.APPROVE_SELECTION.equals(actionCommand))
      {
         _option = JFileChooser.APPROVE_OPTION;
      }
      else if(JFileChooser.CANCEL_SELECTION.equals(actionCommand))
      {
         _option = JFileChooser.CANCEL_OPTION;
      }

      previewFileChooser.setVisible(false);
   }

   private static void initSplitDividerLocation(JSplitPane splitPane, Dimension size)
   {
      int preferredDividerLocation = Main.getApplication().getPropsImpl().getInt(PREF_KEY_SPLIT_DIVIDER_LOCATION, size.width / 2);

      int dividerLocation = preferredDividerLocation;
      if (0 < splitPane.getWidth())
      {
         dividerLocation = Math.min(splitPane.getMaximumDividerLocation(), preferredDividerLocation);
      }

      splitPane.setDividerLocation(dividerLocation);
   }
}