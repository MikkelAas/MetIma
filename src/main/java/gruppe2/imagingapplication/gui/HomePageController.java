package gruppe2.imagingapplication.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HomePageController implements Initializable {
  Logger logger = LoggerFactory.getLogger(HomePageController.class);

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    /*
     * This method is currently empty because nothing
     * needs to be started when the home page is accessed
     */
  }

  /**
   * This method handles what happens when the view gallery button is pressed.
   * The button sets a new scene by using the MetIma_GalleryPage.fxml file.
   */
  @FXML
  private void viewGallery(ActionEvent event) {
    try {
      MetImaApplication.getScene().setRoot(
              FXMLLoader.load(getClass().getResource("MetIma_GalleryPage.fxml")));
    } catch (IOException exception) {
      logger.error("File not found", exception);
    }
  }


  /**
   * This method handles what happens when the view gallery button is pressed.
   * The button sets a new scene by using the MetIma_AddImagePage.fxml file.
   */
  @FXML
  private void addImage(ActionEvent event) {
    try {
      MetImaApplication.getScene().setRoot(
              FXMLLoader.load(getClass().getResource("MetIma_AddImagePage.fxml")));
    } catch (IOException exception) {
      logger.error("File not found", exception);
    }
  }

}
