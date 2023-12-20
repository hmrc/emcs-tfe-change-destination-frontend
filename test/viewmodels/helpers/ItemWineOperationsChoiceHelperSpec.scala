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
import fixtures.ItemFixtures
import forms.sections.items.ItemWineOperationsChoiceFormProvider
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.{CheckboxItem, ExclusiveCheckbox}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, HtmlContent}

import scala.util.Random

class ItemWineOperationsChoiceHelperSpec extends SpecBase with ItemFixtures {

  val form = new ItemWineOperationsChoiceFormProvider()(testWineOperations)

  "formatCheckboxesForDisplay" - {

    "must return the expected ordering" in {

      val randomisedCheckboxItems = Random.shuffle(testWineOperations).map(wineOperations =>
        CheckboxItem(HtmlContent(wineOperations.description), value = wineOperations.code)
      )

      ItemWineOperationsChoiceHelper.formatCheckboxesForDisplay(randomisedCheckboxItems) mustBe Seq(
        CheckboxItem(HtmlContent("The product has been enriched"), value = "1"),
        CheckboxItem(HtmlContent("The product has been acidified"), value = "2"),
        CheckboxItem(HtmlContent("The product has been de-acidified"), value = "3"),
        CheckboxItem(HtmlContent("The product has been sweetened"), value = "4"),
        CheckboxItem(HtmlContent("The product has been fortified for distillation"), value = "5"),
        CheckboxItem(HtmlContent("A product originating in a geographical unit other than that indicated in the description has been added to the product"), value = "6"),
        CheckboxItem(HtmlContent("A product obtained from a vine variety other than that indicated in the description has been added to the product"), value = "7"),
        CheckboxItem(HtmlContent("A product harvested during a year other than that indicated in the description has been added to the product"), value = "8"),
        CheckboxItem(HtmlContent("The product has been made using oak chips"), value = "9"),
        CheckboxItem(HtmlContent("The product has been made on the basis of experimental use of a new oenological practice"), value = "10"),
        CheckboxItem(HtmlContent("The product has been partially dealcoholised"), value = "11"),
        CheckboxItem(HtmlContent("Other operations"), value = "12"),
        CheckboxItem(Empty, divider = Some("or")),
        CheckboxItem(HtmlContent("The product has undergone none of the following operations"), value = "0", behaviour = Some(ExclusiveCheckbox))
      )
    }
  }
}
