@ignore
Feature: Helper to register and authenticate

  Scenario:
    * url baseUrl
    * def UUID = Java.type('java.util.UUID')
    * def email = 'karate-' + UUID.randomUUID().toString() + '@interview.com'
    * def credentials = { email: '#(email)', password: 'password123' }
    Given path 'auth', 'register'
    And request credentials
    When method post
    Then status 201
    Given path 'auth', 'login'
    And request credentials
    When method post
    Then status 200
    And match response.accessToken == '#string'
    * def token = response.accessToken
