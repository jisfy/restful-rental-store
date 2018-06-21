package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Film;
import com.chompchompfig.store.domain.FilmCategory;
import com.chompchompfig.store.infrastructure.jpa.FilmRepository;
import com.chompchompfig.store.tools.JsonPathTools;
import com.chompchompfig.store.domain.SimpleFixtureFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(FilmController.class)
public class FilmControllerTest {

    private static final Long FILM_ID = 1l;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FilmRepository filmRepository;
    private JsonPathTools jsonPathTools = new JsonPathTools();
    private SimpleFixtureFactory simpleFixtureFactory = new SimpleFixtureFactory();

    @Test
    public void storeFilmsShouldReturn200OkAndEmptyListBodyWhenNoFilms() throws Exception {
        Mockito.when(filmRepository.findAll()).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/store/films")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(content().json("{}"));
    }

    @Test
    public void storeFilmsShouldReturn200OkAndCorrectBodyWhenFilmsExist() throws Exception {
        List<Film> films = simpleFixtureFactory.newFilms();
        Mockito.when(filmRepository.findAll()).thenReturn(films);
        this.mockMvc.perform(get("/store/films")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$._embedded['ex:filmResourceList'].length()").value(films.size()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForFilmNameInList(0))
                        .value(films.get(0).getName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForFilmCategoryInList(0)).
                        value(films.get(0).getCategory().toString()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForFilmNameInList(1))
                        .value(films.get(1).getName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForFilmCategoryInList(1))
                        .value(films.get(1).getCategory().toString()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForFilmNameInList(2))
                        .value(films.get(2).getName()))
                .andExpect(jsonPath(jsonPathTools.jsonPathForFilmCategoryInList(2)).
                        value(films.get(2).getCategory().toString()));
    }

    @Test
    public void storeFilmIdWithNonExistingIdShouldReturn404NotFound() throws Exception {
        long nonExistingFilmId = 1l;
        Mockito.when(filmRepository.findById(nonExistingFilmId)).thenReturn(Optional.empty());
        this.mockMvc.perform(get("/store/film/" + nonExistingFilmId)).andExpect(status().isNotFound());
    }

    @Test
    public void storeFilmIdWithExistingIdShouldReturn200OkAndValidContent() throws Exception {
        Film someOldFilm =
                simpleFixtureFactory.newFilmOfCategory(FILM_ID, SimpleFixtureFactory.FILM_NAME_1, FilmCategory.OLD);
        Mockito.when(filmRepository.findById(FILM_ID)).thenReturn(Optional.of(someOldFilm));
        this.mockMvc.perform(get("/store/film/" + FILM_ID)).andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.filmId").value(someOldFilm.getId()))
                .andExpect(jsonPath("$.name").value(someOldFilm.getName()))
                .andExpect(jsonPath("$.category").value(someOldFilm.getCategory().toString()));
    }
}
