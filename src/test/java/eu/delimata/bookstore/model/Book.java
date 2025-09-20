package eu.delimata.bookstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Book(
        @JsonProperty("id") int id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("pageCount") int pageCount,
        @JsonProperty("excerpt") String excerpt,
        @JsonProperty("publishDate") String publishDate
) {}
