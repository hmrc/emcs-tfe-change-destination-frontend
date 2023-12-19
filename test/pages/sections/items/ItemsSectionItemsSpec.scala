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
import play.api.test.FakeRequest

class ItemsSectionItemsSpec extends SpecBase with ItemFixtures {

  "isCompleted" - {

    "must return true" - {

      "when all items are completed" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem)

        ItemsSectionItems.isCompleted mustBe true
      }
    }

    "must return false" - {

      "when no items added" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), emptyUserAnswers)

        ItemsSectionItems.isCompleted mustBe false
      }

      "when an item exists which is not complete" in {

        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), singleCompletedWineItem
          .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
        )

        ItemsSectionItems.isCompleted mustBe false
      }
    }
  }
}
