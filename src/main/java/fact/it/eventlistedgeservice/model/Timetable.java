package fact.it.eventlistedgeservice.model;

import java.util.ArrayList;
import java.util.List;

public class Timetable {
    private String eventName;
    private String organiser;
    private List<Artist> artists;

    public Timetable(Event event, List<Artist> tempArtists){
        setEventName(event.getEventName());
        setOrganiser(event.getOrganiser());

        artists = new ArrayList<>();

        tempArtists.forEach(artist -> {
            artists.add(new Artist(
                    artist.getEvent(),
                    artist.getArtist(),
                    artist.getHour(),
                    artist.getMinute()));
        });

        setArtists(artists);
    }

    public Timetable(Event event, Artist artist){
        setEventName(event.getEventName());
        setOrganiser(event.getOrganiser());

        artists = new ArrayList<>();

        artists.add(artist);

        setArtists(artists);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }
}
