package com.chompchompfig.store.infrastructure.rest;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value ="A Video Rental Store", description = "All Operations available in the Store", tags = "{1}")
@RestController
@RequestMapping("/store")
public class StoreController {

    @GetMapping(value = "", produces = "application/json")
    public StoreResource getStore() {
        StoreResource storeResource = new StoreResource();
        return storeResource;
    }
}
