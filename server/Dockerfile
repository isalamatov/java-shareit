FROM amazoncorretto:11
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8082
COPY target/shareit-server.jar server.jar
ENTRYPOINT ["java","-jar","/server.jar"]