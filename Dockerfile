FROM java:8
MAINTAINER Rhett
WORKDIR /data
COPY ./runcodeNettyWithJava_jar/* /data/
COPY ./runcodeNettyWithJava_jar/config/* /data/config/
COPY ./certs/* /data/certs/
EXPOSE 7000 7001
CMD java -Xbootclasspath/a:./*:./config -jar runcodeNettyWithJava.jar