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

package controllers.sections.info

import base.SpecBase
import controllers.actions.FakeDataRetrievalAction
import models.{NormalMode, UserAnswers}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

class InfoIndexControllerSpec extends SpecBase {

  class Test(userAnswers: Option[UserAnswers]) {

    lazy val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

    lazy val controller = new InfoIndexController(
      fakeUserAllowListAction,
      new FakeDataRetrievalAction(userAnswers, Some(testMinTraderKnownFacts)),
      dataRequiredAction,
      fakeAuthAction,
      Helpers.stubMessagesControllerComponents()
    )
  }

  "InfoIndex Controller" - {

    "pre-draft" - {

      "with a Northern Ireland Warehouse Keeper ERN" - {

        "must redirect to the Dispatch place page (CAM-INFO01)" in new Test(None) {
          val result = controller.onPreDraftPageLoad(testNorthernIrelandErn)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.info.routes.DispatchPlaceController.onPreDraftPageLoad(testNorthernIrelandErn, NormalMode).url)
        }
      }

      "with any other ERN" - {

        "must redirect to the Destination Type page (CAM-INFO08)" in new Test(None) {
          val result = controller.onPreDraftPageLoad(testGreatBritainErn)(request)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe
            Some(controllers.sections.info.routes.DestinationTypeController.onPreDraftPageLoad(testGreatBritainErn, NormalMode).url)
        }
      }
    }

    "post-draft" - {
      "must redirect to CYA" in new Test(Some(emptyUserAnswers)) {
        val result = controller.onPageLoad(testGreatBritainErn, testDraftId)(request)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe
          Some(controllers.sections.info.routes.InformationCheckAnswersController.onPageLoad(testGreatBritainErn, testDraftId).url)
      }
    }
  }
}