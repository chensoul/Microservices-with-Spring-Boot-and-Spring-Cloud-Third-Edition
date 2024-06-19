package se.magnus.microservices.composite.product;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.RabbitMQContainer;

public abstract class RabbitmqTestBase {
	@ServiceConnection
	private static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.13.0-management-alpine");

	static {
		rabbitMQContainer.start();
	}

}
