package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.awt.LayoutManager;
import java.sql.PreparedStatement;

import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.session.ISession;

abstract public class BaseSourcePanel extends JPanel {

    public BaseSourcePanel(LayoutManager manager) {
        super(manager);
    }
    
    public abstract void load(ISession session, PreparedStatement stmt);
}
