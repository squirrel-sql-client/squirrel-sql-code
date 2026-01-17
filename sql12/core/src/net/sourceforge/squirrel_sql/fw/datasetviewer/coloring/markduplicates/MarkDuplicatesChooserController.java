package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.gui.buttonchooser.ButtonChooser;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MarkDuplicatesChooserController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MarkDuplicatesChooserController.class);

   private ButtonChooser _toggleBtnChooser;

   private static final String PREF_MARK_DUPLICATES_MODE_LAST_MODE = "MarkDuplicatesMode.last.mode";

   private MarkDuplicatesStateHandler _markDuplicatesStateHandler = null;

   private boolean _dontReactToEvents = false;


   public MarkDuplicatesChooserController(IDataSetViewer dataSetViewer)
   {
      this(dataSetViewer, null);
   }

   public MarkDuplicatesChooserController(IResultTab resultTab)
   {
      this(resultTab.getSQLResultDataSetViewer(), resultTab);
   }

   private MarkDuplicatesChooserController(IDataSetViewer dataSetViewer, IResultTab resultTab)
   {
      _toggleBtnChooser = new ButtonChooser();

      _markDuplicatesStateHandler = new MarkDuplicatesStateHandler(e -> actionWasFired(e),
                                                                 tableSortingAdmin -> onTableSorted(),
                                                                   () -> onColumnMoved());

      _markDuplicatesStateHandler.init(dataSetViewer, resultTab);

      for (MarkDuplicatesMode mode : MarkDuplicatesMode.values())
      {
         JToggleButton btn = new JToggleButton(_markDuplicatesStateHandler.getAction());
         btn.setIcon(mode.getIcon());
         btn.setText(mode.getText());
         btn.setToolTipText(mode.getToolTipText());
         mode.assignModeToButton(btn);
         _toggleBtnChooser.addButton(btn);
      }

      _toggleBtnChooser.setSelectedButton(getLastMode().findButton(_toggleBtnChooser));

      _toggleBtnChooser.setButtonSelectedListener((newSelectedButton, formerSelectedButton) -> onButtonSelected((JToggleButton)newSelectedButton, (JToggleButton)formerSelectedButton));
   }

   private void onColumnMoved()
   {
      AbstractButton selButton = _toggleBtnChooser.getSelectedButton();
      if(false == selButton.isSelected())
      {
         return;
      }

      MarkDuplicatesMode selectedMode = MarkDuplicatesMode.getModeByButton(selButton);
      if(   selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_CELLS_IN_ROW
         || selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_VALUES_IN_COLUMNS)
      {
         try
         {
            _dontReactToEvents = true;
            selButton.setSelected(false);
         }
         finally
         {
            _dontReactToEvents = false;
         }

         doMarkDuplicates();
      }
   }

   private void onTableSorted()
   {
      AbstractButton selButton = _toggleBtnChooser.getSelectedButton();
      if(false == selButton.isSelected())
      {
         return;
      }

      MarkDuplicatesMode selectedMode = MarkDuplicatesMode.getModeByButton(selButton);
      if( selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_VALUES_IN_COLUMNS || selectedMode == MarkDuplicatesMode.DUPLICATE_CONSECUTIVE_ROWS)
      {
         try
         {
            _dontReactToEvents = true;

            selButton.setSelected(false);
         }
         finally
         {
            _dontReactToEvents = false;
         }

         doMarkDuplicates();
      }
   }

   private void onButtonSelected(final JToggleButton newSelectedButton, JToggleButton formerSelectedButton)
   {
      if(_dontReactToEvents)
      {
         return;
      }

      Props.putString(PREF_MARK_DUPLICATES_MODE_LAST_MODE, MarkDuplicatesMode.getModeByButton(newSelectedButton).name());

      try
      {
         _dontReactToEvents = true;
         newSelectedButton.setSelected(formerSelectedButton.isSelected());

         // Needed because of the way GUIUtils.styleAsToolbarButton() works.
         newSelectedButton.setContentAreaFilled(newSelectedButton.isSelected());
      }
      finally
      {
         _dontReactToEvents = false;
      }

      doMarkDuplicates();
   }


   private MarkDuplicatesMode getLastMode()
   {
      return MarkDuplicatesMode.valueOf(Props.getString(PREF_MARK_DUPLICATES_MODE_LAST_MODE, MarkDuplicatesMode.DUPLICATE_VALUES_IN_COLUMNS.name()));
   }


   public JComponent getComponent()
   {
      return _toggleBtnChooser.getComponent();
   }

   public boolean actionWasFired(ActionEvent e)
   {
      if(_dontReactToEvents)
      {
         return true;
      }

      if(e.getSource() != _toggleBtnChooser.getSelectedButton())
      {
         // Happens through shortcut or menu.
         _toggleBtnChooser.getSelectedButton().doClick();
         return false;
      }

      doMarkDuplicates();
      return true;
   }

   private void doMarkDuplicates()
   {

      if(false == _markDuplicatesStateHandler.hasDatasetViewerTablePanel())
      {
         Main.getApplication().getMessageHandler().showWarningMessage(s_stringMgr.getString("MarkDuplicatesChooserController.mark.duplicates.for.table.output.only"));
         return;
      }

      boolean selected = _toggleBtnChooser.getSelectedButton().isSelected();

      MarkDuplicatesMode mode = null;

      if(selected)
      {
         mode = MarkDuplicatesMode.getModeByButton(_toggleBtnChooser.getSelectedButton());
      }

      _markDuplicatesStateHandler.markDuplicates(mode);
   }

   public void copyStateFrom(MarkDuplicatesChooserController controllerToCopyFrom)
   {
      try
      {
         _dontReactToEvents = true;

         for (AbstractButton button : _toggleBtnChooser.getAllButtons())
         {
            if(button.getIcon() == controllerToCopyFrom._toggleBtnChooser.getSelectedButton().getIcon())
            {
               _toggleBtnChooser.setSelectedButton(button);
               button.setSelected(controllerToCopyFrom._toggleBtnChooser.getSelectedButton().isSelected());
               break;
            }
         }

         // Needed because of the way GUIUtils.styleAsToolbarButton() works.
         _toggleBtnChooser.getSelectedButton().setContentAreaFilled(_toggleBtnChooser.getSelectedButton().isSelected());

      }
      finally
      {
         _dontReactToEvents = false;
      }
   }

   public void init(IResultTab resultTab)
   {
      init(resultTab.getSQLResultDataSetViewer(), resultTab);
   }
   public void init(IDataSetViewer dataSetViewer, IResultTab resultTab)
   {
      _markDuplicatesStateHandler.init(dataSetViewer, resultTab);
   }
}
