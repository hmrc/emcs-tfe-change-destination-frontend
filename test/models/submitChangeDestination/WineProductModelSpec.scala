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

import base.SpecBase
import fixtures.ItemFixtures
import models.{GoodsType, UserAnswers}
import models.requests.DataRequest
import models.sections.items.{ItemGeographicalIndicationType, ItemWineGrowingZone}
import pages.sections.items._
import play.api.test.FakeRequest

class WineProductModelSpec extends SpecBase with ItemFixtures {

  def userAnswers(
                   epc: String = testEpcWine,
                   cnCode: String = testCnCodeWine,
                   geographicalIndicationChoice: ItemGeographicalIndicationType = ItemGeographicalIndicationType.ProtectedDesignationOfOrigin,
                   importedChoice: Boolean = true
                 ): UserAnswers =
    emptyUserAnswers
      .set(ItemExciseProductCodePage(testIndex1), epc)
      .set(ItemCommodityCodePage(testIndex1), cnCode)
      .set(ItemGeographicalIndicationChoicePage(testIndex1), geographicalIndicationChoice)
      .set(ItemImportedWineFromEuChoicePage(testIndex1), importedChoice)
      .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CI)
      .set(ItemWineOriginPage(testIndex1), countryModelGB)
      .set(ItemWineMoreInformationPage(testIndex1), Some("more info"))
      .set(ItemWineOperationsChoicePage(testIndex1), testWineOperations)

  "apply" - {
    "must return None" - {
      "if not Wine" in {
        GoodsType.values.filterNot(_ == GoodsType.Wine).map(gt => s"${gt.code}000").foreach {
          epc =>
            implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers(epc = epc))

            WineProductModel.apply(testIndex1) mustBe None
        }
      }
    }

    "must return Some(WineProductModel)" - {
      "if wine" in {
        implicit val dr: DataRequest[_] = dataRequest(FakeRequest(), userAnswers())

        val result = WineProductModel.apply(testIndex1)

        // bodge the wine operations to be sorted so the comparison works
        WineProductModel.apply(testIndex1).map(_.copy(wineOperations = result.flatMap(_.wineOperations.map(_.sorted)))) mustBe Some(WineProductModel(
          wineProductCategory = ItemWineCategory.EuWineWithPdoOrPgiOrGi.toString,
          wineGrowingZoneCode = Some(ItemWineGrowingZone.CI.toString),
          thirdCountryOfOrigin = Some(countryModelGB.code),
          otherInformation = Some("more info"),
          wineOperations = Some(testWineOperations.map(_.code).sorted)
        ))
      }
    }
  }

  "wineProductCategory" - {
    "must return EuWineWithoutPdoOrPgi" - {
      "when from the EU, with GI" in {
        ItemGeographicalIndicationType.values.filterNot(_ == ItemGeographicalIndicationType.NoGeographicalIndication).foreach {
          geographicalIndicationChoice =>
            implicit val dr: DataRequest[_] = dataRequest(
              FakeRequest(),
              userAnswers(geographicalIndicationChoice = geographicalIndicationChoice)
            )

            WineProductModel.wineProductCategory(testIndex1) mustBe ItemWineCategory.EuWineWithPdoOrPgiOrGi
        }
      }
    }
    "must return EuWineWithoutPdoOrPgi" - {
      "when from the EU, no GI, and not varietal" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          userAnswers(geographicalIndicationChoice = ItemGeographicalIndicationType.NoGeographicalIndication)
        )

        WineProductModel.wineProductCategory(testIndex1) mustBe ItemWineCategory.EuWineWithoutPdoOrPgi
      }
    }
    "must return EuVarietalWineWithoutPdoOrPgi" - {
      "when from the EU, no GI, and varietal" in {
        ItemWineCategory.varietalWines.foreach {
          commodityCode =>
            implicit val dr: DataRequest[_] = dataRequest(
              FakeRequest(),
              userAnswers(cnCode = commodityCode, geographicalIndicationChoice = ItemGeographicalIndicationType.NoGeographicalIndication)
            )

            WineProductModel.wineProductCategory(testIndex1) mustBe ItemWineCategory.EuVarietalWineWithoutPdoOrPgi
        }
      }
    }
    "must return ImportedWine" - {
      "when not from the EU" - {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          userAnswers(importedChoice = false)
        )

        WineProductModel.wineProductCategory(testIndex1) mustBe ItemWineCategory.ImportedWine
      }
    }
  }
}
