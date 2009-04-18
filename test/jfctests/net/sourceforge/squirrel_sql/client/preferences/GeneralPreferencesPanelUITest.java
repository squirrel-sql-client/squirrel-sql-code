package net.sourceforge.squirrel_sql.client.preferences;

import static org.easymock.EasyMock.expect;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JRadioButtonFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This is a FEST UI test for GeneralPreferencesPanel
 */
@GUITest
public class GeneralPreferencesPanelUITest extends BaseSQuirreLJUnit4TestCase {

	JFrame frame = null;
	FrameFixture fixture = null;
	
	@Before
   public void setUp() throws Exception
   {
   	if (frame == null) {
   		frame = constructTestFrame();
   	}
   	fixture = new FrameFixture(frame);
   	fixture.show();
   	
   }

	@After
   public void tearDown()
   {
		if (fixture != null) {
			fixture.cleanUp();
		}
   }

	@Test
	public void testSomething() {
		JRadioButtonFixture tabbedStyleRadioButton = fixture.radioButton("tabbedStyleRadioButton");
		JRadioButtonFixture internalFrameStyleRadioButton = fixture.radioButton("internalFrameStyleRadioButton");
		JCheckBoxFixture showContentsCheckBox = fixture.checkBox("showContentsCheckBox");
		JCheckBoxFixture maximizeSessionSheetCheckBox = fixture.checkBox("maximizeSessionSheetCheckBox");
		JCheckBoxFixture showTabbedStyleHintCheckBox = fixture.checkBox("showTabbedStyleHintCheckBox");
		
		tabbedStyleRadioButton.click();
		
		showContentsCheckBox.requireDisabled();
		maximizeSessionSheetCheckBox.requireDisabled();
		showTabbedStyleHintCheckBox.requireDisabled();
		
		internalFrameStyleRadioButton.click();
		
		showContentsCheckBox.requireEnabled();
		maximizeSessionSheetCheckBox.requireEnabled();
		showTabbedStyleHintCheckBox.requireEnabled();
	}
	
	/**
    * The main method is not used at all in the test - it is just here to allow for user interaction testing
    * with the graphical component, which doesn't require launching SQuirreL.
    * 
    * @param args
    */
    public static void main(String[] args) {
   	 new GeneralPreferencesPanelUITest().constructTestFrame().setVisible(true);
    }

    private JFrame constructTestFrame() {
   	 IApplication mockApplication = mockHelper.createMock("mockApplication", IApplication.class);
   	 SquirrelPreferences prefs = new SquirrelPreferences();
   	 expect(mockApplication.getSquirrelPreferences()).andStubReturn(prefs);
   	 mockHelper.replayAll();
       final JFrame frame = new JFrame("Test UpdatePreferencesPanel");
       GeneralPreferencesPanel panel = new GeneralPreferencesPanel();
       panel.initialize(mockApplication);
       frame.getContentPane().add(panel.getPanelComponent());
       frame.setSize(700,600);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       return frame;
    }
}
