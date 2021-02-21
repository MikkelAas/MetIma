package gruppe2.imagingapplication;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ImageDataTest {
  private Logger logger = LoggerFactory.getLogger(ImageDataTest.class);

  /**
   * Tests that metadata is extracted from the image.
   * IF there is an error extracting metadata, the hashmap will be empty
   */
  @Test
  public void metadataTests() {
    ImageData testImage = null;
    try {
      // need test imge to work with, this path might be different depending on current working directory
      // works if working directory is project root
      testImage = new ImageData(System.getProperty("user.dir")
          + "/src/test/java/gruppe2/imagingapplication/test_image.png", null, "test_image");
    } catch (Exception e) {
      logger.error("Image file not found", e);
    }

    logger.info("Test 1: Hashmap is populated with data");
    // checks if hashmap has any data inside
    assertFalse(testImage.getMetadata().isEmpty());
  }


  /**
   * Tests for making sure tags are parsed properly
   * Checks if it gets all tags, and that all unique tags are still there
   */
  @Test
  public void tagTests() {
    ImageData testImage = null;
    List<String> tags = Arrays.asList(new String[]{"tags", "are", "cool"});

    try {
      // need test imge to work with, this path might be different depending on current working directory
      // works if working directory is project root
      testImage = new ImageData(System.getProperty("user.dir")
          + "/src/test/java/gruppe2/imagingapplication/test_image.png", tags, "test_image");
    } catch (Exception e) {
      logger.error("Image file not found", e);
    }

    logger.info("Test 2: Object has correct amount of tags");
    assertTrue(testImage.getTags().size() == 3);

    logger.info("Test 3: Object has correct tags");
    assertTrue(testImage.getTags().contains("tags")
        && testImage.getTags().contains("are")
        && testImage.getTags().contains("cool")
    );
  }
}
