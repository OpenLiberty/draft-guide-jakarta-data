# Local Development

1. Pull the [openliberty.io](https://github.com/OpenLiberty/openliberty.io) repository

2. Build the Dockerfile-guides docker container from the ol.io repository using
`docker build -f Dockerfile-guides --tag olio/guides .`  
This dockerfile should be in the prod branch soon, but if not it can be found in [this pr](https://github.com/OpenLiberty/openliberty.io/pull/3988/files#diff-8318ddc366af8a1c39b890da0d2d9aaa3773f662f884aba6e4bcca373658299f) along with a needed update for gem_dependencies.sh


3. Run the container pointed at this repository:
`docker run --name guides -it -d -p 4000:4000 -v <root directory of this guide repository>:/home/jekyll/src/main/content/guides/guide-new olio/guides`

4. The site will take a few minutes to build and start. View the guide [here](http://localhost:4000/guides/jakarta-data)