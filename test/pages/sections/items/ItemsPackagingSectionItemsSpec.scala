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

package pages.sections.items

import base.SpecBase
import models.UserAnswers
import models.requests.DataRequest
import models.sections.items.ItemPackagingSealTypeModel
import play.api.test.FakeRequest

class ItemsPackagingSectionItemsSpec extends SpecBase {
  val completedPackagingWithSealChoiceTrue: UserAnswers =
    emptyUserAnswers
      .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
      .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "4")
      .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
      .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
      .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("test type", Some("test info")))

  val completedPackagingWithSealChoiceFalse: UserAnswers =
    emptyUserAnswers
      .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
      .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "4")
      .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
      .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)

  "isCompleted" - {
    "must return true" - {
      "if SealChoice is true and SealType has an answer" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), completedPackagingWithSealChoiceTrue)

        ItemsPackagingSectionItems(testIndex1, testPackagingIndex1).isCompleted mustBe true
      }
      "if SealChoice is false" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), completedPackagingWithSealChoiceFalse)

        ItemsPackagingSectionItems(testIndex1, testPackagingIndex1).isCompleted mustBe true
      }
    }

    "must return false" - {
      "if SealChoice is true and SealType has no answer" in {
        val userAnswers = emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "4")
          .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSectionItems(testIndex1, testPackagingIndex1).isCompleted mustBe false
      }

      Seq(ItemSelectPackagingPage(testIndex1, testPackagingIndex1),
        ItemPackagingQuantityPage(testIndex1, testPackagingIndex1),
        ItemPackagingProductTypePage(testIndex1, testPackagingIndex1)).foreach(
        page =>
          s"if $page is missing" in {
            val userAnswers = completedPackagingWithSealChoiceTrue.remove(page)

            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

            ItemsPackagingSectionItems(testIndex1, testPackagingIndex1).isCompleted mustBe false
          }
      )
    }
  }
}
