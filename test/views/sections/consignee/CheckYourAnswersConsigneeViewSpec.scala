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
import models.{CheckMode, NormalMode}
import models.requests.DataRequest
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, VatNumber}
import models.sections.info.movementScenario.MovementScenario._
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

  lazy val view = app.injector.instanceOf[CheckYourAnswersConsigneeView]

  object Selectors extends BaseSelectors {
    def govukSummaryListKey(id: Int) = s".govuk-summary-list__row:nth-of-type($id) .govuk-summary-list__key"

    val govukSummaryListChangeLink = ".govuk-summary-list__actions .govuk-link"

  }

  "CheckYourAnswersConsignee view" - {

    Seq(CheckYourAnswersConsigneeMessages.English).foreach { messagesForLanguage =>

      s"when being rendered in lang code of '${messagesForLanguage.lang.code} for ERN'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExcisePage, testErn)
            .set(DestinationTypePage, UkTaxWarehouse.GB)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
          testErn,
          testArc,
          SummaryList(Seq(
            ConsigneeBusinessNameSummary.row,
            ConsigneeExciseSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.traderName,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.ern,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.address,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change business name" in {
          doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url
        }

        "have a link to change ERN" in {
          doc.getElementById("changeConsigneeExcise").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExciseController.onPageLoad(testErn, testArc, CheckMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code} for Exempted Organisation'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExcisePage, testErn)
            .set(ConsigneeExemptOrganisationPage, testExemptedOrganisation)
            .set(DestinationTypePage, ExemptedOrganisation)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
          testErn,
          testArc,
          SummaryList(Seq(
            ConsigneeBusinessNameSummary.row,
            ConsigneeExemptOrganisationSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.traderName,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.exempt,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.address,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change business name" in {
          doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url
        }

        "have a link to change Exempted Organisation" in {
          doc.getElementById("changeConsigneeExemptOrganisation").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExemptOrganisationController.onPageLoad(testErn, testArc, CheckMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code} for Vat'" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
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
            ConsigneeBusinessNameSummary.row,
            consigneeExportInformationSummary.row,
            ConsigneeExportVatSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.traderName,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.identificationProvided,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.vat,
          Selectors.govukSummaryListKey(4) -> messagesForLanguage.address,
          Selectors.button -> messagesForLanguage.confirmAnswers,
        ))

        "have a link to change business name" in {
          doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url
        }

        "have a link to change Vat Number" in {
          doc.getElementById("changeConsigneeExportInformation").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }

      s"when being rendered in lang code of '${messagesForLanguage.lang.code}' for Eori" - {

        implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
        implicit val request: DataRequest[AnyContentAsEmpty.type] =
          dataRequest(FakeRequest(), emptyUserAnswers
            .set(ConsigneeAddressPage, testUserAddress)
            .set(ConsigneeBusinessNamePage, testBusinessName)
            .set(ConsigneeExcisePage, testErn)
            .set(ConsigneeExportInformationPage, Set(EoriNumber))
            .set(DestinationTypePage, UkTaxWarehouse.GB)
          )

        implicit val doc: Document = Jsoup.parse(view(
          controllers.sections.consignee.routes.CheckYourAnswersConsigneeController.onSubmit(testErn, testArc),
          testErn,
          testArc,
          SummaryList(Seq(
            ConsigneeBusinessNameSummary.row,
            consigneeExportInformationSummary.row,
            ConsigneeExportEoriSummary.row,
            ConsigneeAddressSummary.row
          ).flatten)
        ).toString())

        behave like pageWithExpectedElementsAndMessages(Seq(
          Selectors.title -> messagesForLanguage.title,
          Selectors.h1 -> messagesForLanguage.heading,
          Selectors.h2(1) -> messagesForLanguage.caption,
          Selectors.govukSummaryListKey(1) -> messagesForLanguage.traderName,
          Selectors.govukSummaryListKey(2) -> messagesForLanguage.identificationProvided,
          Selectors.govukSummaryListKey(3) -> messagesForLanguage.eori,
          Selectors.govukSummaryListKey(4) -> messagesForLanguage.address,
          Selectors.button -> messagesForLanguage.confirmAnswers
        ))

        "have a link to change business name" in {
          doc.getElementById("changeConsigneeBusinessName").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeBusinessNameController.onPageLoad(testErn, testArc, CheckMode).url
        }

        "have a link to change Eori Number" in {
          doc.getElementById("changeConsigneeExportInformation").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeExportInformationController.onPageLoad(testErn, testArc, NormalMode).url
        }

        "have a link to change Address" in {
          doc.getElementById("changeConsigneeAddress").attr("href") mustBe
            controllers.sections.consignee.routes.ConsigneeAddressController.onPageLoad(testErn, testArc, CheckMode).url
        }
      }


    }
  }
}
