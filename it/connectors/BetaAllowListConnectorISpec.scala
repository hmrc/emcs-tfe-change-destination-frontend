package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.betaAllowList.BetaAllowListConnector
import fixtures.BaseFixtures
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NO_CONTENT, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.AUTHORIZATION
import uk.gov.hmrc.http.{Authorization, HeaderCarrier}

import scala.concurrent.ExecutionContext

class BetaAllowListConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures {

  val authToken: String = "auth-value"
  implicit private lazy val hc: HeaderCarrier = HeaderCarrier(authorization = Some(Authorization(authToken)))

  lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure("microservice.services.emcs-tfe.port" -> server.port)
      .build()

  lazy val connector: BetaAllowListConnector = app.injector.instanceOf[BetaAllowListConnector]

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  val url = s"/emcs-tfe/beta/eligibility/$testErn/changeDestination"

  ".check" - {

    "return true when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.check(testErn).futureValue mustBe Right(true)
    }

    "must return false when the server responds NO_CONTENT" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      connector.check(testErn).futureValue mustBe Right(false)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.check(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url))
          .withHeader(AUTHORIZATION, equalTo(authToken))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.check(testErn).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}