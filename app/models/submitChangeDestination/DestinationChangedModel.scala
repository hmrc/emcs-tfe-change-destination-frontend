/*
 * Copyright 2024 HM Revenue & Customs
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

import models.audit.Auditable
import models.requests.DataRequest
import models.sections.ReviewAnswer.KeepAnswers
import models.sections.info.movementScenario.DestinationType
import pages.sections.consignee.ConsigneeSection
import pages.sections.guarantor.{GuarantorReviewPage, GuarantorSection}
import pages.sections.info.DestinationTypePage
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{Json, OFormat, OWrites, __}
import utils.ModelConstructorHelpers


case class DestinationChangedModel(
                                    destinationTypeCode: DestinationType,
                                    newConsigneeTrader: Option[TraderModel],
                                    deliveryPlaceTrader: Option[TraderModel],
                                    deliveryPlaceCustomsOffice: Option[DeliveryPlaceCustomsOfficeModel],
                                    movementGuarantee: Option[MovementGuaranteeModel]
                                  )

object DestinationChangedModel extends ModelConstructorHelpers {

  implicit val fmt: OFormat[DestinationChangedModel] = Json.format

  def apply(implicit request: DataRequest[_]): DestinationChangedModel =
    DestinationChangedModel(
      destinationTypeCode = mandatoryPage(DestinationTypePage).destinationType,
      newConsigneeTrader = Option.when(ConsigneeSection.hasChanged)(TraderModel.applyConsignee),
      deliveryPlaceTrader = TraderModel.applyDeliveryPlace(mandatoryPage(DestinationTypePage)),
      deliveryPlaceCustomsOffice = DeliveryPlaceCustomsOfficeModel.apply,
      movementGuarantee = Option.when(GuarantorSection.requiresGuarantorToBeProvided && !GuarantorReviewPage.value.contains(KeepAnswers))(MovementGuaranteeModel.apply)
    )

  val auditWrites: OWrites[DestinationChangedModel] = (
    (__ \ "destinationTypeCode").write[DestinationType](Auditable.writes[DestinationType]) and
      (__ \ "newConsigneeTrader").writeNullable[TraderModel] and
      (__ \ "deliveryPlaceTrader").writeNullable[TraderModel] and
      (__ \ "deliveryPlaceCustomsOffice").writeNullable[DeliveryPlaceCustomsOfficeModel] and
      (__ \ "movementGuarantee").writeNullable[MovementGuaranteeModel](MovementGuaranteeModel.auditWrites)
    )(unlift(DestinationChangedModel.unapply))
}
