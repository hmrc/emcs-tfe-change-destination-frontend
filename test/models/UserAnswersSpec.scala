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
 * Copyright 2022 HM Revenue & Customs
 *
 */

package models

import base.SpecBase
import pages.QuestionPage
import play.api.libs.json._


class UserAnswersSpec extends SpecBase {

  case class TestPage(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override def path: JsPath = jsPath \ toString

    override def toString: String = "TestPage"
  }

  case class TestPage2(jsPath: JsPath = JsPath) extends QuestionPage[String] {
    override def path: JsPath = jsPath \ toString

    override def toString: String = "TestPage2"
  }

  case class TestModel(TestPage: String,
                       TestPage2: Option[String] = None)

  object TestModel {
    implicit val fmt: Format[TestModel] = Json.format[TestModel]
  }

  "UserAnswers" - {

    "when calling .set(page)" - {

      "when no data exists for that page" - {

        "must set the answer for the first time" in {
          emptyUserAnswers.set(TestPage(), "foo") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
        }
      }

      "when data exists for that page" - {

        "must change the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.set(TestPage(), "bar") mustBe emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "bar"
          ))
        }
      }

      "when setting at a subPath with indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 2), "wizz")


          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          )
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val result =
            emptyUserAnswers
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 0), "foo")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 1), "bar")
              .set(TestPage(__ \ "items" \ 0 \ "subItems" \ 2), "wizz")

          result.data mustBe Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          )
        }
      }
    }

    "when calling .get(page)" - {

      "when no data exists for that page" - {

        "must return None" in {
          emptyUserAnswers.get(TestPage()) mustBe None
        }
      }

      "when data exists for that page" - {

        "must Some(data)" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.get(TestPage()) mustBe Some("foo")
        }
      }

      "when getting data at a subPath with indexes" - {

        "must return the answer at the subPath" in {

          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj("TestPage" -> "foo"),
              Json.obj("TestPage" -> "bar"),
              Json.obj("TestPage" -> "wizz")
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0)) mustBe Some("foo")
        }
      }

      "when setting at a subPath which contains nested indexes" - {

        "must store the answer at the subPath" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "items" -> Json.arr(
              Json.obj(
                "subItems" -> Json.arr(
                  Json.obj("TestPage" -> "foo"),
                  Json.obj("TestPage" -> "bar"),
                  Json.obj("TestPage" -> "wizz")
                )
              )
            )
          ))
          withData.get(TestPage(__ \ "items" \ 0 \ "subItems" \ 0)) mustBe Some("foo")
        }
      }
    }

    "when calling .remove(page)" - {

      "when no data exists for that page" - {

        "must return the emptyUserAnswers unchanged" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "AnotherPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe withData
        }
      }

      "when data exists for that page" - {

        "must remove the answer" in {
          val withData = emptyUserAnswers.copy(data = Json.obj(
            "TestPage" -> "foo"
          ))
          withData.remove(TestPage()) mustBe emptyUserAnswers
        }
      }

      "when removing data at a subPath with indexes" - {

        "when the page is the last page in the subObject" - {

          "must remove the entire object from the array at the subPath" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "bar"),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }

        "when the page is NOT the last page in the subObject" - {

          "must remove just that page object key from the object in the array" in {

            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj(
                  "TestPage" -> "bar",
                  "TestPage2" -> "bar2"
                ),
                Json.obj("TestPage" -> "wizz")
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj("TestPage" -> "foo"),
                Json.obj("TestPage2" -> "bar2"),
                Json.obj("TestPage" -> "wizz")
              )
            )
          }
        }
      }

      "when removing at a subPath which contains nested indexes" - {

        "when the page is that last item in the arrays object" - {

          "must remove the object containing the answer from the array at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "bar"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }

        "when the page is NOT the last item in the arrays object" - {

          "must remove just that key from the object at the subPath" in {
            val withData = emptyUserAnswers.copy(data = Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage" -> "bar",
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            ))
            val result = withData.remove(TestPage(__ \ "items" \ 0 \ "subItems" \ 1))
            result.data mustBe Json.obj(
              "items" -> Json.arr(
                Json.obj(
                  "subItems" -> Json.arr(
                    Json.obj("TestPage" -> "foo"),
                    Json.obj(
                      "TestPage2" -> "bar2"
                    ),
                    Json.obj("TestPage" -> "wizz")
                  )
                )
              )
            )
          }
        }
      }
    }

    "when calling .handleResult" - {

      "when failed to update the UserAnswers" - {

        "must throw the exception" in {
          intercept[JsResultException](emptyUserAnswers.handleResult(JsError("OhNo")))
        }
      }

      "when updated UserAnswers successfully" - {

        "must return the user answers" in {
          emptyUserAnswers.handleResult(JsSuccess(emptyUserAnswers.data)) mustBe emptyUserAnswers
        }
      }
    }

  }
}