package com.imposter.game.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.imposter.game.repository")
@EnableMongoAuditing
public class MongoConfig {
    // MongoDB configuration will be added as needed
}