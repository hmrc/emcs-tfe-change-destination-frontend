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

object ItemCommercialDescriptionMessages {
  sealed trait ViewMessages extends BaseMessages { _: i18n =>
    def heading(goodsType: String) = s"Enter a commercial description for the $goodsType"
    def title(goodsType: String): String = titleHelper(heading(goodsType))
    val checkYourAnswersLabel = "Commercial Description"
    val errorRequired = "Enter a commercial description of the goods"
    val paragraph = "This will be used to help identify the goods."
    val errorLength = "Commercial description must be 350 characters or less"
    val changehidden = "commercial description"
    val hintb = "This will be used to help identify the goods. For example: 5 x cases of 12 x 330ml cans session IPA."
    val hintw = "This will be used to help identify the goods. For example: 50 x 750 ml bottles of merlot red wine, Bordeaux, France, vintage year 2021."
    val hinti = "Include any information that will help to identify the goods, including the number of the lowest level of packaging used, such as bottles."
    val hints = "This will be used to help identify the goods. For example: 20 x 1 litre bottles of London dry gin."
    val hintt = "This will be used to help identify the goods. For example: 500 x 30g packets of hand rolling tobacco."
    val hinte = "Include any information that will help to identify the goods."
    val detailsBeerP = "Include any information that will help to identify the goods, such as:"
    val detailsBeerB1 = "the number of the lowest level of packaging used, such as cans, bottles or kegs"
    val detailsBeerB2 = "if transporting multipacks or cases, state both the number of multipacks or cases, and the number of individual units. For example: 10 x cases of 12 x 300ml bottles of lager beer"
    val detailsWineP = "Include any information that will help to identify the goods, such as:"
    val detailsWineB1 = "the number and volume of the lowest level of packaging used, such as bottles or barrels"
    val detailsWineB2 = "the region and country of origin"
    val detailsWineB3 = "the grape variety, such as ‘merlot’"
    val detailsWineB4 = "any production method terms such as ‘oak-aged’ or ‘extra-dry’"
    val detailsWineB5 = "vintage year"
    val detailsEthylAlcoholP = "Include any information that will help to identify the goods, such as:"
    val detailsEthylAlcoholB1 = "the number and volume of the lowest level of packaging used, such as cans, bottles or drums"
    val detailsEthylAlcoholB2 = "if transporting multipacks state both the number of multipacks and number of individual units. For example: 5 x boxes of 6 x 35cl bottles of London dry gin"
    val detailsEthylAlcoholB3 = "the spirit name (such as rum or vodka)"
    val detailsEthylAlcoholB4 = "if the spirit drink is dry, blended or mixed"
    val detailsTobaccoP = "Include any information that will help to identify the goods, such as:"
    val detailsTobaccoB1 = "the number of the lowest level of packaging used, such as individual cigarette packets or cigar boxes"
    val detailsTobaccoB2 = "if transporting multipacks state both the number of multipacks and number of individual units. For example: 200 x multipacks of 5 x 20 cigarettes"
    val detailsTobaccoB3 = "if transporting hand rolling or pipe tobacco state the number and weight of individual packets"
    val summary = "Help with commercial description"
    val errorXss = "Description cannot contain < and > and : and ;"
    val cyaLabel = "Commercial Description"
    val valueWhenAnswerNotPresent = "Enter a commercial description of the goods"
    val cyaChangeHidden = "commercial description"
  }

  object English extends ViewMessages with BaseEnglish


}

