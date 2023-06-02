package tqs.project.backend.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import tqs.project.backend.data.collection_point.CollectionPoint;
import tqs.project.backend.data.collection_point.CollectionPointDto;
import tqs.project.backend.data.partner.Partner;
import tqs.project.backend.data.user.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "application-integrationtest.properties")
public class MainWebRestControllerTestIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRegisterACP() {
        String url = "http://localhost:" + port + "/api/main/registerACP";

        Partner partner = new Partner();
        partner.setUsername("username");
        partner.setPassword("password");
        String passwordCheck = "password";
        String zipcode = "3105-325";
        String city = "city123";

        CollectionPointDto cpDto = new CollectionPointDto();
        cpDto.setPartner(partner);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CollectionPointDto> requestEntity = new HttpEntity<>(cpDto, headers);

        ResponseEntity<CollectionPoint> response = restTemplate.exchange(
                url + "?passwordCheck={passwordCheck}&zipcode={zipcode}&city={city}",
                HttpMethod.POST,
                requestEntity,
                CollectionPoint.class,
                passwordCheck,
                zipcode,
                city
        );

        assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void testLogin() {
        String url = "http://localhost:" + port + "/api/main/login";
        String username = "testuser";
        String password = "testpassword";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(
                url + "?username={username}&password={password}",
                HttpMethod.GET,
                requestEntity,
                User.class,
                username,
                password
        );

        assertEquals(200, response.getStatusCodeValue());
    }
}