package org.rhd.katapult.openshift.impl.fabric8.openshift.client;

import org.rhd.katapult.openshift.api.OpenShiftService;
import org.rhd.katapult.openshift.api.OpenShiftServiceLoader;

/**
 * Implementation of {@link org.rhd.katapult.openshift.api.OpenShiftServiceLoader}
 * for creating instances of {@link org.rhd.katapult.openshift.api.OpenShiftService}
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public final class Fabric8OpenShiftClientServiceLoader implements OpenShiftServiceLoader {

    public Fabric8OpenShiftClientServiceLoader() {
        //no-arg ctor required per ServiceLoader specification
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OpenShiftService create(final String apiUrl) throws IllegalArgumentException {
        // Precondition checks
        if (apiUrl == null || apiUrl.isEmpty()) {
            throw new IllegalArgumentException("apiUrl is required");
        }

        // Create and return
        final OpenShiftService service = new Fabric8OpenShiftClientServiceImpl(apiUrl);
        return service;
    }
}
