FROM jboss/wildfly
COPY home-automation-cep.properties /opt/jboss/wildfly/modules/com/ninetailsoftware/configuration/main/
COPY module.xml /opt/jboss/wildfly/modules/com/ninetailsoftware/configuration/main/
ADD target/home-automation-cep*.war /opt/jboss/wildfly/standalone/deployments/

