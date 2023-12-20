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
import fixtures.ItemFixtures
import models.requests.DataRequest
import models.sections.items.ItemsAddToList
import play.api.test.FakeRequest
import viewmodels.taskList._

class ItemsSectionSpec extends SpecBase with ItemFixtures {

  "status" - {

    "must return NotStarted" - {

      "when no items" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        ItemsSection.status mustBe NotStarted
      }
    }

    "must return Completed" - {

      "when all items are completed and ItemsAddToList.No" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem.set(ItemsAddToListPage, ItemsAddToList.No))

        ItemsSection.status mustBe Completed
      }
    }

    "must return InProgress" - {

      "when all items are completed and ItemsAddToList.Yes" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem.set(ItemsAddToListPage, ItemsAddToList.Yes))

        ItemsSection.status mustBe InProgress
      }

      "when all items are completed and ItemsAddToList.MoreLater" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem.set(ItemsAddToListPage, ItemsAddToList.MoreLater))

        ItemsSection.status mustBe InProgress
      }

      "when all items are completed and missing" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem)

        ItemsSection.status mustBe InProgress
      }

      "when an item exists which is not complete, even with ItemAddToList.No" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem
          .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
          .set(ItemsAddToListPage, ItemsAddToList.No)
        )

        ItemsSection.status mustBe InProgress
      }
    }
  }
}
