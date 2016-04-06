package org.rhd.katapult.github.impl.kohsuke;

import org.junit.*;
import org.rhd.katapult.github.api.GitHubRepository;
import org.rhd.katapult.github.api.GitHubService;
import org.rhd.katapult.github.api.GitHubServiceFactory;
import org.rhd.katapult.github.api.NoSuchRepositoryException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tests for the {@link GitHubService}
 *
 * Relies on having environment variables set for:
 * GITHUB_USERNAME
 * GITHUB_TOKEN
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class GitHubServiceIT {

    private static final Logger log = Logger.getLogger(GitHubServiceIT.class.getName());
    private static final String ENVVAR_NAME_GITHUB_USERNAME = "GITHUB_USERNAME";
    private static final String ENVVAR_NAME_GITHUB_TOKEN = "GITHUB_TOKEN";
    private static final String NAME_GITHUB_SOURCE_REPO = "jboss-developer/jboss-eap-quickstarts";

    private static String GITHUB_USERNAME;
    private static String GITHUB_PERSONAL_ACCESS_TOKEN;

    private GitHubService gitHubService;

    @BeforeClass
    public static void initGithubCredentials() throws IOException {
        GITHUB_USERNAME = System.getenv(ENVVAR_NAME_GITHUB_USERNAME);
        Assume.assumeNotNull("Could not find env var " + ENVVAR_NAME_GITHUB_USERNAME, GITHUB_USERNAME);
        GITHUB_PERSONAL_ACCESS_TOKEN = System.getenv(ENVVAR_NAME_GITHUB_TOKEN);
        Assume.assumeNotNull("Could not find env var " + ENVVAR_NAME_GITHUB_TOKEN, GITHUB_PERSONAL_ACCESS_TOKEN);
    }

    @Before
    public void initGithubService() {
        gitHubService = GitHubServiceFactory.INSTANCE.create(GITHUB_USERNAME, GITHUB_PERSONAL_ACCESS_TOKEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void forkRepoCannotBeNull() {
        final GitHubRepository targetRepo = gitHubService.fork(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void forkRepoCannotBeEmpty() {
        final GitHubRepository targetRepo = gitHubService.fork("");
    }

    @Test
    public void fork() {
        final GitHubRepository targetRepo = gitHubService.fork(NAME_GITHUB_SOURCE_REPO);
        Assert.assertNotNull("Got null result in forking " + NAME_GITHUB_SOURCE_REPO, targetRepo);
        log.log(Level.INFO, "Forked " + NAME_GITHUB_SOURCE_REPO + " as " + targetRepo.getFullName() +
                " available at " + targetRepo.getGitTransportUrl());
    }

    @Test(expected = NoSuchRepositoryException.class)
    public void cannotForkNonexistentRepo(){
        gitHubService.fork("ALRubinger/someRepoThatDoesNotAndWillNeverExist");
    }

}
