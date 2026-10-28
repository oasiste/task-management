Feature: Task lifecycle through API Gateway

  Background:
    * url baseUrl
    * def user = call read('classpath:features/login.feature')
    * def authHeader = 'Bearer ' + user.token
    * def taskInput = { title: 'Karate integration test', description: 'Created through gateway', status: 'TODO', dueDate: '2026-12-01' }

  Scenario: Create, list, retrieve, update and delete own task
    Given path 'tasks'
    And header Authorization = authHeader
    And request taskInput
    When method post
    Then status 201
    And match response.id == '#uuid'
    And match response.title == taskInput.title
    And match response.status == 'TODO'
    * def taskId = response.id

    Given path 'tasks'
    And header Authorization = authHeader
    When method get
    Then status 200
    And match response == '#[]'
    And match response contains deep { id: '#(taskId)' }

    Given path 'tasks', taskId
    And header Authorization = authHeader
    When method get
    Then status 200
    And match response.id == taskId

    Given path 'tasks', taskId
    And header Authorization = authHeader
    And request { title: 'Updated task', description: 'Done', status: 'DONE', dueDate: '2026-12-01' }
    When method put
    Then status 200
    And match response.id == taskId
    And match response.status == 'DONE'

    Given path 'tasks', taskId
    And header Authorization = authHeader
    When method delete
    Then status 204

    Given path 'tasks', taskId
    And header Authorization = authHeader
    When method get
    Then status 404

  Scenario: Invalid blank title is rejected
    Given path 'tasks'
    And header Authorization = authHeader
    And request { title: '', status: 'TODO' }
    When method post
    Then status 400
