**************              Video Rental Store       **************************

- I have decided to use Spring Boot because I am already familiar with it, and it is no so different from DropWizard's service offering. It is quite straightforward,
  provides metrics, packaging to run the solution as a command line jar, very 12 factor friendly I would say.

- Disclaimer, I really believe in Agile Methodologies, "building the right thing and building the thing right", Minimum Viable Products to prove hypothesis, etc.
  With that in mind, and considering the lack of a real Product Owner, from now on I will try to make domain related decisions that keep the problem as
  simple as possible. There are many things that sound reasonable to me and worth doing, but that could simply result in over engineering the solution and
  something nobody really wants. Unnecessary complexity, basically.

- Even though it is significantly more difficult to build, I have decided to go for a Level 3 RESTful solution according to Richardson's maturity model.
  Please see [1] and [2]. Notice that payment, or return links will show up in the response only when those operations are possible. This has led me to
  use application/hal+json as the media type. This isn't me most human readable format, to be honest, but it is one with decent support in Spring, and
  that speeds up implementation significantly. There was a version of the code without 100% hal support. It used HAL's links, but embedded resources the
  regular way, as in any other application/json. If you want to have a look at that, you need to go to commit 0d173c97b7ea5672e82c94843d591601662f1995
  For more information on HAL, please see [3]

- In this branch "non-anemic-hal" I have tried to refactor the original anemic model and adhere to Domain Driven Design recommendations. I tried to have
  a domain model based purely on POJOS, that helps build an ubiquitous language. It will also make testing easier. Notice though, that JPA Entity
  annotations are still present in the POJOS. Also JPA sometimes forces you to some accessor modifier on methods and constructors. So I tried to set them
  to the most private modifier that JPA would let me. Also having a more relaxed modifiers help me test the logic. Even though Spring is somewhat DDD
  friendly, Repositories and Entities from Spring Data, etc are merely annotated interfaces, some consider that not DDD at all, so I moved Repositories
  into the infrastructure layer. There is a single Bounded Context. Having separate bounded contexts for Customer Management, Inventory, etc, required
  a significant effort. However, I belong to the school of thought of "build a small monolith first, then split into microservices.". For more info on
  my take on this, please see [4].

- Assuming the video rental store is a physical shop. This means there will be a stock with DVDs or some other physical format that is delivered to customers.
  Unlike an online "rental store", in which there would be no limit to the number of customers that can rent a movie at the same time. Of course, there could
  be legal issues that limited that case too. The problem specs make me think it is a physical shop with a stock, because in an online business one would simply
  pay per view. Not so much rent a movie for a given number of days.

- Assuming complete rental returns. This is, a customer can't choose to return 2 movies from a rental that originally comprised 4 movies. Rental returns are atomic.
- Assuming all movies that are part of a rental have the same rental period, and the same starting rental date. Combined rentals are atomic regarding duration.
- These two points described above would probably require introducing a new entity, RentalItem, that would reference a Film and had its own rental start date and
  status. For now, rentals are kept simple and reference films directly.

- Having a Film reference a Rental directly, seems kind of weird to me, so I am introducing a RentalItem after all.

- Even though a physical rental store is assumed, there is still optimistic locking for movies (just in case). Customers are supposed to go to a shelf and pick up
  whatever film they want. When all copies of a movie are gone, their corresponding box is no longer on the shelves. That limits the chances of somebody trying to
  rent a movie that was gone in the middle of the operation. Nevertheless, placing a rental order can still fail due to movies not being available. This should
  cover the case where we have a physical shop that offers an online book and collect service to customers.

- I had originally included a PointCard entity to represent the customers' card. However, since there aren't so many requirements around this entity, I finally
  decided to embed it into the Customer entity in order to simplify the model. I have neither included an endpoint for that entity, that would enable the store
  clerk to add/withdraw points manually from a customer card. After all, adding points to a card happens automtically. When proven useful, I could go back to
  the original design, and have PointCard as a first class citizen. For instance, if point cards expire, if the customer can have more than one card, points can
  be withdrawn, etc.

