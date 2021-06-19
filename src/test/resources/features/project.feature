Feature: Requests for projects, epic, story, label and workspace endpoint

  Scenario: Get a Project
    Given I build "GET" request
    When I execute "projects/{projectId}" request
    Then the response status code should be "OK"

  Scenario: Get a Epic
    Given I build epic request
    When I add "epics/{epicId}" to epic request
    Then the response status code to epic request should be "200"

  Scenario: Get a Story
    Given I build story request
    When I add to story request "stories/{storyId}"
    Then the response status code should be "200" to story request

  Scenario: Get a label
    Given I build label request
    When I add to label request "labels/{labelId}"
    Then the response status code should be "200" to label request

  Scenario: Get a workspace
    Given I build workspace request
    When I add to workspace request "/my/workspaces/{workspaceId}"
    Then the response status code should be "200" to workspace request