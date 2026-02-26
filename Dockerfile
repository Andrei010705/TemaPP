FROM eclipse-temurin:17-jdk-jammy

RUN apt-get update && \
    apt-get install -y xvfb x11-apps && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY . .

RUN javac src/Calculator.java
CMD ["xvfb-run", "-a", "java", "-cp", "src", "Calculator"]