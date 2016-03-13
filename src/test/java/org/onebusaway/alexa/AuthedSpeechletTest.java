/*
 * Copyright 2016 Sean J. Barbeau (sjbarbeau@gmail.com),
 * Philip M. White (philip@mailworks.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.alexa;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import config.UnitTests;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onebusaway.alexa.lib.ObaUserClient;
import org.onebusaway.alexa.storage.ObaUserDataItem;
import org.onebusaway.io.client.elements.ObaArrivalInfo;
import org.onebusaway.io.client.request.ObaArrivalInfoResponse;
import org.onebusaway.io.client.request.ObaStopResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
        classes = UnitTests.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthedSpeechletTest {
    static final LaunchRequest launchRequest = LaunchRequest.builder().withRequestId("test-req-id").build();

    @Mocked
    ObaArrivalInfo obaArrivalInfo;

    @Mocked
    ObaArrivalInfoResponse obaArrivalInfoResponse;

    @Mocked
    ObaUserClient obaUserClient;

    @Resource
    Session session;

    @Resource
    AuthedSpeechlet authedSpeechlet;

    @Resource
    ObaUserDataItem testUserData;

    @Before
    public void initializeAuthedSpeechlet() throws URISyntaxException {
        authedSpeechlet.setUserData(testUserData);
    }

    @Test
    public void getStopDetails(@Mocked ObaStopResponse mockResponse) throws SpeechletException, IOException, URISyntaxException {
        new Expectations() {{
            mockResponse.getStopCode(); result = "6497";
            mockResponse.getName(); result = "University Area Transit Center";
            obaUserClient.getStopDetails(anyString); result = mockResponse;
        }};

        SpeechletResponse sr = authedSpeechlet.onIntent(
                IntentRequest.builder()
                        .withRequestId("test-request-id")
                        .withIntent(
                                Intent.builder()
                                        .withName("GetStopNumberIntent")
                                        .withSlots(new HashMap<String, Slot>())
                                        .build()
                        )
                        .build(),
                session
        );

        String spoken = ((PlainTextOutputSpeech)sr.getOutputSpeech()).getText();
        assertThat(spoken, equalTo("Your stop is 6497, University Area Transit Center."));
    }

    @Test
    public void launchTellsArrivals() throws SpeechletException, IOException {
        ObaArrivalInfo[] obaArrivalInfoArray = new ObaArrivalInfo[1];
        obaArrivalInfoArray[0] = obaArrivalInfo;
        new Expectations() {{
            obaArrivalInfo.getShortName(); result = "8";
            obaArrivalInfo.getHeadsign(); result = "Mlk Way Jr";
            obaArrivalInfoResponse.getArrivalInfo(); result = obaArrivalInfoArray;
            obaUserClient.getArrivalsAndDeparturesForStop(anyString, anyInt); result = obaArrivalInfoResponse;
        }};

        SpeechletResponse sr = authedSpeechlet.onLaunch(
                launchRequest,
                session);

        String spoken = ((PlainTextOutputSpeech)sr.getOutputSpeech()).getText();
        assertThat(spoken, equalTo("Route 8 Mlk Way Jr is now arriving based on the schedule -- "));
    }

    @Test
    public void help() throws SpeechletException {
        SpeechletResponse sr = authedSpeechlet.onIntent(
                IntentRequest.builder()
                        .withRequestId("test-request-id")
                        .withIntent(
                                Intent.builder()
                                        .withName("AMAZON.HelpIntent")
                                        .withSlots(new HashMap<String, Slot>())
                                        .build()
                        )
                        .build(),
                session
        );
        String spoken = ((PlainTextOutputSpeech)sr.getOutputSpeech()).getText();
        assertThat(spoken, containsString("You've already configured your region and stop"));
    }

    @Test
    public void noUpcomingArrivals() throws SpeechletException, IOException {
        ObaArrivalInfo[] obaArrivalInfoArray = new ObaArrivalInfo[0];
        new Expectations() {{
            obaArrivalInfoResponse.getArrivalInfo(); result = obaArrivalInfoArray;
            obaUserClient.getArrivalsAndDeparturesForStop(anyString, anyInt); result = obaArrivalInfoResponse;
        }};

        SpeechletResponse sr = authedSpeechlet.onLaunch(
                launchRequest,
                session);
        String spoken = ((PlainTextOutputSpeech)sr.getOutputSpeech()).getText();
        assertThat(spoken, equalTo("There are no upcoming arrivals at your stop for the next "
                + AuthedSpeechlet.ARRIVALS_SCAN_MINS + " minutes."));
    }
}