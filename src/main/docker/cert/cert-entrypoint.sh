#!/bin/sh

export DOMAIN=${SERVICE_DOMAIN}
export EMAIL=${SERVICE_EMAIL}
export SERVICE=${SERVICE}
export GRAFANA_URL=${GRAFANA_URL}
export CORS_ORIGINS=${CORS_ORIGINS}
export CORS_METHODS=${CORS_METHODS}
export CORS_HEADERS=${CORS_HEADERS}


# Substitute environment variables in the Nginx config
envsubst '${DOMAIN}${SERVICE}${GRAFANA_URL}${CORS_ORIGINS}${CORS_METHODS}${CORS_HEADERS}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf && \
    rm -f /etc/nginx/conf.d/default.conf.template && \
    rm -f /etc/nginx/sites-enabled/default && \
    mkdir -p /var/cache/nginx/proxycache && \
    mkdir -p /var/cache/nginx/proxytemp

# Substitute environment variables in the telemetry config
envsubst '${TELEMETRY_URL}' < /opt/telemetry.conf > /etc/nginx/conf.d/opentelemetry_module.conf && \
    rm -f telemetry.conf

# Ensure we have a folder for the certificates
if [ ! -d /usr/share/nginx/certificates ]; then
    echo "Creating certificate folder"
    mkdir -p /usr/share/nginx/certificates
fi

### If certificates do not exist yet, create self-signed one before we start nginx
if [ ! -f /usr/share/nginx/certificates/fullchain.pem ]; then
    echo "Generating self-signed certificate"
    openssl genrsa -out /usr/share/nginx/certificates/privkey.pem 4096
    openssl req -new -key /usr/share/nginx/certificates/privkey.pem -out /usr/share/nginx/certificates/cert.csr -nodes -subj \
    "/C=PT/ST=World/L=World/O=$DOMAIN/OU=EGI lda/CN=$DOMAIN"
    openssl x509 -req -days 365 -in /usr/share/nginx/certificates/cert.csr -signkey /usr/share/nginx/certificates/privkey.pem -out /usr/share/nginx/certificates/fullchain.pem
fi

if [ -n "$DOMAIN" ] && [ "$DOMAIN" != "localhost" ] && [ "$DOMAIN" != "test.local" ]; then
    ### Send certbot emission/renewal to background
    $(while :; do /opt/request.sh; sleep "${RENEW_INTERVAL:-12h}"; done;) &

    ### Check for changes in the certificate (i.e renewals or first start) in the background
    $(while inotifywait -e close_write /usr/share/nginx/certificates; do echo "Reloading nginx with new certificate"; nginx -s reload; done) &
fi

### Start nginx with daemon off as our main pid
echo "Starting nginx"
nginx
