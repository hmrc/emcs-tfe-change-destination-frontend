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

package models

import play.api.i18n.Messages

sealed trait GoodsType {
  val code: String

  def toPluralOutput()(implicit messages: Messages): String = messages(s"goodsType.$code.plural")

  def toSingularOutput()(implicit messages: Messages): String = messages(s"goodsType.$code.singular")

  val isAlcohol: Boolean = Seq(GoodsType.Beer.code, GoodsType.Wine.code, GoodsType.Spirits.code, GoodsType.Intermediate.code).contains(code)
}

object GoodsType {
  case object Beer extends GoodsType {
    override val code: String = "B"
  }

  case object Wine extends GoodsType {
    override val code: String = "W"
  }

  case class Fermented(epc: String) extends GoodsType {
    override val code: String = epc.take(1)

    override val isAlcohol: Boolean = true
  }

  case object Energy extends GoodsType {
    override val code: String = "E"
  }

  case object Spirits extends GoodsType {
    override val code: String = "S"
  }

  case object Tobacco extends GoodsType {
    override val code: String = "T"
  }

  case object Intermediate extends GoodsType {
    override val code: String = "I"
  }

  // TODO: see below
  // Fermented is currently only used in BodyEadEsadModel.
  // When (if) we implement Fermented text throughout the frontend, remove default value and (maybe) change from Option[String] to String.
  def apply(epc: String, cnCode: Option[String] = None): GoodsType = {
    if(cnCode.exists(fermentedBeverages.contains)) {
      Fermented(epc = epc)
    } else {
      epc.take(1) match {
        case Beer.code => Beer
        case Wine.code => Wine
        case Energy.code => Energy
        case Spirits.code => Spirits
        case Tobacco.code => Tobacco
        case Intermediate.code => Intermediate
        case invalid => throw new IllegalArgumentException(s"Invalid argument of '$invalid' received which can not be mapped to a GoodsType")
      }
    }
  }

  val fermentedBeverages: Seq[String] = Seq(
    "22060031",
    "22060039",
    "22060051",
    "22060059",
    "22060081",
    "22060089"
  )

  val values: Seq[GoodsType] = Seq(Beer, Wine, Energy, Spirits, Tobacco, Intermediate)
}
