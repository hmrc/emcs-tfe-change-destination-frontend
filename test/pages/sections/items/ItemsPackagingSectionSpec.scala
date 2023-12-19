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
import models.requests.DataRequest
import models.sections.items.ItemPackagingSealTypeModel
import models.sections.items.ItemsPackagingAddToList.MoreLater
import play.api.test.FakeRequest

class ItemsPackagingSectionSpec extends SpecBase {

  val completedPackage = emptyUserAnswers
    .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
    .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "5")
    .set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
    .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "SHIP")
    .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
    .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("SEAL", Some("INFO")))

  "isCompleted" - {

    "MAX packages should be 99" in {
      ItemsPackagingSection(testIndex1).MAX mustBe 99
    }

    "must return true" - {

      "when all mandatory pages have been answered" in {

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), completedPackage)

        ItemsPackagingSection(testIndex1).isCompleted mustBe true
      }

      "when no shipping mark exists but all other mandatory pages are present" in {

        val userAnswers = completedPackage.remove(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1))

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe true
      }

      "when no seal, but all other mandatory pages are present" in {

        val userAnswers = completedPackage
          .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
          .remove(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1))

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe true
      }
    }

    "must return false" - {

      "when select packaging page is missing" in {

        val userAnswers = completedPackage.remove(ItemSelectPackagingPage(testIndex1, testPackagingIndex1))

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe false
      }

      "when packaging quantity page is missing" in {

        val userAnswers = completedPackage.remove(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1))

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe false
      }

      "when packaging product type is missing" in {

        val userAnswers = completedPackage.remove(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1))

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe false
      }

      "when seal choice is `true` but no seal type exists" in {

        val userAnswers = completedPackage.remove(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1))

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe false
      }

      "when there's no packages" in {

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe false
      }

      "when there's packages but the user has indicated that they are going to add more" in {

        val userAnswers = completedPackage.set(ItemsPackagingAddToListPage(testIndex1), MoreLater)

        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), userAnswers)

        ItemsPackagingSection(testIndex1).isCompleted mustBe false
      }
    }
  }

}
