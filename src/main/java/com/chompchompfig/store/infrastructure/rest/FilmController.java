package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.infrastructure.jpa.FilmRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Api(value ="Video Rentals, Films", description = "Film Inventory API", tags = "{3}")
@RestController
@RequestMapping("/store")
public class FilmController {

    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private FilmResourceAssembler resourceAssembler;

    @ApiOperation(value = "Gets the list of all Films in the Inventory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved")
    })
    @GetMapping(value = "/films", produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<FilmResource> getFilms() {
        return new Resources(resourceAssembler.toResources(filmRepository.findAll()));
    }

    @ApiOperation(value = "Gets a Film from the Inventory")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved"),
            @ApiResponse(code = 404, message = "The selected Film does not exist")
    })
    @GetMapping(value = "/film/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public FilmResource getFilm(@PathVariable Long id) {
        Optional<FilmResource> filmResource = filmRepository.findById(id).map(f -> resourceAssembler.toResource(f));
        return filmResource.get();
    }

}
