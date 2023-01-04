package com.aligndating.recommendationengine.di

import com.aligndating.recommendationengine.features.location.source.LocationDataSource
import com.aligndating.recommendationengine.features.location.source.LocationDataSourceImpl
import com.aligndating.recommendationengine.config.AppConfig
import com.aligndating.recommendationengine.database.DatabaseFactory
import com.aligndating.recommendationengine.database.DatabaseFactoryImpl
import com.aligndating.recommendationengine.features.accounts.source.AccountsDataSource
import com.aligndating.recommendationengine.features.accounts.source.AccountsDataSourceImpl
import com.aligndating.recommendationengine.features.likes.source.dislike.DislikeDataSource
import com.aligndating.recommendationengine.features.likes.source.dislike.DislikeDataSourceImpl
import com.aligndating.recommendationengine.features.likes.source.like.LikeDataSource
import com.aligndating.recommendationengine.features.likes.source.like.LikeDataSourceImpl
import com.aligndating.recommendationengine.features.recommendation.engine.RecommendationEngine
import com.aligndating.recommendationengine.features.recommendation.engine.RecommendationEngineImpl
import com.aligndating.recommendationengine.features.recommendation.source.UserRecommendationsDataSource
import com.aligndating.recommendationengine.features.recommendation.source.UserRecommendationsDataSourceImpl
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
    single<AccountsDataSource> { params -> AccountsDataSourceImpl(params.get()) }
    single<LocationDataSource> { params -> LocationDataSourceImpl(params.get()) }
    single<LikeDataSource> { params -> LikeDataSourceImpl(params.get()) }
    single<DislikeDataSource> { params -> DislikeDataSourceImpl(params.get()) }
    single<UserRecommendationsDataSource> { params -> UserRecommendationsDataSourceImpl(params.get()) }
    single<RecommendationEngine> { RecommendationEngineImpl(get(), get(), get(), get(), get(), get()) }
}