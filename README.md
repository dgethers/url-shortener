# Project Title

Pet project for the url-shortener design question in Cracking the Coding Interview

## Getting Started

The project can be started from the command line by running the following Gradle command:
```gradlew run```

If you prefer to use an IDE to run the application, Application.kt has the main method
that will bootstrap the server.

There are several endpoints:

* `POST` / - **Create Url Mapping**\
Accepts: `application/json`\
Body: 
`{
    "fullUrl" : "http://www.something.com",
    "userId" : "your_alias"   
}`
Response:
`{
    "id" : "system_generated_id",
    "urlCode" : "unique_identifier_to_redirect_to_your_full_url",
    "fullUrl" : "the_url_to_redirect_to",
    "userId" : "your_user_id",
    "createDtm" : "creation_date_time_in_UTC"
}`

* `DELETE` /{urlCode} - **Delete url mapping**

* `GET` /mappings/{userId} = **Get all url mappings by user id**\
Response:
`[
    {
        "id" : "unique_visit_id",
        "clientBrowser" : "raw_user_agent",
        "ipAddress" : "client_ip_address",
        "urlCode" : "visited_url_code",
        "createDtm" : "url_visit_datetime"
    }
]`

* `GET` /{urlCode} - **This will redirect to the configured url mapping for the urlCode. The client browser,
ip address, and visit datetime will be recorded**

### Prerequisites

JDK 8\
Kotlin 1.3 (only for development)

### Installing

The application uses an embedded H2 database. Once the codebase is cloned. Only Kotlin is needed to develop

## Running the Tests

```gradlew test```

## Deployment

To generate the full executable jar with embedded server run:
`gradlew shadowJar`

The output file generated is: `build/libs/url-shortener-0.1-all.jar`

## Built with

* [Micronaut](https://micronaut.io/) - Web framework
* [Gradle](https://gradle.org/) - Build tool
* [H2](https://h2database.com/html/main.html) - Database