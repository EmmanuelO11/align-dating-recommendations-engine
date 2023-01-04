package com.aligndating.recommendationengine.features.recommendation.consumer

import com.aligndating.recommendationengine.extensions.getLogger
import com.aligndating.recommendationengine.features.recommendation.engine.RecommendationEngine
import com.aligndating.recommendationengine.features.recommendation.events.*
import com.google.gson.Gson
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.Connection
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

object RecommendationEngineConsumer {

    private val logger = getLogger()

    private val gson = Gson()

     fun startAllConsumers(
        rabbitMqConnection: Connection,
        coroutineScope: CoroutineScope,
        recommendationEngine: RecommendationEngine
    ) {
        startConsumerForNewUserAddedEvents(rabbitMqConnection, coroutineScope, recommendationEngine)
        startConsumerForUserRemovedEvents(rabbitMqConnection, coroutineScope, recommendationEngine)
        startConsumerForUserDistancePreferenceChanged(rabbitMqConnection, coroutineScope, recommendationEngine)
        startConsumerForUserGenderPreferenceChanged(rabbitMqConnection, coroutineScope, recommendationEngine)
        startConsumerForUserSuperLikedEvents(rabbitMqConnection, coroutineScope, recommendationEngine)
        startConsumerForUserAccountDeletedEvents(rabbitMqConnection, coroutineScope, recommendationEngine)
    }

    private fun startConsumerForNewUserAddedEvents(
        rabbitMqConnection: Connection,
        coroutineScope: CoroutineScope,
        recommendationEngine: RecommendationEngine
    ) {
        logger.info("Starting consumer for NewUserAddedEvents")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForNewUserAdded"

        channel.queueDeclare(
            QUEUE_NEW_USER_ADDED, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
            val event = gson.fromJson(message, NewUserAddedToAreaEvent::class.java)
            coroutineScope.launch {
                recommendationEngine.computeAndSave(
                    event.userId,
                    update = false
                )
                recommendationEngine.computeForUsersInArea(
                    lat = event.lat,
                    lng = event.lng,
                    excludeUsers = listOf(event.userId)
                )
            }
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_NEW_USER_ADDED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private  fun startConsumerForUserRemovedEvents(
        rabbitMqConnection: Connection,
        coroutineScope: CoroutineScope,
        recommendationEngine: RecommendationEngine
    ) {
        logger.info("Starting consumer for UserRemovedEvent")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserRemovedEvent"

        channel.queueDeclare(
            QUEUE_USER_REMOVED_FROM_AREA, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
            val event = gson.fromJson(message, AccountDeletedEvent::class.java)
            coroutineScope.launch {
                recommendationEngine.computeForUsersInArea(
                    lat = event.lat,
                    lng = event.lng,
                    excludeUsers = emptyList()
                )
            }
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_REMOVED_FROM_AREA, true, consumerTag, deliverCallback, cancelCallback)
    }

    private  fun startConsumerForUserDistancePreferenceChanged(
        rabbitMqConnection: Connection,
        coroutineScope: CoroutineScope,
        recommendationEngine: RecommendationEngine
    ) {
        logger.info("Starting consumer for UserDistancePreferenceChanged")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserDistancePreferenceChangedEvent"

        channel.queueDeclare(
            QUEUE_USER_DISTANCE_PREFERENCE_CHANGED, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
            val event = gson.fromJson(message, UserDistancePreferenceChangedEvent::class.java)
            coroutineScope.launch {
                recommendationEngine.computeAndSave(
                    userId = event.userId,
                    update = true
                )
                // TODO make sure other users are not affected
            }
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_DISTANCE_PREFERENCE_CHANGED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private  fun startConsumerForUserGenderPreferenceChanged(
        rabbitMqConnection: Connection,
        coroutineScope: CoroutineScope,
        recommendationEngine: RecommendationEngine
    ) {
        logger.info("Starting consumer for UserGenderPreferenceChanged")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserGenderPreferenceChangedEvent"

        channel.queueDeclare(
            QUEUE_USER_GENDER_PREFERENCE_CHANGED, false, false, false, null
        )
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
            val event = gson.fromJson(message, UserGenderPreferenceChangedEvent::class.java)
            coroutineScope.launch {
                recommendationEngine.computeAndSave(
                    userId = event.userId,
                    update = true
                )
                recommendationEngine.computeForUsersInArea(
                    lat = event.lat,
                    lng = event.lng,
                    excludeUsers = listOf(event.userId)
                )
            }
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_GENDER_PREFERENCE_CHANGED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private  fun startConsumerForUserSuperLikedEvents(
        rabbitMqConnection: Connection,
        coroutineScope: CoroutineScope,
        recommendationEngine: RecommendationEngine
    ) {
        logger.info("Starting consumer for UserSuperLikedEvents")
        val channel = rabbitMqConnection.createChannel()
        val consumerTag = "recommendationConsumerForUserSuperLikedEvent"

        channel.queueDeclare(QUEUE_USER_SUPER_LIKED, false, false, false, null)
        val deliverCallback = DeliverCallback { ct: String, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            logger.info("[$ct] Received message: '$message'")
            val event = gson.fromJson(message, UserSuperLikedEvent::class.java)
            coroutineScope.launch {

                recommendationEngine.getUserLocationAndComputeInArea(
                    userId = event.likedBy
                )
            }
        }
        val cancelCallback = CancelCallback { ct: String? ->
            logger.info("[$ct] was canceled")
        }

        channel.basicConsume(QUEUE_USER_SUPER_LIKED, true, consumerTag, deliverCallback, cancelCallback)
    }

    private  fun startConsumerForUserAccountDeletedEvents(
        rabbitMqConnection: Connection,
        coroutineScope: CoroutineScope,
        recommendationEngine: RecommendationEngine
    ) {
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