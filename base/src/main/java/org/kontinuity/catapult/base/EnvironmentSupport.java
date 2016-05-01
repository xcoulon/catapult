package org.kontinuity.catapult.base;

import java.text.MessageFormat;

/**
 * Utility class to read state from the environment
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
public enum EnvironmentSupport {

   INSTANCE;

   private final String MESSAGE_PATTERN = "Could not find required env var or sys prop {0}";

   /**
    * Obtains the environment variable or system property, with preference to the system
    * property in the case both are defined.
    * @param envVarOrSysProp
    * @return
    * @throws IllegalStateException If the requested environment variable or system property was not found
    */
   public static String getEnvVarOrSysProp(final String envVarOrSysProp)
           throws IllegalStateException {
      String value = System.getProperty(envVarOrSysProp);
      if (value == null) {
         value = System.getenv(envVarOrSysProp);
      }
      if (value == null) {
         final String errorMessage = MessageFormat.format(MESSAGE_PATTERN, envVarOrSysProp);
         throw new IllegalStateException(errorMessage);
      }
      return value;
   }

}
