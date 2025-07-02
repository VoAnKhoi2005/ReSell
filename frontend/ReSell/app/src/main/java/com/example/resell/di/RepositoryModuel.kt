package com.example.resell.di

import com.example.resell.repository.AddressRepository
import com.example.resell.repository.AddressRepositoryImpl
import com.example.resell.repository.CategoryRepository
import com.example.resell.repository.CategoryRepositoryImpl
import com.example.resell.repository.FavoriteRepository
import com.example.resell.repository.FavoriteRepositoryImpl
import com.example.resell.repository.MessageRepository
import com.example.resell.repository.MessageRepositoryImpl
import com.example.resell.repository.NotificationRepository
import com.example.resell.repository.NotificationRepositoryImpl
import com.example.resell.repository.OrderRepository
import com.example.resell.repository.OrderRepositoryImpl
import com.example.resell.repository.PaymentRepository
import com.example.resell.repository.PaymentRepositoryImpl
import com.example.resell.repository.PostRepository
import com.example.resell.repository.PostRepositoryImpl
import com.example.resell.repository.ReportRepository
import com.example.resell.repository.ReportRepositoryImpl
import com.example.resell.repository.ReviewRepository
import com.example.resell.repository.ReviewRepositoryImpl
import com.example.resell.repository.SubscriptionRepository
import com.example.resell.repository.SubscriptionRepositoryImpl
import com.example.resell.repository.UserRepository
import com.example.resell.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(impl: PaymentRepositoryImpl): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindAddressRepository(impl: AddressRepositoryImpl): AddressRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(impl: OrderRepositoryImpl): OrderRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}