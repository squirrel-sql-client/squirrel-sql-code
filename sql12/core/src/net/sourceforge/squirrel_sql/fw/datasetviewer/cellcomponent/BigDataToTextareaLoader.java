package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * utility to load big amount of data to JtextArea without causing UI to hang. 
 * @author jarmolow
 *
 */
public class BigDataToTextareaLoader {
	private static final int CHUNK_SIZE = 1024;
	
	private String content;
	private PlainDocument doc;
	
	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(BigDataToTextareaLoader.class);

	/**
	 * I18n messages
	 */
	static interface i18n {

		String LOADING_MSG = s_stringMgr
				.getString("BigDataToTextareaLoader.loading");
	}

	public BigDataToTextareaLoader(String content) {
		super();
		this.content = content;
		this.doc = new PlainDocument();
	};
	
	public void startFillingInBackground() {
		final SimpleAttributeSet attrSet = new SimpleAttributeSet();
		if (content.length() <= CHUNK_SIZE) {
			try {
				doc.insertString(0, content, attrSet);
			} catch (BadLocationException e) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			}
		} else {
			try {
				attrSet.addAttribute(StyleConstants.Foreground, Color.RED);
				doc.insertString(0, i18n.LOADING_MSG, attrSet);
			} catch (BadLocationException e) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					int offset = i18n.LOADING_MSG.length();
					attrSet.addAttribute(StyleConstants.Foreground, Color.BLACK);
					try {
						for (int i = 0; i < content.length(); i += CHUNK_SIZE) {
							int endIndex = Math.min(i + CHUNK_SIZE,
									content.length());
							doc.insertString(offset,
									content.substring(i, endIndex), attrSet);
							offset += CHUNK_SIZE;
						}
						doc.remove(0, i18n.LOADING_MSG.length());
					} catch (BadLocationException e) {
						UIManager.getLookAndFeel().provideErrorFeedback(null);
					}
				}
			});
		}
	}
	
	public Document getDoc() {
		return doc;
	}
}
