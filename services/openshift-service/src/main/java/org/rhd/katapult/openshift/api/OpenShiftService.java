package org.rhd.katapult.openshift.api;

/**
 * Defines the operations we support with the OpenShift backend
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public interface OpenShiftService {

    /**
     * Creates a project with the specified, required displayName
     *
     * @param displayName
     * @return
     * @throws DuplicateProjectException
     * @throws IllegalArgumentException  If the displayName is not specified
     */
    OpenShiftProject createProject(String displayName) throws DuplicateProjectException,
            IllegalArgumentException;

}
