package gruppe2.imagingapplication.gui;

import gruppe2.imagingapplication.ImageData;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ViewImagePageController implements Initializable {
  @FXML
  private ImageView imageView;
  @FXML
  private Text imageName;
  @FXML
  private Text tags;
  Logger logger = LoggerFactory.getLogger(ViewImagePageController.class);
  private ImageData image;
  private static final String FILE_NOT_FOUND = "File not found";

  @FXML
  private TextFlow textFlow;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    imageView.setImage(new Image("file:" + this.image.getPath(), 1000, 0, true, true));
    imageName.setText("FileName: " + this.image.getImageName());
    StringBuilder tagText = new StringBuilder();
    tagText.append("Tags: ");
    this.image.getTags().forEach(tag -> {
      tagText.append(tag);
      tagText.append(", ");
    });
    if (tagText.length() > 1) {
      tagText.setLength(tagText.length() - 2);
    }
    tags.setText(tagText.toString());


    image.getMetadata().forEach((key, value) -> {
      Text text = new Text();
      text.setText(key + ": " + value + "\n");
      textFlow.getChildren().add(text);
    });
  }

  /**
   * This sets the image you are viewing, it uses a getter to get the correct image.
   *
   * @param path This is the image path
   */
  public void setImage(String path) {
    this.image = MetImaApplication.getContentManager().getImages().get(path);
  }


  /**
   * This method handles the home button it takes the user to the homepage.
   *
   */
  @FXML
  private void btnHome() {
    try {
      MetImaApplication.getScene().setRoot(
          FXMLLoader.load(getClass().getResource("MetIma_HomePage.fxml")));
    } catch (IOException exception) {
      logger.error(FILE_NOT_FOUND, exception);
    }
  }

  /**
   * This method handles the gallery button, it takes the user to the gallery page.
   *
   */
  @FXML
  private void btnGallery() {
    try {
      MetImaApplication.getScene().setRoot(
          FXMLLoader.load(getClass().getResource("MetIma_GalleryPage.fxml")));
    } catch (IOException exception) {
      logger.error(FILE_NOT_FOUND, exception);
    }
  }

  /**
   * This method handles the add image button, it takes you to the add Image page.
   *
   */
  @FXML
  private void btnAddImage() {
    try {
      MetImaApplication.getScene().setRoot(
          FXMLLoader.load(getClass().getResource("MetIma_AddImagePage.fxml")));
    } catch (IOException exception) {
      logger.error(FILE_NOT_FOUND, exception);
    }

  }

  /**
   * This method handles the delete button, it deletes the viewed image.
   *
   */
  @FXML
  private void btnDelete() {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Confirmation Dialog");
    alert.setHeaderText("You are trying to delete an image");
    alert.setContentText("Are you sure you wish to delete?");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      MetImaApplication.getContentManager().removeImage(this.image.getPath());
      try {
        MetImaApplication.getScene().setRoot(
            FXMLLoader.load(getClass().getResource("MetIma_GalleryPage.fxml")));
      } catch (IOException exception) {
        logger.error(FILE_NOT_FOUND, exception);
      }
    } else {
      try {
        MetImaApplication.getScene().setRoot(
            FXMLLoader.load(getClass().getResource("MetIma_GalleryPage.fxml")));
      } catch (IOException exception) {
        logger.error(FILE_NOT_FOUND, exception);
      }
    }

  }

  /**
   * This method handles the edit Filename button, it changes the name of a file.
   *
   */
  @FXML
  private void btnEditFilename() {
    TextInputDialog dialog = new TextInputDialog("");
    dialog.setTitle("Edit filename");
    dialog.setHeaderText("Edit filename");
    dialog.setContentText("Enter new filename:");
    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
      MetImaApplication.getContentManager().editDatabaseFilename(this.image.getPath(), result.get());
      loadViewImage(image.getPath());
    } else {
      loadViewImage(image.getPath());
    }
  }

  /**
   * This method handles the edit Tags button. It takes a list of tags,
   * and sets them as the new tags.
   *
   */
  @FXML
  public void btnEditTags() {
    TextInputDialog dialog = new TextInputDialog("");
    dialog.setTitle("Edit tags");
    dialog.setHeaderText("Edit tags");
    dialog.setContentText("Enter new filename:");
    Optional<String> result = dialog.showAndWait();


    if (result.isPresent()) {
      List<String> newTags = Arrays.asList(result.get()
          .split("\\s*,\\s*"));
      MetImaApplication.getContentManager().editDatabaseTags(this.image.getPath(), newTags);
      loadViewImage(image.getPath());
    } else {
      loadViewImage(image.getPath());
    }
  }

  /**
   * Getter for ImageView.
   *
   * @return ImageView, this shows an image within JavaFX
   */
  public ImageView getImageView() {
    return imageView;
  }

  /**
   * Setter for ImageView.
   *
   * @param imageView This shows an image within JavaFX
   */
  public void setImageView(ImageView imageView) {
    this.imageView = imageView;
  }

  public void loadViewImage(String path) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("MetIma_ViewImagePage.fxml"));
      ViewImagePageController controller = new ViewImagePageController();
      loader.setController(controller);
      controller.setImage(path);
      MetImaApplication.getScene().setRoot(loader.load());
    } catch (IOException exception) {
      logger.error(FILE_NOT_FOUND, exception);
    }
  }
}
