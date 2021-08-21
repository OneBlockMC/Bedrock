package gg.tater.bedrock.example;

import gg.tater.bedrock.database.pubsub.entity.RedisEntity;

public class UserEntity implements RedisEntity<UserObject> {

    // Execution on each redis instance that is listening.
    @Override
    public void handle(UserObject user) {
        System.out.println(user.getName());
    }

    // If the object should be put in a redis hash or not.
    @Override
    public boolean cache() {
        return true;
    }

    @Override
    public String hashName() {
        return "users";
    }

    @Override
    public String key(UserObject user) {
        return user.getUuid().toString();
    }
}
