/*
 * Copyright (C) 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.api.services.rcsbusinessmessaging.v1;

// [START of the RBM API Helper]

// [START import_libraries]

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.rcsbusinessmessaging.v1.model.*;
import com.google.api.services.rcsbusinessmessaging.v1.model.cards.CardOrientation;
import com.google.api.services.rcsbusinessmessaging.v1.model.cards.CardWidth;
import com.google.api.services.rcsbusinessmessaging.v1.model.cards.MediaHeight;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
// [END import_libraries]

/**
 * Helper class for using the RBM API.
 */
public class RbmApiHelper {
    private static final Logger logger = Logger.getLogger(RbmApiHelper.class.getName());

    private static final String EXCEPTION_WAS_THROWN = "an exception was thrown";

    // Credentials used for RBM agent API
    private GoogleCredential credential;

    // Reference to the RBM api builder
    private RCSBusinessMessaging.Builder builder;

    public RbmApiHelper() { }

    /**
     * Initializes credentials and the RBM API object.
     * @param serviceAccountKeyFile A file with the service account key information.
     */
    public RbmApiHelper(File serviceAccountKeyFile) {
        initCredentials(serviceAccountKeyFile);
        initRbmApi();
    }

    /**
     * Initializes credentials used by the RBM API.
     * @param serviceAccountKeyFile A file with the service account key information.
     */
    private void initCredentials(File serviceAccountKeyFile) {
        logger.info("Initializing credentials for RBM.");

        try {
            ClassLoader classLoader = getClass().getClassLoader();

            this.credential = GoogleCredential
                    .fromStream(new FileInputStream(serviceAccountKeyFile));

            this.credential = credential.createScoped(Arrays.asList(
                    "https://www.googleapis.com/auth/rcsbusinessmessaging"));
        } catch(Exception e) {
            logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
        }
    }

    /**
     * Initializes the RBM api object.
     */
    private void initRbmApi() {
        try {
            HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            // create instance of the RBM API
            builder = new RCSBusinessMessaging
                    .Builder(httpTransport, jsonFactory, null)
                    .setApplicationName(credential.getServiceAccountProjectId());

            // set the API credentials and endpoint
            builder.setHttpRequestInitializer(credential);
        } catch(Exception e) {
            logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
        }
    }

    /**
     * Takes the msisdn and converts it into the format we need to make API calls.
     * @param msisdn The phone number in E.164 format.
     * @return The phone number reformatted for the API.
     */
    private String convertToApiFormat(String msisdn) {
        return "phones/" + msisdn;
    }

    /**
     * Registers the device as a tester for this agent.
     * @param msisdn The phone number in E.164 format.
     */
    public void registerTester(String msisdn) throws Exception {
        Tester tester = new Tester();

        // convert the msisdn into the API format
        String clientDevice = convertToApiFormat(msisdn);

        // create the test request
        RCSBusinessMessaging.Phones.Testers.Create createTester
                = builder.build().phones().testers().create(clientDevice, tester);

        logger.info(createTester.execute().toString());
    }

    /**
     * Performs a batch user capability check. The API supports a maximum of 10,000
     * users per request.
     *
     * @param phoneNumbers List of user phone numbers to check.
     * @return A BatchGetUsersResponse object.
     * @throws Exception
     */
    public BatchGetUsersResponse getUsers(List<String> phoneNumbers) throws Exception {
        RCSBusinessMessaging.Users.BatchGet batchGetRequest = builder.build().users()
            .batchGet(new BatchGetUsersRequest().setUsers(phoneNumbers));

        HttpRequest request = ((AbstractGoogleClientRequest) batchGetRequest).buildHttpRequest();
        request.setUnsuccessfulResponseHandler(
            new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff()));

