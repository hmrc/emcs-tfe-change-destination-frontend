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

package viewmodels.checkAnswers.sections.items

import base.SpecBase
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemsAddToListMessages
import models.requests.DataRequest
import models.response.referenceData.{BulkPackagingType, ItemPackaging}
import models.sections.items.ItemBulkPackagingCode
import pages.sections.items._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._
import views.html.components.{list, tag}

class ItemPackagingSummarySpec extends SpecBase with ItemFixtures {
  lazy val tag: tag = app.injector.instanceOf[tag]
  lazy val list: list = app.injector.instanceOf[list]

  lazy val summary = new ItemPackagingSummary(tag, list)

  val package1: ItemPackaging = testPackageBag
  val package2: ItemPackaging = testPackageAerosol
  val testBulkPackagingType: BulkPackagingType = BulkPackagingType(ItemBulkPackagingCode.BulkGas, "desc")

  val messagesForLanguage: ItemsAddToListMessages.English.type = ItemsAddToListMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  "getPackagesForItem" - {
    "must return an empty Seq" - {
      "when no packaging" in {
        val userAnswers = emptyUserAnswers

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.getPackagesForItem(testIndex1) mustBe Nil
      }
    }
    "must return a non-empty Seq" - {
      "when all packages have a packaging" in {
        val userAnswers = emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), package1)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "3")
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), package2)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.getPackagesForItem(testIndex1) mustBe Seq(package1 -> Some("3"), package2 -> None)
      }
    }
    "must filter out packaging without an answer to ItemSelectPackagingPage" in {

      val userAnswers = emptyUserAnswers
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "3")
        .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), package2)

      implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

      summary.getPackagesForItem(testIndex1) mustBe Seq(package2 -> None)
    }
  }

  "constructPackagingValues" - {
    "must return an incomplete tag" - {
      "when an item is missing a quantity" in {
        val userAnswers = emptyUserAnswers

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.constructPackagingValues(testIndex1, Seq(package1 -> None)) mustBe Seq(
          HtmlFormat.fill(
            Seq(
              tag(
                message = messagesForLanguage.incomplete,
                colour = "red",
                extraClasses = "float-none"
              )
            )
          )
        )
      }
      "when an item is incomplete" in {
        val userAnswers = emptyUserAnswers.set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.constructPackagingValues(testIndex1, Seq(package1 -> Some("5"))) mustBe Seq(
          HtmlFormat.fill(
            Seq(
              Html(messagesForLanguage.packagesCyaValue("5", package1.description)),
              tag(
                message = messagesForLanguage.incomplete,
                colour = "red",
                extraClasses = "float-none govuk-!-margin-left-2"
              )
            )
          )
        )
      }
    }
    "must return no incomplete tag when an item is complete" in {
      val userAnswers = emptyUserAnswers
        .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), package1)
        .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "4")
        .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
        .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

      implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

      summary.constructPackagingValues(testIndex1, Seq(package1 -> Some("4"))) mustBe Seq(
        HtmlFormat.fill(
          Seq(Html(messagesForLanguage.packagesCyaValue("4", package1.description)))
        )
      )
    }
  }

  "constructBulkPackagingSummary" - {
    "when EPC and bulk type are present" - {
      "must return Some(SummaryListRow) with an incomplete tag when an item is incomplete" in {
        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemBulkPackagingSelectPage(testIndex1), testBulkPackagingType)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), true)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.constructBulkPackagingSummary(testIndex1) mustBe Some(
          SummaryListRowViewModel(
            key = messagesForLanguage.packagesCyaLabel,
            value = ValueViewModel(HtmlContent(
              HtmlFormat.fill(Seq(
                Html(testBulkPackagingType.description),
                tag(
                  message = messagesForLanguage.incomplete,
                  colour = "red",
                  extraClasses = "float-none govuk-!-margin-left-2"
                )
              ))
            ))
          )
        )
      }
      "must return Some(SummaryListRow) with no incomplete tag when an item is complete" in {
        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSelectPage(testIndex1), testBulkPackagingType)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.constructBulkPackagingSummary(testIndex1) mustBe Some(
          SummaryListRowViewModel(
            key = messagesForLanguage.packagesCyaLabel,
            value = ValueViewModel(HtmlContent(
              HtmlFormat.fill(Seq(
                Html(testBulkPackagingType.description)
              ))
            ))
          )
        )
      }
      "must return None when EPC is missing" in {
        val userAnswers = emptyUserAnswers
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
          .set(ItemBulkPackagingSelectPage(testIndex1), testBulkPackagingType)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.constructBulkPackagingSummary(testIndex1) mustBe None
      }
      "must return None when bulkType is missing" in {
        val userAnswers = emptyUserAnswers
          .set(ItemExciseProductCodePage(testIndex1), testEpcBeer)
          .set(ItemBulkPackagingSealChoicePage(testIndex1), false)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        summary.constructBulkPackagingSummary(testIndex1) mustBe None
      }
    }
  }
}
