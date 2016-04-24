package org.kontinuity.catapult.core.impl;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kontinuity.catapult.core.api.Boom;
import org.kontinuity.catapult.core.api.Catapult;
import org.kontinuity.catapult.core.api.Projectile;
import org.kontinuity.catapult.core.api.ProjectileBuilder;
import org.kontinuity.catapult.service.github.api.GitHubRepository;
import org.kontinuity.catapult.service.github.impl.kohsuke.GitHubServiceProducer;
import org.kontinuity.catapult.service.openshift.api.OpenShiftProject;
import org.kontinuity.catapult.service.openshift.api.OpenShiftService;
import org.kontinuity.catapult.service.openshift.api.OpenShiftServiceFactory;
import org.kontinuity.catapult.service.openshift.api.OpenShiftUrl;
import org.kontinuity.catapult.service.openshift.spi.OpenShiftServiceSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Test cases for the {@link org.kontinuity.catapult.core.api.Catapult}
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class CatapultIT {

    private static final Logger log = Logger.getLogger(CatapultIT.class.getName());
    private static final String NAME_GITHUB_SOURCE_REPO = "jboss-developer/jboss-eap-quickstarts";
    private static final Collection<String> openshiftProjectsToDelete = new ArrayList<>();

    private static Catapult catapult;

    @BeforeClass
    public static void init() {
        catapult = new TestCatapult();
    }

    @AfterClass
    public static void cleanup() {

        final OpenShiftService service = OpenShiftServiceFactory.INSTANCE.create(OpenShiftUrl.get());

        openshiftProjectsToDelete.forEach(projectName -> {
            final boolean deleted = ((OpenShiftServiceSpi) service).deleteProject(projectName);
            if (deleted) {
                log.info("Deleted project: " + projectName);
            }
        });
    }

    @Test
    public void fling() {

        // Define the projectile
        //TODO Inject the GitHubServiceProducer
        final Projectile projectile = ProjectileBuilder.newInstance().
                gitHubAccessToken(new GitHubServiceProducer().getGitHubToken()).
                sourceGitHubRepo(NAME_GITHUB_SOURCE_REPO).build();

        // Fling
        final Boom boom = catapult.fling(projectile);

        // Assertions
        final GitHubRepository createdRepo = boom.getCreatedRepository();
        Assert.assertNotNull("repo can not be null", createdRepo);
        final OpenShiftProject createdProject = boom.getCreatedProject();
        Assert.assertNotNull("project can not be null", createdProject);
        final String expectedName = NAME_GITHUB_SOURCE_REPO.substring(
                NAME_GITHUB_SOURCE_REPO.lastIndexOf('/') + 1,
                NAME_GITHUB_SOURCE_REPO.length());
        final String foundName = createdProject.getName();
        log.info("Created OpenShift project: " + foundName);
        openshiftProjectsToDelete.add(foundName);
        Assert.assertEquals(expectedName, foundName);
        /*
           Can't really assert on any of the properties of the
           new repo because they could change in GitHub and
           break our tests
         */
    }

    /**
     * Concrete implementation used in testing; will eventually be
     * refactored to use a managed component
     * TODO: https://github.com/redhat-kontinuity/catapult/issues/49
     */
    private static class TestCatapult extends CatapultBase implements Catapult {

    }


}