        return batchGetRequest.execute();
    }

    /**
     * Checks whether the device associated with the phone number is RCS enabled.
     * This uses the asynchronous capability check API.
     * @param msisdn The phone number in E.164 format.
     */
    public void performCapabilityCheck(String msisdn) throws Exception {
        // convert the msisdn into the API format
        String parent = convertToApiFormat(msisdn);

        // create a random UUID for the request
        String requestId = UUID.randomUUID().toString();

        // initialize the capability request payload
        RequestCapabilityCallbackRequest capabilityCallbackRequest
                = new RequestCapabilityCallbackRequest();

        // set the request id
        capabilityCallbackRequest.setRequestId(requestId);

        // build the request
        RCSBusinessMessaging.Phones.Capability.RequestCapabilityCallback request
                = builder
                .build()
                .phones()
                .capability()
                .requestCapabilityCallback(parent, capabilityCallbackRequest);

        // execute the capability request
        logger.info(request.execute().toString());
    }

    /**
     * Checks whether the device associated with the phone number is RCS enabled.
     * This uses the alpha synchronous capability check API.
     * @param msisdn The phone number in E.164 format.
     * @return The raw response from the capability check.
     */
    public String getCapability(String msisdn) throws Exception {
        // convert the msisdn into the API format
        String parent = convertToApiFormat(msisdn);

        // build the request
        RCSBusinessMessaging.Phones.GetCapabilities capabilityCheck
                = builder
                .build()
                .phones()
                .getCapabilities(parent);

        capabilityCheck.setRequestId(UUID.randomUUID().toString());

        try {
            // execute synchronous capability check and log the result
            return capabilityCheck.execute().toString();
        } catch(GoogleJsonResponseException e) {
            logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);

           return e.getMessage();
        }
    }

    /**
     * Uploads the file located at the publicly available URL to the RBM platform.
     * @param fileUrl A publicly available URL.
     * @return A unique file resource id.
     */
    public String uploadFile(String fileUrl) {
        return uploadFile(fileUrl, null);
    }

    /**
     * Uploads the file located at the publicly available URL to the RBM platform.
     * @param fileUrl A publicly available URL.
     * @param thumbnailFileUrl Includes the thumbnail if there is one.
     * @return A unique file resource id.
     */
    public String uploadFile(String fileUrl, String thumbnailFileUrl) {
        String resourceId = null;

        CreateFileRequest fileRequest = new CreateFileRequest();
        fileRequest.setFileUrl(fileUrl);

        // add the thumbnail if there is one
        if (thumbnailFileUrl != null && thumbnailFileUrl.length() > 0) {
            fileRequest.setThumbnailUrl(thumbnailFileUrl);
        }

        try {
            RCSBusinessMessaging.Files.Create file =
                    builder.build().files().create(fileRequest);

            String jsonResponse = file.execute().toString();

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> jsonMap = gson.fromJson(jsonResponse, type);

            resourceId = jsonMap.get("name");
        } catch(IOException e) {
            logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
        }

        return resourceId;
    }

    /**
     * Creates a card content object based on the parameters.
     * @param title The title for the card.
     * @param description The description for the card.
     * @param imageUrl The image URL for the card's media.
     * @param height The height to display the media.
     * @param suggestions List of suggestions to attach to the card.
     * @return The standalone card object.
     */
    public CardContent createCardContent(String title,
                                         String description,
                                         String imageUrl,
                                         MediaHeight height,
                                         List<Suggestion> suggestions) {
        CardContent cardContent = new CardContent();

        // have to build card from bottom up, starting with the media
        if(imageUrl != null) {
            // create content info for media element with the image URL
            Media media = new Media();
            media.setContentInfo(new ContentInfo().setFileUrl(imageUrl));
            media.setHeight(height.toString());

            // attach media to the card content
            cardContent.setMedia(media);
        }

        // make sure we have a title
        if(title != null) {
            cardContent.setTitle(title);
        }

        // make sure we have a description
        if(description != null) {
            cardContent.setDescription(description);
        }

        // make sure there are suggestions
        if(suggestions != null && suggestions.size() > 0) {
            cardContent.setSuggestions(suggestions);
        }

        return cardContent;
    }

    /**
     * Creates a standalone card object based on the passed in parameters.
     * @param title The title for the card.
     * @param description The description for the card.
     * @param imageUrl The image URL for the card's media.
     * @param height The height to display the media.
     * @param orientation The orientation of the card.
     * @param suggestions List of suggestions to attach to the card.
     * @return The standalone card object.
     */
    public StandaloneCard createStandaloneCard(String title,
                                               String description,
                                               String imageUrl,
                                               MediaHeight height,
                                               CardOrientation orientation,
                                               List<Suggestion> suggestions) {
        // create the card content representation of the parameters
        CardContent cardContent = createCardContent(
                title,
                description,
                imageUrl,
                height,
                suggestions
        );

        // create a standalone vertical card
        StandaloneCard standaloneCard = new StandaloneCard();
        standaloneCard.setCardContent(cardContent);
        standaloneCard.setCardOrientation(orientation.toString());

        return standaloneCard;
    }

    /**
     * Generic method to send a text message using the RBM api to the user with
     * the phone number msisdn.
     * @param messageText The text to send the user.
     * @param msisdn The phone number in E.164 format.
     */
    public void sendTextMessage(String messageText, String msisdn) throws IOException {
        sendTextMessage(messageText, msisdn, null);
    }

    /**
     * Generic method to send a text message using the RBM api to the user with
     * the phone number msisdn.
     * @param messageText The text to send the user.
     * @param msisdn The phone number in E.164 format.
     * @param suggestions The chip list suggestions.
     */
    public void sendTextMessage(String messageText, String msisdn, List<Suggestion> suggestions)
            throws IOException {
        // create content to send to the user
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setText(messageText);

        // attach suggestions if there are some
        if(suggestions != null && suggestions.size() > 0) {
            agentContentMessage.setSuggestions(suggestions);
        }

        // attach content to message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending a standalone card to a client.
     * @param standaloneCard The card object to send.
     * @param msisdn The phone number in E.164 format.
     * @throws IOException
     */
    public void sendStandaloneCard(StandaloneCard standaloneCard, String msisdn) throws IOException {
        // attach the standalone card to a rich card
        RichCard richCard = new RichCard();
        richCard.setStandaloneCard(standaloneCard);

        // attach the rich card to the content for the message
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setRichCard(richCard);

        // attach content to message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        // send the message to the user
        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending of a carousel rich card to a client.
     * @param cardContents List of CardContent items to be attached to the CarourselCard.
     * @param cardWidth Width of the cards for the carousel.
     * @param msisdn The phone number in E.164 format.
     * @throws IOException
     */
    public void sendCarouselCards(List<CardContent> cardContents, CardWidth cardWidth, String msisdn)
            throws IOException {
        // create a carousel card and attach the falist of card contents
        CarouselCard carouselCard = new CarouselCard();
        carouselCard.setCardContents(cardContents);
        carouselCard.setCardWidth(cardWidth.toString());

        // attach the carousel card to a rich card
        RichCard richCard = new RichCard();
        richCard.setCarouselCard(carouselCard);

        // attach the rich card to the content for the message
        AgentContentMessage agentContentMessage = new AgentContentMessage();
        agentContentMessage.setRichCard(richCard);

        // attach content to message
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setContentMessage(agentContentMessage);

        // send the message to the user
        sendAgentMessage(agentMessage, msisdn);
    }

    /**
     * Generic method to execute the sending of an agent message to a client.
     * @param agentMessage The message payload to send.
     * @param msisdn The phone number in E.164 format.
     */
    public void sendAgentMessage(AgentMessage agentMessage, String msisdn) throws IOException {
        // create a message request to send to the msisdn
        RCSBusinessMessaging.Phones.AgentMessages.Create message =
                builder.build().phones().agentMessages().create(convertToApiFormat(msisdn), agentMessage);

        // generate a unique message id
        message.setMessageId(UUID.randomUUID().toString());

        logger.info("Sending message to client " + msisdn);

        // execute the request, sending the text to the user's phone
        logger.info(message.execute().toString());
    }

    /**
     * Sends a READ request to a user's phone.
     * @param messageId The message id for the message that was read.
     * @param msisdn The phone number in E.164 format to send the event to.
     */
    public void sendReadMessage(String messageId, String msisdn) {
        try {
            String deviceNumber = convertToApiFormat(msisdn);

            // create READ event to send user
            AgentEvent agentEvent = new AgentEvent();
            agentEvent.setEventType(EventType.READ.toString());
            agentEvent.setMessageId(messageId);

            // create an agent event request to send to the msisdn
            RCSBusinessMessaging.Phones.AgentEvents.Create agentEventMessage =
                    builder.build().phones().agentEvents().create(deviceNumber, agentEvent);

            // set a unique event id
            agentEventMessage.setEventId(UUID.randomUUID().toString());

            // execute the request, sending the READ event to the user's phone
            agentEventMessage.execute();
        } catch(Exception e) {
            logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
        }
    }

    /**
     * Sends the IS_TYPING event to the user.
     * @param msisdn The phone number in E.164 format to send the event to.
     */
    public void sendIsTypingMessage(String msisdn) {
        try {
            String deviceNumber = convertToApiFormat(msisdn);

            // create READ event to send user
            AgentEvent agentEvent = new AgentEvent();
            agentEvent.setEventType(EventType.IS_TYPING.toString());

            // create an agent event request to send to the msisdn
            RCSBusinessMessaging.Phones.AgentEvents.Create agentEventMessage =
                    builder.build().phones().agentEvents().create(deviceNumber, agentEvent);

            // set a unique event id
            agentEventMessage.setEventId(UUID.randomUUID().toString());

            // execute the request, sending the READ event to the user's phone
            agentEventMessage.execute();
        } catch(Exception e) {
            logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
        }
    }
}
// [END of the RBM API Helper]
