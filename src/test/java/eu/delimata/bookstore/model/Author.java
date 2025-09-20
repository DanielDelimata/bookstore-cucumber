package eu.delimata.bookstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Author(
        @JsonProperty("id") int id,
        @JsonProperty("idBook") int idBook,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName
) {}
