package net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrameToolBar;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

/**
 * SoundPlayer singleton.
 */
public enum  SoundPlayer
{
   PLAYER;

   private SourceDataLine _sdl;

   /**
    * Will return when sound finished playing or quit() was called from another thread.
    */
   public synchronized void play(File soundFile)
   {
      try
      {
         GUIUtils.processOnSwingEventThread(() -> addQuitSoundToolbarButton());

         if(null == soundFile)
         {
            beep();
         }
         else
         {
            playFile(soundFile);
         }
      }
      finally
      {
         _sdl = null;
         GUIUtils.processOnSwingEventThread(() -> removeQuitSoundToolbarButton());
      }
   }

   private void removeQuitSoundToolbarButton()
   {
      MainFrameToolBar mainFrameToolBar = Main.getApplication().getMainFrame().getMainFrameToolBar();
      mainFrameToolBar.remove(Main.getApplication().getActionCollection().get(QuitSoundAction.class));
   }

   private void addQuitSoundToolbarButton()
   {
      MainFrameToolBar mainFrameToolBar = Main.getApplication().getMainFrame().getMainFrameToolBar();
      mainFrameToolBar.add(Main.getApplication().getActionCollection().get(QuitSoundAction.class));
   }

   private void playFile(File soundFile)
   {
      AudioInputStream in = null;
      try
      {
         in = getAudioInputStream(soundFile);
         AudioFormat outFormat = getOutFormat(in.getFormat());
         DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);

         _sdl = (SourceDataLine) AudioSystem.getLine(info);

         _sdl.open(outFormat);
         _sdl.start();
         stream(getAudioInputStream(outFormat, in), _sdl);
      }
      catch (UnsupportedAudioFileException | LineUnavailableException | IOException e)
      {
         throw new IllegalStateException(e);
      }
      finally
      {
         quit();

         if(null != in)
         {
            try {in.close();} catch (IOException e){}
         }
      }
   }

   private AudioFormat getOutFormat(AudioFormat inFormat)
   {
      final int ch = inFormat.getChannels();
      final float rate = inFormat.getSampleRate();
      return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
   }

   private void stream(AudioInputStream in, SourceDataLine line) throws IOException
   {
      final byte[] buffer = new byte[65536];
      for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length))
      {
         line.write(buffer, 0, n);
      }
   }



   private void beep()
   {
      try
      {
         byte[] buf = new byte[1];
         AudioFormat af =
               new AudioFormat(
                     8000f, // sampleRate
                     8,           // sampleSizeInBits
                     1,           // channels
                     true,        // signed
                     false);      // bigEndian

         _sdl = AudioSystem.getSourceDataLine(af);
         _sdl.open(af);
         _sdl.start();

         int msecs = 1201;
         int hz = 800;
         double vol = 1.0;

         for (int i = 0; i < msecs * 8; i++)
         {
            double angle = i / (8000f / hz) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
            _sdl.write(buf, 0, 1);
         }
         _sdl.stop();
         _sdl.drain();
         _sdl.close();
      }
      catch (LineUnavailableException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   /**
    * @return true if playing sound was stopped.
    */
   public boolean quit()
   {
      GUIUtils.processOnSwingEventThread(() -> removeQuitSoundToolbarButton());

      // _sdl could become null in between
      SourceDataLine sdlBuf = _sdl;

      if(null == sdlBuf)
      {
         return false;
      }

      // Will let play() return.
      //_sdl.drain();
      sdlBuf.stop();
      return true;
   }

   public static void main(String[] args) throws InterruptedException
   {
      PLAYER.play(null);

      Thread.sleep(40000);
   }
}
