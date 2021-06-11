# [Google's RCS Business Messaging: Java Client](https://github.com/google-business-communications/java-rcsbusinessmessaging)

[RCS Business Messaging](https://developers.google.com/business-communications/rcs-business-messaging/guides/get-started/how-it-works) upgrades SMS with branding, rich media, interactivity, and analytics. With RCS, businesses can bring branded, interactive mobile experiences, right to the native Android messaging app. This library can be used to ease the development of RCS Business Messaging applications in Java.

This document contains an [API reference](https://developers.google.com/business-communications/rcs-business-messaging/reference/rest), samples, and other resources useful to developing Java applications.
For additional help developing RCS Business Messaging applications, in Java and other languages, see our
[quickstart](https://developers.google.com/business-communications/rcs-business-messaging/guides/get-started/first-agent)
guide.

## Documentation

The documentation for the RCS Business Messaging API can be found [here](https://developers.google.com/business-communications/rcs-business-messaging/reference/rest).

## Quickstart

### Before you begin

1.  [Register with RCS Business Messaging](https://developers.google.com/business-communications/rcs-business-messaging/guides/get-started/register-partner).
1.  Once registered, follow the instructions to [create your first agent](https://developers.google.com/business-communications/rcs-business-messaging/guides/get-started/first-agent).

### Installation

The RCS Business Messaging library is hosted on Maven central.
To use the library in your project, add the following to the dependencies section of your
project’s build.gradle.

```
repositories {
   mavenCentral()
}

dependencies {
   compile group: 'com.google.apis', name: 'google-api-services-rcsbusinessmessaging', version: '1.25.2'
}
```

If using maven, add the following to your pom.xml file.

```xml
<dependency>
  <groupId>com.google.apis</groupId>
  <artifactId>google-api-services-rcsbusinessmessaging</artifactId>
  <version>1.25.2</version>
</dependency>
```

### Using the client library

```java
/**
* Initializes credentials used by the Business Messages API.
*/
private static RCSBusinessMessaging.Builder getRCSBusinessMessagingBuilder(String credentialsFileLocation) {
  System.out.println("Initializing credentials for the RCS Business Messaging API.");
    
  RCSBusinessMessaging.Builder builder = null;
  try {
    File file = new File(credentialsFileLocation);
    
    GoogleCredential credential = GoogleCredential
        .fromStream(new FileInputStream(file));
    
    credential = credential.createScoped(Arrays.asList(
       "https://www.googleapis.com/auth/rcsbusinessmessaging"));
    
    HttpTransport httpTransport = GoogleApacheHttpTransport.newTrustedTransport();
    JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    
    // Create instance of the RBM API
    builder = new RCSBusinessMessaging
       .Builder(httpTransport, jsonFactory, null)
       .setApplicationName(credential.getServiceAccountProjectId());
    
    // Set the API credentials and endpoint
    builder.setHttpRequestInitializer(credential);
  } catch (Exception e) {
    e.printStackTrace();
  }
    
  return builder;
}

public static void main(String args[]) {
  try {
    String msisdn = "VALID-PHONE-NUMBER";
    
    RCSBusinessMessaging.Builder builder = getRCSBusinessMessagingBuilder("SERVICE-ACCOUNT-CREDENTIALS-FILE-LOCATION");
    
    // Create message to send to user
    AgentMessage agentMessage = new AgentMessage()
      .setContentMessage(new AgentContentMessage().setText("Hello, World!"));
    
    // Create a message request to send to the msisdn
    RCSBusinessMessaging.Phones.AgentMessages.Create messageRequest =
        builder.build().phones().agentMessages().create("phones/" + msisdn, agentMessage);
    
    // Generate a unique message id
    messageRequest.setMessageId(UUID.randomUUID().toString());
    
    // Setup retries with exponential backoff
    HttpRequest httpRequest =
        ((AbstractGoogleClientRequest) messageRequest).buildHttpRequest();
    
    httpRequest.setUnsuccessfulResponseHandler(new
        HttpBackOffUnsuccessfulResponseHandler(
        new ExponentialBackOff()));
    
    // Execute request
    httpRequest.execute();
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

## Sample usage

Samples below assume a similar library initialization as shown in the [Using the client library](https://github.com/google-business-communications/java-rcsbusinessmessaging#using-the-client-library) section.

### Sending a text message

```java
// Create message to send to user
AgentMessage agentMessage = new AgentMessage()
  .setContentMessage(new AgentContentMessage().setText("Hello, World!"));

// Create a message request to send to the msisdn
RCSBusinessMessaging.Phones.AgentMessages.Create messageRequest =
    builder.build().phones().agentMessages().create("phones/" + msisdn, agentMessage);

// Setup retries with exponential backoff
HttpRequest httpRequest =
    ((AbstractGoogleClientRequest) messageRequest).buildHttpRequest();

httpRequest.setUnsuccessfulResponseHandler(new
    HttpBackOffUnsuccessfulResponseHandler(
    new ExponentialBackOff()));

// Execute request
httpRequest.execute();
```

### Sending a text message with suggested replies and actions

```java
// Create message to send to user
AgentMessage agentMessage = new AgentMessage()
  .setContentMessage(new AgentContentMessage().setText("Hello, World!")
    .setSuggestions(Arrays.asList(
        new Suggestion()
          .setReply(new SuggestedReply().setText("Sample suggestion").setPostbackData("sample_suggestion")),
        new Suggestion()
          .setAction(new SuggestedAction().setText("URL Action").setPostbackData("url_action")
              .setOpenUrlAction(new OpenUrlAction().setUrl("https://www.google.com"))),
        new Suggestion()
            .setAction(new SuggestedAction().setText("Dial Action").setPostbackData("dial_action")
                .setDialAction(new DialAction().setPhoneNumber("+12223334444")))
        )));

// Create a message request to send to the msisdn
RCSBusinessMessaging.Phones.AgentMessages.Create messageRequest =
  builder.build().phones().agentMessages().create("phones/" + msisdn, agentMessage);

// Generate a unique message id
messageRequest.setMessageId(UUID.randomUUID().toString());

// Setup retries with exponential backoff
HttpRequest httpRequest =
    ((AbstractGoogleClientRequest) messageRequest).buildHttpRequest();

httpRequest.setUnsuccessfulResponseHandler(new
    HttpBackOffUnsuccessfulResponseHandler(
    new ExponentialBackOff()));

// Execute request
httpRequest.execute();
```

### Sending a rich card

```java
// Create message to send to user
AgentMessage agentMessage = new AgentMessage()
  .setContentMessage(new AgentContentMessage()
          .setRichCard(new RichCard()
              .setStandaloneCard(new StandaloneCard()
                  .setCardOrientation(CardOrientation.VERTICAL.toString())
                  .setCardContent(new CardContent()
                      .setTitle("RCS Business messaging!")
                      .setDescription("This is an example rich card")
                      .setMedia(new Media()
                          .setHeight(MediaHeight.MEDIUM.toString())
                          .setContentInfo(new ContentInfo()
                              .setFileUrl("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png")
                              .setForceRefresh(false)))
                      .setSuggestions(
                          Arrays.asList(
                              new Suggestion()
                                  .setReply(new SuggestedReply()
                                      .setText("Sample suggestion")
                                      .setPostbackData("sample_suggestion"))))))));

// Create a message request to send to the msisdn
RCSBusinessMessaging.Phones.AgentMessages.Create messageRequest =
  builder.build().phones().agentMessages().create("phones/" + msisdn, agentMessage);

// Generate a unique message id
messageRequest.setMessageId(UUID.randomUUID().toString());

// Setup retries with exponential backoff
HttpRequest httpRequest =
    ((AbstractGoogleClientRequest) messageRequest).buildHttpRequest();

httpRequest.setUnsuccessfulResponseHandler(new
    HttpBackOffUnsuccessfulResponseHandler(
    new ExponentialBackOff()));

// Execute request
httpRequest.execute();
```

### Sending a carousel

```java
// Create message to send to user
AgentMessage agentMessage = new AgentMessage()
  .setContentMessage(new AgentContentMessage()
          .setRichCard(new RichCard()
              .setCarouselCard(new CarouselCard()
                  .setCardWidth(CardWidth.MEDIUM.toString())
                  .setCardContents(Arrays.asList(
                      new CardContent()
                          .setTitle("Card #1")
                          .setDescription("The description for card #1")
                          .setMedia(new Media()
                              .setHeight(MediaHeight.MEDIUM.toString())
                              .setContentInfo(new ContentInfo()
                                  .setFileUrl("https://storage.googleapis.com/kitchen-sink-sample-images/cute-dog.jpg")
                                  .setForceRefresh(false)))
                          .setSuggestions(
                              Arrays.asList(
                                  new Suggestion()
                                      .setReply(new SuggestedReply()
                                          .setText("Card #1")
                                          .setPostbackData("card_1")))),
                      new CardContent()
                          .setTitle("Card #2")
                          .setDescription("The description for card #2")
                          .setMedia(new Media()
                              .setHeight(MediaHeight.MEDIUM.toString())
                              .setContentInfo(new ContentInfo()
                                  .setFileUrl("https://storage.googleapis.com/kitchen-sink-sample-images/elephant.jpg")
                                  .setForceRefresh(false)))
                          .setSuggestions(
                              Arrays.asList(
                                  new Suggestion()
                                      .setReply(new SuggestedReply()
                                          .setText("Card #2")
                                          .setPostbackData("card_2")))))))));

// Create a message request to send to the msisdn
RCSBusinessMessaging.Phones.AgentMessages.Create messageRequest =
  builder.build().phones().agentMessages().create("phones/" + msisdn, agentMessage);

// Generate a unique message id
messageRequest.setMessageId(UUID.randomUUID().toString());

// Setup retries with exponential backoff
HttpRequest httpRequest =
    ((AbstractGoogleClientRequest) messageRequest).buildHttpRequest();

httpRequest.setUnsuccessfulResponseHandler(new
    HttpBackOffUnsuccessfulResponseHandler(
    new ExponentialBackOff()));

// Execute request
httpRequest.execute();
```

## Samples

See the [code examples](https://developers.google.com/business-communications/rcs-business-messaging/samples) to see
example usage for most API features. The samples' `README.md` has instructions for running the samples.

## Contributing

Contributions welcome! See the [Contributing Guide](https://github.com/google-business-communications/java-rcsbusinessmessaging/CONTRIBUTING.md).

## License

Apache Version 2.0

See [LICENSE](https://github.com/google-business-communications/java-rcsbusinessmessaging/LICENSE)
