package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

import javax.swing.*;
import java.awt.*;

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
	JButton btnChooseEditorCommand;
	JTable tblBundels = new JTable();


	public TranslatorsPanel(PluginResources resources)
	{
		setLayout(new GridLayout(1,1));
		add(tabTranlators);


		JPanel translationsPanel = getTranslationsPanel(resources);
		// i18n[I18n.tranlations=Translations]
		tabTranlators.addTab(s_stringMgr.getString("I18n.tranlations"), translationsPanel);

		// i18n[I18n.info=Info]
		tabTranlators.addTab(s_stringMgr.getString("I18n.info"), new JScrollPane(new MultipleLineLabel(infoText)));


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
		// i18n[I18n.WorkingDir=Working Directory]
		ret.add(new JLabel(s_stringMgr.getString("I18n.EditorCommand")), gbc);

		gbc = new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(txtEditorCommand, gbc);

		btnChooseEditorCommand = new JButton(resources.getIcon("Open"));
		gbc = new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
		ret.add(btnChooseEditorCommand, gbc);

		gbc = new GridBagConstraints(0, 3, 3, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0);
		// i18n[I18n.bundles=Bundles]
		ret.add(new JLabel(s_stringMgr.getString("I18n.bundles")), gbc);

		gbc = new GridBagConstraints(0, 4, 3, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0);
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
			"In the shell or batch files you'll find further details about what to do.";


}
