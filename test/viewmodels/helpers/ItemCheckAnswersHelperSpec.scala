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
import fixtures.ItemFixtures
import fixtures.messages.sections.items._
import models.requests.DataRequest
import models.response.referenceData.{BulkPackagingType, ItemPackaging}
import models.sections.items._
import models.{CheckMode, UserAnswers}
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{Actions, SummaryList, SummaryListRow, Value}
import viewmodels.checkAnswers.sections.items._
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ItemCheckAnswersHelperSpec extends SpecBase with ItemFixtures {
  val messagesForLanguage: ItemCheckAnswersMessages.English.type = ItemCheckAnswersMessages.English

  val baseUserAnswers: UserAnswers = emptyUserAnswers
    .set(ItemExciseProductCodePage(testIndex1), testEpcWine)

  class Test(val userAnswers: UserAnswers) {
    lazy implicit val request: DataRequest[AnyContentAsEmpty.type] = dataRequest(FakeRequest(), userAnswers, testErn)
    lazy implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))
    lazy val itemExciseProductCodeSummary: ItemExciseProductCodeSummary = app.injector.instanceOf[ItemExciseProductCodeSummary]
    lazy val itemCommodityCodeSummary: ItemCommodityCodeSummary = app.injector.instanceOf[ItemCommodityCodeSummary]
    lazy val itemWineOperationsChoiceSummary: ItemWineOperationsChoiceSummary = app.injector.instanceOf[ItemWineOperationsChoiceSummary]
    lazy val itemWineMoreInformationSummary: ItemWineMoreInformationSummary = app.injector.instanceOf[ItemWineMoreInformationSummary]
    lazy val itemBulkPackagingSealTypeSummary: ItemBulkPackagingSealTypeSummary = app.injector.instanceOf[ItemBulkPackagingSealTypeSummary]

    lazy val helper = new ItemCheckAnswersHelper(
      itemExciseProductCodeSummary = itemExciseProductCodeSummary,
      itemCommodityCodeSummary = itemCommodityCodeSummary,
      itemWineOperationsChoiceSummary = itemWineOperationsChoiceSummary,
      itemWineMoreInformationSummary = itemWineMoreInformationSummary,
      itemBulkPackagingSealTypeSummary = itemBulkPackagingSealTypeSummary
    )
  }

  "ItemCheckAnswersHelper" - {
    "constructItemDetailsCard" - {
      "must return rows" in new Test(baseUserAnswers) {
        val card: SummaryList = helper.constructItemDetailsCard(testIndex1, testCommodityCodeWine)

        card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitleItemDetails, 3, None))
        card.rows must not be empty
      }
    }

    "constructQuantityCard" - {
      "must return all rows" in new Test(
        baseUserAnswers
          .set(ItemQuantityPage(testIndex1), BigDecimal(1.23))
          .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(BigDecimal(4.56), BigDecimal(7.89)))
      ) {
        val card: SummaryList = helper.constructQuantityCard(testIndex1, testCommodityCodeWine)

        card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitleQuantity, 3, None))
        card.rows.length mustBe 3
      }
    }

    "constructWineDetailsCard" - {
      "must return rows" in new Test(
        baseUserAnswers
          .set(ItemWineOperationsChoicePage(testIndex1), Seq())
      ) {
        val card: SummaryList = helper.constructWineDetailsCard(testIndex1)

        card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitleWineDetails, 3, None))
        card.rows must not be empty
      }
    }

    "constructPackagingCard" - {
      "must return rows" - {
        "when bulk" in new Test(
          baseUserAnswers
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
        ) {
          val card: SummaryList = helper.constructPackagingCard(testIndex1, testCommodityCodeTobacco)

          card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitlePackaging, 3, None))
          card.rows must not be empty
        }
        "when not bulk" in new Test(
          baseUserAnswers
            .set(ItemBulkPackagingChoicePage(testIndex1), false)
        ) {
          val card: SummaryList = helper.constructPackagingCard(testIndex1, testCommodityCodeTobacco)

          card.card mustBe Some(CardViewModel(messagesForLanguage.cardTitlePackaging, 3, Some(Actions(items = Seq(ActionItemViewModel(
            messagesForLanguage.change,
            href = controllers.sections.items.routes.ItemsPackagingAddToListController.onPageLoad(request.ern, request.arc, testIndex1).url,
            s"changeItemPackaging${testIndex1.displayIndex}"
          ).withVisuallyHiddenText(ItemsPackagingAddToListMessages.English.cyaChangeHidden))))))
          card.rows must not be empty
        }
      }
    }

    "bulkPackagingSummaryListRows" - {
      val numberOfRows = 5

      s"must return all rows" - {
        "when all fields are filled in" in new Test(
          emptyUserAnswers
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
            .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(ItemBulkPackagingCode.BulkLiquid, "desc"))
            .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
            .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("type", Some("info")))
        ) {
          val result: Seq[SummaryListRow] = helper.bulkPackagingSummaryListRows(testIndex1, testCommodityCodeWine)

          result.length mustBe numberOfRows

          result.map(_.key.content) mustBe Seq(
            Text(ItemBulkPackagingChoiceMessages.English.cyaLabel),
            Text(ItemBulkPackagingSelectMessages.English.cyaLabel),
            Text(ItemPackagingSealChoiceMessages.English.cyaLabel),
            Text(ItemPackagingSealTypeMessages.English.cyaLabelSealType),
            Text(ItemPackagingSealTypeMessages.English.cyaLabelSealInformation)
          )
        }
      }
    }

    "notBulkPackagingSummaryListRows" - {
      "must return a Bulk Package row, and one row per packaging type" - {
        "when packaging types are filled in" in new Test(
          emptyUserAnswers
            .set(ItemBulkPackagingChoicePage(testIndex1), false)
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("type 1", "description 1"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "4")
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), ItemPackaging("type 2", "description 2"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "15")
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex3), ItemPackaging("type 3", "description 3"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex3), "7")
        ) {
          val result: Seq[SummaryListRow] = helper.notBulkPackagingSummaryListRows(testIndex1, testCommodityCodeTobacco)

          result.length mustBe 4

          result mustBe Seq(
            SummaryListRowViewModel(
              key = ItemBulkPackagingChoiceMessages.English.cyaLabel,
              value = Value(Text(ItemBulkPackagingChoiceMessages.English.no)),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.items.routes.ItemBulkPackagingChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  id = "changeItemBulkPackagingChoice1"
                ).withVisuallyHiddenText(ItemBulkPackagingChoiceMessages.English.cyaChangeHidden("tobacco"))
              )
            ),
            SummaryListRowViewModel(
              key = messagesForLanguage.packagingKey(testPackagingIndex1),
              value = ValueViewModel(messagesForLanguage.packagingValue("4", "description 1"))
            ),
            SummaryListRowViewModel(
              key = messagesForLanguage.packagingKey(testPackagingIndex2),
              value = ValueViewModel(messagesForLanguage.packagingValue("15", "description 2"))
            ),
            SummaryListRowViewModel(
              key = messagesForLanguage.packagingKey(testPackagingIndex3),
              value = ValueViewModel(messagesForLanguage.packagingValue("7", "description 3"))
            )
          )
        }
      }

      "must not return any packaging type rows" - {
        "if both ItemSelectPackagingPage and ItemPackagingQuantityPage are not present" in new Test(
          emptyUserAnswers
            .set(ItemBulkPackagingChoicePage(testIndex1), false)
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("type 1", "description 1"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "15")
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex3), true)
        ) {
          val result: Seq[SummaryListRow] = helper.notBulkPackagingSummaryListRows(testIndex1, testCommodityCodeTobacco)

          result.length mustBe 1

          result mustBe Seq(
            SummaryListRowViewModel(
              key = ItemBulkPackagingChoiceMessages.English.cyaLabel,
              value = Value(Text(ItemBulkPackagingChoiceMessages.English.no)),
              actions = Seq(
                ActionItemViewModel(
                  content = messagesForLanguage.change,
                  href = controllers.sections.items.routes.ItemBulkPackagingChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode).url,
                  id = "changeItemBulkPackagingChoice1"
                ).withVisuallyHiddenText(ItemBulkPackagingChoiceMessages.English.cyaChangeHidden("tobacco"))
              )
            )
          )
        }
      }
    }
  }
}
