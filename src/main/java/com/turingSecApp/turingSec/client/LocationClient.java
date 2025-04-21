package com.turingSecApp.turingSec.client;


import com.turingSecApp.turingSec.client.clientResponse.City;
import com.turingSecApp.turingSec.client.clientResponse.Country;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "locationClient", url = "${location.api.url}")
public interface LocationClient {

    @GetMapping("/countries")
    List<Country> getCountries(@RequestHeader("X-CSCAPI-KEY") String apiKey);

    @GetMapping("/countries/{iso2}/cities")
    List<City> getCitiesByCountry(@RequestHeader("X-CSCAPI-KEY") String apiKey, @PathVariable(name = "iso2") String iso2);
}
