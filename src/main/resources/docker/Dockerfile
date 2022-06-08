FROM openjdk:19-slim-buster
# set default shell to bash (instead of /bin/sh)
#SHELL ["/bin/bash","-c"]

# create non-root ID
# -l and static IDs assigned to avoid delay in lookups and system logging
ARG THE_USER_ID=1001
ARG THE_GROUP_ID=1001
RUN DEBIAN_FRONTEND=noninteractive && \
    /usr/sbin/groupadd -g $THE_GROUP_ID spring && \
    /usr/sbin/useradd -l -u $THE_USER_ID -G spring -g $THE_GROUP_ID spring && \
    mkdir logs && chgrp spring logs && chmod ug+rwx logs

# run as non-root
USER spring:spring

# main service /api/product and /swagger-ui/index.html
EXPOSE 8080
# actuator metrics /actuator/prometheus-custom and /actuator/prometheus
EXPOSE 8081

COPY <%=name%>-<%=version%>.jar app.jar
CMD ["java","-jar","app.jar"]