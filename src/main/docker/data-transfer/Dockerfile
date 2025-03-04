#
# 1.--- Set up CERN CA ---------------------------------------------------
#
FROM gitlab-registry.cern.ch/linuxsupport/alma9-base:latest AS ca_store

RUN yum -y install java-1.8.0-openjdk && \
    keytool -import -alias cernbundle -file "/etc/pki/tls/certs/CERN-bundle.crt" \
        -keystore /etc/pki/tls/certs/cern-truststore.jks -storepass 'password' -noprompt && \
    keytool -import -alias cernGridCA -file "/etc/pki/tls/certs/CERN_Grid_Certification_Authority(1).crt" \
        -keystore /etc/pki/tls/certs/cern-truststore.jks -storepass 'password' -noprompt && \
    keytool -import -alias cernRootCA -file "/etc/pki/tls/certs/CERN_Root_CA.crt" \
        -keystore /etc/pki/tls/certs/cern-truststore.jks -storepass 'password' -noprompt && \
    keytool -import -alias cernRootCA2 -file "/etc/pki/tls/certs/CERN_Root_Certification_Authority_2.crt" \
        -keystore /etc/pki/tls/certs/cern-truststore.jks -storepass 'password' -noprompt

#
# 2.--- Build Java app ---------------------------------------------------
#
FROM registry.cern.ch/docker.io/maven:3-eclipse-temurin-17 AS builder

# Get source code
WORKDIR /app

RUN git clone --single-branch https://github.com/joaopblopes/eosc-data-transfer.git . && \
    chmod a+x mvnw.sh

# Replace cern-truststore file
COPY --from=ca_store /etc/pki/tls/certs/cern-truststore.jks /app/src/main/resources/

RUN ls /app/src/main/resources/ -la

# Build app
RUN mvn package

#
# 3.--- Build container  --------------------------------------------------
#
FROM registry.access.redhat.com/ubi8/openjdk-17:1.17

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=builder --chown=185 /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185 /app/target/quarkus-app/*.jar /deployments/
COPY --from=builder --chown=185 /app/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185 /app/target/quarkus-app/quarkus/ /deployments/quarkus/
RUN mkdir /deployments/logs && \
    chmod a+rw /deployments/logs

# If you want to include the debug port into your docker image
# you will have to expose the debug port (default 5005) like this :  EXPOSE 8089 5005

EXPOSE 8089
USER 185

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_DIR="/deployments"

# This image uses the `run-java.sh` script to run the application.
# This scripts computes the command line to execute your Java application, and
# includes memory/GC tuning.
# You can configure the behavior using the environment variables from file data-transfer.env
# See also https://github.com/fabric8io-images/run-java-sh
