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

package object forms {
  private[forms] val TEXTAREA_MAX_LENGTH = 350
  private[forms] val ALPHANUMERIC_REGEX = "^(?s)(?=.*[A-Za-z0-9]).{1,}$"
  private[forms] val XSS_REGEX = "^(?s)(?!.*javascript)(?!.*[<>;:]).{1,}$"
  private[forms] val ONLY_ALPHANUMERIC_REGEX = "^[A-Za-z0-9]*$"
  private[forms] val CUSTOMS_OFFICE_CODE_REGEX = "^[A-Z]{2}[a-zA-Z0-9]{6}$"
  private[forms] val EORI_NUMBER_REGEX = "[A-Za-z]{2}[A-Za-z0-9]{0,15}"
  private[forms] val GB_00_EXCISE_NUMBER_REGEX = "(GB00)[a-zA-Z0-9]{9}"
  private[forms] val XI_00_EXCISE_NUMBER_REGEX = "(XI00)[a-zA-Z0-9]{9}"
  private[forms] val XI_OR_GB_00_EXCISE_NUMBER_REGEX = "(GB00|XI00)[a-zA-Z0-9]{9}"
}
