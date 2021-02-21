package gruppe2.imagingapplication;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import javafx.print.Paper;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Export {
  Logger logger = LoggerFactory.getLogger(Export.class);

  /**
   * This method generates a pdf document and adds images to it based on the HashMap you give it.
   * Width of image in pdf will be standard A4 width, and height is fixed to image ratio.
   *
   * @param images Takes a Map that holds the images you wish to export to a pdf document
   */
  public void exportImagesToPdf(Map<String, ImageData> images) {
    // do nothing if there are no images
    if (images.isEmpty()) {
      logger.error("No images in gallery/search result to export...");
      return;
    }

    Document document = new Document();

    try {
      logger.info("Requesting save location for export");
      PdfWriter.getInstance(document, new FileOutputStream(getSaveLocation()));
    } catch (IOException e) {
      logger.info("User didn't choose a save location, cancelling save export operation...");
      return;
    } catch (DocumentException e) {
      logger.error("Error occured when trying to write to file", e);
    }

    document.open();

    images.keySet().forEach(path -> {
      Image image = null;
      try {
        image = Image.getInstance("file:" + path);

        // calculate page size / ratio based on standard A4 page
        Rectangle size = new Rectangle((float) Paper.A4.getWidth(),
                (float) Paper.A4.getWidth() * (image.getPlainHeight() / image.getPlainWidth()));
        document.setPageSize(size);

        // set image properties and position
        image.setAbsolutePosition(0,0);
        image.setBorderWidth(0);
        image.scaleToFit(size);

        // create new page and add image to said page
        document.newPage();
        try {
          document.add(image);
        } catch (DocumentException e) {
          // this should never happen :-P
          logger.error("Document is not opened");
        }
      } catch (BadElementException e) {
        logger.error("Error creating image object");
      } catch (IOException e) {
        logger.error("Error accessing image file");
      }
    });
    try {
      document.close();
    } catch (Exception e) {
      logger.info("document is empty");
    }

  }

  /**
   * Method for getting a path to where the user wants to save the exported document.
   * @return The absolute path where the pdf will be saved
   */
  public String getSaveLocation() throws IOException {
    FileChooser fileChooser = new FileChooser();
    File saveLocation = fileChooser.showSaveDialog(new Stage());

    // throw exception if no save location was chosen
    if (saveLocation == null) {
      throw new IOException("User cancelled during save location selection");
    }

    // checks if the user included file extension when selecting save location
    if (!saveLocation.getAbsolutePath().endsWith(".pdf")) {
      return (saveLocation.getAbsolutePath() + ".pdf");
    } else {
      return (saveLocation.getAbsolutePath());
    }
  }
}
