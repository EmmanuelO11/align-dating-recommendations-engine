package com.aligndating.recommendationengine.rabbitmq

import com.aligndating.recommendationengine.config.RabbitMqConfig
import com.rabbitmq.client.Connection

interface RabbitMQConnectionFactory {
    fun connect(rabbitMqConfig: RabbitMqConfig): Connection
}