package com.chompchompfig.store.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * A Customer that performs rental operations in a Video Rental Business
 */
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Long pointsInCard  = Long.valueOf(0l);
    @OneToMany(mappedBy="customer",targetEntity=Rental.class, fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Rental> rentals;

    public Customer() {
        this.rentals = new ArrayList<>();
    }

    /**
     * Gets the Customer unique identifier
     * @return <p>the Customer unique identifier</p>
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the Customer unique identifier
     * @param id <p>the unique identifier to set</p>
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the Customer's first name
     * @return <p>the Customer's first name</p>
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the Customer's last name
     * @return <p>the Customer's last name</p>
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the Customer's phone number
     * @return <p>the Customer's phone number</p>
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the Customer's bonus points from his/her points card
     * @return <p>the Customer's bonus points</p>
     */
    public Long getPointsInCard() {
        return pointsInCard;
    }

    /**
     * Gets the Customer's associated Rentals
     * @return <p>a List of all rentals from the Customer</p>
     */
    public List<Rental> getRentals() {
        return rentals;
    }

    /**
     * Sets the Customer's first name
     * @param firstName <p>the Customer's first name to set</p>
     */
    void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the Customer's last name
     * @param lastName <p>the Customer's last name to set</p>
     */
    void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the Customer's phone number
     * @param telephone <p>the Customer's phone number to be set</p>
     */
    void setPhoneNumber(String telephone) {
        this.phoneNumber = telephone;
    }

    /**
     * Sets the Customer's bonus points in his/her points card
     * @param pointsInCard <p>the bonus points to set</p>
     */
    void setPointsInCard(Long pointsInCard) {
        this.pointsInCard = pointsInCard;
    }

    /**
     * Creates a new Rental for a Customer, renting a list of Films, for a given duration in days.
     * @param days <p>the expected duration of the Rental in days</p>
     * @param films <p>a List of Film identifiers indicating the Films the Customer wants to rent</p>
     * @return <p>a new Rental for a List of Films and a Customer</p>
     * @throws IllegalArgumentException <p>in case any of the requested Films is unavailable</p>
     */
    public Rental rent(int days, List<Film> films) {
        Rental rental = new Rental(this, days, films);
        rental.setRentalStartDate(new Date());
        this.rentals.add(rental);
        return rental;
    }

    /**
     * Updates the bonus points in the Bonus Points Card of a Customer according to the given Rental
     * @param bonusPointsForRental <p>the new bonus points to reward to a Customer </p>
     */
    void updateBonusPointsForRental(long bonusPointsForRental) {
        setPointsInCard(getPointsInCard() + bonusPointsForRental);
    }

    /**
     * Updates an existing Rental with a list of Films, for a given duration in days. The list of Films provided
     * will completely replace the existing one
     * @param rentalId <p>the identifier of the Rental we would like to update</p>
     * @param days <p>the expected duration of the Rental in days</p>
     * @param films <p>a List of Film identifiers indicating the Films the Customer wants to rent</p>
     * @return <p>a new Rental for a List of Films and a Customer</p>
     * @throws IllegalArgumentException <p>in case any of the requested Films is unavailable</p>
     * @throws IllegalStateException <p>in case the Rental can't be updated because it is in a invalid state. Only
     * RentalStatus.AWAITING_PAYMENT supports modifications</p>
     */
    public void modifyRental(RentalId rentalId, int days, List<Film> films) {
        getRentalById(rentalId).modify(days, films);
    }

    private Rental getRentalById(final RentalId rentalId) {
        Optional<Rental> rentalWithId = rentals.stream().filter(r -> r.getId().equals(rentalId)).findFirst();
        return rentalWithId.get();
    }

}
