
# A RESTful Video Rental Store API

## Description

This is an implementation of a Video Rental Store RESTful API. It is a level 3 RESTful API according to Richardson's maturity levels [1].
Put simply, it uses Hypermedia As The Engine Of Application State, a.k.a as HATEOAS.

The project is built using Spring Boot, and particularly Spring Data, Spring MVC and Spring HATEOAS. All resource representation is based
on HAL [2] which is nicely supported by Spring HATEOAS. All project layout and design adheres to Domain Driven Design principles [3].
I have tried to make the project as conformant to DDD as possible and avoid the dreadful Anemic Domain Model antipattern [4]. However it
is still not fully DDD, since for the sake of simplicity I let the JPA Entity annotations bleed into the Domain classes.


### Hypermedia and Profiles

HAL is a rather lightweight hypermedia format. At least as long as the hypermedia controls is concerned. With HAL, you can't really express whether a client
should use _POST_, _PUT_ or _DELETE_ with a link representation. Other hypermedia formats, like **Siren** are more advanced in this regard, and allow you to
express **actions**, which contain both the HTTP verb and request body to send to the endpoint. This is great since it enables you to provide a fully self
contained representation of a resource, and almost completely avoid external documentation. A representation containing all possible next actions and the
way to proceed with them.

As of this writing there is no Siren support in Spring, so I am stuck with HAL. There is a new project in Spring though, called Spring Affordances, which adds
support for a HAL extension called HAL-FORMS [5]. This extension provides support for the self contained resource representations described before.
Regretfully the project isn't publicly available yet.

In order to try to fill the semantic gap left by HAL, some external documentation is required. One which a proper description of the link relations, what they
mean, the HTTP verb that should be used, and a nice resource representation description. Some like to use Swagger to provide this documentation, but I feel it
is more REST-level-2 oriented. Others like to provide the documentation in plain english. I think it is much better to provide a machine-readable documentation.
One that we can use to build smart clients. Basically, this documentation is often called a **Profile**. A document that provides additional semantic information
on top of a Media Type without altering it [6]. There are several Profile formats available. _XMDP_ for instance, which is suitable for microformat based media
types. I will be looking at **ALPS**, which is JSON based and can be used later on with any Hypermedia Type of choice.


#### A look at ALPS

Spring Data REST supports ALPS out of the box. Nevertheless I am not using Spring Data REST, but Spring HATEOAS. Spring Data REST adds support to export Spring
Data Repositories directly. Sparing you from the creation of your own Spring MVC Controllers and Resources. As useful and convenient as Spring Data REST is, I
think it doesn't help much our DDD goals. In my humble opinion your Entity classes often end up having representation annotations, and I think it is better to
have the Domain Model as cleanly segregated from the infrastructure as possible. Thus, I am not the biggest fan of adding even more annotations to the Domain
Model classes. Remember they are already polluted with Java Persistence Annotations. Something I am not quite happy about.

Spring HATEOAS comes with several ALPS support classes. However, it lacks any kind of __profile__ controller, and all the logic provided by Spring Data REST.
You could think of using just Spring Data REST's ALPS support, and sticking to Spring HATEOAS for the rest. I have given it a try, but Spring Data REST's
controllers are too tightly coupled to the Spring Data Repositories and PersistentEntity classes. The whole idea of exposing Repositories as RESTful resources
seems to be deeply ingrained in Spring Data REST. You can have a look at the class org/springframework/data/rest/webmvc/ProfileController.java for more information.
I would say that the ALPS profile implementation in Spring Data doesn't lend itself too much for reuse in other RESTful projects that are not Repository based.
Or whose Resources are not coming directly from a Spring Data Repository.

A final idea worth exploring could be the creation of my own ProfileController, which would serve the final ALPS document. In order to make things easier, I
think I could maybe reuse the ResourceAssemblers and add the corresponding link to the ALP section for that resource. There should also be something in the custom
ProfileController that was able to discover the resources that would be served by the different controllers, and also to translate those resources into their
corresponding ALPS representation.


## REFERENCES

[1] Webber, Parastatidis, Robinson (2010). Web Friendliness and the Richardson Maturity Model.
    In "Rest in Practice, Hypermedia and Systems Architecture" (pp. 18-20). O'Reilly
[2] Kelly, Mike (2013). In HAL, Hypertext Application Language. http://stateless.co/hal_specification.html
[3] Evans, Eric. (2003). Domain Driven Design. Addison Wesley
[4] Fowler, Martin (2003). In Anemic Model. https://martinfowler.com/bliki/AnemicDomainModel.html
[5] Building richer hypermedia with Spring HATEOAS
    https://spring.io/blog/2018/01/12/building-richer-hypermedia-with-spring-hateoas
[6] Richardson, Leonard. Amundsen, Mike (2013). 8-Profiles. In 'RESTful Web APIs' (pp 133-155). O'Reilly


