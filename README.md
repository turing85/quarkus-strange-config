### Start artemis

```
$ cd local-deployment
$ docker compose up -d
$ cd ..
```

### Start the service in dev mode and make some requests

In one terminal:
```
$ ./mvnw quakrus:dev
```

In another terminal:

```
$ curl --location --request POST 'localhost:8080/hello' \
  --header 'Content-Type: text/plain' \
  --data-raw 'foo'
```

Observe that the program behaves as expected.

Query the health-endpoint and observe that a connection with name `<default>` is reported as `UP`:
```
$ curl localhost:8080/q/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Artemis JMS health check",
            "status": "UP",
            "data": {
                "<default>": "UP"
            }
        }
    ]
}%
```

Cancel dev-mode

### Build the application in JVM-mode

```
$ ./mvnw clean package
$ cd target/quarkus-app
```

Start the application without any additional configuration:

```
$ java -jar quarkus-run.jar
```

Wait a few seconds, and observe that the application starts showing errors, which is correct since no connection details are present:
```
...
2023-01-24 23:31:28,976 ERROR [org.apa.cam.com.jms.DefaultJmsMessageListenerContainer] (Camel (camel-1) thread #1 - JmsConsumer[foo::foo]) Could not refresh JMS Connection for destination 'foo::foo' - retrying using FixedBackOff{interval=5000, currentAttempts=0, maxAttempts=unlimited}. Cause: Configuration <default>: the configuration is enabled, but no URL is configured. Please either disable the configuration or set the URL.
...
```

In a second terminal, query the health-endpoint, observe that no connection factory was found:
```
$ curl localhost:8080/q/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Artemis JMS health check",
            "status": "UP"
        }
    ]
}% 
```

Stop the application, set connection properties, and restart the application:
```
$ ARTEMIS_URL=tcp://localhost:61616 ARTEMIS_USER=doesnot ARTEMIS_PASSWORD=matter java -jar quarkus-run.jar
```

In a second terminal, make a request:
```
$ curl --location --request POST 'localhost:8080/hello' \
  --header 'Content-Type: text/plain' \
  --data-raw 'foo'
```

Observe that the application behaves as expected.

Query the health-endpoint and observe that a connection with name `<default>` is reported as `UP`:
```
$ curl localhost:8080/q/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Artemis JMS health check",
            "status": "UP",
            "data": {
                "<default>": "UP"
            }
        }
    ]
}%
```

Stop the running application.

### Building the application native

Build the application:
```
$ ./mvnw -Dnative clean package
```

... get a coffee...

Start the application again:

```
$ cd target
$ ./quarkus-strange-config-1.0.0-SNAPSHOT-runner
```

Wait a few seconds, and observe that the application starts showing errors, which is correct since no connection details are present:
```
...
2023-01-24 23:40:29,471 ERROR [org.apa.cam.com.jms.DefaultJmsMessageListenerContainer] (Camel (camel-1) thread #1 - JmsConsumer[foo::foo]) Could not refresh JMS Connection for destination 'foo::foo' - retrying using FixedBackOff{interval=5000, currentAttempts=0, maxAttempts=unlimited}. Cause: Configuration <default>: the configuration is enabled, but no URL is configured. Please either disable the configuration or set the URL.

...
```

In a second terminal, query the health-endpoint, observe that no connection factory was found:
```
$ curl localhost:8080/q/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Artemis JMS health check",
            "status": "UP"
        }
    ]
}% 
```

Stop the application, set connection properties, and restart the application:
```
$ ARTEMIS_URL=tcp://localhost:61616 ARTEMIS_USER=doesnot ARTEMIS_PASSWORD=matter ./quarkus-strange-config-1.0.0-SNAPSHOT-runner
```

Observe that, beside the fact the connection details are set, the application starts to report errors again:
```
...
2023-01-24 23:42:56,869 ERROR [org.apa.cam.com.jms.DefaultJmsMessageListenerContainer] (Camel (camel-1) thread #1 - JmsConsumer[foo::foo]) Could not refresh JMS Connection for destination 'foo::foo' - retrying using FixedBackOff{interval=5000, currentAttempts=0, maxAttempts=unlimited}. Cause: Configuration <default>: the configuration is enabled, but no URL is configured. Please either disable the configuration or set the URL.
...
```

In a second terminal, query the health-endpoint, observe that no connection factory was found:
```
$ curl localhost:8080/q/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Artemis JMS health check",
            "status": "UP"
        }
    ]
}% 
```

### Workaround 
switch to branch `works-with-prod-profile`:
```
$ git checkout works-with-prod-profile
```

Build the application:
```
$ ./mvnw -Dnative clean package
```

... get a coffee...

Start the application again:

```
$ cd target
$ ./quarkus-strange-config-1.0.0-SNAPSHOT-runner
```

Wait a few seconds, and observe that the application starts showing errors, which is correct since no connection details are present:
```
...
2023-01-24 23:40:29,471 ERROR [org.apa.cam.com.jms.DefaultJmsMessageListenerContainer] (Camel (camel-1) thread #1 - JmsConsumer[foo::foo]) Could not refresh JMS Connection for destination 'foo::foo' - retrying using FixedBackOff{interval=5000, currentAttempts=0, maxAttempts=unlimited}. Cause: Configuration <default>: the configuration is enabled, but no URL is configured. Please either disable the configuration or set the URL.
...
```

In a second terminal, query the health-endpoint, observe that no connection factory was found:
```
$ curl localhost:8080/q/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Artemis JMS health check",
            "status": "UP"
        }
    ]
}% 
```

Stop the application, set connection properties, and restart the application:
```
ARTEMIS_URL=tcp://localhost:61616 ARTEMIS_USER=doesnot ARTEMIS_PASSWORD=matter ./quarkus-strange-config-1.0.0-SNAPSHOT-runner
```

In a second terminal, make a request:
```
$ curl --location --request POST 'localhost:8080/hello' \
  --header 'Content-Type: text/plain' \
  --data-raw 'foo'
```

Observe that the application behaves as expected.

Query the health-endpoint and observe that a connection with name `<default>` is reported as `UP`:
```
$ curl localhost:8080/q/health

{
    "status": "UP",
    "checks": [
        {
            "name": "Artemis JMS health check",
            "status": "UP",
            "data": {
                "<default>": "UP"
            }
        }
    ]
}%
```