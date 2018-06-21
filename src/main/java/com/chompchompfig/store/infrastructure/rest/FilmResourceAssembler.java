package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Film;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class FilmResourceAssembler extends ResourceAssemblerSupport<Film, FilmResource> {

    public FilmResourceAssembler() {
        super(FilmController.class, FilmResource.class);
    }

    @Override
    public FilmResource toResource(Film film) {
        return new FilmResource(film);
    }
}
