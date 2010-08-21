package net.sourceforge.squirrel_sql.client.update.gui;

/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction.INSTALL;
import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction.NONE;
import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction.REMOVE;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.sourceforge.squirrel_sql.fw.gui.SortableTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * Implements the table summary of updates which includes artifacts in each of
 * the core, plugins and translations modules.
 * 
 * @author manningr
 */
public class UpdateSummaryTable extends SortableTable {

	private static final long serialVersionUID = 1L;

   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr = 
   	StringManagerFactory.getStringManager(UpdateSummaryTable.class);
   
   private interface i18n {
   	//i18n[UpdateSummaryTable.allTranslationsLabel=All translations]
   	String ALL_TRANSLATIONS_LABEL = s_stringMgr.getString("UpdateSummaryTable.allTranslationsLabel");
   	
   	//i18n[UpdateSummaryTable.allPluginsLabel=All plugins]
   	String ALL_PLUGINS_LABEL = s_stringMgr.getString("UpdateSummaryTable.allPluginsLabel");
   	
   	//i18n[UpdateSummaryTable.installOptionsLabel=Install Options]
   	String INSTALL_OPTIONS_LABEL = s_stringMgr.getString("UpdateSummaryTable.installOptionsLabel");
   }

   private List<ArtifactStatus> _artifacts = null;
   private boolean _releaseVersionWillChange = false;
   private UpdateSummaryTableModel _model = null;
   
   public UpdateSummaryTable(List<ArtifactStatus> artifactStatus, 
                             UpdateSummaryTableModel model) {
      super(model);
      _model = model;
      _artifacts = artifactStatus;
      setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      getTableHeader().setResizingAllowed(true);
      getTableHeader().setReorderingAllowed(true);
      setAutoCreateColumnsFromModel(false);
      setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);

      final TableColumnModel tcm = new DefaultTableColumnModel();
      JComboBox _actionComboBox = new JComboBox();
		for (int i = 0; i < model.getColumnCount(); ++i) {
         final TableColumn col = new TableColumn(i, model.getColumnWidth(i));
         col.setHeaderValue(model.getColumnName(i));
         if (i == 3) {
            col.setCellEditor(new DefaultCellEditor(initCbo(_actionComboBox)));
         }
         tcm.addColumn(col);
      }
      setColumnModel(tcm);
      initPopup();
   }

   /**
    * Gets the list of changes requested by the user.
    */
   public List<ArtifactStatus> getUserRequestedChanges() {
      List<ArtifactStatus> changes = new ArrayList<ArtifactStatus>();
      for (ArtifactStatus artifactStatus : _artifacts) {
         if (artifactStatus.getArtifactAction() != ArtifactAction.NONE) {
            changes.add(artifactStatus);
         }
      }
      return changes;
   }

	/**
	 * This will adjust the list of artifacts presented to the user based on whether or not the release version
	 * will change.
	 * 
	 * @param releaseVersionWillChange
	 *           a boolean value indicating whether or not the release version will change - that is, whether
	 *           or not new core artifacts will be downloaded.
	 */
	public void setReleaseVersionWillChange(boolean releaseVersionWillChange)
	{
		Iterator<ArtifactStatus> i = _artifacts.iterator();
		_releaseVersionWillChange = releaseVersionWillChange;
		if (releaseVersionWillChange) {
			// All currently installed artifacts will be marked with INSTALL action. 
			while (i.hasNext()) {
				ArtifactStatus status = i.next();
				if (status.isInstalled()) {
					status.setArtifactAction(ArtifactAction.INSTALL);
				}
			}
			
		} else {
			// Remove the core items since they are the most recent, and the user is not allowed to remove them
			while (i.hasNext()) {
				ArtifactStatus status = i.next();
				if (status.isCoreArtifact()) {
					i.remove();
				}
			}
			
		}
	}
	
	/**
	 * @return a boolean value indicating whether or not the release version will change with this update.
	 */
	public boolean getReleaseVersionWillChange() {
		return _releaseVersionWillChange;
	}
   
   
   private void initPopup() {
      final JPopupMenu popup = new JPopupMenu(i18n.INSTALL_OPTIONS_LABEL);
      
      JMenuItem pluginItem = new JMenuItem(i18n.ALL_PLUGINS_LABEL);
      pluginItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               if (status.isPluginArtifact()) {
                  status.setArtifactAction(ArtifactAction.INSTALL);
               }
            }
            _model.fireTableDataChanged();
         }
      });
      JMenuItem translationItem = new JMenuItem(i18n.ALL_TRANSLATIONS_LABEL);
      translationItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            for (ArtifactStatus status : _artifacts) {
               if (status.isTranslationArtifact()) {
                  status.setArtifactAction(ArtifactAction.INSTALL);
               }
            }
            _model.fireTableDataChanged();
         }
      });
                  
      popup.add(pluginItem);
      popup.add(translationItem);
      
      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent event){
          if(popup.isPopupTrigger(event)){
           popup.show(event.getComponent(), event.getX(),event.getY());
          }
         }
         public void mouseReleased(MouseEvent event){
          if(popup.isPopupTrigger(event)){
           popup.show(event.getComponent(), event.getX(),event.getY());
          }
         }
        });            
   }
      
   private JComboBox initCbo(final JComboBox cbo) {
      cbo.setEditable(false);
      setModel(cbo, NONE, INSTALL, REMOVE);
      
      cbo.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {}
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e){}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
				JComboBox source =(JComboBox) e.getSource();
				updateDataModel(source);
			}
      });
      
      return cbo;
   }
   
   /**
    *  We want to adjust the items in the popup menu that are available to the user to select
    *  based on 1) whether or not the release version will change, and 2) what type of artifact the row 
    *  is dealing with and 3) whether or not the artifact is already installed   
    * @param e
    * @param source
    */
   private void updateDataModel(JComboBox source) {
		final int row = UpdateSummaryTable.this.getEditingRow();
		if (row == -1) {
			return;
		}
		final ArtifactStatus as = UpdateSummaryTable.this._artifacts.get(row);
		
		// is it installed?
		boolean installed = as.isInstalled();

		// get the type of artifact
		if (as.isCoreArtifact()) {
			if (_releaseVersionWillChange) {
				source.setModel(getComboBoxModel(INSTALL));
			} else {
				// core artifacts are not displayed
			}
		} else {
			if (_releaseVersionWillChange) {
				if (installed) {
					setModel(source, INSTALL, REMOVE);
				} else {
					setModel(source, NONE, INSTALL);
				}
			} else {
				if (installed) {
					setModel(source, NONE, REMOVE);
				} else {
					setModel(source, NONE, INSTALL);
				}
			}
		}   
   }
   
   private void setModel(JComboBox box, ArtifactAction... actions) {
   	ComboBoxModel oldModel = box.getModel();
   	box.setModel(getComboBoxModel(actions));
   	if (oldModel.getSize() != actions.length) {
   		box.firePropertyChange("itemCount", oldModel.getSize(), actions.length);
   	}
   }
   
   private ComboBoxModel getComboBoxModel(ArtifactAction... actions) {
   	ComboBoxModel result = new DefaultComboBoxModel(actions);
   	result.setSelectedItem(actions[0]);
   	return result;
   }

}