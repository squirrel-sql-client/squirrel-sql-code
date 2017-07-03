package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.text.NumberFormat;

import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;

public abstract class FloatingPointBase extends BaseDataTypeComponent
{


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
		FloatingPointBaseDTProperties.loadProperties();

	  return new FloatingPointPanel();
	}


	public FloatingPointBase()
	{
		FloatingPointBaseDTProperties.loadProperties();
	}


	protected NumberFormat createNumberFormat()
   {
   	if(FloatingPointBaseDTProperties.isUseLocaleFormat())
		{
			NumberFormat numberFormat = NumberFormat.getInstance();

			// If we use _scale here some number displays go crazy.
			numberFormat.setMinimumFractionDigits(FloatingPointBaseDTProperties.getMinimumFractionDigits());
			numberFormat.setMaximumFractionDigits(FloatingPointBaseDTProperties.getMaximumFractionDigits());

			return numberFormat;
		}
		else if (FloatingPointBaseDTProperties.isUseUserDefinedFormat())
		{
			String userDefinedDecimalSeparator = FloatingPointBaseDTProperties.getUserDefinedDecimalSeparator();
			String groupingSeparator = FloatingPointBaseDTProperties.getUserDefinedGroupingSeparator();

			int userDefinedMinimumFractionDigits = FloatingPointBaseDTProperties.getUserDefinedMinimumFractionDigits();
			int userDefinedMaximumFractionDigits = FloatingPointBaseDTProperties.getUserDefinedMaximumFractionDigits();


			NumberFormat numberFormat = UserDefinedDecimalFormatFactory.createUserDefinedFormat(userDefinedDecimalSeparator, groupingSeparator, userDefinedMinimumFractionDigits, userDefinedMaximumFractionDigits);

			return numberFormat;

		}
		else  if (FloatingPointBaseDTProperties.isUseJavaDefaultFormat())
		{
			// Is not used at all in derived classes
			return NumberFormat.getInstance();
		}
		else
		{
			throw new IllegalStateException("Unknown Format");
		}
   }


}
