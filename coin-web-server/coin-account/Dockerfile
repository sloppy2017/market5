FROM java:8
MAINTAINER jianghongyan
RUN mkdir /app
COPY . ~/java
WORKDIR ~/java
RUN mkdir target
RUN chmod a+x mvnw
RUN ./mvnw clean compile package && cp target/coin-account-1.0-SNAPSHOT.jar  /app/app.jar
ADD runboot.sh /app/
RUN bash -c 'touch /app/app.jar'
WORKDIR /app
RUN chmod a+x runboot.sh
EXPOSE 7892
CMD /app/runboot.sh