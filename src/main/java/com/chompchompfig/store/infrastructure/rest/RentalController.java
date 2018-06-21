package com.chompchompfig.store.infrastructure.rest;

import com.chompchompfig.store.domain.Rental;
import com.chompchompfig.store.domain.RentalId;
import com.chompchompfig.store.infrastructure.jpa.RentalRepository;
import com.chompchompfig.store.application.VideoRentalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Api(value ="Video Rentals, Rentals", description = "Film Rentals API", tags = "{4}")
@RestController
@RequestMapping("/store")
public class RentalController {

    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private VideoRentalService videoRentalService;
    @Autowired
    private RentalResourceAssembler resourceAssembler;

    @ApiOperation(value = "Gets the list of all Rentals registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved")
    })
    @GetMapping(value = "/rentals", produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<RentalResource> getRentals() {
        return new Resources(resourceAssembler.toResources(rentalRepository.findAll()));
    }

    @ApiOperation(value = "Gets a Rental from the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved"),
            @ApiResponse(code = 404, message = "The selected Rental does not exist")
    })
    @GetMapping(value = "/rental/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public RentalResource getRental(@PathVariable String id) {
        RentalId rentalId = RentalId.from(id);
        Optional<Rental> rental = rentalRepository.findById(rentalId);
        Optional<RentalResource> rentalResource = rental.map(r -> new RentalResource(r));
        return rentalResource.get();
    }

    @ApiOperation(value = "Returns a Rental. All Films rented are returned and made available again")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully performed"),
            @ApiResponse(code = 404, message = "The selected Rental does not exist"),
            @ApiResponse(code = 405, message = "The selected Rental can't be returned. It is in an invalid state for" +
                    " the operation. Only Rentals in the PAID state can be returned. PAID Rentals can also be " +
                    "rejected with the same error if they are overdue. Rentals would then be transitioned into the " +
                    "AWAITING_PAYMENT_OVERDUE state. A new Payment would need to be performed in order for the " +
                    "Rental to be successfully RETURNED, which will then be done automatically")
    })
    @DeleteMapping(value ="/rental/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public RentalResource returnRental(@PathVariable String id) {
        RentalId rentalId = RentalId.from(id);
        Rental returnedRental = videoRentalService.returnRental(rentalId);
        return new RentalResource(returnedRental);
    }

    @ApiOperation(value = "Creates a new Rental with some Films")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created"),
            @ApiResponse(code = 400, message = "The Rental can't be created, probably because some of the Films" +
                    " included are not available to rent")
    })
    @PostMapping(value = "/rentals", consumes= "application/json", produces = MediaTypes.HAL_JSON_VALUE)
    public RentalResource addRental(@RequestBody RentalResourceCreationRepresentation rentalResourceRepresentation) {
        Rental rental = videoRentalService.newRental(rentalResourceRepresentation.getCustomerId(),
                rentalResourceRepresentation.getDays(), rentalResourceRepresentation.getFilmIds());
        return new RentalResource(rental);
    }


    @PostMapping(value ="/rental/{id}", consumes = "application/json", produces = MediaTypes.HAL_JSON_VALUE)
    public RentalResource updateRental(@PathVariable String id,
                                       @RequestBody RentalResourceUpdateRepresentation rentalResourceRepresentation) {
        RentalId rentalId = RentalId.from(id);
        Rental rental = videoRentalService.updateRental(rentalId,
                rentalResourceRepresentation.days, rentalResourceRepresentation.getFilmIds());
        return new RentalResource(rental);
    }

    /**
     * A trimmed down representation of a Rental resource made for updating purposes. It just contains a handful of
     * attributes which are required. The rest are derived internally
     */
    public static class RentalResourceUpdateRepresentation {

        private Integer days;
        private List<Long> filmIds;

        public Integer getDays() {
            return days;
        }
        public void setDays(Integer days) {
            this.days = days;
        }
        public List<Long> getFilmIds() {
            return filmIds;
        }
        public void setFilmIds(List<Long> filmIds) {
            this.filmIds = filmIds;
        }
    }

    /**
     * A trimmed down representation of a Rental resource made for creation purposes. It just contains a handful of
     * attributes which are required. The rest are derived internally
     */
    public static class RentalResourceCreationRepresentation  extends RentalResourceUpdateRepresentation {

        private Long customerId;

        public Long getCustomerId() {
            return customerId;
        }
        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }
    }

}
