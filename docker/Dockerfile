#
# CivBuddy - A calculator app for players of Francis Tresham's original Civilization board game (1980)
# Copyright (C) 2012-2023 Thomas Jensen
#
# This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
# License, version 3, as published by the Free Software Foundation.
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
# warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
# details.
# You should have received a copy of the GNU General Public License along with this program.
# If not, see <http://www.gnu.org/licenses/>.
#

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
