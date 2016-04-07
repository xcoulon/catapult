package org.kontinuity.catapult.service.openshift.impl;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kontinuity.catapult.service.openshift.api.DuplicateProjectException;
import org.kontinuity.catapult.service.openshift.api.OpenShiftProject;
import org.kontinuity.catapult.service.openshift.api.OpenShiftService;
import org.kontinuity.catapult.service.openshift.api.OpenShiftServiceFactory;
import org.kontinuity.catapult.service.openshift.spi.OpenShiftServiceSpi;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.kontinuity.catapult.service.openshift.api.OpenShiftEnvVars.OPENSHIFT_PROJECT;
import static org.kontinuity.catapult.service.openshift.api.OpenShiftEnvVars.OPENSHIFT_URL;

/**
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public class OpenShiftServiceIT {

    private static final Logger log = Logger.getLogger(OpenShiftServiceIT.class.getName());

    private static final String DEFAULT_OPENSHIFT_URL = "https://localhost:8443";
    private static final String PREFIX_NAME_PROJECT = "test-project-";
    private static final String TEST_OPENSHIFT_URL = "https://katapult-it-test:8443";

    private static final Collection<OpenShiftProject> createdProjects = new ArrayList<>();

    private static OpenShiftService service;

    @BeforeClass
    public static void createService() {
        service = OpenShiftServiceFactory.INSTANCE.create(getOpenShiftUrl());
    }
    
    private static String getOpenShiftUrl() {
    	//if (isSystemEnvSet(OPENSHIFT_PROJECT)) {
    		// This one is tough, there doesn't seem to be an env var for master
    	//} else 
		if (isSystemEnvSet(OPENSHIFT_URL)) {
    		return System.getenv(OPENSHIFT_URL);
    	} else if (isSystemPropertySet(OPENSHIFT_URL)) {
    		return System.getProperty(OPENSHIFT_URL);
    	} else return DEFAULT_OPENSHIFT_URL;
    }
    
    private static boolean isSystemEnvSet(String name) {
    	String val = System.getenv(name);
    	return val != null && !val.isEmpty();
    }
    
    private static boolean isSystemPropertySet(String name) {
    	String val = System.getProperty(name);
    	return val != null && !val.isEmpty();
    }

    @AfterClass
    public static void deleteCreatedProjects() {
        createdProjects.forEach(project -> {
            final String projectName = project.getName();
            ((OpenShiftServiceSpi) service).deleteProject(project);
            log.info("Deleted " + projectName);
        });
    }

    @Test
    public void createProject() {
        final String projectName = PREFIX_NAME_PROJECT + System.currentTimeMillis();
        final OpenShiftProject project = service.createProject(projectName);
        final String name = project.getName();
        log.log(Level.INFO, "Created project: \'" + name + "\'");
        createdProjects.add(project);
        Assert.assertEquals("returned project did not have expected name", projectName, name);
    }

    @Test(expected = DuplicateProjectException.class)
    public void duplicateProjectNameShouldFail() {
        final String projectName = PREFIX_NAME_PROJECT + System.currentTimeMillis();
        final OpenShiftProject project = service.createProject(projectName);
        createdProjects.add(project);
        final String name = project.getName();
        service.createProject(name); // Using same name should fail with DPE here
        // Just in case the above doesn't fail
        createdProjects.add(project);
    }
    
    @Test
    public void openShiftUrl() {
    	/*String oldOpenShiftProjectEnv = System.getenv(OPENSHIFT_PROJECT);
    	try {
    		setEnv(OPENSHIFT_PROJECT, TEST_OPENSHIFT_PROJECT);
    		Assert.assertEquals(getOpenShiftUrl(), TEST_OPENSHIFT_PROJECT);
    		
    	} finally {
    		setEnv(OPENSHIFT_PROJECT, oldOpenShiftProjectEnv);
    	}*/
    	
    	String oldOpenShiftProjectEnv = System.getenv(OPENSHIFT_PROJECT);
    	String oldOpenShiftUrlEnv = System.getenv(OPENSHIFT_URL);
		try {
			setEnv(OPENSHIFT_PROJECT, "");
			setEnv(OPENSHIFT_URL, TEST_OPENSHIFT_URL);
    		Assert.assertEquals(getOpenShiftUrl(), TEST_OPENSHIFT_URL);
    		
		} finally {
			setEnv(OPENSHIFT_URL, oldOpenShiftUrlEnv);
			setEnv(OPENSHIFT_PROJECT, oldOpenShiftProjectEnv);
		}
		
		String oldOpenShiftProperty = System.getProperty(OPENSHIFT_URL);
		oldOpenShiftProjectEnv = System.getenv(OPENSHIFT_PROJECT);
		oldOpenShiftUrlEnv = System.getenv(OPENSHIFT_URL);
		try {
			setEnv(OPENSHIFT_PROJECT, "");
			setEnv(OPENSHIFT_URL, "");
			System.setProperty(OPENSHIFT_URL, TEST_OPENSHIFT_URL);
			Assert.assertEquals(getOpenShiftUrl(), TEST_OPENSHIFT_URL);
			
		} finally {
			if (oldOpenShiftProperty != null) System.setProperty(OPENSHIFT_URL, oldOpenShiftProperty);
			setEnv(OPENSHIFT_URL, oldOpenShiftUrlEnv);
			setEnv(OPENSHIFT_PROJECT, oldOpenShiftProjectEnv);
		}
		
		oldOpenShiftProperty = System.getProperty(OPENSHIFT_URL);
		oldOpenShiftProjectEnv = System.getenv(OPENSHIFT_PROJECT);
		oldOpenShiftUrlEnv = System.getenv(OPENSHIFT_URL);
		try {
			setEnv(OPENSHIFT_PROJECT, "");
			setEnv(OPENSHIFT_URL, "");
			System.setProperty(OPENSHIFT_URL, "");
			Assert.assertEquals(getOpenShiftUrl(), DEFAULT_OPENSHIFT_URL);
			
		} finally {
			System.setProperty(OPENSHIFT_URL, oldOpenShiftProperty);
			setEnv(OPENSHIFT_URL, oldOpenShiftUrlEnv);
			setEnv(OPENSHIFT_PROJECT, oldOpenShiftProjectEnv);
		}
    }
    
	private static void setEnv(String name, String value) {
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
				e2.printStackTrace();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
    

}
