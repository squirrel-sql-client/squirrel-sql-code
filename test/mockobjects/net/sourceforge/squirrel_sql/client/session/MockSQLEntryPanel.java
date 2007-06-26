package net.sourceforge.squirrel_sql.client.session;

import java.awt.Font;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public class MockSQLEntryPanel implements ISQLEntryPanel {

	public void addCaretListener(CaretListener lis) {
		// TODO Auto-generated method stub

	}

	public void addMouseListener(MouseListener lis) {
		// TODO Auto-generated method stub

	}

	public void addSQLTokenListener(SQLTokenListener tl) {
		// TODO Auto-generated method stub

	}

	public void addToSQLEntryAreaMenu(JMenu menu) {
		// TODO Auto-generated method stub

	}

	public JMenuItem addToSQLEntryAreaMenu(Action action) {
		return new JMenuItem();
	}

	public void addUndoableEditListener(UndoableEditListener listener) {
		// TODO Auto-generated method stub

	}

	public void appendText(String text) {
		// TODO Auto-generated method stub

	}

	public void appendText(String sqlScript, boolean select) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public int[] getBoundsOfSQLToBeExecuted() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCaretLineNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCaretLinePosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getCaretPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getDoesTextComponentHaveScroller() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getSQLToBeExecuted() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSelectedText() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSelectionEnd() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSelectionStart() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	public JTextComponent getTextComponent() {
		return new JTextArea();
	}

	public boolean hasFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasOwnUndoableManager() {
		// TODO Auto-generated method stub
		return false;
	}

	public void moveCaretToNextSQLBegin() {
		// TODO Auto-generated method stub

	}

	public void moveCaretToPreviousSQLBegin() {
		// TODO Auto-generated method stub

	}

	public void removeCaretListener(CaretListener lis) {
		// TODO Auto-generated method stub

	}

	public void removeMouseListener(MouseListener lis) {
		// TODO Auto-generated method stub

	}

	public void removeSQLTokenListener(SQLTokenListener tl) {
		// TODO Auto-generated method stub

	}

	public void removeUndoableEditListener(UndoableEditListener listener) {
		// TODO Auto-generated method stub

	}

	public void replaceSelection(String sqlScript) {
		// TODO Auto-generated method stub

	}

	public void requestFocus() {
		// TODO Auto-generated method stub

	}

	public void selectCurrentSql() {
		// TODO Auto-generated method stub

	}

	public void setCaretPosition(int pos) {
		// TODO Auto-generated method stub

	}

	public void setFont(Font font) {
		// TODO Auto-generated method stub

	}

	public void setSelectionEnd(int pos) {
		// TODO Auto-generated method stub

	}

	public void setSelectionStart(int pos) {
		// TODO Auto-generated method stub

	}

	public void setTabSize(int tabSize) {
		// TODO Auto-generated method stub

	}

	public void setText(String sqlScript) {
		// TODO Auto-generated method stub

	}

	public void setText(String sqlScript, boolean select) {
		// TODO Auto-generated method stub

	}

	public void setUndoActions(Action undo, Action redo) {
		// TODO Auto-generated method stub

	}

	public void setUndoManager(UndoManager manager) {
		// TODO Auto-generated method stub

	}

	public IIdentifier getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
