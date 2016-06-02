package org.kontinuity.catapult.test;

import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kontinuity.catapult.service.openshift.api.OpenShiftService;
import org.kontinuity.catapult.service.openshift.api.OpenShiftSettings;
import org.kontinuity.catapult.service.openshift.impl.OpenShiftProjectImpl;
import org.kontinuity.catapult.service.openshift.spi.OpenShiftServiceSpi;
import org.openqa.selenium.*;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * Base class for building integration tests for the Catapult; deploys both the real
 * WAR as well as a test deployment to do cleanupCreatedProject when done
 */
abstract class CatapultITBase {

   private static final Logger log = Logger.getLogger(CatapultITBase.class.getName());

   /*
    Contracts (define here; do NOT link back to where these are defined in runtime code;
    if the runtime code changes that's a contract break)
    */
   protected static final String PATH_FLING = "api/catapult/fling";

   @ArquillianResource
   private URL deploymentUrl;

   URL getDeploymentUrl(){
      return deploymentUrl;
   }

   @Inject
   private OpenShiftService service;

   // We don't let Drone inject this because we manually-specify the version
   WebDriver driver;

   @Before
   public void createWebDriver(){
      this.driver = WebDriverProviderHack.getWebDriver();
   }

   @After
   public void closeWebDriver(){
      this.driver.close();
      this.driver.quit();
   }

   /**
    * Ensures that the "source_repo" query param is specified in the "fling"
    * endpoint
    *
    * @throws IOException
    */
   @Test
   @RunAsClient
   @InSequence(0)
   @OperateOnDeployment("real")
   public void sourceRepoIsRequired() throws IOException {
      // Try to auth but don't pass a source_repo as a query param
      final String authUrl = deploymentUrl.toExternalForm() + PATH_FLING;
      TestSupport.assertHttpClientErrorStatus(
              authUrl,
              400,
              "Was expecting an HTTP status code of 400");
   }

   /**
    * Not really a test, but abusing the test model to take advantage
    * of a test-only deployment to help us do some cleanup.  Contains no assertions
    * intentionally.
    */
   @Test
   @InSequence(2)
   @OperateOnDeployment("test")
   public void cleanupCreatedProject() {
      final String sourceRepo = this.getSourceRepo();
      final String project = sourceRepo.substring(sourceRepo.lastIndexOf('/') + 1);
      final boolean deleted = ((OpenShiftServiceSpi) service).
              deleteProject(project);
      log.info("Deleted OpenShift project \"" +
              project + "\" as part of cleanup: " + deleted);
   }

   /**
    * Defines the source repository used for this test
    * @return
    */
   abstract String getSourceRepo();

   protected void assertLanding(final WebDriver driver){
      assert driver!=null:"driver must be specified";

      // Ensure we land at *some* OpenShift console page until we can test for the
      // project overview page reliably
      //TODO https://github.com/redhat-kontinuity/catapult/issues/106
      final String currentUrl = driver.getCurrentUrl();
      log.info("Ended up at: " + currentUrl);
      Assert.assertTrue(currentUrl.startsWith(OpenShiftSettings.getOpenShiftUrl()));

      /*

      // Follow GitHub OAuth, then log into OpenShift console, and land at the
      // project overview page.
      log.info("Current URL Before Login: " + driver.getCurrentUrl());
      log.info(driver.getPageSource());

      final By inputUserName = By.id("inputUsername");
      final WebDriverWait blocker = new WebDriverWait(driver, 3);
      blocker.until(ExpectedConditions.presenceOfElementLocated(inputUserName));
      final WebElement loginField = driver.findElement(inputUserName);
      final WebElement passwordField = driver.findElement(By.id("inputPassword"));
      final WebElement logInButton = driver.findElement(By.xpath("//button[@type='submit']"));
      loginField.sendKeys("admin");
      passwordField.sendKeys("admin");
      logInButton.click();

      // Ensure we're at the Console overview page for the project
      log.info("Current URL: " + driver.getCurrentUrl());
      log.info("Current Title: " + driver.getTitle());
      Assert.assertTrue(driver.getCurrentUrl().endsWith(
              "console/project/" +
                      SOURCE_REPO.substring(SOURCE_REPO.lastIndexOf('/')) +
                      "/overview"));
      Assert.assertEquals("OpenShift Web Console", driver.getTitle());
      */
   }
}