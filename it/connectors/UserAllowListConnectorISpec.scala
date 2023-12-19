package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.userAllowList.UserAllowListConnector
import models.requests.CheckUserAllowListRequest
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class UserAllowListConnectorISpec
  extends AnyFreeSpec
    with WireMockHelper
    with ScalaFutures
    with Matchers
    with IntegrationPatience
    with EitherValues
    with OptionValues {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.user-allow-list.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  private lazy val connector: UserAllowListConnector = app.injector.instanceOf[UserAllowListConnector]

  ".check" - {

    val service = "emcs-tfe"
    val feature = "createMovement"
    val url = s"/user-allow-list/$service/$feature/check"
    val request = CheckUserAllowListRequest("value")

    "must return true when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.check(request).futureValue mustBe Right(true)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.check(request).futureValue mustBe Right(false)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.check(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo("token"))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.check(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
