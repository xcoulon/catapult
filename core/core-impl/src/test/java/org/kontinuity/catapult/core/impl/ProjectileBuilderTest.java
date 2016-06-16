package org.kontinuity.catapult.core.impl;

import org.junit.Assert;
import org.junit.Test;
import org.kontinuity.catapult.core.api.Projectile;
import org.kontinuity.catapult.core.api.ProjectileBuilder;

/**
 * Test cases to ensure the {@link org.kontinuity.catapult.core.api.ProjectileBuilder}
 * is working as contracted
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class ProjectileBuilderTest {

   private static final String SOME_VALUE = "test";
   private static final String EMPTY = "";

	@Test(expected = IllegalStateException.class)
	public void requiresSourceGitHubRepo() {
		ProjectileBuilder.newInstance().gitHubAccessToken(SOME_VALUE).build();
	}

	@Test(expected = IllegalStateException.class)
	public void requiresGitHubAccessToken() {
		ProjectileBuilder.newInstance().sourceGitHubRepo(SOME_VALUE).build();
	}

	@Test(expected = IllegalStateException.class)
	public void requiresSourceGitHubRepoNotEmpty() {
		ProjectileBuilder.newInstance().gitHubAccessToken(SOME_VALUE).sourceGitHubRepo(EMPTY).build();
	}

	@Test(expected = IllegalStateException.class)
	public void requiresGitHubAccessTokenNotEmpty() {
		ProjectileBuilder.newInstance().sourceGitHubRepo(SOME_VALUE).gitHubAccessToken(EMPTY).build();
	}

	@Test
	public void createsInstanceWithSimpleRepoName() {
		final Projectile projectile = ProjectileBuilder.newInstance().sourceGitHubRepo("https://github.com/foo/bar")
		        .gitHubAccessToken(SOME_VALUE).build();
		Assert.assertNotNull(projectile);
		Assert.assertEquals(projectile.getOpenShiftProjectName(), "bar");
	}

	@Test
	public void createsInstanceWithFullRepoName() {
		final Projectile projectile = ProjectileBuilder.newInstance().sourceGitHubRepo("https://github.com/foo/bar.git")
				.gitHubAccessToken(SOME_VALUE).build();
		Assert.assertNotNull(projectile);
		Assert.assertEquals("bar", projectile.getOpenShiftProjectName());
	}

	@Test
	public void createsInstanceWithCustomRepoName() {
		final Projectile projectile = ProjectileBuilder.newInstance().sourceGitHubRepo("https://github.com/foo/bar.git")
				.gitHubAccessToken(SOME_VALUE).openShiftProjectName("BAZ").build();
		Assert.assertNotNull(projectile);
		Assert.assertEquals(projectile.getOpenShiftProjectName(), "BAZ");
	}
	
}
