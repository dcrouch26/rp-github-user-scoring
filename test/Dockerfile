FROM java:8-alpine

ADD target/rp-scoring.jar /opt/rp-scoring.jar
EXPOSE 3000

ENTRYPOINT ["/usr/bin/java", "-jar", "/opt/rp-scoring.jar"]