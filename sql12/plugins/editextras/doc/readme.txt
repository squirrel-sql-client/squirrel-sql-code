This plugin provides
- Search and replace
- Adding and removing Java quotes
- Formating SQL

The functions are accessable through Menu Session --> SQL Entry Editing --> ...
or through the shortcuts named in the menus.


INSTALLATION HINTS:

The plugin requires JDK 1.4.x 

To build the plugin, edit the plugin-build.xml uncomment the
<property name="p_plugin.work_dir" ...>
<property name="p_plugin.core_libs_dir" ...>
<property name="p_plugin.dist_dir" ... >
and adjust them to your needs.

When running SQuirrel check the plugins dialog (Menu Plugins --> Summary)
to see if there is an entry named "SQL Entry Area Enhancements". If not you have
to copy the plugin jar to "Plugins location" directory that is named at the top
of the dialog.


