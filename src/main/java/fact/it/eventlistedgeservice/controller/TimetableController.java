package fact.it.eventlistedgeservice.controller;

import fact.it.eventlistedgeservice.model.Artist;
import fact.it.eventlistedgeservice.model.Event;
import fact.it.eventlistedgeservice.model.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public List<Timetable> getEventList(){
        List<Timetable> returnList = new ArrayList<>();

        ResponseEntity<List<Event>> responseEntityEvents =
                restTemplate.exchange("http://" + eventServiceBaseUrl + "/events",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Event>>() {
                        });

        List<Event> events = responseEntityEvents.getBody();

        for(Event event : events){
            ResponseEntity<List<Artist>> responseEntityArtists = restTemplate.exchange("http://" + artistServiceBaseUrl + "/artists/event/{eventName}", HttpMethod.GET, null, new ParameterizedTypeReference<List<Artist>>(){}, event.getEventName());
            returnList.add(new Timetable(event, responseEntityArtists.getBody()));
        }
        return returnList;
    }

    @GetMapping("/eventlists/event/{eventName}")
    public Timetable getEventlistsByEventName(@PathVariable String eventName){
        Event event = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}", Event.class, eventName);
        ResponseEntity<List<Artist>> responseEntityArtists = restTemplate.exchange("http://" + artistServiceBaseUrl + "/artists/event/{eventName}", HttpMethod.GET, null, new ParameterizedTypeReference<List<Artist>>(){}, eventName);

        return new Timetable(event, responseEntityArtists.getBody());
    }

    @GetMapping("/eventlists/organizer/{organizer}")
    public List<Timetable> getEventlistsByOrganizer(@PathVariable String organizer){
        List<Timetable> returnList = new ArrayList();

        ResponseEntity<List<Event>> responseEntityEvents =
                restTemplate.exchange("http://" + eventServiceBaseUrl + "/events/organizer/{organizer}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Event>>(){
                    }, organizer);

        List<Event> events = responseEntityEvents.getBody();

        for(Event event : events){
            Artist artist = restTemplate.getForObject("http://" + artistServiceBaseUrl + "/artists/event/{eventName}",
                Artist.class, event.getEventName());

            returnList.add(new Timetable(event, artist));
        }

        return returnList;
    }

    @GetMapping("/eventlists/{artist}")
    public List<Timetable> getEventlistsByArtist(@PathVariable String artist){
        List<Timetable> returnList = new ArrayList();
        ResponseEntity<List<Artist>> responseEntityArtists = restTemplate.exchange("http://" + artistServiceBaseUrl + "/artists/{artist}", HttpMethod.GET, null, new ParameterizedTypeReference<List<Artist>>(){}, artist);
        List<Artist> artists = responseEntityArtists.getBody();
        for (Artist artist1: artists){
            Event event = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}", Event.class, artist1.getEvent());
            returnList.add(new Timetable(event, artist1));
        }
        return returnList;
    }

    @PostMapping("/eventlists")
    public Timetable addEventlist(@RequestParam String eventName, @RequestParam(required = false) String organizer, @RequestParam(required = false) String artistName, @RequestParam Integer hour, @RequestParam Integer minute){
        Event eventTest = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}", Event.class, eventName);
        if(eventTest == null){
            if (organizer == null){
                organizer = "TBA";
            }
            restTemplate.postForObject("http://" + eventServiceBaseUrl + "/events", new Event(eventName, organizer), Event.class);
        }
        Event event = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}", Event.class, eventName);
        if(artistName == null){
            artistName = "TBA";
        }
        Artist artist = restTemplate.postForObject("http://" + artistServiceBaseUrl + "/artists", new Artist(eventName, artistName, hour, minute), Artist.class);
        return new Timetable(event, artist);
    }

//    @PutMapping("/eventlists")

    @DeleteMapping("eventlists/{artistName}/event/{eventName}")
    public ResponseEntity deleteEventlist(@PathVariable String eventName, @PathVariable String artistName){
        restTemplate.delete("http://" + eventServiceBaseUrl + "/events/event/" + eventName);
        restTemplate.delete("http://" + artistServiceBaseUrl + "/artists/" + artistName + "/event/" + eventName);

        return ResponseEntity.ok().build();
    }
}
