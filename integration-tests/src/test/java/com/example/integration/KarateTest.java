package com.example.integration;

import com.intuit.karate.junit5.Karate;

class KarateTest {
    @Karate.Test
    Karate runAll() {
        return Karate.run("classpath:features");
    }
}
