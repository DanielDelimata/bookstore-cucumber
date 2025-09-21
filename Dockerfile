FROM gradle:8.10.2-jdk21-alpine AS runner
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew || true
ENV BASE_URL=https://fakerestapi.azurewebsites.net
ENTRYPOINT ["./gradlew","--no-daemon","test"]