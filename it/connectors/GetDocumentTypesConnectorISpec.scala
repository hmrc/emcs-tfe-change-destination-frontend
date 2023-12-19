package connectors

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, urlEqualTo}
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetDocumentTypesConnector
import fixtures.BaseFixtures
import models.response.UnexpectedDownstreamResponseError
import models.sections.documents.DocumentType
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetDocumentTypesConnectorISpec extends AnyFreeSpec
  with WireMockHelper
  with ScalaFutures
  with Matchers
  with IntegrationPatience
  with EitherValues
  with OptionValues
  with BaseFixtures {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  val url = "/emcs-tfe-reference-data/oracle/type-of-document"

  val documentTypesSeq: Seq[DocumentType] = Seq(
    DocumentType("testCode1", "testDescription1"),
    DocumentType("testCode2", "testDescription2")
  )

  ".getDocumentTypes" - {

    def app: Application =
      new GuiceApplicationBuilder()
        .configure("microservice.services.emcs-tfe-reference-data.port" -> server.port)
        .configure("features.stub-get-trader-known-facts" -> "false")
        .build()

    lazy val connector: GetDocumentTypesConnector = app.injector.instanceOf[GetDocumentTypesConnector]

    "must return Right(Seq[DocumentType]) when the server responds OK" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.arr(
                Json.obj("code" -> "testCode1", "description" -> "testDescription1"),
                Json.obj("code" -> "testCode2", "description" -> "testDescription2")
              ))))
      )

      connector.getDocumentTypes().futureValue mustBe Right(documentTypesSeq)
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getDocumentTypes().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        get(urlEqualTo(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getDocumentTypes().futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
