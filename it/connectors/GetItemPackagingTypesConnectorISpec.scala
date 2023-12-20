package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetItemPackagingTypesConnector
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

class GetItemPackagingTypesConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures
  with ItemFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  def url(isCountable: Option[Boolean]) = s"/emcs-tfe-reference-data/oracle/packaging-types${isCountable.fold("")(param => s"?isCountable=$param")}"

  ".getItemPackagingTypes" - {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data.port" -> server.port)
        .configure("features.stub-get-trader-known-facts" -> "false")
        .build()

    lazy val connector: GetItemPackagingTypesConnector = app.injector.instanceOf[GetItemPackagingTypesConnector]

    s"must return Right(Seq[ItemPackaging]) when the server responds OK (with a query parameter)" in {

      server.stubFor(
        get(urlEqualTo(url(Some(true))))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(testItemPackagingTypesJson.toString()))
      )

      connector.getItemPackagingTypes(Some(true)).futureValue mustBe Right(testItemPackagingTypes)
    }

    s"must return Right(Seq[ItemPackaging]) when the server responds OK (without a query parameter)" in {

      server.stubFor(
        get(urlEqualTo(url(None)))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(testItemPackagingTypesJson.toString()))
      )

      connector.getItemPackagingTypes(None).futureValue mustBe Right(testItemPackagingTypes)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url(Some(true))))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getItemPackagingTypes(Some(true)).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url(None)))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getItemPackagingTypes(Some(true)).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
