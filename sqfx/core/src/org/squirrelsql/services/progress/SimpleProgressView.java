package org.squirrelsql.services.progress;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;

public class SimpleProgressView
{
   @FXML ProgressIndicator progressIndicator;
   @FXML TextArea txtProgressMsg;
   @FXML Button btnCancelClose;
}
