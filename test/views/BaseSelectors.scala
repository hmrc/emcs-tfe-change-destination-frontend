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

package views

trait BaseSelectors {

  val title = "title"
  val h1: String = "h1"
  val subHeadingCaptionSelector: String = "main .govuk-caption-xl"
  def h2(i: Int) = s"main h2:nth-of-type($i)"
  val hiddenText = s".govuk-visually-hidden"
  def h3(i: Int) = s"main h3:nth-of-type($i)"
  val p: Int => String = i => s"main p:nth-of-type($i)"
  val strong: Int => String = i => s"main span.govuk-\\!-font-weight-bold:nth-of-type($i)"
  val link: Int => String = i => s"main a:nth-of-type($i)"
  def bullet(i: Int, ul: Int = 1) = s"main ul.govuk-list--bullet:nth-of-type($ul) li:nth-of-type($i)"
  val inset = ".govuk-inset-text"
  val hint: String = "main .govuk-hint"
  val button = "main .govuk-button"
  val secondaryButton = ".govuk-button--secondary"
  val saveAndExitLink = "#save-and-exit"
  val label: String => String = forId => s"main label[for='$forId']"
  val legend = "main legend"
  def radioButton(radioIndex: Int) = s".govuk-radios > div:nth-child($radioIndex) > label"
  def radioDividerButton(radioIndex: Int) = s".govuk-radios > div:nth-child($radioIndex)"
  def checkboxItem(index: Int) = s".govuk-checkboxes > div:nth-child($index) > label"
  def checkboxDividerItem(index: Int) = s".govuk-checkboxes > div:nth-child($index)"
  val dateDay = s".govuk-date-input .govuk-date-input__item:nth-of-type(1)"
  val dateMonth = s".govuk-date-input .govuk-date-input__item:nth-of-type(2)"
  val dateYear = s".govuk-date-input .govuk-date-input__item:nth-of-type(3)"

  val notificationBannerTitle = ".govuk-notification-banner__title"
  val notificationBannerContent = ".govuk-notification-banner__heading"
  val notificationBannerError: Int => String = i => s".govuk-notification-banner__content li:nth-of-type($i)"

  val tableHeader: Int => String = i => s"main table thead tr th:nth-of-type($i)"
  val tableRow: (Int, Int) => String = (x, y) => s"main table tbody tr:nth-of-type($x) > :nth-child($y)"

  val inputSuffix = ".govuk-input__suffix"

  def summary(i: Int) = s"main details:nth-of-type($i) summary"

  def summaryCardHeading(i: Int) = s"main .govuk-summary-card:nth-of-type($i) .govuk-summary-card__title"
}

object BaseSelectors extends BaseSelectors

