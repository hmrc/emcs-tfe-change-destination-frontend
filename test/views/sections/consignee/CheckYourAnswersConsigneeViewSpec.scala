/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.CheckYourAnswersConsigneeMessages
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, VatNumber}
import models.sections.info.movementScenario.MovementScenario._
import models.{CheckMode, NormalMode}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import pages.sections.consignee._
import pages.sections.info.DestinationTypePage
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryList
import viewmodels.checkAnswers.sections.consignee._
import views.html.sections.consignee.CheckYourAnswersConsigneeView
import views.{BaseSelectors, ViewBehaviours}

class CheckYourAnswersConsigneeViewSpec extends SpecBase with ViewBehaviours {

  val consigneeExportInformationSummary: ConsigneeExportInformationSummary = app.injector.instanceOf[ConsigneeExportInformationSummary]

  lazy val view: CheckYourAnswersConsigneeView = app.injector.instanceOf[CheckYourAnswersConsigneeView]

  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"

    val govukSummaryListChangeLink = ".govuk-summary-list__actions .govuk-link"

  }

  "CheckYourAnswersConsignee view" - {

    Seq(CheckYourAnswersConsigneeMessages.English).foreach { messagesForLanguage =>

      Seq(UkTaxWarehouse.GB, UkTaxWarehouse.NI, EuTaxWarehouse, DirectDelivery, RegisteredConsignee).foreach { movementScenario =>
        s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for ${simpleName(movementScenario)} " - {

          implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

          implicit val request: DataRequest[AnyContentAsEmpty.type] =
            dataRequest(FakeRequest(), emptyUserAnswers
              .set(ConsigneeAddressPage, testUserAddress)
              .set(ConsigneeExcisePage, testErn)
              .set(DestinationTypePage, movementScenario)
            )

          implicit val doc: Document = Jsoup.parse(view(
            controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
            testErn,
            testArc,
            SummaryList(Seq(
              ConsigneeExciseSummary.row,
              ConsigneeAddressSummary.row
            ).flatten)
          ).toString())

          behave like pageWithExpectedElementsAndMessages(Seq(
            Selectors.title -> messagesForLanguage.title,
            Selectors.h1 -> messagesForLanguage.heading,
            Selectors.h2(1) -> messagesForLanguage.caption,
            Selectors.govukSummaryListKey(1) -> messagesForLanguage.ern,
            Selectors.govukSummaryListKey(2) -> messagesForLanguage.details,
            Selectors.button -> messagesForLanguage.confirmAnswers,
          ))

          "have a link to change ERN" in {
            doc.getElementById("changeConsigneeExcise").attr("href") mustBe
              controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testArc, CheckMode).url
          }

          "have a link to change Address" in {
            doc.getElementById("changeConsigneeAddress").attr("href") mustBe
              controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
          }
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for Temporary Registered Consignee" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers.copy(ern = testNorthernIrelandWarehouseKeeperErn)
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeExcisePage, "TMPREGCON")
            .set(DestinationTypePage, TemporaryRegisteredConsignee),
            ern = testNorthernIrelandWarehouseKeeperErn
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testNorthernIrelandWarehouseKeeperErn, testArc),
          testNorthernIrelandWarehouseKeeperErn,
          testArc,
          SummaryList(Seq(
            ConsigneeExciseSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.identificationForTemporaryRegisteredConsignee,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.details,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change identification for temporary registered consignee" in {
          doc.getElementById("changeConsigneeExcise").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testNorthernIrelandWarehouseKeeperErn, testArc, CheckMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testNorthernIrelandWarehouseKeeperErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for Temporary Certified Consignee" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers.copy(ern = testNorthernIrelandWarehouseKeeperErn)
              .set(ConsigneeAddressPage, testUserAddress)
              .set(ConsigneeExcisePage, "TMPREGCON")
              .set(DestinationTypePage, TemporaryCertifiedConsignee),
            ern = testNorthernIrelandWarehouseKeeperErn
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testNorthernIrelandWarehouseKeeperErn, testArc),
          testNorthernIrelandWarehouseKeeperErn,
          testArc,
          SummaryList(Seq(
            ConsigneeExciseSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.identificationForTemporaryCertifiedConsignee,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.details,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change identification for temporary certified consignee" in {
          doc.getElementById("changeConsigneeExcise").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testNorthernIrelandWarehouseKeeperErn, testArc, CheckMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testNorthernIrelandWarehouseKeeperErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for an Export with a VAT identifier" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeExcisePage, testErn)
            .set(ConsigneeExportInformationPage, Set(VatNumber))
            .set(ConsigneeExportVatPage, testVatNumber)
            .set(DestinationTypePage, UkTaxWarehouse.GB)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
          testErn,
          testArc,
          SummaryList(Seq(
            consigneeExportInformationSummary.row,
            ConsigneeExportVatSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.identificationProvided,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.vat,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.details,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change Vat Number" in {
          doc.getElementById("changeConsigneeExportInformation").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for an Export with an EORI identifier" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeExcisePage, testErn)
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(DestinationTypePage, UkTaxWarehouse.GB)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
          testErn,
          testArc,
          SummaryList(Seq(
            consigneeExportInformationSummary.row,
            ConsigneeExportEoriSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.identificationProvided,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.eori,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.details,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

        "have a link to change Eori Number" in {
          doc.getElementById("changeConsigneeExportInformation").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for an Export with both a VAT and EORI identifier" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeExcisePage, testErn)
            .set(ConsigneeExportInformationPage, Set(VatNumber, EoriNumber))
            .set(ConsigneeExportVatPage, testVatNumber)
            .set(ConsigneeExportEoriPage, testEoriNumber)
            .set(DestinationTypePage, UkTaxWarehouse.GB)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
          testErn,
          testArc,
          SummaryList(Seq(
            consigneeExportInformationSummary.row,
            ConsigneeExportVatSummary.row,
            ConsigneeExportEoriSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.identificationProvided,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.vat,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.eori,
          Selectors.govukSummaryListKey(4) -> messagesForLanguage.details,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change Vat Number" in {
          doc.getElementById("changeConsigneeExportInformation").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for Exempted Organisation'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeExcisePage, testErn)
            .set(ConsigneeExemptOrganisationPage, testExemptedOrganisation)
            .set(DestinationTypePage, ExemptedOrganisation)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
          testErn,
          testArc,
          SummaryList(Seq(
            ConsigneeExemptOrganisationSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.exempt,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.details,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change Exempted Organisation" in {
          doc.getElementById("changeConsigneeExemptOrganisation").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testArc, CheckMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }


    }
  }
}
