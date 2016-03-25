package org.squirrelsql.session.sql.searchchandreplace;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;

public class ReplaceView
{
   //////////////////////////////////////////////////////////////////////
   // Naming convention, see:
   // http://docs.oracle.com/javafx/2/fxml_get_started/whats_new2.htm
   @FXML AnchorPane searchView;
   @FXML SearchView searchViewController;
   //
   //////////////////////////////////////////////////////////////////////

   @FXML ComboBox cboReplaceText;
   @FXML Button btnReplace;
   @FXML Button btnReplaceAll;
   @FXML Button btnExclude;
}
