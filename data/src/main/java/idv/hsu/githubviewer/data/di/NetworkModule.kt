package idv.hsu.githubviewer.data.di

import android.content.Context
import android.content.pm.ApplicationInfo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import idv.hsu.githubviewer.data.BuildConfig
import idv.hsu.githubviewer.data.source.remote.GitHubApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.github.com/"
    private const val GITHUB_TOKEN = BuildConfig.GITHUB_API_TOKEN

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(@ApplicationContext context: Context): HttpLoggingInterceptor {
        val isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        return HttpLoggingInterceptor().apply {
            level =
                if (isDebuggable) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
            .addInterceptor { chain ->
                val original: Request = chain.request()
                val requestBuilder: Request.Builder =
                    original.newBuilder().header("Accept", "application/vnd.github+json")
                        .header("X-GitHub-Api-Version:", "2022-11-28")
                        .header("Authorization", "Bearer $GITHUB_TOKEN}")
                val request: Request = requestBuilder.build()
                chain.proceed(request)
            }.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi)).client(okHttpClient).build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): GitHubApi = retrofit.create(GitHubApi::class.java)
}