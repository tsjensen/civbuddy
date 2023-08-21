# docker

This folder contains some supplementary files for setting up a Docker environment on the local machine for building
CivBuddy. It is not part of the CivBuddy app.

Prerequisites:

1. [Add SSH keys to GitHub](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)
2. Provide them in /docker/.ssh/ so that they can be included with the container

In this folder:

- `docker build -t civbuddy-node:8 --progress plain .`
- `docker run --rm -it -vC:\path\to\civbuddy:/build --entrypoint=/bin/bash civbuddy-node:8`
- `cd /build`
- `npm install`
- `npm run build` (or whatever other command)
