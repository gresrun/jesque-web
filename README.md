# Jesque-Web

[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2Fgresrun%2Fjesque-web%2Fbadge%3Fref%3Dmaster&style=flat&label=build&logo=none)](https://actions-badge.atrox.dev/gresrun/jesque-web/goto?ref=master)  [![License Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/gresrun/jesque-web/blob/master/LICENSE)


Jesque-Web is a faithful re-implementation of the [Sinatra](http://www.sinatrarb.com/) web application, resque-web, that is a part of [Resque](https://github.com/resque/resque) in [Java](https://www.java.com/en/).

Jesque-Web is a [Maven](https://maven.apache.org/) project built using [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html) and depends on [Jesque](https://github.com/gresrun/jesque), [Jedis](https://github.com/xetorthio/jedis) to connect to [Redis](https://redis.io/), [Jackson](https://github.com/FasterXML/jackson/) to map to/from [JSON](https://www.json.org/) and [SLF4J](https://www.slf4j.org/) for logging.

***

## How do I use it?

1. Download the latest source at: `https://github.com/gresrun/jesque-web`
1. Edit the [Redis connection settings](https://github.com/gresrun/jesque-web/blob/master/src/main/resources/META-INF/spring/redis.properties) for your environment.
1. Start Jetty for testing: `mvn -Pjetty-run clean test`
1. Build WAR for deployment to a servlet container: `mvn clean package`

***

## Misc.

If you are on Mac OS X, I highly recommend using the fantasic [Homebrew package manager](https://brew.sh). It makes installing and maintaining libraries, tools and applications a cinch. E.g.:

	brew install redis
	brew install git
	brew install maven
	gem install resque

Boom! Ready to go!

***

## License

Copyright 2026 Greg Haines

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   <https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

