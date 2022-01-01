package fact.it.eventlistedgeservice.model;

import org.springframework.http.HttpEntity;

public class Event {
    private String id;
    private String eventName;
    private String organizer;

    public Event() {
    }

    public Event(String eventName, String organizer) {
        setEventName(eventName);
        setOrganizer(organizer);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }
}
