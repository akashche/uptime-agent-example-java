Proof of concept example of Uptime Agent HTTP Server in Java
============================================================

This repository contains a basic implementation of standalone HTTP server application that can recieve XML messages and send responses in RESTful way.

This example uses JAXB for XML serialization/deserialization and optional messages validation usind XSD schema. This example does NOT use SOAP/JAX-RS specifications.

Build and start
---------------

    git clone https://github.com/akashche/uptime-agent-example-java.git
    cd uptime-agent-example-java
    mvn clean install
    java -jar uptime-server/target/uptime-server-1.0-SNAPSHOT-dist/bin/uptime-agent.jar

Server will start on port 8881 - [http://127.0.0.1:8881/](http://127.0.0.1:8881/) and will return data from `/proc/uptime`.

Server implementation details
-----------------------------

Server uses [Tomcat 8 embedded API](http://tomcat.apache.org/tomcat-8.0-doc/api/org/apache/catalina/startup/Tomcat.html). Requests are received by [one single servlet](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-server/src/main/files/conf/web.xml) dispatching requests-to-actions is currently not implemented (currently GET and POST are bound to "/" path) but can be implemented easily inside the servlet.

Server sources [web link](https://github.com/akashche/uptime-agent-example-java/tree/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-server/src/main/java/com/alexkasko/thermostat/uptime/server).

Transport implementation details
--------------------------------

Transport serialization/deserialization is implemented using JAXB, transport classes are generated in build time using [org.apache.cxf.cxf-xjc-plugin](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-client-jaxb/pom.xml#L25) with additional all-field constructors and fluent getters/setters.

[XSD schema](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-client-jaxb/src/main/resources/com.alexkasko.thermostat.uptime.xsd) and [XJB bindings for it](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-client-jaxb/src/main/resources/com.alexkasko.thermostat.uptime.xjb) designed to use as little XSD features as possible (no namespaces, no inheritance) and at the same time allow generated classes to implement "marker" interfaces for [input](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-client-jaxb/src/main/java/com/alexkasko/thermostat/uptime/client/UptimeCommand.java) and [output](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-client-jaxb/src/main/java/com/alexkasko/thermostat/uptime/client/UptimeResponse.java) messages. XSD also contains enumeration (java enum will be generated) with all available server actions and with detailed descriptions for this actions (description will be copied to generated classes and from there to javadocs).

Client implementation details
-----------------------------

Client library contains [starightforward implementation](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-client/src/main/java/com/alexkasko/thermostat/uptime/client/UptimeClient.java) of HTTP calls using [Apache HTTP components](https://hc.apache.org/) library. Input and output messages are serialized/deserialized transparently usign JAXB. Client [usage example](https://github.com/akashche/uptime-agent-example-java/blob/2fb02d1478d2f93c9d0471a8e864115a996d2992/uptime-client/src/test/java/com/alexkasko/thermostat/uptime/client/UptimeTest.java#L19).

