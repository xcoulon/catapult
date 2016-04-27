package org.kontinuity.catapult.service.openshift.api;

import java.net.URL;

/**
 * Represents a Project in OpenShift
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public interface OpenShiftProject {

    /**
     * @return the name of this project
     */
    String getName();

   /**
    * @return the URL of the console overview page for this project
    */
   URL getConsoleOverviewUrl();

}
