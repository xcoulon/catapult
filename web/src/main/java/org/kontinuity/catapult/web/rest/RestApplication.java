package org.kontinuity.catapult.web.rest;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.kontinuity.catapult.service.github.GithubResource;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@ApplicationPath("/api")
public class RestApplication extends Application
{
   @Inject
   private GithubResource githubResource;

   @Override
   public Set<Object> getSingletons()
   {
      HashSet<Object> singletons = new HashSet<>();
      singletons.add(githubResource);
      return singletons;
   }
}
