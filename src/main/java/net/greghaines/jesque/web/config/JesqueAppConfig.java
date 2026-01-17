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

import net.greghaines.jesque.Config;
import net.greghaines.jesque.meta.dao.FailureDAO;
import net.greghaines.jesque.meta.dao.KeysDAO;
import net.greghaines.jesque.meta.dao.QueueInfoDAO;
import net.greghaines.jesque.meta.dao.WorkerInfoDAO;
import net.greghaines.jesque.meta.dao.impl.FailureDAORedisImpl;
import net.greghaines.jesque.meta.dao.impl.KeysDAORedisImpl;
import net.greghaines.jesque.meta.dao.impl.QueueInfoDAORedisImpl;
import net.greghaines.jesque.meta.dao.impl.WorkerInfoDAORedisImpl;
import net.greghaines.jesque.utils.PoolUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import redis.clients.jedis.JedisPool;

/** Spring application configuration for Jesque Web. */
@Configuration
@PropertySource("classpath:META-INF/spring/redis.properties")
@ComponentScan(
    basePackages = {"net.greghaines.jesque.web.controller", "net.greghaines.jesque.web.config"})
public class JesqueAppConfig {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public Config jesqueConfig(
      @Value("${redis.host}") final String host,
      @Value("${redis.port}") final int port,
      @Value("${redis.timeout}") final int timeout,
      @Value("${redis.password}") final String password,
      @Value("${redis.namespace}") final String namespace,
      @Value("${redis.database}") final int database) {
    return new Config(host, port, timeout, password, namespace, database);
  }

  @Bean
  public JedisPool jedisPool(
      @Value("${redis.host}") final String host,
      @Value("${redis.port}") final int port,
      @Value("${redis.timeout}") final int timeout,
      @Value("${redis.password}") final String password,
      @Value("${redis.database}") final int database) {
    var actualPassword = (password == null || password.isBlank()) ? null : password;
    return new JedisPool(
        PoolUtils.getDefaultPoolConfig(), host, port, timeout, actualPassword, database);
  }

  @Bean
  public FailureDAO failureDAO(final JedisPool jedisPool, final Config jesqueConfig) {
    return new FailureDAORedisImpl(jesqueConfig, jedisPool);
  }

  @Bean
  public KeysDAO keysDAO(final JedisPool jedisPool, final Config jesqueConfig) {
    return new KeysDAORedisImpl(jesqueConfig, jedisPool);
  }

  @Bean
  public QueueInfoDAO queueInfoDAO(final JedisPool jedisPool, final Config jesqueConfig) {
    return new QueueInfoDAORedisImpl(jesqueConfig, jedisPool);
  }

  @Bean
  public WorkerInfoDAO workerInfoDAO(final JedisPool jedisPool, final Config jesqueConfig) {
    return new WorkerInfoDAORedisImpl(jesqueConfig, jedisPool);
  }
}
