package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.emcsTfe.SubmitChangeDestinationConnector
import fixtures.{BaseFixtures, GetMovementResponseFixtures, SubmitChangeDestinationFixtures}
import models.requests.{DataRequest, MovementRequest, UserRequest}
import models.response.{UnexpectedDownstreamResponseError, UnexpectedDownstreamSubmissionResponseError}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class SubmitChangeDestinationConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures
  with SubmitChangeDestinationFixtures
  with GetMovementResponseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.emcs-tfe.port" -> server.port,
        "internal-auth.token" -> "token"
      )
      .build()

  implicit lazy val dr: DataRequest[_] =
    DataRequest(
      request = MovementRequest(
        request = UserRequest(FakeRequest(), testErn, "", "", "", false),
        arc = testArc,
        movementDetails = maxGetMovementResponse
      ),
      userAnswers = emptyUserAnswers,
      traderKnownFacts = Some(testMinTraderKnownFacts)
    )

  private lazy val connector: SubmitChangeDestinationConnector = app.injector.instanceOf[SubmitChangeDestinationConnector]

  ".submit" - {

    val url = s"/emcs-tfe/change-destination/$testErn/$testArc"
    val requestBody = Json.toJson(minimumSubmitChangeDestinationModel)
    val responseBody = successResponseEISJson

    "must return true when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(responseBody)))
      )

      connector.submit(minimumSubmitChangeDestinationModel).futureValue mustBe Right(submitChangeDestinationResponseEIS)
    }

    "must return false when the server responds NOT_FOUND" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.submit(minimumSubmitChangeDestinationModel).futureValue mustBe Left(UnexpectedDownstreamSubmissionResponseError(NOT_FOUND))
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.submit(minimumSubmitChangeDestinationModel).futureValue mustBe Left(UnexpectedDownstreamSubmissionResponseError(INTERNAL_SERVER_ERROR))
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestBody)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.submit(minimumSubmitChangeDestinationModel).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }


}
