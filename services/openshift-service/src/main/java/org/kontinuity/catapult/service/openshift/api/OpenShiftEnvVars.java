package org.kontinuity.catapult.service.openshift.api;

/**
 * Contains names of environment variables relating to the OpenShift Service
 */
public interface OpenShiftEnvVars {
	
	static String OPENSHIFT_PROJECT = "OPENSHIFT_BUILD_NAMESPACE";
	static String OPENSHIFT_URL = "KONTINUITY_CATAPULT_OPENSHIFT_URL";

}
