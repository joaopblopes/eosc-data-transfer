FROM registry.cern.ch/docker.io/nginx:1.23.0

MAINTAINER Levente Farkas (levente.farkas@egi.eu)

# Install certbot and openssl
# Using inotifywait to watch our certificates and trigger actions on change
RUN apt-get -q -y update && \
    apt-get -q -y install inotify-tools openssl certbot gettext-base vim && \
    apt-get -q clean && \
    rm -rf /var/lib/apt/lists/*

# Configure website
COPY ssl/ /etc/ssl-options
COPY nginx.conf /etc/nginx/nginx.conf
COPY cert-nginx.conf /etc/nginx/conf.d/default.conf.template

# Configure telemetry
WORKDIR /opt
ADD https://github.com/open-telemetry/opentelemetry-cpp-contrib/releases/download/webserver%2Fv1.0.3/opentelemetry-webserver-sdk-x64-linux.tgz .
RUN tar xvfz opentelemetry-webserver-sdk-x64-linux.tgz && \
    rm -f opentelemetry-webserver-sdk-x64-linux.tgz

WORKDIR /opt/opentelemetry-webserver-sdk
RUN ./install.sh

WORKDIR /opt
COPY telemetry.conf telemetry.conf

ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/opentelemetry-webserver-sdk/sdk_lib/lib

# Configure entrypoint
COPY cert-entrypoint.sh entrypoint.sh
COPY cert-request.sh request.sh
COPY health-check.sh status.sh

RUN chmod a+x entrypoint.sh && \
    chmod a+x request.sh && \
    chmod a+x status.sh

EXPOSE 80 443 8080

ENTRYPOINT ["./entrypoint.sh"]
