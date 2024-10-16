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

package models

import config.Constants.NONGBVAT
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsString, Json}

class VatNumberModelSpec extends AnyFreeSpec with Matchers {

  ".reads" - {

    "when reading the old format" - {

      s"must return true and Some(value) when NOT '$NONGBVAT'" in {

        val json = JsString("123456789")
        val expectedModel = VatNumberModel(hasVatNumber = true, Some("123456789"))

        json.as[VatNumberModel] mustEqual expectedModel
      }

      s"must return false and None when '$NONGBVAT'" in {

        val json = JsString(NONGBVAT)
        val expectedModel = VatNumberModel(hasVatNumber = false, None)

        json.as[VatNumberModel] mustEqual expectedModel
      }
    }

    "when reading the new format" - {

      s"must return true and Some(value) when true" in {

        val json = Json.obj(
          "hasVatNumber" -> true,
          "vatNumber" -> "123456789"
        )
        val expectedModel = VatNumberModel(hasVatNumber = true, Some("123456789"))

        json.as[VatNumberModel] mustEqual expectedModel
      }

      s"must return false and None when false" in {

        val json = Json.obj(
          "hasVatNumber" -> false
        )
        val expectedModel = VatNumberModel(hasVatNumber = false, None)

        json.as[VatNumberModel] mustEqual expectedModel
      }
    }
  }
}
