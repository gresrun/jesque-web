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
package net.greghaines.jesque.web.config;

import java.util.Set;
import net.greghaines.jesque.json.ObjectMapperFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/** WebMVC configuration for Jesque Web. */
@Configuration
@EnableWebMvc
public class JesqueWebConfig implements WebMvcConfigurer {
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/error");
    registry.addViewController("/notFound");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**").addResourceLocations("/images/");
    registry.addResourceHandler("/css/**").addResourceLocations("/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("/js/");
    registry.addResourceHandler("/favicon.ico").addResourceLocations("/");
    registry.addResourceHandler("/apple-touch-icon*").addResourceLocations("/");
  }

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.jsp("/WEB-INF/jsp/", ".jsp");

    var jsonView = new MappingJackson2JsonView(ObjectMapperFactory.get());
    jsonView.setPrefixJson(false);
    jsonView.setModelKeys(
        Set.of(
            "count",
            "errorCode",
            "failures",
            "hostMap",
            "key",
            "keys",
            "keyName",
            "errorMessage",
            "errorType",
            "queue",
            "queues",
            "queueName",
            "stackTrace",
            "start",
            "stats",
            "totalFailureCount",
            "totalWorkerCount",
            "worker",
            "workers",
            "working"));
    registry.enableContentNegotiation(jsonView);
  }
}
