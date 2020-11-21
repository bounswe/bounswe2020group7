# pull official base image
FROM node:13.12.0-alpine

# set working directory
WORKDIR /platon_frontend

# add `/app/node_modules/.bin` to $PATH
ENV PATH /platon_frontend/node_modules/.bin:$PATH

# install app dependencies
COPY frontend/package.json ./
COPY frontend/package-lock.json ./
RUN npm install --silent
RUN npm install react-scripts@3.4.1 -g --silent

# add app
COPY . ./

EXPOSE 3000

# start app
CMD ["npm", "start"]