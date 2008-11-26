package net.sourceforge.squirrel_sql.client.preferences;

import static org.easymock.EasyMock.expect;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.IApplication;
import utils.EasyMockHelper;

public class UpdatePreferencesPanelTestUI {

	
	
    /**
     * @param args
     */
    public static void main(String[] args) {
   	 ApplicationArguments.initialize(new String[] {});
   	 
   	 EasyMockHelper mockHelper = new EasyMockHelper();
   	 
   	 // mocks
   	 IApplication mockApplication = mockHelper.createMock(IApplication.class);
   	 SquirrelPreferences mockPreferences = mockHelper.createMock(SquirrelPreferences.class);
   	 IUpdateSettings mockUpdateSettings = mockHelper.createMock(IUpdateSettings.class);
   	 
   	 expect(mockApplication.getSquirrelPreferences()).andStubReturn(mockPreferences);
   	 expect(mockPreferences.getUpdateSettings()).andStubReturn(mockUpdateSettings);
   	 expect(mockUpdateSettings.getUpdateServer()).andStubReturn("aTestServer");
   	 expect(mockUpdateSettings.getUpdateServerPort()).andStubReturn("aTestServerPort");
   	 expect(mockUpdateSettings.getUpdateServerPath()).andStubReturn("aTestServerPath");
   	 expect(mockUpdateSettings.getUpdateServerChannel()).andStubReturn("aTestServerChannel");
   	 expect(mockUpdateSettings.isEnableAutomaticUpdates()).andStubReturn(true);
   	 expect(mockUpdateSettings.getUpdateCheckFrequency()).andReturn("Daily");
   	 expect(mockUpdateSettings.isRemoteUpdateSite()).andReturn(true);
   	 expect(mockUpdateSettings.getFileSystemUpdatePath()).andReturn("");
   	 
   	 mockHelper.replayAll();
   	 
        
        
        
        final JFrame frame = new JFrame("Test UpdatePreferencesPanel");
        UpdatePreferencesTab tab = new UpdatePreferencesTab();
        tab.initialize(mockApplication);
        
        frame.getContentPane().add(tab.getPanelComponent());
        frame.setSize(600,600);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
