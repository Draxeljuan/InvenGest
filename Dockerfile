
#Imagen Modelo
FROM eclipse-temurin:21.0.2_13-jdk

#Informar el puerto donde se ejecuta el contenedor
EXPOSE 8080

#Definir directorio Raiz del Contenedor
WORKDIR /root

#Copiar y pegar archivos dentro del contenedor
COPY ./pom.xml /root
COPY ./.mvn /root/.mvn
COPY ./mvnw /root

#Descargar las dependencias
RUN ./mvnw dependency:go-offline

#Copiar el codigo fuente en el contenedor
COPY ./src /root/src

#Construir Aplicación
RUN ./mvnw clean install -DskipTests

#Levantar Nuestra Aplicación cuando el contenedor inicie
ENTRYPOINT ["java","-jar", "/root/target/InvenGest-0.0.1-SNAPSHOT.jar"]


