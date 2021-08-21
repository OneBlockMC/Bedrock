package gg.tater.bedrock.example;

import gg.tater.bedrock.database.BedrockDatabase;

public class ProjectImplementation {

    // Your implementation instance here
    private final BedrockDatabase database;

    public ProjectImplementation(BedrockDatabase database) {
        this.database = database;

        // Register the entity to the database instance.
        database.registerEntity(UserObject.class, new UserEntity());

        createAndPublish("Alex");
    }

    // Create the user and publish it across all listening redis instances.
    public void createAndPublish(String name) {
        UserObject user = new UserObject(name);
        database.publish(user);
    }
}
