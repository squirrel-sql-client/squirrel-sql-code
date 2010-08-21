/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel.controlpanel;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.MenuElement;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.PanelUI;
import javax.swing.plaf.ScrollPaneUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import de.muntjak.tinylookandfeel.SpecialUIButton;
import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.ThemeDescription;
import de.muntjak.tinylookandfeel.TinyLabelUI;
import de.muntjak.tinylookandfeel.TinyLookAndFeel;
import de.muntjak.tinylookandfeel.TinyMenuItemUI;
import de.muntjak.tinylookandfeel.TinyPopupFactory;
import de.muntjak.tinylookandfeel.TinyTitlePane;
import de.muntjak.tinylookandfeel.TinyUtils;
import de.muntjak.tinylookandfeel.TinyWindowButtonUI;
import de.muntjak.tinylookandfeel.borders.TinyFrameBorder;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.ListCP.FakeList;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.TableCP.FakeTable;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.ToolTipCP.DisabledToolTip;
import de.muntjak.tinylookandfeel.controlpanel.ControlPanel.ToolTipCP.EnabledToolTip;
import de.muntjak.tinylookandfeel.util.BooleanReference;
import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.ColoredFont;
import de.muntjak.tinylookandfeel.util.DrawRoutines;
import de.muntjak.tinylookandfeel.util.HSBReference;
import de.muntjak.tinylookandfeel.util.IntReference;
import de.muntjak.tinylookandfeel.util.SBReference;

