package discordApi

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

private const val TEST_CLIENT_ID = "clientID"
private const val TEST_CLIENT_SECRET = "clientSecret"
private const val TEST_CODE = "testCode"
private const val TEST_REFRESH_TOKEN = "testRefreshToken"

class OAuthHandlerTests {
    private var testHttpHandlerForExchangeToken: HttpHandler = { request ->
        Assertions.assertEquals("authorization_code", request.queries("grant_type").first())
        Assertions.assertEquals(TEST_CLIENT_ID, request.queries("client_id").first())
        Assertions.assertEquals(TEST_CLIENT_SECRET, request.queries("client_secret").first())
        Assertions.assertEquals(TEST_CODE, request.queries("code").first())
        Assertions.assertEquals("application/x-www-form-urlencoded", request.header("Content-Type"), )
        Response(Status.ACCEPTED)
    }

    private var testHttpHandlerForRefreshToken: HttpHandler = { request ->
        Assertions.assertEquals("refresh_token", request.queries("grant_type").first())
        Assertions.assertEquals(TEST_CLIENT_ID, request.queries("client_id").first())
        Assertions.assertEquals(TEST_CLIENT_SECRET, request.queries("client_secret").first())
        Assertions.assertEquals(TEST_REFRESH_TOKEN, request.queries("refresh_token").first())
        Assertions.assertEquals("application/x-www-form-urlencoded", request.header("Content-Type"), )
        Response(Status.ACCEPTED)
    }

    @Test
    fun `given code when exchangeCodeForToken, verify that request contains required data`() {
        val unit = OAuthHandler(testHttpHandlerForExchangeToken)

        unit.exchangeCodeForToken(
            clientId = TEST_CLIENT_ID,
            clientSecret = TEST_CLIENT_SECRET,
            code = TEST_CODE
        )
    }

    @Test
    fun `given refreshToken when requestNewToken, verify that request contains required data`() {
        val unit = OAuthHandler(testHttpHandlerForRefreshToken)

        unit.requestNewToken(
            clientId = TEST_CLIENT_ID,
            clientSecret = TEST_CLIENT_SECRET,
            refreshToken = TEST_REFRESH_TOKEN
        )
    }
}