@books @positive
Feature: Manage books in the bookstore
  In order to keep the bookstore accurate and up to date,
  as an authorized user I want to add, view, edit, and remove books.

  Background:
    Given I have valid permissions to manage the bookstore
    And I have some unique data of a book

  @smoke @CRUD
  Scenario: Fast end-to-end path CRUD
    When I add a new book to the bookstore
    Then the book is visible in the list and in its details
    When I edit selected information of that book
    Then the changes are visible in the book details
    When I remove that book from the bookstore
    Then I should see that the book is no longer available to users

  @regression @create
  Scenario: Add a new book
    When I add a new book to the bookstore
    Then I see a confirmation that the book was added
    And the book details include the standard information expected in the bookstore

  @regression @read
  Scenario: View an existing book
    Given a book exists in the bookstore
    When I open the details of that book
    Then the details reflect the information provided when it was added

  @regression @update
  Scenario: Update a book
    Given a book exists in the bookstore
    When I change selected information of that book
    Then the updated information about the book is visible to users

  @regression @delete
  Scenario: Remove a book
    Given a book exists in the bookstore
    When I remove that book from the bookstore
    Then the book no longer appears in search or details