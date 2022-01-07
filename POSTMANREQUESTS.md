### GET /eventlists
#### Get all events + playing artists of each event
![APT GET /eventlists](src/assets/APTGETEventlists.png)

### GET /eventlists/artist/Artist1
#### Get all timetables for Artist 1
![APT GET /eventlists/artist/Artist1](src/assets/APTGETEventlistsArtistArtist1.png)

### GET /eventlists/event/Event1
#### Get all artists for Event 1
![APT GET /eventlists/artist/Artist1](src/assets/APTGETEventlistsEventEvent1.png)

### GET /eventlists/organizer/Organizer2
#### Get all timetables for Organizer 2
![APT GET /eventlists/organizer/Organizer2](src/assets/APTGETEventlistsOrganizerOrganizer2.png)

### POST /eventlists?eventName=Event3&organizer=Organizer3&artistName=Artist1&hour=0&minute=0
#### Post/Add a new timetable for Artist 1 at Event 3 organized by Organizer 3, 00:00
![APT POST /eventlists?eventName=Event3&organizer=Organizer3&artistName=Artist1&hour=0&minute=0](src/assets/APTPOSTEventlists.png)

### PUT /eventlists?eventName=Event3&artistName=Artist1&hour=22&minute=30
#### Put/Update a timetable for Artist 1 at Event 3, 22:30
![APT PUT /eventlists?eventName=Event3&artistName=Artist1&hour=22&minute=30](src/assets/APTPUTEventlists.png)

### DELETE /eventlists/artist/Artist1/event/Event3
#### Delete the timetable for Artist 1 at Event 3
![APT DELETE /eventlists/artist/Artist1/event/Event3](src/assets/APTDELETEEventlistsArtist.png)

### DELETE /eventlists/event/Event3
#### Delete Event 3
![APT DELETE /eventlists/event/Event3](src/assets/APTDELETEEventlistsEvent.png)
