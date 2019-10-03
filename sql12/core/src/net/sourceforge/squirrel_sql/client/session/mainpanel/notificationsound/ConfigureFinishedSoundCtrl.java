package net.sourceforge.squirrel_sql.client.session.mainpanel.notificationsound;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigureFinishedSoundCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ConfigureFinishedSoundCtrl.class);

   private static final ILogger s_log = LoggerController.createLogger(ConfigureFinishedSoundCtrl.class);


   private static final String PREF_LAST_SOUND_FILE_DIR = "ConfigureFinishedSoundCtrl.last.sound.file.dir";

   private final ConfigureFinishedSoundDlg _dlg;
   private boolean _adjustingTestSound = false;

   public ConfigureFinishedSoundCtrl(JComponent parentComp)
   {
      _dlg = new ConfigureFinishedSoundDlg(parentComp);

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      _dlg.chkPLaySoundAfter.setSelected(SoundConfigDAO.isPlayNotification());
      _dlg.chkPLaySoundAfter.addActionListener(e -> onchkPLaySound());
      onchkPLaySound();

      _dlg.txtTime.setText("" + SoundConfigDAO.getPlayNotificationAfter());

      _dlg.cboTimeUnit.setSelectedItem(SoundConfigDAO.getTimeUnit());

      _dlg.btnChooseSoundToPlay.addActionListener(e -> onChooseSoundFile());

      _dlg.txtSoundFile.setText(SoundConfigDAO.getSoundFileAsString());


      _dlg.btnTestSound.addActionListener(e -> onTestSound());

      _dlg.setVisible(true);
   }

   private void onChooseSoundFile()
   {
      JFileChooser fc = new JFileChooser(Props.getString(PREF_LAST_SOUND_FILE_DIR, System.getProperty("user.home")));

      for (FileFilter choosableFileFilter : fc.getChoosableFileFilters())
      {
         fc.removeChoosableFileFilter(choosableFileFilter);
      }

      FileExtensionFilter mp3Filter = new FileExtensionFilter("MP3 files", new String[]{".mp3"});
      fc.addChoosableFileFilter(mp3Filter);

      FileExtensionFilter oggFilter = new FileExtensionFilter("OGG files", new String[]{".ogg"});
      fc.addChoosableFileFilter(oggFilter);

      FileExtensionFilter wavFilter = new FileExtensionFilter("WAV files", new String[]{".wav"});
      fc.addChoosableFileFilter(wavFilter);

      fc.setFileFilter(mp3Filter);

      int returnVal = fc.showOpenDialog(_dlg);

      if (null != fc.getCurrentDirectory())
      {
         Props.putString(PREF_LAST_SOUND_FILE_DIR, fc.getCurrentDirectory().getPath());
      }

      String soundFilePath = null;
      if (returnVal == JFileChooser.APPROVE_OPTION)
      {
         soundFilePath = fc.getSelectedFile().getAbsolutePath();
         //Props.putString(PREF_LAST_SOUND_FILE_DIR, fc.getSelectedFile().getParent());

      }
      _dlg.txtSoundFile.setText(soundFilePath);
   }

   private void onTestSound()
   {
      if(_adjustingTestSound)
      {
         return;
      }

      try
      {
         _adjustingTestSound = true;

         if(SoundPlayer.PLAYER.quit())
         {
            _dlg.btnTestSound.setSelected(false);
            return;
         }

         if(StringUtilities.isEmpty(_dlg.txtSoundFile.getText(), true))
         {
            _dlg.btnTestSound.setSelected(false);
            JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("ConfigureFinishedSoundCtrl.no.sound.file"));
            return;
         }

         Path path = Paths.get(_dlg.txtSoundFile.getText());

         if(false == Files.isRegularFile(path) || false == Files.exists(path))
         {
            _dlg.btnTestSound.setSelected(false);
            JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("ConfigureFinishedSoundCtrl.sound.file.does.not.exist"));
            return;
         }

         _dlg.btnTestSound.setSelected(true);

         Thread t = new Thread(() -> playSound(path));
         t.start();
      }
      finally
      {
         _adjustingTestSound = false;
      }
   }

   private void playSound(Path path)
   {
      try
      {
         SoundPlayer.PLAYER.play(path.toFile());
      }
      catch (Throwable e)
      {
         s_log.error("Error testing execution finished sound", e);
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("FinishedNotificationSoundHandler.error.testing.finished.sound", e.getMessage()));
      }
      finally
      {
         SwingUtilities.invokeLater(() -> deselectTestButton());
      }
   }

   private void deselectTestButton()
   {
      try
      {
         _adjustingTestSound = true;
         if(_dlg.btnTestSound.isSelected())
         {
            _dlg.btnTestSound.setSelected(false);
         }
      }
      finally
      {
         _adjustingTestSound = false;
      }
   }

   private void onchkPLaySound()
   {
      _dlg.txtTime.setEnabled(_dlg.chkPLaySoundAfter.isSelected());
      _dlg.cboTimeUnit.setEnabled(_dlg.chkPLaySoundAfter.isSelected());
   }

   private void onOk()
   {
      SoundConfigDAO.setPlayNotification(_dlg.chkPLaySoundAfter.isSelected());
      SoundConfigDAO.setPlayNotificationAfter(_dlg.txtTime.getInt());
      SoundConfigDAO.setTimeUnit(((TimeUnit) _dlg.cboTimeUnit.getSelectedItem()));

      SoundConfigDAO.writeSoundFile(_dlg.txtSoundFile.getText());


      close();
   }

   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

}
