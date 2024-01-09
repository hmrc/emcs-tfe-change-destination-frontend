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

package pages.behaviours

import base.SpecBase
import models.requests.DataRequest
import pages.QuestionPage
import play.api.libs.json._
import play.api.test.FakeRequest

trait PageBehaviours extends SpecBase {

  implicit val request: DataRequest[_] = dataRequest(FakeRequest())

  class BeRetrievable[A] {
    def apply[P <: QuestionPage[A]](page: P, value: A)(implicit fmt: Format[A]): Unit = {

      "when being retrieved from UserAnswers" - {
        "and the question has not been answered" - {
          "must return None" in {
            val userAnswers = emptyUserAnswers.remove(page)
            userAnswers.get(page) must be(empty)
          }
        }

        "and the question has been answered" - {
          "must return the saved value" in {
            val userAnswers = emptyUserAnswers.set(page, value)
            userAnswers.get(page).value mustEqual value
          }
        }
      }
    }
  }

  class BeSettable[A] {
    def apply[P <: QuestionPage[A]](page: P, value: A, newValue: A)(implicit fmt: Format[A]): Unit = {

      "must be able to be set on UserAnswers" in {
        val userAnswers = emptyUserAnswers.set(page, value)
        val updatedAnswers = userAnswers.set(page, newValue)
        updatedAnswers.get(page).value mustEqual newValue
      }
    }
  }

  class BeRemovable[A] {
    def apply[P <: QuestionPage[A]](page: P, value: A)(implicit fmt: Format[A]): Unit = {

      "must be able to be removed from UserAnswers" in {
        val userAnswers = emptyUserAnswers.set(page, value)
        val updatedAnswers = userAnswers.remove(page)
        updatedAnswers.get(page) must be(empty)
      }
    }
  }

  def beRetrievable[A]: BeRetrievable[A] = new BeRetrievable[A]

  def beSettable[A]: BeSettable[A] = new BeSettable[A]

  def beRemovable[A]: BeRemovable[A] = new BeRemovable[A]
}