- Added a Payment Entity to better highlight the necessary next step in a rental operation. This is, after failing to return an overdue rental, the process
  could return a payment link to let the user know that a payment is required. The same goes for a new rental order that is awaiting a payment. The rental
  order could include a payment link.

- I have lighter-weight version of the underlying business entities for their representation as resources. However, even though some attributes are left out of
  their representation to avoid verbosity, they can be easily included.

- Assuming the customer will always pay whatever surcharges. Otherwise we should probably keep a balance and maybe not allow him to rent anything until his balance
  goes back to normal.

- Performing a payment currently only returns the modified Payment resource representation. It could well return a receipt resource in the future.

- It maybe clearer to return a payload with the corresponding payment link when the customer tries to return a rental which is overdue. As of today, it simply
  returns a 405 Method not allowed and a message. That forces you to check the rental resource again to find out what is wrong.

- Link relations "pay" and "return" are added to the Rentals. Issuing a POST to the pay's relation HREF should result in th
  corresponding payment being performed. Issuing a DELETE to the return's relation HREF should result in the rental being
  returned (all its films). There is also a modify link rel that will show up only when the Rental is in AWAITING_PAYMENT
  state

- I have used the DELETE verb in the Rental resource for the return operation, as in some REST literature. I still need to cancel Rentals, which would also fit
  in the DELETE verb purpose. Since rental cancellation is not yet implemented I am leaving it for now. I wouldn't like POSTing to the Rental resource just to
  return it. DELETE can't use parameters. I could also reuse DELETE for cancellation and return but disregarding semantics but I would lose expressiveness. This
  would mean always performing a delete operation on the entities, which would be kind of overloaded and carry out a cancellation or return depending on its
  internal state. The current return implementation does not unbind the Rental from the URI as per the DELETE spec. It doesn't return a 404 from then on.

**** References

[1] Webber, Parastatidis, Robinson (2010). Hypermedia Services. In "Rest in Practice, Hypermedia and Systems Architecture" (pp. 93-128). O'Reilly
[2] Webber, Parastatidis, Robinson (2010). Web Friendliness and the Richardson Maturity Model.
    In "Rest in Practice, Hypermedia and Systems Architecture" (pp. 18-20). O'Reilly
[3] Kelly, Mike (2013). In HAL, Hypertext Application Language. http://stateless.co/hal_specification.html
[4] Newman, Sam (2015). 5. Splitting the Monolith, In Building Microservices. O'Reilly

**** TODO

- The solution is not yet production ready. In order to run it in production, it needs to be thoroughly tested with a real RBDMS, it currently uses an in
  memory H2 instance. A decent DDL needs to be created for the database, with proper indexes where required. Needless to say that load testing needs to be performed
  with real data too.
- JPA entities need to be revisited. There are some which use EAGER fetching, which is okay when data is small, but will definitely hurt performance overtime.
  Also need to take a serious look at the queries that are being run behind the scenes by Hibernate. Often a simple operation ends up being two or three queries, or
  a rather monstrous one, requiring tweaking and even named queries sometimes.
- I need to make sure that Optimistic locking and transactions are working correctly. I can't do this easily with the H2 DB instance, which is
  single threaded. But I need to test that everything fails correctly when some films are no longer available in the db when
  another rental is in flight. That should throw a 409 conflict too.
- Spring Data Repository Pagination is required for unbounded endpoints.
- Reorganize the source code into additional packages
- No endpoint security. As of today, endpoints are open to the public. This needs to be revisited before going in production. Think about which endpoints can be
  public, etc.
- enable all endpoint metrics
- Consider semantic versioning of the API.
- Replace application/hal+json with Domain Specific Hypermedia type?. Maybe application/vnd.video-rental+json.
- Need to add Etag support for conditional updates or caching GETs
- Make relation names be URLs referencing their corresponding documentation
- I would like to add some integration tests for the RESTful API. Only comprising the HTTP layer with all Repositories
  mocked, which they already are.


**** RUNNING THE SOLUTION

In order to run the solution, please type "mvn spring-boot:run" in the command line.
For further documentation on how to use the endpoints, please access http://localhost:8080/swagger-ui.html
Several Customers, Films and Rentals are created out of the box for you to try them out
