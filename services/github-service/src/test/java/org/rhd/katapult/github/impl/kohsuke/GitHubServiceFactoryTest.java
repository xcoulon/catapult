package org.rhd.katapult.github.impl.kohsuke;

import org.junit.Assert;
import org.junit.Test;
import org.rhd.katapult.github.api.GitHubService;
import org.rhd.katapult.github.api.GitHubServiceFactory;

/**
 * Tests for the {@link GitHubServiceFactory}
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class GitHubServiceFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void usernameCannotBeEmpty() {
        GitHubServiceFactory.INSTANCE.create("", "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void usernameCannotBeNull() {
        GitHubServiceFactory.INSTANCE.create(null, "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tokenCannotBeEmpty() {
        GitHubServiceFactory.INSTANCE.create("test", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void tokenCannotBeNull() {
        GitHubServiceFactory.INSTANCE.create("test", null);
    }

    @Test
    public void createsInstance() {
        final GitHubService service = GitHubServiceFactory.INSTANCE.create("test", "test");
        Assert.assertNotNull("instance was not created", service);
    }


}
