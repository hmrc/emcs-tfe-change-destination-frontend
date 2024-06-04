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

package models.sections.consignee

import base.SpecBase
import fixtures.messages.sections.consignee.ConsigneeExportInformationMessages
import models.sections.consignee.ConsigneeExportInformation.{EoriNumber, NoInformation, VatNumber}
import play.api.i18n.Messages
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.{CheckboxItem, ExclusiveCheckbox}

class ConsigneeExportInformationSpec extends SpecBase {

  implicit val msgs: Messages = messages(FakeRequest())

  "ConsigneeExportInformation" - {

    ".options" - {

      Seq(ConsigneeExportInformationMessages.English).foreach { messagesForLang =>

        "should render the correct set of checkboxes" in {

          ConsigneeExportInformation.options() mustBe Seq(
            CheckboxItem(
              content = Text(messagesForLang.checkboxItemForVat),
              value = VatNumber.toString,
              id = Some("value")
            ),
            CheckboxItem(
              content = Text(messagesForLang.checkboxItemForEori),
              value = EoriNumber.toString,
              id = Some("value_1")
            ),
            CheckboxItem(
              divider = Some(messagesForLang.or)
            ),
            CheckboxItem(
              content = Text(messagesForLang.checkboxItemForNoInfo),
              value = NoInformation.toString,
              id = Some("value_2"),
              behaviour = Some(ExclusiveCheckbox)
            )
          )
        }
      }
    }

  }
}
