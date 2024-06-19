package se.magnus.springcloud.eurekaserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.cloud.config.enabled=false"})
class EurekaServerApplicationTests {

	@Value("${app.eureka-username}")
	private String username;
	@Value("${app.eureka-password}")
	private String password;
	@LocalServerPort
	private int port;
	private TestRestTemplate testRestTemplate;

	@Test
	void contextLoads() {
	}

	@Autowired
	void setTestRestTemplate(TestRestTemplate testRestTemplate) {
		this.testRestTemplate = testRestTemplate.withBasicAuth(username, password);
	}

	@Test
	void catalogLoads() {
		String expectedReponseBody = "{\"applications\":{\"versions__delta\":\"1\",\"apps__hashcode\":\"\",\"application\":[]}}";
		ResponseEntity<String> entity = testRestTemplate.getForEntity("http://localhost:" + port + "/eureka/apps", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertEquals(expectedReponseBody, entity.getBody());
	}

	@Test
	void healthy() {
		String expectedReponseBody = "{\"status\":\"UP\"}";
		ResponseEntity<String> entity = testRestTemplate.getForEntity("http://localhost:" + port + "/actuator/health", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertEquals(expectedReponseBody, entity.getBody());
	}
}
