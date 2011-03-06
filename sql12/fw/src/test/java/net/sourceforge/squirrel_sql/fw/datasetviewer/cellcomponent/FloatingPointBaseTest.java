/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.text.NumberFormat;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Some Test-Cases for data types based on {@link FloatingPointBase}.
 * Especially rendering of values will be tested.
 * @author Stefan Willinger
 * @param <T> Type, which the specific data type will handle e.g. {@link Float}
 */
public abstract class FloatingPointBaseTest<T> extends AbstractDataTypeComponentTest {

	private String[] savedDTProperties;

	/**
	 * For some tests, we need to change {@link DTProperties} before initialization of 
	 * the tested class. This method will be called after changing DTProperties
	 * and must recreate the classUnderTest.
	 */
	protected abstract void initClassUnderTest();


	@Before
	public void setUp() throws Exception {
		// some tests need to change the configuration. Save it first.
		saveCurrentDTProperties();
		super.setUp();

	}
	
	@After
	public void tearDown() throws Exception {
		// some tests need to change the configuration. Restore them now.
		restoreCurrentDTProperties();
	}
	
	
	/**
	 * Ensure that rendering of a value will be right with the default behavior.
	 * The default behavior uses a locale dependent format and an default fraction of 5.
	 * @see #getValueForRenderingTests()
	 * @throws Exception
	 */
	@Test
	public void testFormat_defaultBehavior() throws Exception
	{
		initClassUnderTest();
		
		T value = getValueForRenderingTests();
		
		
		String rendered = classUnderTest.renderObject(value);
		
		
		/*
		 * Use the systems number format instead of comparing string's
		 * make this test independent from the locale.
		 */
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(5);
		
		assertEquals(numberFormat.format(value), rendered);
	}
	
	
	/**
	 * The value to use for rendering-tests.
	 * It is suggested to use the value <code>1234.1456789</code> because the default fraction is 5
	 * and the custom fraction used by these tests is 6.
	 */
	protected abstract T getValueForRenderingTests();


	/**
	 * Ensure that rendering of a value will be right by choosing the locale dependent format and an default fraction of 5.
	 * @see #getValueForRenderingTests()
	 * @throws Exception
	 */
	@Test
	public void testUseLocaleDependingFormat_defaultFraction() throws Exception
	{
		DTProperties.put(DataTypeBigDecimal.class.getName(), "useJavaDefaultFormat", "false");
		
		initClassUnderTest();
		
		T value = getValueForRenderingTests();
		
		String rendered = classUnderTest.renderObject(value);
		
		/*
		 * Use the systems number format instead of comparing string's
		 * make this test independent from the locale.
		 */
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(5);  //Default fraction
		
		assertEquals(numberFormat.format(value), rendered);
		
	}
	
	
	/**
	 * Ensure that rendering of a value will be right by choosing the locale dependent format and an custom fraction.
	 * @see #getValueForRenderingTests()
	 * @throws Exception
	 */
	@Test
	public void testUseLocaleDependingFormat_customFraction() throws Exception
	{
		DTProperties.put(DataTypeBigDecimal.class.getName(), "useJavaDefaultFormat", "false");
		DTProperties.put(DataTypeBigDecimal.class.getName(), "maximumFractionDigits", "6");
		
		initClassUnderTest();
		
		T value = getValueForRenderingTests();
		
		String rendered = classUnderTest.renderObject(value);
		
		/*
		 * Use the systems number format instead of comparing string's
		 * make this test independent from the locale.
		 */
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(6);
		
		assertEquals(numberFormat.format(value), rendered);
		
	}
	
	
	
	private void restoreCurrentDTProperties() {
		DTProperties props = new DTProperties();
		props.setDataArray(savedDTProperties);
		
	}

	private void saveCurrentDTProperties() {
		DTProperties props = new DTProperties();
		savedDTProperties = props.getDataArray();
	}

	/**
	 * Test rendering of a value using the JavaDefaultFormat
	 * @see #getValueForRenderingTests()
	 */
	@Test
	public void testUseDefaultFormat()  throws Exception
	{
		DTProperties.put(DataTypeBigDecimal.class.getName(), "useJavaDefaultFormat", "true");
		
		initClassUnderTest();
		
		T value = getValueForRenderingTests();

		String rendered = classUnderTest.renderObject(value);
		
		assertEquals(value.toString(), rendered);
		
	}
	
	
	protected void resetPropertiesLoadedFlag() throws Exception {
		Field field = FloatingPointBase.class.getDeclaredField("propertiesAlreadyLoaded");
		field.setAccessible(true);
		field.set(null, false);
	}
	
	
}
