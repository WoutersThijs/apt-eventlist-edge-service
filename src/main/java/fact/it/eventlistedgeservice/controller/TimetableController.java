package fact.it.eventlistedgeservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TimetableController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${eventservice.baseurl}")
    private String eventServiceBaseUrl;

    @Value("${artistservice.baseurl}")
    private String artistServiceBaseUrl;
}
