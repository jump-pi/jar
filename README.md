# jump-pi jar

This repository delivers a single jump-pi.jar file that includes a very lean persistence framework and a simple Java REST API development framework.  
It can be used in a JEE application (see jump-pi/war project) or in a stand alone Java program.  
Jump-pi is absolutely autonomous but it could be implemented using other persistence frameworks like Hibernate or DataNucleous (JPA), keeping the same high level interface, so the migration path is guaranteed. Developments done using frameworks like Derby or Spring (REST) could be easily ported to jump-pi due it uses the most generic signature for a request.  
This version has minimun dependencies with other frameworks and it is intended to be used from Embedded Systems to high scale parallelized systems.  
  
## KeyFacts  
__License__: [MIT License](https://tldrlegal.com/license/mit-license)

