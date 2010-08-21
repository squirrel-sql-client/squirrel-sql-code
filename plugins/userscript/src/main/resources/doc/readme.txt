This plugin provides an easy way to extend Squirrel with little
Java programms called user scripts. For example you might
- write user scripts for code generation
- write user scripts for to generate sql scripts

The functions are accessable through right click in the object tree.
To execute a user script for an SQL statement, mark the statement than
go to menu Session --> User Scrpts --> Execute User Script on SQL.

To write a user script choose the execute user script menu item either in
the object tree or the Session menu. In the dialog choose
"Generate script template". When you have written and compiled your script
(which is just a normal Java class) you use the same dialog to make the script
available in SQuirreL.


INSTALLATION HINTS:

The plugin requires JDK 1.4.x

To build the plugin, edit the plugin-build.xml uncomment the
<property name="p_plugin.work_dir" ...>
<property name="p_plugin.core_libs_dir" ...>
<property name="p_plugin.dist_dir" ... >
and adjust them to your needs.

When running SQuirrel check the plugins dialog (Menu Plugins --> Summary)
to see if there is an entry named "User Scripts Plugin". If not you have
to copy the plugin jar to "Plugins location" directory that is named at the top
of the dialog.


