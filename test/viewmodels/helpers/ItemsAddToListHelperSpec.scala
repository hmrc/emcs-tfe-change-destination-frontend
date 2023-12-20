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
import controllers.sections.items.routes
import fixtures.ItemFixtures
import fixtures.messages.sections.items.ItemsAddToListMessages
import mocks.services.MockGetCnCodeInformationService
import models.UnitOfMeasure.{Litres20, Thousands}
import models.requests.{CnCodeInformationItem, DataRequest}
import models.response.referenceData.{BulkPackagingType, CnCodeInformation}
import models.sections.items.ItemBrandNameModel
import models.sections.items.ItemBulkPackagingCode.BulkLiquid
import models.{NormalMode, UserAnswers}
import pages.sections.items._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import uk.gov.hmrc.http.HeaderCarrier
import viewmodels.checkAnswers.sections.items._
import views.html.components.{span, tag}

import scala.concurrent.{ExecutionContext, Future}

class ItemsAddToListHelperSpec extends SpecBase with ItemFixtures with MockGetCnCodeInformationService {

  implicit lazy val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val span: span = app.injector.instanceOf[span]
  implicit lazy val tag: tag = app.injector.instanceOf[tag]
  lazy val itemPackagingSummary: ItemPackagingSummary = app.injector.instanceOf[ItemPackagingSummary]

  val headingLevel = 2

