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

package fixtures.messages.sections.items

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object ItemSmallIndependentProducerMessages {

  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"Can you confirm that the producer of the $goodsType is certified as an independent small producer?"
    def title(goodsType: String): String = titleHelper(heading(goodsType))

    def yesCertified(value: String) = s"Yes - $value"
    val yesBeer = "It is hereby certified that the product described has been produced by an independent small brewery"
    val yesWine = "It is hereby certified that the product described has been produced by an independent small wine producer"
    val yesFermented = "It is hereby certified that the product described has been produced by an independent small producer of fermented beverages other than wine and beer"
    val yesIntermediate = "It is hereby certified that the product described has been produced by an independent small intermediate products producer"
    val yesSpirits = "It is hereby certified that the product described has been produced by an independent small distillery"
    val yesOther = "It is hereby certified that the alcoholic product described has been produced by an independent small producer"

    val cyaLabel = "Independent small producer"
    val cyaChangeHidden = "if the producer is an independent small producer"
  }

  object English extends ViewMessages with BaseEnglish

}
