@allure.label.epic:BookstoreManagement
@allure.label.feature:AuthorsPositiveTests
Feature: Manage authors in the bookstore
  In order to keep the bookstore accurate and informative,
  as an authorized user I want to add, view, edit, and remove authors.

  Background:
    Given I have valid permissions to manage the bookstore
    And I have some unique data of an author

  @smoke @CRUD
  Scenario: Fast end-to-end path CRUD
    When I add a new author to the bookstore
    Then the author is visible in the list and in the author details
    When I edit selected information for that author
    Then the changes are visible in the author details
    When I remove that author from the bookstore
    Then the author is no longer available to users

  @regression @create
  Scenario: Add a new author
    When I add a new author to the bookstore
    Then I see a confirmation that the author was added
    And the author details include the first name and the last name

  @regression @read
  Scenario: View an existing author
    Given an author exists in the bookstore
    When I open the details of that author
    Then the details reflect the information provided when the author was added

  @regression @update
  Scenario: Update an author
    Given an author exists in the bookstore
    When I change selected information for that author
    Then the updated information about the author is visible to users

  @regression @delete
  Scenario: Remove an author
    Given an author exists in the bookstore
    When I remove that author from the bookstore
    Then the author no longer appears in search or details