/*
 * Copyright 2024 HM Revenue & Customs
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
import models.requests.{DataRequest, UserRequest}
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, VatNumber}
import pages.sections.consignee._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{SummaryList, SummaryListRow}
import viewmodels.checkAnswers.sections.consignee._

class ConsigneeCheckAnswersHelperSpec extends SpecBase {

  lazy val consigneeExportInformationSummary: ConsigneeExportInformationSummary = app.injector.instanceOf[ConsigneeExportInformationSummary]

  lazy val checkAnswersConsigneeHelper = new ConsigneeCheckAnswersHelper(consigneeExportInformationSummary)

  class Setup(ern: String = testErn, userAnswers: UserAnswers = emptyUserAnswers) {
    implicit val fakeDataRequest: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers.copy(ern = ern), movementDetails = maxGetMovementResponse.copy(consigneeTrader = None, memberStateCode = None, serialNumberOfCertificateOfExemption = None))
    implicit val testUserRequest: UserRequest[AnyContentAsEmpty.type] = userRequest(fakeDataRequest)
    implicit val msgs: Messages = stubMessagesApi().preferred(fakeDataRequest)
  }

  "CheckAnswersConsigneeHelper" - {

    ".buildSummaryRows should return the correct rows when" - {
      val testGbWarehouseErn = "GB00123456789"
      val testNiWarehouseErn = "XI00123456789"
      val testGbrcWarehouseErn = "GBRC123456789"
      val testXircWarehouseErn = "XIRC123456789"

      val warehouseUserAnswers = {
        emptyUserAnswers
          // TODO .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExcisePage, testGbWarehouseErn)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val exemptDataUserAnswers = {
        emptyUserAnswers
          // TODO .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExemptOrganisationPage, testExemptedOrganisation)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val vatNumberUserAnswers = {
        emptyUserAnswers
          // TODO .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExportInformationPage, Set(VatNumber))
          .set(ConsigneeExportVatPage, testVatNumber)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val eoriUserAnswers = {
        emptyUserAnswers
          // TODO .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeExportInformationPage, Set(EoriNumber))
          .set(ConsigneeExportEoriPage, testEoriNumber)
          .set(ConsigneeAddressPage, testUserAddress)
      }
      val noVatNumberOrEoriUserAnswers = {
        emptyUserAnswers
          // TODO .set(ConsigneeBusinessNamePage, "testBusinessName")
          .set(ConsigneeAddressPage, testUserAddress)
      }

      "the user type is GreatBritainWarehouse" in new Setup(testGbWarehouseErn, warehouseUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeExciseSummary.row(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user type is NorthernIrelandWarehouse" in new Setup(testNiWarehouseErn, warehouseUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeExciseSummary.row(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the destination type is DirectDelivery" in new Setup(testGbWarehouseErn, warehouseUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeExciseSummary.row(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user type is GreatBritainRegisteredConsignor & destination type is TemporaryRegisteredConsignee" in
        new Setup(testGbrcWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeExciseSummary.row(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is GreatBritainRegisteredConsignor & destination type is RegisteredConsignee" in
        new Setup(testGbrcWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeExciseSummary.row(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is GreatBritainRegisteredConsignor & destination type is Export with dec in Uk" in
        new Setup(testGbrcWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeExciseSummary.row(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is NorthernIrelandRegisteredConsignor & destination type is RegisteredConsignee" in
        new Setup(testXircWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeExciseSummary.row(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the user type is NorthernIrelandRegisteredConsignor & destination type is Export with dec in Eu" in
        new Setup(testXircWarehouseErn, warehouseUserAnswers) {

          val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
            ConsigneeExciseSummary.row(fakeDataRequest, msgs),
            ConsigneeAddressSummary.row(fakeDataRequest, msgs)
          ).flatten

          checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
        }

      "the destination type is ExemptedOrganisations" in new Setup(testGbWarehouseErn, exemptDataUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeExemptOrganisationSummary.row(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected yes to VAT number" in new Setup(testGbWarehouseErn, vatNumberUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          consigneeExportInformationSummary.row(fakeDataRequest, msgs),
          ConsigneeExportVatSummary.row(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected Yes Eori number" in new Setup(testGbWarehouseErn, eoriUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          consigneeExportInformationSummary.row(fakeDataRequest, msgs),
          ConsigneeExportEoriSummary.row(fakeDataRequest, msgs),
          ConsigneeAddressSummary.row(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }

      "the user has selected no VAT or Eori number" in new Setup(testGbWarehouseErn, noVatNumberOrEoriUserAnswers) {

        val expectedSummaryListRows: Seq[SummaryListRow] = Seq(
          ConsigneeAddressSummary.row(fakeDataRequest, msgs)
        ).flatten

        checkAnswersConsigneeHelper.summaryList() mustBe SummaryList(rows = expectedSummaryListRows)
      }
    }

  }
}
