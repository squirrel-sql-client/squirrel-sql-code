package com.digitprop.tonicdemo;


import java.awt.*;
import java.awt.event.*; 

import java.net.*;

import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import com.digitprop.tonic.*;



/**	Demo for the Tonic Look and Feel. Displays a frame with a mock-up 
 * 	user interface, showcasing the Swing components with the Tonic
 * 	look.
 * 
 * 	@version 1.0.0
 * 
 * 	@author	Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class TonicDemo extends JFrame implements ActionListener
{
	
	private static final String	MENU_OPEN="Open";
	
	private static final String	MENU_EXIT="Exit";
	
	private static final String	MENU_JEDITORPANE="JEditorPane";
	
	private static final String	MENU_JINTERNALFRAME="JInternalFrames";
	
	private static final String	MENU_JOPTIONPANE="JOptionPane";
	
	private static final String	MENU_DIALOG="Demo dialog";
	
	/** These menu items will have an instance of this class attached 
	 *  as an ActionListener */
	private static final String[]	LISTENER_ITEMS=
		{ 
			MENU_OPEN, MENU_EXIT, MENU_JEDITORPANE, MENU_JINTERNALFRAME,
			MENU_JOPTIONPANE, MENU_DIALOG 
		};
	
	/**	Toolbar with selected items from the menu */
	private JToolBar toolBar;
	
	/**	Two split panes next to each other, making up the main panel */
	private JSplitPane left, right, sp;
	
	/**	Editor pane */
	private JEditorPane jep;
	
	/**	Central panel, containing the split panes */
	private JComponent mainPanelContent;
	
	/**	Example table */
	private JTable table;
	
	/**	The main panel */
	private JPanel mainPanel;
	
	/**	Used to display JInternalFrames */
	private JDesktopPane desktopPane;
	
	
	/**	Creates an instance with the specified frame title */	
	public TonicDemo(String title)
	{
		super(title);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initGUI();
		initMenu();
		
		int w=700;
		int h=500;

		Dimension sd=Toolkit.getDefaultToolkit().getScreenSize();		
		setBounds(sd.width/2-w/2, sd.height/2-h/2, w, h);
		setVisible(true);
		
		validate();
		left.setDividerLocation(0.68);
		right.setDividerLocation(0.75);
		sp.setDividerLocation(0.29);
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		
		// Get default frame icon from UIManager and convert it to an image
		Icon icon2=UIManager.getIcon("InternalFrame.icon");
		Image img=createImage(icon2.getIconWidth(), icon2.getIconHeight());
		Graphics g=img.getGraphics();
		icon2.paintIcon(this, g, 0, 0);
		g.dispose();
		
		setIconImage(img);
	} 
	
	
	/**	Initializes the menu and adds it to the frame */
	private void initMenu()
	{
		String m[][]=
			{
				// Item name, parent name, icon, accelerator
				{ "File", "Root", null, null },
				{ "-New", "File", null, null },
				{ "Open", "File", "open.gif", null },
				{ "-Save", "File", "save.gif", null },
				{ "-Save as...", "File", "saveas.gif", null },
				{ "sep", "File",  },
				{ "-Print", "File", "printer.gif", null },
				{ "sep", "File",  },
				{ "Exit", "File", null, null },

				{ "Edit", "Root", null, null },
				{ "-Undo", "Edit", "undo.gif", null },
				{ "-Redo", "Edit", "redo.gif", null },
				{ "sep", "Edit",  },
				{ "-Cut", "Edit", "cut.gif", "CTRL+x" },
				{ "-Copy", "Edit", "copy.gif", "CTRL+c" },
				{ "-Paste", "Edit", "paste.gif", "CTRL+v" },
				{ "-Delete", "Edit", null, null },
				{ "sep", "Edit"  },
				{ "Demo dialog", "Edit", null, null },

				{ "View", "Root", null, null },
				{ "JEditorPane", "View", "standardview.gif", null },
				{ "JInternalFrames", "View", "layout.gif", null },
				{ "sep", "View",  },
				{ "-Rulers", "View", null, null },
				{ "-Document structure", "View", null, null },
				{ "sep", "View",  },
				{ "Zoom", "View", "zoom.gif", null },
				{ "Standard", "Zoom", null, null },
				{ "Page width", "Zoom", "pagewidth.gif", null },
				{ "Whole page", "Zoom", "standardview.gif", null },
				{ "25%", "Zoom", null, null },
				{ "50%", "Zoom", null, null },
				{ "75%", "Zoom", null, null },
				{ "100%", "Zoom", null, null },
				{ "200%", "Zoom", null, null },
				
				{ "Project", "Root", null, null },
				{ "JOptionPane", "Project", null, null },
				
				{ "Help", "Root", null, null },
				{ "-Info", "Help", null, null },
				{ "-About", "Help", null, null }			
			};
		

		JMenuBar 	mb=new JMenuBar();
		Hashtable	itemTable=new Hashtable();
		Hashtable 	createdTable=new Hashtable();
		
		// Create hierarchy
		for(int i=0; i<m.length; i++)
		{
			String line[]=m[i];
			
			String parent=line[1];
			Vector v=(Vector)itemTable.get(parent);
			if(v==null)
			{
				v=new Vector();
				itemTable.put(parent, v);
			}
			
			v.add(line);
		}
		
		// Create menu		
		JMenu menu=null;
		Enumeration keys=itemTable.keys();
		while(itemTable.size()>0)
		{
			String key=(String)keys.nextElement();
			processKey(key, itemTable, createdTable, mb);
		}		
			
		setJMenuBar(mb);
	}
	
	
	/**	Helper method for initMenu(): Creates a JMenuItem or JMenu from the
	 * 	specified key, and adds any child menu items if necessary.
	 * 
	 * 	@param	key				The key of the item to be created
	 * 	@param	itemTable		Contains Vectors with child menu item
	 * 									definitions, indexed by the name of the
	 * 									parent item
	 * 	@param	createdTable	Contains all menu items created so far
	 * 	@param	mb					The menu bar which will contain all created
	 * 									menus
	 * 
	 * 	@return						A JMenuItem which has been created from the 	
	 * 									specified key and itemTable. This can be a
	 * 									JMenu with its own children
	 * 
	 * 	@see #initMenu() 
	 */
	private JMenuItem processKey(String key, Hashtable itemTable, Hashtable createdTable, JMenuBar mb)
	{
		Vector children=(Vector)itemTable.get(key);
		itemTable.remove(key);
		
		boolean isRoot=key.equals("Root");
		JMenu result=null;
		if(!isRoot)
		{
			result=new JMenu(key);
			createdTable.put(key, result);
		}

		for(int i=0; i<children.size(); i++)
		{
			String def[]=(String [])children.get(i);
			
			JMenuItem child=null;
			if(def[0].equals("sep"))
			{
				if(!isRoot)
					result.add(new JSeparator());
			}
			else
			{
				if(itemTable.get(def[0])!=null)
					child=processKey(def[0], itemTable, createdTable, mb);
				else if(createdTable.get(def[0])!=null)
				{
					child=(JMenuItem)createdTable.get(def[0]);
				}
				else
				{
					boolean enabled=!(def[0].startsWith("-"));
					if(!enabled)
						def[0]=def[0].substring(1);
						
					child=new JMenuItem(def[0]);
					if(def[2]!=null)
						child.setIcon(getIcon(def[2]));
					if(def[3]!=null)
						child.setAccelerator(getAccelerator(def[3]));
						
					child.setEnabled(enabled);
					
					for(int j=0; j<LISTENER_ITEMS.length; j++)	
						if(LISTENER_ITEMS[j].equals(def[0]))
						{
							child.addActionListener(this);
							break;
						}
				}
				
				if(isRoot)
					mb.add((JMenu)child);
				else
					result.add(child);
			}
		}
		
		return result;
	}
					
	
	/**	Creates and returns the toolbar. Please note that in this demo,
	 * 	the tool bar does not have any functionality. Also, in a real-world
	 * 	application, the toolbar items would be based on Actions bound
	 * 	to both the toolbar button and the corresponding menu item.
	 */
	public JToolBar initToolBar()
	{
		String b[]=
			{
				"open.gif",
				"save.gif",
				"saveas.gif",
				"sep",
				"cut.gif",
				"copy.gif",
				"paste.gif",
				"sep",
				"zoom.gif",
				"combo",
				"sep",
				"undo.gif",
				"redo.gif",
				"sep",
				"help.gif"
			};
			
			
		JToolBar tb=new JToolBar("Demo toolbar", JToolBar.HORIZONTAL);
		
		for(int i=0; i<b.length; i++)
		{
			if(b[i].equals("sep"))
			{
				JSeparator sep=new JSeparator(SwingConstants.VERTICAL);
				tb.add(sep);
			}
			else if(b[i].equals("combo"))
			{
				String content[]=
					{
						"200%",
						"125%",
						"100%",
						"75%",
						"50%",
						"25%",
						"10%",
						"Whole page",
						"Page width",
					};
					
				JComboBox box=new JComboBox(content);
				tb.add(box);
			}
			else
			{
				Icon icon=getIcon(b[i]);
				
				if(i<3)
					tb.add(new JButton(icon));
				else
					tb.add(new ToolButton(icon));
			}	
		}
			
		return tb;
	}
	

	/**	Returns an ImageIcon for the specified icon name */	
	private ImageIcon getIcon(String name)
	{
		return TonicLookAndFeel.getTonicIcon(name);
	}
	

	/**	Retrieves and returns an ImageIcon with the specified 
	 * 	file name. This method searches in the resource path
	 * 	of the specified base class.
	 * 
	 * 	@param	baseClass			The base class which defines the
	 * 										resource path from which the icon
	 * 										is retrieved
	 * 	@param	name					File name of the icon
	 * 
	 * 	@return							The retrieved icon, or null, if the
	 * 										retrieval failed
	 */	
	private ImageIcon getIcon(Class baseClass, String name)
	{
		try
		{
			URL url=baseClass.getResource(name);
			ImageIcon result=new ImageIcon(url);
			return result;
		}
		catch(Exception e)
		{
			System.out.println("Could not find icon \""+name+"\"!");
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**	Parses the specified String and returns a KeyStroke from it. The
	 * 	KeyStroke is composed of the last character of the String, plus
	 * 	a modifier, if the String starts with "CTRL+", "ALT+", or "SHIFT+".
	 */
	private KeyStroke getAccelerator(String acc)
	{
		int modifiers=0;
		
		if(acc.startsWith("CTRL+"))
			modifiers|=KeyEvent.CTRL_MASK;
		if(acc.startsWith("ALT+"))
			modifiers|=KeyEvent.CTRL_MASK;
		if(acc.startsWith("SHIFT+"))
			modifiers|=KeyEvent.SHIFT_MASK;
			
		return KeyStroke.getKeyStroke(acc.charAt(acc.length()-1), modifiers);
	}
	
	
	/**	Returns a JPanel with a JTree mockup */
	private JPanel createDataStructurePanel()
	{
		String treeDef[]=
			{
				"Root", "Data",
				"Data", "Statistics",
				"Data", "Profiling",
				"Data", "Analysis",
				"Data", "Images",
				"Statistics", "Time",
				"Statistics", "Volume",
				"Time", "Average",
				"Time", "Cumulated",
				"Time", "Histogram",
				"Volume", "Average",
				"Volume", "Cumulated",
				"Volume", "Histogram",
				"Analysis", "Deconvolution",
				"Analysis", "Noise reduction",
				"Analysis", "Signal enhancement",
				"Analysis", "FFT",
				"Profiling", "Average",
				"Profiling", "Weakest of chain",
				"Profiling", "Trace",
				"Images", "Hyperbolic map",
				"Images", "Gauss",
				"Images", "Histograms"
			};
			
		JPanel result=new JPanel(new BorderLayout());
		result.setBackground(Color.WHITE);
		result.setBorder(new SplitPaneContentBorder(true));
	
		JPanel titlePanel=new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		titlePanel.add(new JLabel("Data structure"));
		titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Button.borderColor")));
	
		result.add(BorderLayout.NORTH, titlePanel);
		JPanel subPanel=new JPanel(new BorderLayout());
		subPanel.setBackground(Color.WHITE);
		result.add(BorderLayout.CENTER, subPanel);
		
		TreeNode tn=getTree(treeDef);
		JTree tree=new JTree(tn);
		tree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		JScrollPane sp=new JScrollPane(tree);
		sp.setBorder(null);
		subPanel.add(BorderLayout.CENTER, sp);
		
		tree.expandRow(1);
		tree.expandRow(2);
		tree.expandRow(7);
		tree.expandRow(11);
		
		return result;
	}
	
	
	/**	Returns another JPanel with a tree mockup */
	private JPanel createOutlinePanel()
	{
		String treeDef[]=
			{
				"Root", "Assets",
				"Assets", "com",
				"com", "digitprop",
				"digitprop", "lf",
				"lf", "DPLookAndFeel",
				"lf", "ScrollBarUI",
				"lf", "OptionPaneUI",
				"lf", "MenuBarUI",
				"lf", "ButtonUI",
				"Assets", "resources",
				"resources", "images",
				"images", "Overview",
				"images", "Icon_01",
				"images", "Icon_02"
			};
			
		JPanel result=new JPanel(new BorderLayout());
		result.setBackground(Color.WHITE);
		result.setBorder(new SplitPaneContentBorder(true));
	
		JPanel titlePanel=new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		titlePanel.add(new JLabel("Outline"));
		titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Button.borderColor")));
	
		result.add(BorderLayout.NORTH, titlePanel);
		JPanel subPanel=new JPanel(new BorderLayout());
		subPanel.setBackground(Color.WHITE);
		result.add(BorderLayout.CENTER, subPanel);
		
		TreeNode tn=getTree(treeDef);
		JTree tree=new JTree(tn);
		tree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		JScrollPane sp=new JScrollPane(tree);
		sp.setBorder(null);
		subPanel.add(BorderLayout.CENTER, sp);
		
		tree.expandRow(1);
		tree.expandRow(2);
		tree.expandRow(3);
		
		return result;
	}
	
	
	/**	Returns a JDesktopPane with two JInternalFrames */
	private JDesktopPane createDeskopPanel()
	{
		JDesktopPane dp=new JDesktopPane();
		
		JInternalFrame inf=new JInternalFrame("Auxiliary data");
		
		inf.setClosable(true);
		inf.setMaximizable(true);
		inf.setIconifiable(true);
		inf.setResizable(true);
			
		ImageIcon ii=(ImageIcon)getIcon(getClass(), "resources/img1.jpg");
		inf.setContentPane(new ImageScroller(ii));
		//inf.pack();
		inf.setBounds(100, 80, 300, 200);
		inf.setVisible(true);
			
		JInternalFrame inf2=new JInternalFrame("Profiling results");
			
		inf2.setClosable(true);
		inf2.setMaximizable(true);
		inf2.setIconifiable(true);
		inf2.setResizable(true);
			
		ii=(ImageIcon)getIcon(getClass(), "resources/img2.jpg");
		inf2.setContentPane(new ImageScroller(ii));
		inf2.setBounds(10, 10, 180, 300);
		inf2.setVisible(true);
									
		dp.add(inf);			
		dp.add(inf2);
		
		return dp;
	}
	

	/**	Creates the application's main panel and returns it */	
	private JPanel createMainPanel()
	{		
		JPanel topleft=new JPanel(new BorderLayout());
		topleft.setBackground(Color.WHITE);
		topleft.setBorder(new SplitPaneContentBorder(true));
	
		JPanel titlePanel=new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
		titlePanel.add(new JLabel("Editor - \\assets\\outline.rtf"));
		titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Button.borderColor")));
	
		topleft.add(BorderLayout.NORTH, titlePanel);
		JPanel subPanel=new JPanel(new BorderLayout());
		subPanel.setBackground(Color.WHITE);
		mainPanel=subPanel;
		topleft.add(BorderLayout.CENTER, subPanel);
		try
		{
			URL url=getClass().getResource("resources/demo.rtf");
			jep=new JEditorPane(url);
			
			JScrollPane sp2=new JScrollPane(jep);
			sp2.setBorder(null);
			
			subPanel.add(BorderLayout.CENTER, sp2);
			mainPanelContent=sp2;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return topleft;
	}
			
	
	/**	Creates a tree of TreeNodes from the specified String array.
	 * 	The array is expected to contain String pairs, consisting of
	 * 	the parent node name, and a child node name.
	 */
	private TreeNode getTree(String tree[])
	{		
		Hashtable table=new Hashtable();
		
		TreeNode root=null;
		
		for(int i=0; i<tree.length; i+=2)
		{
			if(tree[i].equals("Root"))
			{
				root=new DefaultMutableTreeNode(tree[i+1]);
				table.put(tree[i+1], root);
			}
			else
			{
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(tree[i+1]);
				DefaultMutableTreeNode parent=(DefaultMutableTreeNode)table.get(tree[i]);
				if(parent!=null)
					parent.add(node);
					
				table.put(tree[i+1], node);
			}
		}
		
		return root;
	}
	

	/**	Returns a JPanel with a JTable */	
	private JPanel createTasksPanel()
	{
		JPanel result=new JPanel(new BorderLayout());
		result.setBackground(Color.WHITE);
		result.setBorder(new SplitPaneContentBorder(false));
		
		table=new JTable(new TonicDemoTableModel());
		JScrollPane sp2=new JScrollPane(table);
		sp2.setBorder(null);
		result.add(BorderLayout.CENTER, sp2);
		
		// Set column widths for the table
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(270);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setPreferredWidth(120);
		
		return result;		
	}
	
	
	/**	Handles action events. This is not very object-oriented, but
	 * 	more than sufficient for this demo. Reacts on some of the
	 * 	menu items.
	 */	
	public void actionPerformed(ActionEvent e)
	{
		if(((JMenuItem)e.getSource()).getText().equals(MENU_JEDITORPANE))
		{
			// Display a JEditorPane at the top right panel
			mainPanel.removeAll();
			mainPanel.add(BorderLayout.CENTER, mainPanelContent);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(MENU_JINTERNALFRAME))
		{
			// Display a JDesktopPane with JInternalFrames at the top right panel
			mainPanel.removeAll();
			mainPanel.add(BorderLayout.CENTER, desktopPane);						
		}
		else if(((JMenuItem)e.getSource()).getText().equals(MENU_OPEN))
		{
			// Display a file chooser
			JFileChooser fc=new JFileChooser();
			Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
			fc.setLocation(screen.width/2-fc.getWidth()/2, screen.height/2-fc.getHeight()/2);
			fc.showOpenDialog(this);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(MENU_JOPTIONPANE))
		{
			// Displays a JOptionPane
			int result=JOptionPane.showConfirmDialog(this, "Have you seen my glasses?", "Duh...!", JOptionPane.YES_NO_CANCEL_OPTION);
			
			if(result==JOptionPane.YES_OPTION)
				JOptionPane.showMessageDialog(this, "Yes, but I won't tell you where!", "Muahahaha...", JOptionPane.INFORMATION_MESSAGE);
			else if(result==JOptionPane.NO_OPTION)
				JOptionPane.showMessageDialog(this, "No, your glasses were nowhere to be found.", "Nope...", JOptionPane.ERROR_MESSAGE);
		}
		else if(((JMenuItem)e.getSource()).getText().equals(MENU_DIALOG))
		{
			// Displays the demo dialog 
			TonicDemoDialog td=new TonicDemoDialog(this, "Preferences");
			int x=getX()+getWidth()/2-td.getWidth()/2;
			int y=getY()+getHeight()/2-td.getHeight()/2;
			
			td.setLocation(x, y);
			td.show();
		}
		else if(((JMenuItem)e.getSource()).getText().equals(MENU_EXIT))
		{
			// Exits the application
			setVisible(false);
			dispose();
			System.exit(0);
		}
		
		repaint();
	}


	/**	Initializes the GUI, including the central panels of the
	 * 	application.
	 */
	private void initGUI()
	{	
		this.toolBar=initToolBar();
		JToolBar tb=toolBar;
	
		JPanel topleft=createDataStructurePanel();
			
		JPanel bottomleft=createOutlinePanel();

		JPanel topright=createMainPanel();

		JPanel bottomright=createTasksPanel();
				
		desktopPane=createDeskopPanel();
		
		left=new JSplitPane(JSplitPane.VERTICAL_SPLIT, topleft, bottomleft);
		left.setOneTouchExpandable(true);
		left.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		right=new JSplitPane(JSplitPane.VERTICAL_SPLIT, topright, bottomright);
		right.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		sp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		
		getContentPane().add(BorderLayout.NORTH, tb);
		getContentPane().add(BorderLayout.CENTER, sp);				
	}
	
		
	/**	Main method: Sets the Tonic look and feel, then creates an instance
	 * 	of this class and shows it.
	 */
	public static void main(String args[])
	{
		if(args.length==0 || !args[0].startsWith("m"))
		{
			try
			{
				UIManager.setLookAndFeel(new TonicLookAndFeel());
			}
			catch(UnsupportedLookAndFeelException e)
			{
				e.printStackTrace();
			}
		}
								
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		
		TonicDemo instance=new TonicDemo("Tonic Look And Feel v1.0");
		instance.setIconImage(TonicLookAndFeel.getTonicIcon("open").getImage());
	}
		

	/**	Used to display an image within a scroll pane - taken from the
	 * 	SwingSet demo.
	 */		
	class ImageScroller extends JScrollPane 
	{
		public ImageScroller(Icon icon) 
		{
			super();
			JPanel p = new JPanel();
			p.setBackground(Color.white);
			setBorder(null);
			p.setLayout(new BorderLayout() );
	    
			p.add(new JLabel(icon), BorderLayout.CENTER);
	    
			getViewport().add(p);
			getHorizontalScrollBar().setUnitIncrement(10);
			getVerticalScrollBar().setUnitIncrement(10);
		}
	
  		
  		public Dimension getMinimumSize() 
  		{
			return new Dimension(25, 25);
  		}
  	}	
}
