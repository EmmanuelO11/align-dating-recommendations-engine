package com.aligndating.recommendationengine.di

import com.aligndating.recommendationengine.features.location.source.LocationDataSource
import com.aligndating.recommendationengine.features.location.source.LocationDataSourceImpl
import com.aligndating.recommendationengine.config.AppConfig
import com.aligndating.recommendationengine.database.DatabaseFactory
import com.aligndating.recommendationengine.database.DatabaseFactoryImpl
import com.aligndating.recommendationengine.rabbitmq.RabbitMQConnectionFactory
import com.aligndating.recommendationengine.rabbitmq.RabbitMQConnectionFactoryImpl
import org.koin.dsl.module
import org.koin.dsl.single

@Suppress("EXPERIMENTAL_API_USAGE")

val appModule = module {

    // Backend Config
    @Suppress("OPT_IN_USAGE")
    single<AppConfig>()
    single<DatabaseFactory> { DatabaseFactoryImpl() }
    single<RabbitMQConnectionFactory> { RabbitMQConnectionFactoryImpl() }
    single<LocationDataSource> { params -> LocationDataSourceImpl(params.get()) }
}