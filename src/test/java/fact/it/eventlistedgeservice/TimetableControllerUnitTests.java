package fact.it.eventlistedgeservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fact.it.eventlistedgeservice.model.Artist;
import fact.it.eventlistedgeservice.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TimetableControllerUnitTests {
    @Value("${eventservice.baseurl}")
    private String eventServiceBaseUrl;

    @Value("${artistservice.baseurl}")
    private String artistServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private Event event1 = new Event("Event1", "Organizer1");
    private Event event2 = new Event("Event2", "Organizer2");

    private Artist artist1Event1 = new Artist("Event1", "Artist1",21, 0);
    private Artist artist1Event2 = new Artist("Event2", "Artist1",0, 0);
    private Artist artist2Event1 = new Artist("Event1", "Artist2",22, 30);

    private List<Artist> allArtistsForEvent1 = Arrays.asList(artist1Event1, artist2Event1);
    private List<Artist> allArtistsForEvent2 = Arrays.asList(artist1Event2);
    private List<Artist> allEventsForArtist1 = Arrays.asList(artist1Event1, artist1Event2);
    private List<Event> allEvents = Arrays.asList(event1, event2);

    @BeforeEach
    public void initializeMockserver() throws URISyntaxException, JsonProcessingException {
        mockServer = MockRestServiceServer.createServer(restTemplate);

    }

    @Test
    public void whenGetEventlistsByArtist_thenReturnTimetableJson() throws Exception {

        // GET all events from Artist1
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + artistServiceBaseUrl + "/artists/Artist1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(allEventsForArtist1))
                );

        // GET Event1 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + eventServiceBaseUrl + "/events/Event1")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(event1))
                );

        // GET Event2 info
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + eventServiceBaseUrl + "/events/Event2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(event2))
                );

        mockMvc.perform(get("/eventlists/{artist}", "Artist1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventName", is("Event1")))
                .andExpect(jsonPath("$[0].organizer", is("Organizer1")))
                .andExpect(jsonPath("$[0].artists[0].event", is("Event1")))
                .andExpect(jsonPath("$[0].artists[0].artist", is("Artist1")))
                .andExpect(jsonPath("$[1].eventName", is("Event2")))
                .andExpect(jsonPath("$[1].organizer", is("Organizer2")))
                .andExpect(jsonPath("$[1].artists[0].event", is("Event2")))
                .andExpect(jsonPath("$[1].artists[0].artist", is("Artist1")));
    }

//     @Test
//     public void whenGetEventlistsByEventName_thenReturnTimetableJson() throws Exception {

//         // GET Books by Title 'Book'
//         mockServer.expect(ExpectedCount.once(),
//                 requestTo(new URI("http://" + eventServiceBaseUrl + "/events")))
//                 .andExpect(method(HttpMethod.GET))
//                 .andRespond(withStatus(HttpStatus.OK)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .body(mapper.writeValueAsString(allEvents))
//                 );

//         // GET all artists for Event1
//         mockServer.expect(ExpectedCount.once(),
//                 requestTo(new URI("http://" + artistServiceBaseUrl + "/artists/event/Event1")))
//                 .andExpect(method(HttpMethod.GET))
//                 .andRespond(withStatus(HttpStatus.OK)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .body(mapper.writeValueAsString(allArtistsForEvent1))
//                 );

//         // GET all artists for Event2
//         mockServer.expect(ExpectedCount.once(),
//                 requestTo(new URI("http://" + artistServiceBaseUrl + "/artists/event/Event2")))
//                 .andExpect(method(HttpMethod.GET))
//                 .andRespond(withStatus(HttpStatus.OK)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .body(mapper.writeValueAsString(allArtistsForEvent2))
//                 );

//         mockMvc.perform(get("/eventlists"))
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$", hasSize(3)))
//                 .andExpect(jsonPath("$[0].eventName", is("Event1")))
//                 .andExpect(jsonPath("$[0].organizer", is("Organizer1")))
//                 .andExpect(jsonPath("$[0].artists[0].event", is("Event1")))
//                 .andExpect(jsonPath("$[0].artists[0].artist", is("Artist1")))
//                 .andExpect(jsonPath("$[1].eventName", is("Event2")))
//                 .andExpect(jsonPath("$[1].organizer", is("Organizer2")))
//                 .andExpect(jsonPath("$[1].artists[0].event", is("Event2")))
//                 .andExpect(jsonPath("$[1].artists[0].artist", is("Artist1")));

//     }
    @Test
    public void whenAddEventlist_thenReturnTimetableJson() throws Exception {

        Artist artist2Event2 = new Artist("Event2","Artist2", 1, 30);

        // GET Event2 info
        mockServer.expect(ExpectedCount.twice(),
                requestTo(new URI("http://" + eventServiceBaseUrl + "/events/Event2")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(event2))
                );

        // POST timetable for Artist at Event 2
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + artistServiceBaseUrl + "/artists")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(artist2Event2))
                );



        mockMvc.perform(post("/eventlists")
                .param("eventName", artist2Event2.getEvent())
                .param("artist", artist2Event2.getArtist())
                .param("hour", artist2Event2.getHour().toString())
                .param("minute", artist2Event2.getMinute().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventName", is("Event2")))
                .andExpect(jsonPath("$.artists[0].event", is("Event2")))
                .andExpect(jsonPath("$.artists[0].artist", is("Artist2")))
                .andExpect(jsonPath("$.artists[0].hour", is(1)))
                .andExpect(jsonPath("$.artists[0].minute", is(30)));
    }

    @Test
    public void whenDeleteEventlist_thenReturnStatusOk() throws Exception {

        // DELETE eventlisting from Artist DB & Event DB
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + eventServiceBaseUrl + "/events/event/EventTBD")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://" + artistServiceBaseUrl + "/artists/ArtistTBD/event/EventTBD")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK)
                );


        mockMvc.perform(delete("/eventlists/{artistName}/event/{eventName}", "ArtistTBD", "EventTBD"))
                .andExpect(status().isOk());
    }

}

