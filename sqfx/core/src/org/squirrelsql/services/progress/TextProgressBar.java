package org.squirrelsql.services.progress;

import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class TextProgressBar extends StackPane {
	private final ProgressBar _bar = new ProgressBar();
	private Text _text = new Text();
	
	public TextProgressBar() {		
		getChildren().setAll(_bar, _text);
	}

	public Text getText() {
		return _text;
	}
	public void setText(String text) {
		_text.setText(text);
	}
	public ProgressBar getProgressBar(){
		return _bar;
	}
	public void setBarWidth(double width){
		_bar.setPrefWidth(width);
	}
	public void setBarHeight(double height){
		_bar.setPrefHeight(height);
	}
}