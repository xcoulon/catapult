package org.rhd.katapult.github.api;

/**
 * Value object representing a repository in GitHub
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public interface GitHubRepository {

    /**
     * Obtains the "git" protocol transport URL
     *
     * @return
     */
    String getGitTransportUrl();

    /**
     * Obtains the "http" protocol transport URL
     *
     * @return
     */
    String gitHttpTransportUrl();

    /**
     * Obtains the repository name (which does not include the owner)
     *
     * @return
     */
    String getName();

    /**
     * Obtains the full repository name in form "owner/repoName"
     *
     * @return
     */
    String getFullName();
}
