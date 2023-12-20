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

package models.response

import base.SpecBase
import fixtures.ItemFixtures
import models.response.referenceData.ItemPackaging
import play.api.libs.json._

class ItemPackagingSpec extends SpecBase with ItemFixtures {

  "ItemPackaging" - {
    "should be able to parse a valid JSON object response" in {
      val result = Json.fromJson(testItemPackagingTypesJson)(ItemPackaging.seqReads)
      result.isSuccess mustBe true
      result.get mustBe testItemPackagingTypes
    }

    "should return a JsError when the return type is not JsObject (e.g., JsArray)" in {
      val jsonArray = JsArray(Seq(JsString("x"), JsString("y"), JsString("z")))
      val result = Json.fromJson(jsonArray)(ItemPackaging.seqReads)
      result.isError mustBe true
    }
  }
}
