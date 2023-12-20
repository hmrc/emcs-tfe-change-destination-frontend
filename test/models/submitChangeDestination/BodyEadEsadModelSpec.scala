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
import fixtures.messages.sections.items.ItemSmallIndependentProducerMessages
import models.GoodsType
import models.requests.DataRequest
import models.response.MissingMandatoryPage
import models.response.referenceData.{ItemPackaging, WineOperations}
import models.sections.items._
import pages.sections.items._
import play.api.i18n.Messages
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class BodyEadEsadModelSpec extends SpecBase with ItemFixtures {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val messagesForLanguage = ItemSmallIndependentProducerMessages.English
  implicit val msgs: Messages = messages(Seq(messagesForLanguage.lang))

  "apply" - {
    "must throw an error" - {
      "when no items" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
        )

        val result = intercept[MissingMandatoryPage](BodyEadEsadModel.apply)

        result.message mustBe "ItemsSection should contain at least one item"
      }
    }

    "must return a BodyEadEsadModel" - {
      "when one bulk item" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
            .set(ItemQuantityPage(testIndex1), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex1), true)
            .set(ItemGeographicalIndicationPage(testIndex1), "talkin' 'bout my deeeeeesignation")
            .set(ItemProducerSizePage(testIndex1), BigInt(4))
            .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex1), "beans")
            .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
            .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.head)
            .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("seal type", Some("seal info")))
            .set(ItemImportedWineFromEuChoicePage(testIndex1), false)
            .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
            .set(ItemWineOriginPage(testIndex1), countryModelGB)
            .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
            .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc")))
        )

        BodyEadEsadModel.apply mustBe Seq(BodyEadEsadModel(
          bodyRecordUniqueReference = 1,
          exciseProductCode = testEpcWine,
          cnCode = testCnCodeWine,
          quantity = BigDecimal(1),
          grossMass = BigDecimal(3),
          netMass = BigDecimal(2),
          alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
          degreePlato = Some(4.56),
          fiscalMark = Some("fiscal marks"),
          fiscalMarkUsedFlag = Some(true),
          designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
          sizeOfProducer = Some(BigInt(4)),
          density = Some(BigDecimal(7.89)),
          commercialDescription = Some("beans"),
          brandNameOfProducts = Some("name"),
          maturationPeriodOrAgeOfProducts = Some("really old"),
          packages = Seq(
            PackageModel(
              kindOfPackages = bulkPackagingTypes.head.packagingType.toString,
              numberOfPackages = None,
              shippingMarks = None,
              commercialSealIdentification = Some("seal type"),
              sealInformation = Some("seal info")
            )
          ),
          wineProduct = Some(
            WineProductModel(
              wineProductCategory = ItemWineCategory.ImportedWine.toString,
              wineGrowingZoneCode = Some(ItemWineGrowingZone.CII.toString),
              thirdCountryOfOrigin = Some(countryModelGB.code),
              otherInformation = Some("more wine info"),
              wineOperations = Some(Seq("op code"))
            )
          )
        ))
      }
      "when one non bulk item" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
            .set(ItemQuantityPage(testIndex1), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex1), true)
            .set(ItemGeographicalIndicationPage(testIndex1), "talkin' 'bout my deeeeeesignation")
            .set(ItemProducerSizePage(testIndex1), BigInt(4))
            .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex1), "beans")
            .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex1), false)
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BA", "Barrel"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "3")
            .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "marks")
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("seal", None))
            .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), ItemPackaging("JR", "Jar"))
            .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "1")
            .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex2), ItemPackagingSealTypeModel("seal 2", Some("seal info")))
            .set(ItemImportedWineFromEuChoicePage(testIndex1), false)
            .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
            .set(ItemWineOriginPage(testIndex1), countryModelGB)
            .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
            .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc")))
        )

        BodyEadEsadModel.apply mustBe Seq(BodyEadEsadModel(
          bodyRecordUniqueReference = 1,
          exciseProductCode = testEpcWine,
          cnCode = testCnCodeWine,
          quantity = BigDecimal(1),
          grossMass = BigDecimal(3),
          netMass = BigDecimal(2),
          alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
          degreePlato = Some(4.56),
          fiscalMark = Some("fiscal marks"),
          fiscalMarkUsedFlag = Some(true),
          designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
          sizeOfProducer = Some(BigInt(4)),
          density = Some(BigDecimal(7.89)),
          commercialDescription = Some("beans"),
          brandNameOfProducts = Some("name"),
          maturationPeriodOrAgeOfProducts = Some("really old"),
          packages = Seq(
            PackageModel(
              kindOfPackages = "BA",
              numberOfPackages = Some(3),
              shippingMarks = Some("marks"),
              commercialSealIdentification = Some("seal"),
              sealInformation = None
            ),
            PackageModel(
              kindOfPackages = "JR",
              numberOfPackages = Some(1),
              shippingMarks = None,
              commercialSealIdentification = Some("seal 2"),
              sealInformation = Some("seal info")
            )
          ),
          wineProduct = Some(
            WineProductModel(
              wineProductCategory = ItemWineCategory.ImportedWine.toString,
              wineGrowingZoneCode = Some(ItemWineGrowingZone.CII.toString),
              thirdCountryOfOrigin = Some(countryModelGB.code),
              otherInformation = Some("more wine info"),
              wineOperations = Some(Seq("op code"))
            )
          )
        ))
      }
      "when a mix of bulk and non bulk items" in {
        implicit val dr: DataRequest[_] = dataRequest(
          FakeRequest(),
          emptyUserAnswers
            // index 1
            .set(ItemExciseProductCodePage(testIndex1), testEpcWine)
            .set(ItemCommodityCodePage(testIndex1), testCnCodeWine)
            .set(ItemQuantityPage(testIndex1), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex1), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex1), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex1), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex1), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex1), true)
            .set(ItemGeographicalIndicationPage(testIndex1), "talkin' 'bout my deeeeeesignation")
            .set(ItemProducerSizePage(testIndex1), BigInt(4))
            .set(ItemDensityPage(testIndex1), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex1), "beans")
            .set(ItemBrandNamePage(testIndex1), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex1), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex1), true)
            .set(ItemBulkPackagingSelectPage(testIndex1), bulkPackagingTypes.last)
            .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("seal type", Some("seal info")))
            .set(ItemImportedWineFromEuChoicePage(testIndex1), false)
            .set(ItemWineGrowingZonePage(testIndex1), ItemWineGrowingZone.CII)
            .set(ItemWineOriginPage(testIndex1), countryModelGB)
            .set(ItemWineMoreInformationPage(testIndex1), Some("more wine info"))
            .set(ItemWineOperationsChoicePage(testIndex1), Seq(WineOperations("op code", "choice desc")))
            // index 2
            .set(ItemExciseProductCodePage(testIndex2), testEpcTobacco)
            .set(ItemCommodityCodePage(testIndex2), testCnCodeTobacco)
            .set(ItemQuantityPage(testIndex2), BigDecimal(1))
            .set(ItemNetGrossMassPage(testIndex2), ItemNetGrossMassModel(netMass = BigDecimal(2), grossMass = BigDecimal(3)))
            .set(ItemAlcoholStrengthPage(testIndex2), BigDecimal(1.23))
            .set(ItemDegreesPlatoPage(testIndex2), ItemDegreesPlatoModel(hasDegreesPlato = true, Some(4.56)))
            .set(ItemFiscalMarksPage(testIndex2), "fiscal marks")
            .set(ItemFiscalMarksChoicePage(testIndex2), true)
            .set(ItemGeographicalIndicationPage(testIndex2), "talkin' 'bout my deeeeeesignation")
            .set(ItemProducerSizePage(testIndex2), BigInt(4))
            .set(ItemDensityPage(testIndex2), BigDecimal(7.89))
            .set(ItemCommercialDescriptionPage(testIndex2), "beans")
            .set(ItemBrandNamePage(testIndex2), ItemBrandNameModel(hasBrandName = true, Some("name")))
            .set(ItemMaturationPeriodAgePage(testIndex2), ItemMaturationPeriodAgeModel(hasMaturationPeriodAge = true, Some("really old")))
            .set(ItemBulkPackagingChoicePage(testIndex2), false)
            .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex1), ItemPackaging("BA", "Barrel"))
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex1), "3")
            .set(ItemPackagingShippingMarksPage(testIndex2, testPackagingIndex1), "marks")
            .set(ItemPackagingSealTypePage(testIndex2, testPackagingIndex1), ItemPackagingSealTypeModel("seal", None))
            .set(ItemSelectPackagingPage(testIndex2, testPackagingIndex2), ItemPackaging("JR", "Jar"))
            .set(ItemPackagingQuantityPage(testIndex2, testPackagingIndex2), "1")
            .set(ItemPackagingSealTypePage(testIndex2, testPackagingIndex2), ItemPackagingSealTypeModel("seal 2", Some("seal info")))
            .set(ItemImportedWineFromEuChoicePage(testIndex2), false)
        )

        BodyEadEsadModel.apply mustBe Seq(
          BodyEadEsadModel(
            bodyRecordUniqueReference = 1,
            exciseProductCode = testEpcWine,
            cnCode = testCnCodeWine,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = bulkPackagingTypes.last.packagingType.toString,
                numberOfPackages = None,
                shippingMarks = None,
                commercialSealIdentification = Some("seal type"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = Some(
              WineProductModel(
                wineProductCategory = ItemWineCategory.ImportedWine.toString,
                wineGrowingZoneCode = Some(ItemWineGrowingZone.CII.toString),
                thirdCountryOfOrigin = Some(countryModelGB.code),
                otherInformation = Some("more wine info"),
                wineOperations = Some(Seq("op code"))
              )
            )
          ),
          BodyEadEsadModel(
            bodyRecordUniqueReference = 2,
            exciseProductCode = testEpcTobacco,
            cnCode = testCnCodeTobacco,
            quantity = BigDecimal(1),
            grossMass = BigDecimal(3),
            netMass = BigDecimal(2),
            alcoholicStrengthByVolumeInPercentage = Some(BigDecimal(1.23)),
            degreePlato = Some(4.56),
            fiscalMark = Some("fiscal marks"),
            fiscalMarkUsedFlag = Some(true),
            designationOfOrigin = Some("talkin' 'bout my deeeeeesignation"),
            sizeOfProducer = Some(BigInt(4)),
            density = Some(BigDecimal(7.89)),
            commercialDescription = Some("beans"),
            brandNameOfProducts = Some("name"),
            maturationPeriodOrAgeOfProducts = Some("really old"),
            packages = Seq(
              PackageModel(
                kindOfPackages = "BA",
                numberOfPackages = Some(3),
                shippingMarks = Some("marks"),
                commercialSealIdentification = Some("seal"),
                sealInformation = None
              ),
              PackageModel(
                kindOfPackages = "JR",
                numberOfPackages = Some(1),
                shippingMarks = None,
                commercialSealIdentification = Some("seal 2"),
                sealInformation = Some("seal info")
              )
            ),
            wineProduct = None
          )
        )
      }
    }
  }

  "designationOfOrigin" - {
    "when ItemGeographicalIndicationPage and ItemSmallIndependentProducerPage have answers" - {
      "must concatenate those answers" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(ItemGeographicalIndicationPage(testIndex1), "indication")
            .set(ItemSmallIndependentProducerPage(testIndex1), true)
        )

        BodyEadEsadModel.designationOfOrigin(testIndex1, testEpcWine, "cnCode") mustBe Some(s"${messagesForLanguage.yesWine} indication")
      }
    }
    "when ItemGeographicalIndicationPage has an answer" - {
      "must return that answer" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
            .set(ItemGeographicalIndicationPage(testIndex1), "indication")
        )

        BodyEadEsadModel.designationOfOrigin(testIndex1, testEpcWine, "cnCode") mustBe Some("indication")
      }
    }

    "when ItemSmallIndependentProducerPage has an answer" - {
      s"must return Yes text" in {
        Seq(
          GoodsType.Beer -> messagesForLanguage.yesBeer,
          GoodsType.Spirits -> messagesForLanguage.yesSpirits,
          GoodsType.Wine -> messagesForLanguage.yesWine,
          GoodsType.Energy -> messagesForLanguage.yesOther,
          GoodsType.Tobacco -> messagesForLanguage.yesOther,
          GoodsType.Intermediate -> messagesForLanguage.yesIntermediate
        ).foreach {
          case (goodsType, yesText) =>
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(ItemSmallIndependentProducerPage(testIndex1), true)
            )

            BodyEadEsadModel.designationOfOrigin(testIndex1, s"${goodsType.code}123", "cnCode") mustBe Some(yesText)
        }
      }
      s"when CN Code means that goodsType is [${GoodsType.Fermented(GoodsType.fermentedBeverages.head).getClass.getName.stripSuffix("$")}]" +
        s" must return [${messagesForLanguage.yesFermented}]" in {
        GoodsType.fermentedBeverages.map {
          cnCode =>

            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers
                .set(ItemSmallIndependentProducerPage(testIndex1), true)
            )

            BodyEadEsadModel.designationOfOrigin(testIndex1, s"W123", cnCode) mustBe Some(messagesForLanguage.yesFermented)
        }
      }
    }

    "when neither ItemGeographicalIndicationPage nor ItemSmallIndependentProducerPage have an answer" - {
      "must return None" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
        )

        BodyEadEsadModel.designationOfOrigin(testIndex1, testEpcWine, testCnCodeWine) mustBe None
      }
    }
  }

  "yesAnswer" - {
    "when XI trader" - {
      Seq(
        GoodsType.Beer -> messagesForLanguage.yesBeer,
        GoodsType.Spirits -> messagesForLanguage.yesSpirits,
        GoodsType.Wine -> messagesForLanguage.yesWine,
        GoodsType.Energy -> messagesForLanguage.yesOther,
        GoodsType.Tobacco -> messagesForLanguage.yesOther,
        GoodsType.Intermediate -> messagesForLanguage.yesIntermediate
      ).foreach {
        case (goodsType, yesText) =>
          s"when goodsType is [$goodsType] must return [$yesText]" in {
            Seq("XIRC123", "XIWK123").foreach {
              ern =>
                implicit val dr: DataRequest[_] = dataRequest(
                  fakeRequest,
                  emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true),
                  ern = ern
                )

                BodyEadEsadModel.yesAnswer(goodsType) mustBe yesText
            }
          }
      }
      s"when goodsType is [${GoodsType.Fermented(GoodsType.fermentedBeverages.head).getClass.getName.stripSuffix("$")}]" +
        s" must return [${messagesForLanguage.yesFermented}]" in {
        Seq("XIRC123", "XIWK123").foreach {
          ern =>
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true),
              ern = ern
            )

            BodyEadEsadModel.yesAnswer(GoodsType.Fermented(GoodsType.fermentedBeverages.head)) mustBe messagesForLanguage.yesFermented
        }
      }
    }
    "when GB trader" - {
      Seq(
        GoodsType.Beer -> messagesForLanguage.yesOther,
        GoodsType.Spirits -> messagesForLanguage.yesOther,
        GoodsType.Wine -> messagesForLanguage.yesOther,
        GoodsType.Energy -> messagesForLanguage.yesOther,
        GoodsType.Tobacco -> messagesForLanguage.yesOther,
        GoodsType.Intermediate -> messagesForLanguage.yesOther
      ).foreach {
        case (goodsType, yesText) =>
          s"when goodsType is [$goodsType] must return [$yesText]" in {
            Seq("GBRC123", "GBWK123").foreach {
              ern =>
                implicit val dr: DataRequest[_] = dataRequest(
                  fakeRequest,
                  emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true),
                  ern = ern
                )

                BodyEadEsadModel.yesAnswer(goodsType) mustBe yesText
            }
          }
      }
      s"when goodsType is [${GoodsType.Fermented(GoodsType.fermentedBeverages.head).getClass.getName.stripSuffix("$")}]" +
        s" must return [${messagesForLanguage.yesOther}]" in {
        Seq("GBRC123", "GBWK123").foreach {
          ern =>
            implicit val dr: DataRequest[_] = dataRequest(
              fakeRequest,
              emptyUserAnswers.set(ItemSmallIndependentProducerPage(testIndex1), true),
              ern = ern
            )

            BodyEadEsadModel.yesAnswer(GoodsType.Fermented(GoodsType.fermentedBeverages.head)) mustBe messagesForLanguage.yesOther
        }
      }
    }
  }
}
