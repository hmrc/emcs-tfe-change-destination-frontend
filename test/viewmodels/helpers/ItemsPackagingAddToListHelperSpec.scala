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
import fixtures.DocumentTypeFixtures
import fixtures.messages.sections.items.ItemsPackagingAddToListMessages
import models.sections.items.ItemPackagingSealTypeModel
import models.{NormalMode, UserAnswers}
import pages.sections.items._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.checkAnswers.sections.items._
import views.html.components.{link, span, tag}

class ItemsPackagingAddToListHelperSpec extends SpecBase with DocumentTypeFixtures {

  class Setup(userAnswers: UserAnswers = emptyUserAnswers) {
    implicit lazy val link = app.injector.instanceOf[link]
    implicit lazy val request = dataRequest(FakeRequest(), userAnswers)
    implicit lazy val span = app.injector.instanceOf[span]
    implicit lazy val tag = app.injector.instanceOf[tag]

    lazy val itemPackagingSealInformationSummary = app.injector.instanceOf[ItemPackagingSealInformationSummary]
    lazy val helper: ItemsPackagingAddToListHelper = app.injector.instanceOf[ItemsPackagingAddToListHelper]
  }

  "ItemsPackagingAddToListHelper" - {

    Seq(ItemsPackagingAddToListMessages.English).foreach { messagesForLanguage =>

      implicit lazy val msgs: Messages = messages(Seq(messagesForLanguage.lang))

      s"when rendered for language of '${messagesForLanguage.lang.code}'" - {

        "return nothing" - {

          s"when no answers specified" in new Setup() {

            helper.allPackagesSummary(testIndex1) mustBe Nil
          }
        }

        "return required rows when all answers filled out" - {

          s"when the row is Complete" in new Setup(emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIP")
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", Some("INFO")))
          ) {

            helper.allPackagesSummary(testIndex1) mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(HtmlContent(span(messagesForLanguage.packageCardTitle(testPackagingIndex1))))),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemPackagingRemovePackageController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.packageCardTitle(testPackagingIndex1)),
                      attributes = Map("id" -> "removePackage-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingQuantitySummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingShippingMarksSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingSealChoiceSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingSealTypeSummary.row(testIndex1, testPackagingIndex1).get,
                  itemPackagingSealInformationSummary.row(testIndex1, testPackagingIndex1).get
                )
              )
            )
          }

          s"when the row is Complete no Shipping Mark, Seal with no additional Information for the seal" in new Setup(emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", None))
          ) {

            helper.allPackagesSummary(testIndex1) mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(HtmlContent(span(messagesForLanguage.packageCardTitle(testPackagingIndex1))))),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemPackagingRemovePackageController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.packageCardTitle(testPackagingIndex1)),
                      attributes = Map("id" -> "removePackage-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingQuantitySummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingSealChoiceSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingSealTypeSummary.row(testIndex1, testPackagingIndex1).get,
                  itemPackagingSealInformationSummary.row(testIndex1, testPackagingIndex1).get
                )
              )
            )
          }

          s"when the row is Complete with No Seal" in new Setup(emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
          ) {

            helper.allPackagesSummary(testIndex1) mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(HtmlContent(span(messagesForLanguage.packageCardTitle(testPackagingIndex1))))),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemPackagingRemovePackageController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.packageCardTitle(testPackagingIndex1)),
                      attributes = Map("id" -> "removePackage-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingQuantitySummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingSealChoiceSummary.row(testIndex1, testPackagingIndex1).get
                )
              )
            )
          }

          s"when all answers entered and there is both a Completed and an InProgress row" in new Setup(emptyUserAnswers
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
            .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), testPackageAerosol)
          ) {

            helper.allPackagesSummary(testIndex1) mustBe Seq(
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(HtmlContent(span(messagesForLanguage.packageCardTitle(testPackagingIndex1))))),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemPackagingRemovePackageController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.packageCardTitle(testPackagingIndex1)),
                      attributes = Map("id" -> "removePackage-1")
                    )
                  )))
                )),
                rows = Seq(
                  ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingQuantitySummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingProductTypeSummary.row(testIndex1, testPackagingIndex1).get,
                  ItemPackagingSealChoiceSummary.row(testIndex1, testPackagingIndex1).get
                )
              ),
              SummaryList(
                card = Some(Card(
                  title = Some(CardTitle(HtmlContent(HtmlFormat.fill(Seq(
                    span(messagesForLanguage.packageCardTitle(testPackagingIndex2), Some("govuk-!-margin-right-2")),
                    tag(messagesForLanguage.incomplete, "red")
                  ))))),
                  actions = Some(Actions(items = Seq(
                    ActionItem(
                      href = routes.ItemSelectPackagingController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode).url,
                      content = Text(messagesForLanguage.continueEditing),
                      visuallyHiddenText = Some(messagesForLanguage.packageCardTitle(testPackagingIndex2)),
                      attributes = Map("id" -> "editPackage-2")
                    ),
                    ActionItem(
                      href = routes.ItemPackagingRemovePackageController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2).url,
                      content = Text(messagesForLanguage.remove),
                      visuallyHiddenText = Some(messagesForLanguage.packageCardTitle(testPackagingIndex2)),
                      attributes = Map("id" -> "removePackage-2")
                    )
                  )))
                )),
                rows = Seq(
                  ItemSelectPackagingSummary.row(testIndex1, testPackagingIndex2).get,
                )
              )
            )
          }
        }
      }
    }
  }
}
