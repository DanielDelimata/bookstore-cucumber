@books @negative
Feature: System responses to exceptional situations in the book catalog
  To protect data consistency and the user experience,
  the system should clearly communicate errors and block invalid operations.

  Background:
    Given I have valid permissions to manage the bookstore

  @regression @read
  Scenario Outline: Attempting to view a book that does not exist
    When I try to open the details of a book that <case_description>
    Then the system informs me that the book item is not available

    Examples:
      | case_description         |
      | was already removed      |
      | never existed            |
      | has an invalid identifier|

  @regression @create
  Scenario Outline: Rejecting an attempt to add a book without required information
    When I try to add a new book with missing data <missing>
    Then the addition of a book is rejected with a clear message indicating which data must be provided

    Examples:
      | missing           |
      | title             |
      | page count        |
      | description       |

  @regression @update
  Scenario: Rejecting an ambiguous update
    Given a book exists in the bookstore
    When I attempt to update the book in an ambiguous or inconsistent way
    Then the update of book is rejected with guidance on how to correctly specify the item to change

  @regression @delete
  Scenario: Idempotent removal - repeating the operation
    Given a book exists in the bookstore
    And I have removed that book from the bookstore
    When I attempt to remove the same book again
    Then the system informs me the book item is already unavailable and no further changes are made