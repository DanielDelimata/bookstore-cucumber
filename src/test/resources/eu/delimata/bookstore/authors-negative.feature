@authors @negative
Feature: System responses to exceptional situations in the author catalog
  To protect data consistency and the user experience,
  the system should clearly communicate errors and block invalid operations.

  Background:
    Given I have valid permissions to manage the bookstore

  @regression @read
  Scenario Outline: Attempting to view an author that does not exist
    When I try to open the details of an author that <case_description>
    Then the system informs me that the author item is not available

    Examples:
      | case_description         |
      | was already removed      |
      | never existed            |
      | has an invalid identifier|

  @regression @create
  Scenario Outline: Rejecting an attempt to add an author without required information
    When I try to add a new author with missing data <missing>
    Then the addition of an author is rejected with a clear message indicating which data must be provided

    Examples:
      | missing        |
      | display name   |
      | short bio      |
      | date of birth  |

  @regression @update
  Scenario: Rejecting an ambiguous update
    Given an author exists in the bookstore
    When I attempt to update the author in an ambiguous or inconsistent way
    Then the update of author is rejected with guidance on how to correctly specify the item to change

  @regression @delete
  Scenario: Idempotent removal - repeating the operation
    Given an author exists in the bookstore
    And I have removed that author from the bookstore
    When I attempt to remove the same author again
    Then the system informs me the author item is already unavailable and no further changes are made