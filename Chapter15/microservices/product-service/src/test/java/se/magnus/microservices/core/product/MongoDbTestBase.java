package se.magnus.microservices.core.product;

import java.time.Duration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;

public abstract class MongoDbTestBase {

	@ServiceConnection
	private static MongoDBContainer database = new MongoDBContainer("mongo:4.4").withStartupTimeout(Duration.ofSeconds(300));

	@ServiceConnection
	private static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.13.0-management-alpine").withStartupTimeout(Duration.ofSeconds(300));

	static {
		database.start();
		rabbitMQContainer.start();
	}

}
