package org.saigon.striker.integration;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BasicITLocalTest extends BasicIT {

    @LocalServerPort
    private int port;

    @Override
    protected String serverUrl() {
        return localhostServerUrl(port);
    }
}