  lazy val helper = new ItemsAddToListHelper(
    tag = tag,
    span = span,
    cnCodeInformationService = mockGetCnCodeInformationService,
    itemPackagingSummary = itemPackagingSummary
  )

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)
  }

  "ItemsAddToListHelper" - {

    Seq(ItemsAddToListMessages.English).foreach { messagesForLanguage =>

      implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when rendered for language of '${messagesForLanguage.lang.code}'" - {

        "return nothing" - {

          s"when no answers specified" in new Setup() {

            helper.allItemsSummary.futureValue mustBe Nil
          }
        }

        "return required rows when all answers filled out" - {

          s"when the row is Complete" in new Setup(singleCompletedWineItem) {

            val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
              .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

            helper.allItemsSummary.futureValue mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(
                    content = HtmlContent(span(messagesForLanguage.itemCardTitle(testIndex1))),
                    headingLevel = Some(headingLevel)
                  )),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.change),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "changeItem-1")
                    ),
                    ActionItem(
                      href = routes.ItemRemoveItemController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "removeItem-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemBrandNameSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemCommercialDescriptionSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemQuantitySummary.row(testIndex1, Litres20, showChangeLinks = false).get,
                  itemPackagingSummary.row(testIndex1).get
                )
              )
            )
          }

          s"when item is complete but packaging is missing (NOT BULK) (edit link to Add First Package)" in new Setup(singleCompletedWineItem
            .remove(ItemSelectPackagingPage(testIndex1, testPackagingIndex1))
            .remove(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1))
            .remove(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1))
            .remove(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1))
          ) {

            val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
              .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

            helper.allItemsSummary.futureValue mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(
                    content = HtmlContent(HtmlFormat.fill(Seq(
                      span(messagesForLanguage.itemCardTitle(testIndex1), Some("govuk-!-margin-right-2")),
                      tag(messagesForLanguage.incomplete, "red")
                    ))),
                    headingLevel = Some(headingLevel)
                  )),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemSelectPackagingController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode).url,
                      content = Text(messagesForLanguage.continueEditing),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "editItem-1")
                    ),
                    ActionItem(
                      href = routes.ItemRemoveItemController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "removeItem-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemBrandNameSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemCommercialDescriptionSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemQuantitySummary.row(testIndex1, Litres20, showChangeLinks = false).get
                )
              )
            )
          }

          s"when item is complete but one package in incomplete (NOT BULK) (edit link to Packaging Add to List)" in new Setup(singleCompletedWineItem
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), testPackageBag)
          ) {

            val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
              .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

            helper.allItemsSummary.futureValue mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(
                    content = HtmlContent(HtmlFormat.fill(Seq(
                      span(messagesForLanguage.itemCardTitle(testIndex1), Some("govuk-!-margin-right-2")),
                      tag(messagesForLanguage.incomplete, "red")
                    ))),
                    headingLevel = Some(headingLevel)
                  )),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.continueEditing),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "editItem-1")
                    ),
                    ActionItem(
                      href = routes.ItemRemoveItemController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "removeItem-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemBrandNameSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemCommercialDescriptionSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemQuantitySummary.row(testIndex1, Litres20, showChangeLinks = false).get,
                  itemPackagingSummary.row(testIndex1).get
                )
              )
            )
          }

          s"when item is complete but packaging is missing (BULK) (edit link to Bulk Packaging Choice page)" in new Setup(singleCompletedWineItem
            .remove(ItemSelectPackagingPage(testIndex1, testPackagingIndex1))
            .remove(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1))
            .remove(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1))
            .remove(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1))
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
            .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Tanker"))
          ) {

            val item = CnCodeInformationItem(testEpcWine, testCnCodeWine)

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item))
              .returns(Future.successful(Seq(item -> CnCodeInformation(item.cnCode, "Sparkling Wine", item.productCode, "Wine", Litres20))))

            helper.allItemsSummary.futureValue mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(
                    content = HtmlContent(HtmlFormat.fill(Seq(
                      span(messagesForLanguage.itemCardTitle(testIndex1), Some("govuk-!-margin-right-2")),
                      tag(messagesForLanguage.incomplete, "red")
                    ))),
                    headingLevel = Some(headingLevel)
                  )),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemBulkPackagingChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode).url,
                      content = Text(messagesForLanguage.continueEditing),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "editItem-1")
                    ),
                    ActionItem(
                      href = routes.ItemRemoveItemController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "removeItem-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemBrandNameSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemCommercialDescriptionSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemQuantitySummary.row(testIndex1, Litres20, showChangeLinks = false).get,
                  itemPackagingSummary.row(testIndex1).get
                )
              )
            )
          }

          s"when all answers entered and there is both a Completed and an InProgress item row" in new Setup(singleCompletedWineItem
            .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex2), testCnCodeTobacco)
            .set(ItemBrandNamePage(testIndex2), ItemBrandNameModel(hasBrandName = false, None))
            .set(ItemCommercialDescriptionPage(testIndex2), "Wine from apples")
          ) {

            val item1 = CnCodeInformationItem(testEpcWine, testCnCodeWine)
            val item2 = CnCodeInformationItem(testEpcTobacco, testCnCodeTobacco)

            MockGetCnCodeInformationService.getCnCodeInformationWithMovementItems(Seq(item1, item2))
              .returns(Future.successful(Seq(
                item1 -> CnCodeInformation(item1.cnCode, "Sparkling Wine", item1.productCode, "Wine", Litres20),
                item2 -> CnCodeInformation(item2.cnCode, "Cigars", item2.productCode, "Tobacco", Thousands)
              )))

            helper.allItemsSummary.futureValue mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(
                    content = HtmlContent(span(messagesForLanguage.itemCardTitle(testIndex1))),
                    headingLevel = Some(headingLevel)
                  )),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.change),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "changeItem-1")
                    ),
                    ActionItem(
                      href = routes.ItemRemoveItemController.onPageLoad(testErn, testDraftId, testIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex1)),
                      attributes = Map("id" -> "removeItem-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemBrandNameSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemCommercialDescriptionSummary.row(testIndex1, showChangeLinks = false).get,
                  ItemQuantitySummary.row(testIndex1, Litres20, showChangeLinks = false).get,
                  itemPackagingSummary.row(testIndex1).get
                )
              ),
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(
                    content = HtmlContent(HtmlFormat.fill(Seq(
                      span(messagesForLanguage.itemCardTitle(testIndex2), Some("govuk-!-margin-right-2")),
                      tag(messagesForLanguage.incomplete, "red")
                    ))),
                    headingLevel = Some(headingLevel)
                  )),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex2, NormalMode).url,
                      content = Text(messagesForLanguage.continueEditing),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex2)),
                      attributes = Map("id" -> "editItem-2")
                    ),
                    ActionItem(
                      href = routes.ItemRemoveItemController.onPageLoad(testErn, testDraftId, testIndex2).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.itemCardTitle(testIndex2)),
                      attributes = Map("id" -> "removeItem-2")
                    )
                  )))
                )),
                rows = Seq(
                  ItemBrandNameSummary.row(testIndex2, showChangeLinks = false).get,
                  ItemCommercialDescriptionSummary.row(testIndex2, showChangeLinks = false).get
                )
              )
            )
          }
        }
      }
    }
  }
}
