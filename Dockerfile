FROM ubuntu:18.04

ARG DRIVER_VERSION=3.11.1
ARG MONGODB_URI

RUN apt-get update && apt-get install -y \
	git \
	nano \
    vim \
	sudo \
    default-jdk \
    maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN export uid=1000 gid=1000 && \
    mkdir -p /home/ubuntu && \
    echo "ubuntu:x:${uid}:${gid}:Developer,,,:/home/ubuntu:/bin/bash" >> /etc/passwd && \
    echo "ubuntu:x:${uid}:" >> /etc/group && \
    echo "ubuntu ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/ubuntu && \
    chmod 0440 /etc/sudoers.d/ubuntu && \
    chown ${uid}:${gid} -R /home/ubuntu

ENV HOME /home/ubuntu
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV DRIVER_VERSION ${DRIVER_VERSION}
ENV MONGODB_URI=${MONGODB_URI}

WORKDIR ${HOME}

RUN mkdir -p ${HOME}/java/src/main/java/com/start
COPY ./java/pom.xml ${HOME}/java/
COPY ./java/src/main/java/com/start/Getstarted.java ${HOME}/java/src/main/java/com/start/

RUN sed -i "s/x.x.x/${DRIVER_VERSION}/g" ${HOME}/java/pom.xml

RUN chown -R ubuntu ${HOME}/java && chmod -R 750 ${HOME}/java

USER ubuntu

CMD ["/bin/bash"]  