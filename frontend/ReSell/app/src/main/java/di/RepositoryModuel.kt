package di

import com.example.resell.ui.repository.AddressRepository
import com.example.resell.ui.repository.AddressRepositoryImpl
import com.example.resell.ui.repository.CategoryRepository
import com.example.resell.ui.repository.CategoryRepositoryImpl
import com.example.resell.ui.repository.MessageRepository
import com.example.resell.ui.repository.MessageRepositoryImpl
import com.example.resell.ui.repository.OrderRepository
import com.example.resell.ui.repository.OrderRepositoryImpl
import com.example.resell.ui.repository.PostRepository
import com.example.resell.ui.repository.PostRepositoryImpl
import com.example.resell.ui.repository.ReviewRepository
import com.example.resell.ui.repository.ReviewRepositoryImpl
import com.example.resell.ui.repository.UserRepository
import com.example.resell.ui.repository.UserRepositoryImpl
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
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository
}