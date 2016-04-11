package org.kontinuity.catapult.service.openshift.impl;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.kontinuity.catapult.service.openshift.api.OpenShiftEnvVarSysPropNames.OPENSHIFT_URL;
import static org.kontinuity.catapult.service.openshift.api.OpenShiftApiUrl.get;

/**
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class OpenShiftApiUrlTest {

    private static final String TEST_OPENSHIFT_URL = "https://katapult-it-test:8443";
    private static final String DEFAULT_OPENSHIFT_URL = "https://localhost:8443";

    @Test
    public void openShiftUrlFromEnvVar() {
        String oldOpenShiftUrlEnv = System.getenv(OPENSHIFT_URL);
        try {
            setEnv(OPENSHIFT_URL, TEST_OPENSHIFT_URL);
            Assert.assertEquals(TEST_OPENSHIFT_URL, get());

        } finally {
            setEnv(OPENSHIFT_URL, oldOpenShiftUrlEnv);
        }
    }

    @Test
    public void openShiftUrlFromSysPropNoEnvVar() {
        String oldOpenShiftProperty = System.getProperty(OPENSHIFT_URL);
        String oldOpenShiftUrlEnv = System.getenv(OPENSHIFT_URL);
        try {
            setEnv(OPENSHIFT_URL, "");
            System.setProperty(OPENSHIFT_URL, TEST_OPENSHIFT_URL);
            Assert.assertEquals(TEST_OPENSHIFT_URL, get());

        } finally {
            if (oldOpenShiftProperty != null) {
                System.setProperty(OPENSHIFT_URL, oldOpenShiftProperty);
            }
            setEnv(OPENSHIFT_URL, oldOpenShiftUrlEnv);
        }
    }

    @Test
    public void openShiftUrlSysPropOverridesEnvVar() {
        String oldOpenShiftProperty = System.getProperty(OPENSHIFT_URL);
        String oldOpenShiftUrlEnv = System.getenv(OPENSHIFT_URL);
        try {
            setEnv(OPENSHIFT_URL, "shouldBeOverriddenBySysPropValue");
            System.setProperty(OPENSHIFT_URL, TEST_OPENSHIFT_URL);
            Assert.assertEquals(TEST_OPENSHIFT_URL, get());

        } finally {
            if (oldOpenShiftProperty != null) {
                System.setProperty(OPENSHIFT_URL, oldOpenShiftProperty);
            }
            setEnv(OPENSHIFT_URL, oldOpenShiftUrlEnv);
        }
    }

    @Test
    public void openShiftUrlFromDefaultOnNoSysPropOrEnvVar() {
        String oldOpenShiftProperty = System.getProperty(OPENSHIFT_URL);
        String oldOpenShiftUrlEnv = System.getenv(OPENSHIFT_URL);
        try {
            setEnv(OPENSHIFT_URL, "");
            System.setProperty(OPENSHIFT_URL, "");
            Assert.assertEquals(DEFAULT_OPENSHIFT_URL, get());

        } finally {
            if (oldOpenShiftProperty != null && !oldOpenShiftProperty.isEmpty()) {
                System.setProperty(OPENSHIFT_URL, oldOpenShiftProperty);
            }
            setEnv(OPENSHIFT_URL, oldOpenShiftUrlEnv);
        }
    }

    /**
     * Adapted from:
     * http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
     */
    private static void setEnv(final String name, final String value) {
        Map<String, String> newenv = new HashMap<String, String>();
        newenv.put(name, value);
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);

            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass
                    .getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            try {
                Class[] classes = Collections.class.getDeclaredClasses();
                Map<String, String> env = System.getenv();
                for (Class cl : classes) {
                    if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                        Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        Object obj = field.get(env);
                        Map<String, String> map = (Map<String, String>) obj;
                        map.clear();
                        map.putAll(newenv);
                    }
                }
            } catch (Exception e2) {
                //NOOP
            }
        } catch (Exception e1) {
            //NOOP
        }
    }

}
