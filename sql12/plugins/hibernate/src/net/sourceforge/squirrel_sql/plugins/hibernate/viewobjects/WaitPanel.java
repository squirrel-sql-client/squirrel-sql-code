package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jidesoft.swing.MultilineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePluginResources;

public class WaitPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(WaitPanel.class);
   private MultilineLabel _label;
   private boolean _isDisplayingError;
   private JButton _btnClose;
   private WaitPanelListener _waitPanelListener;

   public WaitPanel(String hqlQuery, HibernatePluginResources resource, WaitPanelListener waitPanelListener)
   {
      this._waitPanelListener = waitPanelListener;
      setLayout(new BorderLayout());

      add(createCloseButtonPanel(resource), BorderLayout.NORTH);

      add(createLabelPanel(hqlQuery), BorderLayout.CENTER);

      setMinimumSize(new Dimension(0,0));
   }

   private JPanel createLabelPanel(String hqlQuery)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 10), 0, 0);

      ret.add(new JLabel(s_stringMgr.getString("WaitPanel.hqlLabel")), gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 10, 5, 10), 0, 0);
      _label = new MultilineLabel(hqlQuery);
      ret.add(_label, gbc);

      return ret;
   }

   private JPanel createCloseButtonPanel(HibernatePluginResources resource)
   {
      JPanel ret = new JPanel(new BorderLayout());

      ret.add(new JPanel(), BorderLayout.CENTER);

      _btnClose = new JButton(resource.getIcon(HibernatePluginResources.IKeys.CLOSE_IMAGE));

      _btnClose.setMargin(new Insets(0, 0, 0, 0));
      _btnClose.setBorderPainted(false);

      _btnClose.setVisible(false);
      ret.add(_btnClose, BorderLayout.EAST);

      _btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _waitPanelListener.removeWaitPanel(WaitPanel.this);
         }
      });

      return ret;
   }

   public String getTitle()
   {
      return s_stringMgr.getString("WaitPanel.title");
   }

   public boolean isDisplayingError()
   {
      return _isDisplayingError;
   }

   public void displayError(Throwable t)
   {
      prepareForErrorDisplay();
      _label.setText(s_stringMgr.getString("WaitPanel.errorOccured", Utilities.getExceptionStringSave(t)));
   }

   public void displayHqlQueryError(String errMsg)
   {
      prepareForErrorDisplay();
      _label.setText(s_stringMgr.getString("WaitPanel.hqlErrorOccured", errMsg));
   }

   private void prepareForErrorDisplay()
   {
      _label.setBackground(Color.white);
      _label.setForeground(Color.red);
      _label.setFont(_label.getFont().deriveFont(Font.BOLD));
      _btnClose.setVisible(true);
      _isDisplayingError = true;
   }
}
