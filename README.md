Proof of concept example of Uptime Agent Server
===============================================

This repository contains a basic implementation of standalone HTTP server application that can recieve XML messages and send responses in RESTful way.

This example uses JAXB for XML serialization/deserialization and optional messages validation usind XSD schema. This example does NOT use SOAP/JAX-RS specifications.

Build and start
---------------

    git clone https://bitbucket.org/alexkasko/uptime-agent-example-java.git
    cd uptime-agent-example-java
    mvn clean install
    java -jar uptime-server/target/uptime-server-1.0-SNAPSHOT-dist/bin/uptime-agent.jar

Server will start on port 8881 - [http://127.0.0.1:8881/](http://127.0.0.1:8881/) and will return data from `/proc/uptime`.

Server implementation details
-----------------------------

Server uses [Tomcat 8 embedded API](http://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/catalina/startup/Tomcat.html). Requests are received by [one single servlet](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-server/src/main/files/conf/web.xml?at=master) dispatching requests-to-actions is currently not implemented (currently GET and POST are bound to "/" path) but can be implemented easily inside the servlet.

Server sources [web link](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-server/src/main/java/com/alexkasko/thermostat/uptime/server/?at=master).

Transport implementation details
--------------------------------

Transport serialization/deserialization is implemented using JAXB, transport classes are generated in build time using [org.apache.cxf.cxf-xjc-plugin](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-client-jaxb/pom.xml?at=master#cl-25) with additional all-field constructors and fluent getters/setters.

[XSD schema](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-client-jaxb/src/main/resources/com.alexkasko.thermostat.uptime.xsd?at=master) and [XJB bindings for it](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-client-jaxb/src/main/resources/com.alexkasko.thermostat.uptime.xjb?at=master) designed to use as little XSD features as possible (no namespaces, no inheritance) and at the same time allow generated classes to implement "marker" interfaces for [input](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-client-jaxb/src/main/java/com/alexkasko/thermostat/uptime/client/UptimeCommand.java?at=master) and [output](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-client-jaxb/src/main/java/com/alexkasko/thermostat/uptime/client/UptimeResponse.java?at=master) messages. XSD also contains enumeration (java enum will be generated) with all available server actions and with detailed descriptions for this actions (description will be copied to generated classes and from there to javadocs).

Client implementation details
-----------------------------

Client library contains [starightforward implementation](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-client/src/main/java/com/alexkasko/thermostat/uptime/client/UptimeClient.java?at=master) of HTTP calls using [Apache HTTP components](https://hc.apache.org/) library. Input and output messages are serialized/deserialized transparently usign JAXB. Client [usage example](https://bitbucket.org/alexkasko/uptime-agent-example-java/src/afc95e55653e13758914713b3062f360282eab3d/uptime-client/src/test/java/com/alexkasko/thermostat/uptime/client/UptimeTest.java?at=master#cl-19).

