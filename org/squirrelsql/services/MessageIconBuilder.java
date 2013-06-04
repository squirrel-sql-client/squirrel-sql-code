package org.squirrelsql.services;


/*******************************************************************************
 * JfxMessageBox
 * Copyright (C) 2012 Toshiki IGA
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
/*******************************************************************************
 * Copyright (c) 2012 Toshiki IGA and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Toshiki IGA - initial implementation
 *******************************************************************************/
/*******************************************************************************
 * Copyright 2012 Toshiki IGA and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import javafx.scene.Group;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * Icon builder for MessageBox
 *
 * @author ToshikiIga
 */
public class MessageIconBuilder
{
   /**
    * Draw error icon.
    *
    * @param zoomRate
    */
   public static Group drawErrorIcon(final int zoomRate)
   {
      final Group root = new Group();

      final Circle circle = new Circle(5 * zoomRate, 5 * zoomRate, 5 * zoomRate);
      root.getChildren().add(circle);
      circle.setFill(Color.RED);

      {
         final Light.Spot light = new Light.Spot();
         light.setX(0);
         light.setY(0);
         light.setZ(10 * zoomRate);
         final Lighting lighting = new Lighting();
         lighting.setLight(light);
         circle.setEffect(lighting);
      }

      {
         final Line line = new Line(3 * zoomRate, 3 * zoomRate, 7 * zoomRate, 7 * zoomRate);
         line.setFill(null);
         line.setStroke(Color.WHITE);
         line.setStrokeWidth(1.2 * zoomRate);
         root.getChildren().add(line);
      }

      {
         final Line line = new Line(7 * zoomRate, 3 * zoomRate, 3 * zoomRate, 7 * zoomRate);
         line.setFill(null);
         line.setStroke(Color.WHITE);
         line.setStrokeWidth(1.2 * zoomRate);
         root.getChildren().add(line);
      }

      return root;
   }

   public static Group drawWarningIcon(final int zoomRate)
   {
      final Group root = new Group();

      final Path pathTriangle = new Path();
      {
         root.getChildren().add(pathTriangle);
         pathTriangle.getElements().addAll(
               new MoveTo(4.5 * zoomRate, 0.5 * zoomRate),
               new CubicCurveTo(4.5 * zoomRate, 0.5 * zoomRate, 5 * zoomRate, 0 * zoomRate, 5.5 * zoomRate,
                     0.5 * zoomRate),
               new LineTo(10 * zoomRate, 9 * zoomRate),
               new CubicCurveTo(10 * zoomRate, 9 * zoomRate, 10 * zoomRate, 10 * zoomRate, 9 * zoomRate,
                     10 * zoomRate),
               new LineTo(1 * zoomRate, 10 * zoomRate),
               new CubicCurveTo(1 * zoomRate, 10 * zoomRate, 0 * zoomRate, 10 * zoomRate, 0 * zoomRate,
                     9 * zoomRate), new ClosePath());
         pathTriangle.setStrokeWidth(0);
         pathTriangle.setFill(Color.YELLOW);
      }
      {
         final Light.Spot light = new Light.Spot();
         light.setX(0);
         light.setY(0);
         light.setZ(15 * zoomRate);
         final Lighting lighting = new Lighting();
         lighting.setLight(light);
         pathTriangle.setEffect(lighting);
      }

      {
         final Circle circle = new Circle(5 * zoomRate, 3.8 * zoomRate, 0.8 * zoomRate);
         root.getChildren().add(circle);
         circle.setFill(Color.BLACK);
      }

      {
         final Path path = new Path();
         root.getChildren().add(path);
         path.getElements().addAll(new MoveTo(5.8 * zoomRate, 3.8 * zoomRate),
               new LineTo(5 * zoomRate, 7.5 * zoomRate), new LineTo(4.2 * zoomRate, 3.8 * zoomRate),
               new ClosePath());
         path.setStrokeWidth(0);
         path.setFill(Color.BLACK);
      }

      {
         final Circle circle = new Circle(5 * zoomRate, 8.5 * zoomRate, 0.7 * zoomRate);
         root.getChildren().add(circle);
         circle.setFill(Color.BLACK);
      }

      return root;
   }

