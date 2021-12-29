package fact.it.eventlistedgeservice.controller;

import fact.it.eventlistedgeservice.model.Artist;
import fact.it.eventlistedgeservice.model.Event;
import fact.it.eventlistedgeservice.model.FilledEventArtist;
import fact.it.eventlistedgeservice.model.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TimetableController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${eventservice.baseurl}")
    private String eventServiceBaseUrl;

    @Value("${artistservice.baseurl}")
    private String artistServiceBaseUrl;

    @GetMapping("/eventlists")
    public List<FilledEventArtist> getEventList(){
        List<FilledEventArtist> returnList = new ArrayList<>();

        ResponseEntity<List<Event>> responseEntityEvents =
                restTemplate.exchange("http://" + eventServiceBaseUrl + "/events",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Event>>() {
                        });

        List<Event> events = responseEntityEvents.getBody();

        for(Event event : events){

        }
        return null;
    }

    @GetMapping("/eventlists/event/{eventName}")
    public Timetable getEventlistsByEventName(@PathVariable String eventName){
        Event event = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}", Event.class, eventName);
        ResponseEntity<List<Artist>> responseEntityArtists = restTemplate.exchange("http://" + artistServiceBaseUrl + "/artists/event/{eventName}", HttpMethod.GET, null, new ParameterizedTypeReference<List<Artist>>(){}, eventName);

        return new Timetable(event, responseEntityArtists.getBody());
    }
}
