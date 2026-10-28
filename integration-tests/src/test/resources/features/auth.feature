Feature: Registration and JWT login via API Gateway

  Background:
    * url baseUrl
    * def UUID = Java.type('java.util.UUID')
    * def email = 'e2e-' + UUID.randomUUID().toString() + '@inteview.com'
    * def credentials = { email: '#(email)', password: 'password123' }

  Scenario: Register, log in, and reject incorrect password
    Given path 'auth', 'register'
    And request credentials
    When method post
    Then status 201

    Given path 'auth', 'login'
    And request credentials
    When method post
    Then status 200
    And match response.accessToken == '#string'
    And match response.tokenType == 'Bearer'

    Given path 'auth', 'login'
    And request { email: '#(email)', password: 'wrong-password' }
    When method post
    Then status 401

  Scenario: Duplicate registration is rejected
    Given path 'auth', 'register'
    And request credentials
    When method post
    Then status 201

    Given path 'auth', 'register'
    And request credentials
    When method post
    Then status 409

  Scenario: Invalid registration email is rejected
    Given path 'auth', 'register'
    And request { email: 'not-an-email', password: 'password123' }
    When method post
    Then status 400
