package org.kontinuity.catapult.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

/**
 * Obtains deployments as they were built by Maven
 * represented as ShrinkWrap archives
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
class Deployments {

   private Deployments() {
      // No instances
   }

   public static WebArchive getMavenBuiltWar() {
      final WebArchive webArchive = ShrinkWrap.createFromZipFile(
              WebArchive.class,
              new File("../web/target/kontinuity-catapult.war"));
      return webArchive;
   }
}
