package di

import com.example.resell.ui.MyRepository
import com.example.resell.ui.MyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModuel {
    @Binds
    @Singleton
    abstract fun bindMyRepository(impl: MyRepositoryImpl): MyRepository
}