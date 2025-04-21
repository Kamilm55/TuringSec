package com.turingSecApp.turingSec.controller;

import com.turingSecApp.turingSec.client.LocationClient;
import com.turingSecApp.turingSec.client.clientResponse.City;
import com.turingSecApp.turingSec.client.clientResponse.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LocationController {

    @Value("${location.api.key}")
    private String apiKey;
    private final LocationClient locationClient;


    @GetMapping("/countries")
    public List<Country> getAllCountries() {
        return locationClient.getCountries(apiKey);
    }

    @GetMapping("/{iso2}/cities")
    public List<City> getAllCitiesByCountryId(@PathVariable String iso2) {
        return locationClient.getCitiesByCountry(apiKey, iso2);
    }
}
