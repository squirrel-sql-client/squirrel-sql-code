package net.sourceforge.squirrel_sql.client.session;

import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IUndoHandler;

public class MockSQLEntryPanel implements ISQLEntryPanel {

    ISession _session = null;
    
    public MockSQLEntryPanel(ISession session) {
        _session = session;
    }
    
	public void addCaretListener(CaretListener lis) {
		

	}

	public void addMouseListener(MouseListener lis) {
		

	}

	public void addSQLTokenListener(SQLTokenListener tl) {
		

	}

	public void addToSQLEntryAreaMenu(JMenu menu) {
		

	}

	public JMenuItem addToSQLEntryAreaMenu(Action action) {
		return new JMenuItem();
	}

	public void addUndoableEditListener(UndoableEditListener listener) {
		

	}

	public void appendText(String text) {
		

	}

	public void appendText(String sqlScript, boolean select) {
		

	}

	public void dispose() {
		

	}

	public int[] getBoundsOfSQLToBeExecuted() {
		
		return null;
	}

	public int getCaretLineNumber() {
		
		return 0;
	}

	public int getCaretLinePosition() {
		
		return 0;
	}

	public int getCaretPosition() {
		
		return 0;
	}

	public boolean getDoesTextComponentHaveScroller() {
		
		return false;
	}

	public String getSQLToBeExecuted() {
		
		return null;
	}

	public String getSelectedText() {
		
		return null;
	}

	public int getSelectionEnd() {
		
		return 0;
	}

	public int getSelectionStart() {
		
		return 0;
	}

	public String getText() {
		
		return null;
	}

	public JTextComponent getTextComponent() {
		return new JTextArea();
	}

	public boolean hasFocus() {
		
		return false;
	}

	public boolean hasOwnUndoableManager() {
		
		return false;
	}

	public void moveCaretToNextSQLBegin() {
		

	}

	public void moveCaretToPreviousSQLBegin() {
		

	}

	public void removeCaretListener(CaretListener lis) {
		

	}

	public void removeMouseListener(MouseListener lis) {
		

	}

	public void removeSQLTokenListener(SQLTokenListener tl) {
		

	}

	public void removeUndoableEditListener(UndoableEditListener listener) {
		

	}

	public void replaceSelection(String sqlScript) {
		

	}

	public void requestFocus() {
		

	}

	public void selectCurrentSql() {
		

	}

	public void setCaretPosition(int pos) {
		

	}

	public void setFont(Font font) {
		

	}

	public void setSelectionEnd(int pos) {
		

	}

	public void setSelectionStart(int pos) {
		

	}

	public void setTabSize(int tabSize) {
		

	}

	public void setText(String sqlScript) {
		

	}

	public void setText(String sqlScript, boolean select) {
		

	}

	public void addRedoUndoActionsToSQLEntryAreaMenu(Action undo, Action redo) {
		

	}

	public void setUndoManager(UndoManager manager) {
		

	}

	public IIdentifier getIdentifier() {
		
		return null;
	}

    /**
     * @see net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel#getSession()
     */
    public ISession getSession() {
        
        return null;
    }


   @Override
   public IUndoHandler createUndoHandler()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public String getWordAtCursor()
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public JScrollPane createScrollPane(JTextComponent textComponent)
   {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }
}
