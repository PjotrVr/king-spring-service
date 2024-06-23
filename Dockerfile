FROM openjdk:17-jdk-slim

WORKDIR /app

# update and install Maven
RUN apt-get update 
RUN apt-get install -y maven
RUN apt-get clean

# clean up the apt cache to reduce image size 
RUN rm -rf /var/lib/apt/lists/*  

# copy everything into docker container
COPY . /app

# install all dependencies
RUN ./mvnw clean install

EXPOSE 8080

# start app
CMD ["./mvnw", "spring-boot:run"]
