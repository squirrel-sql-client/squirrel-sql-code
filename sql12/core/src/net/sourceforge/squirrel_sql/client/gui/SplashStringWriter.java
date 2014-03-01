package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.*;

public class SplashStringWriter
{
   private final static ILogger s_log = LoggerController.createLogger(SplashStringWriter.class);


   private SplashScreen _splashScreen;
   private boolean _hasTwoLines;
   private int _maxNumberOffCallsToWriteUpperLine;
   private static final Font FONT = new Font(Font.DIALOG, Font.BOLD, 14);
   private static final Color BG = new Color(171, 176, 195);
   private static final Color FG_UPPER = Color.black;
   private static final Color FG_PROGRESS = new Color(74, 91, 153);
   private static final Color FG_LOWER = new Color(74, 91, 153);
   private static final int X_DIST = 10;
   private static final int Y_DIST = 12;
   private static final int X_PROGRESSBAR = X_DIST - 3;

   private Graphics2D _graphics;

   private int _yUpper;
   private int _yLower;
   private int _numberOffCallsToWriteUpperLine;
   private int _yProgressbar;
   private int _maxWidhtProgressbar;
   private int _heightProgressbar;
   private String _lowerLine;
   private String _upperLine;
   private int _paintAreaHeight;


   public SplashStringWriter(SplashScreen splashScreen, boolean hasTwoLines, int maxNumberOffCallsToWriteUpperLine)
   {
      _splashScreen = splashScreen;
      _hasTwoLines = hasTwoLines;
      _maxNumberOffCallsToWriteUpperLine = maxNumberOffCallsToWriteUpperLine;

      _graphics = _splashScreen.createGraphics();

      _graphics.setFont(FONT);


      _yLower = _splashScreen.getSize().height - Y_DIST;

      FontMetrics fontMetrics = _graphics.getFontMetrics(FONT);

      if (_hasTwoLines)
      {
         _yUpper = _yLower - fontMetrics.getHeight()  - Y_DIST;
         _paintAreaHeight = 2 * (Y_DIST + fontMetrics.getHeight());
      }
      else
      {
         _yUpper = _yLower;
         _paintAreaHeight = Y_DIST + fontMetrics.getHeight();
      }

      _yProgressbar = _yUpper - fontMetrics.getHeight() - 2;

      _maxWidhtProgressbar = _splashScreen.getSize().width - 2 * X_DIST;

      _heightProgressbar = fontMetrics.getHeight() + 10;



      _graphics.setColor(FG_UPPER);

      paintCopyrigthAndVersion(fontMetrics);
   }

   private void paintCopyrigthAndVersion(FontMetrics fontMetrics)
   {
      String[] splits = Version.getCopyrightStatement().split("\\n");

      int xVers = (_splashScreen.getSize().width - fontMetrics.getStringBounds(Version.getVersion(), _graphics).getBounds().width) / 2;
      int yVers = _splashScreen.getSize().height - (_paintAreaHeight + ((splits.length + 1) * (fontMetrics.getHeight() + 5)));
      _graphics.drawString(Version.getVersion(), xVers, yVers);


      for (int i = 0; i < splits.length; i++)
      {
         int xSpilt = (_splashScreen.getSize().width - fontMetrics.getStringBounds(splits[i], _graphics).getBounds().width) / 2;
         int ySplit = _splashScreen.getSize().height - (_paintAreaHeight + ((splits.length - i) * (fontMetrics.getHeight() + 5)));
         _graphics.drawString(splits[i], xSpilt, ySplit);
      }
   }

   public void writeUpperProgressLine(String s)
   {
      _upperLine = s;
      ++_numberOffCallsToWriteUpperLine;
      paint();
   }

   public void writeLowerProgressLine(String s)
   {
      _lowerLine = s;
      paint();
   }


   private void paint()
   {

      clear();

      paintProgress();

      paintStrings();
   }

   private void paintStrings()
   {
      write(_upperLine, _yUpper, FG_UPPER);

      if(_hasTwoLines)
      {
         write(_lowerLine, _yLower, FG_LOWER);
      }
   }

   private void paintProgress()
   {
      if(_maxNumberOffCallsToWriteUpperLine < _numberOffCallsToWriteUpperLine + 1)
      {
         String msg = "Programmer: Please increase _maxNumberOffCallsToWriteUpperLine to make the Progressbar work right";
         s_log.error(msg, new IllegalStateException(msg));

         _numberOffCallsToWriteUpperLine = _maxNumberOffCallsToWriteUpperLine;
      }

      int width = (int)
            (
                  (double)(_maxWidhtProgressbar) * ((double)(_numberOffCallsToWriteUpperLine)) / ((double)(_maxNumberOffCallsToWriteUpperLine))
            );

      _graphics.setColor(FG_PROGRESS);
      _graphics.fillRect(X_PROGRESSBAR, _yProgressbar, width,  _heightProgressbar);
   }

   private void clear()
   {
      _graphics.setColor(BG);
      _graphics.fillRect(0, _splashScreen.getSize().height - _paintAreaHeight, _splashScreen.getSize().width, _paintAreaHeight);
   }


   private String write(String s, int y, Color fg)
   {
      if (null != s)
      {
         _graphics.setColor(fg);
         _graphics.drawString(s, X_DIST, y);
         _splashScreen.update();
      }

      return s;
   }
}
