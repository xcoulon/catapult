package org.kontinuity.catapult.service.openshift.api;

import static org.kontinuity.catapult.service.openshift.api.OpenShiftEnvVarSysPropNames.OPENSHIFT_URL;

/**
 * Obtains the OpenShift URL according to precedence (lower number gets priority):
 * <p>
 * 1) System Property {@link org.kontinuity.catapult.service.openshift.api.OpenShiftEnvVarSysPropNames#OPENSHIFT_URL}
 * 2) Environment Variable {@link org.kontinuity.catapult.service.openshift.api.OpenShiftEnvVarSysPropNames#OPENSHIFT_URL}
 * 3) {@link OpenShiftUrl#DEFAULT_OPENSHIFT_URL}
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class OpenShiftUrl {

    /**
     * No instances
     */
    private OpenShiftUrl(){}

    private static final String DEFAULT_OPENSHIFT_URL = "https://localhost:8443";

    public static String get() {
        if (isSystemPropertySet(OPENSHIFT_URL)) {
            return System.getProperty(OPENSHIFT_URL);
        } else if (isEnvVarSet(OPENSHIFT_URL)) {
            return System.getenv(OPENSHIFT_URL);
        }

        return DEFAULT_OPENSHIFT_URL;
    }

    private static boolean isEnvVarSet(final String name) {
        String val = System.getenv(name);
        return val != null && !val.isEmpty();
    }

    private static boolean isSystemPropertySet(final String name) {
        String val = System.getProperty(name);
        return val != null && !val.isEmpty();
    }
}
