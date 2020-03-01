FROM openjdk:8u171-alpine3.7
RUN apk --no-cache add curl
COPY build/libs/url-shortener-*-all.jar url-shortener.jar
EXPOSE 8080
CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar url-shortener.jar
