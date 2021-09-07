package gg.tater.bedrock.database;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class Credentials {

    private final String host, password;
    private final int port;
    private final int database;

}
