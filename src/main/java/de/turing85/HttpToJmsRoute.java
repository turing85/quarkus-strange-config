package de.turing85;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.jms;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.platformHttp;

import org.apache.camel.builder.RouteBuilder;

@SuppressWarnings("unused")
public class HttpToJmsRoute extends RouteBuilder {
  @Override
  public void configure() {
    from(platformHttp("/hello").httpMethodRestrict("POST"))
        .log("Sending: ${body}")
        .to(jms("foo::foo"));

    from(jms("foo::foo").subscriptionName("foo").subscriptionDurable(false))
        .log("Received: ${body}");
  }
}
