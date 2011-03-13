package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class ConstraintIconHandler
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ConstraintIconHandler.class);


   void paintJoinIcon(Graphics g,
                      GraphLine line,
                      TableFrameController fkFrameOriginatingFrom,
                      TableFrameController pkFramePointingTo,
                      GraphDesktopController desktopController,
                      ConstraintData constraintData)
   {

      if(false == desktopController.getModeManager().getMode().isQueryBuilder())
      {
         return;
      }

      ImageIcon icon = getIcon(fkFrameOriginatingFrom, pkFramePointingTo, desktopController, constraintData);

      int imageX = getImageX(line, icon);
      int imageY = getImageY(line, icon);

      g.drawImage(icon.getImage(), imageX, imageY, null);
   }

   private ImageIcon getIcon(TableFrameController fkFrameOriginatingFrom,
                             TableFrameController pkFramePointingTo,
                             GraphDesktopController desktopController,
                             ConstraintData constraintData)
   {
      final TableFrameController left;
      final TableFrameController right;

      if(pkFramePointingTo.getFrame().getLocation().x < fkFrameOriginatingFrom.getFrame().getLocation().x)
      {
         left = pkFramePointingTo;
         right = fkFrameOriginatingFrom;
      }
      else
      {
         left = fkFrameOriginatingFrom;
         right = pkFramePointingTo;
      }


      if(constraintData.getConstraintQueryData().isInnerJoin())
      {
         return desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_INNER);
      }
      else if(constraintData.getConstraintQueryData().isOuterJoinFor(left.getTableInfo().getSimpleName()))
      {
         return desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_LEFT);
      }
      else if(constraintData.getConstraintQueryData().isOuterJoinFor(right.getTableInfo().getSimpleName()))
      {
         return desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_RIGHT);
      }
      else if(constraintData.getConstraintQueryData().isNoJoin())
      {
         return desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_NONE);
      }
      else
      {
         throw new IllegalStateException("Could not find Join-Icon");
      }
   }

   private int getImageY(GraphLine line, ImageIcon icon)
   {
      int yMid = line.getBegin().y + (int)((double)((line.getEnd().y - line.getBegin().y) / 2d) + 0.5);
      int iHeight = icon.getImage().getHeight(null);
      return yMid - ((int) (iHeight / 2d + 0.5d));
   }

   private int getImageX(GraphLine line, ImageIcon icon)
   {
      int xMid = line.getBegin().x + (int)((double)((line.getEnd().x- line.getBegin().x) / 2d) + 0.5);
      int iWidth = icon.getImage().getWidth(null);
      return xMid - ((int) (iWidth / 2d + 0.5d));
   }

   public boolean hitMe(MouseEvent e,
                        GraphLine line,
                        TableFrameController fkFrameOriginatingFrom,
                        TableFrameController pkFramePointingTo,
                        GraphDesktopController desktopController,
                        ConstraintData constraintData)
   {
      if(false == desktopController.getModeManager().getMode().isQueryBuilder())
      {
         return false;
      }

      ImageIcon icon = getIcon(fkFrameOriginatingFrom, pkFramePointingTo, desktopController, constraintData);


      int imageX = getImageX(line, icon);
      int imageY = getImageY(line, icon);
      int hitX = e.getPoint().x;
      int hitY = e.getPoint().y;

      if(   imageX < hitX && hitX < imageX + icon.getImage().getWidth(null)
         && imageY < hitY && hitY < imageY + icon.getImage().getHeight(null))
      {
         if(0 != (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK))
         {
            showPopup(e, imageX, imageY, pkFramePointingTo, fkFrameOriginatingFrom, desktopController, constraintData);
         }
         return true;
      }

      return false;
   }

   private void showPopup(MouseEvent e,
                          int imageX,
                          int imageY,
                          TableFrameController pkFramePointingTo,
                          TableFrameController fkFrameOriginatingFrom,
                          GraphDesktopController desktopController,
                          final ConstraintData constraintData)
   {
      JPopupMenu popupMenu = new JPopupMenu();

      ImageIcon icon;
      String txt;

      final TableFrameController left;
      final TableFrameController right;

      if(pkFramePointingTo.getFrame().getLocation().x < fkFrameOriginatingFrom.getFrame().getLocation().x)
      {
         left = pkFramePointingTo;
         right = fkFrameOriginatingFrom;
      }
      else
      {
         left = fkFrameOriginatingFrom;
         right = pkFramePointingTo;
      }



      icon = desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_INNER);
      txt = s_stringMgr.getString("ConstraintIconHandler.innerJoin");
      final JRadioButtonMenuItem innerMenuItem = new JRadioButtonMenuItem(txt, icon);
      innerMenuItem.setSelected(constraintData.getConstraintQueryData().isInnerJoin());
      innerMenuItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onInnerMenuItem(innerMenuItem, constraintData);
         }
      });


      icon = desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_LEFT);
      txt = s_stringMgr.getString("ConstraintIconHandler.outerJoin", left.getTableInfo().getSimpleName());
      final JRadioButtonMenuItem leftMenuItem = new JRadioButtonMenuItem(txt, icon);
      leftMenuItem.setSelected(constraintData.getConstraintQueryData().isOuterJoinFor(left.getTableInfo().getSimpleName()));
      leftMenuItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onOuterMenuItem(leftMenuItem, constraintData, left.getTableInfo().getSimpleName());
         }
      });


      icon = desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_RIGHT);
      txt = s_stringMgr.getString("ConstraintIconHandler.outerJoin", right.getTableInfo().getSimpleName());
      final JRadioButtonMenuItem rightMenuItem = new JRadioButtonMenuItem(txt, icon);
      rightMenuItem.setSelected(constraintData.getConstraintQueryData().isOuterJoinFor(right.getTableInfo().getSimpleName()));
      rightMenuItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onOuterMenuItem(rightMenuItem, constraintData, right.getTableInfo().getSimpleName());
         }
      });


      icon = desktopController.getResource().getIcon(GraphPluginResources.IKeys.JOIN_NONE);
      txt = s_stringMgr.getString("ConstraintIconHandler.noJoin");
      final JRadioButtonMenuItem noneMenuItem = new JRadioButtonMenuItem(txt, icon);
      noneMenuItem.setSelected(constraintData.getConstraintQueryData().isNoJoin());
      noneMenuItem.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onNoneMenuItem(noneMenuItem, constraintData);
         }
      });

      ButtonGroup bg = new ButtonGroup();
      bg.add(innerMenuItem);
      bg.add(leftMenuItem);
      bg.add(rightMenuItem);
      bg.add(noneMenuItem);

      popupMenu.add(innerMenuItem);
      popupMenu.add(leftMenuItem);
      popupMenu.add(rightMenuItem);
      popupMenu.add(noneMenuItem);

      popupMenu.show(e.getComponent(), imageX, imageY);
   }

   private void onNoneMenuItem(JRadioButtonMenuItem noneMenuItem, ConstraintData constraintData)
   {
      if (noneMenuItem.isSelected())
      {
         constraintData.getConstraintQueryData().setNoJoin();
      }
   }

   private void onOuterMenuItem(JRadioButtonMenuItem leftmenuItem, ConstraintData constraintData, String outerTableName)
   {
      if (leftmenuItem.isSelected())
      {
         constraintData.getConstraintQueryData().setOuterJoin(outerTableName);
      }
   }

   private void onInnerMenuItem(JRadioButtonMenuItem innerMenuItem, ConstraintData constraintData)
   {
      if (innerMenuItem.isSelected())
      {
         constraintData.getConstraintQueryData().setInnerJoin();
      }
   }
}
