package org.kontinuity.catapult.core.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

   /** the name of OpenShift project to create. */
   private String openShiftProjectName;

   /** the path to the file in the repo that contains the pipeline template. */
   private String pipelineTemplatePath;

   private String gitRef;

   private static final Pattern REPO_PATTERN = Pattern.compile("^[a-zA-Z_0-9\\-]+/[a-zA-Z_0-9\\-]+");

    private ProjectileBuilder(){
        // No external instances
    }

    /**
     * Creates and returns a new instance with uninitialized values
     * @return a new instance of the {@link ProjectileBuilder}
     */
    public static ProjectileBuilder newInstance(){
        return new ProjectileBuilder();
    }

    /**
     * Creates and returns a new {@link Projectile} instance based on the
     * state of this builder; if any preconditions like missing properties
     * or improper values exist, an {@link IllegalStateException} will be thrown
     *
     * @return the created {@link Projectile}
     * @throws IllegalStateException
     */
    public Projectile build() throws IllegalStateException {
       // Precondition checks
       ProjectileBuilder.checkSpecified("sourceGitHubRepo", this.sourceGitHubRepo);
       final Matcher matcher = REPO_PATTERN.matcher(sourceGitHubRepo);
       if(!matcher.matches()) {
          throw new IllegalStateException("source repo must be in form \"owner/repoName\"");
       }
       ProjectileBuilder.checkSpecified("gitHubAccessToken", this.gitHubAccessToken);
       ProjectileBuilder.checkSpecified("pipelineTemplatePath", this.pipelineTemplatePath);
       ProjectileBuilder.checkSpecified("girRef", this.gitRef);

       // Default the openshiftProjectName if need be
       try{
          ProjectileBuilder.checkSpecified("openshiftProjectName", this.openShiftProjectName);
       }
       catch(final IllegalStateException ise){
          final String  sourceGitHubRepo = this.getSourceGitHubRepo();
          final String targetProjectName = this.getSourceGitHubRepo().substring(
                  sourceGitHubRepo.lastIndexOf('/')+1);
          this.openShiftProjectName(targetProjectName);
       }

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
    private static void checkSpecified(final String name,
                                final String value) throws IllegalStateException {
        assert name != null && !name.isEmpty() : "name is required";
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(name + " must be specified");
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
    public ProjectileBuilder sourceGitHubRepo(final String sourceGitHubRepo){
        this.sourceGitHubRepo = sourceGitHubRepo;
        return this;
    }
    
    /**
     * Sets the GitHub access token we have obtained from the user as part of
     * the OAuth process. Required.
     *
     * @param gitHubAccessToken
     * @return This builder
     */
    public ProjectileBuilder gitHubAccessToken(final String gitHubAccessToken) {
        this.gitHubAccessToken = gitHubAccessToken;
        return this;
    }

   /**
    * Sets the path to file that contains the template to apply on the
    * OpenShift project. Required.
    *
    * @param pipelineTemplatePath
    * @return This builder
    */
   public ProjectileBuilder pipelineTemplatePath(final String pipelineTemplatePath) {
      this.pipelineTemplatePath = pipelineTemplatePath;
      return this;
   }

   /**
    * Sets the name of the OpenShift project to create. By default, the name is derived from
    * the GitHub repository to fork. Optional.
    * @param openShiftProjectName
    * @return This builder
    */
   public ProjectileBuilder openShiftProjectName(final String openShiftProjectName) {
      this.openShiftProjectName = openShiftProjectName;;
      return this;
   }

   /**
    * Sets Git ref to use. Required
    *
    * @param gitRef
    * @return This builder
    */
   public ProjectileBuilder gitRef(final String gitRef) {
      this.gitRef = gitRef;
      return this;
   }

	/**
     * @return source GitHub repository name in form "owner/repoName".
     */
    public String getSourceGitHubRepo() {
        return this.sourceGitHubRepo;
    }

    /**
     * @return the GitHub access token we have obtained from the user as part of
     * the OAuth process
     */
    public String getGitHubAccessToken() {
        return this.gitHubAccessToken;
    }

   /**
    * @return The name to use in creating the new OpenShift project
    */
   public String getOpenShiftProjectName() { return openShiftProjectName;  }

   /**
    * @return the path to the file that contains the template to apply on the OpenShift project.
    */
   public String getPipelineTemplatePath() { return this.pipelineTemplatePath; }

   /**
    * @return The Git reference to use
    */
   public String getGitRef() { return gitRef; }
}
