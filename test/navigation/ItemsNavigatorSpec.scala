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

package navigation

import base.SpecBase
import controllers.sections.items.{routes => itemsRoutes}
import fixtures.ItemFixtures
import models.response.referenceData.{BulkPackagingType, ItemPackaging}
import models.sections.items.ItemBulkPackagingCode.BulkLiquid
import models.sections.items.ItemGeographicalIndicationType.{NoGeographicalIndication, ProtectedGeographicalIndication}
import models.sections.items._
import models.{CheckMode, GoodsType, NormalMode, ReviewMode}
import pages.Page
import pages.sections.items._

class ItemsNavigatorSpec extends SpecBase with ItemFixtures {
  val navigator = new ItemsNavigator

  "ItemsNavigator" - {
    "in Normal mode" - {
      "must go from a page that doesn't exist in the route map to ItemAddToList" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, NormalMode, emptyUserAnswers) mustBe
          itemsRoutes.ItemsAddToListController.onPageLoad(testErn, testDraftId)
      }

      "must go from the Excise Product Code page" - {
        "to CAM-ITM38 page" - {
          "when the EPC has multiple commodity codes" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              NormalMode,
              emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
            ) mustBe controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items index page" - {
          "when there is no answer" in {
            navigator.nextPage(ItemExciseProductCodePage(testIndex1),
              NormalMode, emptyUserAnswers) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the Item Commodity Code page" - {
        "to the Confirm Commodity Code page" in {
          navigator.nextPage(
            ItemCommodityCodePage(testIndex1),
            NormalMode,
            emptyUserAnswers
          ) mustBe controllers.sections.items.routes.ItemConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the Confirm Commodity Code page" - {
        "to the Item Brand Name page" in {
          navigator.nextPage(
            ItemConfirmCommodityCodePage(testIndex1),
            NormalMode,
            emptyUserAnswers
          ) mustBe controllers.sections.items.routes.ItemBrandNameController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Brand Name page" - {

        "to the Item Commercial Description Page" in {
          val userAnswers = emptyUserAnswers.set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("brand")))

          navigator.nextPage(ItemBrandNamePage(testIndex1), NormalMode, userAnswers) mustBe
            itemsRoutes.ItemCommercialDescriptionController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Commercial Description page" - {
        "when GoodsType is Beer" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)

            navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Spirits" - {
          "to the  Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeS200.code)

            navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Wine" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)

            navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is Intermediate" - {
          "to the Alcohol Strength Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeI200.code)

            navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemAlcoholStrengthController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
        "when GoodsType is one type of Tobacco " - {

          "to the Item Fiscal Marks Choice Page" in {
            val userAnswers = emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)

            navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemFiscalMarksChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Energy " - {

          Seq(testExciseProductCodeE470, testExciseProductCodeE500, testExciseProductCodeE600, testExciseProductCodeE930).foreach(epc => {
            s"when the EPC is $epc" - {
              "to the Item Quantity Page" in {
                val userAnswers = emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), epc.code)

                navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
                  itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
              }
            }
          })

          s"when the EPC is anything else" - {
            "to the Item Density Page" in {
              val userAnswers = emptyUserAnswers
                .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeE200.code)

              navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), NormalMode, userAnswers) mustBe
                itemsRoutes.ItemDensityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
            }
          }
        }
      }

      "must go from the Item Density Page" - {
        "to the Item Quantity Page" in {
          val userAnswers = emptyUserAnswers
            .set(ItemDensityPage(testIndex1), BigDecimal("1234.5"))

          navigator.nextPage(ItemDensityPage(testIndex1), NormalMode, userAnswers) mustBe
            itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Alcohol Strength page" - {

        "when GoodsType is Beer and XIWK or XIRC" - {

          "to the Item Degrees Plato Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemDegreesPlatoController.onPageLoad(testNorthernIrelandErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV < 8.5" - {

          "to the Small Independent Producer Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.arc, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Beer and GBWK or GBRC and ABV >= 8.5" - {

          "to the Item Quantity page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeB000.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemQuantityController.onPageLoad(testGreatBritainErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "when GoodsType is Spirits" - {

          "to the Item Maturation Period Age Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeS100.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemMaturationPeriodAgeController.onPageLoad(userAnswers.ern, userAnswers.arc, testIndex1, NormalMode)
          }
        }

        "when GoodsType is anything else (e.g. Wine)" - {

          "to the Geographical Indication Choice Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testGreatBritainErn)
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW100.code)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemGeographicalIndicationChoiceController.onPageLoad(testGreatBritainErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the Item Small Independent Producer page" - {

        "when the answer is 'Yes'" - {

          "to the Under Construction Page" in {

            val userAnswers = emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true)

            navigator.nextPage(ItemSmallIndependentProducerPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemProducerSizeController.onPageLoad(testErn, testDraftId, 0, NormalMode)
          }
        }

        "when the answer is 'No'" - {

          "to the Item Quantity Page" in {

            val userAnswers = emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), false)

            navigator.nextPage(ItemSmallIndependentProducerPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the ItemProducerSize page" - {

        "to the quantity page" in {

          navigator.nextPage(ItemProducerSizePage(0), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Quantity page" - {

        "to the Item Net Gross Mass Page" in {
          navigator.nextPage(ItemQuantityPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemNetGrossMassController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Net Gross Mass Page" - {

        "to the bulk-packaging-choice page" in {
          navigator.nextPage(ItemNetGrossMassPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemBulkPackagingChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Degrees Plato page" - {

        "when Alcohol Strength is < 8.5 abv" - {

          "to the Small Independent Producer Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.4))

            navigator.nextPage(ItemDegreesPlatoPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(userAnswers.ern, userAnswers.arc, testIndex1, NormalMode)
          }
        }

        "when Alcohol Strength is >= 8.5 abv" - {

          "to the Item Quantity Page" in {

            val userAnswers = emptyUserAnswers.copy(ern = testNorthernIrelandErn)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))

            navigator.nextPage(ItemDegreesPlatoPage(testIndex1), NormalMode, userAnswers) mustBe
              itemsRoutes.ItemQuantityController.onPageLoad(testNorthernIrelandErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the Item Maturation Period Age page" - {

        "to the Geographical Indication Choice Page" in {

          navigator.nextPage(ItemMaturationPeriodAgePage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemGeographicalIndicationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the Item Geographical Indication Choice page" - {

        "to the Geographical Indication Page" - {
          "when the answer is Yes (any option)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), ProtectedGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            ) mustBe itemsRoutes.ItemGeographicalIndicationController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Small Independent Producer Page" - {
          "when the answer is No (and the ABV is < 8.5)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            ) mustBe itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity Page" - {

          "when the answer is No (and the ABV is >= 8.5)" in {

            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.501))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items index page" - {
          "when the goods type is not spirits / wine / fermented / intermediate (e.g., Tobacco) and answered no" in {
            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemGeographicalIndicationChoicePage(testIndex1), NoGeographicalIndication)
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.501))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }

          "when there is no answers" in {
            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), NormalMode, emptyUserAnswers
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the Item Geographical Indication page" - {

        "to the Small Independent Producer Page" - {

          "when the alcoholic strength is < 8.5" in {

            navigator.nextPage(ItemGeographicalIndicationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.499))
            ) mustBe itemsRoutes.ItemSmallIndependentProducerController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity Page" - {

          "when the alcoholic strength is >= 8.5" in {

            navigator.nextPage(ItemGeographicalIndicationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(8.5))
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items index page" - {

          "when there is no answers" in {
            navigator.nextPage(ItemGeographicalIndicationPage(testIndex1), NormalMode, emptyUserAnswers
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemFiscalMarksChoicePage" - {
        "to the Fiscal Marks page" - {

          "when the user answers yes" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
              .set(ItemFiscalMarksChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemFiscalMarksController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Quantity page" - {

          "when the user answers no" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
              .set(ItemFiscalMarksChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items Index page" - {
          "when there is no answer for ItemFiscalMarksChoicePage" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeT200.code)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }

      }

      "must go from the ItemFiscalMarksPage" - {

        "to the Quantity page" in {
          navigator.nextPage(ItemFiscalMarksPage(testIndex1), NormalMode, emptyUserAnswers
            .set(ItemFiscalMarksPage(testIndex1), "some fiscal mark")
          ) mustBe itemsRoutes.ItemQuantityController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the ItemBulkPackagingChoicePage" - {
        "to the Packaging Bulk Select page" - {

          "when the user answers yes" in {
            navigator.nextPage(ItemBulkPackagingChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemBulkPackagingSelectController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Imported Wine Choice page" - {

          "when the user answers no and EPC is wine" in {
            navigator.nextPage(ItemBulkPackagingChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemImportedWineChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Packaging Select (Items Packaging Index) page" - {
          "when the user answers no and EPC is not wine" in {
            GoodsType.values.filterNot(_ == GoodsType.Wine).foreach(
              goodsType =>
                navigator.nextPage(ItemBulkPackagingChoicePage(testIndex1), NormalMode, emptyUserAnswers
                  .set(ItemExciseProductCodePage(testIndex1), s"${goodsType.code}200")
                  .set(ItemBulkPackagingChoicePage(testIndex1), false)
                ) mustBe itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1)
            )
          }
        }

      }

      "must go from the ItemBulkPackagingSelectPage" - {

        "to the Wine Operations Choice page" - {
          "when the wine quantity is equal or over 60 litres" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemQuantityPage(testIndex1), BigDecimal(60))
            ) mustBe itemsRoutes.ItemWineOperationsChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Imported Wine Choice page" - {
          "when the wine quantity is under 60 litres" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemQuantityPage(testIndex1), BigDecimal(59.99))
            ) mustBe itemsRoutes.ItemImportedWineChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Packaging Seal Choice (Bulk packaging) page" - {
          "when goods type is not Wine" in {
            GoodsType.values.filterNot(_ == GoodsType.Wine).foreach { goodsType =>
              navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
                .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
                .set(ItemExciseProductCodePage(testIndex1), s"${goodsType.code}300")
              ) mustBe itemsRoutes.ItemBulkPackagingSealChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
            }
          }
        }

        "to the Items Index page" - {
          "when there is no answer for ItemExciseProductCodePage" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }

          "when there is no answer for ItemQuantityPage when the EPC is Wine" in {
            navigator.nextPage(ItemBulkPackagingSelectPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(BulkLiquid, "Bulk, liquid"))
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemSelectPackagingPage" - {
        "to the Packaging Quantity page" in {
          navigator.nextPage(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("AE", "Aerosol"))
          ) mustBe itemsRoutes.ItemPackagingQuantityController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)
        }
      }

      "must go from the ItemPackagingQuantityPage" - {

        "to the Packaging Product Type page" in {

          navigator.nextPage(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
            .set(ItemPackagingQuantityPage(0, 0), "1")
          ) mustBe itemsRoutes.ItemPackagingProductTypeController.onPageLoad(testErn, testDraftId, 0, 0, NormalMode)
        }
      }

      "must go from the ItemPackagingProductTypePage" - {

        "to the Packaging Seal Choice (Item packaging) page when user answered Yes" in {

          navigator.nextPage(ItemPackagingProductTypePage(0, 0), NormalMode, emptyUserAnswers
            .set(ItemPackagingProductTypePage(0, 0), true)
          ) mustBe itemsRoutes.ItemPackagingSealChoiceController.onPageLoad(testErn, testDraftId, 0, 0, NormalMode)
        }

        "to the Shipping Marks page when user answered No" in {

          navigator.nextPage(ItemPackagingProductTypePage(0, 0), NormalMode, emptyUserAnswers
            .set(ItemPackagingProductTypePage(0, 0), false)
          ) mustBe itemsRoutes.ItemPackagingShippingMarksController.onPageLoad(testErn, testDraftId, 0, 0, NormalMode)
        }

        "to the PackagingIndex route when neither answer has been selected" in {

          navigator.nextPage(ItemPackagingProductTypePage(0, 0), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, 0)
        }
      }

      "must go from the ItemWineGrowingZonePage" - {

        "to the Wine More Information Choice page" in {

          navigator.nextPage(ItemWineGrowingZonePage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the ItemWineMoreInformationChoicePage" - {
        "to the Wine More Information page" - {
          "when the user answers yes" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemWineMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Select Packaging (Items Packaging Index) page" - {
          "when the user answers no and the item is not classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), false)
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }

        "to the Packaging Seal Choice (Bulk packaging) page" - {
          "when the user answers no and the item is classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), false)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemBulkPackagingSealChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Items Index page" - {
          "when the user has no answer for ItemWineMoreInformationChoicePage" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }

          "when the user has no answer for ItemBulkPackagingChoicePage (when clicking no)" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), testExciseProductCodeW200.code)
              .set(ItemWineMoreInformationChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemWineMoreInformationPage" - {

        "to the Select Packaging (Items Packaging Index) page" - {

          "when the user answers no and the item is not classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }

        "to the Packaging Seal Choice (bulk packaging) page" - {

          "when the user answers no and the item is classed as bulk" in {
            navigator.nextPage(ItemWineMoreInformationPage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemBulkPackagingSealChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)

          }
        }

        "to the Items Index page" - {

          "when the user has no answer for ItemBulkPackagingChoicePage (when clicking no)" in {
            navigator.nextPage(ItemWineMoreInformationChoicePage(testIndex1), NormalMode, emptyUserAnswers) mustBe
              itemsRoutes.ItemsIndexController.onPageLoad(testErn, testDraftId)
          }
        }
      }

      "must go from the ItemPackagingShippingMarksPage" - {
        "to the Packaging Seal Choice (Item packaging) page" in {
          navigator.nextPage(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "answer")
          ) mustBe itemsRoutes.ItemPackagingSealChoiceController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)
        }
      }

      "must go from the ItemBulkPackagingSealChoicePage" - {
        "to the Packaging Seal Type (bulk packaging) page" - {
          "when the user answers 'yes'" in {
            navigator.nextPage(ItemBulkPackagingSealChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSealChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemBulkPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Item CYA page" - {
          "when the user answers 'no'" in {
            navigator.nextPage(ItemBulkPackagingSealChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemBulkPackagingSealChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from the ItemPackagingSealChoicePage" - {
        "to the Packaging Seal Type (item packaging) page" - {
          "when the user answers 'yes'" in {
            navigator.nextPage(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
            ) mustBe itemsRoutes.ItemPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, NormalMode)
          }
        }

        "to the Item Packaging CYA page" - {
          "when the user answers 'no'" in {
            navigator.nextPage(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
              .set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
            ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from the ItemImportedWineFromEuChoicePage" - {

        "to the Wine Growing Zone page" - {

          "when the user answers yes, moving in bulk and more than 60 litres" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), true)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
              .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            ) mustBe itemsRoutes.ItemWineGrowingZoneController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Wine More Information Choice page" - {

          "when the user answers yes, moving in Bulk but <= 60 litres" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), true)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
              .set(ItemQuantityPage(testIndex1), BigDecimal(60))
            ) mustBe itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Wine More Information Choice page" - {

          "when the user answers yes, NOT moving in Bulk > 60 litres" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), true)
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
              .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            ) mustBe itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to the Wine Origin page" - {

          "when the user answers no" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), NormalMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemWineOriginController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }
      }

      "must go from the ItemWineOperationsChoicePage" - {
        "to the Imported Wine Choice page" in {
          navigator.nextPage(ItemWineOperationsChoicePage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemImportedWineChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the ItemPackagingSealTypePage" - {
        "to the Item Packaging CYA page" in {
          navigator.nextPage(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), NormalMode, emptyUserAnswers
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("test", Some("other")))
          ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the ItemBulkPackagingSealTypePage" - {
        "to the Item CYA page" in {
          navigator.nextPage(ItemBulkPackagingSealTypePage(testIndex1), NormalMode, emptyUserAnswers
            .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("test", None))
          ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the ItemWineOriginPage" - {
        "to the Wine More Information Choice page" in {
          navigator.nextPage(ItemWineOriginPage(testIndex1), NormalMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
        }
      }

      "must go from the ItemsPackagingAddToList page" - {

        "to Item CYA page" - {

          "when answer is `No` (not adding more packages)" in {
            navigator.nextPage(
              ItemsPackagingAddToListPage(testIndex1), NormalMode, emptyUserAnswers
                .set(ItemsPackagingAddToListPage(testIndex1), ItemsPackagingAddToList.No)
            ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }

          "when answer is `More later`" in {
            navigator.nextPage(
              ItemsPackagingAddToListPage(testIndex1), NormalMode, emptyUserAnswers
                .set(ItemsPackagingAddToListPage(testIndex1), ItemsPackagingAddToList.MoreLater)
            ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }

        "to the Select Packaging type page at the next idx" - {

          "when answer is `Yes` (adding another package)" in {
            navigator.nextPage(
              ItemsPackagingAddToListPage(testIndex1), NormalMode, emptyUserAnswers
                .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), testPackageBag)
                .set(ItemsPackagingAddToListPage(testIndex1), ItemsPackagingAddToList.Yes)
            ) mustBe itemsRoutes.ItemSelectPackagingController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex2, NormalMode)
          }
        }
      }

      "must go from the ItemCheckAnswers page" - {

        "to Item AddToList page" in {
          navigator.nextPage(
            ItemCheckAnswersPage(testIndex1), NormalMode, emptyUserAnswers
          ) mustBe itemsRoutes.ItemsAddToListController.onPageLoad(testErn, testDraftId)
        }
      }

      "must go from the ItemsAddToList page" - {

        "to Draft Movement page" - {

          "when answer is `No` (not adding more packages)" in {
            navigator.nextPage(
              ItemsAddToListPage, NormalMode, emptyUserAnswers.set(ItemsAddToListPage, ItemsAddToList.No)
            ) mustBe controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId)
          }

          "when answer is `More later`" in {
            navigator.nextPage(
              ItemsAddToListPage, NormalMode, emptyUserAnswers.set(ItemsAddToListPage, ItemsAddToList.MoreLater)
            ) mustBe controllers.routes.DraftMovementController.onPageLoad(testErn, testDraftId)
          }
        }

        "to the Excise Product Code page at the next idx" - {

          "when answer is `Yes` (adding another package)" in {
            navigator.nextPage(
              ItemsAddToListPage, NormalMode, singleCompletedWineItem.set(ItemsAddToListPage, ItemsAddToList.Yes)
            ) mustBe itemsRoutes.ItemExciseProductCodeController.onPageLoad(testErn, testDraftId, testIndex2, NormalMode)
          }
        }
      }
    }

    "in Check mode" - {
      "must go from ItemExciseProductCodePage" - {
        "to ItemCommodityCode page" - {
          "when ItemCommodityCodePage has no answer (EPC has been changed)" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              CheckMode,
              emptyUserAnswers) mustBe
              controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              CheckMode,
              emptyUserAnswers.set(ItemExciseProductCodePage(testIndex1), testCommodityCodeWine.exciseProductCode)) mustBe
              controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
          }
        }

        "to CYA page" - {
          "when ItemCommodityCodePage has an answer (EPC has not changed)" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              CheckMode,
              emptyUserAnswers
                .set(ItemExciseProductCodePage(testIndex1), testCommodityCodeWine.exciseProductCode)
                .set(ItemCommodityCodePage(testIndex1), testCommodityCodeWine.cnCode)
            ) mustBe
              itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from ItemCommodityCodePage" - {
        "to CYA page" in {
          navigator.nextPage(ItemCommodityCodePage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemBrandNamePage" - {
        "to CYA page" in {
          navigator.nextPage(ItemBrandNamePage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemCommercialDescriptionPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemCommercialDescriptionPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemAlcoholStrengthPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemAlcoholStrengthPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemDegreesPlatoPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemDegreesPlatoPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemMaturationPeriodAgePage" - {
        "to CYA page" in {
          navigator.nextPage(ItemMaturationPeriodAgePage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemDensityPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemDensityPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemFiscalMarksChoicePage" - {
        "when answer is true" - {
          "to ItemFiscalMarks page" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), true)) mustBe
              itemsRoutes.ItemFiscalMarksController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }
        "when answer is false" - {
          "to CYA page" in {
            navigator.nextPage(ItemFiscalMarksChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(ItemFiscalMarksChoicePage(testIndex1), false)) mustBe
              itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from ItemFiscalMarksPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemFiscalMarksPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemGeographicalIndicationChoicePage" - {
        "when answer is true" - {
          "to ItemGeographicalIndication page" in {
            ItemGeographicalIndicationType.values.filterNot(_ == ItemGeographicalIndicationType.NoGeographicalIndication).map {
              itemGeographicalIndicationChoicePageAnswer =>
                navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), CheckMode,
                  emptyUserAnswers
                    .set(ItemGeographicalIndicationChoicePage(testIndex1), itemGeographicalIndicationChoicePageAnswer)
                ) mustBe
                  itemsRoutes.ItemGeographicalIndicationController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
            }
          }
        }
        "when answer is false" - {
          "to CYA page" in {
            navigator.nextPage(ItemGeographicalIndicationChoicePage(testIndex1), CheckMode,
              emptyUserAnswers.set(ItemGeographicalIndicationChoicePage(testIndex1), ItemGeographicalIndicationType.NoGeographicalIndication)
            ) mustBe
              itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from ItemGeographicalIndicationPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemGeographicalIndicationPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemSmallIndependentProducerPage" - {
        "when answer is true" - {
          "to ItemProducerSize page" in {
            navigator.nextPage(ItemSmallIndependentProducerPage(testIndex1), CheckMode,
              emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true)
            ) mustBe
              itemsRoutes.ItemProducerSizeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }
        "when answer is false" - {
          "to CYA page" in {
            navigator.nextPage(ItemSmallIndependentProducerPage(testIndex1), CheckMode,
              emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), false)
            ) mustBe
              itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from ItemProducerSizePage" - {
        "to CYA page" in {
          navigator.nextPage(ItemProducerSizePage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemWineOperationsChoicePage" - {
        "to CYA page" in {
          navigator.nextPage(ItemWineOperationsChoicePage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the ItemImportedWineFromEuChoicePage" - {

        "to the Wine Growing Zone page" - {

          "when the user answers yes, moving in bulk and more than 60 litres" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), CheckMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), true)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
              .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            ) mustBe itemsRoutes.ItemWineGrowingZoneController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }

        "to the Wine More Information Choice page" - {

          "when the user answers yes, moving in Bulk but <= 60 litres" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), CheckMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), true)
              .set(ItemBulkPackagingChoicePage(testIndex1), true)
              .set(ItemQuantityPage(testIndex1), BigDecimal(60))
            ) mustBe itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }

        "to the Wine More Information Choice page" - {

          "when the user answers yes, NOT moving in Bulk > 60 litres" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), CheckMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), true)
              .set(ItemBulkPackagingChoicePage(testIndex1), false)
              .set(ItemQuantityPage(testIndex1), BigDecimal(61))
            ) mustBe itemsRoutes.ItemWineMoreInformationChoiceController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }

        "to the Wine Origin page" - {

          "when the user answers no" in {
            navigator.nextPage(ItemImportedWineFromEuChoicePage(testIndex1), CheckMode, emptyUserAnswers
              .set(ItemExciseProductCodePage(testIndex1), "W200")
              .set(ItemImportedWineFromEuChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemWineOriginController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }
      }

      "must go from ItemWineGrowingZonePage" - {
        "to CYA page" in {
          navigator.nextPage(ItemWineGrowingZonePage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemWineOriginPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemWineOriginPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from ItemWineMoreInformationChoicePage" - {
        "to ItemWineMoreInformation page" - {
          "when the answer is 'Yes'" in {
            navigator.nextPage(
              ItemWineMoreInformationChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(ItemWineMoreInformationChoicePage(testIndex1), true)
            ) mustBe itemsRoutes.ItemWineMoreInformationController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }

        "to CYA page" - {
          "when the answer is 'No'" in {
            navigator.nextPage(
              ItemWineMoreInformationChoicePage(testIndex1), CheckMode, emptyUserAnswers.set(ItemWineMoreInformationChoicePage(testIndex1), false)
            ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from ItemWineMoreInformationPage" - {
        "to CYA page" in {
          navigator.nextPage(ItemWineMoreInformationPage(testIndex1), CheckMode, emptyUserAnswers) mustBe
            itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the ItemBulkPackagingChoicePage" - {
        "when answer is Yes" - {
          "when ItemBulkPackagingSelectPage has an answer (answer has not changed)" - {
            "to Item CYA" in {
              navigator.nextPage(
                ItemBulkPackagingChoicePage(testIndex1),
                CheckMode,
                emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), true)
                  .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.head)
              ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
            }
          }
          "when ItemBulkPackagingSelectPage has no answer (answer has changed)" - {
            "to ItemBulkPackagingSelect page" in {
              navigator.nextPage(
                ItemBulkPackagingChoicePage(testIndex1),
                CheckMode,
                emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), true)
              ) mustBe itemsRoutes.ItemBulkPackagingSelectController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
            }
          }
        }
        "when answer is No" - {
          "to Item CYA" - {
            "when ItemImportedWineFromEuChoicePage has an answer (answer has not changed)" in {
              navigator.nextPage(
                ItemBulkPackagingChoicePage(testIndex1),
                CheckMode,
                emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), false)
                  .set(ItemImportedWineFromEuChoicePage(testIndex1), true)
              ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
            }
            "when ItemsPackagingSectionItems has an answer (answer has not changed)" in {
              navigator.nextPage(
                ItemBulkPackagingChoicePage(testIndex1),
                CheckMode,
                emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), false)
                  .set(ItemPackagingSealChoicePage(testIndex1, testIndex1), true)
              ) mustBe itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
            }
          }
          "to ItemImportedWineChoice page" - {
            "when Wine (answer has changed)" in {
              navigator.nextPage(
                ItemBulkPackagingChoicePage(testIndex1),
                CheckMode,
                emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), false)
                  .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
              ) mustBe itemsRoutes.ItemImportedWineChoiceController.onPageLoad(testErn, testDraftId, testIndex1, NormalMode)
            }
          }
          "to ItemsPackagingIndex page" - {
            "when not Wine (answer has changed)" in {
              navigator.nextPage(
                ItemBulkPackagingChoicePage(testIndex1),
                CheckMode,
                emptyUserAnswers
                  .set(ItemBulkPackagingChoicePage(testIndex1), false)
                  .set(ItemExciseProductCodePage(testIndex1), testEpcTobacco)
              ) mustBe itemsRoutes.ItemsPackagingIndexController.onPageLoad(testErn, testDraftId, testIndex1)
            }
          }
        }
      }

      "must go from ItemBulkPackagingSealChoicePage" - {
        val page = ItemBulkPackagingSealChoicePage(testIndex1)

        "to ItemBulkPackagingSealType page" - {
          "when the answer is 'Yes'" in {
            navigator.nextPage(page, CheckMode, emptyUserAnswers.set(page, true)) mustBe
              itemsRoutes.ItemBulkPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, CheckMode)
          }
        }

        "to CYA page" - {
          "when the answer is 'No'" in {
            navigator.nextPage(page, CheckMode, emptyUserAnswers.set(page, false)) mustBe
              itemsRoutes.ItemCheckAnswersController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }

      "must go from the ItemSelectPackaging page" - {
        "to Item Packaging CYA page" in {
          navigator.nextPage(
            ItemSelectPackagingPage(testIndex1, testPackagingIndex1), CheckMode, emptyUserAnswers
          ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the ItemPackagingQuantity page" - {
        "to Item Packaging CYA page" in {
          navigator.nextPage(
            ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), CheckMode, emptyUserAnswers
          ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the ItemPackagingProductType page" - {
        "to Item Packaging CYA page" - {
          "when answered 'Yes'" in {
            navigator.nextPage(
              page = ItemPackagingProductTypePage(testIndex1, testPackagingIndex1),
              mode = CheckMode,
              userAnswers = emptyUserAnswers.set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), true)
            ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
        "to Item Shipping Marks page" - {
          "when answered 'No'" in {
            navigator.nextPage(
              page = ItemPackagingProductTypePage(testIndex1, testPackagingIndex1),
              mode = CheckMode,
              userAnswers = emptyUserAnswers.set(ItemPackagingProductTypePage(testIndex1, testPackagingIndex1), false)
            ) mustBe itemsRoutes.ItemPackagingShippingMarksController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, CheckMode)
          }
        }
      }

      "must go from the ItemPackagingShippingMarks page" - {
        "to Item Packaging CYA page" in {
          navigator.nextPage(
            ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), CheckMode, emptyUserAnswers
          ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go from the ItemPackagingSealChoice page" - {
        "to Item Packaging CYA page" - {
          "when answered 'No'" in {
            navigator.nextPage(
              page = ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1),
              mode = CheckMode,
              userAnswers = emptyUserAnswers.set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), false)
            ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
        "to ItemPackagingSealType page" - {
          "when answered 'Yes'" in {
            navigator.nextPage(
              page = ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1),
              mode = CheckMode,
              userAnswers = emptyUserAnswers.set(ItemPackagingSealChoicePage(testIndex1, testPackagingIndex1), true)
            ) mustBe itemsRoutes.ItemPackagingSealTypeController.onPageLoad(testErn, testDraftId, testIndex1, testPackagingIndex1, CheckMode)
          }
        }
      }

      "must go from the ItemPackagingSealType page" - {
        "to Item Packaging CYA page" in {
          navigator.nextPage(
            ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), CheckMode, emptyUserAnswers
          ) mustBe itemsRoutes.ItemsPackagingAddToListController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "when page isn't explicitly specified" - {
        "must go to the ItemAddToList page" in {
          // TODO: update when AddToList page is created
          case object UnknownPage extends Page
          navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) mustBe
            testOnly.controllers.routes.UnderConstructionController.onPageLoad()
        }
      }
    }

    "in Review mode" - {
      "must go from the Item Excise Product Code page" - {
        "to the Item Commodity Code page" - {
          "when ItemCommodityCodePage is empty" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              ReviewMode,
              emptyUserAnswers
            ) mustBe controllers.sections.items.routes.ItemCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1, ReviewMode)
          }
        }
        "to the Item Confirm Commodity Code page" - {
          "when ItemCommodityCodePage is not empty" in {
            navigator.nextPage(
              ItemExciseProductCodePage(testIndex1),
              ReviewMode,
              emptyUserAnswers.set(ItemCommodityCodePage(testIndex1), testCnCodeTobacco)
            ) mustBe controllers.sections.items.routes.ItemConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1)
          }
        }
      }
      "must go from the Item Commodity Code page" - {
        "to the Item Confirm Commodity Code page" in {
          navigator.nextPage(
            ItemCommodityCodePage(testIndex1),
            ReviewMode,
            emptyUserAnswers
          ) mustBe controllers.sections.items.routes.ItemConfirmCommodityCodeController.onPageLoad(testErn, testDraftId, testIndex1)
        }
      }

      "must go to CheckYourAnswers" in {
        case object UnknownPage extends Page
        navigator.nextPage(UnknownPage, ReviewMode, emptyUserAnswers) mustBe
          controllers.routes.CheckYourAnswersController.onPageLoad(testErn, testDraftId)
      }
    }
  }
}
