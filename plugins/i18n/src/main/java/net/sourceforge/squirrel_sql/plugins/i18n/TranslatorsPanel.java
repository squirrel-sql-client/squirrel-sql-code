package net.sourceforge.squirrel_sql.plugins.i18n;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TranslatorsPanel extends JPanel
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TranslatorsPanel.class);

	JTabbedPane tabTranlators = new JTabbedPane();


	JComboBox cboLocales = new JComboBox();
	// i18n[I18n.loadBundles=Load bundles]
	JButton btnLoad = new JButton(s_stringMgr.getString("I18n.loadBundles"));

	JTextField txtWorkingDir = new JTextField();
	JButton btnChooseWorkDir;

	JTextField txtEditorCommand = new JTextField();
	JButton btnChooseNativeToAsciiCommand;

   JTextField txtNativeToAsciiCommand = new JTextField();
   JButton btnChooseEditorCommand;

   JTextField txtNativeToAsciiOutDir = new JTextField();
   JButton btnChooseNativeToAsciiOutDir;


   JTable tblBundels = new JTable();

   JCheckBox cbxIncludeTimestamp = null;
   
   private static final String PREF_KEY_INCLUDE_TIMESTAMP = "SquirrelSQL.i18n.includeTimestamp";

   public TranslatorsPanel(PluginResources resources)
   {
      setLayout(new GridLayout(1,1));
      add(tabTranlators);


      JPanel translationsPanel = getTranslationsPanel(resources);
      // i18n[I18n.tranlations=Translations]
      tabTranlators.addTab(s_stringMgr.getString("I18n.tranlations"), translationsPanel);

      // i18n[I18n.info=Info]
      tabTranlators.addTab(s_stringMgr.getString("I18n.info"), new JScrollPane(new MultipleLineLabel(infoText)));


      String includeTimestamp = 
          Preferences.userRoot().get(PREF_KEY_INCLUDE_TIMESTAMP, "true");
      
      cbxIncludeTimestamp.setSelected(includeTimestamp.equals("true"));
   }

	private JPanel getTranslationsPanel(PluginResources resources)
	{
		JPanel ret = new JPanel();

		GridBagConstraints gbc;

		ret.setLayout(new GridBagLayout());

		gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
		// i18n[I18n.locales=Locales]
		ret.add(new JLabel(s_stringMgr.getString("I18n.locales")), gbc);

		gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(cboLocales, gbc);

		gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(btnLoad, gbc);


		gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
		// i18n[I18n.WorkingDir=Working Directory]
		ret.add(new JLabel(s_stringMgr.getString("I18n.WorkingDir")), gbc);

		gbc = new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(txtWorkingDir, gbc);

		btnChooseWorkDir = new JButton(resources.getIcon("Open"));
		gbc = new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(btnChooseWorkDir, gbc);



		gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
		// i18n[I18n.EditorCommand=Editor command]
		ret.add(new JLabel(s_stringMgr.getString("I18n.EditorCommand")), gbc);

      gbc = new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(txtEditorCommand, gbc);

		btnChooseEditorCommand = new JButton(resources.getIcon("Open"));
		gbc = new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(btnChooseEditorCommand, gbc);



      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      // i18n[I18n.NativeToAscii=Path to native to Unicode converter (JDK's native2ascii)]
      ret.add(new MultipleLineLabel(s_stringMgr.getString("I18n.NativeToAscii")), gbc);

      gbc = new GridBagConstraints(1, 3, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 5), 0, 0);
      ret.add(txtNativeToAsciiCommand, gbc);

      btnChooseNativeToAsciiCommand = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
      ret.add(btnChooseNativeToAsciiCommand, gbc);


      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0);
      // i18n[I18n.NativeToAsciiOutDir=native2Ascii output dir]
      ret.add(new JLabel(s_stringMgr.getString("I18n.NativeToAsciiOutDir")), gbc);

      gbc = new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0);
      ret.add(txtNativeToAsciiOutDir, gbc);

      btnChooseNativeToAsciiOutDir = new JButton(resources.getIcon("Open"));
      gbc = new GridBagConstraints(2, 4, 1, 1, 0, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
      ret.add(btnChooseNativeToAsciiOutDir, gbc);

      gbc = new GridBagConstraints(1,5,1,1,0,0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      // i18n[I18n.includeTimestamp=Include timestamp in modified properties file]
      cbxIncludeTimestamp = new JCheckBox(s_stringMgr.getString("I18n.includeTimestamp"));
      ret.add(cbxIncludeTimestamp, gbc);


/*      gbc = new GridBagConstraints(1,5,1,1,0,0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,5),0,0);
      // i18n[I18n.includeTimestamp=Include timestamp in modified properties file]
      JLabel timestampLabel = new JLabel(s_stringMgr.getString("I18n.includeTimestamp"));
      ret.add(timestampLabel, gbc);
*/      
      
      gbc = new GridBagConstraints(0, 6, 3, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
		// i18n[I18n.bundles=Bundles]
		ret.add(new JLabel(s_stringMgr.getString("I18n.bundles")), gbc);

		gbc = new GridBagConstraints(0, 7, 3, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(new JScrollPane(tblBundels), gbc);

		return ret;
	}


	private String infoText =
		"How translation works:\n" +
			"1. Choose the locale you wish to translate for. A Locale at least consist of a language part like en (English). " +
			"It may also consist of a country part like en_us (English _ United States). In most cases your translation won't be " +
			"country depended. In this case you choose a locale that only consists of a language part.\n" +
			"\n" +
			"2. Click the 'Load bundles' button to fill the 'Bundles' table. This will give you an overview how many translations are to be done.\n" +
			"\n" +
			"3. Choose a working directory where SQuirreL should generate the templates for your translations. When loading the 'Bundles' table SQuirreL looks in this directory to find out which translations have already been done.\n" +
			"\n" +
			"4. Optionally choose an edtior command. This will allow you to open the files where you do your translations via right mouse menu of the bundles table.\n" +
			"\n" +
			"5. Before you can start translating you must use the right mouse menu in the 'Bundles' table to generate " +
			"a translation template file. The generated template file consist of entries like this:\n" +
			"#button.add.title=Add\n" +
			"#button.add.title=\n" +
			"which you turn into an entry of this kind:\n" +
			"#button.add.title=Add\n" +
			"button.add.title=<Your translation here>\n" +
			"After this you might click the load bundles button again to see the number of tranlations to do decrease.\n" +
			"\n" +
			"6. To see your translations on next SQuirreL restart you need to:\n" +
			"i. Edit the class path in your SQuirreL start batch or shell file to include your working directory.\n" +
			"ii. If your machine is running with locale different from the one you are translating to edit your your SQuirreL start batch or shell to pass the right country to your Java Virtual Machine.\n" +
			"In the shell or batch files you'll find further details about what to do.\n\n" +
			"7. For non-latin1 encodings use the native2ascii tool included in JDK 1.4.x. For details see:\n" +
			"http://java.sun.com/j2se/1.4.2/docs/tooldocs/windows/native2ascii.html\n\n" +
			"8. For other translation issues or any further questions see our developer mailing list at " +
         "squirrel-sql-develop@lists.sourceforge.net";


}
