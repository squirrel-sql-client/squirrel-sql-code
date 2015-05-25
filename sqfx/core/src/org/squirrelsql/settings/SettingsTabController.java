package org.squirrelsql.settings;

import javafx.scene.control.Tab;

public interface SettingsTabController
{
   void setSettingsContext(SettingsContext settingsContext);

   Tab getTab();

   void saveSettings();
}
