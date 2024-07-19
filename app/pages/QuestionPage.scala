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

package pages

import models.Index
import models.requests.DataRequest
import play.api.libs.json.Reads
import queries.{Derivable, Gettable, Settable}

trait QuestionPage[+A] extends Page with Gettable[A] with Settable[A] {

  def ifIndexIsValid[T, B](itemCount: Derivable[T, Int], idx: Index)
                          (valueIfIndexIsValid: Option[B])
                          (implicit request: DataRequest[_], reads: Reads[T]): Option[B] =
    request.userAnswers.get(itemCount) match {
      case Some(value) if idx.position >= 0 && idx.position < value => valueIfIndexIsValid
      case _ => None
    }

  def value[T >: A](implicit request: DataRequest[_], reads: Reads[T]): Option[T] = request.userAnswers.get[T](this)
  def is[T >: A](t: T)(implicit request: DataRequest[_], reads: Reads[T]): Boolean = value[T].contains(t)
}
