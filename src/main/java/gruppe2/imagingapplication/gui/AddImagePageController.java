package gruppe2.imagingapplication.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddImagePageController implements Initializable {
  private Map<Integer, File> chosenImages = new HashMap<>();
  private Logger logger = LoggerFactory.getLogger(AddImagePageController.class);

  @FXML
  private VBox entryContainer;


  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    FileChooser fileChooser = new FileChooser();

    // let user choose files
    List<File> chosenFiles = fileChooser.showOpenMultipleDialog(new Stage());

    // check for image files and associate image file with an identifier in chosenImages HashMap
    int identifier = 0;
    for (File file : chosenFiles) {
      try {
        // try to read image file
        if (ImageIO.read(new File(file.getAbsolutePath())) != null) {
          chosenImages.put(identifier, file);
          identifier++;
        } else {
          logger.info("File is not an image ({})", file.getName());
        }
      } catch (IOException e) {
        logger.error("File doesn't exist", e);
      }
    }

    chosenImages.forEach((imageIdentifier, imageFile) -> entryContainer.getChildren()
          .add(createListEntry(imageFile.getAbsolutePath(), imageIdentifier))
    );

    // populate name text boxes with suggested name from file
    chosenImages.forEach((imageIdentifier, imageFile) -> {
      TextField txtEntryName = (TextField) entryContainer.lookup("#txtEntryName" + imageIdentifier);
      txtEntryName.setText(imageFile.getName());
    });
  }

  /**
   * Method for creating an HBox that represents the entries of chosen images.
   * The method changes node fx:id to associate entry with and image and it's identifier,
   * and configures image for the preview.
   * @param imagePath The filepath of the image
   * @param identifier The identifier for keeping track of images and entries
   * @return Generated HBox
   */
  private HBox createListEntry(String imagePath, int identifier) {
    HBox entry = null;

    // load fxml for HBox
    try {
      entry = FXMLLoader.load(getClass().getResource("AddImagePage_Entry.fxml"));
    } catch (IOException exception) {
      logger.error("File not found", exception);
    }

    // get nodes by default id and add identifier at the end for later reference
    try {
      entry.setId("entry" + identifier);
    } catch (NullPointerException e) {
      // this should never happen :-)
      logger.error("Entry object invalid", e);
    }
    entry.lookup("#txtEntryName").setId("txtEntryName" + identifier);
    entry.lookup("#txtEntryTags").setId("txtEntryTags" + identifier);

    // get remove button, set onaction and new id
    Button btnEntryRemove = (Button) entry.lookup("#btnEntryRemove");
    btnEntryRemove.setId(String.valueOf("btnEntryRemove" + identifier));
    btnEntryRemove.setOnAction(this::btnEntryRemove);

    // set image for preview
    ImageView previewImage = (ImageView) entry.lookup("#imgEntryPreview");
    previewImage.setImage(new Image("file:" + imagePath, 200, 0, true, true));

    return entry;
  }

  /**
   * Method for removing image entries from both view and chosenImages HashMap.
   * @param event Event from button click
   */
  private void btnEntryRemove(ActionEvent event) {
    // get button clicked
    Button btnEntryRemove = (Button) event.getSource();

    // extract number from id
    int entryIdentifier = Integer.parseInt(btnEntryRemove.getId().replaceAll("[^0-9]", ""));
    // remove image and entry
    entryContainer.getChildren().remove(entryContainer.lookup("#entry" + entryIdentifier));
    chosenImages.remove(entryIdentifier);

    logger.info("Removed entry associated with identifier {}", entryIdentifier);
  }

  /**
   * This method handles what happens when the home button is pressed.
   * The button sets a new scene by using the MetIma_HomePage.fxml file.
   *
   * @param event Represents the action when the button is pressed
   */
  @FXML
  private void btnHome(ActionEvent event) {
    try {
      MetImaApplication.getScene()
          .setRoot(FXMLLoader.load(getClass()
              .getResource("MetIma_HomePage.fxml")));
    } catch (IOException exception) {
      logger.error("File not found", exception);
    }
  }

  /**
   * This method handles what happens when the gallery button is pressed.
   * The button sets a new scene by using the MetIma_GalleryPage.fxml file.
   *
   * @param event Represents the action when the button is pressed
   */
  @FXML
  private void btnGallery(ActionEvent event) {
    try {
      MetImaApplication.getScene()
          .setRoot(FXMLLoader.load(getClass()
              .getResource("MetIma_GalleryPage.fxml")));
    } catch (IOException exception) {
      logger.error("File not found", exception);
    }
  }

  /**
   * Method for adding all images in chosenImages to HashMap and changing scene to galleryPage.
   * @param actionEvent Event from button click
   */
  @FXML
  public void btnAddToGallery(ActionEvent actionEvent) {
    if (!chosenImages.isEmpty()) {
      chosenImages.forEach((imageIdentifier, imageFile) -> {
        // get tags, pass null to ContentManager if tags are empty
        TextField txtEntryTags = (TextField) entryContainer.lookup("#txtEntryTags" + imageIdentifier);
        List<String> tags = null;
        if (!txtEntryTags.getText().isEmpty()) {
          tags = Arrays.asList(txtEntryTags.getText()
              .split("\\s*,\\s*"));
        }

        // get image name, field is filled out with recommended image name
        TextField txtEntryName = (TextField) entryContainer.lookup("#txtEntryName" + imageIdentifier);

        MetImaApplication.getContentManager()
            .addImageToDB(imageFile.getAbsolutePath(), tags, txtEntryName.getText());
      });
    }
    // switch to gallery view after adding all images to gallery
    try {
      MetImaApplication.getScene()
          .setRoot(FXMLLoader.load(getClass()
              .getResource("MetIma_GalleryPage.fxml")));
    } catch (IOException exception) {
      logger.error("File not found", exception);
    }
  }
}