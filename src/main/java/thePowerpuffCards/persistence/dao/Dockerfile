# use image from repo
FROM postgres:alpine

# set working directory
WORKDIR /home

# copy files from host to container
# init sql file
COPY init.sql /docker-entrypoint-initdb.d/