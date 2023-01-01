package com.aligndating.recommendationengine.features.recommendation.consumer

import com.aligndating.recommendationengine.extensions.getLogger
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import java.nio.charset.StandardCharsets

object RecommendationEngineConsumer {

    private val logger = getLogger()

    suspend fun startAllConsumers(rabbitMqConnection: Connection) {
        startConsumerForNewUserAddedEvents(rabbitMqConnection)
        startConsumerForUserRemovedEvents(rabbitMqConnection)
        startConsumerForUserDistancePreferenceChanged(rabbitMqConnection)
        startConsumerForUserGenderPreferenceChanged(rabbitMqConnection)
        startConsumerForUserSuperLikedEvents(rabbitMqConnection)
        startConsumerForUserAccountDeletedEvents(rabbitMqConnection)
    }

    private suspend fun startConsumerForNewUserAddedEvents(rabbitMqConnection: Connection) {
        logger.info("Starting consumer for NewUserAddedEvents")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForNewUserAdded"

        channel.queueDeclare(
            QUEUE_NEW_USER_ADDED, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            // TODO here
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_NEW_USER_ADDED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private suspend fun startConsumerForUserRemovedEvents(rabbitMqConnection: Connection) {
        logger.info("Starting consumer for UserRemovedEvent")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserRemovedEvent"

        channel.queueDeclare(
            QUEUE_USER_REMOVED_FROM_AREA, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            // TODO here
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_REMOVED_FROM_AREA, true, consumerTag, deliverCallback, cancelCallback)
    }

    private suspend fun startConsumerForUserDistancePreferenceChanged(rabbitMqConnection: Connection) {
        logger.info("Starting consumer for UserDistancePreferenceChanged")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserDistancePreferenceChangedEvent"

        channel.queueDeclare(
            QUEUE_USER_DISTANCE_PREFERENCE_CHANGED, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            // TODO here
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_DISTANCE_PREFERENCE_CHANGED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private suspend fun startConsumerForUserGenderPreferenceChanged(rabbitMqConnection: Connection) {
        logger.info("Starting consumer for UserGenderPreferenceChanged")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserGenderPreferenceChangedEvent"

        channel.queueDeclare(
            QUEUE_USER_GENDER_PREFERENCE_CHANGED, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            // TODO here
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_GENDER_PREFERENCE_CHANGED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private suspend fun startConsumerForUserSuperLikedEvents(rabbitMqConnection: Connection) {
        logger.info("Starting consumer for UserSuperLikedEvents")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserSuperLikedEvent"

        channel.queueDeclare(QUEUE_USER_SUPER_LIKED, false, false, false, null)
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            // TODO here
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_SUPER_LIKED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private suspend fun startConsumerForUserAccountDeletedEvents(rabbitMqConnection: Connection) {
        logger.info("Starting consumer for UserAccountDeletedEvents")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserAccountDeletedEvent"

        channel.queueDeclare(QUEUE_ACCOUNT_DELETED, false, false, false, null)
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            // TODO here
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_ACCOUNT_DELETED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private const val QUEUE_NEW_USER_ADDED = "newUserAdded"
    private const val QUEUE_USER_REMOVED_FROM_AREA = "userRemovedFromArea"
    private const val QUEUE_USER_DISTANCE_PREFERENCE_CHANGED = "userDistancePreferenceChanged"
    private const val QUEUE_USER_GENDER_PREFERENCE_CHANGED = "userGenderPreferenceChanged"
    private const val QUEUE_USER_SUPER_LIKED = "userSuperLiked"
    private const val QUEUE_ACCOUNT_DELETED = "accountDeleted"
}