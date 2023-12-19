package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import connectors.referenceData.GetCnCodeInformationConnector
import models.UnitOfMeasure.Kilograms
import models.requests.{CnCodeInformationItem, CnCodeInformationRequest}
import models.response.referenceData.{CnCodeInformation, CnCodeInformationResponse}
import models.response.{JsonValidationError, UnexpectedDownstreamResponseError}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsArray, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class GetCnCodeInformationConnectorISpec
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
        "microservice.services.emcs-tfe-reference-data.port" -> server.port
      )
      .build()

  private lazy val connector: GetCnCodeInformationConnector = app.injector.instanceOf[GetCnCodeInformationConnector]

  ".getCnCodeInformation()" - {

    val url = "/emcs-tfe-reference-data/oracle/cn-code-information"
    val request = CnCodeInformationRequest(Seq(CnCodeInformationItem("T400", "24029000")))
    val requestJson = Json.obj("items" -> Json.arr(
      Json.obj(
        "productCode" -> "T400",
        "cnCode" -> "24029000"
      )
    ))

    "must return a response model when the server responds OK" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "24029000" -> Json.obj(
              "cnCode" -> "T400",
              "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
              "exciseProductCode" -> "24029000",
              "exciseProductCodeDescription" -> "Fine-cut tobacco for the rolling of cigarettes",
              "unitOfMeasureCode" -> 1
            )
          ))))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Right(CnCodeInformationResponse(data = Map(
        "24029000" -> CnCodeInformation(
          cnCode = "T400",
          cnCodeDescription = "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
          exciseProductCode = "24029000",
          exciseProductCodeDescription = "Fine-cut tobacco for the rolling of cigarettes",
          unitOfMeasure = Kilograms
        )
      )))
    }

    "must fail when the server responds with any other status" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }

    "must fail when the server responds with a body that can't be parsed to the expected response model" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(requestJson)))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.obj(
            "24029000" -> JsArray(Seq(Json.obj(
              "cnCodeDescription" -> "Cigars, cheroots, cigarillos and cigarettes not containing tobacco",
              "unitOfMeasureCode" -> 1
            )))
          ))))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Left(JsonValidationError)
    }

    "must fail when the connection fails" in {

      server.stubFor(
        post(urlEqualTo(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.getCnCodeInformation(request).futureValue mustBe Left(UnexpectedDownstreamResponseError)
    }
  }
}
