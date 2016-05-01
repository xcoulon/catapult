package org.kontinuity.catapult.service.github.test;

/**
 * Used to obtain the GitHub credentials from the environment
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class GitHubTestCredentials {

    private static final String NAME_ENV_VAR_SYSPROP_GITHUB_USERNAME = "GITHUB_USERNAME";
    private static final String NAME_ENV_VAR_SYSPROP_GITHUB_TOKEN = "GITHUB_TOKEN";
    private static final String NAME_ENV_VAR_SYSPROP_GITHUB_PASSWORD = "GITHUB_PASSWORD";

    private GitHubTestCredentials(){
        // No instances
    }

    /**
     * @return the GitHub username
     */
    public static String getUsername(){
        return GitHubTestCredentials.getEnvVarOrSysProp(NAME_ENV_VAR_SYSPROP_GITHUB_USERNAME);
    }

    /**
     * @return the GitHub token
     */
    public static String getToken(){
        return GitHubTestCredentials.getEnvVarOrSysProp(NAME_ENV_VAR_SYSPROP_GITHUB_TOKEN);
    }

    /**
     * @return the GitHub password
     */
    public static String getPassword(){
        return GitHubTestCredentials.getEnvVarOrSysProp(NAME_ENV_VAR_SYSPROP_GITHUB_PASSWORD);
    }

    private static String getEnvVarOrSysProp(final String envVarOrSysProp){
        String value = System.getProperty(envVarOrSysProp);
        if (value == null) {
            value = System.getenv(envVarOrSysProp);
        }
        if (value == null) {
            throw new IllegalStateException("Could not find required env var or sys prop " + envVarOrSysProp);
        }
        return value;
    }

}
