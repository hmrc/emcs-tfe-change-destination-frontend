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

package models.submitChangeDestination

import models.{Enumerable, WithName}

sealed trait ItemWineCategory

object ItemWineCategory extends Enumerable.Implicits {

  case object EuWineWithoutPdoOrPgi extends WithName("1") with ItemWineCategory

  case object EuVarietalWineWithoutPdoOrPgi extends WithName("2") with ItemWineCategory

  case object EuWineWithPdoOrPgiOrGi extends WithName("3") with ItemWineCategory

  case object ImportedWine extends WithName("4") with ItemWineCategory

  case object Other extends WithName("5") with ItemWineCategory

  val values: Seq[ItemWineCategory] = Seq(
    EuWineWithoutPdoOrPgi,
    EuVarietalWineWithoutPdoOrPgi,
    EuWineWithPdoOrPgiOrGi,
    ImportedWine,
    Other
  )

  val varietalWines: Seq[String] = Seq(
    "22042181",
    "22042182",
    "22042195",
    "22042196",
    "22042281",
    "22042282",
    "22042295",
    "22042296",
    "22042981",
    "22042982",
    "22042995",
    "22042996",
    "22041096",
    "22042108"
  )

  implicit val enumerable: Enumerable[ItemWineCategory] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
