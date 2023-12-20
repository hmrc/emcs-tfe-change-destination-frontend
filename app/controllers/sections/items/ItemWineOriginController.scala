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

package controllers.sections.items

import controllers.actions._
import forms.sections.items.ItemWineOriginFormProvider
import models.requests.DataRequest
import models.{Index, Mode}
import navigation.ItemsNavigator
import pages.sections.items.ItemWineOriginPage
import play.api.data.Form
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import services.{GetCountriesAndMemberStatesService, UserAnswersService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.select.SelectItem
import viewmodels.helpers.SelectItemHelper
import views.html.sections.items.ItemWineOriginView

import javax.inject.Inject
import scala.concurrent.Future

class ItemWineOriginController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       override val userAnswersService: UserAnswersService,
                                       override val navigator: ItemsNavigator,
                                       override val auth: AuthAction,
                                       override val getData: DataRetrievalAction,
                                       override val requireData: DataRequiredAction,
                                       override val userAllowList: UserAllowListAction,
                                       formProvider: ItemWineOriginFormProvider,
                                       countryAndMemberStatesService: GetCountriesAndMemberStatesService,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: ItemWineOriginView
                                     ) extends BaseItemsNavigationController with AuthActionHelper {

  def onPageLoad(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        countryAndMemberStatesService.getCountryCodesAndMemberStates().flatMap { allCountries =>
          countryAndMemberStatesService.removeEUMemberStates(allCountries).flatMap { countries =>
            val selectItems = SelectItemHelper.constructSelectItems(
              selectOptions = countries,
              defaultTextMessageKey = "itemWineOrigin.select.defaultValue",
              existingAnswer = request.userAnswers.get(ItemWineOriginPage(idx)).map(_.countryCode)
            )
            renderView(Ok, fillForm(ItemWineOriginPage(idx), formProvider(countries)), selectItems, idx, mode)
          }
        }

      }
    }

  def onSubmit(ern: String, arc: String, idx: Index, mode: Mode): Action[AnyContent] =
    authorisedDataRequestAsync(ern, arc) { implicit request =>
      validateIndexAsync(idx) {
        countryAndMemberStatesService.getCountryCodesAndMemberStates().flatMap { allCountries =>
          countryAndMemberStatesService.removeEUMemberStates(allCountries).flatMap { countries =>
            formProvider(countries).bindFromRequest().fold(
              formWithErrors => {
                val selectItems = SelectItemHelper.constructSelectItems(
                  selectOptions = countries,
                  defaultTextMessageKey = "itemWineOrigin.select.defaultValue"
                )
                renderView(BadRequest, formWithErrors, selectItems, idx, mode)
              },
              saveAndRedirect(ItemWineOriginPage(idx), _, mode)
            )
          }
        }
      }
    }

  private def renderView(status: Status, form: Form[_], selectItems: Seq[SelectItem], idx: Index, mode: Mode)
                        (implicit request: DataRequest[_]): Future[Result] =
    Future.successful(status(view(
      form = form,
      action = routes.ItemWineOriginController.onSubmit(request.ern, request.arc, idx, mode),
      selectOptions = selectItems
    )))
}
