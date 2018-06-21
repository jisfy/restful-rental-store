package com.chompchompfig.store.domain;

import javax.persistence.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A Film or Movie that is part of the inventory of a Video Rental Business
 */
@Entity
public class Film {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    @Version
    @Column(name = "VERSION")
    private Integer version;
    private String name;
    private boolean isAvailable;
    private FilmCategory category;

    protected Film() {
    }

    public Film(String name, FilmCategory category) {
        this();
        this.name = name;
        this.category = category;
        this.isAvailable = true;
    }

    /**
     * Gets the Film unique identifier
     * @return <p>the Film unique identifier</p>
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the Film unique identifier
     * @param id <p>the Film unique identifier to set</p>
     */
    void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the Name of the Film
     * @return <p>the Film name</p>
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the Category of the Film
     * @return <p>the Film Category</p>
     */
    public FilmCategory getCategory() {
        return category;
    }

    /**
     * Gets the Film availability (for rent)
     * @return <p>the Film availability</p>
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Sets the Name of the Film
     * @return <p>the Film name to set</p>
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the Category of the Film
     * @return <p>the Film Category to set</p>
     */
    void setCategory(FilmCategory category) {
        this.category = category;
    }

    /**
     * Sets the Film availability (for rent)
     * @param available <p>the Film availability</p>
     */
    void setAvailable(boolean available) {
        isAvailable = available;
    }

    /**
     * Validates a list of Films, making sure all of them are available
     * @param films <p>the list of Films to validate</p>
     * @param exceptionBuilder <p>a Function from String->RuntimeException that is used to build custom Exceptions
     *                         in case the validation fails</p>
     */
    static void validateFilmsAvailability(List<Film> films, Function<String, RuntimeException> exceptionBuilder) {
        boolean hasUnavailableFilms = films.stream().anyMatch(f -> !f.isAvailable());
        if (hasUnavailableFilms) {
            List<Long> unavailableFilmIds =
                    films.stream().filter(f -> !f.isAvailable()).map(f -> f.getId()).collect(Collectors.toList());
            throw exceptionBuilder.apply(unavailableFilmIds.toString());
        }
    }
}
