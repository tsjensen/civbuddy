FROM node:8-buster

COPY .vimrc /root
COPY .ssh /root/.ssh/

RUN  apt-get update && \
     apt-get install -y vim man && \
     chmod 644 /root/.vimrc && \
     echo 'alias ll="ls -lF --color=auto"' >> /root/.bashrc && \
     chmod 700 /root/.ssh && \
     chmod 644 /root/.ssh/* && \
     chmod 600 /root/.ssh/id_ed25519

WORKDIR /build
