package fact.it.eventlistedgeservice.controller;

import fact.it.eventlistedgeservice.model.Artist;
import fact.it.eventlistedgeservice.model.Event;
import fact.it.eventlistedgeservice.model.Timetable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
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
        List<Timetable> returnList = new ArrayList<>();

        ResponseEntity<List<Event>> responseEntityEvents = restTemplate.exchange("http://" + eventServiceBaseUrl + "/events/organizer/{organizer}", HttpMethod.GET, null, new ParameterizedTypeReference<List<Event>>(){}, organizer);

        List<Event> events = responseEntityEvents.getBody();

        for(Event event : events){
            ResponseEntity<List<Artist>> responseEntityArtists = restTemplate.exchange("http://" + artistServiceBaseUrl + "/artists/event/{eventName}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<Artist>>(){
                    }, event.getEventName());

            returnList.add(new Timetable(event, responseEntityArtists.getBody()));
        }
        return returnList;
    }

    @GetMapping("/eventlists/artist/{artistName}")
    public List<Timetable> getEventlistsByArtist(@PathVariable String artistName){
        List<Timetable> returnList = new ArrayList();
        ResponseEntity<List<Artist>> responseEntityArtists = restTemplate.exchange("http://" + artistServiceBaseUrl + "/artists/{artistName}", HttpMethod.GET, null, new ParameterizedTypeReference<List<Artist>>(){}, artistName);
        List<Artist> artists = responseEntityArtists.getBody();
        for (Artist artist1: artists){
            Event event = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}", Event.class, artist1.getEvent());
            returnList.add(new Timetable(event, artist1));
        }
        return returnList;
    }

    @PostMapping("/eventlists")
    public Timetable addEventlist(@RequestParam String eventName, @RequestParam String organizer, @RequestParam String artistName, @RequestParam Integer hour, @RequestParam Integer minute){
        Event event = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}", Event.class, eventName);
        if(event == null){
            event = restTemplate.postForObject("http://" + eventServiceBaseUrl + "/events", new Event(eventName, organizer), Event.class);
        }
        Artist artist = restTemplate.postForObject("http://" + artistServiceBaseUrl + "/artists", new Artist(eventName, artistName, hour, minute), Artist.class);
        return new Timetable(event, artist);
    }

    @PutMapping("/eventlists")
    public Timetable updateEventList(@RequestParam String eventName, @RequestParam String artistName, @RequestParam Integer hour, @RequestParam Integer minute){
        Artist artist = restTemplate.getForObject("http://" + artistServiceBaseUrl + "/artists/" + artistName + "/event/" + eventName,
                Artist.class);

        artist.setHour(hour);
        artist.setMinute(minute);

        ResponseEntity<Artist> responseEntityArtist =
                restTemplate.exchange("http://" + artistServiceBaseUrl + "/artists", HttpMethod.PUT, new HttpEntity<>(artist), Artist.class);

        Artist retrievedArtist = responseEntityArtist.getBody();

        Event event = restTemplate.getForObject("http://" + eventServiceBaseUrl + "/events/{eventName}",
                Event.class, eventName);

        return new Timetable(event, retrievedArtist);
    }

    @DeleteMapping("eventlists/artist/{artistName}/event/{eventName}")
    public ResponseEntity deleteEventlist(@PathVariable String eventName, @PathVariable String artistName){
        restTemplate.delete("http://" + artistServiceBaseUrl + "/artists/" + artistName + "/event/" + eventName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("eventlists/event/{eventName}")
    public ResponseEntity deleteEvent(@PathVariable String eventName){
        restTemplate.delete("http://" + eventServiceBaseUrl + "/events/event/" + eventName);

        return ResponseEntity.ok().build();

    }
}
