Jesque-Web
==========

Jesque-Web is a faithful re-implementation of the [Sinatra](http://www.sinatrarb.com/) web application, resque-web, that is a part of [Resque](https://github.com/defunkt/resque) in [Java](http://www.oracle.com/technetwork/java/index.html).

Jesque-Web is a [Maven](http://maven.apache.org/) project built using [Spring MVC](http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/mvc.html) and depends on [Jesque](https://github.com/gresrun/jesque), [Jedis](https://github.com/xetorthio/jedis) to connect to [Redis](http://redis.io/), [Jackson](http://jackson.codehaus.org/) to map to/from [JSON](http://www.json.org/) and [SLF4J](http://www.slf4j.org/) for logging.

***

How do I use it?
----------------
Download the latest source at:

	https://github.com/gresrun/jesque-web
Build the WAR and start Jetty:

	mvn clean jetty:run
***

Misc.
-----

If you are on Mac OS X, I highly recommend using the fantasic [Homebrew package manager](https://github.com/mxcl/homebrew). It makes installing and maintaining libraries, tools and applications a cinch. E.g.:

	brew install redis
	brew install git
	brew install maven
	gem install resque
Boom! Ready to go!

***

License
-------
Copyright 2011 Greg Haines

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

