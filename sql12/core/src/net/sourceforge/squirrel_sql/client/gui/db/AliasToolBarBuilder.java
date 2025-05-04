package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

public class AliasToolBarBuilder
{
   private record ToolBarItem(Action action, IToggleAction toggleAction, boolean separator, double widthMeasure)
   {
      static ToolBarItem of(Action action) {return new ToolBarItem(action, null, false, 1);};
      static ToolBarItem of(IToggleAction toggleAction) {return new ToolBarItem(null, toggleAction, false, 1);};
      static ToolBarItem ofSeparator() {return new ToolBarItem(null, null, true, 0.3);};
   }

   private final ToolBar _containerToolBar;
   private List<ToolBarItem> _toolBarItems = new ArrayList<>();

   public AliasToolBarBuilder(String windowTitle)
   {
      ToolBar toolBar = new ToolBar();
      toolBar.setUseRolloverButtons(true);
      toolBar.setFloatable(false);

      if (Main.getApplication().getDesktopStyle().isInternalFrameStyle())
      {
         final JLabel lbl = new JLabel(windowTitle, SwingConstants.CENTER);
         lbl.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
         toolBar.add(lbl, 0);
      }


      _containerToolBar = toolBar;
   }

   public void clearItems()
   {
      _toolBarItems.clear();
   }

   public void add(Action action)
   {
      _toolBarItems.add(ToolBarItem.of(action));
   }

   public void addSeparator()
   {
      _toolBarItems.add(ToolBarItem.ofSeparator());
   }

   public void addToggleAction(IToggleAction toggleAction)
   {
      _toolBarItems.add(ToolBarItem.of(toggleAction));
   }

   public ToolBar reBuildToolBar(boolean compressAliasesToolbar)
   {
      if(false == Main.getApplication().getSquirrelPreferences().getShowAliasesToolBar())
      {
         return null;
      }

      _containerToolBar.removeAll();

      if(compressAliasesToolbar)
      {
         buildCompressedToolBar();
      }
      else
      {
         buildUncompressedToolBar();
      }

      _containerToolBar.invalidate();
      _containerToolBar.repaint();

      return _containerToolBar;
   }

   private void buildCompressedToolBar()
   {
      double completeWidthMeasure = _toolBarItems.stream().mapToDouble(i -> i.widthMeasure).sum();

      ToolBar tlbUpper = new ToolBar();
      tlbUpper.setUseRolloverButtons(true);
      tlbUpper.setFloatable(false);

      ToolBar tlbLower = new ToolBar();
      tlbLower.setUseRolloverButtons(true);
      tlbLower.setFloatable(false);

      double currentMeasure = 0;
      for(ToolBarItem toolBarItem : _toolBarItems)
      {
         currentMeasure += toolBarItem.widthMeasure;

         if(currentMeasure < completeWidthMeasure / 2)
         {
            appendItemToToolBar(toolBarItem, tlbUpper);
         }
         else
         {
            appendItemToToolBar(toolBarItem, tlbLower);
         }
      }

      JPanel pnlOuterToolBarContend = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 1,1);
      pnlOuterToolBarContend.add(tlbUpper, gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 1,1);
      pnlOuterToolBarContend.add(tlbLower, gbc);

      _containerToolBar.add(pnlOuterToolBarContend);
   }

   private static void appendItemToToolBar(ToolBarItem toolBarItem, ToolBar tlbUpper)
   {
      if(null != toolBarItem.action)
      {
         tlbUpper.add(toolBarItem.action);
      }
      else if(null != toolBarItem.toggleAction)
      {
         tlbUpper.addToggleAction(toolBarItem.toggleAction);
      }
      else if(toolBarItem.separator)
      {
         tlbUpper.addSeparator();
      }
   }

   private void buildUncompressedToolBar()
   {
      for(ToolBarItem item : _toolBarItems)
      {
         if(null != item.action)
         {
            _containerToolBar.add(item.action);
         }
         else if(null != item.toggleAction)
         {
            _containerToolBar.addToggleAction(item.toggleAction);
         }
         else if(item.separator)
         {
            _containerToolBar.addSeparator();
         }
         else
         {
            throw new UnsupportedOperationException("Don't know what to do here");
         }
      }
   }
}
