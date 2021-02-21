package gruppe2.imagingapplication;

import com.drew.imaging.ImageProcessingException;
import gruppe2.imagingapplication.gui.MetImaApplication;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ContentManager {
  private EntityManagerFactory entityManagerFactory;
  Logger logger = LoggerFactory.getLogger(ContentManager.class);
  /**
   * A local HashMap for storing the ImageData class locally
   */
  private Map<String, ImageData> images;
  private Map<String, ImageData> searchResults;

  /**
   * Constructs a new content manager object.
   */
  public ContentManager() {
    images = new HashMap<>();
    searchResults = new HashMap<>();
    entityManagerFactory = Persistence.createEntityManagerFactory("hahalocal");
    readFromDB();
  }

  /**
   * This method reads the image data from the database, and puts them
   * in the images HashMap.
   */
  public void readFromDB() {
    List<ImageData> imageDataList;
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    String jdbcQuery = "SELECT image FROM ImageData image";
    Query databaseQuery = entityManager.createQuery(jdbcQuery);
    imageDataList = databaseQuery.getResultList();
    for (ImageData imageData : imageDataList) {
      if(new Image("file:" + imageData.getPath(), 1, 0, false, false).isError()) {
        logger.info("Image does not exist at the path");
      } else {
        images.put(imageData.getPath(), imageData);
      }
    }
  }

  /**
   * Returns the images field.
   *
   * @return Returns it as HashMap<>
   */
  public Map<String, ImageData> getImages() {
    return images;
  }

  /**
   * Method for adding images to the DB with it's path.
   *
   * @param absolutePath The absolute path of the image to add
   * @param tags         User-defined tags to describe image, set null for no tags
   */
  public void addImageToDB(String absolutePath, List<String> tags, String name) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      ImageData image = new ImageData(absolutePath, tags, name);
      entityManager.getTransaction().begin();
      entityManager.merge(image);
      entityManager.flush();
      entityManager.getTransaction().commit();
      images.put(image.getPath(), image);
    } catch (ImageProcessingException e) {
      logger.error("Not and image file", e);
    } catch (IOException e) {
      logger.error("Could not find file", e);
    } finally {
      entityManager.close();
    }
  }

  /**
   * Method for editing an existing image.
   *
   * @param absolutePath The absolute path of the image you want to edit
   * @param name         The new filename
   */
  public void editDatabaseFilename(String absolutePath, String name) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      ImageData image = MetImaApplication.getContentManager().getImages().get(absolutePath);
      image.setImageName(name);
      entityManager.getTransaction().begin();
      entityManager.merge(image);
      entityManager.flush();
      entityManager.getTransaction().commit();
      images.put(image.getPath(), image);
    } finally {
      entityManager.close();
    }
  }

  /**
   * Method for editing the tags of an existing image.
   *
   * @param absolutePath The absolute path of the image you want to edit
   * @param tags         A list of the new tags
   */
  public void editDatabaseTags(String absolutePath, List<String> tags) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      ImageData image = MetImaApplication.getContentManager().getImages().get(absolutePath);
      image.setTags(tags);
      entityManager.getTransaction().begin();
      entityManager.merge(image);
      entityManager.flush();
      entityManager.getTransaction().commit();
      images.put(image.getPath(), image);
    } finally {
      entityManager.close();
    }
  }

  /**
   * Removes an image from images by using the image path.
   *
   * @param path The path of the image as a String
   */
  public void removeImage(String path) {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    try {
      logger.info("Removed image: {}", path);
      entityManager.getTransaction().begin();
      entityManager.remove(entityManager.find(ImageData.class, path));
      entityManager.getTransaction().commit();
      images.remove(path);
    } finally {
      entityManager.close();
    }
  }

  /**
   * Method that combines tagSearch and imageNameSearch and searches for both.
   *
   * @param searchTerm String to search for
   * @return Matching results in a HashMap
   */
  public Map<String, ImageData> fullSearch(String searchTerm) {
    Map<String, ImageData> results = new HashMap<>();

    tagSearch(searchTerm).forEach(results::put);
    imageNameSearch(searchTerm).forEach(results::put);
    metadataSearch(searchTerm).forEach(results::put);

    return results;
  }

  /**
   * Method that searches by tags and returns a hashmap with all matches.
   *
   * @param searchTerm String to search for
   * @return Matching results in a HashMap
   */
  private Map<String, ImageData> tagSearch(String searchTerm) {
    Map<String, ImageData> gallery = MetImaApplication.getContentManager().getImages();
    Map<String, ImageData> results = new HashMap<>();

    gallery.forEach((String key, ImageData image) ->
        image.getTags().forEach(tag -> {
          if (tag.toLowerCase().contains(searchTerm)) {
            results.put(key, image);
          }
        }));

    return results;
  }

  /**
   * Method that searches by image names and returns a hashmap with all matches.
   *
   * @param searchTerm String to search for
   * @return Matching results in a HashMap
   */
  private Map<String, ImageData> imageNameSearch(String searchTerm) {
    Map<String, ImageData> gallery = MetImaApplication.getContentManager().getImages();
    Map<String, ImageData> results = new HashMap<>();

    gallery.values().forEach((ImageData image) -> {
      if (image.getImageName().toLowerCase().contains(searchTerm)) {
        results.put(image.getPath(), image);
      }
    });

    return results;
  }

  /**
   * Method that searches by image names and returns a Map with all matches.
   * @param searchTerm String to search for
   * @return Matching results in a Map
   */
  private Map<String, ImageData> metadataSearch(String searchTerm){
    Map<String, ImageData> gallery = MetImaApplication.getContentManager().getImages();
    Map<String, ImageData> results = new HashMap<>();

    gallery.values().forEach((ImageData image) -> {
      if (image.getMetadata().toString().toLowerCase().contains(searchTerm)){
        results.put(image.getPath(), image);
      }
    });
    return results;
  }

  /**
   * Takes a search term and performs a search.
   *
   * @param searchTerm Takes a search term as String
   */
  public void performSearch(String searchTerm) {
    this.searchResults = fullSearch(searchTerm);
  }

  /**
   * Returns the search result.
   *
   * @return Returns the result as HashMap<>
   */
  public Map<String, ImageData> getSearchResults() {
    return searchResults;
  }
}
