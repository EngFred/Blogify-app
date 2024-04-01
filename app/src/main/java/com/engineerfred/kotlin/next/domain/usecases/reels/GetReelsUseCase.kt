package com.engineerfred.kotlin.next.domain.usecases.reels

import com.engineerfred.kotlin.next.domain.repository.ReelsRepository
import javax.inject.Inject

class GetReelsUseCase @Inject constructor(
    private val reelsRepository: ReelsRepository
) {
    operator fun invoke() = reelsRepository.getReels()
}