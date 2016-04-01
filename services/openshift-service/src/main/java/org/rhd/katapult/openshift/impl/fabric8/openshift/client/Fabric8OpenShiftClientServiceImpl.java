package org.rhd.katapult.openshift.impl.fabric8.openshift.client;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.api.model.ProjectRequest;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.rhd.katapult.openshift.api.DuplicateProjectException;
import org.rhd.katapult.openshift.api.OpenShiftProject;
import org.rhd.katapult.openshift.api.OpenShiftService;
import org.rhd.katapult.openshift.impl.OpenShiftProjectImpl;

/**
 * Implementation of the {@link OpenShiftService} using the Fabric8
 * OpenShift client
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
final class Fabric8OpenShiftClientServiceImpl implements OpenShiftService {

    private final OpenShiftClient client;

    /**
     * Creates an {@link OpenShiftService} implementation communicating
     * with the backend service via the specified, required apiUrl
     *
     * @param apiUrl
     */
    Fabric8OpenShiftClientServiceImpl(final String apiUrl) {
        assert apiUrl != null && !apiUrl.isEmpty() : "apiUrl is required";

        final Config config = new ConfigBuilder().
                withMasterUrl(apiUrl).
                withUsername("admin"). //TODO externalize or account for this?
                withPassword("admin"). // TODO externalize or account for this?
                withTrustCerts(true). //TODO never do this in production as it opens us to man-in-the-middle attacks
                build();
        final OpenShiftClient client = new DefaultOpenShiftClient(config);
        this.client = client;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OpenShiftProject createProject(final String displayName) throws
            DuplicateProjectException,
            IllegalArgumentException {

        // Create
        final ProjectRequest projectRequest = client.projectrequests().createNew().
                withNewMetadata().
                withName(displayName).
                endMetadata().
                done();

        //TODO DuplicateProjectException handling and throwing

        // Populate value object and return it
        final String roundtripDisplayName = projectRequest.getMetadata().getName();
        final OpenShiftProject project = new OpenShiftProjectImpl(roundtripDisplayName);
        return project;
    }
}
