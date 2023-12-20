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

package fixtures.messages.sections.info

import fixtures.messages.{BaseEnglish, BaseMessages, i18n}

object LocalReferenceNumberMessages {

  sealed trait ViewMessages extends BaseMessages {
    _: i18n =>

    val newHeading: String = "Create a unique reference for this movement"
    val newTitle: String = titleHelper(newHeading)
    val newP1: String = "This is also known as a Local Reference Number (LRN)."
    val newP2: String = "Use this reference to help you identify this movement in your records. The reference you create must not have been used for a previous movement."
    val newErrorRequired: String = "Enter a unique reference"
    val newErrorLength: String = "Unique reference must be 22 characters or less"
    val newCyaLabel: String = "Unique reference (LRN)"
    val newCyaChangeHidden: String = "Unique reference (LRN)"

    val deferredHeading: String = "Enter the Local Reference Number (LRN) for the deferred movement"
    val deferredTitle: String = titleHelper(deferredHeading)
    val deferredP1: String = "This must match the LRN on the Fallback Accompanying Document (FAD)."
    val deferredErrorRequired: String = "Enter the LRN"
    val deferredErrorLength: String = "LRN must be 22 characters or less"
    val deferredCyaLabel: String = "Local Reference Number (LRN)"
    val deferredCyaChangeHidden: String = "Local Reference Number (LRN)"
  }

  object English extends ViewMessages with BaseEnglish


}
