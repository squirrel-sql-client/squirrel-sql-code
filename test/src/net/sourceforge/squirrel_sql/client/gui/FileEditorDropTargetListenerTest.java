/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.gui;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.gui.dnd.FileEditorDropTargetListener;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.EasyMockHelper;

public class FileEditorDropTargetListenerTest extends BaseSQuirreLJUnit4TestCase
{
	private FileEditorDropTargetListener classUnderTest = null;

	EasyMockHelper mockHelper = new EasyMockHelper();
	
	private ISession mockSession = mockHelper.createMock(ISession.class);

	private DropTargetDropEvent dtde = mockHelper.createMock(DropTargetDropEvent.class);
	private DropTargetContext mockDropTargetContext = mockHelper.createMock(DropTargetContext.class); 
	private Transferable mockTransferable = mockHelper.createMock(Transferable.class);
	private ISQLPanelAPI mockSqlPanelApi = mockHelper.createMock(ISQLPanelAPI.class);
	
	List<File> mockDroppedFiles = new ArrayList<File>();
	
	@Before
	public void setUp() throws Exception
	{
		mockSession.showErrorMessage(isA(String.class));
		EasyMock.expectLastCall().anyTimes();
		expect(dtde.getDropTargetContext()).andStubReturn(mockDropTargetContext);
		expect(dtde.getTransferable()).andStubReturn(mockTransferable);
		expect(mockSession.getSQLPanelAPIOfActiveSessionWindow()).andStubReturn(mockSqlPanelApi);
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		expectLastCall().anyTimes();
		classUnderTest = new FileEditorDropTargetListener(mockSession);
		
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testDrop_javaFileListFlavor_emptyFileList() throws Exception
	{
		expect(mockTransferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)).andReturn(true);
		expect(mockTransferable.getTransferData(DataFlavor.javaFileListFlavor));
		expectLastCall().andReturn(mockDroppedFiles);
		mockDropTargetContext.dropComplete(true);
		
		mockHelper.replayAll();
		classUnderTest.drop(dtde);
		mockHelper.verifyAll();
	}

	@Test
	public void testDrop_javaFileListFlavor_SingleFileInList() throws Exception
	{
		File testFile = new File("/a/test/file");
		mockDroppedFiles.add(testFile);
		testDrop_javaFileListFlavor(mockDroppedFiles);
	}
	
	@Test
	public void testDrop_javaFileListFlavor_MultiFileInList() throws Exception
	{
		File testFile = new File("/a/test/file");
		File testFile2 = new File("/a/test/file2");
		mockDroppedFiles.add(testFile);
		mockDroppedFiles.add(testFile2);
		testDrop_javaFileListFlavor(mockDroppedFiles);
	}


 
	private void testDrop_javaFileListFlavor(List<File> mockDroppedFiles) throws Exception
	{
		expect(mockTransferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)).andReturn(true);
		expect(mockTransferable.getTransferData(DataFlavor.javaFileListFlavor));
		expectLastCall().andReturn(mockDroppedFiles);
		mockDropTargetContext.dropComplete(true);
		if (mockDroppedFiles.size() == 1) {
			mockSqlPanelApi.fileOpen(mockDroppedFiles.get(0));
		}
		mockHelper.replayAll();
		classUnderTest.drop(dtde);
		mockHelper.verifyAll();
	}

	@Test 
	public void testDrop_javaStringFlavor() throws Exception
	{
		expect(mockTransferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)).andReturn(false);
		expect(mockTransferable.isDataFlavorSupported(DataFlavor.stringFlavor)).andReturn(true);
		expect(mockTransferable.getTransferData(DataFlavor.stringFlavor));
		expectLastCall().andReturn("file:///foo.txt");
		mockSqlPanelApi.fileOpen(isA(File.class));
		mockDropTargetContext.dropComplete(true);

		mockHelper.replayAll();
		classUnderTest.drop(dtde);
		mockHelper.verifyAll();

	}
	
	@Test
	public void testDrop_uriListFlavor() throws Exception {
		
		expect(mockTransferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)).andReturn(false);
		expect(mockTransferable.isDataFlavorSupported(DataFlavor.stringFlavor)).andReturn(false);
		expect(mockTransferable.isDataFlavorSupported(EasyMock.isA(DataFlavor.class))).andStubReturn(true);
		
		expect(mockTransferable.getTransferData(EasyMock.isA(DataFlavor.class)));
		expectLastCall().andReturn("file:///foo.txt");
		mockSqlPanelApi.fileOpen(isA(File.class));
		mockDropTargetContext.dropComplete(true);
		
		mockHelper.replayAll();
		classUnderTest.drop(dtde);
		mockHelper.verifyAll();
		
	}
	
}
