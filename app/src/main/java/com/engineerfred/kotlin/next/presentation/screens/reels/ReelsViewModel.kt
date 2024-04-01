package com.engineerfred.kotlin.next.presentation.screens.reels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.engineerfred.kotlin.next.di.NextApp.Companion.simpleCache
import com.engineerfred.kotlin.next.domain.usecases.reels.GetReelsUseCase
import com.engineerfred.kotlin.next.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val getReelsUseCase: GetReelsUseCase,
    val player: ExoPlayer
)  : ViewModel() {

    private val _uiState = MutableStateFlow(ReelsUiState())
    val uiState = _uiState.asStateFlow()

    companion object {
        val TAG = ReelsViewModel::class.simpleName

        @UnstableApi
        private val httpDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        @UnstableApi
        val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    init {
        getReels()
    }

    fun onEvent( event: ReelsUiEvents) {
        when(event){
            ReelsUiEvents.RetryClicked -> {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        loadError = null,
                        reels = emptyList()
                    )
                }
                getReels()
            }
        }
    }

    private fun getReels() =  viewModelScope.launch {
        getReelsUseCase.invoke().collectLatest { apiResponse ->
            when(apiResponse){
                is Response.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loadError = apiResponse.exception,
                        )
                    }
                }
                is Response.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            reels = apiResponse.data.shuffled()
                        )
                    }
                }
                Response.Undefined -> Unit
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.stop()
    }

}