# docker

This folder contains some supplementary files for setting up a Docker environment for building CivBuddy and trying it
out on a local machine.  
It is not part of the CivBuddy app.

Prerequisites:

1. [Add SSH keys to GitHub](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)  
   These are required because `npm install` will run some scripts for some components which access GitHub directly,
   and without the SSH keys, these calls will fail. GitHub changed their policy a few years back.
2. Provide them in docker/.ssh/ so that they can be included with the container

In this folder:

```shell
docker pull node:8-buster
docker build -t civbuddy-node:8 --progress plain .
docker run --rm -it -vC:\path\to\civbuddy:/build --entrypoint=/bin/bash civbuddy-node:8
# The following steps happen inside the build container:
cd /build
npm install
npm run build
```

Then, in order to use the app in a local browser:

```
docker pull nginx:1-bookworm
docker run -it --rm -d -p 8080:80 --name civbuddy -v C:\path\to\civbuddy\build\dist:/usr/share/nginx/html nginx:1-bookworm
```

Now, you can use the app at http://localhost:8080/.

In order to stop the web server again:

```shell
docker stop civbuddy
```

It is also possible to keep the webserver running, e.g. for immediate testing of changes during development.
