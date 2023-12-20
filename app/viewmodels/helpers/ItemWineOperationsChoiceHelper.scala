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

import models.response.referenceData.WineOperations
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.{CheckboxItem, ExclusiveCheckbox}

object ItemWineOperationsChoiceHelper {

  def formatCheckboxesForDisplay(items: Seq[CheckboxItem]): Seq[CheckboxItem] = {
    val dividerItem = Seq(CheckboxItem(divider = Some("or")))

    items
      .sortBy(_.value.toInt)
      .partition(_.value == WineOperations.nonWineOperationCode) match {
      case (nonWineOperations, wineOperations) =>
        val exclusiveOption = nonWineOperations.map(_.copy(behaviour = Some(ExclusiveCheckbox)))
        wineOperations ++ dividerItem ++ exclusiveOption
    }

  }
}
