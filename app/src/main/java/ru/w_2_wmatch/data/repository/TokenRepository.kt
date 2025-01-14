package ru.w_2_wmatch.data.repository

import ru.w_2_wmatch.data.api.TokenApi
import ru.w_2_wmatch.data.dto.token.request.CreateTokenRequest
import ru.w_2_wmatch.data.dto.token.request.RefreshTokenRequest
import ru.w_2_wmatch.data.dto.token.response.CreateTokenResponse
import ru.w_2_wmatch.data.storage.TokenStorage
import ru.w_2_wmatch.domain.irepository.ITokenRepository
import ru.w_2_wmatch.domain.istorage.ITokenStorage
import ru.w_2_wmatch.domain.models.AuthUser
import ru.w_2_wmatch.domain.models.Token

class TokenRepository(
    private val tokenApi: TokenApi,
    private val tokenStorage: ITokenStorage,
) : ITokenRepository {

    override suspend fun getToken(authUser: AuthUser): Result<Token> {
        val result = tokenApi.get(mapperAuthUserToCreateTokenRequest(authUser))
        return result.map { mapperCreateTokenRequestToToken(it) }
    }

    override suspend fun refreshToken(refreshToken: String): Result<Token> {
        val result = tokenApi.refresh(RefreshTokenRequest(refresh = refreshToken))
        return result.map { mapperCreateTokenRequestToToken(it) }
    }

    private fun mapperAuthUserToCreateTokenRequest(
        authUser: AuthUser
    ): CreateTokenRequest {
        return CreateTokenRequest(
            email = authUser.login,
            password = authUser.password,
        )
    }

    private fun mapperCreateTokenRequestToToken(
        createTokenResponse: CreateTokenResponse
    ): Token {
        return Token(
            access = createTokenResponse.access,
            refresh = createTokenResponse.refresh,
        )
    }
}