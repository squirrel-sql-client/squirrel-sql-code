This plugin provides code completion
hit ctrl+space in the SQL Editor to perform code completion

Through code completion you can call join generation functions.
These functions' names start with '#'. If you want to see which functions
exist just type # and ctrl+space. Select the function you would like
to try from the code completion popup list. Code completion will generate
a template for the function call. Adjust the template to your needs and position
the cursor at the end of the template. Then hit ctrl+space again and see
what happens.



INSTALLATION HINTS:

The plugin requires JDK 1.4.x

To build the plugin, edit the plugin-build.xml and adjust
<property name="p_plugin.work_dir" ...>
<property name="p_plugin.core_libs_dir" ...>
<property name="p_plugin.dist_dir" ... >
to your needs.

When running SQuirrel check the plugins dialog (Menu Plugins --> Summary)
to see if there is an entry named "SQL Entry Area Enhancements". If not you have
to copy the plugin jar to "Plugins location" directory that is named at the top
of the dialog.


