package gg.tater.bedrock.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserObject {

    private final UUID uuid = UUID.randomUUID();

    private String name;

}
