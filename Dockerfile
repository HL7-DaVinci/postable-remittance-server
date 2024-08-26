# This is multi-stage Docker builds approach where one stage is used for building the application
# and a subsequent stage is used for running it. This allows the final image to be smaller as
# it only needs to include the runtime environment and the built application, not the entire build toolchain.

# https://hub.docker.com/_/maven
FROM maven:3.9-amazoncorretto-17 AS build

# To run it as non-root user https://docs.docker.com/reference/dockerfile/#user
USER 1001

# Set the working directory
WORKDIR /home/1001

# Copy current local folder content to the docker image
COPY --chown=1001 . .

# Compile and build the project
RUN mvn clean package --quiet -DskipTests

###################################################################################
# Use same image as Build if deploying pipeline required to have maven build tools
#FROM maven:3.9-amazoncorretto-17
# https://hub.docker.com/_/amazoncorretto
FROM amazoncorretto:17

# To run it as non-root user
USER 1001

# Copy target folder to the docker image
COPY --from=build --chown=1001 /home/1001/target /home/1001/app/target

# Run the service
CMD ["java", "-jar", "/home/1001/app/target/postable-remittance.jar"]
