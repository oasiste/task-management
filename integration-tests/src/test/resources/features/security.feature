Feature: Authentication and task ownership enforcement

  Background:
    * url baseUrl

  Scenario: Reject missing and invalid bearer tokens
    Given path 'tasks'
    When method get
    Then status 401

    Given path 'tasks'
    And header Authorization = 'Bearer invalid.jwt.token'
    When method get
    Then status 401

  Scenario: User B cannot read, modify or delete user A's task
    * def userA = call read('classpath:features/login.feature')
    * def userB = call read('classpath:features/login.feature')
    * def bearerA = 'Bearer ' + userA.token
    * def bearerB = 'Bearer ' + userB.token

    Given path 'tasks'
    And header Authorization = bearerA
    And request { title: 'Private task', description: 'Only A', status: 'TODO' }
    When method post
    Then status 201
    * def taskId = response.id

    Given path 'tasks', taskId
    And header Authorization = bearerB
    When method get
    Then status 404

    Given path 'tasks', taskId
    And header Authorization = bearerB
    And request { title: 'new value', status: 'DONE' }
    When method put
    Then status 404

    Given path 'tasks', taskId
    And header Authorization = bearerB
    When method delete
    Then status 404

    Given path 'tasks', taskId
    And header Authorization = bearerA
    When method delete
    Then status 204
