package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetWineOperationsConnector
import fixtures.{BaseFixtures, ItemFixtures}
import models.response.UnexpectedDownstreamResponseError
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetWineOperationsConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures
  with ItemFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = "/emcs-tfe-reference-data/oracle/wine-operations"

  ".getWineOperations" - {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data.port" -> server.port)
        .configure("features.stub-get-trader-known-facts" -> "false")
        .build()

    lazy val connector: GetWineOperationsConnector = app.injector.instanceOf[GetWineOperationsConnector]

    s"must return Right(Seq[WineOperations]) when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(testWineOperationsJson.toString()))
      )

      connector.getWineOperations().futureValue mustBe Right(testWineOperations)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getWineOperations().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getWineOperations().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
