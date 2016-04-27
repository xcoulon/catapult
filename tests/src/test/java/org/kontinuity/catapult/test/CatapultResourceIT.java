package org.kontinuity.catapult.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kontinuity.catapult.service.openshift.api.OpenShiftService;
import org.kontinuity.catapult.service.openshift.api.OpenShiftSettings;
import org.kontinuity.catapult.service.openshift.impl.OpenShiftProjectImpl;
import org.kontinuity.catapult.service.openshift.spi.OpenShiftServiceSpi;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Validation of the {@link org.kontinuity.catapult.web.api.CatapultResource}
 */
@RunWith(Arquillian.class)
public class CatapultResourceIT {

   private static final Logger log = Logger.getLogger(CatapultResourceIT.class.getName());

   /*
    Contracts (define here; do NOT link back to where these are defined in runtime code;
    if the runtime code changes that's a contract break)
    */
   private static final String PATH_FLING = "api/catapult/fling";

   /**
    * Name of the repo on GitHub to fork into our user namespace
    */
   private static final String SOURCE_REPO = "jboss-developer/jboss-eap-quickstarts";

   /**
    * Deploy the catapult.war as built
    *
    * @return
    */
   @Deployment(name = "real", testable = false)
   public static WebArchive createDeployment() {
      return Deployments.getMavenBuiltWar();
   }

   /**
    * Test hooks so we can do some cleanup
    *
    * @return
    */
   @Deployment(name = "test")
   public static WebArchive testDeployment() {
      final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
              .addPackages(
                      true,
                      OpenShiftServiceSpi.class.getPackage(),
                      OpenShiftProjectImpl.class.getPackage())
              .addClass(WebDriverProviderHack.class);
      final File[] deps = Resolvers.use(MavenResolverSystem.class).
              loadPomFromFile("../services/openshift-service-impl/pom.xml").
              importRuntimeAndTestDependencies().
              resolve().
              withTransitivity().
              asFile();
      final File[] ourTestDeps = Resolvers.use(MavenResolverSystem.class).
              loadPomFromFile("pom.xml").
              importTestDependencies().
              resolve().
              withTransitivity().
              asFile();
      archive.addAsLibraries(deps);
      archive.addAsLibraries(ourTestDeps);
      log.info(archive.toString(true));
      return archive;
   }

   @ArquillianResource
   private URL deploymentUrl;

   @Inject
   private OpenShiftService service;

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

   @Test
   @RunAsClient
   @InSequence(1)
   @OperateOnDeployment("real")
   public void shouldFling() throws IOException {

      // Define the request URL
      final String flingUrl = deploymentUrl.toExternalForm() + PATH_FLING +
              "?source_repo=" +
              SOURCE_REPO;
      log.info("Request URL: " + flingUrl);

      // Execute the Fling URL which should perform all actions and dump us on the return page
      final WebDriver driver = WebDriverProviderHack.getWebDriver();
      GitHubResourceIT.performGitHubOAuth(
              driver,
              flingUrl);

      // Ensure we land at *some* OpenShift console page until we can test for the
      // project overview page
      //TODO https://github.com/openshift/origin-web-console/issues/50
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

   /**
    * Not really a test, but abusing the test model to take advantage
    * of a test-only deployment to help us do some cleanup.  Contains no assertions
    * intentionally.
    */
   @Test
   @InSequence(3)
   @OperateOnDeployment("test")
   public void cleanup() {
      final String project = SOURCE_REPO.substring(SOURCE_REPO.lastIndexOf('/') + 1);
      final boolean deleted = ((OpenShiftServiceSpi) service).
              deleteProject(project);
      log.info("Deleted OpenShift project \"" +
              project + "\" as part of cleanup: " + deleted);
   }
}