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

package viewmodels.helpers

import base.SpecBase
import models.UserAnswers
import models.requests.DataRequest
import play.api.i18n.Messages
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.consignee._

class CheckAnswersConsigneeHelperSpec extends SpecBase {

  class Setup(ern: String = testErn, data: JsObject = Json.obj()) {
    lazy val checkAnswersConsigneeHelper = new CheckYourAnswersConsigneeHelper()
    val userAnswers: UserAnswers = UserAnswers(ern, testArc, data)
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, movementDetails = maxGetMovementResponse.copy(consigneeTrader = None, memberStateCode = None, serialNumberOfCertificateOfExemption = None))
    implicit val testUserRequest = userRequest(fakeDataRequest)
    implicit val msgs: Messages = stubMessagesApi().preferred(fakeDataRequest)
  }

  "CheckAnswersConsigneeHelper" - {

    ".buildSummaryRows should return the correct rows when" - {
      val testGbWarehouseErn = "GB00123456789"
      val testNiWarehouseErn = "XI00123456789"
      val testGbrcWarehouseErn = "GBRC123456789"
      val testXircWarehouseErn = "XIRC123456789"

      val testWarehouseDataJson = testConsigneeBusinessNameJson ++ testConsigneeExciseJson(testGbWarehouseErn) ++ testConsigneeAddressJson
      val testExemptDataJson = testConsigneeBusinessNameJson ++ testConsigneeExemptOrganisationJson ++ testConsigneeAddressJson
      val testVatDataJson = testConsigneeBusinessNameJson ++ testConsigneeVatJson ++ testConsigneeAddressJson
      val testEoriDataJson = testConsigneeBusinessNameJson ++ testConsigneeEoriJson ++ testConsigneeAddressJson
      val testNoVatOrEoriDataJson = testConsigneeBusinessNameJson ++ testConsigneeAddressJson

      "the user type is GreatBritainWarehouse" in new Setup(testGbWarehouseErn, testWarehouseDataJson) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user type is NorthernIrelandWarehouse" in new Setup(testNiWarehouseErn, testWarehouseDataJson) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the destination type is DirectDelivery" in new Setup(testGbWarehouseErn, testWarehouseDataJson) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user type is GreatBritainRegisteredConsignor & destination type is TemporaryRegisteredConsignee" in
        new Setup(testGbrcWarehouseErn, testWarehouseDataJson) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is GreatBritainRegisteredConsignor & destination type is RegisteredConsignee" in
        new Setup(testGbrcWarehouseErn, testWarehouseDataJson) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is GreatBritainRegisteredConsignor & destination type is Export with dec in Uk" in
        new Setup(testGbrcWarehouseErn, testWarehouseDataJson) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is NorthernIrelandRegisteredConsignor & destination type is RegisteredConsignee" in
        new Setup(testXircWarehouseErn, testWarehouseDataJson) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is NorthernIrelandRegisteredConsignor & destination type is Export with dec in Eu" in
        new Setup(testXircWarehouseErn, testWarehouseDataJson) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeExciseSummary.row(true)(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the destination type is ExemptedOrganisations" in new Setup(testGbWarehouseErn, testExemptDataJson) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExemptOrganisationSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected yes VAT number" in new Setup(testGbWarehouseErn, testVatDataJson) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExportVatSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected Yes Eori number" in new Setup(testGbWarehouseErn, testEoriDataJson) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeExportVatSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected no VAT or Eori number" in new Setup(testGbWarehouseErn, testNoVatOrEoriDataJson) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeBusinessNameSummary.row(true)(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(true)(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }
    }

  }
}
