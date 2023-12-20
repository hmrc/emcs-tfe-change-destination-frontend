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

package models.sections.items

import base.SpecBase
import models.sections.items.ItemGeographicalIndicationType._
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Hint, RadioItem}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class ItemGeographicalIndicationTypeSpec extends SpecBase {

  "ItemGeographicalIndicationTypeSpec" - {
    ".values" - {
      "should return all the geographical indication options" in {
        ItemGeographicalIndicationType.values mustBe Seq(
          ProtectedDesignationOfOrigin, ProtectedGeographicalIndication, GeographicalIndication, NoGeographicalIndication
        )
      }
    }

    ".options" - {
      "return all the radio options for geographical indications" in {
        implicit val msgs: Messages = messages(FakeRequest())

        ItemGeographicalIndicationType.options mustBe Seq(
          RadioItem(
            content = Text(msgs(s"itemGeographicalIndicationChoice.PDO")),
            value = Some("PDO"),
            hint = Some(Hint(content = Text(msgs(s"itemGeographicalIndicationChoice.PDO.hint")))),
            id = Some(s"value_PDO")
          ),
          RadioItem(
            content = Text(msgs(s"itemGeographicalIndicationChoice.PGI")),
            value = Some("PGI"),
            hint = Some(Hint(content = Text(msgs(s"itemGeographicalIndicationChoice.PGI.hint")))),
            id = Some(s"value_PGI")
          ),
          RadioItem(
            content = Text(msgs(s"itemGeographicalIndicationChoice.GI")),
            value = Some("GI"),
            hint = Some(Hint(content = Text(msgs(s"itemGeographicalIndicationChoice.GI.hint")))),
            id = Some(s"value_GI")
          ),
          RadioItem(
            divider = Some(msgs(s"site.divider"))
          ),
          RadioItem(
            content = Text(msgs(s"itemGeographicalIndicationChoice.None")),
            value = Some("None"),
            hint = None,
            id = Some(s"value_None")
          ),
        )
      }
    }
  }

}
