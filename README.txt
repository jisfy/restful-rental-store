**************              Video Rental Store       **************************

Please have a look at docs/README.txt

Bearing in mind how controversial is the debate about "anemic domain model" vs "domain driven design", and not having
a clue about which school of thought you guys are coming from, I decided to do both. You can find the
as-domain-driven-as-possible design in the non-anemic-hal branch of this project. It certainly has a nicer package
layout, but it is not fully DDD, since I left the JPA Entity annotations bleed into the Domain classes.
Please notice that the docs/README.txt file of those two branches also differ. For more info about the
anemic design debate, please see [1]

[1] Fowler, Martin (2003). In Anemic Model. https://martinfowler.com/bliki/AnemicDomainModel.html




///////////////////////////////////////////////////////////////////////////////

Since I am using HAL which is a really lightweight hypermedia format, at least as long as the hypermedia controls are
concerned. Notice that you can't really express with HAL whether a client should use a POST, a PUT or DELETE with a
HAL link representation. Other hypermedia formats, like Siren are more advanced in this regard, and allow you to
express "actions", which contain the HTTP verb and request body to send to the endpoint. This is great since it enables
you to provide a self contained representation of a resource. One with all possible next actions and the way to proceed
with them. Since there is no Siren support for now in Spring, I am stuck with HAL. There is a new project in Spring,
called Spring Affordances, which adds support for some HAL extension called HAL-FORMS [2]. This extension provides
support for self contained resource representations as described before. However the project hasn't been publicly
released yet. With all of this in mind, the only way I can provide semantic information about the link relations and
semantic descriptors is by means of some out of bound documentation. Some provide this documentation in plain english,
but it is much better if this documentation can be processed by a machine, since that enables you to build smart clients.
ALPS is spec which supports just that. What is usually called a Profile [3].

I have been looking into ALPS in order to provide machine readable semantic information for the API. However, it seems
ALPS support comes with spring data rest, not so much with spring hateoas. I don't really want spring data rest to
kick in and provide the controller endpoints for the different methods in my repositories. I want to keep my own.
I am looking at the classes
spring-data-rest-webmvc/

src/main/java/org/springframework/data/rest/webmvc/alps/RootResourceInformationToAlpsDescriptorConverter.java
org/springframework/data/rest/webmvc/alps/AlpsController.java, org/springframework/data/rest/webmvc/ProfileController.java
from the Spring Data Rest project, which seem to be the ones in charge of Alps support.

org/springframework/data/rest/webmvc/ProfileController.java This Controller creates the top level profile URI, which
will include links to the corresponding profiles for the different resources.

There doesn't seem to be an easy way to add ALPS support to an existing Spring HATEOAS project. The main ALPS Controller in 
the Spring Data Rest project, ProfileController, already relies on the Spring Data Repositories class. Everything is Repository
and PersistentEntity based. I am using ResourceSupport classes in my Spring HATEOAS project, so there isn't an easy translation
into PersistentEntity classes. I would say that the ALPS profile implementation in Spring Data doesn't lend itself much to
reuse for other RESTful projects that are not Repository based. Or whose Resources are not coming directly from a Spring Data
Repository.

I could maybe try to create my own ProfileController which will serve the ALPS document. In order to make things easier, I 
think I could maybe reuse the ResourceAssemblers and add the corresponding link to the ALP section for that resource.
There should also be something in the ProfileController that is able to discover the resources that will be served by the
different controllers, and also to translate those resources into their corresponding ALPS representation.


================================================== REFERENCES =================================================
[2] Building richer hypermedia with Spring HATEOAS
    https://spring.io/blog/2018/01/12/building-richer-hypermedia-with-spring-hateoas

[3] Richardson, Leonard. Amundsen, Mike. 8-Profiles. In 'RESTful Web APIs' (pp 133-155). O'Reilly 2013.