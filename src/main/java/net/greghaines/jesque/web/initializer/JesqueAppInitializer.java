/*
 * Copyright 2026 Greg Haines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.greghaines.jesque.web.initializer;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import net.greghaines.jesque.web.config.JesqueAppConfig;
import net.greghaines.jesque.web.config.JesqueWebConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

/** Initializes the Jesque Web application. */
public class JesqueAppInitializer implements WebApplicationInitializer {
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    // Create the Spring web application context
    var webAppContext = new AnnotationConfigWebApplicationContext();
    webAppContext.setDisplayName("jesque-web");
    webAppContext.register(JesqueAppConfig.class);
    webAppContext.register(JesqueWebConfig.class);

    // Manage the lifecycle of the application context
    servletContext.addListener(new ContextLoaderListener(webAppContext));

    // Register and map the Dispatcher Servlet
    ServletRegistration.Dynamic dispatcher = servletContext.addServlet("jesque-web",
        new DispatcherServlet(webAppContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/app/*");

    // Register filters
    var characterEncodingFilter = new CharacterEncodingFilter();
    characterEncodingFilter.setEncoding("UTF-8");
    characterEncodingFilter.setForceEncoding(true);
    servletContext
        .addFilter("CharacterEncodingFilter", characterEncodingFilter)
        .addMappingForUrlPatterns(null, false, "/*");

    var hiddenHttpMethodFilter = new HiddenHttpMethodFilter();
    servletContext
        .addFilter("httpMethodFilter", hiddenHttpMethodFilter)
        .addMappingForUrlPatterns(null, false, "/*");

    var urlRewriteFilter = new UrlRewriteFilter();
    servletContext
        .addFilter("UrlRewriteFilter", urlRewriteFilter)
        .addMappingForUrlPatterns(null, false, "/*");

    // Misc.
    servletContext.setSessionTimeout(10);
  }
}
