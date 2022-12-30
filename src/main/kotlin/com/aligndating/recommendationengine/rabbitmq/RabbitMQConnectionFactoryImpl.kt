package com.aligndating.recommendationengine.rabbitmq

import com.aligndating.recommendationengine.config.RabbitMqConfig
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory

class RabbitMQConnectionFactoryImpl : RabbitMQConnectionFactory {
    override fun connect(rabbitMqConfig: RabbitMqConfig): Connection {
        val factory = ConnectionFactory()
        return factory.newConnection(rabbitMqConfig.connectionString)
    }
}