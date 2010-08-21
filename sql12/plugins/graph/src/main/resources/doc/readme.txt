This plugin allows you to create graphs of database tables.
To create a graph right mouse click one or more tables in the
object tree and choose the 'add to graph' menu. After that the
session window will have a new tab which contains the table graph.
All functions on the tab are available via rigth mouse clicks on the
tables, lines and the backround panel.

You may have serveral graphs in one session window. If there is
already an existing graph. The 'add to graph' function will popup
a list box first in which you choose if you want to add the table to
an existing graph or to a new graph.

Graphs can be saved via right mouse click on the backround panel.
Saved graphs will be loaded automatically when you open the session
next time.

You may print graphs. To do so choose the the Zoom/Print item in the
right mouse click menu of the graph pane. A tool bar on the bottom of
the pane will appear. The right side slider in the toolbar allows you
to zoom the graph. The left slider allows you to zoom the paper edges
if the paper edges check box is checked. Both sliders enable you to arrange
your Graph on paper sheets in a WYSIWYG way. There are some predefined paper
formats and you are able to configure formats yourself.

INSTALLATION HINTS:

The plugin requires JDK 1.4.x

To build the plugin, edit the plugin-build.xml uncomment the
<property name="p_plugin.work_dir" ...>
<property name="p_plugin.core_libs_dir" ...>
<property name="p_plugin.dist_dir" ... >
and adjust them to your needs.

When running SQuirrel check the plugins dialog (Menu Plugins --> Summary)
to see if there is an entry named "Table graph". If not you have
to copy the plugin jar to "Plugins location" directory that is named at the top
of the dialog.


