# Entorno de PRODUCCIÓN

# ==ETAPA 1: Construcción del JAR con Maven==
   # Imagen combinada: maven + jdk21 de eclipse
FROM maven:3.9.9-eclipse-temurin-21 AS build
   # Directorio de trabajo dentro del contenedor
WORKDIR /app
   # Solo se copia el pom.xml al directorio de trabajo
COPY pom.xml ./
   # Baja las dependencias sin conexión y en modo Batch (sin asistencia)
RUN mvn dependency:go-offline -B
   # Solo copia los fuentes java, NO los test
COPY src ./src
   # Limpia y empaqueta (se crea el *.jar)
RUN mvn clean package -DskipTests

# ==ETAPA 2: Configuración de la app Java ==
   # Contenedor solo con JRE, para hacerlo mas pequeño
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
   # Usuario sin privilegios para no ejecutar como root
RUN addgroup -S app && adduser -S app -G app
   # Copia el archivo *jar generado en el contenedor de construcción
COPY --from=build /app/target/*.jar app.jar
USER app
   # Este contenedor escucha el puerto indicado
EXPOSE 8080
   # Verifica que la aplicación responde al health check
HEALTHCHECK --interval=120s --timeout=5s --start-period=60s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1
   # Define un comando para cuando se inicialice el contenedor en el host
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]


# ------------------------------------- COMANDOS ----------------------------------------------------------
# Construir la imagen, ATENCION!!! existe un punto al final que se debe incluir
#> docker build -t devops:latest .

# Crea y arrancar el contenedor a partir de la imagen
#> docker run -d --name devops1  -p 8080:8080 devops

# Arranca el contenedor
#> docker start devops1