/**
 * ControlPanel
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class ControlPanel {
	
	public static ControlPanel instance;
	public JFrame theFrame;
	private JPanel thePanel;
	
	private static final String WINDOW_TITLE = "TinyLaF " +
		TinyLookAndFeel.VERSION_STRING + " Controlpanel";
	
	private static final String YQ_THEME = "YQ Theme";
	private static final FileFilter fileFilter = new ThemeFileFilter();
	private static ActionListener selectThemeAction;
	
	private static final int PLAIN_FONT 	= 1;
	private static final int BOLD_FONT 		= 2;
	private static final int SPECIAL_FONT 	= 3;
	
	// control modes
	static final int CONTROLS_BUTTON 					= 1;
	static final int CONTROLS_COMBO 					= 2;
	static final int CONTROLS_ACTIVE_FRAME_CAPTION 		= 3;
	static final int CONTROLS_INACTIVE_FRAME_CAPTION 	= 4;
	static final int CONTROLS_WINDOW_BUTTON 			= 5;
	static final int CONTROLS_ICON 						= 6;
	static final int CONTROLS_LIST 						= 7;
	static final int CONTROLS_MENU 						= 8;
	static final int CONTROLS_PROGRESSBAR 				= 9;
	static final int CONTROLS_SCROLLBAR 				= 10;
	static final int CONTROLS_SLIDER 					= 11;
	static final int CONTROLS_SPINNER 					= 12;
	static final int CONTROLS_SPLITPANE 				= 13;
	static final int CONTROLS_TABBED_PANE 				= 14;
	static final int CONTROLS_TABLE 					= 15;
	static final int CONTROLS_TEXT 						= 16;
	static final int CONTROLS_TOOLBAR 					= 17;
	static final int CONTROLS_TOOL_TIP 					= 18;
	static final int CONTROLS_TREE 						= 19;
	static final int CONTROLS_FONT 						= 20;
	static final int CONTROLS_FRAME_BORDER 				= 21;
	static final int CONTROLS_ALL 						= 22;
	static final int CONTROLS_NONE 						= 23;
	
	// All available themes, set in createThemesMenu()
	private static ThemeDescription[] themes;
	
	private static String directoryPath = TinyUtils.getSystemProperty("user.dir");
	
	private static final Color INFO_COLOR = new Color(236, 249, 255);
	private static final Border INFO_BORDER = BorderFactory.createCompoundBorder(
		BorderFactory.createLineBorder(new Color(108, 108, 147)),
		BorderFactory.createEmptyBorder(3, 3, 3, 3));
	
	private static final Insets insets0404 = new Insets(0, 4, 0, 4);
	private static final Insets insets2404 = new Insets(2, 4, 0, 4);
	private static final Insets insets4404 = new Insets(4, 4, 0, 4);
	private static final Insets insets0804 = new Insets(0, 8, 0, 4);
	private static final Insets insets2804 = new Insets(2, 8, 0, 4);
	private static final Insets insets4804 = new Insets(4, 8, 0, 4);
	
	private static final int menuShortcutKeyMask =
		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	public static Selection selection;
	private static ParameterSet copies;
	
	// different targets to repaint
	private Map components = new HashMap();
	private static Component[] windowButtons;
	private Component[] internalFrames, menus;
	
	private String currentFileName;
	private ThemeDescription currentThemeDescription;
	private final MouseListener progressBarAction= new ProgressBarAction();
	private final ChangeListener updateAction = new UpdateAction();
	
	private boolean resistUpdate = false;
	
	// for magnifier
	private static MagnifierPanel magnifierPanel;
	private static BufferedImage magnifierImg;
	private static int scaleFactor = 4;	// 4:1
	private static boolean magnifierActive = true;

	private static Icon copyIcon, pasteIcon, pasteDisabledIcon;
	private PopupTrigger popupTrigger;
	private ExamplePanel.ExampleDesktopPane desktopPane;
	private JTree tree1, tree2;
	private JScrollPane sp1, sp2;
	private JToolBar theToolBar;
	private JMenuItem saveItem;
	private JMenuItem undoItem, redoItem;
	JMenuItem copyItem, pasteItem;
	private JMenu themesMenu;
	private JTabbedPane mainTab, compTab;
	JButton applySettingsButton;
	private FontPanel plainFontPanel, boldFontPanel, specialFontPanel;
	private JComboBox fontCombo;
	private JRadioButton isPlainFont, isBoldFont;
	private FrameCPsPanel framesCP;
	// There is exactly one selected (special) font at each time
	private ColoredFont selectedFont;
	ExamplePanel examplePanel;
	JList exampleList;
	private JButton exampleButton, exampleDisabledButton;
	private JToggleButton exampleToggleButton;
	private Icon buttonIcon;
	private JPopupMenu thePopup;
	private JInternalFrame internalFrame, palette;
	private JPopupMenu hsbPopup, sbPopup, cpSBPopup, cpHSBPopup;
	private JMenuItem pasteSBParametersItem;
	private JMenuItem pasteHSBParametersItem;
	private SBReference copiedSBReference;
	private HSBReference copiedHSBReference;
	private ButtonsCP buttonsCP;
	private ScrollBarCP scrollsCP;
	private SeparatorCP separatorCP;
	private TabbedPaneCP tabsCP;
	private ComboCP comboCP;
	private MenuCP menuCP;
	private ListCP listCP;
	private Component fakedList;
	private SliderCP sliderCP;
	private SpinnerCP spinnerCP;
	private ProgressCP progressCP;
	private TextCP textCP;
	private TreeCP treeCP;
	private ToolBarCP toolCP;
	private TableCP tableCP;
	JTable exampleTable;
	private FakeList fakeList;
	private FakeTable fakeTable;
	private JLabel focusedCellLabel;
	private JLabel focusedItemLabel;
	// focusedState is evaluated by fake list and fake table
	private boolean focusedState = true;
	private FrameCP frameCP;
	private FrameButtonsCP frameButtonsCP;
	private FrameCloseButtonCP frameCloseButtonCP;
	private static JCheckBox decoratedFramesCheck;
	private InactiveFramePanel disabledFramePanel;
	private IconCP iconCP;
	private ToolTipCP tipCP;
	private MiscCP miscCP;
	private JSlider slider1, slider2, slider3, slider4, slider5, slider6;	
	private SBControl selectedSBControl;
	private SBControl mainField, rollField, backField, frameField,
	sub1Field, sub2Field, sub3Field, sub4Field,
	sub5Field, sub6Field, sub7Field, sub8Field;
	private HSBControl selectedHSBControl;
	
//	buttonCP
	private SBControl buttonNormalBg, buttonRolloverBg, buttonPressedBg, buttonDisabledBg;
	private SBControl buttonBorder;
	private SBControl buttonRollover, buttonDefault, buttonCheck, buttonCheckDisabled;
	private SBControl buttonDisabledBorder;
	private SBControl buttonDisabledFg, checkDisabledFg, radioDisabledFg;
	private SBControl toggleSelectedBg;
	private SpreadControl buttonSpreadLight, buttonSpreadLightDisabled;
	private SpreadControl buttonSpreadDark, buttonSpreadDarkDisabled;
	
//	textCP
	private SBControl textBg, textSelectedBg, textDisabledBg, textNonEditableBg;
	private SBControl textBorder, textBorderDisabled, textCaret;
	private SBControl textText, textSelectedText;
	
//	comboCP
	private SBControl comboBg, comboText;
	private SBControl comboBorder, comboBorderDisabled, comboSelectedBg;
	private SBControl comboArrowField, comboArrowDisabled;
	private SBControl comboButtonBg, comboButtonRollover, comboButtonPressed, comboButtonDisabled;
	private SBControl comboButtonBorder;
	private SBControl comboButtonBorderDisabled;
	private SBControl comboSelectedText;
	private SpreadControl comboSpreadLight, comboSpreadLightDisabled;
	private SpreadControl comboSpreadDark, comboSpreadDarkDisabled;
	
//	menuCP
	private SBControl menuRolloverBg, menuSeparator;
	private SBControl menuRolloverFg, menuDisabledFg;
	private SBControl menuBar, menuItemRollover, menuPopup;
	private SBControl menuBorder;
	private SBControl menuInnerHilight, menuInnerShadow, menuOuterHilight, menuOuterShadow;
	private SBControl menuIcon, menuIconRollover, menuIconDisabled;	
	private SBControl menuItemSelectedText, menuItemDisabledText;
	
//	listCP
	private SBControl listBg, listText;
	private SBControl listSelectedBg, listSelectedText;
	private SBControl listFocusBorder;
	
//	tabsCP
	private SBControl tabNormalBg, tabSelectedBg, tabRoll;
	private SBControl tabDisabled, tabDisabledSelected, tabDisabledText;
	private SBControl tabBorder, tabPaneBorder;
	private SBControl tabDisabledBorder, tabPaneDisabledBorder;
	
//	scrollsCP
	private SBControl scrollThumbField, scrollButtField, scrollArrowField, trackField,
	scrollThumbRolloverBg, scrollThumbPressedBg, scrollThumbDisabledBg,
	scrollButtRolloverBg, scrollButtPressedBg, scrollButtDisabledBg,
	trackDisabled, trackBorder, trackBorderDisabled, scrollArrowDisabled,
	scrollGripDark, scrollGripLight, scrollPane,
	scrollBorder, scrollLight,
	scrollBorderDisabled, scrollLightDisabled;
	
	private IntControl scrollSizeControl;
	
	SpreadControl scrollSpreadLight, scrollSpreadLightDisabled;
	SpreadControl scrollSpreadDark, scrollSpreadDarkDisabled;
	
//	sliderCP
	private SBControl sliderThumbRolloverBg, sliderThumbPressedBg, sliderThumbDisabledBg;
	private SBControl sliderBorder, sliderDark, sliderLight, sliderThumbField;
	private SBControl sliderDisabledBorder;
	private SBControl sliderTrack, sliderTrackBorder, sliderTrackDark, sliderTrackLight;
	private SBControl sliderTick, sliderTickDisabled, sliderFocusColor;
	
//	spinnerCP
	private SBControl spinnerButtField, spinnerArrowField;
	private SBControl spinnerButtRolloverBg, spinnerButtPressedBg, spinnerButtDisabledBg;
	private SBControl spinnerBorder, spinnerDisabledBorder;
	private SBControl spinnerArrowDisabled;
	
	SpreadControl spinnerSpreadLight, spinnerSpreadLightDisabled;
	SpreadControl spinnerSpreadDark, spinnerSpreadDarkDisabled;
	
//	progressCP
	private Timer progressTimer;
	private JProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
	private SBControl progressField, progressTrack;
	private SBControl progressBorder, progressDark, progressLight;
	private SBControl progressSelectFore, progressSelectBack;
	
//	treeCP
	private SBControl treeBg, treeTextBg, treeSelectedBg, treeText;
	private SBControl treeSelectedText, treeLine;
	
//	toolCP
	private SBControl toolBar, toolBarDark, toolBarLight;
	private SBControl toolButt, toolButtRollover,
	toolButtPressed, toolButtSelected;
	private SBControl toolBorder, toolBorderPressed,
	toolBorderRollover, toolBorderSelected;
	private SBControl toolGripDark, toolGripLight;
	private SBControl toolSeparator;
	
//	frameCP
	private SBControl frameCaption, frameCaptionDisabled;
	private SBControl frameBorder, frameLight;
	private SBControl frameBorderDisabled, frameLightDisabled;
	private SBControl frameTitle, frameTitleShadow, frameTitleDisabled;
	private SBControl frameButt, frameButtRollover, frameButtPressed, frameButtDisabled;
	SpreadControl frameButtSpreadLight, frameButtSpreadLightDisabled;
	SpreadControl frameButtSpreadDark, frameButtSpreadDarkDisabled;
	private SBControl frameButtClose, frameButtCloseRollover, frameButtClosePressed, frameButtCloseDisabled;
	SpreadControl frameButtCloseSpreadLight, frameButtCloseSpreadLightDisabled;
	SpreadControl frameButtCloseSpreadDark, frameButtCloseSpreadDarkDisabled;
	private SBControl frameButtBorder, frameButtBorderDisabled;
	private SBControl frameButtCloseBorder, frameButtCloseBorderDisabled;
	private SBControl frameSymbol, frameSymbolPressed, frameSymbolDisabled;
	private SBControl frameSymbolDark, frameSymbolLight;
	private SBControl frameSymbolDarkDisabled, frameSymbolLightDisabled;
	private SBControl frameSymbolClose, frameSymbolClosePressed, frameSymbolCloseDisabled;
	private SBControl frameSymbolCloseDark, frameSymbolCloseDarkDisabled;
	
	SpreadControl frameSpreadDark, frameSpreadLight, frameSpreadDarkDisabled, frameSpreadLightDisabled;
	
//	iconCP
	private ColorizeIconCheck[] iconChecks = new ColorizeIconCheck[20];
	private HSBControl[] hsb = new HSBControl[20];
	
//	tableCP
	private SBControl tableBack, tableGrid;
	private SBControl tableHeaderBack, tableHeaderRolloverBack,
	tableHeaderRollover, tableHeaderArrow;
	private SBControl tableSelectedBack, tableSelectedFore, tableFocusBorder;
	private SBControl tableAlternateRow;
	private SBControl tableBorderDark, tableBorderLight;
	private SBControl tableHeaderDark, tableHeaderLight;
	
//	separatorCP
	private SBControl separator;
	
//	tipCP
	private SBControl tipBg, tipBorder, tipBgDis, tipBorderDis, tipText, tipTextDis;
	private EnabledToolTip enabledToolTip;
	private DisabledToolTip disabledToolTip;
	
//	miscCP
	private SBControl titledBorderColor, textPaneBg, editorPaneBg, desktopPaneBg;
	private SBControl splitPaneButtonColor;
	
	public ControlPanel() {
		instance = this;
		selection = Selection.getSelection(this);

		createFrame();
		
		//showUIVariables("TableHeader");
		//showUIValues("167");
		//showSystemProperties();
	}
	
	private void analyzeComponent(Component c) {
		Object key = null;
		
		if(c instanceof JList) {
			key = JList.class;
		}
		else if(c instanceof JProgressBar) {
			key = JProgressBar.class;
		}
		else if(c instanceof JButton) {
			key = JButton.class;
		}
		else if(c instanceof JToggleButton) {
			Container parent = c.getParent();
			
			if(!(parent instanceof JToolBar)) {
				key = JButton.class;
			}
		}
		else if(c instanceof JCheckBox) {
			key = JButton.class;
		}
		else if(c instanceof JRadioButton) {
			key = JButton.class;
		}
		else if(c instanceof JComboBox) {
			key = JComboBox.class;
		}
		else if(c instanceof JScrollBar) {
			key = JScrollBar.class;
		}
		else if(c instanceof JSpinner) {
			key = JSpinner.class;
		}
		else if(c instanceof JSlider) {
			key = JSlider.class;
		}
		else if(c instanceof JTabbedPane) {
			key = JTabbedPane.class;
		}
		else if(c instanceof JTable) {
			key = JTable.class;
		}
		else if(c instanceof JTextComponent) {
			key = JTextComponent.class;
		}
		else if(c instanceof JToolBar) {
			key = JToolBar.class;
		}
		else if(c instanceof JTree) {
			key = JTree.class;
		}
		
		if(key != null) {
			Object value = components.get(key);
			
			if(value == null) {
				Vector v = new Vector();
				v.add(c);
				components.put(key, v);
			}
			else {
				((Vector)value).add(c);
			}
		}

		if(c instanceof Container) {
			Component[] cs = ((Container)c).getComponents();
			
			for(int i = 0; i < cs.length; i++) {
				analyzeComponent(cs[i]);
			}
		}
	}
	
	private void printComponentsMap() {
		Iterator ii = components.keySet().iterator();
		while(ii.hasNext()) {
			Object key = ii.next();
			System.out.println(key + " : " + ((Vector)components.get(key)).size());
		}
	}
	
	private void createFrame() {
		// since 1.4.0 frames are decorated by default
		if(decoratedFramesCheck == null || decoratedFramesCheck.isSelected()) {
			Toolkit.getDefaultToolkit().setDynamicLayout(true);
			System.setProperty("sun.awt.noerasebackground", "true");
			JFrame.setDefaultLookAndFeelDecorated(true);
		}
		else {
			Toolkit.getDefaultToolkit().setDynamicLayout(false);
			System.setProperty("sun.awt.noerasebackground", "false");
			JFrame.setDefaultLookAndFeelDecorated(false);
		}
		
		JDialog.setDefaultLookAndFeelDecorated(true);
		
		theFrame = new XFrame(WINDOW_TITLE);
		theFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		theFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		
		boolean saveItemEnabled = false;
		boolean undoItemEnabled = false;
		boolean redoItemEnabled = false;
		
		if(saveItem != null) {
			saveItemEnabled = saveItem.isEnabled();
			undoItemEnabled = undoItem.isEnabled();
			redoItemEnabled = redoItem.isEnabled();
		}
		
		createMenuBar();
		
		saveItem.setEnabled(saveItemEnabled);
		undoItem.setEnabled(undoItemEnabled);
		redoItem.setEnabled(redoItemEnabled);
		
		if(thePanel == null) {
			thePanel = createUI();
			analyzeComponent(thePanel);
			//printComponentsMap();
			createHSBPopup();
			createSBPopup();
			createCPSBPopup();
			createCPHSBPopup();
		}
		
		theFrame.getContentPane().add(thePanel);
		theFrame.getRootPane().setDefaultButton(applySettingsButton);
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		
		theFrame.pack();
		
		theFrame.setLocation((d.width - theFrame.getWidth()) / 2,
			(d.height - theFrame.getHeight()) / 3);
		theFrame.setVisible(true);
		
		int w = palette.getWidth();
		palette.setBounds(desktopPane.getWidth() - (w + 2), 2, w, 120);

		initColors();
		initPanels();
		applySettingsButton.setEnabled(false);
		startProgressTimer();
		
		try {
			internalFrame.setSelected(true);
		}
		catch (PropertyVetoException ignore) {}
	}
	
	private void startProgressTimer() {
		if(progressTimer == null) {
			progressTimer = new javax.swing.Timer(500, new ProgressAction());
		}
		
		progressBar2.setIndeterminate(true);
		progressBar1.setIndeterminate(false);
		progressBar3.setIndeterminate(true);
		progressBar4.setIndeterminate(false);
		progressTimer.start();
	}
	
	private void stopProgressTimer() {
		if(progressTimer == null) return;
		
		progressTimer.stop();
		progressBar1.setIndeterminate(false);
		progressBar2.setIndeterminate(false);
		progressBar3.setIndeterminate(false);
		progressBar4.setIndeterminate(false);
	}
	
	private void showUIVariables() {
		UIDefaults defaults = UIManager.getDefaults();
		
		String key;
		int c = 0;
		TreeMap map = new TreeMap();
		
		Enumeration e = defaults.keys();
		while(e.hasMoreElements()) {
			key = e.nextElement().toString();
			map.put(key, defaults.get(key));
		}
		
		Iterator ii = map.keySet().iterator();
		while(ii.hasNext()) {
			key = ii.next().toString();
			System.out.print("#" + (c++) + " : " + key);
			System.out.println(" = " + map.get(key));
		}
		
		System.out.println();
	}
	
	void showUIVariables(String inString) {
		UIDefaults defaults = UIManager.getDefaults();
		
		String key;
		int c = 0;
		TreeMap map = new TreeMap();
		
		Enumeration e = defaults.keys();
		while(e.hasMoreElements()) {
			key = e.nextElement().toString();
			if(inString == null || key.indexOf(inString) != -1) {
				map.put(key, defaults.get(key));
			}
		}
		
		Object val;
		
		Iterator ii = map.keySet().iterator();
		while(ii.hasNext()) {
			key = ii.next().toString();
			val = map.get(key);
			
			System.out.print("#" + (c++) + " : " + key);
			System.out.println(" = " + map.get(key));
		}
	}
	
	private void showUIValues(String val) {
		UIDefaults defaults = UIManager.getDefaults();
		
		String key;
		int c = 0;
		TreeMap map = new TreeMap();
		
		Enumeration e = defaults.keys();
		while(e.hasMoreElements()) {
			key = e.nextElement().toString();
			map.put(key, defaults.get(key));
		}
		
		Object value;
		Iterator ii = map.keySet().iterator();
		while(ii.hasNext()) {
			key = ii.next().toString();
			value = map.get(key);
			if(value != null && value.toString().indexOf(val) != -1) {
				System.out.print("#" + (c++) + " : " + key);
				System.out.println(" = " + value);
			}
		}
		
		System.out.println();
	}

	private void showMessageDialog() {
		JOptionPane.showMessageDialog(theFrame, "No messages today.");
	}
	
	private void showConfirmDialog() {
		JOptionPane.showConfirmDialog(theFrame, "Do you really have a choice?");
	}
	
	private void showWarningDialog() {
		JOptionPane.showMessageDialog(theFrame,
			"You have been warned!", "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	private void showErrorDialog() {
		JOptionPane.showMessageDialog(theFrame,
			"Unknown software error. Panic!", "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void showHelpDialog() {
		HelpDialog.showDialog(theFrame);
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createThemesMenu());
		menuBar.add(createDialogsMenu());
		menuBar.add(createMagnifierMenu());
		menuBar.add(createDisabledMenu());
		menuBar.add(createTestMenu());
		menuBar.add(createHelpMenu());
		menuBar.add(createRightToLeftMenu());
		menus[7] = menuBar;

		theFrame.setJMenuBar(menuBar);
	}
	
	private JPanel createUI() {
		JPanel p = new JPanel(new BorderLayout());
		JPanel p0 = new JPanel(new BorderLayout());
		JPanel p1 = new JPanel(new BorderLayout());
		
		p1.add(createToolBar(), BorderLayout.NORTH);
		
		// Colors/Fonts/Decoration
		mainTab = new JTabbedPane(JTabbedPane.LEFT);
		mainTab.add("Colors", createColorPanel());
		mainTab.add("Fonts", createFontPanel());
		mainTab.add("Decoration", createDecorationPane());
		p1.add(mainTab, BorderLayout.CENTER);
		p0.add(p1, BorderLayout.NORTH);
		
		// Apply Settings
		p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 2));
		applySettingsButton = new JButton("Apply Settings");
		applySettingsButton.addActionListener(new ApplySettingsAction());
		p1.add(applySettingsButton);
		
		p0.add(p1, BorderLayout.SOUTH);
		p.add(p0, BorderLayout.NORTH);
		
		examplePanel = new ExamplePanel();
		p0 = new JPanel(new BorderLayout());
		p0.setBorder(new TitledBorder("Example Components"));
		p0.add(examplePanel, BorderLayout.CENTER);
		p.add(p0, BorderLayout.CENTER);
		
		return p;
	}
	
	private void switchFrameDecoration() {
		stopProgressTimer();
		theFrame.dispose();
		createFrame();
	}
	
	private JPanel createColorPanel() {
		JPanel p0 = new JPanel(new BorderLayout());
		JPanel p1 = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = insets2404;
		
		
		p1.add(new JLabel("Main Color"), gc);
		gc.gridx ++;
		
		gc.insets = insets2804;
		p1.add(new JLabel("Background Color"), gc);
		gc.gridx ++;
		
		p1.add(new JLabel("Disabled Color"), gc);
		gc.gridx ++;
		
		p1.add(new JLabel("Frame Color"), gc);
		
		gc.gridx = 0;
		gc.gridy ++;
		gc.insets = new Insets(2, 4, 8, 4);
		
		mainField = new SBControl(Theme.mainColor);
		p1.add(mainField, gc);
		gc.gridx ++;
		
		gc.insets = new Insets(2, 8, 8, 4);
		backField = new SBControl(Theme.backColor);
		p1.add(backField, gc);
		gc.gridx ++;
		
		rollField = new SBControl(Theme.disColor);
		p1.add(rollField, gc);
		gc.gridx ++;
		
		frameField = new SBControl(Theme.frameColor);
		frameField.setName("ff");
		p1.add(frameField, gc);
		
		gc.gridx = 0;
		gc.gridy ++;
		gc.insets = insets2404;
		p1.add(new JLabel("Sub1 Color"), gc);
		gc.gridx ++;
		
		gc.insets = insets2804;
		p1.add(new JLabel("Sub2 Color"), gc);
		gc.gridx ++;
		
		p1.add(new JLabel("Sub3 Color"), gc);
		gc.gridx ++;
		
		p1.add(new JLabel("Sub4 Color"), gc);
		
		gc.gridx = 0;
		gc.gridy ++;
		gc.insets = new Insets(2, 4, 8, 4);
		sub1Field = new SBControl(Theme.sub1Color, true, CONTROLS_ALL);
		p1.add(sub1Field, gc);
		gc.gridx ++;
		
		gc.insets = new Insets(2, 8, 8, 4);
		sub2Field = new SBControl(Theme.sub2Color, true, CONTROLS_ALL);
		p1.add(sub2Field, gc);
		gc.gridx ++;
		
		sub3Field = new SBControl(Theme.sub3Color, true, CONTROLS_ALL);
		p1.add(sub3Field, gc);
		gc.gridx ++;
		
		sub4Field = new SBControl(Theme.sub4Color, true, CONTROLS_ALL);
		p1.add(sub4Field, gc);
		
		gc.gridx = 0;
		gc.gridy ++;
		gc.insets = insets2404;
		p1.add(new JLabel("Sub5 Color"), gc);
		gc.gridx ++;
		
		gc.insets = insets2804;
		p1.add(new JLabel("Sub6 Color"), gc);
		gc.gridx ++;
		
		p1.add(new JLabel("Sub7 Color"), gc);
		gc.gridx ++;
		
		p1.add(new JLabel("Sub8 Color"), gc);
		
		gc.gridx = 0;
		gc.gridy ++;
		gc.insets = new Insets(2, 4, 8, 4);
		sub5Field = new SBControl(Theme.sub5Color, true, CONTROLS_ALL);
		p1.add(sub5Field, gc);
		gc.gridx ++;
		
		gc.insets = new Insets(2, 8, 8, 4);
		sub6Field = new SBControl(Theme.sub6Color, true, CONTROLS_ALL);
		p1.add(sub6Field, gc);
		gc.gridx ++;
		
		sub7Field = new SBControl(Theme.sub7Color, true, CONTROLS_ALL);
		p1.add(sub7Field, gc);
		gc.gridx ++;
		
		sub8Field = new SBControl(Theme.sub8Color, true, CONTROLS_ALL);
		p1.add(sub8Field, gc);
		
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 12));
		p2.add(p1);
		
		p0.add(p2, BorderLayout.NORTH);
		
		p0.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				selection.clearSelection();
			}
		});
		
		return p0;
	}
	
	private JToolBar createToolBar() {
		// Changed in 1.4.0: With JREs prior to 1.4.2, adding a toolbar
		// separator will result in NPE being thrown at JToolBar.Separator.getPreferredSize().
		String javaVersion = TinyUtils.getJavaVersion();
		boolean canAddSeparator = javaVersion != null &&
			!(javaVersion.startsWith("1.0") ||
			javaVersion.startsWith("1.1") ||
			javaVersion.startsWith("1.2") ||
			javaVersion.startsWith("1.3") ||
			javaVersion.startsWith("1.4.0") ||
			javaVersion.startsWith("1.4.1"));
		JToolBar toolBar = new JToolBar();
		
		ButtonGroup group = new ButtonGroup();
		JToggleButton tb = null;
		Dimension iconSize = new Dimension(16, 18);
		
		for(int i = 0; i < 6; i++) {
			tb = new JToggleButton("", new ColorIcon(iconSize));
			group.add(tb);
			toolBar.add(tb);
		}
		
		if(canAddSeparator) {
			toolBar.addSeparator();
		}
		
		for(int i = 0; i < 5; i++) {
			tb = new JToggleButton("", new ColorIcon(iconSize), i == 0);
			group.add(tb);
			toolBar.add(tb);
		}
		
		if(canAddSeparator) {
			toolBar.addSeparator();
		}
		
		for(int i = 0; i < 4; i++) {
			tb = new JToggleButton("", new ColorIcon(iconSize));
			group.add(tb);
			toolBar.add(tb);
		}
		
		tb = new JToggleButton("TB_Button");
		toolBar.add(tb);

		return toolBar;
	}
	
	private StyledDocument createStyledDocument() {
		StyledDocument doc = new DefaultStyledDocument();
		Style defaultStyle = StyleContext.getDefaultStyleContext().
		getStyle(StyleContext.DEFAULT_STYLE);
		
		Style regular = doc.addStyle("regular", defaultStyle);
		StyleConstants.setFontFamily(regular, "SansSerif");
		StyleConstants.setFontSize(regular, 12);
		StyleConstants.setForeground(regular, Color.BLACK);
		StyleConstants.setUnderline(regular, false);
		StyleConstants.setBold(regular, false);
		StyleConstants.setItalic(regular, false);
		
		doc.setLogicalStyle(0, regular);
		
		try {
			doc.insertString(0, "         JTextPane with\n", regular);
		} catch (BadLocationException ignore) {}
		
		int position = 24;
		Color red = new Color(132, 0, 0);
		Style s = doc.addStyle("red24", regular);
		StyleConstants.setFontSize(s, 24);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, red);
		
		try {
			doc.insertString(position++, "S", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("red22", s);
		StyleConstants.setFontSize(s, 22);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, red);
		
		try {
			doc.insertString(position++, "t", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("red20", s);
		StyleConstants.setFontSize(s, 20);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, red);
		
		try {
			doc.insertString(position++, "y", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("red18", s);
		StyleConstants.setFontSize(s, 18);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, red);
		
		try {
			doc.insertString(position++, "l", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("red16", s);
		StyleConstants.setFontSize(s, 16);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, red);
		
		try {
			doc.insertString(position++, "e", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("red14", s);
		StyleConstants.setFontSize(s, 14);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, red);
		
		try {
			doc.insertString(position++, "d ", s);
		} catch (BadLocationException ignore) {}
		
		position++;
		Color green = new Color(0, 130, 132);
		s = doc.addStyle("green12", s);
		StyleConstants.setFontSize(s, 12);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "D", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("green13", s);
		StyleConstants.setFontSize(s, 13);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "o", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("green14", s);
		StyleConstants.setFontSize(s, 14);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "c", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("green16", s);
		StyleConstants.setFontSize(s, 16);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "u", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("green18", s);
		StyleConstants.setFontSize(s, 18);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "m", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("green20", s);
		StyleConstants.setFontSize(s, 20);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "e", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("green22", s);
		StyleConstants.setFontSize(s, 22);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "n", s);
		} catch (BadLocationException ignore) {}
		
		s = doc.addStyle("green24", s);
		StyleConstants.setFontSize(s, 24);
		StyleConstants.setUnderline(s, true);
		StyleConstants.setForeground(s, green);
		
		try {
			doc.insertString(position++, "t", s);
		} catch (BadLocationException ignore) {}
		
		return doc;
	}
	
	private void createCPSBPopup() {
		if(cpSBPopup != null) return;
		
		ActionListener popupAction = new CPSBPopupAction();
		cpSBPopup = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Copy Parameters");
		item.setActionCommand("copy");
		item.addActionListener(popupAction);
		cpSBPopup.add(item);
		
		pasteSBParametersItem = new JMenuItem("Paste Parameters");
		pasteSBParametersItem.setActionCommand("paste");
		pasteSBParametersItem.addActionListener(popupAction);
		pasteSBParametersItem.setEnabled(false);
		cpSBPopup.add(pasteSBParametersItem);
	}
	
	private void createCPHSBPopup() {
		if(cpHSBPopup != null) return;
		
		ActionListener popupAction = new CPHSBPopupAction();
		cpHSBPopup = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Copy Parameters");
		item.setActionCommand("copy");
		item.addActionListener(popupAction);
		cpHSBPopup.add(item);
		
		pasteHSBParametersItem = new JMenuItem("Paste Parameters");
		pasteHSBParametersItem.setActionCommand("paste");
		pasteHSBParametersItem.addActionListener(popupAction);
		pasteHSBParametersItem.setEnabled(false);
		cpHSBPopup.add(pasteHSBParametersItem);
	}
	
	private JPopupMenu createSBPopup() {
		if(sbPopup != null) return sbPopup;
		
		ActionListener hsbPopupAction = new SBPopupAction();
		sbPopup = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Absolute Color");
		item.setActionCommand("1");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		sbPopup.addSeparator();
		
		item = new JMenuItem("Derive from Main Color");
		item.setActionCommand("2");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Back Color");
		item.setActionCommand("3");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Disabled Color");
		item.setActionCommand("4");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Frame Color");
		item.setActionCommand("5");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub1 Color");
		item.setActionCommand("6");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub2 Color");
		item.setActionCommand("7");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub3 Color");
		item.setActionCommand("8");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub4 Color");
		item.setActionCommand("9");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub5 Color");
		item.setActionCommand("10");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub6 Color");
		item.setActionCommand("11");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub7 Color");
		item.setActionCommand("12");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub8 Color");
		item.setActionCommand("13");
		item.addActionListener(hsbPopupAction);
		sbPopup.add(item);
		
		return sbPopup;
	}
	
	private void updateSBPopupIcons() {
		MenuElement[] me = sbPopup.getSubElements();
		
		((JMenuItem)me[0]).setIcon(Theme.mainColor.getAbsoluteIcon());
		((JMenuItem)me[1]).setIcon(Theme.mainColor.getIcon());
		((JMenuItem)me[2]).setIcon(Theme.backColor.getIcon());
		((JMenuItem)me[3]).setIcon(Theme.disColor.getIcon());
		((JMenuItem)me[4]).setIcon(Theme.frameColor.getIcon());
		((JMenuItem)me[5]).setIcon(Theme.sub1Color.getIcon());
		((JMenuItem)me[6]).setIcon(Theme.sub2Color.getIcon());
		((JMenuItem)me[7]).setIcon(Theme.sub3Color.getIcon());
		((JMenuItem)me[8]).setIcon(Theme.sub4Color.getIcon());
		((JMenuItem)me[9]).setIcon(Theme.sub5Color.getIcon());
		((JMenuItem)me[10]).setIcon(Theme.sub6Color.getIcon());
		((JMenuItem)me[11]).setIcon(Theme.sub7Color.getIcon());
		((JMenuItem)me[12]).setIcon(Theme.sub8Color.getIcon());
		
		for(int i = 0; i < 13; i++) {
			((JMenuItem)me[i]).setSelected(false);
		}
		
		for(int i = 5; i < 13; i++) {
			((JMenuItem)me[i]).setEnabled(true);
		}
	}
	
	public void showCPSBPopup(SBControl cf) {
		if(cf.isLocked()) return;
		
		selectedSBControl = cf;
		
		cpSBPopup.show(cf, 0, cf.getHeight());
	}
	
	public void showCPHSBPopup(HSBControl hsb) {
		selectedHSBControl = hsb;
		
		cpHSBPopup.show(hsb, 0, hsb.getHeight());
	}
	
	public void showSBPopup(SBControl cf) {
		updateSBPopupIcons();
		
		selectedSBControl = cf;
		int index = cf.getSBReference().getReference() - 1;
		MenuElement[] me = sbPopup.getSubElements();
		
		((JMenuItem)me[index]).setSelected(true);
		
		if(cf.equals(sub1Field)) {
			((JMenuItem)me[5]).setEnabled(false);
		}
		else if(cf.equals(sub2Field)) {
			((JMenuItem)me[6]).setEnabled(false);
		}
		else if(cf.equals(sub3Field)) {
			((JMenuItem)me[7]).setEnabled(false);
		}
		else if(cf.equals(sub4Field)) {
			((JMenuItem)me[8]).setEnabled(false);
		}
		else if(cf.equals(sub5Field)) {
			((JMenuItem)me[9]).setEnabled(false);
		}
		else if(cf.equals(sub6Field)) {
			((JMenuItem)me[10]).setEnabled(false);
		}
		else if(cf.equals(sub7Field)) {
			((JMenuItem)me[11]).setEnabled(false);
		}
		else if(cf.equals(sub8Field)) {
			((JMenuItem)me[12]).setEnabled(false);
		}
		
		sbPopup.show(cf, 0, cf.getHeight());
	}
	
	private JPopupMenu createHSBPopup() {
		if(hsbPopup != null) return hsbPopup;
		
		ActionListener hsbPopupAction = new HSBPopupAction();
		hsbPopup = new JPopupMenu();
		
		JMenuItem item = new JMenuItem("Derive from Main Color");
		item.setActionCommand("2");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Back Color");
		item.setActionCommand("3");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Disabled Color");
		item.setActionCommand("4");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Frame Color");
		item.setActionCommand("5");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub1 Color");
		item.setActionCommand("6");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub2 Color");
		item.setActionCommand("7");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub3 Color");
		item.setActionCommand("8");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub4 Color");
		item.setActionCommand("9");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub5 Color");
		item.setActionCommand("10");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub6 Color");
		item.setActionCommand("11");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub7 Color");
		item.setActionCommand("12");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		item = new JMenuItem("Derive from Sub8 Color");
		item.setActionCommand("13");
		item.addActionListener(hsbPopupAction);
		hsbPopup.add(item);
		
		return hsbPopup;
	}
	
	private void updateHSBPopupIcons() {
		MenuElement[] me = hsbPopup.getSubElements();
		
		((JMenuItem)me[0]).setIcon(Theme.mainColor.getIcon());
		((JMenuItem)me[1]).setIcon(Theme.backColor.getIcon());
		((JMenuItem)me[2]).setIcon(Theme.disColor.getIcon());
		((JMenuItem)me[3]).setIcon(Theme.frameColor.getIcon());
		((JMenuItem)me[4]).setIcon(Theme.sub1Color.getIcon());
		((JMenuItem)me[5]).setIcon(Theme.sub2Color.getIcon());
		((JMenuItem)me[6]).setIcon(Theme.sub3Color.getIcon());
		((JMenuItem)me[7]).setIcon(Theme.sub4Color.getIcon());
		((JMenuItem)me[8]).setIcon(Theme.sub5Color.getIcon());
		((JMenuItem)me[9]).setIcon(Theme.sub6Color.getIcon());
		((JMenuItem)me[10]).setIcon(Theme.sub7Color.getIcon());
		((JMenuItem)me[11]).setIcon(Theme.sub8Color.getIcon());
		
		for(int i = 0; i < 12; i++) {
			((JMenuItem)me[i]).setSelected(false);
		}
		
		for(int i = 4; i < 12; i++) {
			((JMenuItem)me[i]).setEnabled(true);
		}
	}
	
	private void showHSBPopup(HSBControl cf) {
		updateHSBPopupIcons();
		
		selectedHSBControl = cf;
		int index = cf.getRef() - 2;
		MenuElement[] me = hsbPopup.getSubElements();
		
		((JMenuItem)me[index]).setSelected(true);
		
		if(cf.equals(sub1Field)) {
			((JMenuItem)me[4]).setEnabled(false);
		}
		else if(cf.equals(sub2Field)) {
			((JMenuItem)me[5]).setEnabled(false);
		}
		else if(cf.equals(sub3Field)) {
			((JMenuItem)me[6]).setEnabled(false);
		}
		else if(cf.equals(sub4Field)) {
			((JMenuItem)me[7]).setEnabled(false);
		}
		else if(cf.equals(sub5Field)) {
			((JMenuItem)me[8]).setEnabled(false);
		}
		else if(cf.equals(sub6Field)) {
			((JMenuItem)me[9]).setEnabled(false);
		}
		else if(cf.equals(sub7Field)) {
			((JMenuItem)me[10]).setEnabled(false);
		}
		else if(cf.equals(sub8Field)) {
			((JMenuItem)me[11]).setEnabled(false);
		}
		
		hsbPopup.show(cf, 0, cf.getHeight());
	}
	
	private JPanel createFontPanel() {
		JPanel p1 = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(4, 2, 4, 2);
		
		plainFontPanel = new FontPanel(PLAIN_FONT);
		p1.add(plainFontPanel, gc);
		gc.gridy ++;
		
		boldFontPanel = new FontPanel(BOLD_FONT);
		p1.add(boldFontPanel, gc);
		gc.gridy ++;
		
		gc.insets = new Insets(11, 2, 0, 2);
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		p2.add(createFontCombo());
		
		p2.add(new JLabel("    "));
		isPlainFont = new JRadioButton("is Plain Font");
		isPlainFont.addActionListener(new DerivedFontAction());
		p2.add(isPlainFont);
		
		p2.add(new JLabel("    "));
		isBoldFont = new JRadioButton("is Bold Font");
		isBoldFont.addActionListener(new DerivedFontAction());
		p2.add(isBoldFont);
		
		p1.add(p2, gc);
		gc.gridy ++;
		
		gc.insets = new Insets(2, 2, 0, 2);      	
		specialFontPanel = new FontPanel(SPECIAL_FONT);
		specialFontPanel.init(selectedFont);
		p1.add(specialFontPanel, gc);
		
		return p1;
	}
	
	private JComboBox createFontCombo() {
		Vector items = new Vector();
		
		items.add("Button Font");
		items.add("CheckBox Font");
		items.add("ComboBox Font");
		items.add("EditorPane Font");
		items.add("FrameTitle Font");
		items.add("InternalFrameTitle Font");
		items.add("InternalPaletteTitle Font");
		items.add("Label Font");
		items.add("List Font");
		items.add("Menu Font");
		items.add("MenuItem Font");
		items.add("Password Font");
		items.add("ProgressBar Font");
		items.add("RadioButton Font");
		items.add("Table Font");
		items.add("TableHeader Font");
		items.add("TextArea Font");
		items.add("TextField Font");
		items.add("TextPane Font");
		items.add("TitledBorder Font");
		items.add("ToolTip Font");
		items.add("Tree Font");
		items.add("TabbedPane Font");
		
		Collections.sort(items);
		
		fontCombo = new JComboBox(items);
		fontCombo.addActionListener(new SelectSpecialFontAction());
		selectedFont = Theme.buttonFont;
		
		return fontCombo;
	}
	
	private JTabbedPane createDecorationPane() {
		compTab = new JTabbedPane();
		
		buttonsCP = new ButtonsCP();
		compTab.add("Button", buttonsCP);
		compTab.setMnemonicAt(0, KeyEvent.VK_B);
		compTab.setToolTipTextAt(0,
			"<html>JButton<br>" +
			"JToggleButton<br>" +
			"JRadioButton<br>" +
			"JCheckBox");
		
		comboCP = new ComboCP();
		compTab.add("ComboBox", comboCP);
		compTab.setMnemonicAt(1, KeyEvent.VK_C);
		
		framesCP = new FrameCPsPanel();
		compTab.add("Frame", framesCP);
		compTab.setToolTipTextAt(2,
			"<html>JFrame<br>" +
			"JInternalFrame<br>" +
			"JDialog<br>" +
			"JOptionPane");
		
		iconCP = new IconCP();
		compTab.add("Icon", iconCP);
		compTab.setMnemonicAt(3, KeyEvent.VK_I);
		
		listCP = new ListCP();
		compTab.add("List", listCP);
		compTab.setMnemonicAt(4, KeyEvent.VK_L);
		
		menuCP = new MenuCP();
		compTab.add("Menu", menuCP);
		compTab.setToolTipTextAt(5,
			"<html>JMenu<br>" +
			"JMenuItem<br>" +
			"JCheckBoxMenuItem<br>" +
			"JRadioButtonMenuItem");
		
		miscCP = new MiscCP();
		compTab.add("Miscellaneous", miscCP);
		
		progressCP = new ProgressCP();
		compTab.add("ProgressBar", progressCP);
		compTab.setMnemonicAt(7, KeyEvent.VK_P);
		
		scrollsCP = new ScrollBarCP();
		compTab.add("ScrollBar", scrollsCP);
		compTab.setMnemonicAt(8, KeyEvent.VK_S);
		compTab.setToolTipTextAt(8,
			"<html>JScrollPane<br>" +
			"JScrollBar");
		
		separatorCP = new SeparatorCP();
		compTab.add("Separator", separatorCP);
		
		sliderCP = new SliderCP();
		compTab.add("Slider", sliderCP);
		
		spinnerCP = new SpinnerCP();
		compTab.add("Spinner", spinnerCP);
		
		tabsCP = new TabbedPaneCP();
		compTab.add("TabbedPane", tabsCP);
		
		tableCP = new TableCP();
		compTab.add("Table", tableCP);
		
		textCP = new TextCP();
		compTab.add("Text", textCP);
		compTab.setToolTipTextAt(14,
			"<html>JTextField<br>" +
			"JFormattedTextField<br>" +
			"JTextArea<br>" +
			"JPasswordField<br>" +
			"JSpinner.Editor<br>" +
			"JComboBox.Editor");
		
		toolCP = new ToolBarCP();
		compTab.add("ToolBar", toolCP);
		compTab.setToolTipTextAt(15,
			"<html>JToolBar<br>" +
			"ToolBar Button<br>" +
			"JToolBar.Separator");
		
		tipCP = new ToolTipCP();
		compTab.add("ToolTip", tipCP);
		
		treeCP = new TreeCP();
		compTab.add("Tree", treeCP);

		return compTab;
	}
	
	private JMenu createMagnifierMenu() {
		JMenu menu = new JMenu("Magnifier");
		menu.setMnemonic(KeyEvent.VK_M);
		menus[8] = menu;
		
		JMenuItem item = new JCheckBoxMenuItem("Active", magnifierActive);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				magnifierActive = ((AbstractButton)e.getSource()).isSelected();
				magnifierPanel.repaint();
			}
		});
		item.setMnemonic(KeyEvent.VK_A);
		menu.add(item);
		
		menu.addSeparator();
		
		ButtonGroup group = new ButtonGroup();
		item = new JCheckBoxMenuItem("Scale Factor 2:1", scaleFactor == 2);
		group.add(item);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaleFactor = 2;
			}
		});
		menu.add(item);
		
		item = new JCheckBoxMenuItem("Scale Factor 4:1", scaleFactor == 4);
		group.add(item);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaleFactor = 4;
			}
		});
		menu.add(item);
		
		item = new JCheckBoxMenuItem("Scale Factor 6:1", scaleFactor == 6);
		group.add(item);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaleFactor = 6;
			}
		});
		menu.add(item);
		
		item = new JCheckBoxMenuItem("Scale Factor 8:1", scaleFactor == 8);
		group.add(item);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaleFactor = 8;
			}
		});
		menu.add(item);
		
		return menu;
	}
	
	private JMenu createFileMenu() {
		menus = new Component[10];	// includes JMenuBar
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menus[0] = menu;
		
		JMenuItem item = new JMenuItem("Open Theme...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openTheme();
			}
		});
		item.setMnemonic(KeyEvent.VK_O);
		item.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_O, menuShortcutKeyMask));
		menu.add(item);
		
		menu.addSeparator();
		
		saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveTheme(false);
			}
		});
		saveItem.setMnemonic(KeyEvent.VK_S);
		saveItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, menuShortcutKeyMask));
		menu.add(saveItem);

		item = new JMenuItem("Save as...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveTheme(true);
			}
		});
		item.setMnemonic(KeyEvent.VK_A);
		item.setDisplayedMnemonicIndex(5);
		item.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S,
				menuShortcutKeyMask | ActionEvent.SHIFT_MASK));
		menu.add(item);
		
		item = new JMenuItem("Save as Default");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveDefaults();
			}
		});
		item.setMnemonic(KeyEvent.VK_D);
		item.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_D, menuShortcutKeyMask));
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("Quit");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		item.setMnemonic(KeyEvent.VK_Q);
		item.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Q, menuShortcutKeyMask));
		menu.add(item);
		
		return menu;
	}
	
	private JMenu createEditMenu() {
		JMenu menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menus[1] = menu;
		
		undoItem = new JMenuItem("Undo");
		undoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(UndoManager.doUndo(ControlPanel.this)) {
					if(!redoItem.isEnabled()) {
						redoItem.setEnabled(true);
					}
					
					if(!UndoManager.canUndo()) {
						undoItem.setEnabled(false);
					}
					
					undoItem.setText("Undo " + UndoManager.getUndoDescription());
					redoItem.setText("Redo " + UndoManager.getRedoDescription());
					updateColorTTT();
					setFrameTitle();
				}
			}
		});
		undoItem.setMnemonic(KeyEvent.VK_U);
		undoItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Z, menuShortcutKeyMask));
		undoItem.setEnabled(false);
		menu.add(undoItem);
		
		redoItem = new JMenuItem("Redo");
		redoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(UndoManager.doRedo(ControlPanel.this)) {
					if(!undoItem.isEnabled()) {
						undoItem.setEnabled(true);
					}
					
					if(!UndoManager.canRedo()) {
						redoItem.setEnabled(false);
					}
					
					undoItem.setText("Undo " + UndoManager.getUndoDescription());
					redoItem.setText("Redo " + UndoManager.getRedoDescription());
					updateColorTTT();
					setFrameTitle();
				}
			}
		});
		redoItem.setMnemonic(KeyEvent.VK_R);
		redoItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Y, menuShortcutKeyMask));
		redoItem.setEnabled(false);
		menu.add(redoItem);
		
		menu.addSeparator();
		
		JMenuItem item = new JMenuItem("Cut");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		item.setMnemonic(KeyEvent.VK_T);
		item.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_X, menuShortcutKeyMask));
		item.setEnabled(false);
		menu.add(item);
		
		copyItem = new JMenuItem("Copy selected Parameters");
		copyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				copies = selection.createParameterSet();
				
				if(!pasteItem.isEnabled()) {
					pasteItem.setEnabled(true);
				}
			}
		});
		copyItem.setMnemonic(KeyEvent.VK_C);
		copyItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_C, menuShortcutKeyMask));
		copyItem.setEnabled(false);
		menu.add(copyItem);
		
		pasteItem = new JMenuItem("Paste Parameters");
		pasteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				System.out.println("pasteItem.copies: " + copies);
				copies.pasteParameters(true);
				updateColorTTT();
			}
		});
		pasteItem.setMnemonic(KeyEvent.VK_P);
		pasteItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_V, menuShortcutKeyMask));
		pasteItem.setEnabled(false);
		menu.add(pasteItem);
		
		return menu;
	}

	private JMenu createThemesMenu() {
		themesMenu = new JMenu("Themes");
		themesMenu.setMnemonic(KeyEvent.VK_T);
		menus[2] = themesMenu;
		themes = Theme.getAvailableThemes();
		
		if(selectThemeAction == null) {
			selectThemeAction = new SelectThemeAction();
		}
		
		for(int i = 0; i < themes.length; i++) {
			JMenuItem item = new JMenuItem(themes[i].getName());
			item.setActionCommand(String.valueOf(i));
			item.addActionListener(selectThemeAction);
			themesMenu.add(item);
		}

		return themesMenu;
	}
	
	/**
	 * Creates a new ThemeDescription from argument and
	 * adds to available themes and to 'Themes' menu.
	 * @param uri
	 */
	private void addTheme(URI uri) {
		ThemeDescription td = new ThemeDescription(uri);
		ThemeDescription[] temp = new ThemeDescription[themes.length + 1];
		
		System.arraycopy(themes, 0, temp, 0, themes.length);
		temp[themes.length] = td;
		themes = temp;

		JMenuItem item = new JMenuItem(td.getName());
		item.setActionCommand(String.valueOf(themes.length - 1));
		item.addActionListener(selectThemeAction);
		themesMenu.add(item);
	}

	private JMenu createDisabledMenu() {
		JMenu menu = new JMenu("Disabled Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menus[5] = menu;
		
		menu.setEnabled(false);
		
		return menu;
	}
	
	private JMenu createRightToLeftMenu() {
		JMenu menu = new JMenu("Right-to-left Menu");
		menu.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		menu.setMnemonic(KeyEvent.VK_R);
		menu.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menus[9] = menu;
		
		JMenuItem item = new JCheckBoxMenuItem("CheckBoxMenuItem", true);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		menu.add(item);
		item = new JCheckBoxMenuItem("Disabled selected CheckBoxMenuItem", true);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setEnabled(false);
		menu.add(item);
		item = new JCheckBoxMenuItem("Disabled unselected CheckBoxMenuItem", false);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setEnabled(false);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JRadioButtonMenuItem("RadioButtonMenuItem", true);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		menu.add(item);
		item = new JRadioButtonMenuItem("Disabled selected RadioButtonMenuItem", true);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setEnabled(false);
		menu.add(item);
		item = new JRadioButtonMenuItem("Disabled unselected RadioButtonMenuItem", false);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setEnabled(false);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("Java version: " + TinyUtils.getSystemProperty("java.version"));
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		menu.add(item);
		
		item = new JMenuItem("Disabled MenuItem");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, menuShortcutKeyMask));
		item.setEnabled(false);
		menu.add(item);
		
		JMenu sub2 = new JMenu("Disabled Submenu");
		sub2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		sub2.setEnabled(false);
		menu.add(sub2);

		menu.addSeparator();
		
		item = new JMenuItem("Differently ...");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask));
		item.setIcon(new SizedIcon(12, 12));
		menu.add(item);
		
		item = new JMenuItem("... sized ...");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask + KeyEvent.ALT_DOWN_MASK));
		item.setIcon(new SizedIcon(16, 16));
		menu.add(item);
		
		item = new JMenuItem("... Icons");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask + KeyEvent.SHIFT_DOWN_MASK));
		item.setIcon(new SizedIcon(20, 20));
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("horizontalTextPosition = TRAILING");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalTextPosition(SwingConstants.TRAILING);
		menu.add(item);
		
		item = new JMenuItem("HTP = CENTER");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalTextPosition(SwingConstants.CENTER);
		menu.add(item);
		
		item = new JMenuItem("HTP = LEADING");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalTextPosition(SwingConstants.LEADING);
		menu.add(item);
		menu.add(menu);
		
		menu.addSeparator();
		
		item = new JMenuItem("horizontalAlignment = LEADING");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalAlignment(SwingConstants.LEADING);
		menu.add(item);
		
		item = new JMenuItem("HALG = CENTER");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalAlignment(SwingConstants.CENTER);
		menu.add(item);
		
		item = new JMenuItem("HALG = TRAILING");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalAlignment(SwingConstants.TRAILING);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JCheckBoxMenuItem("CheckBoxMenuItem w/Icon", true);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);
		
		item = new JCheckBoxMenuItem("CheckBoxMenuItem deselected");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);
		
		item = new JRadioButtonMenuItem("RadioButtonMenuItem w/Icon", true);
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);

		item = new JRadioButtonMenuItem("RadioButtonMenuItem deselected");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);
		
		menu.addSeparator();
		
		JMenu sub3 = new JMenu("Submenu");
		sub3.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		item = new JMenuItem("Item 1");
		item.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		sub3.add(item);
		menu.add(sub3);
		
		return menu;
	}
	
	private JMenu createTestMenu() {
		JMenu menu = new JMenu("Test Menu");
		menu.setMnemonic(KeyEvent.VK_S);
		menu.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menus[6] = menu;
		
		JMenuItem item = new JMenuItem("<html><b>Note: </b>For JMenuItems displaying HTML text<br>" +
			"<font color=\"#0000ff\">Decoration | Menu | Selected Foreground<br>" +
			"</font><font color=\"#000000\">will have </font>" +
		"<font color=\"#ff0000\">no</font><font color=\"#000000\"> effect.");
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JCheckBoxMenuItem("CheckBoxMenuItem", true);
		menu.add(item);
		item = new JCheckBoxMenuItem("Disabled selected CheckBoxMenuItem", true);
		item.setEnabled(false);
		menu.add(item);
		item = new JCheckBoxMenuItem("Disabled unselected CheckBoxMenuItem", false);
		item.setEnabled(false);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JRadioButtonMenuItem("RadioButtonMenuItem", true);
		menu.add(item);
		item = new JRadioButtonMenuItem("Disabled selected RadioButtonMenuItem", true);
		item.setEnabled(false);
		menu.add(item);
		item = new JRadioButtonMenuItem("Disabled unselected RadioButtonMenuItem", false);
		item.setEnabled(false);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("Java version: " + TinyUtils.getSystemProperty("java.version"));
		menu.add(item);
		
		item = new JMenuItem("Disabled MenuItem");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, menuShortcutKeyMask));
		item.setEnabled(false);
		menu.add(item);
		
		JMenu sub2 = new JMenu("Disabled Submenu");
		sub2.setEnabled(false);
		menu.add(sub2);

		menu.addSeparator();

		item = new JMenuItem("Differently ...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask));
		item.setIcon(new SizedIcon(12, 12));
		menu.add(item);
		
		item = new JMenuItem("... sized ...");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask + KeyEvent.ALT_DOWN_MASK));
		item.setIcon(new SizedIcon(16, 16));
		menu.add(item);
		
		item = new JMenuItem("... Icons");
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask + KeyEvent.SHIFT_DOWN_MASK));
		item.setIcon(new SizedIcon(20, 20));
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("horizontalTextPosition = TRAILING");
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalTextPosition(SwingConstants.TRAILING);
		menu.add(item);
		
		item = new JMenuItem("HTP = CENTER");
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalTextPosition(SwingConstants.CENTER);
		menu.add(item);
		
		item = new JMenuItem("HTP = LEADING");
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalTextPosition(SwingConstants.LEADING);
		menu.add(item);
		menu.add(menu);
		
		menu.addSeparator();
		
		item = new JMenuItem("horizontalAlignment = LEADING");
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalAlignment(SwingConstants.LEADING);
		menu.add(item);
		
		item = new JMenuItem("HALG = CENTER");
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalAlignment(SwingConstants.CENTER);
		menu.add(item);
		
		item = new JMenuItem("HALG = TRAILING");
		item.setIcon(new SizedIcon(16, 16));
		item.setHorizontalAlignment(SwingConstants.TRAILING);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JCheckBoxMenuItem("CheckBoxMenuItem w/Icon", true);
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);
		
		item = new JCheckBoxMenuItem("CheckBoxMenuItem deselected");
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);
		
		item = new JRadioButtonMenuItem("RadioButtonMenuItem w/Icon", true);
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);

		item = new JRadioButtonMenuItem("RadioButtonMenuItem deselected");
		item.setIcon(TinyLookAndFeel.loadIcon("cp_icons/smileyIcon.png"));
		menu.add(item);
		
		menu.addSeparator();
		
		JMenu sub3 = new JMenu("Submenu");
		item = new JMenuItem("Item 1");
		sub3.add(item);
		menu.add(sub3);

		return menu;
	}
	
	private JMenu createHelpMenu() {
		JMenu menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		menus[4] = menu;
		
		JMenuItem item = new JMenuItem("Control Panel Help...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHelpDialog();
			}
		});
		item.setMnemonic(KeyEvent.VK_H);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("About TinyLaF...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutDialog();
			}
		});
		item.setMnemonic(KeyEvent.VK_A);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("Check for Updates...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CheckForUpdatesDialog.showDialog(theFrame);
			}
		});
		item.setMnemonic(KeyEvent.VK_C);
		menu.add(item);
		
		return menu;
	}
	
	private JMenu createDialogsMenu() {
		JMenu menu = new JMenu("Dialogs");
		menu.setMnemonic(KeyEvent.VK_D);
		menus[3] = menu;

		JMenuItem item = new JMenuItem("Plain Dialog...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PlainDialog(theFrame);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, menuShortcutKeyMask));
		item.setMnemonic(KeyEvent.VK_P);
		menu.add(item);

		item = new JMenuItem("MessageDialog...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMessageDialog();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, menuShortcutKeyMask));
		item.setMnemonic(KeyEvent.VK_M);
		menu.add(item);
		
		item = new JMenuItem("ConfirmDialog...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showConfirmDialog();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, menuShortcutKeyMask));
		item.setMnemonic(KeyEvent.VK_C);
		menu.add(item);
		
		item = new JMenuItem("WarningDialog...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showWarningDialog();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, menuShortcutKeyMask));
		item.setMnemonic(KeyEvent.VK_W);
		menu.add(item);
		
		item = new JMenuItem("ErrorDialog...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showErrorDialog();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, menuShortcutKeyMask));
		item.setMnemonic(KeyEvent.VK_E);
		menu.add(item);
		
		menu.addSeparator();
		
		item = new JMenuItem("InternalMessageDialog...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showInternalMessageDialog(
					palette, "Life is a while(true) loop.");
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, menuShortcutKeyMask));
		item.setMnemonic(KeyEvent.VK_I);
		menu.add(item);

		item = new JMenuItem("InternalConfirmDialog...");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showInternalConfirmDialog(
					palette, "Is programming art?");
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, menuShortcutKeyMask));
		item.setMnemonic(KeyEvent.VK_N);
		menu.add(item);
		
		return menu;
	}
	
	private void addButtonIcons(boolean b) {
		if(b && exampleButton.getIcon() == null) {
			getButtonIcon();
			exampleButton.setIcon(buttonIcon);
			exampleDisabledButton.setIcon(buttonIcon);
			exampleToggleButton.setIcon(buttonIcon);
			// We change the toggle button's text, so
			// it will not require more space than before
			exampleToggleButton.setText("JToggleBtn");
		}
		else if(!b && exampleButton.getIcon() != null) {
			exampleButton.setIcon((Icon)null);
			exampleDisabledButton.setIcon((Icon)null);
			exampleToggleButton.setIcon((Icon)null);
			exampleToggleButton.setText("JToggleButton");
		}
	}
	
	private Icon getButtonIcon() {
		if(buttonIcon == null) {
			buttonIcon = new ImageIcon(
				ClassLoader.getSystemResource(
				"de/muntjak/tinylookandfeel/cp_icons/theIcon.gif"));
		}
		
		return buttonIcon;
	}
	
	void updateExamplePanel() {
		examplePanel.update(false);
	}
	
	/**
	 * Updates tooltips (including reference count)
	 * of all color references.
	 *
	 */
	private void updateColorTTT() {
		mainField.updateTTT();
		rollField.updateTTT();
		backField.updateTTT();
		frameField.updateTTT();
		sub1Field.updateTTT();
		sub2Field.updateTTT();
		sub3Field.updateTTT();
		sub4Field.updateTTT();
		sub5Field.updateTTT();
		sub6Field.updateTTT();
		sub7Field.updateTTT();
		sub8Field.updateTTT();
	}
	
	private void updateFont(int type) {
		if(type == PLAIN_FONT) {
			Theme.plainFont.setFont(plainFontPanel.getCurrentFont());
		}
		else if(type == BOLD_FONT) {
			Theme.boldFont.setFont(boldFontPanel.getCurrentFont());
		}
		else {	// Special Font
			selectedFont.setFont(specialFontPanel.getCurrentFont());
		}
		
		examplePanel.update(true);
	}
	
	/*
	 * Alphabetical ordering!
	 */
	private void updateSpecialFont() {
		int index = fontCombo.getSelectedIndex();
		
		switch(index) {
			case 0:
				selectedFont = Theme.buttonFont;
				break;
			case 1:
				selectedFont = Theme.checkFont;
				break;
			case 2:
				selectedFont = Theme.comboFont;
				break;
			case 3:
				selectedFont = Theme.editorFont;
				break;
			case 4:
				selectedFont = Theme.frameTitleFont;
				break;
			case 5:
				selectedFont = Theme.internalFrameTitleFont;
				break;
			case 6:
				selectedFont = Theme.internalPaletteTitleFont;
				break;
			case 7:
				selectedFont = Theme.labelFont;
				break;
			case 8:
				selectedFont = Theme.listFont;
				break;
			case 9:
				selectedFont = Theme.menuFont;
				break;
			case 10:
				selectedFont = Theme.menuItemFont;
				break;
			case 11:
				selectedFont = Theme.passwordFont;
				break;
			case 12:
				selectedFont = Theme.progressBarFont;
				break;
			case 13:
				selectedFont = Theme.radioFont;
				break;
			case 14:
				selectedFont = Theme.tabFont;
				break;
			case 15:
				selectedFont = Theme.tableFont;
				break;
			case 16:
				selectedFont = Theme.tableHeaderFont;
				break;
			case 17:
				selectedFont = Theme.textAreaFont;
				break;
			case 18:
				selectedFont = Theme.textFieldFont;
				break;
			case 19:
				selectedFont = Theme.textPaneFont;
				break;
			case 20:
				selectedFont = Theme.titledBorderFont;
				break;
			case 21:
				selectedFont = Theme.toolTipFont;
				break;
			case 22:
				selectedFont = Theme.treeFont;
				break;
		}
		
		specialFontPanel.init(selectedFont);
		
		// update all font colors
		Theme.buttonFontColor.update();
		Theme.labelFontColor.update();
		Theme.menuFontColor.update();
		Theme.menuItemFontColor.update();
		Theme.radioFontColor.update();
		Theme.checkFontColor.update();
		Theme.tableFontColor.update();
		Theme.tableHeaderFontColor.update();
		Theme.titledBorderFontColor.update();
		Theme.tabFontColor.update();
	}
	
	void setTheme() {
		updateTheme();
		// Note: We must close all non-modal dialogs before
		// the LAF is updated, else popup shadows will not
		// work correctly on non-modal dialogs.
		TinyPopupFactory.closeDialogs();
		
		LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
		
		try {
			UIManager.setLookAndFeel(currentLookAndFeel);
		}
		catch(Exception e) {
			System.err.println(e.toString());
		}
		
		SwingUtilities.updateComponentTreeUI(theFrame);
		
		if(sbPopup != null) {
			SwingUtilities.updateComponentTreeUI(sbPopup);
		}
		
		if(hsbPopup != null) {
			SwingUtilities.updateComponentTreeUI(hsbPopup);
		}
		
		if(cpSBPopup != null) {
			SwingUtilities.updateComponentTreeUI(cpSBPopup);
		}
		
		if(cpHSBPopup != null) {
			SwingUtilities.updateComponentTreeUI(cpHSBPopup);
		}
		
		HelpDialog.updateUI();
		
		applySettingsButton.setEnabled(false);
		iconCP.init(true);

		sp1.setViewportBorder(BorderFactory.createLineBorder(
			Theme.treeBgColor.getColor(), 2));
		sp2.setViewportBorder(BorderFactory.createLineBorder(
			Theme.treeBgColor.getColor(), 2));
		
		if(theFrame.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
			theFrame.pack();
		}
		
		PSColorChooser.deleteInstance();
		SBChooser.deleteInstance();
		HSBChooser.deleteInstance();
	}
	
	void storeUndoData(Object source) {
		// if change relies on apply settings (aka setTheme()),
		// we must store undoData immediately but activate it
		// only if 'Apply Settings' button was pressed (where a
		// single button press can activate several undo items)
		
		// Note: Because FontColorControl is a SBControl,
		// order of if-clauses is important
		if(source instanceof FontColorControl) {
			UndoManager.storeUndoData(selectedFont);
			
			return;
		}
		else if(source instanceof ColoredFont) {
			UndoManager.storeUndoData((ColoredFont)source);
			
			return;
		}
		else if(source instanceof SBControl) {
			SBControl sb = (SBControl)source;
			UndoManager.storeUndoData((SBControl)source);
			
			if(sb.forceUpdate) return;
		}
		else if(source instanceof SpreadControl) {
			// SpreadControl never applies settings
			UndoManager.storeUndoData((SpreadControl)source);
		}
		else if(source instanceof BooleanControl) {
			BooleanControl bc = (BooleanControl)source;
			
			UndoManager.storeUndoData(bc);
			
			if(bc.forceUpdate) return;
		}
		else if(source instanceof HSBControl) {
			// HSBControl always applies settings
			HSBControl hsb = (HSBControl)source;
			UndoManager.storeUndoData(hsb, iconChecks[hsb.index]);
			
			return;
		}
		else if(source instanceof ColorizeIconCheck) {
			// ColorizeIconCheck always applies settings
			UndoManager.storeUndoData((ColorizeIconCheck)source);
			
			return;
		}
		else if(source instanceof ParameterSet) {
			// Note: The current parameter set might be pasted again,
			// so we must create a copy
			UndoManager.storeUndoData(new ParameterSet((ParameterSet)source));
		}

		// enable undo - disable redo
		if(!undoItem.isEnabled()) {
			undoItem.setEnabled(true);
		}
		
		undoItem.setText("Undo " + UndoManager.getUndoDescription());
		
		if(redoItem.isEnabled()) {
			redoItem.setEnabled(false);
		}
		
		setFrameTitle();
	}
	
	/**
	 * Enables undo menu item, disables
	 * redo menu item.
	 *
	 */
	void undoItemsActivated() {
		// enable undo - disable redo
		if(!undoItem.isEnabled()) {
			undoItem.setEnabled(true);
		}
		
		if(redoItem.isEnabled()) {
			redoItem.setEnabled(false);
		}
	}
	
	private void updateTheme() {
		updateSpecialFont();
	}

	private void updateStyle() {
		stopProgressTimer();
		
		initColors();
		initPanels();
		setTheme();
		startProgressTimer();
	}
	
	void colorizeIcon(HSBControl control, boolean doColorize) {
		Icon icon = TinyLookAndFeel.getUncolorizedSystemIcon(control.index);

		if(doColorize) {
			icon = DrawRoutines.colorizeIcon(
				((ImageIcon)icon).getImage(), control.hsbRef);
		}

		iconChecks[control.index].setIcon(icon);
	}
	
	public SBControl getSBControlFromRef(int ref) {
		if(ref == SBReference.SUB8_COLOR) {
			return sub8Field;
		}
		else if(ref == SBReference.SUB7_COLOR) {
			return sub7Field;
		}
		else if(ref == SBReference.SUB6_COLOR) {
			return sub6Field;
		}
		else if(ref == SBReference.SUB5_COLOR) {
			return sub5Field;
		}
		else if(ref == SBReference.SUB4_COLOR) {
			return sub4Field;
		}
		else if(ref == SBReference.SUB3_COLOR) {
			return sub3Field;
		}
		else if(ref == SBReference.SUB2_COLOR) {
			return sub2Field;
		}
		else if(ref == SBReference.SUB1_COLOR) {
			return sub1Field;
		}
		
		return null;
	}
	
	private String getDescription() {
		String retVal = null;
		
		if(currentFileName == null) {
			if(currentThemeDescription == null) {
				retVal = "YQ Theme";
			}
			else {
				if(currentThemeDescription.isFile()) {
					retVal = truncate(
						currentThemeDescription.getFile().getAbsolutePath(), 80);
				}
				else {
					retVal = currentThemeDescription.getName();
				}
			}
		}
		else {
			retVal = truncate(currentFileName, 80);
		}
		
		if(UndoManager.canUndo()) {
			return retVal + " *";
		}
		
		return retVal;
	}
	
	private static String truncate(String s, int maxLen) {
		if(s == null) return s;
		if(s.length() <= maxLen) return s;
		
		return s.substring(0, maxLen / 2) + "..." + s.substring(s.length() - maxLen / 2);
	}
	
	public void initPanels() {
		resistUpdate = true;
		
		buttonsCP.init(true);
		scrollsCP.init(true);
		separatorCP.init(true);
		tabsCP.init(true);
		comboCP.init(true);
		menuCP.init(true);
		listCP.init(true);
		sliderCP.init(true);
		spinnerCP.init(true);
		progressCP.init(true);
		textCP.init(true);
		treeCP.init(true);
		toolCP.init(true);
		tableCP.init(true);
		frameButtonsCP.init(true);
		frameCloseButtonCP.init(true);
		frameCP.init(true);
		iconCP.init(true);
		tipCP.init(true);
		miscCP.init(true);
		
		//printPreferredSizes();
		
		initFonts();
		
		resistUpdate = false;
		
		setFrameTitle();
	}
	
	private void printPreferredSizes() {
		System.out.println("buttonsCP: " + buttonsCP.getPreferredSize());
		System.out.println("scrollsCP: " + scrollsCP.getPreferredSize());
		System.out.println("tabsCP: " + tabsCP.getPreferredSize());
		System.out.println("comboCP: " + comboCP.getPreferredSize());
		System.out.println("menuCP: " + menuCP.getPreferredSize());
		System.out.println("tableCP: " + tableCP.getPreferredSize());
		System.out.println("framesCP: " + framesCP.getPreferredSize());
	}
	
	private void initColors() {
		mainField.setSBReference(Theme.mainColor);
		rollField.setSBReference(Theme.disColor);
		backField.setSBReference(Theme.backColor);
		frameField.setSBReference(Theme.frameColor);
		sub1Field.setSBReference(Theme.sub1Color);
		sub2Field.setSBReference(Theme.sub2Color);
		sub3Field.setSBReference(Theme.sub3Color);
		sub4Field.setSBReference(Theme.sub4Color);
		sub5Field.setSBReference(Theme.sub5Color);
		sub6Field.setSBReference(Theme.sub6Color);
		sub7Field.setSBReference(Theme.sub7Color);
		sub8Field.setSBReference(Theme.sub8Color);
	}
	
	void initFonts() {
		plainFontPanel.init(Theme.plainFont);
		boldFontPanel.init(Theme.boldFont);
		updateSpecialFont();
	}
	
	private void repaintTargets(Component[] targets) {
		if(targets == null) return;

		for(int i = 0; i < targets.length; i++) {
			targets[i].repaint();
			
//			if(!targets[i].isShowing()) {
//				System.out.println("! target not showing !");
//			}
		}
	}
	
	private void repaintTargets(Vector targets) {
		if(targets == null) return;
		
		Iterator ii = targets.iterator();
		while(ii.hasNext()) {
			Component c = (Component)ii.next();
			
			if(c.isShowing()) {
				c.repaint();
			}
		}
	}
	
	public void repaintTargets(int controlMode) {
		switch(controlMode) {
			case CONTROLS_ACTIVE_FRAME_CAPTION:
				if(decoratedFramesCheck.isSelected()) {
					// to be performant, we repaint title pane only
					Component[] cs =
						theFrame.getLayeredPane().getComponentsInLayer(
							JLayeredPane.FRAME_CONTENT_LAYER.intValue());
					
					for(int i = 0; i < cs.length; i++) {
						if(cs[i] instanceof TinyTitlePane) {
							cs[i].repaint();
							break;
						}
					}
				}
				
				repaintTargets(internalFrames);
				break;
			case CONTROLS_FRAME_BORDER:
				theFrame.repaint();
				break;
			case CONTROLS_ALL:
				examplePanel.update(false);
				break;
			case CONTROLS_BUTTON:
				repaintTargets((Vector)components.get(JButton.class));
				break;
			case CONTROLS_COMBO:
				repaintTargets((Vector)components.get(JComboBox.class));
				break;
			case CONTROLS_INACTIVE_FRAME_CAPTION:
				repaintTargets(internalFrames);
				disabledFramePanel.repaint();
				break;
			case CONTROLS_LIST:
				repaintTargets((Vector)components.get(JList.class));
				fakedList.repaint();
				break;
			case CONTROLS_MENU:
				repaintTargets(menus);
				break;
			case CONTROLS_PROGRESSBAR:
				repaintTargets((Vector)components.get(JProgressBar.class));
				break;
			case CONTROLS_SCROLLBAR:
				repaintTargets((Vector)components.get(JScrollBar.class));
				break;
			case CONTROLS_SLIDER:
				repaintTargets((Vector)components.get(JSlider.class));
				break;
			case CONTROLS_SPINNER:
				repaintTargets((Vector)components.get(JSpinner.class));
				break;
			case CONTROLS_TABBED_PANE:
				repaintTargets((Vector)components.get(JTabbedPane.class));
				break;
			case CONTROLS_TABLE:
				repaintTargets((Vector)components.get(JTable.class));
			case CONTROLS_TEXT:
				repaintTargets((Vector)components.get(JTextComponent.class));
				break;
			case CONTROLS_TOOL_TIP:
				enabledToolTip.repaint();
				disabledToolTip.repaint();
				break;
			case CONTROLS_TOOLBAR:
				repaintTargets((Vector)components.get(JToolBar.class));
				break;
			case CONTROLS_TREE:
				repaintTargets((Vector)components.get(JTree.class));
				break;
			case CONTROLS_WINDOW_BUTTON:
				repaintTargets(windowButtons);
				repaintTargets(internalFrames);
				disabledFramePanel.repaint();
				break;
		}
	}
	
	public static void setWindowButtons(JButton[] buttons) {
		windowButtons = buttons;
	}

	/**
	 * User selected "Open Theme..." command.
	 *
	 */
	private void openTheme() {
		if(!checkThemeState()) return;

		JFileChooser ch = new JFileChooser(directoryPath);
		ch.setFileFilter(fileFilter);
		
		if(ch.showOpenDialog(theFrame) != JFileChooser.APPROVE_OPTION) return;
		
		File f = ch.getSelectedFile();
		
		if(f == null) return;
		
		if(!Theme.loadTheme(f)) {
			String msg = null;
			
			if(Theme.errorCode == Theme.ERROR_FILE_NOT_FOUND) {
				msg = "File '" + f.getName() + "' not found.";
			}
			else if(Theme.errorCode == Theme.ERROR_NO_TINYLAF_THEME) {
				msg = "File '" + f.getName() + "' is no valid TinyLaF theme.";
			}
			else if(Theme.errorCode == Theme.ERROR_WIN99_STYLE) {
				msg = "99 Style not supported.";
			}
			
			JOptionPane.showMessageDialog(theFrame,
				msg,
				"Error loading file",
				JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		currentFileName = f.getAbsolutePath();
		
		if(f.getParent() != null) {
			directoryPath = f.getParent();
		}
		
		saveItem.setEnabled(true);
		updateStyle();
	}
	
	/**
	 * User selected theme from Themes menu.
	 * @param fn
	 */
	private void openTheme(ThemeDescription td) {
		if(!checkThemeState()) return;

		if(!Theme.loadTheme(td)) {
			String msg = null;
			
			if(Theme.errorCode == Theme.ERROR_FILE_NOT_FOUND) {
				msg = "Resource '" + td.getName() + "' not found.";
			}
			else if(Theme.errorCode == Theme.ERROR_NO_TINYLAF_THEME) {
				msg = "Resource '" + td.getName() + "' is no valid TinyLaF theme.";
			}
			else if(Theme.errorCode == Theme.ERROR_WIN99_STYLE) {
				msg = "99 Style not supported.";
			}
			else if(Theme.errorCode == Theme.ERROR_INVALID_THEME_DESCRIPTION) {
				msg = "Invalid ThemeDescription.";
			}
			
			JOptionPane.showMessageDialog(theFrame,
				msg,
				"Error loading theme",
				JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		currentThemeDescription = td;
		
		if(td.isFile()) {
			currentFileName = td.getFile().getAbsolutePath();
			
			if(td.getFile().getParent() != null) {
				directoryPath = td.getFile().getParent();
			}
		}
		else {
			currentFileName = null;
		}
		
		saveItem.setEnabled(td.isFile());
		UndoManager.clear();
		undoItem.setEnabled(false);
		redoItem.setEnabled(false);
		updateStyle();
	}
	
	private void quit() {
		if(!checkThemeState()) return;

		System.exit(0);
	}
	
	private void saveTheme(boolean showFileChooser) {
		if(currentFileName != null && !showFileChooser) {
			if(!Theme.saveTheme(currentFileName)) return;
			
			UndoManager.clear();
			setFrameTitle();
			return;
		}
		
		JFileChooser ch = new JFileChooser(directoryPath);
		ch.setFileFilter(fileFilter);
		ch.setSelectedFile(new File(
			TinyUtils.getSystemProperty("user.dir") + File.separator + "Untitled.theme"));
		
		int answer = ch.showSaveDialog(theFrame);
		
		if(answer == JFileChooser.CANCEL_OPTION) return;
		
		File f = ch.getSelectedFile();
		
		if(f == null) return;
		
		String fn = createFileExtension(f, Theme.FILE_EXTENSION);
		
		if(!Theme.saveTheme(fn)) return;
		
		currentFileName = fn;

		addTheme(new File(fn).toURI());
		UndoManager.clear();
		setFrameTitle();
		
		if(!saveItem.isEnabled()) {
			saveItem.setEnabled(true);
		}
	}
	
	private void setFrameTitle() {
		theFrame.setTitle(WINDOW_TITLE + " - " + getDescription());
	}
	
	/**
	 * 
	 * @return false if user decided to cancel, true otherwise
	 */
	private boolean checkThemeState() {
		if(!UndoManager.canUndo()) return true;
		
		Object[] options = null;
		
		if(saveItem.isEnabled()) {
			options = new Object[] {
				"Save", "Save as...", "Discard", "Cancel"
			};
		}
		else {
			options = new Object[] {
				"Save as...", "Discard", "Cancel"
			};
		}
		
		String msg = "Current theme has unsaved changes.";
		String title = "Save changes?";
		
		int answer = JOptionPane.showOptionDialog(theFrame,
			msg, title,
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE,
			null,
			options,
			options[0]);
		
		if(answer == options.length - 1) return false;	// Cancel
		if(answer == options.length - 2) return true;	// Continue
		
		if(answer == 0) {	// 1st option
			if(saveItem.isEnabled()) {	// Save
				saveTheme(false);
			}
			else {						// Save as
				saveTheme(true);
			}
		}
		else if(answer == 1) {	// Save as
			saveTheme(true);
		}
		
		return true;
	}

	private String createFileExtension(File f, String ext) {
		String fn = f.getAbsolutePath();
		
		if(fn.endsWith(ext)) return fn;
		
		if(fn.lastIndexOf(".") < fn.lastIndexOf(File.separator)) {
			return fn + ext;
		}
		
		return fn.substring(0, fn.lastIndexOf(".")) + ext;
	}
	
	private void saveDefaults() {
		Theme.saveTheme(Theme.DEFAULT_THEME);
	}
	
	public static void main(String[] args) {
		TinyLookAndFeel.controlPanelInstantiated = true;
		System.setProperty("swing.aatext", "true");

		// $JAVA_HOME/jre/lib/swing.properties:
		// swing.defaultlaf = de.muntjak.tinylookandfeel.TinyLookAndFeel
		
		// the following also works:
		//System.setProperty("swing.defaultlaf", "de.muntjak.tinylookandfeel.TinyLookAndFeel");
		
		try {
			UIManager.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new ControlPanel();
			}
		});
	}
	
	public class FontColorControl extends SBControl {

		FontColorControl(ColoredFont font) {
			super(font.getSBReference(), true, CONTROLS_FONT);
		}
	}

	/**
	 * HSBControl is used to colorize system icons (brightness, saturation & hue).
	 */
	class HSBControl extends JPanel implements ActionListener, Selectable {
		
		private final int cpSize = 10;
		private HSBReference hsbRef, memRef;
		private Dimension size = new Dimension(46, 18);
		private int index;
		private boolean selected = false;
		
		HSBControl(HSBReference hsbRef, int index) {
			this.hsbRef = hsbRef;
			this.index = index;

			update();
			addMouseListener(new Mousey());
		}
		
		public boolean controlsTreeIcon() {
			return index > 0 && index < 6;
		}
		
		public boolean controlsFrameIcon() {
			return index == 0;
		}
		
		public void actionPerformed(ActionEvent e) {
			// called only from HSBChooser.performAction(),
			// therefore we don't have to store undo data
			colorizeIcon((HSBControl)e.getSource(), true);
		}
		
		void createMemRef() {
			memRef = new HSBReference(hsbRef);
		}

		public HSBReference getHSBReference() {
			return hsbRef;
		}
		
		public HSBReference getUndoReference() {
			return memRef;
		}
		
		public Icon getUncolorizedIcon() {
			return TinyLookAndFeel.getUncolorizedSystemIcon(index);
		}
		
		public int getHue() {
			return hsbRef.getHue();
		}
		
		public int getSaturation() {
			return hsbRef.getSaturation();
		}
		
		public int getBrightness() {
			return hsbRef.getBrightness();
		}
		
		public boolean isPreserveGrey() {
			return hsbRef.isPreserveGrey();
		}
		
		public void setPreserveGrey(boolean b) {
			hsbRef.setPreserveGrey(b);
		}
		
		public int getRef() {
			return hsbRef.getReference();
		}
		
		public void setReference(int ref, boolean updateHue) {
			hsbRef.setReference(ref);
			
			if(updateHue) {
				hsbRef.setHue(ColorRoutines.calculateHue(
					SBReference.getReferencedColor(ref)));
			}
			
			update();
		}
		
		public void setHue(int hue) {
			hsbRef.setHue(hue);
		}
		
		public void setSaturation(int sat) {
			hsbRef.setSaturation(sat);
		}
		
		void setBrightness(int bri) {
			hsbRef.setBrightness(bri);
		}
		
		void update() {
			setBackground(calculateBackground(
				SBReference.getReferencedColor(hsbRef.getReference())));
			repaint();
			updateTTT();
		}
		
		private Color calculateBackground(Color c) {
			float[] f = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
			f[0] = (float)((float)hsbRef.getHue() / 360.0);
			
			return Color.getHSBColor(f[0], f[1], f[2]);
		}
		
		public void calculateHue() {
			Color c = hsbRef.getReferenceColor();
			hsbRef.setHue(ColorRoutines.calculateHue(c));
		}
		
		private void updateTTT() {
			if(hsbRef == null) {
				setToolTipText(null);
				return;
			} 
			
			StringBuffer buff = new StringBuffer();
			
			buff.append("H:" + hsbRef.getHue());
			buff.append(" S:" + hsbRef.getSaturation());
			buff.append(" B:" + hsbRef.getBrightness());
			buff.append(" (" + hsbRef.getReferenceString() + ")");
			
			setToolTipText(buff.toString());
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		
		public void paint(Graphics g) {
			// fill with background
			g.setColor(getBackground());
			g.fillRect(2, 2, getWidth() - 3, getHeight() - 3);
			
			// paint border
			if(selected) {
				g.setColor(Color.DARK_GRAY);
				g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
				g.setColor(Color.RED);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
			else {
				g.setColor(Color.DARK_GRAY);
				g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
				g.setColor(Theme.backColor.getColor());
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
			
			// paint left rectangle
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(2, 2, cpSize, getHeight() - 4);
			g.setColor(Color.BLACK);
			g.fillRect(cpSize + 1, 2, 1, getHeight() - 4);
			
			// paint right rectangle
			int x = getWidth() - 18;
			int grey = 255;
			
			g.setColor(Color.BLACK);
			g.drawLine(x - 1, 2, x - 1, getHeight() - 3);
			
			for(int i = 0; i < 16; i++) {
				g.setColor(new Color(grey, grey, grey));
				g.drawLine(x + i, 2, x + i, getHeight() - 3);
				grey -= 255 / 16;
			}
		}

		class Mousey extends MouseAdapter {
			
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					if(e.getX() <= cpSize) {
						showCPHSBPopup(HSBControl.this);
					}
					else {
						showSBPopup((SBControl)e.getSource());
					}
				}
			}
			
			public void mousePressed(MouseEvent e) {
				requestFocusInWindow();
				
				if(e.isControlDown()) {
					if(!selected) {
						selection.add(HSBControl.this);
					}
					return;
				}
				else if(e.isAltDown()) {
					if(selected) {
						selection.remove(HSBControl.this);
					}
					return;
				}

				if(e.getX() <= cpSize) {
					showCPHSBPopup(HSBControl.this);
					return;
				}
				
				if(e.isPopupTrigger()) {
					showHSBPopup(HSBControl.this);
					return;
				}
				else if(e.getX() > getWidth() - 19) {
					showHSBPopup(HSBControl.this);
					return;
				}

				if(e.getButton() != MouseEvent.BUTTON1) return;

				// copy current data
				memRef = new HSBReference(hsbRef);

				if(!HSBChooser.showDialog(theFrame, HSBControl.this)) {	// cancelled
					hsbRef.setBrightness(memRef.getBrightness());
					hsbRef.setHue(memRef.getHue());
					hsbRef.setPreserveGrey(memRef.isPreserveGrey());
					hsbRef.setSaturation(memRef.getSaturation());

					colorizeIcon(HSBControl.this, iconChecks[index].isSelected());
					return;
				}

				colorizeIcon(HSBControl.this, true);
				storeUndoData(HSBControl.this);
				
				if(!iconChecks[index].isSelected()) {
					iconChecks[index].setSelected(true);
					Theme.colorize[index].setValue(true);
				}

				update();
				updateTTT();
				applySettingsButton.setEnabled(true);
			}
		}
		
		private void printValues(String t) {
			
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			if(this.selected == selected) return;
			
			this.selected = selected;
			repaint();
		}
	}
	
	class SelectSpecialFontAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			updateSpecialFont();
		}
	}
	
	class DerivedFontAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if(resistUpdate) return;
			
			if(e.getSource().equals(isPlainFont)) {
				if(isPlainFont.isSelected()) {
					isBoldFont.setSelected(false);
					storeUndoData(selectedFont);
					selectedFont.setPlainFont(true);
				}
				else {
					storeUndoData(selectedFont);
					selectedFont.setPlainFont(false);
				}
			}
			else if(e.getSource().equals(isBoldFont)) {
				if(isBoldFont.isSelected()) {
					isPlainFont.setSelected(false);
					storeUndoData(selectedFont);
					selectedFont.setBoldFont(true);
				}
				else {
					storeUndoData(selectedFont);
					selectedFont.setBoldFont(false);
				}
			}
			
			specialFontPanel.init(selectedFont);
			updateFont(SPECIAL_FONT);
		}
	}
	
	class SBPopupAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			int ref = Integer.parseInt(e.getActionCommand());
			
			storeUndoData(selectedSBControl);
			selectedSBControl.getSBReference().setReference(ref);
			selectedSBControl.getSBReference().reset();
			selectedSBControl.update();
			initPanels();
			updateColorTTT();
			selectedSBControl.updateTargets(true);
		}
	}
	
	class CPSBPopupAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if("copy".equals(e.getActionCommand())) {
				copiedSBReference = selectedSBControl.sbReference.copy();
				
				if(!pasteSBParametersItem.isEnabled()) {
					pasteSBParametersItem.setEnabled(true);
				}
			}
			else {	// paste
				storeUndoData(selectedSBControl);
				selectedSBControl.sbReference.update(copiedSBReference);
				selectedSBControl.update();
				selectedSBControl.updateTargets(true);
				updateColorTTT();
			}
		}
	}
	
	class HSBPopupAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			int ref = Integer.parseInt(e.getActionCommand());
			
			selectedHSBControl.createMemRef();
			storeUndoData(selectedHSBControl);
			
			if(!iconChecks[selectedHSBControl.index].isSelected()) {
				iconChecks[selectedHSBControl.index].setSelected(true);
			}

			selectedHSBControl.setReference(ref, true);
			colorizeIcon(selectedHSBControl, true);
			applySettingsButton.setEnabled(true);
		}
	}
	
	class CPHSBPopupAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if("copy".equals(e.getActionCommand())) {
				copiedHSBReference = new HSBReference(selectedHSBControl.hsbRef);
				
				if(!pasteHSBParametersItem.isEnabled()) {
					pasteHSBParametersItem.setEnabled(true);
				}
			}
			else {	// paste
				selectedHSBControl.createMemRef();
				storeUndoData(selectedHSBControl);
				
				if(!iconChecks[selectedHSBControl.index].isSelected()) {
					iconChecks[selectedHSBControl.index].setSelected(true);
				}
				
				selectedHSBControl.hsbRef.update(copiedHSBReference);
				selectedHSBControl.update();
				colorizeIcon(selectedHSBControl, true);
				applySettingsButton.setEnabled(true);
			}
		}
	}
	
	/*
	 * Action for "Apply Settings" button
	 */
	class ApplySettingsAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			InsetsControl.confirmChanges();
			IntControl.confirmChanges();
			setTheme();

			if(UndoManager.activateDelayedUndoItems(ControlPanel.this)) {
				undoItem.setText("Undo " + UndoManager.getUndoDescription());
			}
			
			setFrameTitle();
		}
	}
	
	class FontPanel extends JPanel implements ActionListener {
		
		private int type;
		private JComboBox fontFamilyCombo, fontSizeCombo;
		private JCheckBox boldCheck;
		private SBControl colorField;
		
		FontPanel(int type) {
			this.type = type;
			
			setupUI();
		}
		
		private void setupUI() {
			Font theFont = null;
			
			if(type == PLAIN_FONT) {
				theFont = Theme.plainFont.getFont();
			}
			else if(type == BOLD_FONT) {
				theFont = Theme.boldFont.getFont();
			}
			else {	// Special Font
				theFont = selectedFont.getFont();
			}
			
			setLayout(new FlowLayout(FlowLayout.LEFT, 3, 1));
			
			if(type == PLAIN_FONT) {
				setBorder(new TitledBorder("Plain Font"));
			}
			else if(type == BOLD_FONT) {
				setBorder(new TitledBorder("Bold Font"));
			}
			else {
				setBorder(new TitledBorder("Special Font"));
			}
			
			add(new JLabel("Family"));
			fontFamilyCombo = createSchriftarten(theFont);
			fontFamilyCombo.addActionListener(this);
			add(fontFamilyCombo);
			
			add(new JLabel("  Size"));
			fontSizeCombo = createSchriftgroessen(theFont);
			fontSizeCombo.addActionListener(this);
			add(fontSizeCombo);
			
			add(new JLabel("    "));
			boldCheck = new JCheckBox("Bold", theFont.isBold());
			boldCheck.addActionListener(this);
			add(boldCheck);
			
			if(type == SPECIAL_FONT) {
				colorField = new FontColorControl(selectedFont);
				add(colorField);
			}
		}
		
		public String getFontFamily() {
			return (String)fontFamilyCombo.getSelectedItem();
		}
		
		public int getFontSize() {
			return Integer.parseInt(
				(String)fontSizeCombo.getSelectedItem());
		}
		
		public int getFontType() {
			if(boldCheck.isSelected()) {
				return Font.BOLD;
			}
			
			return Font.PLAIN;
		}
		
		public FontUIResource getCurrentFont() {
			return new FontUIResource(getFontFamily(), getFontType(), getFontSize());
		}
		
		public void init(ColoredFont f) {
			resistUpdate = true;
			fontSizeCombo.setSelectedItem(String.valueOf(f.getFont().getSize()));
			fontFamilyCombo.setSelectedItem(f.getFont().getFamily());
			boldCheck.setSelected(f.getFont().isBold());
			resistUpdate = false;
			
			if(colorField == null) return;
			
			resistUpdate = true;
			colorField.setSBReference(f.getSBReference());
			isPlainFont.setSelected(f.isPlainFont());
			isBoldFont.setSelected(f.isBoldFont());
			resistUpdate = false;
		}
		
		private JComboBox createSchriftarten(Font font) {
			Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
			TreeSet family = new TreeSet();
			
			for(int i = 0; i < fonts.length; i++) {
				family.add(fonts[i].getFamily());
			}
			
			JComboBox box = new JComboBox(new Vector(family));
			
			for(int i = 0; i < box.getItemCount(); i++) {
				if(box.getItemAt(i).equals(font.getFamily())) {
					box.setSelectedIndex(i);
					break;
				}
			}
			
			return box;
		}
		
		private JComboBox createSchriftgroessen(Font font) {
			String[] groessen = new String[10];
			groessen[0] = "10";
			groessen[1] = "11";
			groessen[2] = "12";
			groessen[3] = "13";
			groessen[4] = "14";
			groessen[5] = "16";
			groessen[6] = "18";
			groessen[7] = "20";
			groessen[8] = "22";
			groessen[9] = "24";
			JComboBox box = new JComboBox(groessen);
			
			switch (font.getSize()) {
				case 10:
					box.setSelectedIndex(0);
					break;
				case 11:
					box.setSelectedIndex(1);
					break;
				case 12:
					box.setSelectedIndex(2);
					break;
				case 13:
					box.setSelectedIndex(3);
					break;
				case 14:
					box.setSelectedIndex(4);
					break;
				case 16:
					box.setSelectedIndex(5);
					break;
				case 18:
					box.setSelectedIndex(6);
					break;
				case 20:
					box.setSelectedIndex(7);
					break;
				case 22:
					box.setSelectedIndex(8);
					break;
				case 24:
					box.setSelectedIndex(9);
					break;
			}
			
			box.setMaximumRowCount(10);
			return box;
		}
		
		public void actionPerformed(ActionEvent e) {
			if(resistUpdate) return;
			
			// User selected fontFamilyCombo or fontSizeCombo
			// or boldCheck.
			if(type == PLAIN_FONT) {
				storeUndoData(Theme.plainFont);
			}
			else if(type == BOLD_FONT) {
				storeUndoData(Theme.boldFont);
			}
			else if(type == SPECIAL_FONT) {
				storeUndoData(selectedFont);
				selectedFont.setPlainFont(false);
				selectedFont.setBoldFont(false);
			}
			
			updateFont(type);
			specialFontPanel.init(selectedFont);
		}
	}
	
	class ExamplePanel extends JPanel {
		
		private JTabbedPane exampleTab;
		
		ExamplePanel() {
			setupUI();
		}
		
		private void setupUI() {
			setLayout(new BorderLayout());
			JPanel p0 = new JPanel(new BorderLayout(4, 0));
			JPanel p1 = new JPanel(new GridLayout(2, 2, 2, 2));
			JPanel p2 = new JPanel(new BorderLayout(4, 4));
			
			// Scrollables
			SizedPanel sizey = new SizedPanel(60, 130);
			JScrollPane sp = new JScrollPane(sizey,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			sp.setPreferredSize(new Dimension(80, 80));
			sp.getVerticalScrollBar().setUnitIncrement(8);
			sp.getHorizontalScrollBar().setUnitIncrement(8);
			p1.add(sp);
			
			sizey = new SizedPanel(130, 130);
			sp = new JScrollPane(sizey,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			sp.setPreferredSize(new Dimension(80, 80));
			sp.getVerticalScrollBar().setUnitIncrement(8);
			sp.getHorizontalScrollBar().setUnitIncrement(8);
			p1.add(sp);
			
			// List
			exampleList = createList();
			exampleList.setSelectedIndex(1);
			exampleList.setVisibleRowCount(6);
			sp = new JScrollPane(exampleList);
			p1.add(sp);

			// TextAreas
			JPanel p5 = new JPanel(new GridLayout(3, 1));
			JTextArea ta = new JTextArea("  JTextArea\n  enabled");
			p5.add(ta);

			ta = new JTextArea("  JTextArea\n  non-editable");
			ta.setEditable(false);
			p5.add(ta);
			
			ta = new JTextArea("  JTextArea\n  disabled");
			ta.setEnabled(false);
			p5.add(ta);
			
			sp = new JScrollPane(p5,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			p1.add(sp);
			
			p2.add(p1, BorderLayout.NORTH);
			
			// TextPane
			JTextPane textPane = new JTextPane(createStyledDocument());
			textPane.setEditable(false);
			p5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
			p5.add(textPane);
			
			p2.add(p5, BorderLayout.SOUTH);
			
			p0.add(p2, BorderLayout.WEST);
			
			// Buttons
			p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.WEST;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = new Insets(0, 2, 4, 2);
			
			exampleButton = new JButton("JButton");
			exampleButton.setMnemonic(KeyEvent.VK_J);
//			exampleButton.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					Theme.getAvailableThemes();
//					
//				}
//			});
			p1.add(exampleButton, gc);
			
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 4, 0);
			exampleDisabledButton = new JButton("Disabled");
			exampleDisabledButton.setEnabled(false);
			p1.add(exampleDisabledButton, gc);
			
			// ToggleButton
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 4, 2);
			exampleToggleButton = new JToggleButton("JToggleButton");
			p1.add(exampleToggleButton, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 4, 0);
			JCheckBox ch = new JCheckBox("Buttons w/icon", false);
			ch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addButtonIcons(((AbstractButton)e.getSource()).isSelected());
				}
			});
			p1.add(ch, gc);
			
			// CheckBox
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 0, 2);
			ch = new JCheckBox("JCheckBox", false);
			p1.add(ch, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 0, 0);
			ch = new JCheckBox("Disabled", true);
			ch.setEnabled(false);
			p1.add(ch, gc);
			
			// Radio
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 0, 2);
			JRadioButton rb = new JRadioButton("JRadioButton");
			p1.add(rb, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 0, 0);
			rb = new JRadioButton("Disabled", true);
			rb.setEnabled(false);
			p1.add(rb, gc);
			
			// Separators
			gc.fill = gc.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy ++;
			gc.gridwidth = 2;
			gc.insets = new Insets(4, 0, 4, 0);
			p1.add(new JSeparator(), gc);
			gc.gridwidth = 1;
			gc.fill = gc.NONE;
			
			// Combos
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 4, 2);
			JComboBox cb = createCombo("JComboBox");
			p1.add(cb, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 4, 0);
			cb = createCombo("Disabled Combo");
			cb.setEnabled(false);
			p1.add(cb, gc);
			
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 1, 2);
			cb = createCombo("Editable JComboBox");
			cb.setEditable(true);
			p1.add(cb, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 1, 0);
			cb = createCombo("Disabled Editable");
			cb.setEditable(true);
			cb.setEnabled(false);
			p1.add(cb, gc);
			
			// Separators
			gc.fill = gc.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy ++;
			gc.gridwidth = 2;
			gc.insets = new Insets(4, 0, 4, 0);
			p1.add(new JSeparator(), gc);
			gc.gridwidth = 1;
			gc.fill = gc.NONE;
			
			// Text
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 4, 2);
			JTextField tf = new JTextField("JTextField");
			p1.add(tf, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 4, 0);
			tf = new JTextField("Disabled");
			tf.setEnabled(false);
			p1.add(tf, gc);
			
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 4, 2);
			tf = new JTextField("Non-editable Textfield");
			tf.setEditable(false);
			p1.add(tf, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 4, 0);
			tf = new JTextField("Disabled non-editable");
			tf.setEditable(false);
			tf.setEnabled(false);
			p1.add(tf, gc);
			
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 4, 2);
			tf = new JFormattedTextField("JFormattedTextField");
			p1.add(tf, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 4, 0);
			tf = new JFormattedTextField("Disabled");
			tf.setEditable(false);
			tf.setEnabled(false);
			p1.add(tf, gc);
			
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 1, 2);
			tf = new JPasswordField("JPasswordField");
			p1.add(tf, gc);
			gc.gridx ++;
			gc.insets = new Insets(0, 2, 1, 0);
			tf = new JPasswordField("Disabled");
			tf.setEnabled(false);
			p1.add(tf, gc);
			
			// Separators
			gc.fill = gc.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy ++;
			gc.gridwidth = 2;
			gc.insets = new Insets(4, 0, 4, 0);
			p1.add(new JSeparator(), gc);
			gc.gridwidth = 1;
			gc.fill = gc.NONE;
			
			// Spinners
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(0, 2, 2, 2);
			JSpinner spinner = new JSpinner(new SpinnerDateModel());
			p1.add(spinner, gc);

			gc.gridx ++;
			gc.insets = new Insets(0, 2, 2, 0);
			p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			spinner = new JSpinner(new SpinnerNumberModel(42, 0, 359, 1));
			p2.add(spinner);
			p2.add(new JLabel(" "));
			spinner = new JSpinner(new SpinnerDateModel(
				new Date(), null, null, Calendar.DAY_OF_WEEK));
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
			if(df instanceof SimpleDateFormat) {
				JSpinner.DateEditor editor =
					new JSpinner.DateEditor(spinner, ((SimpleDateFormat)df).toPattern());
				spinner.setEditor(editor);
				spinner.setValue(new Date());
			}

			spinner.setEnabled(false);
			p2.add(spinner);
			p1.add(p2, gc);
			p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			p2.add(p1);
			
			p0.add(p2, BorderLayout.CENTER);
			
			// Tree
			JPanel p3 = new JPanel(new BorderLayout());
			p2 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.fill = GridBagConstraints.VERTICAL;
			gc2.gridx = 0;
			gc2.gridy = 0;
			tree1 = new JTree();
			tree1.setCellRenderer(new SwitchTreeIcons(true));
			tree1.setEditable(true);
			tree1.expandPath(tree1.getNextMatch("colors", 0, Position.Bias.Forward));
			tree1.expandPath(tree1.getNextMatch("food", 0, Position.Bias.Forward));
			tree1.setVisibleRowCount(10);
			sp1 = new JScrollPane(tree1);
			sp1.setViewportBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
			p2.add(sp1, gc2);
			
			gc2.gridx ++;
			tree2 = new JTree();
			tree2.setCellRenderer(new SwitchTreeIcons(true));
			DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree2.getModel().getRoot();
			root.setUserObject("JTree disabled");
			tree2.getModel().valueForPathChanged(new TreePath(root.getPath()), "JTree disabled");
			tree2.expandPath(tree2.getNextMatch("sports", 0, Position.Bias.Forward));
			tree2.setEnabled(false);
			tree2.setVisibleRowCount(10);
			sp2 = new JScrollPane(tree2);
			sp2.setViewportBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
			p2.add(sp2, gc2);
			
			JPanel p4 = new JPanel(new BorderLayout());
			p4.add(p2, BorderLayout.CENTER);
			
			// Popup trigger
			p5 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
			JCheckBox check = new JCheckBox("Show Tree Icons", true);
			check.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SwitchTreeIcons renderer =
						(SwitchTreeIcons)tree1.getCellRenderer();
					renderer.setShowIcons(((AbstractButton)e.getSource()).isSelected());
					
					renderer =
						(SwitchTreeIcons)tree2.getCellRenderer();
					renderer.setShowIcons(((AbstractButton)e.getSource()).isSelected());
					
					tree1.revalidate();
					tree2.revalidate();
					repaint();
				}			
			});
			p5.add(check);

			popupTrigger = new PopupTrigger();
			p5.add(popupTrigger);
			p4.add(p5, BorderLayout.NORTH);	
			
			// EditorPane
			URL page = getClass().getResource(
				"/de/muntjak/tinylookandfeel/html/default.html");
			JEditorPane editorPane = null;
			try {
				editorPane = new JEditorPane(page);
				editorPane.setEditable(false);
				editorPane.setPreferredSize(new Dimension(150, 70));
			} catch (IOException e) {
				editorPane = new JEditorPane("text", "Plain Document");
			}
			
			p5 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 3));
			p5.add(editorPane);
			
			p4.add(p5, BorderLayout.SOUTH);	
			
			p3.add(p4, BorderLayout.CENTER);
			
			// ProgressBars & Sliders
			// (we simulate a 6x6 grid)
			p1 = new JPanel(new GridBagLayout());
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.gridwidth = 6;
			gc.insets = new Insets(0, 4, 2, 2);
			
			progressBar1 = new JProgressBar(0, 20);
			progressBar1.setValue(0);
			progressBar1.setStringPainted(true);
			progressBar1.addMouseListener(progressBarAction);
			progressBar1.setToolTipText("Click to start/stop");
			p1.add(progressBar1, gc);
			
			gc.gridy ++;
			gc.fill = GridBagConstraints.VERTICAL;
			gc.gridwidth = 1;
			gc.gridheight = 5;
			gc.insets = new Insets(0, 4, 0, 2);
			progressBar2 = new JProgressBar(JProgressBar.VERTICAL, 0, 20);
			progressBar2.setValue(0);
			progressBar2.setStringPainted(true);
			progressBar2.addMouseListener(progressBarAction);
			progressBar2.setToolTipText("Click to start/stop");
			p1.add(progressBar2, gc);
			
			gc.gridx ++;
			gc.gridwidth = 5;
			gc.gridheight = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.insets = new Insets(0, 0, 2, 2);
			progressBar3 = new JProgressBar(0, 20);
			progressBar3.setValue(0);
			progressBar3.addMouseListener(progressBarAction);
			progressBar3.setToolTipText("Click to start/stop");
			p1.add(progressBar3, gc);
			
			gc.gridy ++;
			gc.fill = GridBagConstraints.VERTICAL;
			gc.gridwidth = 1;
			gc.gridheight = 4;
			gc.insets = new Insets(0, 0, 0, 2);
			progressBar4 = new JProgressBar(JProgressBar.VERTICAL, 0, 20);
			progressBar4.setValue(0);
			progressBar4.addMouseListener(progressBarAction);
			progressBar4.setToolTipText("Click to start/stop");
			p1.add(progressBar4, gc);
			
			gc.gridx ++;
			gc.gridwidth = 4;
			gc.gridheight = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.insets = new Insets(0, 0, 2, 2);
			slider1 = new JSlider(JSlider.HORIZONTAL, 0, 80, 30);
			slider1.setMajorTickSpacing(20);
			slider1.setMinorTickSpacing(10);
			slider1.setPaintTicks(true);
			slider1.setPaintLabels(true);
			Dimension d = slider1.getPreferredSize();
			d.width = 80;
			slider1.setPreferredSize(d);
			p1.add(slider1, gc);
			
			gc.gridy ++;
			gc.fill = GridBagConstraints.VERTICAL;
			gc.gridwidth = 1;
			gc.gridheight = 3;
			gc.insets = new Insets(0, 0, 0, 2);
			slider2 = new JSlider(JSlider.VERTICAL, 0, 80, 50);
			slider2.setMajorTickSpacing(20);
			slider2.setMinorTickSpacing(10);
			slider2.setPaintTicks(true);
			slider2.setPaintLabels(true);
			d = slider2.getPreferredSize();
			d.height = 80;
			slider2.setPreferredSize(d);
			p1.add(slider2, gc);
			
			gc.gridx ++;
			gc.gridwidth = 3;
			gc.gridheight = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.insets = new Insets(0, 0, 2, 2);
			slider3 = new JSlider(JSlider.HORIZONTAL, 0, 80, 40);
			d = slider3.getPreferredSize();
			d.width = 80;
			slider3.setPreferredSize(d);
			p1.add(slider3, gc);
			
			gc.gridy ++;
			gc.fill = GridBagConstraints.VERTICAL;
			gc.gridwidth = 1;
			gc.gridheight = 2;
			gc.insets = new Insets(0, 0, 0, 2);
			slider4 = new JSlider(JSlider.VERTICAL, 0, 80, 40);
			d = slider4.getPreferredSize();
			d.height = 80;
			slider4.setPreferredSize(d);
			p1.add(slider4, gc);

			gc.gridx ++;
			gc.gridwidth = 2;
			gc.gridheight = 1;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.insets = new Insets(0, 2, 0, 0);
			slider5 = new JSlider(JSlider.HORIZONTAL, 0, 40, 30);
			slider5.setEnabled(false);
			d = slider5.getPreferredSize();
			d.width = 80;
			slider5.setPreferredSize(d);
			p1.add(slider5, gc);
			
			gc.gridy ++;
			gc.fill = GridBagConstraints.VERTICAL;
			gc.gridwidth = 1;
			gc.gridheight = 1;
			gc.insets = new Insets(0, 0, 0, 0);
			slider6 = new JSlider(JSlider.VERTICAL, 0, 40, 10);
			slider6.setEnabled(false);
			slider6.setMajorTickSpacing(20);
			slider6.setMinorTickSpacing(10);
			slider6.setPaintTicks(true);
			slider6.setPaintLabels(true);
			d = slider6.getPreferredSize();
			d.height = 120;
			slider6.setPreferredSize(d);
			p1.add(slider6, gc);
			// end ProgressBars & Sliders
			
			p3.add(p1, BorderLayout.EAST);
			
			p0.add(p3, BorderLayout.EAST);
			
			p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
			p2.setBorder(new EtchedBorder());
			p2.add(p0);			
			add(p2, BorderLayout.NORTH);
			
			desktopPane = new ExampleDesktopPane();
			add(desktopPane, BorderLayout.CENTER);
		}
		
		private JList createList() {
			String[] items = new String[11];
			items[0] = "A JList";
			items[1] = "can have";
			items[2] = "zero to";
			items[3] = "many items";
			items[4] = "and can be";
			items[5] = "scrolled";
			items[6] = "(or not)";
			items[7] = "[Wait! -";
			items[8] = "give it";
			items[9] = "some more";
			items[10] = "items ...]";
			
			return new JList(items);
		}
		
		private JComboBox createCombo(String s) {
			return new JComboBox(new String[] {
				s, "can have", "zero to", "many items",
				"and can be", "triggered", "many times"
			});
		}
		
		public void update(boolean forceUpdate) {
			updateTheme();
			
			if(forceUpdate) {
				applySettingsButton.setEnabled(true);
			}
			
			theFrame.repaint();
		}
		
		class SwitchTreeIcons extends DefaultTreeCellRenderer {
			
			private boolean showIcons;
			
			SwitchTreeIcons(boolean showIcons) {
				this.showIcons = showIcons;
			}
			
			void setShowIcons(boolean b) {
				showIcons = b;
			}
			
			public Icon getClosedIcon() {
				if(showIcons) {
					return super.getClosedIcon();
				}
				
				return null;
			}
			
			public Icon getOpenIcon() {
				if(showIcons) {
					return super.getOpenIcon();
				}
				
				return null;
			}
			
			public Icon getLeafIcon() {
				if(showIcons) {
					return super.getLeafIcon();
				}
				
				return null;
			}
			
			public Icon getDisabledIcon() {
				if(showIcons) {
					return super.getDisabledIcon();
				}
				
				return null;
			}
		}
		
		class ContentLabel extends JLabel {
			
			ContentLabel() {
				super("Content");
				setOpaque(true);
				setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));
				setBackground(new Color(224, 224, 224));
				setForeground(Color.LIGHT_GRAY);
				setFont(getFont().deriveFont(24f));
			}
		}
		
		class ExampleDesktopPane extends JDesktopPane {
			
			private final Dimension preferredSize = new Dimension(780, 140);
			
			ExampleDesktopPane() {
				setupUI();
			}
			
			public Dimension getPreferredSize() {
				return preferredSize;
			}
			
			private void setupUI() {
				JPanel p0 = new JPanel();
				p0.setBounds(0, 0, preferredSize.width, preferredSize.height);

				// Table
				exampleTable = new JTable(new TinyTableModel());
				exampleTable.setRowSelectionAllowed(true);
				exampleTable.setColumnSelectionAllowed(true);
				exampleTable.setColumnSelectionInterval(2, 2);
				exampleTable.setRowSelectionInterval(0, 2);
				exampleTable.setDefaultRenderer(
					TinyTableModel.TableColorIcon.class, new IconRenderer());
				exampleTable.getColumnModel().getColumn(2).setMinWidth(50);

				JScrollPane sp = new JScrollPane(exampleTable);

				// Disabled TabbedPane
				exampleTab = new JTabbedPane();
				exampleTab.add("Disabled", new ContentLabel());
				exampleTab.add("Tabbed", new ContentLabel());
				exampleTab.add("Pane", new ContentLabel());
				exampleTab.setEnabled(false);
				exampleTab.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 1));
				exampleTab.setPreferredSize(new Dimension(180, 60));
				
				// SplitPane for exampleTable and exampleTab - new in 1.4.0
				JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					true, sp, exampleTab);
				split.setBounds(2, 2, 392, 135);
				split.setOneTouchExpandable(true);	// just to show the arrows
				split.setDividerLocation(0.55);
				add(split, JDesktopPane.DEFAULT_LAYER);
				
				// Internal Frame
				internalFrames = new Component[2];
				internalFrame = new JInternalFrame("InternalFrame", true, true, true, true);
				internalFrames[0] = internalFrame;
				internalFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				
				internalFrame.addInternalFrameListener(new InternalFrameAdapter() {				
					public void internalFrameClosing(InternalFrameEvent e) {
						internalFrame.setVisible(false);
						
						new Timer(1500, new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								internalFrame.show();
								
								((Timer)e.getSource()).stop();
							}
						}).start();
					}
				});
				
				magnifierPanel = new MagnifierPanel(240, 120);
				internalFrame.getContentPane().add(magnifierPanel);
				internalFrame.pack();
				Dimension frameSize = internalFrame.getPreferredSize();
				internalFrame.setBounds(split.getWidth() + 6, 2, frameSize.width, 135);
				internalFrame.show();
				add(internalFrame, JDesktopPane.PALETTE_LAYER);

				// Palette
				palette = new JInternalFrame("Palette", false, true, true, true);
				internalFrames[1] = palette;
				palette.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
				palette.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				
				palette.addInternalFrameListener(new InternalFrameAdapter() {				
					public void internalFrameClosing(InternalFrameEvent e) {
						palette.setVisible(false);
						
						new Timer(1500, new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								palette.show();
								
								((Timer)e.getSource()).stop();
							}
						}).start();
					}
				});
				
				// add a default button
				palette.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 0, 12));
				JButton def = new JButton("Default button");
				palette.getRootPane().setDefaultButton(def);
				palette.getContentPane().add(def);

				// Note: palette location will be set again as
				// our main frame is pack()'ed
				palette.setBounds(split.getWidth() + internalFrame.getWidth() + 18,
					2, def.getPreferredSize().width + 32, 120);
				palette.show();
				add(palette, JDesktopPane.PALETTE_LAYER);
			}
		}
	}
	
	class ScrollBarCP extends CP {
		
		private JCheckBox rolloverEnabled;
		
		ScrollBarCP() {
			super();
			super.setupUI(setupUI());
		}

		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "ScrollBar");
			
			ps.addParameter(scrollSizeControl);
			ps.addParameter(rolloverEnabled.isSelected(),
				Theme.scrollRollover);

			ps.addParameter(scrollThumbField);
			ps.addParameter(scrollButtField);
			ps.addParameter(scrollArrowField);
			ps.addParameter(trackField);
			ps.addParameter(scrollThumbRolloverBg);
			ps.addParameter(scrollThumbPressedBg);
			ps.addParameter(scrollThumbDisabledBg);
			ps.addParameter(trackBorder);
			ps.addParameter(scrollButtRolloverBg);
			ps.addParameter(scrollButtPressedBg);
			ps.addParameter(scrollButtDisabledBg);
			ps.addParameter(trackDisabled);
			ps.addParameter(trackBorderDisabled);
			ps.addParameter(scrollArrowDisabled);
			ps.addParameter(scrollGripDark);
			ps.addParameter(scrollGripLight);
			ps.addParameter(scrollBorder);
			ps.addParameter(scrollLight);
			ps.addParameter(scrollBorderDisabled);
			ps.addParameter(scrollLightDisabled);
			ps.addParameter(scrollPane);

			ps.addParameter(scrollSpreadDark);
			ps.addParameter(scrollSpreadLight);
			ps.addParameter(scrollSpreadDarkDisabled);
			ps.addParameter(scrollSpreadLightDisabled);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			
			// Size
			gc.insets = insets0404;
			p1.add(new JLabel("Size"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			gc.gridheight = 2;
			scrollSizeControl = new IntControl(new SpinnerNumberModel(17, 14, 64, 1),
				Theme.scrollSize, true, "ScrollBar Size");
			p1.add(scrollSizeControl , gc);
			
			// Thumb
			gc.gridheight = 1;
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Thumb Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollThumbField = new SBControl(
				Theme.scrollThumbColor, CONTROLS_SCROLLBAR);
			p1.add(scrollThumbField, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollThumbRolloverBg = new SBControl(
				Theme.scrollThumbRolloverColor, CONTROLS_SCROLLBAR);
			p1.add(scrollThumbRolloverBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Pressed Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollThumbPressedBg = new SBControl(
				Theme.scrollThumbPressedColor, CONTROLS_SCROLLBAR);
			p1.add(scrollThumbPressedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollThumbDisabledBg = new SBControl(
				Theme.scrollThumbDisabledColor, CONTROLS_SCROLLBAR);
			p1.add(scrollThumbDisabledBg, gc);
			
			// Grip
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			JLabel l = new JLabel("<html>Grip Dark Color <b>*");
			l.setIconTextGap(2);
			l.setHorizontalTextPosition(JLabel.LEADING);
			l.setVerticalTextPosition(JLabel.TOP);
			p1.add(l, gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollGripDark = new SBControl(
				Theme.scrollGripDarkColor, CONTROLS_SCROLLBAR);
			p1.add(scrollGripDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			l = new JLabel("<html>Grip Light Color <b>*");
			l.setIconTextGap(2);
			l.setHorizontalTextPosition(JLabel.LEADING);
			l.setVerticalTextPosition(JLabel.TOP);
			p1.add(l, gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollGripLight = new SBControl(
				Theme.scrollGripLightColor, CONTROLS_SCROLLBAR);
			p1.add(scrollGripLight, gc);
			
			gc.gridy ++;
			gc.gridheight = 3;
			gc.insets = new Insets(6, 8, 0, 4);
			l = new JLabel("<html><b>*</b> Only saturation<br>" +
			"and lightness<br>are considered.");
			l.setVerticalTextPosition(JLabel.TOP);
			l.setBackground(INFO_COLOR);
			l.setForeground(Color.BLACK);
			l.setOpaque(true);
			l.setIconTextGap(2);
			l.setBorder(INFO_BORDER);
			p1.add(l, gc);
			
			// Button
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 1;
			gc.insets = insets0804;
			p1.add(new JLabel("Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollButtField = new SBControl(
				Theme.scrollButtColor, CONTROLS_SCROLLBAR);
			p1.add(scrollButtField, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollButtRolloverBg = new SBControl(
				Theme.scrollButtRolloverColor, CONTROLS_SCROLLBAR);
			p1.add(scrollButtRolloverBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Pressed Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollButtPressedBg = new SBControl(
				Theme.scrollButtPressedColor, CONTROLS_SCROLLBAR);
			p1.add(scrollButtPressedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollButtDisabledBg = new SBControl(
				Theme.scrollButtDisabledColor, CONTROLS_SCROLLBAR);
			p1.add(scrollButtDisabledBg, gc);
			
			// Spread
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Spread Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollSpreadLight = new SpreadControl(
				Theme.scrollSpreadLight, 20, CONTROLS_SCROLLBAR);
			p1.add(scrollSpreadLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Spread Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollSpreadDark = new SpreadControl(
				Theme.scrollSpreadDark, 20, CONTROLS_SCROLLBAR);
			p1.add(scrollSpreadDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollSpreadLightDisabled = new SpreadControl(
				Theme.scrollSpreadLightDisabled, 20, CONTROLS_SCROLLBAR);
			p1.add(scrollSpreadLightDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollSpreadDarkDisabled = new SpreadControl(
				Theme.scrollSpreadDarkDisabled, 20, CONTROLS_SCROLLBAR);
			p1.add(scrollSpreadDarkDisabled, gc);
			
			// Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollBorder = new SBControl(
				Theme.scrollBorderColor, CONTROLS_SCROLLBAR);
			p1.add(scrollBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Border Light Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollLight = new SBControl(
				Theme.scrollBorderLightColor, CONTROLS_SCROLLBAR);
			p1.add(scrollLight, gc);

			// Border disabled
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollBorderDisabled = new SBControl(
				Theme.scrollBorderDisabledColor, CONTROLS_SCROLLBAR);
			p1.add(scrollBorderDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollLightDisabled = new SBControl(
				Theme.scrollLightDisabledColor, CONTROLS_SCROLLBAR);
			p1.add(scrollLightDisabled, gc);
			
			// Track
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 1;
			gc.insets = insets0804;
			p1.add(new JLabel("Track Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			trackField = new SBControl(
				Theme.scrollTrackColor, CONTROLS_SCROLLBAR);
			p1.add(trackField, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Track Disabled"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			trackDisabled = new SBControl(
				Theme.scrollTrackDisabledColor, CONTROLS_SCROLLBAR);
			p1.add(trackDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Track Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			trackBorder = new SBControl(
				Theme.scrollTrackBorderColor, CONTROLS_SCROLLBAR);
			p1.add(trackBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Track B."), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			trackBorderDisabled = new SBControl(
				Theme.scrollTrackBorderDisabledColor, CONTROLS_SCROLLBAR);
			p1.add(trackBorderDisabled, gc);
			
			// Arrow
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Arrow Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollArrowField = new SBControl(
				Theme.scrollArrowColor, CONTROLS_SCROLLBAR);
			p1.add(scrollArrowField, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Arrow Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollArrowDisabled = new SBControl(
				Theme.scrollArrowDisabledColor, CONTROLS_SCROLLBAR);
			p1.add(scrollArrowDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("ScrollPane Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			scrollPane = new SBControl(
				Theme.scrollPaneBorderColor, CONTROLS_ALL);
			p1.add(scrollPane, gc);
			
			gc.gridy = 7;
			gc.insets = new Insets(0, 8, 0, 4);
			rolloverEnabled = new BooleanControl(
				Theme.scrollRollover, "Paint Rollover", CONTROLS_SCROLLBAR);
			p1.add(rolloverEnabled, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;

			rolloverEnabled.setSelected(Theme.scrollRollover.getValue());
			scrollSizeControl.commitValue(Theme.scrollSize.getValue());
			scrollThumbField.update();
			scrollButtField.update();
			scrollArrowField.update();
			trackField.update();
			scrollThumbRolloverBg.update();
			scrollThumbPressedBg.update();
			scrollThumbDisabledBg.update();
			trackBorder.update();
			scrollButtRolloverBg.update();
			scrollButtPressedBg.update();
			scrollButtDisabledBg.update();
			trackDisabled.update();
			trackBorderDisabled.update();
			scrollArrowDisabled.update();
			scrollGripDark.update();
			scrollGripLight.update();
			scrollBorder.update();
			scrollLight.update();
			scrollBorderDisabled.update();
			scrollLightDisabled.update();
			scrollPane.update();
			scrollSpreadDark.init();
			scrollSpreadLight.init();
			scrollSpreadDarkDisabled.init();
			scrollSpreadLightDisabled.init();
			
			inited = true;
		}
	}
	
	class SliderCP extends CP {
		
		private JCheckBox rolloverEnabled;
		private JCheckBox focusEnabled;
		
		SliderCP() {
			super();
			super.setupUI(setupUI());
		}

		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Slider");
			
			ps.addParameter(rolloverEnabled.isSelected(),
				Theme.sliderRolloverEnabled);
			ps.addParameter(focusEnabled.isSelected(),
				Theme.sliderFocusEnabled);
			
			ps.addParameter(Theme.labelFont);

			ps.addParameter(sliderThumbField);
			ps.addParameter(sliderThumbRolloverBg);
			ps.addParameter(sliderThumbPressedBg);
			ps.addParameter(sliderThumbDisabledBg);
			ps.addParameter(sliderBorder);
			ps.addParameter(sliderDark);
			ps.addParameter(sliderLight);
			ps.addParameter(sliderDisabledBorder);
			ps.addParameter(sliderTrack);
			ps.addParameter(sliderTrackBorder);
			ps.addParameter(sliderTrackDark);
			ps.addParameter(sliderTrackLight);
			ps.addParameter(sliderTick);
			ps.addParameter(sliderTickDisabled);
			ps.addParameter(sliderFocusColor);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			// Thumb
			p1.add(new JLabel("Thumb Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			sliderThumbField = new SBControl(
				Theme.sliderThumbColor, CONTROLS_SLIDER);
			p1.add(sliderThumbField, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			sliderThumbRolloverBg = new SBControl(
				Theme.sliderThumbRolloverColor, CONTROLS_SLIDER);
			p1.add(sliderThumbRolloverBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Pressed Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			sliderThumbPressedBg = new SBControl(
				Theme.sliderThumbPressedColor, CONTROLS_SLIDER);
			p1.add(sliderThumbPressedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			sliderThumbDisabledBg = new SBControl(
				Theme.sliderThumbDisabledColor, CONTROLS_SLIDER);
			p1.add(sliderThumbDisabledBg, gc);
			
			// border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderBorder = new SBControl(
				Theme.sliderBorderColor, CONTROLS_SLIDER);
			p1.add(sliderBorder, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Dark Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderDark = new SBControl(
				Theme.sliderDarkColor, CONTROLS_SLIDER);
			p1.add(sliderDark, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Light Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderLight = new SBControl(
				Theme.sliderLightColor, CONTROLS_SLIDER);
			p1.add(sliderLight, gc);
			
			gc.gridy += 2;
			gc.insets = new Insets(0, 8, 0, 4);
			gc.gridwidth = 2;
			rolloverEnabled = new BooleanControl(
				Theme.sliderRolloverEnabled, "Paint Rollover", CONTROLS_SLIDER);
			p1.add(rolloverEnabled, gc);
			
			// disabled border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			gc.gridwidth = 1;
			p1.add(new JLabel("Disabled Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderDisabledBorder = new SBControl(
				Theme.sliderBorderDisabledColor, CONTROLS_SLIDER);
			p1.add(sliderDisabledBorder, gc);

			// Track
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Track Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderTrack = new SBControl(
				Theme.sliderTrackColor, CONTROLS_SLIDER);
			p1.add(sliderTrack, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Track Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderTrackBorder = new SBControl(
				Theme.sliderTrackBorderColor, CONTROLS_SLIDER);
			p1.add(sliderTrackBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Track Border Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderTrackDark = new SBControl(
				Theme.sliderTrackDarkColor, CONTROLS_SLIDER);
			p1.add(sliderTrackDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Track Border Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderTrackLight = new SBControl(
				Theme.sliderTrackLightColor, CONTROLS_SLIDER);
			p1.add(sliderTrackLight, gc);
			
			// Ticks
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Ticks Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderTick = new SBControl(
				Theme.sliderTickColor, CONTROLS_SLIDER);
			p1.add(sliderTick, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Ticks Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderTickDisabled = new SBControl(
				Theme.sliderTickDisabledColor, CONTROLS_SLIDER);
			p1.add(sliderTickDisabled, gc);
			
			// Focus
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Focus Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			sliderFocusColor = new SBControl(
				Theme.sliderFocusColor, CONTROLS_SLIDER);
			p1.add(sliderFocusColor, gc);
			
			gc.gridy = 3;
			gc.insets = insets0804;
			focusEnabled = new BooleanControl(
				Theme.sliderFocusEnabled, "Paint Focus", CONTROLS_SLIDER);
			p1.add(focusEnabled, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			rolloverEnabled.setSelected(Theme.sliderRolloverEnabled.getValue());
			focusEnabled.setSelected(Theme.sliderFocusEnabled.getValue());
			
			sliderThumbField.update();
			sliderThumbRolloverBg.update();
			sliderThumbPressedBg.update();
			sliderThumbDisabledBg.update();
			sliderBorder.update();
			sliderDark.update();
			sliderLight.update();
			sliderDisabledBorder.update();
			sliderTrack.update();
			sliderTrackBorder.update();
			sliderTrackDark.update();
			sliderTrackLight.update();
			sliderTick.update();
			sliderTickDisabled.update();
			sliderFocusColor.update();
			
			inited = true;
		}
	}
	
	class ToolBarCP extends CP {
		
		private JCheckBox focusEnabled;
		private InsetsControl mTop, mLeft, mBottom, mRight;
		
		ToolBarCP() {
			super();
			super.setupUI(setupUI());
		}

		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "ToolBar");

			ps.addParameter(focusEnabled.isSelected(),
				Theme.toolFocus);

			ps.addParameter(toolBar);
			ps.addParameter(toolBarDark);
			ps.addParameter(toolBarLight);
			ps.addParameter(toolButt);
			ps.addParameter(toolButtRollover);
			ps.addParameter(toolButtPressed);
			ps.addParameter(toolButtSelected);
			ps.addParameter(toolBorder);
			ps.addParameter(toolBorderRollover);
			ps.addParameter(toolBorderPressed);
			ps.addParameter(toolBorderSelected);
			ps.addParameter(toolGripDark);
			ps.addParameter(toolGripLight);
			ps.addParameter(toolSeparator);

			ps.addParameter(new Insets(
					mTop.getIntValue(),
					mLeft.getIntValue(),
					mBottom.getIntValue(),
					mRight.getIntValue()),
				Theme.toolMargin);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			// ToolBar
			p1.add(new JLabel("ToolBar Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			toolBar = new SBControl(
				Theme.toolBarColor, CONTROLS_TOOLBAR);
			p1.add(toolBar, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("ToolBar Light Border"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			toolBarLight = new SBControl(
				Theme.toolBarLightColor, CONTROLS_TOOLBAR);
			p1.add(toolBarLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("ToolBar Dark Border"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			toolBarDark = new SBControl(
				Theme.toolBarDarkColor, CONTROLS_TOOLBAR);
			p1.add(toolBarDark, gc);
			
			// Button
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolButt = new SBControl(
				Theme.toolButtColor, CONTROLS_TOOLBAR);
			p1.add(toolButt, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Button Rollover Col"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolButtRollover = new SBControl(
				Theme.toolButtRolloverColor, CONTROLS_TOOLBAR);
			p1.add(toolButtRollover, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Button Pressed Col"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolButtPressed = new SBControl(
				Theme.toolButtPressedColor, CONTROLS_TOOLBAR);
			p1.add(toolButtPressed, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Button Selected Col"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolButtSelected = new SBControl(
				Theme.toolButtSelectedColor, CONTROLS_TOOLBAR);
			p1.add(toolButtSelected, gc);
			
			// Button Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Button Border Col"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolBorder = new SBControl(
				Theme.toolBorderColor, CONTROLS_TOOLBAR);
			p1.add(toolBorder, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Border Rollover Col"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolBorderRollover = new SBControl(
				Theme.toolBorderRolloverColor, CONTROLS_TOOLBAR);
			p1.add(toolBorderRollover, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Border Pressed Col"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolBorderPressed = new SBControl(
				Theme.toolBorderPressedColor, CONTROLS_TOOLBAR);
			p1.add(toolBorderPressed, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Border Selected Col"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolBorderSelected = new SBControl(
				Theme.toolBorderSelectedColor, CONTROLS_TOOLBAR);
			p1.add(toolBorderSelected, gc);
			
			// grip
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Grip Dark Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolGripDark = new SBControl(
				Theme.toolGripDarkColor, CONTROLS_TOOLBAR);
			p1.add(toolGripDark, gc);			
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Grip Light Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolGripLight = new SBControl(
				Theme.toolGripLightColor, CONTROLS_TOOLBAR);
			p1.add(toolGripLight, gc);
			
			// separator
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Separator Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toolSeparator = new SBControl(
				Theme.toolSeparatorColor, CONTROLS_TOOLBAR);
			p1.add(toolSeparator, gc);	
			
			gc.gridy += 4;
			gc.insets = insets0804;
			gc.gridheight = 2;
			focusEnabled = new BooleanControl(
				Theme.toolFocus, "Paint Focus", CONTROLS_TOOLBAR);
			p1.add(focusEnabled, gc);
			
			// Margin
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 6;
			gc.gridwidth = 1;
			gc.insets = new Insets(0, 16, 0, 4);
			
			JPanel p2 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			
			
			gc2.anchor = GridBagConstraints.CENTER;
			gc2.fill = GridBagConstraints.NONE;
			gc2.gridwidth = 3;
			gc2.gridx = 0;
			gc2.gridy = 0;
			gc2.insets = new Insets(0, 0, 4, 0);
			p2.add(new JLabel("Button margin"), gc2);
			
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridwidth = 1;
			gc2.gridy = 2;
			gc2.insets = new Insets(0, 0, 0, 0);
			
			mLeft = new InsetsControl(new SpinnerNumberModel(4, 1, 99, 1),
				Theme.toolMargin, InsetsControl.LEFT);
			p2.add(mLeft, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 1;
			mTop = new InsetsControl(new SpinnerNumberModel(4, 1, 99, 1),
				Theme.toolMargin, InsetsControl.TOP);
			p2.add(mTop, gc2);
			
			gc2.gridy += 2;
			mBottom = new InsetsControl(new SpinnerNumberModel(4, 1, 99, 1),
				Theme.toolMargin, InsetsControl.BOTTOM);
			p2.add(mBottom, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 2;
			mRight = new InsetsControl(new SpinnerNumberModel(4, 1, 99, 1),
				Theme.toolMargin, InsetsControl.RIGHT);
			p2.add(mRight, gc2);
			
			p1.add(p2, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			focusEnabled.setSelected(Theme.toolFocus.getValue());
			
			toolBar.update();
			toolBarDark.update();
			toolBarLight.update();
			toolButt.update();
			toolButtRollover.update();
			toolButtPressed.update();
			toolButtSelected.update();
			toolBorder.update();
			toolBorderRollover.update();
			toolBorderPressed.update();
			toolBorderSelected.update();
			toolGripDark.update();
			toolGripLight.update();
			toolSeparator.update();
			
			mTop.setValue(Theme.toolMargin.top);
			mLeft.setValue(Theme.toolMargin.left);
			mBottom.setValue(Theme.toolMargin.bottom);
			mRight.setValue(Theme.toolMargin.right);
			
			inited = true;
		}
	}
	
	class TableCP extends CP {
		
		private JCheckBox focusEnabled;
		
		TableCP() {
			super();
			super.setupUI(setupUI());
		}

		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Table");

			ps.addParameter(Theme.tableFont);
			ps.addParameter(Theme.tableHeaderFont);
			
			ps.addParameter(tableBack);
			ps.addParameter(tableHeaderBack);
			ps.addParameter(tableHeaderRolloverBack);
			ps.addParameter(tableHeaderRollover);
			ps.addParameter(tableHeaderArrow);
			ps.addParameter(tableGrid);
			ps.addParameter(tableSelectedBack);
			ps.addParameter(tableSelectedFore);
			ps.addParameter(tableBorderDark);
			ps.addParameter(tableBorderLight);
			ps.addParameter(tableHeaderDark);
			ps.addParameter(tableHeaderLight);
			ps.addParameter(tableFocusBorder);
			ps.addParameter(tableAlternateRow);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			// Back
			p1.add(new JLabel("Background Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tableBack = new SBControl(
				Theme.tableBackColor, true, CONTROLS_TABLE);
			p1.add(tableBack, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Grid Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tableGrid = new SBControl(
				Theme.tableGridColor, true, CONTROLS_TABLE);
			p1.add(tableGrid, gc);
			gc.gridy ++;
			
			// Border
			gc.insets = insets4404;
			p1.add(new JLabel("Border Dark Col"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tableBorderDark = new SBControl(
				Theme.tableBorderDarkColor, CONTROLS_TABLE);
			p1.add(tableBorderDark, gc);
			
			gc.gridy ++;
			gc.insets = insets4404;
			p1.add(new JLabel("Border Light Col"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tableBorderLight = new SBControl(
				Theme.tableBorderLightColor, CONTROLS_TABLE);
			p1.add(tableBorderLight, gc);
			
			// Header Colors
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Header Background"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableHeaderBack = new SBControl(
				Theme.tableHeaderBackColor, true, CONTROLS_TABLE);
			p1.add(tableHeaderBack, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("<html>H. Rollover Background <b>*</b>"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableHeaderRolloverBack = new SBControl(
				Theme.tableHeaderRolloverBackColor, CONTROLS_TABLE);
			p1.add(tableHeaderRolloverBack, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("<html>Header Rollover Color <b>*</b>"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableHeaderRollover = new SBControl(
				Theme.tableHeaderRolloverColor, true, CONTROLS_TABLE);
			p1.add(tableHeaderRollover, gc);
			gc.gridy ++;
			
			gc.fill = GridBagConstraints.NONE;
			gc.gridwidth = 2;
			gc.gridheight = 2;
			gc.insets = new Insets(2, 8, 0, 4);
			JLabel info = new JLabel("<html>" +
				"<b>*</b> Considered only with tables implementing" +
			"<br>de.muntjak.tinylookandfeel.table.SortableTableData");
			info.setOpaque(true);
			info.setBackground(INFO_COLOR);
			info.setForeground(Color.BLACK);
			info.setBorder(INFO_BORDER);
			p1.add(info, gc);
			
			// Header Border
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridwidth = 1;
			gc.gridheight = 1;
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Header Border Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableHeaderDark = new SBControl(
				Theme.tableHeaderDarkColor, true, CONTROLS_TABLE);
			p1.add(tableHeaderDark, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Header Border Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableHeaderLight = new SBControl(
				Theme.tableHeaderLightColor, true, CONTROLS_TABLE);
			p1.add(tableHeaderLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("<html>Header Arrow Color <b>*</b>"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableHeaderArrow = new SBControl(
				Theme.tableHeaderArrowColor, CONTROLS_TABLE);
			p1.add(tableHeaderArrow, gc);
			
			// Selected
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Selected Cell Background"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableSelectedBack = new SBControl(
				Theme.tableSelectedBackColor, true, CONTROLS_TABLE);
			p1.add(tableSelectedBack, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Selected Cell Foreground"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableSelectedFore = new SBControl(
				Theme.tableSelectedForeColor, true, CONTROLS_TABLE);
			p1.add(tableSelectedFore, gc);
			
			// Focus border color
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Focus Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableFocusBorder = new SBControl(
				Theme.tableFocusBorderColor, true, CONTROLS_TABLE);
			p1.add(tableFocusBorder, gc);
			
			// Alternate row color
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Alternate Row Color **"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tableAlternateRow = new SBControl(
				Theme.tableAlternateRowColor, true, CONTROLS_TABLE);
			p1.add(tableAlternateRow, gc);
			
			// Table model radios
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = new Insets(0, 16, 0, 4);
			gc.gridheight = 7;
			
			JPanel p2 = new JPanel(new BorderLayout(0, 4));
			JPanel p = new JPanel(new GridLayout(3, 1));
			p.setBorder(new TitledBorder("Table properties (not saved)"));
			
			JCheckBox check = new JCheckBox("Sortable table model", true);
			check.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBox check = (JCheckBox)e.getSource();
					
					if(check.isSelected()) {
						exampleTable.setModel(new TinyTableModel());
						exampleTable.setColumnSelectionInterval(2, 2);
						exampleTable.setRowSelectionInterval(0, 2);
					}
					else {
						exampleTable.setModel(new NonSortableTableModel());
						exampleTable.setColumnSelectionInterval(2, 2);
						exampleTable.setRowSelectionInterval(0, 3);
					}
					
					exampleTable.getColumnModel().getColumn(2).setMinWidth(50);
				}
			});
			p.add(check);
			
			check = new JCheckBox("Column reordering allowed", true);
			check.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBox check = (JCheckBox)e.getSource();
					exampleTable.getTableHeader().setReorderingAllowed(check.isSelected());
				}
			});
			p.add(check);
			
			check = new JCheckBox("Column resizing allowed", true);
			check.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JCheckBox check = (JCheckBox)e.getSource();
					exampleTable.getTableHeader().setResizingAllowed(check.isSelected());
				}
			});
			p.add(check);
			p2.add(p, BorderLayout.CENTER);

			p2.add(new FakeTable(), BorderLayout.SOUTH);
			
			p1.add(p2, gc);
			
			gc.gridy = 6;
			gc.insets = new Insets(0, 2, 1, 4);
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.SOUTHWEST;
			gc.gridheight = 2;
			info = new JLabel("** Requires a 1.6 JRE");
			info.setOpaque(true);
			info.setBackground(INFO_COLOR);
			info.setForeground(Color.BLACK);
			info.setBorder(INFO_BORDER);
			p1.add(info, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			tableBack.update();
			tableHeaderBack.update();
			tableHeaderRolloverBack.update();
			tableHeaderRollover.update();
			tableHeaderArrow.update();
			tableGrid.update();
			tableSelectedBack.update();
			tableSelectedFore.update();
			tableBorderDark.update();
			tableBorderLight.update();
			tableHeaderDark.update();
			tableHeaderLight.update();
			tableFocusBorder.update();
			tableAlternateRow.update();
			
			inited = true;
		}
		
		class FakeTable extends JPanel {
			
			FakeTable() {
				super(new BorderLayout());
				
				fakeTable = this;

				setBorder(new GridBorder());
				add(new FocusedCell());
				init();
			}
			
			private void init() {
				setBackground(Theme.tableBackColor.getColor());
			}
			
			public void setUI(PanelUI ui) {
				super.setUI(ui);
				init();
			}
			
			class GridBorder implements Border {
				
				private final Insets insets = new Insets(9, 9, 9, 9);

				public void paintBorder(Component c,
					Graphics g, int x, int y, int width, int height)
				{
					g.setColor(Theme.tableGridColor.getColor());
					
					g.drawLine(x, y + 8, x + width, y + 8);
					g.drawLine(x, y + height - 9, x + width, y + height - 9);
					g.drawLine(x + 8, y, x + 8, y + height);
					g.drawLine(x + width - 9, y, x + width - 9, y + height);
				}

				public Insets getBorderInsets(Component c) {
					return insets;
				}

				public boolean isBorderOpaque() {
					return false;
				}
			}
		}
		
		class FocusedCellBorder implements Border {

			private Insets insets = new Insets(1, 1, 1, 1);
			
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				if(focusedState) {
					g.setColor(Theme.tableFocusBorderColor.getColor());
				}
				else {
					g.setColor(Theme.tableSelectedBackColor.getColor());
				}
				
				g.drawRect(x, y, width - 1, height - 1);
			}

			public Insets getBorderInsets(Component c) {
				return insets;
			}

			public boolean isBorderOpaque() {
				return true;
			}
		}
		
		class FocusedCell extends JLabel {
			
			FocusedCell() {
				super(" Focused selected Cell");
				focusedCellLabel = this;
				setBorder(new FocusedCellBorder());
				init();
			}
			
			private void init() {
				setOpaque(true);
				setBackground(Theme.tableSelectedBackColor.getColor());
				setForeground(Theme.tableSelectedForeColor.getColor());
				setFont(Theme.tableFont.getFont());
			}
			
			public void setUI(LabelUI ui) {
				super.setUI(ui);
				init();
			}
		}
	}
	
	class SpinnerCP extends CP {
		
		private JCheckBox rolloverEnabled;
		
		SpinnerCP() {
			super();
			super.setupUI(setupUI());
		}

		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Spinner");
			
			ps.addParameter(rolloverEnabled.isSelected(),
				Theme.spinnerRollover);

			ps.addParameter(spinnerButtField);
			ps.addParameter(spinnerArrowField);
			ps.addParameter(spinnerButtRolloverBg);
			ps.addParameter(spinnerButtPressedBg);
			ps.addParameter(spinnerButtDisabledBg);
			ps.addParameter(spinnerBorder);
			ps.addParameter(spinnerDisabledBorder);
			ps.addParameter(spinnerArrowDisabled);

			ps.addParameter(spinnerSpreadDark);
			ps.addParameter(spinnerSpreadLight);
			ps.addParameter(spinnerSpreadDarkDisabled);
			ps.addParameter(spinnerSpreadLightDisabled);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			// Button
			p1.add(new JLabel("Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			spinnerButtField = new SBControl(
				Theme.spinnerButtColor, CONTROLS_SPINNER);
			p1.add(spinnerButtField, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Rollover Col"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			spinnerButtRolloverBg = new SBControl(
				Theme.spinnerButtRolloverColor, CONTROLS_SPINNER);
			p1.add(spinnerButtRolloverBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Pressed Col"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			spinnerButtPressedBg = new SBControl(
				Theme.spinnerButtPressedColor, CONTROLS_SPINNER);
			p1.add(spinnerButtPressedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Disabled Col"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			spinnerButtDisabledBg = new SBControl(
				Theme.spinnerButtDisabledColor, CONTROLS_SPINNER);
			p1.add(spinnerButtDisabledBg, gc);

			// Spread
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Spread Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerSpreadLight = new SpreadControl(
				Theme.spinnerSpreadLight, 20, CONTROLS_SPINNER);
			p1.add(spinnerSpreadLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Spread Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerSpreadDark = new SpreadControl(
				Theme.spinnerSpreadDark, 20, CONTROLS_SPINNER);
			p1.add(spinnerSpreadDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerSpreadLightDisabled = new SpreadControl(
				Theme.spinnerSpreadLightDisabled, 20, CONTROLS_SPINNER);
			p1.add(spinnerSpreadLightDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerSpreadDarkDisabled = new SpreadControl(
				Theme.spinnerSpreadDarkDisabled, 20, CONTROLS_SPINNER);
			p1.add(spinnerSpreadDarkDisabled, gc);
			
			// Spinner border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerBorder = new SBControl(
				Theme.spinnerBorderColor, CONTROLS_SPINNER);
			p1.add(spinnerBorder, gc);

			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerDisabledBorder = new SBControl(
				Theme.spinnerBorderDisabledColor, CONTROLS_SPINNER);
			p1.add(spinnerDisabledBorder, gc);
			
			// arrow
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Arrow Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerArrowField = new SBControl(
				Theme.spinnerArrowColor, CONTROLS_SPINNER);
			p1.add(spinnerArrowField, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Arrow"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			spinnerArrowDisabled = new SBControl(
				Theme.spinnerArrowDisabledColor, CONTROLS_SPINNER);
			p1.add(spinnerArrowDisabled, gc);
			gc.gridy += 2;
			
			gc.gridheight = 2;
			gc.insets = new Insets(0, 8, 0, 4);
			rolloverEnabled = new BooleanControl(
				Theme.spinnerRollover, "Paint Rollover Border", CONTROLS_SPINNER);
			p1.add(rolloverEnabled, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			rolloverEnabled.setSelected(Theme.spinnerRollover.getValue());
			
			spinnerButtField.update();
			spinnerArrowField.update();
			spinnerButtRolloverBg.update();
			spinnerButtPressedBg.update();
			spinnerButtDisabledBg.update();
			spinnerBorder.update();
			spinnerDisabledBorder.update();
			spinnerArrowDisabled.update();
			spinnerSpreadDark.init();
			spinnerSpreadLight.init();
			spinnerSpreadDarkDisabled.init();
			spinnerSpreadLightDisabled.init();
			
			inited = true;
		}
	}
	
	class MenuCP extends CP {
		
		private JCheckBox rolloverEnabled;
		private JCheckBox popupShadow;
		private JCheckBox allowTwoIcons;
		
		MenuCP() {
			super();
			super.setupUI(setupUI());
		}

		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Menu");
			
			ps.addParameter(rolloverEnabled.isSelected(),
				Theme.menuRollover);
			ps.addParameter(popupShadow.isSelected(),
				Theme.menuPopupShadow);
			ps.addParameter(allowTwoIcons.isSelected(),
				Theme.menuAllowTwoIcons);

			ps.addParameter(Theme.menuFont);
			ps.addParameter(Theme.menuItemFont);

			ps.addParameter(menuRolloverBg);
			ps.addParameter(menuSeparator);
			ps.addParameter(menuRolloverFg);
			ps.addParameter(menuDisabledFg);
			ps.addParameter(menuBar);
			ps.addParameter(menuItemRollover);
			ps.addParameter(menuPopup);
			ps.addParameter(menuBorder);
			ps.addParameter(menuInnerHilight);
			ps.addParameter(menuInnerShadow);
			ps.addParameter(menuOuterHilight);
			ps.addParameter(menuOuterShadow);
			ps.addParameter(menuIcon);
			ps.addParameter(menuIconRollover);
			ps.addParameter(menuIconDisabled);
			ps.addParameter(menuItemSelectedText);
			ps.addParameter(menuItemDisabledText);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Menubar Background"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			menuBar = new SBControl(
				Theme.menuBarColor, CONTROLS_MENU);
			p1.add(menuBar, gc);
			gc.gridy ++;
			
			// Popup
			gc.insets = insets4404;
			p1.add(new JLabel("Popup Background"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			menuPopup = new SBControl(
				Theme.menuPopupColor, CONTROLS_MENU);
			p1.add(menuPopup, gc);
			
			// Flags
			JPanel p2 = new JPanel(new GridLayout(2, 1));
			popupShadow = new BooleanControl(
				Theme.menuPopupShadow, "Popup Shadows", true, CONTROLS_MENU);
			p2.add(popupShadow);
			
			allowTwoIcons = new BooleanControl(
				Theme.menuAllowTwoIcons, "Allow two Icons", CONTROLS_MENU);
			allowTwoIcons.setToolTipText("<html>Affects" +
					"<br>JCheckBoxMenuItem and" +
					"<br>JRadioButtonMenuItem only");
			// Note: We must initialize client properties each time the value changes
			allowTwoIcons.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(menus == null) return;
					
					for(int i = 0; i < menus.length; i++) {
						if(menus[i] instanceof JMenu) {
							// This forces the menu items to be laid out again
							removeClientProperties((JMenu)menus[i]);
						}
					}
				}
			});
			p2.add(allowTwoIcons);

			gc.gridy ++;
			gc.gridheight = 4;
			gc.anchor = GridBagConstraints.CENTER;
			p1.add(p2, gc);

			// Popup border colors
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 1;
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.insets = insets0804;
			p1.add(new JLabel("Popup Inner Hilight"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			menuInnerHilight = new SBControl(
				Theme.menuInnerHilightColor, CONTROLS_MENU);
			p1.add(menuInnerHilight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Popup Inner Shadow"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			menuInnerShadow = new SBControl(
				Theme.menuInnerShadowColor, CONTROLS_MENU);
			p1.add(menuInnerShadow, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Popup Outer Hilight"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			menuOuterHilight = new SBControl(
				Theme.menuOuterHilightColor, CONTROLS_MENU);
			p1.add(menuOuterHilight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Popup Outer Shadow"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			menuOuterShadow = new SBControl(
				Theme.menuOuterShadowColor, CONTROLS_MENU);
			p1.add(menuOuterShadow, gc);
			
			// Separator
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Separator Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			menuSeparator = new SBControl(
				Theme.menuSeparatorColor, CONTROLS_MENU);
			p1.add(menuSeparator, gc);
			
			// Top Menus
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 8;
			gc.insets = new Insets(0, 4, 0, 0);
			
			p2 = new JPanel(new GridBagLayout());
			p2.setBorder(new TitledBorder("Top Menus"));
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridx = 0;
			gc2.gridy = 0;
			gc2.insets = new Insets(0, 2, 0, 2);
			p2.add(new JLabel("Rollover Background"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 2, 0, 2);
			menuRolloverBg = new SBControl(
				Theme.menuRolloverBgColor, CONTROLS_MENU);
			p2.add(menuRolloverBg, gc2);
			gc2.gridy ++;
			
			gc2.insets = new Insets(4, 2, 0, 2);
			p2.add(new JLabel("Rollover Foreground"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 2, 0, 2);
			menuRolloverFg = new SBControl(
				Theme.menuRolloverFgColor, CONTROLS_MENU);
			p2.add(menuRolloverFg, gc2);
			gc2.gridy ++;
			
			gc2.insets = new Insets(4, 2, 0, 2);
			p2.add(new JLabel("Disabled Foreground"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 2, 0, 2);
			menuDisabledFg = new SBControl(
				Theme.menuDisabledFgColor, CONTROLS_MENU);
			p2.add(menuDisabledFg, gc2);
			
			// Top Menu border
			gc2.gridx ++;
			gc2.gridy = 0;
			gc2.insets = new Insets(0, 8, 0, 2);
			p2.add(new JLabel("Menu Border Color"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 8, 2, 2);
			menuBorder = new SBControl(
				Theme.menuBorderColor, CONTROLS_MENU);
			p2.add(menuBorder, gc2);
			gc2.gridy += 2;
			
			rolloverEnabled = new BooleanControl(
				Theme.menuRollover, "Paint Rollover", CONTROLS_MENU);
			p2.add(rolloverEnabled, gc2);
			
			p1.add(p2, gc);
			
			// Menu Items
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 8;
			gc.insets = new Insets(0, 0, 0, 0);
			
			p2 = new JPanel(new GridBagLayout());
			p2.setBorder(new TitledBorder("Menu Items & Submenus"));
			gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridx = 0;
			gc2.gridy = 0;
			gc2.insets = new Insets(0, 2, 0, 2);
			p2.add(new JLabel("Selected Background"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 2, 0, 2);
			menuItemRollover = new SBControl(
				Theme.menuItemRolloverColor, CONTROLS_MENU);
			p2.add(menuItemRollover, gc2);
			gc2.gridy ++;
			
			gc2.insets = new Insets(4, 2, 0, 2);
			p2.add(new JLabel("Selected Foreground"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 2, 0, 2);
			menuItemSelectedText = new SBControl(
				Theme.menuItemSelectedTextColor, CONTROLS_MENU);
			p2.add(menuItemSelectedText, gc2);
			
			gc2.gridy ++;
			
			gc2.insets = new Insets(4, 2, 0, 2);
			p2.add(new JLabel("Disabled Foreground"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 2, 0, 2);
			menuItemDisabledText = new SBControl(
				Theme.menuItemDisabledFgColor, CONTROLS_MENU);
			p2.add(menuItemDisabledText, gc2);
			
			// Icon
			gc2.gridx ++;
			gc2.gridy = 0;
			gc2.insets = new Insets(0, 8, 0, 2);
			p2.add(new JLabel("Icon Color"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 8, 0, 2);
			menuIcon = new SBControl(
				Theme.menuIconColor, CONTROLS_MENU);
			p2.add(menuIcon, gc2);
			gc2.gridy ++;
			
			gc2.insets = new Insets(4, 8, 0, 2);
			p2.add(new JLabel("Icon Rollover Color"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 8, 0, 2);
			menuIconRollover = new SBControl(
				Theme.menuIconRolloverColor, CONTROLS_MENU);
			p2.add(menuIconRollover, gc2);
			gc2.gridy ++;
			
			gc2.insets = new Insets(4, 8, 0, 2);
			p2.add(new JLabel("Icon Disabled Color"), gc2);
			gc2.gridy ++;
			gc2.insets = new Insets(0, 8, 2, 2);
			menuIconDisabled = new SBControl(
				Theme.menuIconDisabledColor, CONTROLS_MENU);
			p2.add(menuIconDisabled, gc2);
			
			p1.add(p2, gc);

			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			rolloverEnabled.setSelected(Theme.menuRollover.getValue());
			popupShadow.setSelected(Theme.menuPopupShadow.getValue());
			allowTwoIcons.setSelected(Theme.menuAllowTwoIcons.getValue());

			menuRolloverBg.update();
			menuSeparator.update();
			menuRolloverFg.update();
			menuDisabledFg.update();
			menuBar.update();
			menuItemRollover.update();
			menuPopup.update();
			menuBorder.update();
			menuInnerHilight.update();
			menuInnerShadow.update();
			menuOuterHilight.update();
			menuOuterShadow.update();
			menuIcon.update();
			menuIconRollover.update();
			menuIconDisabled.update();	
			menuItemSelectedText.update();
			menuItemDisabledText.update();

			inited = true;
		}
		
		private void removeClientProperties(JMenu menu) {
			int mc = menu.getMenuComponentCount();
			boolean removed = false;
			
			for(int i = 0; i < mc; i++) {
				Component c = menu.getMenuComponent(i);
				
				if(c instanceof JMenu) {
					removeClientProperties((JMenu)c);
				}
				else if(c instanceof JMenuItem) {
					if(!removed) {
						removed = true;
						Container parent = c.getParent();

						if(parent != null && (parent instanceof JComponent)) {
							JComponent p = (JComponent)parent;
							p.putClientProperty(TinyMenuItemUI.MAX_ACC_WIDTH, null);
							p.putClientProperty(TinyMenuItemUI.MAX_TEXT_WIDTH, null);
							p.putClientProperty(TinyMenuItemUI.MAX_ICON_WIDTH, null);
							p.putClientProperty(TinyMenuItemUI.MAX_LABEL_WIDTH, null);
						}
					}
				}
			}
		}
	}
	
	class TreeCP extends CP {

		TreeCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Tree");

			ps.addParameter(Theme.treeFont);
			
			ps.addParameter(treeText);
			ps.addParameter(treeSelectedText);
			ps.addParameter(treeBg);
			ps.addParameter(treeTextBg);
			ps.addParameter(treeSelectedBg);
			ps.addParameter(treeLine);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Tree Background"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			treeBg = new SBControl(
				Theme.treeBgColor, true, CONTROLS_TREE);
			p1.add(treeBg, gc);
			
			// Text
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 1;
			gc.insets = insets0804;
			p1.add(new JLabel("Text Background"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			treeTextBg = new SBControl(
				Theme.treeTextBgColor, true, CONTROLS_TREE);
			p1.add(treeTextBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Text Foreground"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			treeText = new SBControl(
				Theme.treeTextColor, true, CONTROLS_TREE);
			p1.add(treeText, gc);
			
			// Selected Text
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Selected Text Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			treeSelectedBg = new SBControl(
				Theme.treeSelectedBgColor, true, CONTROLS_TREE);
			p1.add(treeSelectedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Selected Foreground"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			treeSelectedText = new SBControl(
				Theme.treeSelectedTextColor, true, CONTROLS_TREE);
			p1.add(treeSelectedText, gc);
			
			// Line Color
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Line Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			treeLine = new SBControl(
				Theme.treeLineColor, true, CONTROLS_TREE);
			p1.add(treeLine, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			treeText.update();
			treeSelectedText.update();   		
			treeBg.update();
			treeTextBg.update();
			treeSelectedBg.update();
			treeLine.update();
			
			inited = true;
		}
	}
	
	class TabbedPaneCP extends CP {
		
		private JCheckBox rolloverEnabled, focusEnabled, ignoreSelectedBg, fixedTabs;
		private InsetsControl tabTop, tabLeft, tabBottom, tabRight;
		private InsetsControl areaTop, areaLeft, areaBottom, areaRight;
		
		TabbedPaneCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "TabbedPane");
			
			ps.addParameter(rolloverEnabled.isSelected(),
				Theme.tabRollover);
			ps.addParameter(focusEnabled.isSelected(),
				Theme.tabFocus);
			ps.addParameter(ignoreSelectedBg.isSelected(),
				Theme.ignoreSelectedBg);
			ps.addParameter(fixedTabs.isSelected(),
				Theme.fixedTabs);
			
			ps.addParameter(Theme.tabFont);

			ps.addParameter(tabPaneBorder);
			ps.addParameter(tabNormalBg);
			ps.addParameter(tabSelectedBg);
			ps.addParameter(tabDisabled);
			ps.addParameter(tabDisabledSelected);
			ps.addParameter(tabDisabledText);
			ps.addParameter(tabBorder);
			ps.addParameter(tabDisabledBorder);
			ps.addParameter(tabPaneDisabledBorder);
			ps.addParameter(tabRoll);

			ps.addParameter(new Insets(
					tabTop.getIntValue(),
					tabLeft.getIntValue(),
					tabBottom.getIntValue(),
					tabRight.getIntValue()),
				Theme.tabInsets);
			
			ps.addParameter(new Insets(
					areaTop.getIntValue(),
					areaLeft.getIntValue(),
					areaBottom.getIntValue(),
					areaRight.getIntValue()),
				Theme.tabAreaInsets);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			// Tab Border
			p1.add(new JLabel("Tab Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tabBorder = new SBControl(
				Theme.tabBorderColor, CONTROLS_TABBED_PANE);
			p1.add(tabBorder, gc);
			gc.gridy ++;
			
			// Pane Border
			gc.insets = insets4404;
			p1.add(new JLabel("Pane Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tabPaneBorder = new SBControl(
				Theme.tabPaneBorderColor, CONTROLS_TABBED_PANE);
			p1.add(tabPaneBorder, gc);
			
			// Tab
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Unselected Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabNormalBg = new SBControl(
				Theme.tabNormalColor, true, CONTROLS_TABBED_PANE);
			p1.add(tabNormalBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Selected Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabSelectedBg = new SBControl(
				Theme.tabSelectedColor, CONTROLS_TABBED_PANE);
			p1.add(tabSelectedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;			
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabRoll = new SBControl(
				Theme.tabRolloverColor, CONTROLS_TABBED_PANE);
			p1.add(tabRoll, gc);
			
			// Disabled Borders
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Disabled Tab Border C."), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabDisabledBorder = new SBControl(
				Theme.tabDisabledBorderColor, CONTROLS_TABBED_PANE);
			p1.add(tabDisabledBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Pane Border C."), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabPaneDisabledBorder = new SBControl(
				Theme.tabPaneDisabledBorderColor, CONTROLS_TABBED_PANE);
			p1.add(tabPaneDisabledBorder, gc);
			
			// Disabled Colors
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Disabled Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabDisabled = new SBControl(
				Theme.tabDisabledColor, CONTROLS_TABBED_PANE);
			p1.add(tabDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Selected Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabDisabledSelected = new SBControl(
				Theme.tabDisabledSelectedColor, CONTROLS_TABBED_PANE);
			p1.add(tabDisabledSelected, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;			
			p1.add(new JLabel("Disabled Text Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tabDisabledText = new SBControl(
				Theme.tabDisabledTextColor, CONTROLS_TABBED_PANE);
			p1.add(tabDisabledText, gc);
			
			// Tab Insets
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 1;
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.NORTH;
			gc.insets = new Insets(0, 16, 0, 4);
			p1.add(new JLabel("Tab Insets"), gc);
			
			gc.gridy = 1;
			gc.gridheight = 7;
			gc.insets = new Insets(4, 16, 0, 4);
			
			JPanel p2 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridx = 0;
			gc2.gridy = 1;
			gc2.insets = new Insets(0, 0, 0, 0);
			
			tabLeft = new InsetsControl(new SpinnerNumberModel(6, 0, 99, 1),
				Theme.tabInsets, InsetsControl.LEFT);
			p2.add(tabLeft, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 0;
			tabTop = new InsetsControl(new SpinnerNumberModel(1, 0, 99, 1),
				Theme.tabInsets, InsetsControl.TOP);
			p2.add(tabTop, gc2);
			gc2.gridy += 2;
			gc2.gridy ++;
			tabBottom = new InsetsControl(new SpinnerNumberModel(4, 0, 99, 1),
				Theme.tabInsets, InsetsControl.BOTTOM);
			p2.add(tabBottom, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 1;
			tabRight = new InsetsControl(new SpinnerNumberModel(6, 0, 99, 1),
				Theme.tabInsets, InsetsControl.RIGHT);
			p2.add(tabRight, gc2);
			
			p1.add(p2, gc);
			
			// Tab Area Insets
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 1;
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.NORTH;
			gc.insets = new Insets(0, 16, 0, 4);
			p1.add(new JLabel("Tab Area Insets"), gc);
			
			gc.gridy = 1;
			gc.gridheight = 7;
			gc.insets = new Insets(4, 16, 0, 4);
			
			p2 = new JPanel(new GridBagLayout());
			gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridx = 0;
			gc2.gridy = 1;
			gc2.insets = new Insets(0, 0, 0, 0);
			
			areaLeft = new InsetsControl(new SpinnerNumberModel(2, 0, 99, 1),
				Theme.tabAreaInsets, InsetsControl.LEFT);
			p2.add(areaLeft, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 0;
			areaTop = new InsetsControl(new SpinnerNumberModel(6, 2, 99, 1),
				Theme.tabAreaInsets, InsetsControl.TOP);
			p2.add(areaTop, gc2);
			gc2.gridy += 2;
			gc2.gridy ++;
			areaBottom = new InsetsControl(new SpinnerNumberModel(0, 0, 99, 1),
				Theme.tabAreaInsets, InsetsControl.BOTTOM);
			p2.add(areaBottom, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 1;
			areaRight = new InsetsControl(new SpinnerNumberModel(0, 0, 99, 1),
				Theme.tabAreaInsets, InsetsControl.RIGHT);
			p2.add(areaRight, gc2);
			
			p1.add(p2, gc);
			
			// Flags
			gc.gridx = 0;
			gc.gridy = 8;
			gc.insets = new Insets(8, 4, 0, 4);
			gc.anchor = GridBagConstraints.WEST;
			gc.fill = GridBagConstraints.NONE;
			gc.gridwidth = 5;		
			p2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
			rolloverEnabled = new BooleanControl(
				Theme.tabRollover, "Paint Rollover", CONTROLS_TABBED_PANE);
			p2.add(rolloverEnabled);
			
			focusEnabled = new BooleanControl(
				Theme.tabFocus, "Paint Focus", CONTROLS_TABBED_PANE);
			p2.add(focusEnabled);
			
			ignoreSelectedBg = new BooleanControl(
				Theme.ignoreSelectedBg, "Ignore Selected Bg", CONTROLS_TABBED_PANE);
			p2.add(ignoreSelectedBg, BorderLayout.CENTER);
			
			fixedTabs = new BooleanControl(
				Theme.fixedTabs, "Fixed Tab Positions",
				true, CONTROLS_TABBED_PANE);
			p2.add(fixedTabs);
			
			p1.add(p2, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			rolloverEnabled.setSelected(Theme.tabRollover.getValue());
			focusEnabled.setSelected(Theme.tabFocus.getValue());
			ignoreSelectedBg.setSelected(Theme.ignoreSelectedBg.getValue());
			fixedTabs.setSelected(Theme.fixedTabs.getValue());
			
			tabPaneBorder.update();
			tabNormalBg.update();
			tabSelectedBg.update();
			tabDisabled.update();
			tabDisabledSelected.update();
			tabDisabledText.update();
			tabBorder.update();
			tabDisabledBorder.update();
			tabPaneDisabledBorder.update();
			tabRoll.update();
			
			tabTop.setValue(Theme.tabInsets.top);
			tabLeft.setValue(Theme.tabInsets.left);
			tabBottom.setValue(Theme.tabInsets.bottom);
			tabRight.setValue(Theme.tabInsets.right);
			
			areaTop.setValue(Theme.tabAreaInsets.top);
			areaLeft.setValue(Theme.tabAreaInsets.left);
			areaBottom.setValue(Theme.tabAreaInsets.bottom);
			areaRight.setValue(Theme.tabAreaInsets.right);
			
			inited = true;
		}

		int getFirstTabDistance() {
			return 2;
		}
	}
	
	class TextCP extends CP {
		
		private InsetsControl mTop, mLeft, mBottom, mRight;
		
		TextCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Text");

			ps.addParameter(Theme.labelFont);
			ps.addParameter(Theme.passwordFont);
			ps.addParameter(Theme.textAreaFont);
			ps.addParameter(Theme.textFieldFont);
			
			ps.addParameter(textText);
			ps.addParameter(textCaret);
			ps.addParameter(textSelectedText);
			ps.addParameter(textBg);
			ps.addParameter(textSelectedBg);
			ps.addParameter(textDisabledBg);
			ps.addParameter(textNonEditableBg);
			ps.addParameter(textBorder);
			ps.addParameter(textBorderDisabled);

			ps.addParameter(new Insets(
					mTop.getIntValue(),
					mLeft.getIntValue(),
					mBottom.getIntValue(),
					mRight.getIntValue()),
				Theme.textInsets);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			// background
			p1.add(new JLabel("Text Background"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			textBg = new SBControl(
				Theme.textBgColor, true, CONTROLS_TEXT);
			p1.add(textBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Text Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			textText = new SBControl(
				Theme.textTextColor, true, CONTROLS_TEXT);
			p1.add(textText, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Caret Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			textCaret = new SBControl(
				Theme.textCaretColor, true, CONTROLS_TEXT);
			p1.add(textCaret, gc);
			
			// Selected Bg
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Selected Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			textSelectedBg = new SBControl(
				Theme.textSelectedBgColor, true, CONTROLS_TEXT);
			p1.add(textSelectedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Selected Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			textSelectedText = new SBControl(
				Theme.textSelectedTextColor, true, CONTROLS_TEXT);
			p1.add(textSelectedText, gc);
			
			// Disabled & non-editable Bg
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Disabled Bg"), gc);
			gc.gridy ++;
			textDisabledBg = new SBControl(
				Theme.textDisabledBgColor, CONTROLS_TEXT);
			p1.add(textDisabledBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Non-editable Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			textNonEditableBg = new SBControl(
				Theme.textNonEditableBgColor, CONTROLS_TEXT);
			p1.add(textNonEditableBg, gc);
			
			// Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			textBorder = new SBControl(
				Theme.textBorderColor, CONTROLS_TEXT);
			p1.add(textBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			textBorderDisabled = new SBControl(
				Theme.textBorderDisabledColor, CONTROLS_TEXT);
			p1.add(textBorderDisabled, gc);
			
			// Insets
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 6;
			gc.insets = new Insets(0, 16, 0, 4);
			
			JPanel p2 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.CENTER;
			gc2.fill = GridBagConstraints.NONE;
			gc2.gridx = 0;
			gc2.gridy = 0;
			gc2.gridwidth = 3;
			gc2.insets = new Insets(0, 2, 2, 2);
			p2.add(new JLabel("Insets"), gc2);
			
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.gridwidth = 1;
			gc2.gridx = 0;
			gc2.gridy = 2;
			gc2.insets = new Insets(0, 0, 0, 0);
			mLeft = new InsetsControl(new SpinnerNumberModel(16, 2, 24, 1),
				Theme.textInsets, InsetsControl.LEFT);
			p2.add(mLeft, gc2);
			
			gc2.gridx = 1;
			gc2.gridy = 1;
			mTop = new InsetsControl(new SpinnerNumberModel(2, 1, 8, 1),
				Theme.textInsets, InsetsControl.TOP);
			p2.add(mTop, gc2);
			
			gc2.gridy = 3;
			mBottom = new InsetsControl(new SpinnerNumberModel(3, 1, 8, 1),
				Theme.textInsets, InsetsControl.BOTTOM);
			p2.add(mBottom, gc2);
			
			gc2.gridx = 2;
			gc2.gridy = 2;
			mRight = new InsetsControl(new SpinnerNumberModel(16, 2, 24, 1),
				Theme.textInsets, InsetsControl.RIGHT);
			p2.add(mRight, gc2);
			
			p1.add(p2, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			mTop.setValue(Theme.textInsets.top);
			mLeft.setValue(Theme.textInsets.left);
			mBottom.setValue(Theme.textInsets.bottom);
			mRight.setValue(Theme.textInsets.right);
			
			textText.update();
			textCaret.update();
			textSelectedText.update();
			textBg.update();
			textSelectedBg.update();
			textDisabledBg.update();
			textNonEditableBg.update();
			textBorder.update();
			textBorderDisabled.update();
			
			inited = true;
		}
	}

	class ListCP extends CP {

		ListCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "List");

			ps.addParameter(Theme.listFont);
			
			ps.addParameter(listText);
			ps.addParameter(listBg);
			ps.addParameter(listSelectedText);
			ps.addParameter(listSelectedBg);
			ps.addParameter(listFocusBorder);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Background"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			listBg = new SBControl(
				Theme.listBgColor, true, CONTROLS_LIST);
			p1.add(listBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Foreground"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			listText = new SBControl(
				Theme.listTextColor, true, CONTROLS_LIST);
			p1.add(listText, gc);
			
			// Selected Bg
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Selected Background"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			listSelectedBg = new SBControl(
				Theme.listSelectedBgColor, true, CONTROLS_LIST);
			p1.add(listSelectedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Selected Foreground"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			listSelectedText = new SBControl(
				Theme.listSelectedTextColor, true, CONTROLS_LIST);
			p1.add(listSelectedText, gc);
			
			// Focus Border Color
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Focus Border Color"), gc);
			gc.gridy ++;
			listFocusBorder = new SBControl(
				Theme.listFocusBorderColor, true, CONTROLS_LIST);
			p1.add(listFocusBorder, gc);
			
			// Fake list
			gc.gridy ++;
			gc.insets = insets4804;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridheight = 3;
			p1.add(new FakeList(), gc);
			
			gc.gridy ++;
			gc.gridheight = 1;
			p1.add(new JLabel() {
				private int pw = 0;
				
				public Dimension getPreferredSize() {
					int w = fakeList.getPreferredSize().width;
					
					if(w > pw) pw = w;
					
					return new Dimension(pw, 2);
				}
			}, gc);

			panel.add(p1);
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			listText.update();
			listBg.update();
			listSelectedText.update();
			listSelectedBg.update();
			listFocusBorder.update();
			
			inited = true;
		}
		
		class FakeList extends JScrollPane {
			
			FakeList() {
				super(new FocusedListItem());
				fakeList = this;
				setViewportBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
				init();
			}
			
			private void init() {
				setBackground(Theme.listBgColor.getColor());
			}
			
			public void setUI(ScrollPaneUI ui) {
				super.setUI(ui);
				init();
			}
		}
		
		class FocusedListItemBorder implements Border {

			private final Insets insets = new Insets(1, 1, 1, 1);
			
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				if(focusedState) {
					g.setColor(Theme.listFocusBorderColor.getColor());
				}
				else {
					g.setColor(Theme.listSelectedBgColor.getColor());
				}
				
				g.drawRect(x, y, width - 1, height - 1);
			}

			public Insets getBorderInsets(Component c) {
				return insets;
			}

			public boolean isBorderOpaque() {
				return true;
			}
		}
		
		class FocusedListItem extends JLabel {
			
			FocusedListItem() {
				super(" Focused selected Item");
				focusedItemLabel = this;
				setBorder(new FocusedListItemBorder());
				init();
			}
			
			private void init() {
				setOpaque(true);
				setBackground(Theme.listSelectedBgColor.getColor());
				setForeground(Theme.listSelectedTextColor.getColor());
				setFont(Theme.listFont.getFont());
			}
			
			public void setUI(LabelUI ui) {
				super.setUI(ui);
				init();
			}
		}
	}
	
	class MiscCP extends CP {

		MiscCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Miscellaneous");
			
			ps.addParameter(Theme.titledBorderFont);
			ps.addParameter(Theme.textPaneFont);

			ps.addParameter(titledBorderColor);
			ps.addParameter(editorPaneBg);
			ps.addParameter(textPaneBg);
			ps.addParameter(desktopPaneBg);
			ps.addParameter(splitPaneButtonColor);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.WEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("TitledBorder Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			titledBorderColor = new SBControl(
				Theme.titledBorderColor, true, CONTROLS_ALL);
			p1.add(titledBorderColor, gc);
			
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("EditorPane Bg Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			editorPaneBg = new SBControl(
				Theme.editorPaneBgColor, true, CONTROLS_ALL);
			p1.add(editorPaneBg, gc);
			
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("TextPane Bg Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			textPaneBg = new SBControl(
				Theme.textPaneBgColor, true, CONTROLS_ALL);
			p1.add(textPaneBg, gc);
			
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("DesktopPane Bg Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			desktopPaneBg = new SBControl(
				Theme.desktopPaneBgColor, true, CONTROLS_ALL);
			p1.add(desktopPaneBg, gc);
			
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("SplitPane Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			splitPaneButtonColor = new SBControl(
				Theme.splitPaneButtonColor, false, CONTROLS_ALL);
			p1.add(splitPaneButtonColor, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			titledBorderColor.update();
			editorPaneBg.update();
			textPaneBg.update();
			desktopPaneBg.update();
			splitPaneButtonColor.update();
			
			inited = true;
		}
	}
	
	class ToolTipCP extends CP {

		ToolTipCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "ToolTip");

			ps.addParameter(Theme.toolTipFont);
			
			ps.addParameter(tipBg);
			ps.addParameter(tipBgDis);
			ps.addParameter(tipBorder);
			ps.addParameter(tipBorderDis);
			ps.addParameter(tipText);
			ps.addParameter(tipTextDis);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.WEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			
			// Border
			gc.insets = insets0404;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tipBorder = new SBControl(
				Theme.tipBorderColor, CONTROLS_TOOL_TIP);
			p1.add(tipBorder, gc);
			
			gc.gridy ++;
			gc.insets = insets4404;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			tipBorderDis = new SBControl(
				Theme.tipBorderDis, CONTROLS_TOOL_TIP);
			p1.add(tipBorderDis, gc);
			
			// Background
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Background Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tipBg = new SBControl(
				Theme.tipBgColor, true, CONTROLS_TOOL_TIP);
			p1.add(tipBg, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Background"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tipBgDis = new SBControl(
				Theme.tipBgDis, true, CONTROLS_TOOL_TIP);
			p1.add(tipBgDis, gc);
			
			// Text
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Text Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tipText = new SBControl(
				Theme.tipTextColor, true, CONTROLS_TOOL_TIP);
			p1.add(tipText, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Text"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			tipTextDis = new SBControl(
				Theme.tipTextDis, true, CONTROLS_TOOL_TIP);
			p1.add(tipTextDis, gc);
			
			// Test labels
			JPanel p2 = new JPanel(new BorderLayout(12, 0));
			p2.add(new EnabledToolTip(), BorderLayout.WEST);
			p2.add(new DisabledToolTip(), BorderLayout.EAST);
			
			gc.gridx = 0;
			gc.gridy ++;
			gc.insets = new Insets(12, 4, 0, 4);
			gc.gridwidth = 3;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.anchor = GridBagConstraints.CENTER;
			p1.add(p2, gc);

			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			tipBg.update();
			tipBgDis.update();
			tipBorder.update();
			tipBorderDis.update();
			tipText.update();
			tipTextDis.update();
			
			inited = true;
		}
		
		class ToolTipBorder implements Border {

			private final Insets insets = new Insets(3, 6, 3, 6);
			private boolean enabled;
			
			ToolTipBorder(boolean enabled) {
				this.enabled = enabled;
			}
			
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				if(enabled) {
					g.setColor(Theme.tipBorderColor.getColor());
				}
				else {
					g.setColor(Theme.tipBorderDis.getColor());
				}
				
				g.drawRect(x, y, width - 1, height - 1);
			}

			public Insets getBorderInsets(Component c) {
				return insets;
			}

			public boolean isBorderOpaque() {
				return false;
			}
			
		}
		
		class EnabledToolTip extends JLabel {
			
			EnabledToolTip() {
				super("Enabled Tooltip");
				enabledToolTip = this;
				setBorder(new ToolTipBorder(true));
				setToolTipText("Enabled Tooltip");
				init();
			}
			
			private void init() {
				setOpaque(true);
				setBackground(Theme.tipBgColor.getColor());
				setForeground(Theme.tipTextColor.getColor());
				setFont(Theme.toolTipFont.getFont());
			}
			
			public void setUI(LabelUI ui) {
				super.setUI(ui);
				init();
			}
		}
		
		class DisabledToolTip extends JLabel {
			
			private final DisabledToolTipUI ui = new DisabledToolTipUI();
			
			DisabledToolTip() {
				super("Disabled Tooltip");
				setUI(ui);
				disabledToolTip = this;
				setBorder(new ToolTipBorder(false));
				setToolTipText("Disabled Tooltip");
				setEnabled(false);
				init();
			}
			
			private void init() {
				setOpaque(true);
				setBackground(Theme.tipBgDis.getColor());
				setForeground(Theme.tipTextDis.getColor());
				setFont(Theme.toolTipFont.getFont());
			}

			public void setUI(LabelUI ignore) {
				super.setUI(ui);
				init();
			}
		}
	}
	
	private static class DisabledToolTipUI extends TinyLabelUI {

		public static ComponentUI createUI(JComponent c) {
			return new DisabledToolTipUI();
		}
		
		protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
			super.paintEnabledText(l, g, s, textX, textY);
	    }
	}
	
	class SeparatorCP extends CP {

		SeparatorCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Separator");

			ps.addParameter(separator);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.WEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Separator Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			separator = new SBControl(Theme.separatorColor, true, CONTROLS_ALL);
			p1.add(separator, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			separator.update();
			
			inited = true;
		}
	}
	
	class IconCP extends CP {

		IconCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Icon");
			
			for(int i = 0; i < 20; i++) {
				ps.addParameter(iconChecks[i].isSelected(),
					Theme.colorize[i]);
				ps.addParameter(hsb[i]);
			}
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			for(int i = 0; i < 20; i++) {
				hsb[i] = new HSBControl(Theme.colorizer[i], i);
				iconChecks[i] = new ColorizeIconCheck(
					Theme.colorize[i], hsb[i], TinyLookAndFeel.getSystemIconName(i));
			}

			JPanel p = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = gc.WEST;
			gc.gridx = 0;
			gc.gridy = 0;
			
			JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 7));
			p1.add(new JLabel("Tree"));
			for(int i = 1; i < 6; i++) {
				p1.add(new CombiPanel(hsb[i], iconChecks[i]));
			}			
			p.add(p1, gc);
			gc.gridy ++;
			
			p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 7));
			p1.add(new JLabel("FileView"));
			for(int i = 6; i < 11; i++) {
				p1.add(new CombiPanel(hsb[i], iconChecks[i]));
			}			
			p.add(p1, gc);
			gc.gridy ++;
			
			p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
			p1.add(new JLabel("FileChooser"));
			for(int i = 11; i < 16; i++) {
				p1.add(new CombiPanel(hsb[i], iconChecks[i]));
			}			
			p.add(p1, gc);
			gc.gridy ++;
			
			p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
			p1.add(new JLabel("OptionPane"));
			for(int i = 16; i < 20; i++) {
				p1.add(new CombiPanel(hsb[i], iconChecks[i]));
			}
			p.add(p1, gc);
			gc.gridy ++;
			
			p1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
			p1.add(new JLabel("InternalFrame"));
			p1.add(new CombiPanel(hsb[0], iconChecks[0]));
			
			p.add(p1, gc);
			
			panel.add(p);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			for(int i = 0; i < 20; i++) {
				iconChecks[i].setSelected(Theme.colorize[i].getValue());
				Icon icon = TinyLookAndFeel.getUncolorizedSystemIcon(i);
				
				if(iconChecks[i].isSelected()) {
					HSBReference ref = Theme.colorizer[i];
					iconChecks[i].setIcon(
						DrawRoutines.colorizeIcon(
							((ImageIcon)icon).getImage(), ref));
				}
				else {
					iconChecks[i].setIcon(icon);
				}
				
				hsb[i].setHue(Theme.colorizer[i].getHue());
				hsb[i].setSaturation(Theme.colorizer[i].getSaturation());
				hsb[i].setBrightness(Theme.colorizer[i].getBrightness());
				hsb[i].setPreserveGrey(Theme.colorizer[i].isPreserveGrey());
				hsb[i].setReference(Theme.colorizer[i].getReference(), false);
			}
			
			inited = true;
		}
		
		class CombiPanel extends JPanel {
			
			CombiPanel(HSBControl control, ColorizeIconCheck check) {
				setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
				add(control);
				add(check);
			}
		}
	}
	
	class ProgressCP extends CP {

		ProgressCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "ProgressBar");

			ps.addParameter(Theme.progressBarFont);
			
			ps.addParameter(progressField);
			ps.addParameter(progressTrack);
			ps.addParameter(progressBorder);
			ps.addParameter(progressDark);
			ps.addParameter(splitPaneButtonColor);
			ps.addParameter(progressLight);
			ps.addParameter(progressSelectFore);
			ps.addParameter(progressSelectBack);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.WEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Track Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			progressTrack = new SBControl(
				Theme.progressTrackColor, true, CONTROLS_PROGRESSBAR);
			p1.add(progressTrack, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Display Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			progressField = new SBControl(
				Theme.progressColor, true, CONTROLS_PROGRESSBAR);
			p1.add(progressField, gc);
			
			// Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			progressBorder = new SBControl(
				Theme.progressBorderColor, CONTROLS_PROGRESSBAR);
			p1.add(progressBorder, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Dark Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			progressDark = new SBControl(
				Theme.progressDarkColor, CONTROLS_PROGRESSBAR);
			p1.add(progressDark, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Light Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			progressLight = new SBControl(
				Theme.progressLightColor, CONTROLS_PROGRESSBAR);
			p1.add(progressLight, gc);
			
			// Text
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Text Forecolor"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			progressSelectFore = new SBControl(
				Theme.progressSelectForeColor, CONTROLS_PROGRESSBAR);
			p1.add(progressSelectFore, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Text Backcolor"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			progressSelectBack = new SBControl(
				Theme.progressSelectBackColor, CONTROLS_PROGRESSBAR);
			p1.add(progressSelectBack, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			progressField.update();
			progressTrack.update();
			progressBorder.update();
			progressDark.update();
			progressLight.update();
			progressSelectFore.update();
			progressSelectBack.update();
			
			inited = true;
		}
	}
	
	class ComboCP extends CP {
		private JCheckBox paintFocus, rolloverEnabled;
		private InsetsControl mTop, mLeft, mBottom, mRight;
		
		ComboCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "ComboBox");

			ps.addParameter(rolloverEnabled.isSelected(),
				Theme.comboRollover);
			ps.addParameter(paintFocus.isSelected(),
				Theme.comboFocus);
			
			ps.addParameter(Theme.comboFont);
			
			ps.addParameter(comboBg);
			ps.addParameter(comboText);
			ps.addParameter(comboSelectedText);
			ps.addParameter(comboArrowField);
			ps.addParameter(comboSelectedBg);
			ps.addParameter(comboBorder);
			ps.addParameter(comboBorderDisabled);
			ps.addParameter(comboButtonBg);
			ps.addParameter(comboButtonRollover);
			ps.addParameter(comboButtonDisabled);
			ps.addParameter(comboButtonPressed);
			ps.addParameter(comboButtonBorder);
			ps.addParameter(comboButtonBorderDisabled);
			ps.addParameter(comboArrowDisabled);

			ps.addParameter(comboSpreadDark);
			ps.addParameter(comboSpreadLight);
			ps.addParameter(comboSpreadDarkDisabled);
			ps.addParameter(comboSpreadLightDisabled);

			ps.addParameter(new Insets(
					mTop.getIntValue(),
					mLeft.getIntValue(),
					mBottom.getIntValue(),
					mRight.getIntValue()),
				Theme.comboInsets);
			
			return ps;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.WEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			// Background
			p1.add(new JLabel("Background Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			comboBg = new SBControl(
				Theme.comboBgColor, true, CONTROLS_COMBO);
			p1.add(comboBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Foreground Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			comboText = new SBControl(
				Theme.comboTextColor, true, CONTROLS_COMBO);
			p1.add(comboText, gc);
			gc.gridy ++;
			
			p1.add(new JLabel("Selected Background"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			comboSelectedBg = new SBControl(
				Theme.comboSelectedBgColor, true, CONTROLS_COMBO);
			p1.add(comboSelectedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Selected Foreground"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			comboSelectedText = new SBControl(
				Theme.comboSelectedTextColor, true, CONTROLS_COMBO);
			p1.add(comboSelectedText, gc);
			gc.gridy ++;
			
			// Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridheight = 1;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboBorder = new SBControl(
				Theme.comboBorderColor, CONTROLS_COMBO);
			p1.add(comboBorder, gc);
			
			// Border Disabled
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboBorderDisabled = new SBControl(
				Theme.comboBorderDisabledColor, CONTROLS_COMBO);
			p1.add(comboBorderDisabled, gc);
			
			// Button
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboButtonBg = new SBControl(
				Theme.comboButtColor, CONTROLS_COMBO);
			p1.add(comboButtonBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboButtonRollover = new SBControl(
				Theme.comboButtRolloverColor, CONTROLS_COMBO);
			p1.add(comboButtonRollover, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Pressed Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboButtonPressed = new SBControl(
				Theme.comboButtPressedColor, CONTROLS_COMBO);
			p1.add(comboButtonPressed, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboButtonDisabled = new SBControl(
				Theme.comboButtDisabledColor, CONTROLS_COMBO);
			p1.add(comboButtonDisabled, gc);
			
			// Spread
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Spread Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboSpreadLight = new SpreadControl(
				Theme.comboSpreadLight, 20, CONTROLS_COMBO);
			p1.add(comboSpreadLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Spread Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboSpreadDark = new SpreadControl(
				Theme.comboSpreadDark, 20, CONTROLS_COMBO);
			p1.add(comboSpreadDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboSpreadLightDisabled = new SpreadControl(
				Theme.comboSpreadLightDisabled, 20, CONTROLS_COMBO);
			p1.add(comboSpreadLightDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboSpreadDarkDisabled = new SpreadControl(
				Theme.comboSpreadDarkDisabled, 20, CONTROLS_COMBO);
			p1.add(comboSpreadDarkDisabled, gc);
			
			// Button Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Button Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboButtonBorder = new SBControl(
				Theme.comboButtBorderColor, CONTROLS_COMBO);
			p1.add(comboButtonBorder, gc);
			
			// Border disabled
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboButtonBorderDisabled = new SBControl(
				Theme.comboButtBorderDisabledColor, CONTROLS_COMBO);
			p1.add(comboButtonBorderDisabled, gc);
			gc.gridy ++;
			
			// Flags
			gc.anchor = GridBagConstraints.CENTER;
			gc.fill = GridBagConstraints.NONE;
			gc.insets = insets4804;
			gc.gridheight = 4;
			gc.gridwidth = 2;
			JPanel p2 = new JPanel(new GridLayout(2, 1, 0, 2));
			rolloverEnabled = new BooleanControl(
				Theme.comboRollover, "Paint Rollover Border", CONTROLS_COMBO);
			p2.add(rolloverEnabled);
			paintFocus = new BooleanControl(
				Theme.comboFocus, "Paint Focus", CONTROLS_COMBO);
			p2.add(paintFocus);
			
			p1.add(p2, gc);
			
			// Arrow
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridwidth = 1;
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.insets = insets0804;
			p1.add(new JLabel("Arrow Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboArrowField = new SBControl(
				Theme.comboArrowColor, CONTROLS_COMBO);
			p1.add(comboArrowField, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Arrow"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			comboArrowDisabled = new SBControl(
				Theme.comboArrowDisabledColor, CONTROLS_COMBO);
			p1.add(comboArrowDisabled, gc);

			// Insets
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = new Insets(0, 16, 0, 4);
			gc.anchor = GridBagConstraints.NORTH;
			gc.gridheight = 5;
			gc.gridwidth = 1;
			
			p2 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTH;
			gc2.fill = GridBagConstraints.NONE;
			gc2.gridx = 0;
			gc2.gridwidth = 3;
			gc2.gridy = 0;
			gc2.insets = new Insets(0, 0, 2, 0);
			p2.add(new JLabel("Insets"), gc2);
			
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridx = 0;
			gc2.gridy = 2;
			gc2.gridwidth = 1;
			gc2.insets = new Insets(0, 0, 0, 0);
			gc2.anchor = GridBagConstraints.NORTHWEST;
			mLeft = new InsetsControl(new SpinnerNumberModel(2, 2, 24, 1),
				Theme.comboInsets, InsetsControl.LEFT);
			p2.add(mLeft, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 1;
			mTop = new InsetsControl(new SpinnerNumberModel(2, 2, 8, 1),
				Theme.comboInsets, InsetsControl.TOP);
			p2.add(mTop, gc2);
			
			gc2.gridy = 3;
			mBottom = new InsetsControl(new SpinnerNumberModel(2, 2, 8, 1),
				Theme.comboInsets, InsetsControl.BOTTOM);
			p2.add(mBottom, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 2;
			mRight = new InsetsControl(new SpinnerNumberModel(2, 2, 24, 1),
				Theme.comboInsets, InsetsControl.RIGHT);
			p2.add(mRight, gc2);
			
			p1.add(p2, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			rolloverEnabled.setSelected(Theme.comboRollover.getValue());
			paintFocus.setSelected(Theme.comboFocus.getValue());
			
			comboBg.update();
			comboText.update();
			comboSelectedText.update();
			comboArrowField.update();
			comboSelectedBg.update();
			comboBorder.update();
			comboBorderDisabled.update();
			comboButtonBg.update();
			comboButtonRollover.update();
			comboButtonDisabled.update();
			comboButtonPressed.update();
			comboButtonBorder.update();
			comboButtonBorderDisabled.update();
			comboArrowDisabled.update();
			comboSpreadDark.init();
			comboSpreadLight.init();
			comboSpreadDarkDisabled.init();
			comboSpreadLightDisabled.init();
			
			mTop.setValue(Theme.comboInsets.top);
			mLeft.setValue(Theme.comboInsets.left);
			mBottom.setValue(Theme.comboInsets.bottom);
			mRight.setValue(Theme.comboInsets.right);
			
			inited = true;
		}
	}
	
	abstract class CP extends JPanel implements ParameterSetGenerator {

		protected ParameterSet params;
		protected boolean inited = false;
		
		CP() {
			super(new BorderLayout());
			
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					selection.clearSelection();
					requestFocusInWindow();
				}
			});
		}

		abstract JPanel setupUI();
		
		void setupUI(JPanel panel) {
			add(panel, BorderLayout.WEST);
			
			JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
			right.add(new CopyPastePanel(this));
			add(right, BorderLayout.CENTER);
			
//			System.out.println(getPreferredSize());
		}
		
		void createParameterSet() {
			params = getParameterSet();
		}
		
		void pasteParameters() {
			params.pasteParameters(true);
			updateColorTTT();
		}
	}
	
	class CopyPastePanel extends JPanel implements ActionListener {

		private CP cp;
		private JButton pasteButton;
		
		CopyPastePanel(CP cp) {
			super(new GridLayout(2, 1, 0, 2));
			
			this.cp = cp;
			
			if(copyIcon == null) {
				copyIcon = TinyLookAndFeel.loadIcon("cp_icons/mencopy.gif");
				pasteIcon = TinyLookAndFeel.loadIcon("cp_icons/menpaste.gif");
				pasteDisabledIcon = TinyLookAndFeel.loadIcon("cp_icons/menpastedis.gif");
			}
			
			JButton b = new IconButton(copyIcon);
			b.setActionCommand("copy");
			b.setToolTipText("Copy Parameter Set");
			b.addActionListener(this);
			add(b);
			
			pasteButton = new IconButton(pasteIcon);
			pasteButton.setDisabledIcon(pasteDisabledIcon);
			pasteButton.setActionCommand("paste");
			pasteButton.setToolTipText("Paste Parameter Set");
			pasteButton.addActionListener(this);
			pasteButton.setEnabled(false);
			add(pasteButton);
		}

		public void actionPerformed(ActionEvent e) {
			AbstractButton b = (AbstractButton)e.getSource();
			
			if("copy".equals(b.getActionCommand())) {
				cp.createParameterSet();
				
				if(!pasteButton.isEnabled()) {
					pasteButton.setEnabled(true);
				}
			}
			else {	// "paste"
				cp.pasteParameters();
			}
		}
		
		class IconButton extends JButton {
			
			IconButton(Icon icon) {
				super(icon);
				
				setFocusPainted(false);
				setMargin(new Insets(2, 2, 2, 2));
			}
		}
	}
	
	class ButtonsCP extends CP {
		
		private JCheckBox rolloverEnabled, focusEnabled, enterEnabled;
		private JCheckBox focusBorderEnabled, shiftTextEnabled;
		private InsetsControl mTop, mLeft, mBottom, mRight;
		private InsetsControl cTop, cLeft, cBottom, cRight;
		private JPanel cardPanel;
		
		ButtonsCP() {
			super();
			super.setupUI(setupUI());
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Normal Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			buttonNormalBg = new SBControl(
				Theme.buttonNormalColor, CONTROLS_BUTTON);
			p1.add(buttonNormalBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Rollover Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			buttonRolloverBg = new SBControl(
				Theme.buttonRolloverBgColor, CONTROLS_BUTTON);
			p1.add(buttonRolloverBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Pressed Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			buttonPressedBg = new SBControl(
				Theme.buttonPressedColor, CONTROLS_BUTTON);
			p1.add(buttonPressedBg, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Disabled Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			buttonDisabledBg = new SBControl(
				Theme.buttonDisabledColor, CONTROLS_BUTTON);
			p1.add(buttonDisabledBg, gc);
			
			// Spread
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Spread Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonSpreadLight = new SpreadControl(
				Theme.buttonSpreadLight, 20, CONTROLS_BUTTON);
			p1.add(buttonSpreadLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Spread Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonSpreadDark = new SpreadControl(
				Theme.buttonSpreadDark, 20, CONTROLS_BUTTON);
			p1.add(buttonSpreadDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonSpreadLightDisabled = new SpreadControl(
				Theme.buttonSpreadLightDisabled, 20, CONTROLS_BUTTON);
			p1.add(buttonSpreadLightDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonSpreadDarkDisabled = new SpreadControl(
				Theme.buttonSpreadDarkDisabled, 20, CONTROLS_BUTTON);
			p1.add(buttonSpreadDarkDisabled, gc);

			// border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonBorder = new SBControl(
				Theme.buttonBorderColor, CONTROLS_BUTTON);
			p1.add(buttonBorder, gc);
			
			// disabled border
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonDisabledBorder = new SBControl(
				Theme.buttonBorderDisabledColor, CONTROLS_BUTTON);
			p1.add(buttonDisabledBorder, gc);
			
			// disabled foreground
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Button Disabled Text"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonDisabledFg = new SBControl(
				Theme.buttonDisabledFgColor, true, CONTROLS_BUTTON);
			p1.add(buttonDisabledFg, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("CheckBox Disabled T."), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			checkDisabledFg = new SBControl(
				Theme.checkDisabledFgColor, true, CONTROLS_BUTTON);
			p1.add(checkDisabledFg, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("RadioButton Disabled T."), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			radioDisabledFg = new SBControl(
				Theme.radioDisabledFgColor, true, CONTROLS_BUTTON);
			p1.add(radioDisabledFg, gc);
			
			gc.gridy ++;
			gc.insets = insets4804;
			p1.add(new JLabel("ToggleButton Selected Bg"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			toggleSelectedBg = new SBControl(
				Theme.toggleSelectedBg, false, CONTROLS_BUTTON);
			p1.add(toggleSelectedBg, gc);
			
			// default/rollover
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Default Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonDefault = new SBControl(
				Theme.buttonDefaultColor, CONTROLS_BUTTON);
			p1.add(buttonDefault, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonRollover = new SBControl(
				Theme.buttonRolloverColor, CONTROLS_BUTTON);
			p1.add(buttonRollover, gc);
			
			// Flags
			gc.insets = new Insets(2, 8, 0, 0);
			gc.gridy = 4;
			gc.gridheight = 4;
			gc.gridwidth = 3;
			gc.anchor = GridBagConstraints.WEST;
			gc.fill = GridBagConstraints.NONE;
			
			JPanel p3 = new JPanel(new GridBagLayout());
			GridBagConstraints gc3 = new GridBagConstraints();
			gc3.anchor = GridBagConstraints.NORTHWEST;
			gc3.fill = GridBagConstraints.NONE;
			gc3.gridx = 0;
			gc3.gridy = 0;
			gc3.gridwidth = 2;
			gc3.insets = new Insets(0, 0, 0, 0);
			rolloverEnabled = new BooleanControl(
				Theme.buttonRolloverBorder, "Paint Rollover Border", CONTROLS_BUTTON);
			p3.add(rolloverEnabled, gc3);
			
			gc3.gridy ++;
			shiftTextEnabled = new BooleanControl(
				Theme.shiftButtonText, "Shift Button Text", CONTROLS_BUTTON);
			p3.add(shiftTextEnabled, gc3);

			gc3.gridy ++;
			enterEnabled = new BooleanControl(
				Theme.buttonEnter, "ENTER \"presses\" focused button",
				true, CONTROLS_BUTTON);
			p3.add(enterEnabled, gc3);
			
			gc3.gridwidth = 1;
			gc3.gridy ++;
			gc3.insets = new Insets(0, 0, 0, 4);
			focusEnabled = new BooleanControl(
				Theme.buttonFocus, "Paint Focus", CONTROLS_BUTTON);
			p3.add(focusEnabled, gc3);
			
			gc3.gridx ++;
			gc3.insets = new Insets(0, 0, 0, 12);
			focusBorderEnabled = new BooleanControl(
				Theme.buttonFocusBorder, "Paint Focus Border", CONTROLS_BUTTON);
			p3.add(focusBorderEnabled, gc3);
			
			p1.add(p3, gc);
			
			// checkmark
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridwidth = 1;
			gc.gridheight = 1;
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.insets = insets0804;
			p1.add(new JLabel("Checkmark Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonCheck = new SBControl(
				Theme.buttonCheckColor, CONTROLS_BUTTON);
			p1.add(buttonCheck, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Check Disabled"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			buttonCheckDisabled = new SBControl(
				Theme.buttonCheckDisabledColor, CONTROLS_BUTTON);
			p1.add(buttonCheckDisabled, gc);
			
			// Margin
			gc.gridx += 2;
			gc.gridy = 0;
			gc.gridheight = 8;
			gc.gridwidth = 1;
			gc.insets = new Insets(0, 8, 0, 0);
			
			p3 = new JPanel(new GridBagLayout());
			gc3.gridx = 0;
			gc3.gridy = 0;
			gc3.insets = new Insets(0, 0, 0, 0);
			
			ButtonGroup group = new ButtonGroup();
			JRadioButton rb = new JRadioButton("Button margin", true);
			group.add(rb);
			rb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AbstractButton b = (AbstractButton)e.getSource();
					
					if(!b.isSelected()) return;
					
					((CardLayout)cardPanel.getLayout()).show(cardPanel, "buttonMargin");
				}
			});
			p3.add(rb, gc3);
			
			gc3.gridy ++;
			rb = new JRadioButton("<html>CheckBox margin &<br>RadioButton margin");
			group.add(rb);
			rb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AbstractButton b = (AbstractButton)e.getSource();
					
					if(!b.isSelected()) return;
					
					((CardLayout)cardPanel.getLayout()).show(cardPanel, "checkMargin");
				}
			});
			p3.add(rb, gc3);
			
			gc3.gridy ++;
			gc3.insets = new Insets(4, 0, 0, 0);
			cardPanel = new JPanel(new CardLayout());
			cardPanel.add(createButtonMarginPanel(), "buttonMargin");
			cardPanel.add(createCheckMarginPanel(), "checkMargin");	
			p3.add(cardPanel, gc3);
			
			p1.add(p3, gc);
			
			panel.add(p1);
			
			return panel;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			rolloverEnabled.setSelected(Theme.buttonRolloverBorder.getValue());
			focusEnabled.setSelected(Theme.buttonFocus.getValue());
			focusBorderEnabled.setSelected(Theme.buttonFocusBorder.getValue());
			enterEnabled.setSelected(Theme.buttonEnter.getValue());
			shiftTextEnabled.setSelected(Theme.shiftButtonText.getValue());
			
			buttonNormalBg.update();
			buttonRolloverBg.update();
			buttonPressedBg.update();
			buttonDisabledBg.update();
			buttonBorder.update();
			buttonDisabledBorder.update();
			buttonDisabledFg.update();
			checkDisabledFg.update();
			radioDisabledFg.update();
			toggleSelectedBg.update();
			buttonRollover.update();
			buttonDefault.update();
			buttonCheck.update();
			buttonCheckDisabled.update();
			buttonSpreadDark.init();
			buttonSpreadLight.init();
			buttonSpreadDarkDisabled.init();
			buttonSpreadLightDisabled.init();
			
			mTop.setValue(Theme.buttonMargin.top);
			mLeft.setValue(Theme.buttonMargin.left);
			mBottom.setValue(Theme.buttonMargin.bottom);
			mRight.setValue(Theme.buttonMargin.right);
			
			cTop.setValue(Theme.checkMargin.top);
			cLeft.setValue(Theme.checkMargin.left);
			cBottom.setValue(Theme.checkMargin.bottom);
			cRight.setValue(Theme.checkMargin.right);
			
			inited = true;
		}
		
		private JPanel createButtonMarginPanel() {
			JPanel p2 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridx = 0;
			gc2.gridy = 1;
			gc2.insets = new Insets(0, 0, 0, 0);
			
			mLeft = new InsetsControl(new SpinnerNumberModel(16, 0, 99, 1),
				Theme.buttonMargin, InsetsControl.LEFT);
			p2.add(mLeft, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 0;
			mTop = new InsetsControl(new SpinnerNumberModel(2, 0, 99, 1),
				Theme.buttonMargin, InsetsControl.TOP);
			p2.add(mTop, gc2);
			gc2.gridy += 2;
			mBottom = new InsetsControl(new SpinnerNumberModel(3, 0, 99, 1),
				Theme.buttonMargin, InsetsControl.BOTTOM);
			p2.add(mBottom, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 1;
			mRight = new InsetsControl(new SpinnerNumberModel(16, 0, 99, 1),
				Theme.buttonMargin, InsetsControl.RIGHT);
			p2.add(mRight, gc2);
			
			return p2;
		}
		
		private JPanel createCheckMarginPanel() {
			JPanel p2 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.fill = GridBagConstraints.HORIZONTAL;
			gc2.gridx = 0;
			gc2.gridy = 1;
			gc2.insets = new Insets(0, 0, 0, 0);
			
			cLeft = new InsetsControl(new SpinnerNumberModel(16, 0, 99, 1),
				Theme.checkMargin, InsetsControl.LEFT);
			p2.add(cLeft, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 0;
			cTop = new InsetsControl(new SpinnerNumberModel(2, 0, 99, 1),
				Theme.checkMargin, InsetsControl.TOP);
			p2.add(cTop, gc2);
			gc2.gridy += 2;
			cBottom = new InsetsControl(new SpinnerNumberModel(3, 0, 99, 1),
				Theme.checkMargin, InsetsControl.BOTTOM);
			p2.add(cBottom, gc2);
			
			gc2.gridx ++;
			gc2.gridy = 1;
			cRight = new InsetsControl(new SpinnerNumberModel(16, 0, 99, 1),
				Theme.checkMargin, InsetsControl.RIGHT);
			p2.add(cRight, gc2);
			
			return p2;
		}

		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Button");
			
			ps.addParameter(rolloverEnabled.isSelected(),
				Theme.buttonRolloverBorder);
			ps.addParameter(focusEnabled.isSelected(),
				Theme.buttonFocus);
			ps.addParameter(focusBorderEnabled.isSelected(),
				Theme.buttonFocusBorder);
			ps.addParameter(enterEnabled.isSelected(),
				Theme.buttonEnter);
			ps.addParameter(shiftTextEnabled.isSelected(),
				Theme.shiftButtonText);
			
			ps.addParameter(Theme.buttonFont);
			ps.addParameter(Theme.checkFont);
			ps.addParameter(Theme.radioFont);
			
			ps.addParameter(buttonNormalBg);
			ps.addParameter(buttonNormalBg);
			ps.addParameter(buttonRolloverBg);
			ps.addParameter(buttonPressedBg);
			ps.addParameter(buttonDisabledBg);
			ps.addParameter(buttonBorder);
			ps.addParameter(buttonDisabledBorder);
			ps.addParameter(buttonDisabledFg);
			ps.addParameter(checkDisabledFg);
			ps.addParameter(radioDisabledFg);
			ps.addParameter(toggleSelectedBg);
			ps.addParameter(buttonRollover);
			ps.addParameter(buttonDefault);
			ps.addParameter(buttonCheck);
			ps.addParameter(buttonCheckDisabled);
			
			ps.addParameter(buttonSpreadDark);
			ps.addParameter(buttonSpreadLight);
			ps.addParameter(buttonSpreadDarkDisabled);
			ps.addParameter(buttonSpreadLightDisabled);
			
			ps.addParameter(new Insets(
					mTop.getIntValue(),
					mLeft.getIntValue(),
					mBottom.getIntValue(),
					mRight.getIntValue()),
				Theme.buttonMargin);
			
			ps.addParameter(new Insets(
				cTop.getIntValue(),
				cLeft.getIntValue(),
				cBottom.getIntValue(),
				cRight.getIntValue()),
			Theme.checkMargin);
			
			return ps;
		}
	}
	
	class InactiveFramePanel extends JPanel {
		
		private Border border = BorderFactory.createTitledBorder("Inactive Frame");
		private Dimension size = new Dimension(104, 52);

		InactiveFramePanel() {
			setBorder(border);
			
			setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 3));
			JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 2, 0));
			buttonPanel.setOpaque(false);

			JButton b = new SpecialUIButton(TinyWindowButtonUI.
				createButtonUIForType(TinyWindowButtonUI.MINIMIZE));
			b.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
			b.putClientProperty(TinyWindowButtonUI.DISABLED_WINDOW_BUTTON_KEY, Boolean.TRUE);
		    b.setEnabled(false);
		    buttonPanel.add(b);

		    b = new SpecialUIButton(TinyWindowButtonUI.
		    	createButtonUIForType(TinyWindowButtonUI.MAXIMIZE));
		    b.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
		    b.putClientProperty(TinyWindowButtonUI.DISABLED_WINDOW_BUTTON_KEY, Boolean.TRUE);
		    b.setEnabled(false);
		    buttonPanel.add(b);

		    b = new SpecialUIButton(TinyWindowButtonUI.
		    	createButtonUIForType(TinyWindowButtonUI.CLOSE));
		    b.putClientProperty(TinyWindowButtonUI.EXTERNAL_FRAME_BUTTON_KEY, Boolean.TRUE);
		    b.putClientProperty(TinyWindowButtonUI.DISABLED_WINDOW_BUTTON_KEY, Boolean.TRUE);
		    b.setEnabled(false);
		    buttonPanel.add(b);
		    
		    add(buttonPanel);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			Insets insets = border.getBorderInsets(this);
			int x = insets.left;
			int y = getHeight() - 35;
			int w = getWidth() - insets.left - insets.right;
			int h = 29;
			
			// paint part of frame caption (we don't need blended
			// colors, so performance should be ok
			int y2 = y;
			int spread1 = Theme.frameSpreadDarkDisabled.getValue();
			int spread2 = Theme.frameSpreadLightDisabled.getValue();
			Color c = Theme.frameCaptionDisabledColor.getColor();
			Color borderColor = Theme.frameBorderDisabledColor.getColor();
//	 1
			g.setColor(borderColor);
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 2
			Color c2 = ColorRoutines.darken(c, 4 * spread1);
			g.setColor(c2);
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 3
			g.setColor(ColorRoutines.lighten(c, 10 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 4
			g.setColor(c);
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 5
			g.setColor(ColorRoutines.darken(c, 2 * spread1));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 6
			TinyFrameBorder.buttonUpperDisabledColor = ColorRoutines.darken(c, 4 * spread1);
			g.setColor(TinyFrameBorder.buttonUpperDisabledColor);
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 7 - 8
			g.setColor(ColorRoutines.darken(c, 4 * spread1));
			g.fillRect(x + 1, y2, w - 2, 2);
			y2 += 2;
//	 9 - 12
			g.setColor(ColorRoutines.darken(c, 3 * spread1));
			g.fillRect(x + 1, y2, w - 2, 4);
			y2 += 4;
//	 13 - 15
			g.setColor(ColorRoutines.darken(c, 2 * spread1));
			g.fillRect(x + 1, y2, w - 2, 3);
			y2 += 3;
//	 16 - 17
			g.setColor(ColorRoutines.darken(c, 1 * spread1));
			g.fillRect(x + 1, y2, w - 2, 2);
			y2 += 2;
//	 18 - 19
			g.setColor(c);
			g.fillRect(x + 1, y2, w - 2, 2);
			y2 += 2;
//	 20...
			g.setColor(ColorRoutines.lighten(c, 2 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
			g.setColor(ColorRoutines.lighten(c, 4 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
			g.setColor(ColorRoutines.lighten(c, 5 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
			g.setColor(ColorRoutines.lighten(c, 6 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
			g.setColor(ColorRoutines.lighten(c, 8 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
			g.setColor(ColorRoutines.lighten(c, 9 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
			TinyFrameBorder.buttonLowerDisabledColor = ColorRoutines.lighten(c, 10 * spread2);
			g.setColor(TinyFrameBorder.buttonLowerDisabledColor);
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 27
			g.setColor(ColorRoutines.lighten(c, 4 * spread2));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 28
			g.setColor(ColorRoutines.darken(c, 2 * spread1));
			g.drawLine(x + 1, y2, x + w - 2, y2);
			y2 ++;
//	 29		
			g.setColor(Theme.frameLightDisabledColor.getColor());
			g.drawLine(x + 1, y2, x + w - 2, y2);
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		
		public Dimension getMinimumSize() {
			return size;
		}
	}
	
	class FrameCPsPanel extends JPanel {
		
		private CardLayout cardLayout = new CardLayout();
		private JPanel cardPanel;
		
		FrameCPsPanel() {
			setupUI();
		}

		private void setupUI() {
			setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.NONE;
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.insets = new Insets(0, 0, 0, 0);
			gc.gridx = 0;
			gc.gridy = 0;
			
			// CardPanel
			cardPanel = new JPanel(cardLayout);
			frameCP = new FrameCP();
			frameButtonsCP = new FrameButtonsCP();
			frameCloseButtonCP = new FrameCloseButtonCP();
			cardPanel.add(frameCP, "Frame");
			cardPanel.add(frameButtonsCP, "FrameButtons");
			cardPanel.add(frameCloseButtonCP, "FrameCloseButton");
			cardLayout.layoutContainer(this);
			
			add(cardPanel, gc);
			
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc2 = new GridBagConstraints();
			gc2.fill = GridBagConstraints.NONE;
			gc2.weighty = 1;
			gc2.anchor = GridBagConstraints.SOUTH;
			gc2.insets = new Insets(0, 0, 36, 0);
			gc2.gridx = 0;
			gc2.gridy = 0;
			disabledFramePanel = new InactiveFramePanel();
			p1.add(disabledFramePanel, gc2);
			
			gc.anchor = GridBagConstraints.CENTER;
			gc.gridx ++;
			gc.weightx = 1;
			add(p1, gc);
			
			// Radios
			p1 = new JPanel(new GridBagLayout());
			p1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(3, 0, 1, 1),
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)));
			gc2 = new GridBagConstraints();
			gc2.anchor = GridBagConstraints.NORTHWEST;
			gc2.gridy = 0;
			gc2.gridx = 0;
			gc2.insets = new Insets(4, 4, 0, 4);
			
			ButtonGroup group = new ButtonGroup();
			JRadioButton radio = new JRadioButton("Frame", true);
			group.add(radio);
			radio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(cardPanel, "Frame");
				}
			});
			p1.add(radio, gc2);
			
			gc2.gridy ++;
			gc2.insets = new Insets(0, 4, 0, 4);
			radio = new JRadioButton("Iconify/Maximize Buttons");
			group.add(radio);
			radio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(cardPanel, "FrameButtons");
				}
			});
			p1.add(radio, gc2);
			
			gc2.gridy ++;
			radio = new JRadioButton("Close Button");
			group.add(radio);
			radio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(cardPanel, "FrameCloseButton");
				}
			});
			p1.add(radio, gc2);
			
			gc2.gridy ++;
			gc2.insets = new Insets(12, 4, 0, 4);
			JButton b = new JButton("<html>Activate/Deactivate<br>Internal Frame");
			b.addActionListener(new DeactivateInternalFrameAction());
			p1.add(b, gc2);
			
			if(decoratedFramesCheck == null) {
				decoratedFramesCheck = new JCheckBox(
					"Decorated Frame", true);
				decoratedFramesCheck.addActionListener(new DecorateFrameAction());
			}
			
			gc2.gridy ++;
			gc2.insets = new Insets(12, 4, 4, 4);
			gc2.weighty = 1;
			p1.add(decoratedFramesCheck, gc2);

			gc.anchor = GridBagConstraints.NORTHEAST;
			gc.fill = GridBagConstraints.VERTICAL;
			gc.gridx ++;
			gc.weightx = 0;
			add(p1, gc);
		}

		class DeactivateInternalFrameAction implements ActionListener {
			
			public void actionPerformed(ActionEvent e) {
				try {
					internalFrame.setSelected(!internalFrame.isSelected());
				} catch (PropertyVetoException ignore) {}
			}
		}
	}
	
	class FrameCP extends CP {
		
		FrameCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Frame");
			
			ps.addParameter(Theme.frameTitleFont);
			ps.addParameter(Theme.internalFrameTitleFont);
			ps.addParameter(Theme.internalPaletteTitleFont);

			ps.addParameter(frameCaption);
			ps.addParameter(frameCaptionDisabled);
			ps.addParameter(frameBorder);
			ps.addParameter(frameLight);
			ps.addParameter(frameBorderDisabled);
			ps.addParameter(frameLightDisabled);
			ps.addParameter(frameTitle);
			ps.addParameter(frameTitleShadow);
			ps.addParameter(frameTitleDisabled);
			
			ps.addParameter(frameSpreadDark);
			ps.addParameter(frameSpreadLight);
			ps.addParameter(frameSpreadDarkDisabled);
			ps.addParameter(frameSpreadLightDisabled);
			
			return ps;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;
			
			frameCaption.update();
			frameCaptionDisabled.update();
			frameBorder.update();
			frameLight.update();
			frameBorderDisabled.update();
			frameLightDisabled.update();
			frameTitle.update();
			frameTitleShadow.update();
			frameTitleDisabled.update();

			frameSpreadDark.init();
			frameSpreadLight.init();
			frameSpreadDarkDisabled.init();
			frameSpreadLightDisabled.init();
			
			inited = true;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Caption Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameCaption = new SBControl(
				Theme.frameCaptionColor, CONTROLS_ACTIVE_FRAME_CAPTION);
			p1.add(frameCaption, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Caption Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameCaptionDisabled = new SBControl(
				Theme.frameCaptionDisabledColor, CONTROLS_INACTIVE_FRAME_CAPTION);
			p1.add(frameCaptionDisabled, gc);
			
			// Spread
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Spread Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSpreadDark = new SpreadControl(
				Theme.frameSpreadDark, 10, CONTROLS_ACTIVE_FRAME_CAPTION);
			p1.add(frameSpreadDark, gc);			
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Spread Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSpreadLight = new SpreadControl(
				Theme.frameSpreadLight, 10, CONTROLS_ACTIVE_FRAME_CAPTION);
			p1.add(frameSpreadLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("S. Dark Disabled"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSpreadDarkDisabled = new SpreadControl(
				Theme.frameSpreadDarkDisabled, 10, CONTROLS_INACTIVE_FRAME_CAPTION);
			p1.add(frameSpreadDarkDisabled, gc);gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("S. Light Disabled"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSpreadLightDisabled = new SpreadControl(
				Theme.frameSpreadLightDisabled, 10, CONTROLS_INACTIVE_FRAME_CAPTION);
			p1.add(frameSpreadLightDisabled, gc);
			
			// Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameBorder = new SBControl(
				Theme.frameBorderColor, CONTROLS_FRAME_BORDER);
			p1.add(frameBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Border Light Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameLight = new SBControl(
				Theme.frameLightColor, CONTROLS_FRAME_BORDER);
			p1.add(frameLight, gc);
			gc.gridy ++;

			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameBorderDisabled = new SBControl(
				Theme.frameBorderDisabledColor, CONTROLS_INACTIVE_FRAME_CAPTION);
			p1.add(frameBorderDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameLightDisabled = new SBControl(
				Theme.frameLightDisabledColor, CONTROLS_INACTIVE_FRAME_CAPTION);
			p1.add(frameLightDisabled, gc);
			
			// Title
			gc.gridx ++;
			gc.gridy = 0;
			gc.gridwidth = 1;
			gc.insets = new Insets(0, 8, 0, 8);
			p1.add(new JLabel("Title Color"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameTitle = new SBControl(
				Theme.frameTitleColor, CONTROLS_ACTIVE_FRAME_CAPTION);
			p1.add(frameTitle, gc);
			gc.gridy ++;
			
			gc.insets = new Insets(4, 8, 0, 8);
			p1.add(new JLabel("Title Shadow Color"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameTitleShadow = new SBControl(
				Theme.frameTitleShadowColor, CONTROLS_ACTIVE_FRAME_CAPTION);
			p1.add(frameTitleShadow, gc);
			gc.gridy ++;
			
			gc.insets = new Insets(4, 8, 0, 8);
			p1.add(new JLabel("Title Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameTitleDisabled = new SBControl(
				Theme.frameTitleDisabledColor, CONTROLS_INACTIVE_FRAME_CAPTION);
			p1.add(frameTitleDisabled, gc);
			
			panel.add(p1);
			
			return panel;
		}
	}
	
	class FrameButtonsCP extends CP {
		
		FrameButtonsCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Iconify/Maximize Button");

			ps.addParameter(frameButt);
			ps.addParameter(frameButtRollover);
			ps.addParameter(frameButtPressed);
			ps.addParameter(frameButtDisabled);
			ps.addParameter(frameButtBorder);
			ps.addParameter(frameButtBorderDisabled);
			ps.addParameter(frameSymbol);
			ps.addParameter(frameSymbolPressed);
			ps.addParameter(frameSymbolDisabled);
			ps.addParameter(frameSymbolDark);
			ps.addParameter(frameSymbolLight);
			ps.addParameter(frameSymbolDarkDisabled);
			ps.addParameter(frameSymbolLightDisabled);

			ps.addParameter(frameButtSpreadDark);
			ps.addParameter(frameButtSpreadLight);
			ps.addParameter(frameButtSpreadDarkDisabled);
			ps.addParameter(frameButtSpreadLightDisabled);

			return ps;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;

			frameButt.update();
			frameButtRollover.update();
			frameButtPressed.update();
			frameButtDisabled.update();
			frameButtBorder.update();
			frameButtBorderDisabled.update();
			frameSymbol.update();
			frameSymbolPressed.update();
			frameSymbolDisabled.update();
			frameSymbolDark.update();
			frameSymbolLight.update();
			frameSymbolDarkDisabled.update();
			frameSymbolLightDisabled.update();
			
			frameButtSpreadDark.init();
			frameButtSpreadLight.init();
			frameButtSpreadDarkDisabled.init();
			frameButtSpreadLightDisabled.init();

			inited = true;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButt = new SBControl(
				Theme.frameButtColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButt, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButtRollover = new SBControl(
				Theme.frameButtRolloverColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtRollover, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Pressed Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButtPressed = new SBControl(
				Theme.frameButtPressedColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtPressed, gc);			
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButtDisabled = new SBControl(
				Theme.frameButtDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtDisabled, gc);
			
			// Spread
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Spread Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtSpreadLight = new SpreadControl(
				Theme.frameButtSpreadLight, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtSpreadLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Spread Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtSpreadDark = new SpreadControl(
				Theme.frameButtSpreadDark, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtSpreadDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtSpreadLightDisabled = new SpreadControl(
				Theme.frameButtSpreadLightDisabled, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtSpreadLightDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtSpreadDarkDisabled = new SpreadControl(
				Theme.frameButtSpreadDarkDisabled, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtSpreadDarkDisabled, gc);
			
			// Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtBorder = new SBControl(
				Theme.frameButtBorderColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtBorderDisabled = new SBControl(
				Theme.frameButtBorderDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtBorderDisabled, gc);

			// Symbol
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Symbol Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSymbol = new SBControl(
				Theme.frameSymbolColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbol, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Pressed Symbol"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSymbolPressed = new SBControl(
				Theme.frameSymbolPressedColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolPressed, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Symbol"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSymbolDisabled = new SBControl(
				Theme.frameSymbolDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolDisabled, gc);
			
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = new Insets(0, 8, 0, 8);
			p1.add(new JLabel("Symbol Dark Color"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameSymbolDark = new SBControl(
				Theme.frameSymbolDarkColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolDark, gc);
			gc.gridy ++;
			
			gc.insets = new Insets(4, 8, 0, 8);
			p1.add(new JLabel("Symbol Light Color"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameSymbolLight = new SBControl(
				Theme.frameSymbolLightColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolLight, gc);
			gc.gridy ++;
			
			gc.insets = new Insets(4, 8, 0, 8);
			p1.add(new JLabel("Symbol Dark Disabled"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameSymbolDarkDisabled = new SBControl(
				Theme.frameSymbolDarkDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolDarkDisabled, gc);
			gc.gridy ++;
			
			gc.insets = new Insets(4, 8, 0, 8);
			p1.add(new JLabel("Symbol Light Disabled"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameSymbolLightDisabled = new SBControl(
				Theme.frameSymbolLightDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolLightDisabled, gc);

			panel.add(p1);
			
			return panel;
		}
	}
	
	class FrameCloseButtonCP extends CP {
		
		FrameCloseButtonCP() {
			super();
			super.setupUI(setupUI());
		}
		
		public ParameterSet getParameterSet() {
			ParameterSet ps = new ParameterSet(this, "Close Button");

			ps.addParameter(frameButtClose);
			ps.addParameter(frameButtCloseRollover);
			ps.addParameter(frameButtClosePressed);
			ps.addParameter(frameButtCloseDisabled);
			ps.addParameter(frameButtCloseBorder);
			ps.addParameter(frameButtCloseBorderDisabled);
			ps.addParameter(frameSymbolClose);
			ps.addParameter(frameSymbolClosePressed);
			ps.addParameter(frameSymbolCloseDisabled);
			ps.addParameter(frameSymbolCloseDark);
			ps.addParameter(frameSymbolCloseDarkDisabled);
			
			ps.addParameter(frameButtCloseSpreadDark);
			ps.addParameter(frameButtCloseSpreadLight);
			ps.addParameter(frameButtCloseSpreadDarkDisabled);
			ps.addParameter(frameButtCloseSpreadLightDisabled);

			return ps;
		}
		
		public void init(boolean always) {
			if(inited && !always) return;

			frameButtClose.update();
			frameButtCloseRollover.update();
			frameButtClosePressed.update();
			frameButtCloseDisabled.update();
			frameButtCloseBorder.update();
			frameButtCloseBorderDisabled.update();
			frameSymbolClose.update();
			frameSymbolClosePressed.update();
			frameSymbolCloseDisabled.update();
			frameSymbolCloseDark.update();
			frameSymbolCloseDarkDisabled.update();
			
			frameButtCloseSpreadDark.init();
			frameButtCloseSpreadLight.init();
			frameButtCloseSpreadDarkDisabled.init();
			frameButtCloseSpreadLightDisabled.init();

			inited = true;
		}
		
		JPanel setupUI() {
			JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
			JPanel p1 = new JPanel(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.anchor = GridBagConstraints.NORTHWEST;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.gridx = 0;
			gc.gridy = 0;
			gc.insets = insets0404;
			
			p1.add(new JLabel("Button Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButtClose = new SBControl(
				Theme.frameButtCloseColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtClose, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Rollover Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButtCloseRollover = new SBControl(
				Theme.frameButtCloseRolloverColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseRollover, gc);
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Pressed Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButtClosePressed = new SBControl(
				Theme.frameButtClosePressedColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtClosePressed, gc);			
			gc.gridy ++;
			
			gc.insets = insets4404;
			p1.add(new JLabel("Disabled Color"), gc);
			gc.gridy ++;
			gc.insets = insets0404;
			frameButtCloseDisabled = new SBControl(
				Theme.frameButtCloseDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseDisabled, gc);
			
			// Spread
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Spread Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtCloseSpreadLight = new SpreadControl(
				Theme.frameButtCloseSpreadLight, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseSpreadLight, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Spread Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtCloseSpreadDark = new SpreadControl(
				Theme.frameButtCloseSpreadDark, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseSpreadDark, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Light"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtCloseSpreadLightDisabled = new SpreadControl(
				Theme.frameButtCloseSpreadLightDisabled, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseSpreadLightDisabled, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled S. Dark"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtCloseSpreadDarkDisabled = new SpreadControl(
				Theme.frameButtCloseSpreadDarkDisabled, 20, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseSpreadDarkDisabled, gc);
			
			// Border
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Border Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtCloseBorder = new SBControl(
				Theme.frameButtCloseBorderColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseBorder, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Border"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameButtCloseBorderDisabled = new SBControl(
				Theme.frameButtCloseBorderDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameButtCloseBorderDisabled, gc);

			// Symbol
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = insets0804;
			p1.add(new JLabel("Symbol Color"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSymbolClose = new SBControl(
				Theme.frameSymbolCloseColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolClose, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Pressed Symbol"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSymbolClosePressed = new SBControl(
				Theme.frameSymbolClosePressedColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolClosePressed, gc);
			gc.gridy ++;
			
			gc.insets = insets4804;
			p1.add(new JLabel("Disabled Symbol"), gc);
			gc.gridy ++;
			gc.insets = insets0804;
			frameSymbolCloseDisabled = new SBControl(
				Theme.frameSymbolCloseDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolCloseDisabled, gc);
			
			gc.gridx ++;
			gc.gridy = 0;
			gc.insets = new Insets(0, 8, 0, 8);
			p1.add(new JLabel("Symbol Dark Color"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameSymbolCloseDark = new SBControl(
				Theme.frameSymbolCloseDarkColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolCloseDark, gc);
			gc.gridy ++;
			
			gc.insets = new Insets(4, 8, 0, 8);
			p1.add(new JLabel("Symbol Dark Disabled"), gc);
			gc.gridy ++;
			gc.insets = new Insets(0, 8, 0, 8);
			frameSymbolCloseDarkDisabled = new SBControl(
				Theme.frameSymbolCloseDarkDisabledColor, CONTROLS_WINDOW_BUTTON);
			p1.add(frameSymbolCloseDarkDisabled, gc);

			panel.add(p1);
			
			return panel;
		}
	}
	
	class BooleanControl extends JCheckBox implements ActionListener {
		
		private int controlMode;
		BooleanReference ref;
		boolean forceUpdate;
		
		BooleanControl(BooleanReference ref, String text, int controlMode) {
			this(ref, text, false, controlMode);
		}
		
		/**
		 * 
		 * @param ref
		 * @param text
		 * @param forceUpdate if true, 'Apply Settings' button will
		 * be enabled as value changes
		 * @param controlMode
		 */
		BooleanControl(BooleanReference ref, String text, boolean forceUpdate, int controlMode) {
			super(text);
			
			this.ref = ref;
			this.forceUpdate = forceUpdate;
			this.controlMode = controlMode;
			
			addActionListener(this);
		}
		
		public void actionPerformed(ActionEvent e) {
			storeUndoData(this);
			ref.setValue(isSelected());
			updateTargets(true);
		}
		
		void updateTargets(boolean activateApplyButton) {
			if(forceUpdate) {
				if(activateApplyButton) {
					examplePanel.update(true);
				}
				else {
					setTheme();
				}
			}
			else {
				repaintTargets(controlMode);
			}
		}
	}
	
	class SpreadControl extends JPanel implements FocusListener, Selectable {
		
		private int controlMode = 0;
		private final Color activeColor = Color.WHITE;
		private final Color inactiveColor = Color.LIGHT_GRAY;
		private int max = 20;
		private Dimension size = new Dimension(64, 20);
		private Font font = new Font("sansserif", Font.BOLD, 12);
		private IntReference spreadRef;
		private boolean hasFocus = false;
		private boolean inDrag = false;
		private boolean selected = false;
		int spread;
		private int x1 = 7, x2, y = 7;
		
		SpreadControl(IntReference spreadRef, int max, int controlMode) {
			this.spreadRef = spreadRef;
			this.max = max;
			this.controlMode = controlMode;
			
			addMouseListener(new MouseHandler());
			addMouseMotionListener(new MouseMotionHandler());
			addKeyListener(new ArrowKeyAction());
			addFocusListener(this);
		}
		
		void update(int spread, boolean storeUndo) {
			if(spread == this.spread) return;

			if(storeUndo) storeUndoData(this);
			
			this.spread = spread;

			spreadRef.setValue(spread);
			
			repaint();
			
			if(internalFrame == null) return;
			
			updateTargets();
		}
		
		void updateTargets() {
			repaintTargets(controlMode);
		}
		
		int getValue() {
			return spreadRef.getValue();
		}
		
		public void init() {
			update(spreadRef.getValue(), false);
		}
		
		public IntReference getIntReference() {
			return spreadRef;
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		
		public void paint(Graphics g) {
			if(hasFocus) {
				g.setColor(activeColor);
			}
			else {
				g.setColor(inactiveColor);
			}
			
			// fill background
			g.fillRect(2, 2, getWidth() - 3, getHeight() - 3);
			
			// paint border
			if(selected) {
				g.setColor(Color.DARK_GRAY);
				g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
				g.setColor(Color.RED);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
			else {
				g.setColor(Color.DARK_GRAY);
				g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
				g.setColor(Theme.backColor.getColor());
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}
			
			g.setColor(Color.BLACK);
			x2 = getWidth() - 24;
			// Track
			g.drawLine(x1, y - 3, x1, y + 3);
			g.drawLine(x2, y - 3, x2, y + 3);
			g.drawLine(x1, y, x2, y);
			
			// Thumb
			int x = spread * (x2 - x1) / max + x1;
			
			g.drawLine(x, y + 2, x, y + 2);
			g.drawLine(x - 1, y + 3, x + 1, y + 3);
			g.drawLine(x - 2, y + 4, x + 2, y + 4);
			g.drawLine(x - 3, y + 5, x + 3, y + 5);
			g.drawLine(x - 4, y + 6, x + 4, y + 6);
			
			//Number
			g.setFont(font);
			FontMetrics fm = g.getFontMetrics();
			int xd = fm.stringWidth(String.valueOf(spread));
			g.drawString(String.valueOf(spread), getWidth() - xd - 3, getHeight() - 5);
		}
		
		public void focusGained(FocusEvent e) {
			hasFocus = true;
		}
		
		public void focusLost(FocusEvent e) {
			hasFocus = false;
			repaint();
		}
		
		class MouseHandler extends MouseAdapter {
			
			public void mousePressed(MouseEvent e) {
				if(e.isControlDown()) {
					if(!selected) {
						selection.add(SpreadControl.this);
					}
					return;
				}
				else if(e.isAltDown()) {
					if(selected) {
						selection.remove(SpreadControl.this);
					}
					return;
				}

				if(SpreadControl.this.equals(frameSpreadDark) ||
					SpreadControl.this.equals(frameSpreadLight))
				{
					if(!internalFrame.isSelected()) {
						try {
							internalFrame.setSelected(true);
						} catch (PropertyVetoException ignore) {}
					}
				}
				else if(SpreadControl.this.equals(frameSpreadDarkDisabled) ||
					SpreadControl.this.equals(frameSpreadLightDisabled))
				{
					if(internalFrame.isSelected()) {
						try {
							internalFrame.setSelected(false);
						} catch (PropertyVetoException ignore) {}
					}
				}
				
				if(!hasFocus) {
					requestFocusInWindow();
					repaint();
				}
				else {
					int x = e.getX();
					if(x < x1) x = x1;
					if(x > x2) x = x2;
					
					int xd = (x - x1);
					
					update(xd * max / (x2 - x1), true);
				}
			}
			
			public void mouseReleased(MouseEvent e) {
				repaint();
				inDrag = false;
			}
		}
		
		class MouseMotionHandler extends MouseMotionAdapter {
			
			public void mouseDragged(MouseEvent e) {
				if(e.isControlDown() || e.isAltDown()) return;
				
				if(!inDrag) {
					inDrag = true;
					storeUndoData(SpreadControl.this);
				}
				
				int x = e.getX();
				
				if(x < x1) x = x1;
				if(x > x2) x = x2;
				
				int xd = (x - x1);
				
				update(xd * max / (x2 - x1), false);
			}
		}
		
		class ArrowKeyAction extends KeyAdapter implements ActionListener {
			
			private javax.swing.Timer keyTimer;
			private int step;
			
			ArrowKeyAction() {
				keyTimer = new javax.swing.Timer(20, this);
			}
			
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 38) {	// up => increase
					if(spread == max) return;
					
					step = 1;
					
					changeVal();
					keyTimer.setInitialDelay(300);
					keyTimer.start();
				}
				else if(e.getKeyCode() == 40) {	// dwn => decrease
					if(spread == 0) return;
					
					step = -1;
					
					changeVal();
					keyTimer.setInitialDelay(300);
					keyTimer.start();
				}
			}
			
			public void keyReleased(KeyEvent e) {
				keyTimer.stop();
			}
			
			// the keyTimer action
			public void actionPerformed(ActionEvent e) {
				changeVal();
			}
			
			private void changeVal() {
				if(spread + step < 0 || spread + step > max) return;
				
				update(spread + step, true);
			}
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			if(this.selected == selected) return;
			
			this.selected = selected;
			repaint();
		}
	}
	
	class MagnifierPanel extends SizedPanel {
		
		private Dimension scaledSize;
		
		MagnifierPanel(int w, int h) {
			super(w, h);
		}
		
		public void paint(Graphics g) {
			if(magnifierImg == null || scaledSize == null || !magnifierActive) {
				super.paint(g);
			}
			else {
				g.drawImage(magnifierImg,
					0, 0, scaledSize.width * scaleFactor, scaledSize.height * scaleFactor,
					0, 0, scaledSize.width, scaledSize.height, this);
			}
		}
		
		void setPaintData(Dimension scaledSize) {
			this.scaledSize = scaledSize;
			
			repaint();
		}
	}
	
	class SizedPanel extends JPanel {
		
		private Dimension size;
		private Color grey = new Color(204, 204, 204);
		
		SizedPanel(int w, int h) {
			size = new Dimension(w, h);
			setBackground(Color.WHITE);
		}
		
		public Dimension getPreferredSize() {
			return size;
		}
		
		public void paint(Graphics g) {
			int w = getWidth(); int h = getHeight();
			int xOffset = 0;
			
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, w, h);
			
			g.setColor(grey);
			
			for(int y = 0; y < h; y += 8) {
				for(int x = 0; x < w; x += 16) {
					g.fillRect(x + xOffset, y, 8, 8);
				}
				
				if(xOffset == 0) xOffset = 8;
				else xOffset = 0;
			}
		}
		
		public void update(Graphics g) {
			paint(g);
		}
	}
	
	/**
	 * ProgressAction is triggered by a timer each 500 msec.
	 * It bumps progressbar values and switches focused list
	 * item and focused table cell to focused/unfocused state.
	 * @author Hans Bickel
	 *
	 */
	class ProgressAction implements ActionListener {
		
		private int progressValue = 0;
		private int focusValue = 0;
		
		public void actionPerformed(ActionEvent e) {			
			progressValue ++;
			focusValue ++;
			
			if(progressValue > 20) {
				progressValue = 0;
				progressBar1.setIndeterminate(!progressBar1.isIndeterminate());
				progressBar2.setIndeterminate(!progressBar2.isIndeterminate());
				progressBar3.setIndeterminate(!progressBar3.isIndeterminate());
				progressBar4.setIndeterminate(!progressBar4.isIndeterminate());
			}
			
			progressBar1.setValue(progressValue);
			progressBar2.setValue(progressValue);
			progressBar3.setValue(progressValue);
			progressBar4.setValue(progressValue);
			
			int v = progressValue % 20;
			
			if(v < 7) {
				progressBar1.setString("Fun");
				progressBar2.setString("Fun");
			}
			else if(v < 14) {
				progressBar1.setString("with");
				progressBar2.setString("with");
			}
			else {
				progressBar1.setString("Swing");
				progressBar2.setString("Swing");
			}
			
			boolean fs = (focusValue % 6 < 3);
			
			if(fs != focusedState) {
				focusedState = !focusedState;
				focusedCellLabel.setText(focusedState ? " Focused selected Cell" : " Unfocused selected Cell");
				focusedItemLabel.setText(focusedState ? " Focused selected Item" : " Unfocused selected Item");
				fakeList.repaint();
				fakeTable.repaint();
			}
		}
	}
	
	class ProgressBarAction extends MouseAdapter {
		
		public void mousePressed(MouseEvent e) {
			if(progressTimer == null) {
				startProgressTimer();
			}
			else if(progressTimer.isRunning()) {
				stopProgressTimer();
			}
			else {
				startProgressTimer();
			}
		}
	}
	
	static class ThemeFileFilter extends FileFilter {
		
		public boolean accept(File pathname) {
			if(pathname.isDirectory()) return true;
			if(pathname.getName().endsWith(Theme.FILE_EXTENSION)) return true;
			
			return false;
		}
		
		public String getDescription() {
			return "TinyLaF Theme Files (" + Theme.FILE_EXTENSION + ")";
		}
	}
	
	class SelectThemeAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem)e.getSource();
			int index = Integer.parseInt(item.getActionCommand());
			
			openTheme(themes[index]);
		}
	}
	
	/**
	 * ColorizeIconCheck combines a checkbox and a (colorizable) icon.
	 * Each ColorizeIconCheck has its HSBControl.
	 */
	class ColorizeIconCheck extends JPanel implements ActionListener {
		
		HSBControl hsb;
		private JLabel iconLabel;
		private JCheckBox check;
		private Icon icon;
		BooleanReference ref;
		
		ColorizeIconCheck(BooleanReference ref, HSBControl field, String ttt) {
			this.ref = ref;
			this.hsb = field;
			setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			check = new JCheckBox("", ref.getValue());
			check.addActionListener(this);
			add(check);
			iconLabel = new JLabel("");
			add(iconLabel);
			super.setToolTipText(ttt);
		}
		
		public void setIcon(Icon i) {
			icon = i;
			iconLabel.setIcon(icon);
		}
		
		public Icon getIcon() {
			return icon;
		}
		
		public void setSelected(boolean b) {
			check.setSelected(b);
			ref.setValue(b);
		}
		
		public boolean isSelected() {
			return check.isSelected();
		}

		public void actionPerformed(ActionEvent e) {
			storeUndoData(this);
			ref.setValue(check.isSelected());
			colorizeIcon(hsb, check.isSelected());
			examplePanel.update(true);
		}
	}
	
	class DecorateFrameAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {				
			switchFrameDecoration();
		}
	}

	class UpdateAction implements ChangeListener {
		
		public void stateChanged(ChangeEvent e) {
			examplePanel.update(true);
		}
	}

	class PopupTrigger extends JLabel implements MouseListener {
		
		private long cancelTime = 0L;

		PopupTrigger() {
			super("Popup trigger");
			init();
			addMouseListener(this);
			setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
		}
		
		private void init() {
			setOpaque(true);
			setBackground(Color.LIGHT_GRAY);
			setForeground(Color.BLACK);
		}

		public void setUI(LabelUI ui) {
			super.setUI(ui);
			init();
		}

		// MouseListener implementation
		public void mousePressed(MouseEvent e) {
			if(System.currentTimeMillis() - cancelTime < 10L) {
				// Popup was cancelled by this mouse press
				return;
			}
			
			if(thePopup == null) {
				thePopup = new JPopupMenu("Popup Menu");
				
				thePopup.addPopupMenuListener(new PopupMenuListener() {

					public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}

					public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

					public void popupMenuCanceled(PopupMenuEvent e) {
						cancelTime = System.currentTimeMillis();
					}
					
				});
				JMenuItem item = new JMenuItem("Popup item #1");
				thePopup.add(item);
				item = new JMenuItem("Popup item #2");
				thePopup.add(item);
				
				thePopup.addSeparator();
				
				item = new JMenuItem("Popup item #3");
				thePopup.add(item);
				item = new JMenuItem("Popup disabled item");
				item.setEnabled(false);
				thePopup.add(item);
			}
			
			thePopup.show(PopupTrigger.this, 0, -thePopup.getPreferredSize().height - 1);
		}
		
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
	}

	private class AboutDialog extends JDialog {
		
		AboutDialog() {
			super(theFrame, "About TinyLaF", true);
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			getContentPane().setLayout(new BorderLayout());
			JPanel p1 = new JPanel(new BorderLayout());
			
			final String msg = "<html>" +
				"TinyLaF v" + TinyLookAndFeel.VERSION_STRING +
				" (" + TinyLookAndFeel.DATE_STRING + ")" +
				"<br>Copyright 2003 - 2009  Hans Bickel" +
				"<br>TinyLaF Home: www.muntjak.de/hans/java/tinylaf/" +
				"<br><br>" +
				"This program is free software: you can redistribute it and/or modify" +
				"<br>it under the terms of the GNU Lesser General Public License as published by" +
				"<br>the Free Software Foundation, either version 3 of the License, or" +
				"<br>(at your option) any later version." +
				"<br><br>" +
				"This program is distributed in the hope that it will be useful, but" +
				"<br>WITHOUT ANY WARRANTY; without even the implied warranty of" +
				"<br>MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See" +
				"<br>the GNU Lesser General Public License for more details." +
				"<br><br>" +
				"You should have received a copy of the GNU Lesser General Public License" +
				"<br>along with this program.  If not, see www.gnu.org/licenses/.";

			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
			p.add(new JLabel(msg));
			p1.add(p, BorderLayout.NORTH);

			p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
			JButton b = new JButton("Copy TinyLaF Link");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					
					if(cb == null) {
						JOptionPane.showMessageDialog(AboutDialog.this,
							"System Clipboard not available.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					}
					else {
						StringSelection ss = new StringSelection(
						"http://www.muntjak.de/hans/java/tinylaf/");
						cb.setContents(ss, ss);
					}
				}
			});
			p.add(b);
			
			b = new JButton("Copy LGPL Link");
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
					
					if(cb == null) {
						JOptionPane.showMessageDialog(AboutDialog.this,
							"System Clipboard not available.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					}
					else {
						StringSelection ss = new StringSelection(
						"http://www.gnu.org/licenses/lgpl.html");
						cb.setContents(ss, ss);
					}
				}
			});
			p.add(b);
			
			b = new JButton("Close");
			getRootPane().setDefaultButton(b);
			b.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AboutDialog.this.dispose();
				}
			});
			p.add(b);
			
			p1.add(p, BorderLayout.SOUTH);
			getContentPane().add(p1, BorderLayout.CENTER);
			
			pack();
			
			Point loc = theFrame.getLocationOnScreen();
			loc.x += (theFrame.getWidth() - getWidth()) / 2;
			loc.y += (theFrame.getHeight() - getHeight()) / 2;
			
			setLocation(loc);
			setVisible(true);
		}
	}

	class XFrame extends JFrame {

		XFrame(String title) {
			super(title);

			if(TinyLookAndFeel.ROBOT != null) {
				final AWTEventListener l = new AWTEventListener() {
					public void eventDispatched(AWTEvent e) {
						if(!magnifierActive) return;
						if(rootPane == null || !rootPane.isShowing()) return;
						if(magnifierPanel == null) return;
						if(!(e instanceof MouseEvent)) return;
						
						MouseEvent me = (MouseEvent)e;
						Component source = me.getComponent();

						if(source != null) {
							Point p = SwingUtilities.convertPoint(
								source, me.getPoint(), rootPane);
							
							if(p.x < 0) p.x = 0;
							if(p.y < 0) p.y = 0;
							
							Dimension d = magnifierPanel.getSize();
							
							if(d.width <= 0 || d.height <= 0) return;
							
							Dimension scaledSize = new Dimension(
								d.width / scaleFactor + (d.width % scaleFactor == 0 ? 0 : 1),
								d.height / scaleFactor + (d.height % scaleFactor == 0 ? 0 : 1));
							
							// p is in RootPane coordinates
							// The point at cursor position should
							// be in the center of the scaled image
							Point imageLoc = new Point(
								p.x - scaledSize.width / 2,
								p.y - scaledSize.height / 2);
							d = rootPane.getSize();
							
							if(imageLoc.x < 0) {
								imageLoc.x = 0;
							}
							else if(imageLoc.x + scaledSize.width > d.width) {
								imageLoc.x = d.width - scaledSize.width;
							}
							
							if(imageLoc.y < 0) {
								imageLoc.y = 0;
							}
							else if(imageLoc.y + scaledSize.height > d.height) {
								imageLoc.y = d.height - scaledSize.height;
							}
							
							Point screenLoc = rootPane.getLocationOnScreen();
							Rectangle screenRect = new Rectangle(
								imageLoc.x + screenLoc.x,
								imageLoc.y + screenLoc.y,
								scaledSize.width,
								scaledSize.height);
							magnifierImg = TinyLookAndFeel.ROBOT.createScreenCapture(screenRect);

							magnifierPanel.setPaintData(scaledSize);
						}
					}
				};
				
				java.security.AccessController.doPrivileged(
					new java.security.PrivilegedAction() {
						public Object run() {
							// Note: Receiving mouse events on pressed/release
							// makes no sense, because the GUI is not updated at
							// the time the event is received
							Toolkit.getDefaultToolkit().addAWTEventListener(
								l, AWTEvent.MOUSE_MOTION_EVENT_MASK);
							
							return null;
						}
					}
				);
			}
		}
	}
	
	private class SizedIcon implements Icon {
		
		Dimension size;
		
		SizedIcon(Dimension size) {
			this.size = size;
		}
		
		SizedIcon(int width, int height) {
			size = new Dimension(width, height);
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.DARK_GRAY);
			g.drawRect(x, y, size.width - 1, size.height - 1);
			
			g.setColor(Color.ORANGE);
			g.fillRect(x + 1, y + 1, size.width - 2, size.height - 2);
		}

		public int getIconWidth() {
			return size.width;
		}

		public int getIconHeight() {
			return size.height;
		}
	}
	
	private static class ColorIcon implements Icon {

		private Dimension iconSize;
		private Color color;
		private static int hue = 0;
		
		public ColorIcon(Dimension iconSize) {
			this.iconSize = iconSize;
			color = Color.getHSBColor((float)(hue / 360.0), 0.5f, 0.9f);
			hue += 360 / 15;
		}
		
		public int getIconHeight() {
			return iconSize.height;
		}

		public int getIconWidth() {
			return iconSize.width;
		}
			
		public void paintIcon(Component comp, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
			
			g.setColor(Color.BLACK);
			g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
		}
	}
}
