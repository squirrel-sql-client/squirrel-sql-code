package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This is the panel for the Object Tree tab.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreePanel extends JPanel {
	/** Current session. */
	private ISession _session;

	/** Tree of objects within the database. */
	private ObjectTree _tree;

	/** Split pane between the object tree and the data panel. */
	private final JSplitPane _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	/**
	 * Empty data panel. Used if the object selected in the object
	 * tree doesn't require a panel.
	 */
	private final JPanel _emptyPnl = new JPanel();

	/**
	 * ctor specifying the current session.
	 * 
	 * @param	session	Current session.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public ObjectTreePanel(ISession session) {
		super();
		if (session == null) {
			throw new IllegalArgumentException("ISession == null");
		}
		_session = session;
		createUserInterface();
	}

	/**
	 * Register an expander for the specified database object type in the
	 * object tree.
	 * 
	 * @param	dbObjectType	Database object type.
	 *							@see net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectTypes
	 * @param	expander		Expander called to add children to a parent node.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>INodeExpander</TT> thrown.
	 */
	public void registerExpander(int dbObjectType, INodeExpander expander)
	{
		if (expander == null)
		{
			throw new IllegalArgumentException("Null INodeExpander passed");
		}
	}

	/**
	 * Set the panel to be shown in the data area for the passed
	 * path.
	 * 
	 * @param	path	path of node currently selected.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT>TreePath</TT> passed.
	 */
	private void setSelectedObjectPanel(TreePath path) {
		if (path == null) {
			throw new IllegalArgumentException("TreePath == null");
		}

		JComponent comp = _emptyPnl;
		if (path != null) {
//			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
//			if (node instanceof BaseNode) {
//				comp = ((BaseNode)node).getDetailsPanel();
//			}
		}
		setSelectedObjectPanel(comp);
	}

	/**
	 * Set the panel in the data area to that passed.
	 * 
	 * @param	comp	Component to be displayed.
	 * 
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT>Component</TT> passed.
	 */
	private void setSelectedObjectPanel(Component comp) {
		if (comp == null) {
			throw new IllegalArgumentException("Component == null");
		}

		int divLoc = _splitPane.getDividerLocation();
		Component existing = _splitPane.getRightComponent();
		if (existing != null) {
			_splitPane.remove(existing);
		}
		_splitPane.add(comp, JSplitPane.RIGHT);
		_splitPane.setDividerLocation(divLoc);
	}

	/**
	 * Create the user interface.
	 */
	private void createUserInterface() {
		setLayout(new BorderLayout());

		_tree = new ObjectTree(_session);

		_splitPane.setOneTouchExpandable(true);
		_splitPane.setContinuousLayout(true);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(_tree);
		sp.setPreferredSize(new Dimension(200, 200));
		_splitPane.add(sp, JSplitPane.LEFT);
		add(_splitPane, BorderLayout.CENTER);

		setSelectedObjectPanel(_emptyPnl);

//		_tree.addTreeSelectionListener(new MySelectionListener());

		_splitPane.setDividerLocation(200);
	}
}

