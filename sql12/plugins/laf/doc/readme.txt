Look and Feel Plugin
====================
This plugin allows you to select a Look and Feel other than the default
Java one. It also allows you to specify the fonts for the different GUI
elements. As well as allowing you to use the default Look and Feels that
come with your Java environment allowing, it ships with the following
Look and Feel implementations:

Kunststoff 2.0.1 - http://www.incors.org
Metouia 1.0beta - http://mlf.sourceforge.net
Oyoaha 3.0rc1 - http://www.oyoaha.com
Plastic 1.0.9 - http://www.jgoodies.com
Skin 1.2.2 - http://www.l2fprod.com

If you have other Look and Feel implementations that you would like to
use with SQuirreL drop the jar files into the directory

~/.squirrel-sql/plugins/laf/extralafs

where ~ is your home directory. This directory will be created (if it
doesn't exist) the first time you run SQuirreL with the Look and Feel
plugin installed. Then use a text editor to enter information about the
new Look and Feel in the file

~/.squirrel-sql/plugins/laf/extralafs/extralafs.properties

Each new Look and Feel should have a line in extralafs.properties
classnameX=LookAndFeelClassname which specifies the class name of the
Look and Feel (consult the documentation that came with your new Look
and Feel for this information) and an line jarX=JarFIleName which
specifies the name of the jar (or zip) file that contains the Look and
Feel. Replace X with 0 for the first Look and Feel specified, 1 for the
second etc.

Here is an example:

classname0=com.incors.plaf.kunststoff.KunststoffLookAndFeel
jar0=kunststoff.jar

Both the Skin and Oyoaha Look and Feels allow the use of "skins". Skin
Look and Feel themepacks should be placed in the
plugins/laf/skinlf-theme-packs directory within the SQuirreL program
directory and Oyoaha themes should be placed in the directory
plugins/laf/oyoaha-theme-packs.

