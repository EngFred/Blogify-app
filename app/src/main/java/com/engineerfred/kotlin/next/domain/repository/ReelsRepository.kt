package com.engineerfred.kotlin.next.domain.repository

import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.utils.Response
import kotlinx.coroutines.flow.Flow

interface ReelsRepository {
    fun getReels() : Flow<Response<List<Post>>>
}