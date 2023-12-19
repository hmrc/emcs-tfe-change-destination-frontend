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
import models.requests.DataRequest
import models.response.referenceData.{BulkPackagingType, ItemPackaging}
import models.sections.items.{ItemBulkPackagingCode, ItemPackagingSealTypeModel}
import pages.sections.items._
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

class PackageModelSpec extends SpecBase {

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "applyBulkPackaging" - {
    "must return a Seq(PackageModel) with only 1 item" in {
      implicit val dr: DataRequest[_] = dataRequest(
        fakeRequest,
        emptyUserAnswers
          .set(ItemBulkPackagingSealTypePage(testIndex1), ItemPackagingSealTypeModel("seal", Some("seal info")))
          .set(ItemBulkPackagingSelectPage(testIndex1), BulkPackagingType(ItemBulkPackagingCode.BulkLiquid, "bulk desc"))
      )

      PackageModel.applyBulkPackaging(testIndex1) mustBe Seq(PackageModel(
        kindOfPackages = ItemBulkPackagingCode.BulkLiquid.toString,
        numberOfPackages = None,
        shippingMarks = None,
        commercialSealIdentification = Some("seal"),
        sealInformation = Some("seal info")
      ))
    }
  }

  "applyIndividualPackaging" - {
    "must return an empty Seq" - {
      "if no packaging types entered" in {
        implicit val dr: DataRequest[_] = dataRequest(
          fakeRequest,
          emptyUserAnswers
        )

        PackageModel.applyIndividualPackaging(testIndex1) mustBe Nil
      }
    }
    "must return a Seq(PackageModel) for each packaging type" in {
      implicit val dr: DataRequest[_] = dataRequest(
        fakeRequest,
        emptyUserAnswers
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex1), ItemPackaging("BA", "Barrel"))
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex1), "3")
          .set(ItemPackagingShippingMarksPage(testIndex1, testPackagingIndex1), "marks")
          .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex1), ItemPackagingSealTypeModel("seal", None))
          .set(ItemSelectPackagingPage(testIndex1, testPackagingIndex2), ItemPackaging("JR", "Jar"))
          .set(ItemPackagingQuantityPage(testIndex1, testPackagingIndex2), "1")
          .set(ItemPackagingSealTypePage(testIndex1, testPackagingIndex2), ItemPackagingSealTypeModel("seal 2", Some("seal info")))
      )

      PackageModel.applyIndividualPackaging(testIndex1) mustBe Seq(
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
      )
    }
  }
}
