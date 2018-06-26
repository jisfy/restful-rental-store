package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Film;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@ApiModel(description = "A representation of a Film resource", value = "Film")
@Relation(collectionRelation = "films", value = "film")
@JsonPropertyOrder({"filmId", "name", "category", "available", "_links"})
public class FilmResource extends ResourceSupport {

    private Film film;

    public FilmResource(Film film) {
        this.film = film;
        this.add(linkTo(methodOn(FilmController.class).getFilm(this.film.getId())).withSelfRel());
    }

    @ApiModelProperty(notes = "the Film's unique identifier")
    public Long getFilmId() {
        return this.film.getId();
    }

    @ApiModelProperty(notes = "the Film's name")
    public String getName() {
        return this.film.getName();
    }

    @ApiModelProperty(notes = "is the Film available to rent?")
    public boolean isAvailable() {
        return this.film.isAvailable();
    }

    @ApiModelProperty(notes = "the Film's category. Could be NEW, REGULAR or OLD")
    public String getCategory() {
        return this.film.getCategory().toString();
    }
}
