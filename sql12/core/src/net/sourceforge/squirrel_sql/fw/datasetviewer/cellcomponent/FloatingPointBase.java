package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import org.apache.commons.lang.StringUtils;

public abstract class FloatingPointBase extends BaseDataTypeComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(FloatingPointBase.class);

	// flag for whether we have already loaded the properties or not
	private static boolean propertiesAlreadyLoaded = false;


	// flag for whether to use the default Java format (true)
	// or the Locale-dependent format (false)
	protected static boolean useJavaDefaultFormat = false;
	
	
	/**
	 * How many Digits after the comma should be shown?
	 */
	protected static int maximumFractionDigits = 5;
	

	/**
	 * Generate a JPanel containing controls that allow the user
	 * to adjust the properties for this DataType.
	 * All properties are static accross all instances of this DataType.
	 * However, the class may choose to apply the information differentially,
	 * such as keeping a list (also entered by the user) of table/column names
	 * for which certain properties should be used.
	 * <P>
	 * This is called ONLY if there is at least one property entered into the DTProperties
	 * for this class.
	 * <P>
	 * Since this method is called by reflection on the Method object derived from this class,
	 * it does not need to be included in the Interface.
	 * It would be nice to include this in the Interface for consistancy, documentation, etc,
	 * but the Interface does not seem to like static methods.
	 */
	public static OkJPanel getControlPanel() {

	  /*
				* If you add this method to one of the standard DataTypes in the
				* fw/datasetviewer/cellcomponent directory, you must also add the name
				* of that DataType class to the list in CellComponentFactory, method
				* getControlPanels, variable named initialClassNameList.
				* If the class is being registered with the factory using registerDataType,
				* then you should not include the class name in the list (it will be found
				* automatically), but if the DataType is part of the case statement in the
				* factory method getDataTypeObject, then it does need to be explicitly listed
				* in the getControlPanels method also.
				*/

		// if this panel is called before any instances of the class have been
		// created, we need to load the properties from the DTProperties.
		loadProperties();

	  return new FloatingPointOkJPanel();
	}


	public FloatingPointBase()
	{
		loadProperties();
	}


	private static void loadProperties() {

		//set the property values
		// Note: this may have already been done by another instance of
		// this DataType created to handle a different column.
		if (propertiesAlreadyLoaded == false) {
			// get parameters previously set by user, or set default values
			useJavaDefaultFormat = false;	// set to use the Java default
			String useJavaDefaultFormatString = DTProperties.get(DataTypeBigDecimal.class.getName(), "useJavaDefaultFormat");

			if (useJavaDefaultFormatString != null && useJavaDefaultFormatString.equals("true"))
			{
				useJavaDefaultFormat =true;
			}
			
			maximumFractionDigits = 5; // by default use 5
			String maximumFractionDigitsString = DTProperties.get(DataTypeBigDecimal.class.getName(), "maximumFractionDigits");

			if (StringUtils.isEmpty(maximumFractionDigitsString) == false)
			{
				maximumFractionDigits =Integer.valueOf(maximumFractionDigitsString);
			}
		}
	}


	/**
	 * Inner class that extends OkJPanel so that we can call the ok()
	 * method to save the data when the user is happy with it.
	 */
	private static class FloatingPointOkJPanel extends OkJPanel
	{
        private static final long serialVersionUID = 3745853322636427759L;

        JRadioButton optUseDefaultFormat;
		JRadioButton optUseLocaleDependendFormat;
		IntegerField maximumFraction;
		
		public FloatingPointOkJPanel()
		{
			

			optUseDefaultFormat =
				new JRadioButton(s_stringMgr.getString("floatingPointBase.useDefaultFormat", new Double(3.14159).toString()));
			optUseLocaleDependendFormat =
				new JRadioButton(createTextForOptUseLocaleDependendFormat(maximumFractionDigits));
			
			maximumFraction = new IntegerField(2);
			
			setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("floatingPointBase.typeBigDecimal")));

			setLayout(new GridBagLayout());

			GridBagConstraints gbc;
			gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4),0,0);
			add(optUseLocaleDependendFormat, gbc);
			
			gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4),0,0);
			add(maximumFraction, gbc);
			
			gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4),0,0);
			add(optUseDefaultFormat, gbc);



			ButtonGroup bg = new ButtonGroup();
			bg.add(optUseDefaultFormat);
			bg.add(optUseLocaleDependendFormat);

			
			
			// Register listeners, for enabling/disabling the integer-field "maximumFraction"
			optUseLocaleDependendFormat.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(optUseLocaleDependendFormat.isSelected()){
						maximumFraction.setEditable(true);
						maximumFraction.setEnabled(true);
					}
				}
			});			
			
			optUseDefaultFormat.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if(optUseDefaultFormat.isSelected()){
						maximumFraction.setEditable(false);
						maximumFraction.setEnabled(false);
					}
				}
			});
			
			
			// Register a listener for updating the example text with the actual settings of maximumFractions
			maximumFraction.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent e) {
					optUseLocaleDependendFormat.setText(createTextForOptUseLocaleDependendFormat(maximumFraction.getInt()));
					maximumFraction.repaint();
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					// do nothing
				}
			});
			
			
			// set the initial value
			optUseLocaleDependendFormat.setSelected(!useJavaDefaultFormat);
			optUseDefaultFormat.setSelected(useJavaDefaultFormat);
			maximumFraction.setInt(maximumFractionDigits);
			
		}

		/**
		 * Creates the text for the {@link JRadioButton} of "use locale depended format" with respect of maxFractionDigits
		 * @param maxFractionDigits Number of digits after the comma to use in the example.  
		 */
		private String createTextForOptUseLocaleDependendFormat(int maxFractionDigits) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMaximumFractionDigits(maxFractionDigits);
			return s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat", numberFormat.format(new Double(3.14159)));
		}

		/**
		 * User has clicked OK in the surrounding JPanel,
		 * so save the current state of all variables
		 */
		public void ok()
		{
			// get the values from the controls and set them in the static properties
			useJavaDefaultFormat = optUseDefaultFormat.isSelected();
			DTProperties.put(DataTypeBigDecimal.class.getName(), 
			                 "useJavaDefaultFormat", 
			                 Boolean.valueOf(useJavaDefaultFormat).toString());
			
			maximumFractionDigits = maximumFraction.getInt();
			DTProperties.put(DataTypeBigDecimal.class.getName(), 
	                 "maximumFractionDigits", 
	                 Integer.valueOf(maximumFractionDigits).toString());
		}
	} // end of inner class

}
