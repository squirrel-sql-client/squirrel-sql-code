package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public class JoinConfigUtil
{
   public static void drawConfigureImage(GraphicsContext gc, LineSpec lineSpec)
   {
      JoinConfig joinConfig = lineSpec.getFkSpec().getJoinConfig();

      String imageName;
      if(joinConfig == JoinConfig.LEFT_JOIN)
      {
         if(isFkTableLeft(lineSpec))
         {
            imageName = JoinConfig.LEFT_JOIN.getImageName();
         }
         else
         {
            imageName = JoinConfig.RIGHT_JOIN.getImageName();
         }
      }
      else if(joinConfig == JoinConfig.RIGHT_JOIN)
      {
         if(isFkTableLeft(lineSpec))
         {
            imageName = JoinConfig.RIGHT_JOIN.getImageName();
         }
         else
         {
            imageName = JoinConfig.LEFT_JOIN.getImageName();
         }
      }
      else
      {
         imageName = joinConfig.getImageName();
      }


      Image image = new Props(JoinConfigUtil.class).getImage(imageName);

      Point2D imagePoint = getImagePoint(lineSpec, image);

      gc.drawImage(image, imagePoint.getX(), imagePoint.getY());
   }

   private static Point2D getImagePoint(LineSpec lineSpec, Image image)
   {
      Point2D lineForConfigImageBegin = new Point2D(lineSpec.getPkGatherPointX(), lineSpec.getPkGatherPointY());
      Point2D lineForConfigImageEnd;

      if(0 < lineSpec.getFoldingPoints().size())
      {
         lineForConfigImageEnd = lineSpec.getFoldingPoints().get(0);
      }
      else
      {
         lineForConfigImageEnd = new Point2D(lineSpec.getFkGatherPointX(), lineSpec.getFkGatherPointY());
      }


      double imageX = getImageX(lineForConfigImageBegin, lineForConfigImageEnd, image);
      double imageY = getImageY(lineForConfigImageBegin, lineForConfigImageEnd, image);

      return new Point2D(imageX, imageY);
   }

   private static double getImageY(Point2D lineBeg, Point2D lineEnd, Image icon)
   {
      double yMid = lineBeg.getY() + ((lineEnd.getY() - lineBeg.getY()) / 2d);
      double height = icon.getHeight();
      return yMid - (height / 2d);
   }

   private static double getImageX(Point2D lineBeg, Point2D lineEnd, Image icon)
   {
      double xMid = lineBeg.getX() + (lineEnd.getX()- lineBeg.getX()) / 2d;
      double width = icon.getWidth();
      return xMid - width / 2d;
   }

   public static boolean checkAndHandleClickOnIcon(LineInteractionInfo currentLineInteractionInfo, MouseEvent me, Pane desktopPane, Runnable redrawCallback)
   {
      LineSpec clickedOnLineSpec = currentLineInteractionInfo.getClickedOnLineSpec();
      if(null == clickedOnLineSpec)
      {
         return false;
      }

      Image equalImage = new Props(JoinConfigUtil.class).getImage(JoinConfig.INNER_JOIN.getImageName());

      Point2D p1 = getImagePoint(clickedOnLineSpec, equalImage);
      Point2D p2 = new Point2D(p1.getX() + equalImage.getWidth(), p1.getY());
      Point2D p3 = new Point2D(p1.getX() + equalImage.getWidth(), p1.getY() + equalImage.getHeight());
      Point2D p4 = new Point2D(p1.getX(), p1.getY() + equalImage.getHeight());

      Polygon polygon = new Polygon();

      polygon.getPoints().addAll(p1.getX(), p1.getY());
      polygon.getPoints().addAll(p2.getX(), p2.getY());
      polygon.getPoints().addAll(p3.getX(), p3.getY());
      polygon.getPoints().addAll(p4.getX(), p4.getY());

      if(false == polygon.contains(me.getX(), me.getY()))
      {
         return false;
      }

      MenuItem innerJoin = new MenuItem("INNER JOIN", new ImageView(equalImage));
      innerJoin.setOnAction(ae -> onJoinConfigSelected(clickedOnLineSpec, JoinConfig.INNER_JOIN, redrawCallback));



      String leftSideTableName = getLeftSideTableName(clickedOnLineSpec);
      String rightSideTableName = getRightSideTableName(clickedOnLineSpec);

      Image equalLeftImage = new Props(JoinConfigUtil.class).getImage(JoinConfig.LEFT_JOIN.getImageName());
      Image equalRightImage = new Props(JoinConfigUtil.class).getImage(JoinConfig.RIGHT_JOIN.getImageName());
      Image equalCrossedImage = new Props(JoinConfigUtil.class).getImage(JoinConfig.NO_JOIN.getImageName());

      I18n i18n = new I18n(JoinConfigUtil.class);
      MenuItem  leftJoin = new MenuItem(i18n.t("join.type.outer", leftSideTableName), new ImageView(equalLeftImage));
      leftJoin.setOnAction(ae -> onJoinConfigSelected(clickedOnLineSpec, JoinConfig.LEFT_JOIN, redrawCallback));

      MenuItem  rightJoin = new MenuItem(i18n.t("join.type.outer", rightSideTableName), new ImageView(equalRightImage));
      rightJoin.setOnAction(ae -> onJoinConfigSelected(clickedOnLineSpec, JoinConfig.RIGHT_JOIN, redrawCallback));


      MenuItem noJoin = new MenuItem("NO JOIN", new ImageView(equalCrossedImage));
      noJoin.setOnAction(ae -> onJoinConfigSelected(clickedOnLineSpec, JoinConfig.NO_JOIN, redrawCallback));


      ContextMenu popup = new ContextMenu(innerJoin, leftJoin, rightJoin, noJoin);

      Point2D localToScreen = desktopPane.localToScreen(p1.getX(), p1.getY());

      popup.show(desktopPane, localToScreen.getX(), localToScreen.getY());

      return false;
   }

   private static void onJoinConfigSelected(LineSpec clickedOnLineSpec, JoinConfig joinConfig, Runnable redrawCallback)
   {
      clickedOnLineSpec.getFkSpec().setJoinConfig(joinConfig);
      redrawCallback.run();
   }


   private static String getLeftSideTableName(LineSpec lineSpec)
   {
      GraphColumn graphColumn = lineSpec.getFkSpec().getFkPoints().get(0).getGraphColumn();

      if(lineSpec.getFkGatherPointX() < lineSpec.getPkGatherPointX())
      {
         return graphColumn.getColumnInfo().getTableName();
      }
      else
      {
         String fkNameOrId = lineSpec.getFkSpec().getFkNameOrId();
         return graphColumn.getPkTableName(fkNameOrId);
      }
   }

   private static String getRightSideTableName(LineSpec lineSpec)
   {
      GraphColumn graphColumn = lineSpec.getFkSpec().getFkPoints().get(0).getGraphColumn();

      if(lineSpec.getFkGatherPointX() > lineSpec.getPkGatherPointX())
      {
         return graphColumn.getColumnInfo().getTableName();
      }
      else
      {
         String fkNameOrId = lineSpec.getFkSpec().getFkNameOrId();
         return graphColumn.getPkTableName(fkNameOrId);
      }
   }

   private static boolean isFkTableLeft(LineSpec lineSpec)
   {
      return lineSpec.getFkGatherPointX() < lineSpec.getPkGatherPointX();
   }


}
