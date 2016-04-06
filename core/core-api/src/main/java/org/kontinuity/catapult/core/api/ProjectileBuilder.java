package org.kontinuity.catapult.core.api;

/**
 * DSL builder for creating {@link Projectile} objects.  Responsible for
 * validating state before calling upon the {@link ProjectileBuilder#build()}
 * operation.  The following properties are required:
 *
 * <ul>
 *     <li>sourceGitHubRepo</li>
 *     <li>gitHubAccessToken</li>
 * </ul>
 *
 * Each property's valid value and purpose is documented in its setter method.
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class ProjectileBuilder {

    private String sourceGitHubRepo;

    private String gitHubAccessToken;

    private ProjectileBuilder(){
        // No external instances
    }

    /**
     * Creates and returns a new instance with uninitialized values
     * @return
     */
    public static ProjectileBuilder newInstance(){
        return new ProjectileBuilder();
    }

    /**
     * Creates and returns a new {@link Projectile} instance based on the
     * state of this builder; if any preconditions like missing properties
     * or improper values exist, an {@link IllegalStateException} will be thrown
     *
     * @return
     * @throws IllegalStateException
     */
    public Projectile build() throws IllegalStateException {

        // Precondition checks
        this.checkSpecified("sourceGitHubRepo", sourceGitHubRepo);
        this.checkSpecified("gitHubAccessToken", gitHubAccessToken);

        // All good, so make a new instance
        final Projectile projectile = new Projectile(this);
        return projectile;
    }

    /**
     * Ensures the specified value is not null or empty, else throws
     * an {@link IllegalArgumentException} citing the specified name
     * (which is also required ;) )
     *
     * @param value
     * @throws IllegalStateException
     */
    private void checkSpecified(final String name,
                                final String value) throws IllegalStateException {
        assert name != null && !name.isEmpty() : "name is required";
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(name + " must be specified");
        }
    }

    /**
     * Builder methods
     */

    /**
     * Sets the source GitHub repository name in form "owner/repoName"; this
     * is what will be forked on behalf of the user.  Required.
     * @param sourceGitHubRepo
     * @return This builder
     */
    public ProjectileBuilder sourceGitHubRepo(final String sourceGitHubRepo) {
        this.sourceGitHubRepo = sourceGitHubRepo;
        return this;
    }

    /**
     * Sets the GitHub access token we have obtained from the user as part of
     * the OAuth process.  Required.
     *
     * @param gitHubAccessToken
     * @return This builder
     */
    public ProjectileBuilder gitHubAccessToken(final String gitHubAccessToken) {
        this.gitHubAccessToken = gitHubAccessToken;
        return this;
    }

    /**
     * Accessors
     */

    public String getSourceGitHubRepo() {
        return sourceGitHubRepo;
    }

    public String getGitHubAccessToken() {
        return gitHubAccessToken;
    }
}
