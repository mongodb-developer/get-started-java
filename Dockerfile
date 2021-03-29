FROM adoptopenjdk/openjdk15:alpine-slim

LABEL org.opencontainers.image.source=https://github.com/mongodb-developer/get-started-java

ENV DRIVER_VERSION=4.2.2

ENV HOME /home/gsuser

RUN apk add --no-cache maven

RUN addgroup -S gsgroup && adduser -S gsuser -G gsgroup && \
    chown -R gsuser ${HOME} && chmod -R 750 ${HOME} 

USER gsuser

ENTRYPOINT ["/bin/sh", "-c"]
