package com.chompchompfig.store.domain;

import javax.persistence.*;

/**
 * An Entity representing a Film in a Rental order
 */
@Entity
public class RentalItem {

    @EmbeddedId
    private RentalItemId id;

    @ManyToOne(optional=false)
    @JoinColumn(name="FILM_ID",referencedColumnName="ID")
    private Film film;
    @ManyToOne(optional=false)
    @JoinColumns({
            @JoinColumn(name="CUSTOMER_ID", referencedColumnName="CUSTOMER_ID", insertable = false, updatable = false),
            @JoinColumn(name="RENTAL_ID", referencedColumnName="ID", insertable = false, updatable = false),
    })
    private Rental rental;

    /**
     * Gets the unique identifier of a Rental Item
     * @return <p>the Rental Item unique identifier</p>
     *
    */
    public RentalItemId getId() {
        return id;
    }

    /**
     * Sets the unique identifier of a Rental Item
     * @param id <p>the unique identifier to use</p>
     *
    */
    public void setId(RentalItemId id) {
        this.id = id;
    }

    /**
     * Gets the Film associated with a Rental Item
     * @return <p>the Film associated with a Rental Item</p>
     */
    public Film getFilm() {
        return film;
    }

    /**
     * Gets the Rental associated with a Rental Item
     * @return <p>the Rental to which the Rental Item belongs</p>
     */
    public Rental getRental() {
        return this.rental;
    }

    /**
     * Sets the Film associated with a Rental Item
     * @return <p>the Film to associate with a Rental Item</p>
     */
    void setFilm(Film film) {
        this.film = film;
    }

    /**
     * Sets the Rental associated with a Rental Item
     * @return <p>the Rental to associate with a Rental Item</p>
     */
    void setRental(Rental rental) {
        this.rental = rental;
    }

    /**
     * Builds a new Rental Item from the given Film and Rental
     * @param film <p>the Film to use to build the Rental Item</p>
     * @param rental <p>the Rental to use to build the Rental Item</p>
     * @return <p>the newly created Rental Item</p>
     */
    public static RentalItem from(Film film, Rental rental) {
        RentalItem rentalItem = new RentalItem();
        RentalItemId rentalItemId =
                new RentalItemId(rental.getId().getCustomerId(), rental.getId().getId(), System.nanoTime());
        rentalItem.setId(rentalItemId);
        rentalItem.setFilm(film);
        rentalItem.setRental(rental);
        return rentalItem;
    }
}
