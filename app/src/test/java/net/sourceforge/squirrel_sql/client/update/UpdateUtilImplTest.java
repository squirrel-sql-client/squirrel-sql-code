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
package net.sourceforge.squirrel_sql.client.update;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.DOWNLOADS_DIR_NAME;
import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.PLUGIN_ARTIFACT_ID;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import junit.framework.Assert;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.util.PathUtils;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class UpdateUtilImplTest extends BaseSQuirreLJUnit4TestCase {

	private UpdateUtilImpl underTest = null;
	private EasyMockHelper mockHelper = new EasyMockHelper();
   
   /* Mock Objects */
   private IPluginManager mockPluginManager = null;
   private FileWrapperFactory mockFileWrapperFactory = null;
   private ApplicationFileWrappers mockApplicationFileWrappers = null;
	private FileWrapper mockUpdateDirectory = null;
	private FileWrapper mockDownloadsDirectory = null;
	private FileWrapper mockDownloadsPluginDirectory = null;
   private IOUtilities mockIOUtilities = null;
   private UpdateXmlSerializer mockSerializer = null;
	private PathUtils mockPathUtils = null;
	private IProxySettings mockProxySettings = null;
	private FileWrapper mockDownloadsCoreDirectory = null;
	private FileWrapper mockSquiirelLibDir = null;
   
   @Before
   public void setUp() throws Exception {
      underTest = new UpdateUtilImpl();
      
      // create mocks to be injected
      mockPluginManager = mockHelper.createMock("mockPluginManager",IPluginManager.class);
      mockFileWrapperFactory = mockHelper.createMock("mockFileWrapperFactory",FileWrapperFactory.class);
      mockApplicationFileWrappers = mockHelper.createMock("mockApplicationFileWrappers",ApplicationFileWrappers.class);
      mockIOUtilities = mockHelper.createMock("mockIOUtilities", IOUtilities.class);
      mockSerializer = mockHelper.createMock("UpdateXmlSerializer", UpdateXmlSerializer.class);
      mockPathUtils = mockHelper.createMock(PathUtils.class);
      
      // inject mocks
      underTest.setPluginManager(mockPluginManager);
      underTest.setFileWrapperFactory(mockFileWrapperFactory);
      underTest.setApplicationFileWrappers(mockApplicationFileWrappers);
      underTest.setIOUtilities(mockIOUtilities);
      underTest.setUpdateXmlSerializer(mockSerializer);
      underTest.setPathUtils(mockPathUtils);
      
      // common expectations
      setupAppFileExpectations();
      setupDirectoryExpectations();
   }

   private void setupAppFileExpectations() {
   	mockUpdateDirectory = mockHelper.createMock("mockUpdateDirectory",FileWrapper.class);
   	expect(mockUpdateDirectory.exists()).andStubReturn(true);
   	expect(mockUpdateDirectory.isDirectory()).andStubReturn(true);
   	expect(mockApplicationFileWrappers.getUpdateDirectory()).andStubReturn(mockUpdateDirectory);
   }
   
   private void setupDirectoryExpectations() {
   	// downloads directory
   	mockDownloadsDirectory = mockHelper.createMock("mockDownloadsDirectory",FileWrapper.class);
   	expect(mockDownloadsDirectory.exists()).andStubReturn(true);
   	expect(mockDownloadsDirectory.isDirectory()).andStubReturn(true);
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, DOWNLOADS_DIR_NAME));
   	expectLastCall().andStubReturn(mockDownloadsDirectory);
   	
   	// downloads/plugin directory
   	mockDownloadsPluginDirectory = mockHelper.createMock("mockDownloadsPluginDirectory", FileWrapper.class);
   	expect(mockDownloadsPluginDirectory.exists()).andStubReturn(true);
   	expect(mockDownloadsPluginDirectory.isDirectory()).andStubReturn(true);
   	expect(mockFileWrapperFactory.create(mockDownloadsDirectory, PLUGIN_ARTIFACT_ID));
   	expectLastCall().andStubReturn(mockDownloadsPluginDirectory);
   	
   	// squirrel installed lib dir
   	mockSquiirelLibDir = mockHelper.createMock("mockSquiirelLibDir", FileWrapper.class);
   	expect(mockSquiirelLibDir.exists()).andStubReturn(true);
   	expect(mockSquiirelLibDir.isDirectory()).andStubReturn(true);
   	
   	// downloads/core directory
   	mockDownloadsCoreDirectory = mockHelper.createMock("mockDownloadsCoreDirectory", FileWrapper.class);
   	expect(mockDownloadsCoreDirectory.exists()).andStubReturn(true);
   	expect(mockDownloadsCoreDirectory.isDirectory()).andStubReturn(true);
   }
   
   @After
   public void tearDown() throws Exception {
      underTest = null;
   }

   @Test	
   public void testGetInstalledPlugins() {
   	PluginInfo[] pluginInfos = new PluginInfo[2];
   	PluginInfo mockPlugin1 = mockHelper.createMock(PluginInfo.class);
   	PluginInfo mockPlugin2 = mockHelper.createMock(PluginInfo.class);
   	EasyMock.expect(mockPlugin1.getInternalName()).andReturn("plugin1");
   	EasyMock.expect(mockPlugin2.getInternalName()).andReturn("plugin2");
   	pluginInfos[0] = mockPlugin1;
   	pluginInfos[1] = mockPlugin2;
   	expect(mockPluginManager.getPluginInformation()).andReturn(pluginInfos);
   	
   	mockHelper.replayAll();
   	Set<String> installedPlugins = underTest.getInstalledPlugins();
   	mockHelper.verifyAll();
   	
   	assertEquals(2, installedPlugins.size());
   }
   
   @Test
   public void testGetDownloadFileLocation_core() {
   	String coreJarFilename = "somecore.jar";
   	ArtifactStatus mockArtifactStatus = mockHelper.createMock(ArtifactStatus.class);
   	expect(mockArtifactStatus.getType()).andReturn(UpdateUtil.CORE_ARTIFACT_ID).atLeastOnce();
   	expect(mockArtifactStatus.getName()).andReturn(coreJarFilename);
//   	FileWrapper mockDownloadsDirectory = mockHelper.createMock("mockDownloadsDirectory", FileWrapper.class);
//   	expect(mockDownloadsDirectory.isDirectory()).andStubReturn(true);
//   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, UpdateUtil.DOWNLOADS_DIR_NAME));
//   	expectLastCall().andReturn(mockDownloadsDirectory);
   	FileWrapper mockDownloadsCoreDirectory = mockHelper.createMock("mockDownloadsCoreDirectory", FileWrapper.class);
   	expect(mockDownloadsCoreDirectory.isDirectory()).andReturn(true);
   	expect(mockFileWrapperFactory.create(mockDownloadsDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockDownloadsCoreDirectory);
   	FileWrapper mockSomeCoreJarFile = mockHelper.createMock("mockSomeCoreJarFile", FileWrapper.class);
   	expect(mockFileWrapperFactory.create(mockDownloadsCoreDirectory, coreJarFilename));
   	expectLastCall().andReturn(mockSomeCoreJarFile);
   	
   	mockHelper.replayAll();
   	FileWrapper result = underTest.getDownloadFileLocation(mockArtifactStatus);
   	mockHelper.verifyAll();
   	
   	assertEquals(mockSomeCoreJarFile, result);
   }
   
   @Test
   public void testGetCheckSum() throws IOException {
   	
   	FileWrapper mockFile = mockHelper.createMock("mockFile", FileWrapper.class);
   	expect(mockFile.getAbsolutePath()).andReturn("/path/To/Mock/File");
   	expect(mockIOUtilities.getCheckSum(mockFile)).andReturn(1000L);
   	mockHelper.replayAll();
   	long checksumResult = underTest.getCheckSum(mockFile);
   	mockHelper.verifyAll();
   	
   	assertEquals(1000L, checksumResult);
   }
   
   /**
    * CopyFile Scenario 1 is the destination file is identical to the source file. Destination file is not a 
    * directory.
    *  
    * @throws IOException
    */
   @Test
   public void testCopyFile_scenario1() throws IOException {

   	// setup source mock file
   	FileWrapper fromFile = mockHelper.createMock("fromFile", FileWrapper.class);
   	expect(fromFile.exists()).andReturn(true);
   	expect(fromFile.getAbsolutePath()).andReturn("/path/to/from/file").atLeastOnce();
   	expect(mockIOUtilities.getCheckSum(fromFile)).andReturn(1000L);
   	
   	// setup dest mock file
   	FileWrapper toFile = mockHelper.createMock("toFile", FileWrapper.class);
   	expect(toFile.isDirectory()).andReturn(false);
   	expect(toFile.exists()).andReturn(true);
   	expect(toFile.getAbsolutePath()).andReturn("/path/to/to/file").atLeastOnce();
   	expect(mockIOUtilities.getCheckSum(toFile)).andReturn(1000L);
   	
   	mockHelper.replayAll();
		underTest.copyFile(fromFile, toFile);
   	mockHelper.verifyAll();
   	
   }
   
   @Test 
   public void testCheckDir_exists() {

   	FileWrapper mockUpdateCoreDirectory = mockHelper.createMock(FileWrapper.class);
   	expect(mockUpdateCoreDirectory.exists()).andReturn(true);
   	
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockUpdateCoreDirectory);
   	
   	mockHelper.replayAll();
		underTest.checkDir(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID);
   	mockHelper.verifyAll();   	
   }
   
   @Test 
   public void testCheckDir_notexists() {

   	FileWrapper mockUpdateCoreDirectory = mockHelper.createMock(FileWrapper.class);
   	expect(mockUpdateCoreDirectory.exists()).andReturn(false);
   	expect(mockUpdateCoreDirectory.mkdir()).andReturn(true);
   	
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockUpdateCoreDirectory);
   	
   	
   	mockHelper.replayAll();
		underTest.checkDir(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID);
   	mockHelper.verifyAll();   	
   }

   @Test 
   public void testCheckDir_notexists_failure() {

   	FileWrapper mockUpdateCoreDirectory = mockHelper.createMock(FileWrapper.class);
   	expect(mockUpdateCoreDirectory.exists()).andReturn(false);
   	expect(mockUpdateCoreDirectory.mkdir()).andReturn(false);
   	expect(mockUpdateCoreDirectory.getAbsolutePath()).andReturn("/path/to/directory/that/was/not/made");
   	
   	expect(mockFileWrapperFactory.create(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID));
   	expectLastCall().andReturn(mockUpdateCoreDirectory);
   	
   	
   	mockHelper.replayAll();
		underTest.checkDir(mockUpdateDirectory, UpdateUtil.CORE_ARTIFACT_ID);
   	mockHelper.verifyAll();   	
   }

   @Test
   public void testLoadUpdateFromFileSystem() throws IOException {

   	String pathToDirectoryThatContainsReleaseXml = "/path/to/release.xml/file";
   	String releaseXmlFilePath = "/path/to/release.xml/file/release.xml";
   	
   	FileWrapper releaseXmlFileDir = getDirectoryMock("releaseXmlFileDir"); 
   	FileWrapper releaseXmlFile = getFileMock("releaseXmlFile", releaseXmlFilePath);

   	expect(mockFileWrapperFactory.create(pathToDirectoryThatContainsReleaseXml));
   	expectLastCall().andReturn(releaseXmlFileDir);
   	expect(mockFileWrapperFactory.create(releaseXmlFileDir, UpdateUtil.RELEASE_XML_FILENAME));
   	expectLastCall().andReturn(releaseXmlFile);
   	
   	ChannelXmlBean mockChannelXmlBean = mockHelper.createMock(ChannelXmlBean.class);
		expect(mockSerializer.readChannelBean(releaseXmlFile)).andReturn(mockChannelXmlBean);
   	
   	mockHelper.replayAll();
		underTest.loadUpdateFromFileSystem(pathToDirectoryThatContainsReleaseXml);
   	mockHelper.verifyAll();   	
   	
   }

   @Test
   public void testIsPresentInDownloadDirectory() throws IOException {
   	// Data
   	String pluginFilename = "aPluginName.zip";
   	String pluginFileAbsPath = "path/to/plugin/file/"+pluginFilename;
   	long pluginFileCheckSum = 10L;
   	long pluginFileByteSize = 20L;
   	
   	ArtifactStatus status = mockHelper.createMock(ArtifactStatus.class);
   	expect(status.getType()).andStubReturn(UpdateUtil.PLUGIN_ARTIFACT_ID);
   	expect(status.getName()).andReturn(pluginFilename);
		expect(status.getChecksum()).andReturn(pluginFileCheckSum);
		expect(status.getSize()).andReturn(pluginFileByteSize);
		
   	FileWrapper fileInDownlodDir = mockHelper.createMock("fileInDownlodDir", FileWrapper.class);   	
		expect(fileInDownlodDir.getAbsolutePath()).andReturn(pluginFileAbsPath);
   	expect(fileInDownlodDir.exists()).andReturn(true);
   	expect(fileInDownlodDir.length()).andReturn(pluginFileByteSize);
   	expect(mockFileWrapperFactory.create(mockDownloadsPluginDirectory, pluginFilename));
   	expectLastCall().andReturn(fileInDownlodDir);
   	expect(mockIOUtilities.getCheckSum(fileInDownlodDir)).andReturn(pluginFileCheckSum);
   	
   	mockHelper.replayAll();
		boolean result = underTest.isPresentInDownloadsDirectory(status);
   	mockHelper.verifyAll();
   	
   	assertTrue(result);
   }   
   
   @Test
   public void testDownloadHttpUpdateFile_verifysuccess() throws Exception {
   	testDownloadHttpUpdateFile(true);
   }
   
   @Test
   public void testDownloadHttpUpdateFile_verifyfailure() throws Exception {
   	testDownloadHttpUpdateFile(false);
   }   
   
   @Test
   public void testCopyDir() throws Exception {
   	FileWrapper[] listOfFiles = new FileWrapper[1];
   	FileWrapper mockFwJarFileWrapper = mockHelper.createMock("mockFwJarFileWrapper",FileWrapper.class);
   	listOfFiles[0] = mockFwJarFileWrapper;
   	expect(mockFwJarFileWrapper.exists()).andStubReturn(true);
   	expect(mockFwJarFileWrapper.getName()).andReturn("fw.jar");
   	expect(mockFwJarFileWrapper.getAbsolutePath()).andStubReturn("/squirrellibdir/test/path/fw.jar");
   	expect(mockSquiirelLibDir.listFiles()).andStubReturn(listOfFiles);
   	
   	FileWrapper mockFwJarFileInDownloadsWrapper = 
   		mockHelper.createMock("mockFwJarFileInDownloadsWrapper", FileWrapper.class);
   	expect(mockFwJarFileInDownloadsWrapper.getAbsolutePath()).andStubReturn("/downloads/test/path/fw.jar");
   	expect(mockFwJarFileInDownloadsWrapper.exists()).andReturn(false);
   	
   	expect(mockFileWrapperFactory.create(mockDownloadsCoreDirectory, "fw.jar"));
   	expectLastCall().andReturn(mockFwJarFileInDownloadsWrapper);
   	
   	mockIOUtilities.copyFile(mockFwJarFileWrapper, mockFwJarFileInDownloadsWrapper);
   	
   	mockHelper.replayAll();
   	underTest.copyDir(mockSquiirelLibDir, mockDownloadsCoreDirectory);
   	mockHelper.verifyAll();
   }
   
   @Test (expected = IllegalArgumentException.class)
   public void testCopyDir_fromDirNotADir() throws Exception {
   	FileWrapper mockFromDir = mockHelper.createMock("mockFromFile", FileWrapper.class);
   	expect(mockFromDir.getAbsolutePath()).andStubReturn("/absolute/path/to/mockFromFile");
		FileWrapper mockToDir = mockHelper.createMock("mockToDir", FileWrapper.class);
		expect(mockToDir.getAbsolutePath()).andStubReturn("/absolute/path/to/mockToDir");
		
   	expect(mockFromDir.isDirectory()).andStubReturn(false);
		
   	mockHelper.replayAll();
		underTest.copyDir(mockFromDir, mockToDir);
		mockHelper.verifyAll();
   }

   @Test (expected = IllegalArgumentException.class)
   public void testCopyDir_ToDirNotADir() throws Exception {
   	FileWrapper mockFromDir = mockHelper.createMock("mockFromFile", FileWrapper.class);
   	expect(mockFromDir.getAbsolutePath()).andStubReturn("/absolute/path/to/mockFromFile");
		FileWrapper mockToDir = mockHelper.createMock("mockToDir", FileWrapper.class);
		expect(mockToDir.getAbsolutePath()).andStubReturn("/absolute/path/to/mockToDir");
		
   	expect(mockFromDir.isDirectory()).andStubReturn(true);
   	expect(mockToDir.isDirectory()).andStubReturn(false);
		
   	mockHelper.replayAll();
		underTest.copyDir(mockFromDir, mockToDir);
		mockHelper.verifyAll();
   }
   
   // Helper methods
   
   private void testDownloadHttpUpdateFile(boolean simulateSuccess) throws Exception {
		String host = "somehost.com";
		int port = 80;
		String fileToGet = "/updates/snapshot/release.xml";
		String fileFromPath = UpdateUtil.RELEASE_XML_FILENAME;
		String destDir = "/some/dest/directory";
		int fileSize = 10;
		long checksum = 10;
		String absPath = destDir + "/" + fileFromPath;
		mockProxySettings = mockHelper.createMock(IProxySettings.class);
		
		// Cannot mock URLs, they are final and so they cannot be subclassed.
		URL url = new URL("http", host, fileToGet);
		
		expect(mockIOUtilities.constructHttpUrl(host, port, fileToGet)).andReturn(url);
		
   	
   	expect(mockPathUtils.getFileFromPath(fileToGet)).andReturn(fileFromPath);
   	FileWrapper destFile = mockHelper.createMock(FileWrapper.class);
   	expect(destFile.getAbsolutePath()).andReturn(absPath);
   	expect(mockFileWrapperFactory.create(destDir, fileFromPath)).andReturn(destFile);
   	if (simulateSuccess) {
   		expect(mockIOUtilities.downloadHttpFile(url, destFile, mockProxySettings)).andReturn(fileSize);
   	} else {
   		expect(mockIOUtilities.downloadHttpFile(url, destFile, mockProxySettings)).andReturn(fileSize-1);
   	}
		
   	String downloadedPath = null;
   	mockHelper.replayAll();
   	try {
   		downloadedPath =  
   			underTest.downloadHttpUpdateFile(host, port, fileToGet, destDir, fileSize, checksum, mockProxySettings);
			if (!simulateSuccess) {
				fail("Expected an exception to be thrown for failed filesize verification");
			}
   	} catch (Exception e) {
   		if (simulateSuccess) {
   			fail("Unexpected exception : "+e.getMessage());
   		}
   	}
   	mockHelper.verifyAll();   
   	if (simulateSuccess) {
   		Assert.assertEquals(absPath, downloadedPath);
   	}
   	
   }
   
   private FileWrapper getDirectoryMock(String name) {
   	FileWrapper result = mockHelper.createMock(name, FileWrapper.class);
   	expect(result.isDirectory()).andStubReturn(true);
   	return result;
   }
   
   private FileWrapper getFileMock(String name, String path) {
   	FileWrapper result = mockHelper.createMock(name, FileWrapper.class);
   	expect(result.isDirectory()).andStubReturn(false);
   	expect(result.getAbsolutePath()).andStubReturn(path);
   	return result;   	
   }
}