   public static Group drawInformationIcon(final int zoomRate)
   {
      final Group root = new Group();

      final Color baseColor = Color.rgb(30, 30, 255);
      final Color backColor = Color.rgb(120, 140, 255);

      final Path path = new Path();
      {
         root.getChildren().add(path);
         path.getElements().addAll(
               new MoveTo(5 * zoomRate, 0),
               new CubicCurveTo(5 * zoomRate, 0, 0, 0, 0, 4 * zoomRate),
               new CubicCurveTo(0, 4 * zoomRate, 0, 8 * zoomRate, 4 * zoomRate, 8 * zoomRate),
               new CubicCurveTo(4 * zoomRate, 8 * zoomRate, 4 * zoomRate, 10 * zoomRate, 6 * zoomRate,
                     10 * zoomRate),
               new CubicCurveTo(6 * zoomRate, 10 * zoomRate, 5 * zoomRate, 10 * zoomRate, 5.5 * zoomRate,
                     8 * zoomRate),
               new CubicCurveTo(5.5 * zoomRate, 8 * zoomRate, 10 * zoomRate, 8.6 * zoomRate, 10 * zoomRate,
                     4 * zoomRate),
               new CubicCurveTo(10 * zoomRate, 4 * zoomRate, 10 * zoomRate, 0, 4 * zoomRate, 0));
         path.setStrokeWidth(0);
         path.setFill(backColor);
         path.setOpacity(0.6);
      }

      {
         final Light.Spot light = new Light.Spot();
         light.setX(0);
         light.setY(0);
         light.setZ(10 * zoomRate);
         final Lighting lighting = new Lighting();
         lighting.setLight(light);
         path.setEffect(lighting);
      }

      {
         Circle circle = new Circle(5. * zoomRate, 1.5 * zoomRate, 0.7 * zoomRate);
         root.getChildren().add(circle);
         circle.setFill(baseColor);
      }

      {
         final Line line = new Line(5 * zoomRate, 3.5 * zoomRate, 5 * zoomRate, 6 * zoomRate);
         root.getChildren().add(line);
         line.setStrokeWidth(1.2 * zoomRate);
         line.setStroke(baseColor);
      }

      {
         final Line line = new Line(3.8 * zoomRate, 3 * zoomRate, 5.4 * zoomRate, 3 * zoomRate);
         root.getChildren().add(line);
         line.setStrokeWidth(0.4 * zoomRate);
         line.setStroke(baseColor);
      }

      {
         final Line line = new Line(3.8 * zoomRate, 6.8 * zoomRate, 6.2 * zoomRate, 6.8 * zoomRate);
         root.getChildren().add(line);
         line.setStrokeWidth(0.4 * zoomRate);
         line.setStroke(baseColor);
      }

      return root;
   }

   public static Group drawQuestionIcon(final int zoomRate)
   {
      final Group root = new Group();

      final Color baseColor = Color.rgb(0, 64, 0);
      final Color backColor = Color.rgb(120, 255, 140);

      final Path pathBalloon = new Path();
      {
         root.getChildren().add(pathBalloon);
         pathBalloon.getElements().addAll(
               new MoveTo(5 * zoomRate, 0),
               new CubicCurveTo(5 * zoomRate, 0, 0, 0, 0, 4 * zoomRate),
               new CubicCurveTo(0, 4 * zoomRate, 0, 8 * zoomRate, 4 * zoomRate, 8 * zoomRate),
               new CubicCurveTo(4 * zoomRate, 8 * zoomRate, 4 * zoomRate, 10 * zoomRate, 6 * zoomRate,
                     10 * zoomRate),
               new CubicCurveTo(6 * zoomRate, 10 * zoomRate, 5 * zoomRate, 10 * zoomRate, 5.5 * zoomRate,
                     8 * zoomRate),
               new CubicCurveTo(5.5 * zoomRate, 8 * zoomRate, 10 * zoomRate, 8.6 * zoomRate, 10 * zoomRate,
                     4 * zoomRate),
               new CubicCurveTo(10 * zoomRate, 4 * zoomRate, 10 * zoomRate, 0, 4 * zoomRate, 0));
         pathBalloon.setStrokeWidth(0);
         pathBalloon.setFill(backColor);
         pathBalloon.setOpacity(0.6);
      }

      {
         final Light.Spot light = new Light.Spot();
         light.setX(0);
         light.setY(0);
         light.setZ(10 * zoomRate);
         final Lighting lighting = new Lighting();
         lighting.setLight(light);
         pathBalloon.setEffect(lighting);
      }

      {
         Circle circle = new Circle(5 * zoomRate, 6.5 * zoomRate, 0.7 * zoomRate);
         root.getChildren().add(circle);
         circle.setFill(baseColor);
      }

      {
         final Path path = new Path();
         root.getChildren().add(path);
         path.getElements()
               .addAll(new MoveTo(3 * zoomRate, 3 * zoomRate),
                     new CubicCurveTo(3 * zoomRate, 3 * zoomRate, 3 * zoomRate, 1 * zoomRate, 5 * zoomRate,
                           1 * zoomRate),
                     new CubicCurveTo(5 * zoomRate, 1 * zoomRate, 7 * zoomRate, 1 * zoomRate, 7 * zoomRate,
                           3 * zoomRate),
                     new CubicCurveTo(7 * zoomRate, 3 * zoomRate, 6.5 * zoomRate, 4 * zoomRate, 5.5 * zoomRate,
                           4 * zoomRate),
                     new CubicCurveTo(5.5 * zoomRate, 4 * zoomRate, 5 * zoomRate, 4 * zoomRate, 5 * zoomRate,
                           5.5 * zoomRate),
                     new CubicCurveTo(5 * zoomRate, 5 * zoomRate, 4.5 * zoomRate, 4.5 * zoomRate, 5 * zoomRate,
                           3.5 * zoomRate),
                     new CubicCurveTo(5 * zoomRate, 3.5 * zoomRate, 6.5 * zoomRate, 3.5 * zoomRate,
                           6.2 * zoomRate, 2.5 * zoomRate),
                     new CubicCurveTo(6.2 * zoomRate, 2.5 * zoomRate, 6.6 * zoomRate, 2 * zoomRate,
                           5 * zoomRate, 1.5 * zoomRate),
                     new CubicCurveTo(5 * zoomRate, 1.5 * zoomRate, 3 * zoomRate, 1.5 * zoomRate, 4 * zoomRate,
                           3 * zoomRate),
                     new CubicCurveTo(4 * zoomRate, 3 * zoomRate, 3.5 * zoomRate, 4 * zoomRate, 3 * zoomRate,
                           3 * zoomRate));
         path.setStrokeWidth(0);
         path.setFill(baseColor);
      }

      return root;
   }
}
