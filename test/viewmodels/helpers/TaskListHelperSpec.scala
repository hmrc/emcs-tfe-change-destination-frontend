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

package viewmodels.helpers

import base.SpecBase
import fixtures.messages.TaskListMessages
import models.MovementValidationFailure
import models.UserType._
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.response.emcsTfe.HeaderEadEsadModel
import models.sections.ReviewAnswer.ChangeAnswers
import models.sections.info.ChangeType.ReturnToConsignor
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario._
import models.sections.info.movementScenario.{DestinationType, MovementScenario}
import models.sections.info.{ChangeType, DispatchPlace}
import models.sections.journeyType.HowMovementTransported.AirTransport
import models.sections.transportArranger.TransportArranger
import pages.sections.Section
import pages.sections.consignee.ConsigneeSection
import pages.sections.destination.DestinationSection
import pages.sections.exportInformation.ExportInformationSection
import pages.sections.firstTransporter.{FirstTransporterReviewPage, FirstTransporterSection}
import pages.sections.guarantor.GuarantorSection
import pages.sections.info.{ChangeTypePage, DestinationTypePage, DispatchPlacePage}
import pages.sections.journeyType.{HowMovementTransportedPage, JourneyTypeReviewPage, JourneyTypeSection}
import pages.sections.movement.MovementSection
import pages.sections.transportArranger.{TransportArrangerReviewPage, TransportArrangerSection}
import pages.sections.transportUnit.{TransportUnitsReviewPage, TransportUnitsSection}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.{JsObject, JsPath}
import play.api.test.FakeRequest
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.{HtmlContent, TaskListItem, TaskListItemTitle, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.tasklist.TaskListItemStatus
import viewmodels.taskList._
import views.ViewUtils.titleNoForm
import views.html.components.{list, p}

class TaskListHelperSpec extends SpecBase {

  lazy val helper = app.injector.instanceOf[TaskListHelper]
  lazy val p: p = app.injector.instanceOf[p]
  lazy val list: list = app.injector.instanceOf[list]

  Seq(TaskListMessages.English).foreach { messagesForLanguage =>
    s"when being rendered in lang code of ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      "heading" - {
        "when user answers are valid" - {
          "must return the correct taskList.heading.dutyPaid message" in {
            Seq[(String, MovementScenario)](
              ("XIPA123", CertifiedConsignee),
              ("XIPC123", CertifiedConsignee),
              ("XIPA123", TemporaryCertifiedConsignee),
              ("XIPC123", TemporaryCertifiedConsignee),
              ("XIPA123", ReturnToThePlaceOfDispatch),
              ("XIPC123", ReturnToThePlaceOfDispatch)
            ).foreach {
              case (ern, movementScenario) =>

                implicit val request: DataRequest[_] =
                  dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                helper.heading mustBe messagesForLanguage.headingDutyPaid(movementScenario)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleDutyPaid(movementScenario)
            }
          }
          "must return the taskList.heading.gbTaxWarehouseTo message" in {
            Seq[(String, MovementScenario)](
              ("GBWK123", UkTaxWarehouse.GB),
              ("GBWK123", UkTaxWarehouse.NI)
            ).foreach {
              case (ern, movementScenario) =>
                implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                val destination = msgs(s"taskList.heading.$movementScenario")

                helper.heading mustBe messagesForLanguage.headingGbTaxWarehouseTo(destination)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleGbTaxWarehouseTo(destination)
            }
          }
          "must return the taskList.heading.dispatchPlaceTo message" in {
            Seq[(String, DispatchPlace, MovementScenario)](
              ("XIWK123", GreatBritain, UkTaxWarehouse.GB),
              ("XIWK123", GreatBritain, UkTaxWarehouse.NI),
              ("XIWK123", GreatBritain, EuTaxWarehouse),
              ("XIWK123", GreatBritain, RegisteredConsignee),
              ("XIWK123", GreatBritain, TemporaryRegisteredConsignee),
              ("XIWK123", GreatBritain, ExemptedOrganisation),
              ("XIWK123", GreatBritain, UnknownDestination),
              ("XIWK123", GreatBritain, DirectDelivery),
              ("XIWK123", NorthernIreland, UkTaxWarehouse.GB),
              ("XIWK123", NorthernIreland, EuTaxWarehouse),
              ("XIWK123", NorthernIreland, RegisteredConsignee),
              ("XIWK123", NorthernIreland, TemporaryRegisteredConsignee),
              ("XIWK123", NorthernIreland, ExemptedOrganisation),
              ("XIWK123", NorthernIreland, UnknownDestination),
              ("XIWK123", NorthernIreland, DirectDelivery),
            ).foreach {
              case (ern, dispatchPlace, movementScenario) =>
                implicit val request: DataRequest[_] =
                  dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(DispatchPlacePage, dispatchPlace).set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"dispatchPlace.$dispatchPlace")
                val input2 = msgs(Seq(s"taskList.heading.$movementScenario", s"destinationType.$movementScenario"))

                helper.heading mustBe messagesForLanguage.headingDispatchPlaceTo(input1, input2)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleDispatchPlaceTo(input1, input2)
            }
          }
          "must return the taskList.heading.importFor message" in {
            Seq[String](
              "GBRC123",
              "XIRC123"
            ).foreach(
              ern =>
                MovementScenario.values.foreach {
                  movementScenario =>
                    implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                    val input1 = msgs(s"destinationType.$movementScenario")

                    helper.heading mustBe messagesForLanguage.headingImportFor(input1)
                    titleNoForm(helper.heading) mustBe messagesForLanguage.titleImportFor(input1)
                }
            )
          }
          "must return the destination type" in {
            Seq[(String, MovementScenario)](
              ("GBWK123", ExportWithCustomsDeclarationLodgedInTheUk),
              ("GBWK123", ExportWithCustomsDeclarationLodgedInTheEu),
              ("XIWK123", ExportWithCustomsDeclarationLodgedInTheUk),
              ("XIWK123", ExportWithCustomsDeclarationLodgedInTheEu)
            ).foreach {
              case (ern, movementScenario) =>
                implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"destinationType.$movementScenario")

                helper.heading mustBe input1
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleHelper(input1)
            }
          }
        }
        "when inputs are invalid" - {
          "must throw an error when the ERN is invalid" in {
            implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = "GB00123", answers = emptyUserAnswers)

            val response = intercept[InvalidUserTypeException](helper.heading)

            response.message mustBe s"[TaskListHelper][heading] invalid UserType and destinationType combination for CAM journey: $GreatBritainWarehouse | ${Some(EuTaxWarehouse)}"
          }
          "must throw an error when the ERN/destinationType combo is invalid" in {
            implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = "GBWK123", answers = emptyUserAnswers.set(DestinationTypePage, UnknownDestination))

            val response = intercept[InvalidUserTypeException](helper.heading)

            response.message mustBe s"[TaskListHelper][heading] invalid UserType and destinationType combination for CAM journey: $GreatBritainWarehouseKeeper | ${Some(UnknownDestination)}"
          }
        }
      }

      "movementSection" - {
        "should render the Movement details section" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest())
          helper.movementSection mustBe TaskListSection(
            messagesForLanguage.movementSectionHeading,
            Seq(
              TaskListItemViewModel(
                row = TaskListItem(
                  title = TaskListItemTitle(Text(messagesForLanguage.movementDetails)),
                  status = TaskListItemStatus(Some(Review.toTag)),
                  href = Some(controllers.sections.movement.routes.MovementIndexController.onPageLoad(testErn, testArc).url)
                ),
                section = Some(MovementSection)
              )
            )
          )
        }
      }

      "deliverySection" - {

        def consigneeRow(ern: String) =
          TaskListItemViewModel(
            row = TaskListItem(
              title = TaskListItemTitle(Text(messagesForLanguage.consignee)),
              status = TaskListItemStatus(Some(NotStarted.toTag)),
              href = Some(controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testArc).url)
            ),
            section = Some(ConsigneeSection)
          )

        def destinationRow(ern: String) =
          TaskListItemViewModel(
            row = TaskListItem(
              title = TaskListItemTitle(Text(messagesForLanguage.destination)),
              status = TaskListItemStatus(Some(NotStarted.toTag)),
              href = Some(controllers.sections.destination.routes.DestinationIndexController.onPageLoad(ern, testArc).url)
            ),
            section = Some(DestinationSection)
          )

        def exportRow(ern: String) =
          TaskListItemViewModel(
            row = TaskListItem(
              title = TaskListItemTitle(Text(messagesForLanguage.export)),
              status = TaskListItemStatus(Some(NotStarted.toTag)),
              href = Some(controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(ern, testArc).url)
            ),
            section = Some(ExportInformationSection)
          )

        "when ChangeType is ReturnToConsignorPlaceOfDispatch" - {

          "should return None for the whole section" in {
            implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(ChangeTypePage, ReturnToConsignor))
            helper.deliverySection mustBe None
          }
        }

        "when ChangeType is NOT ReturnToConsignorPlaceOfDispatch" - {

          "should have the correct heading" in {
            implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers)
            helper.deliverySection.get.sectionHeading mustBe messagesForLanguage.deliverySectionHeading
          }
          "should render the consignee row" - {
            "when MovementScenario is valid and ChangeType is 'Consignee'" in {
              MovementScenario.values.filterNot(_ == UnknownDestination).foreach {
                scenario =>
                  implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn,
                    answers = emptyUserAnswers.set(DestinationTypePage, scenario).set(ChangeTypePage, ChangeType.ChangeConsignee),
                    movementDetails = maxGetMovementResponse.copy(memberStateCode = None, serialNumberOfCertificateOfExemption = None,
                      consigneeTrader = None
                    ))
                  helper.deliverySection.get.rows must contain(consigneeRow(testErn))
              }
            }
          }
          "should not render the consignee row" - {
            "when MovementScenario is invalid" in {
              Seq(UnknownDestination).foreach {
                scenario =>
                  implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(DestinationTypePage, scenario))
                  helper.deliverySection.get.rows must not contain consigneeRow(testErn)
              }
            }
            "when MovementScenario is missing" in {
              implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers)
              helper.deliverySection.get.rows must not contain consigneeRow(testErn)
            }
          }
          "should render the destination row" - {
            "when MovementScenario is valid" in {
              Seq(
                UkTaxWarehouse.GB,
                UkTaxWarehouse.NI,
                EuTaxWarehouse,
                RegisteredConsignee,
                TemporaryRegisteredConsignee,
                ExemptedOrganisation,
                DirectDelivery
              ).foreach {
                scenario =>
                  implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn,
                    answers = emptyUserAnswers.set(DestinationTypePage, scenario),
                    movementDetails = maxGetMovementResponse.copy(memberStateCode = None, serialNumberOfCertificateOfExemption = None,
                      deliveryPlaceTrader = None
                    ))
                  helper.deliverySection.get.rows must contain(destinationRow(testErn))
              }
            }
          }
          "should not render the destination row" - {
            "when MovementScenario is invalid" in {
              MovementScenario.values.filterNot(Seq(
                UkTaxWarehouse.GB,
                UkTaxWarehouse.NI,
                EuTaxWarehouse,
                RegisteredConsignee,
                TemporaryRegisteredConsignee,
                TemporaryCertifiedConsignee,
                CertifiedConsignee,
                ExemptedOrganisation,
                DirectDelivery
              ).contains).foreach {
                scenario =>
                  implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(DestinationTypePage, scenario))
                  helper.deliverySection.get.rows must not contain destinationRow(testErn)
              }
            }
            "when MovementScenario is missing (use IE801)" in {
              implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers)
              helper.deliverySection.get.rows must contain(destinationRow(testErn))
            }
          }
          "should render the export row" - {
            "when MovementScenario is valid" in {
              Seq(
                ExportWithCustomsDeclarationLodgedInTheUk,
                ExportWithCustomsDeclarationLodgedInTheEu
              ).foreach {
                scenario =>
                  implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn,
                    answers = emptyUserAnswers.set(DestinationTypePage, scenario),
                    movementDetails = maxGetMovementResponse.copy(deliveryPlaceCustomsOfficeReferenceNumber = None)
                  )
                  helper.deliverySection.get.rows must contain(exportRow(testErn))
              }
            }
          }
          "should not render the export row" - {
            "when MovementScenario is invalid" in {
              MovementScenario.values.filterNot(Seq(
                ExportWithCustomsDeclarationLodgedInTheUk,
                ExportWithCustomsDeclarationLodgedInTheEu
              ).contains).foreach {
                scenario =>
                  implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(DestinationTypePage, scenario))
                  helper.deliverySection.get.rows must not contain exportRow(testErn)
              }
            }
            "when MovementScenario is missing" in {
              implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers)
              helper.deliverySection.get.rows must not contain exportRow(testErn)
            }
          }
        }
      }

      "guarantorSection" - {

        "should render the Guarantor section" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest())

          helper.guarantorSection mustBe TaskListSection(
            messagesForLanguage.guarantorSectionHeading,
            Seq(
              TaskListItemViewModel(
                row = TaskListItem(
                  title = TaskListItemTitle(Text(messagesForLanguage.guarantor)),
                  status = TaskListItemStatus(Some(Review.toTag)),
                  href = Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testArc).url)
                ),
                section = Some(GuarantorSection)
              )
            ))
        }
      }

      "transportSection" - {

        "should have the correct heading" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers)
          helper.transportSection.sectionHeading mustBe messagesForLanguage.transportSectionHeading
        }
        "should render the journeyType row" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(JourneyTypeReviewPage, ChangeAnswers))
          helper.transportSection.rows must contain(TaskListItemViewModel(
            row = TaskListItem(
              title = TaskListItemTitle(Text(messagesForLanguage.journeyType)),
              status = TaskListItemStatus(Some(InProgress.toTag)), //Cannot be in 'Not started' state as a transport arranger answer either is in the user answers or 801
              href = Some(controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(testErn, testArc).url)
            ),
            section = Some(JourneyTypeSection)
          ))
        }
        "should render the transportArranger row" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(TransportArrangerReviewPage, ChangeAnswers),
            movementDetails = maxGetMovementResponse.copy(transportArrangerTrader = None, headerEadEsad = HeaderEadEsadModel(
              sequenceNumber = 1,
              dateAndTimeOfUpdateValidation = "HeaderEadEsadDateTime",
              destinationType = DestinationType.TemporaryCertifiedConsignee,
              journeyTime = "10 hours",
              transportArrangement = TransportArranger.GoodsOwner
            )))
          helper.transportSection.rows must contain(TaskListItemViewModel(
            row = TaskListItem(
              title = TaskListItemTitle(Text(messagesForLanguage.transportArranger)),
              status = TaskListItemStatus(Some(InProgress.toTag)), //Cannot be in 'Not started' state as a transport arranger answer either is in the user answers or 801
              href = Some(controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(testErn, testArc).url)
            ),
            section = Some(TransportArrangerSection)
          ))
        }
        "should render the firstTransporter row" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(FirstTransporterReviewPage, ChangeAnswers),
            movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))
          helper.transportSection.rows must contain(TaskListItemViewModel(
            row = TaskListItem(
              title = TaskListItemTitle(Text(messagesForLanguage.firstTransporter)),
              status = TaskListItemStatus(Some(NotStarted.toTag)),
              href = Some(controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(testErn, testArc).url)
            ),
            section = Some(FirstTransporterSection)
          ))
        }
        "should render the units row" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(TransportUnitsReviewPage, ChangeAnswers),
            movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty))
          helper.transportSection.rows must contain(TaskListItemViewModel(
            row = TaskListItem(
              title = TaskListItemTitle(Text(messagesForLanguage.units)),
              status = TaskListItemStatus(Some(NotStarted.toTag)),
              href = Some(controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url)
            ),
            section = Some(TransportUnitsSection)
          ))
        }
      }

      "submitSection" - {
        implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers)

        "must return a TaskList with a link" - {
          "when all sections are completed" in {
            object TestSection extends Section[JsObject] {
              override def status(implicit request: DataRequest[_]): TaskListStatus = Completed

              override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = true

              override val path: JsPath = JsPath
            }

            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq(
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName1")),
                      status = TaskListItemStatus(Some(Completed.toTag)),
                      href = None
                    ),
                    section = Some(TestSection)
                  ),
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName2")),
                      status = TaskListItemStatus(Some(Completed.toTag)),
                      href = None
                    ),
                    section = Some(TestSection)
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName3")),
                      status = TaskListItemStatus(Some(Completed.toTag)),
                      href = None
                    ),
                    section = Some(TestSection)
                  ),
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName4")),
                      status = TaskListItemStatus(Some(Completed.toTag)),
                      href = None
                    ),
                    section = Some(TestSection)
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListItemViewModel(
                  row = TaskListItem(
                    title = TaskListItemTitle(Text(messagesForLanguage.submit)),
                    status = TaskListItemStatus(Some(NotStarted.toTag)),
                    href = Some(controllers.routes.DeclarationController.onPageLoad(request.ern, request.arc).url)
                  ),
                  section = None
                )
              )
            )
          }
        }

        "must return a TaskList without a link" - {
          "when some sections are incomplete" in {
            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq(
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName1")),
                      status = TaskListItemStatus(Some(InProgress.toTag)),
                      href = None
                    ),
                    section = None
                  ),
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName2")),
                      status = TaskListItemStatus(Some(NotStarted.toTag)),
                      href = None
                    ),
                    section = None
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName3")),
                      status = TaskListItemStatus(Some(Completed.toTag)),
                      href = None
                    ),
                    section = None
                  ),
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName4")),
                      status = TaskListItemStatus(Some(Completed.toTag)),
                      href = None
                    ),
                    section = None
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListItemViewModel(
                  row = TaskListItem(
                    title = TaskListItemTitle(Text(messagesForLanguage.submit)),
                    status = TaskListItemStatus(Some(CannotStartYet.toTag)),
                    href = None
                  ),
                  section = None
                )
              )
            )
          }
          "when all sections are incomplete" in {
            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq(
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName1")),
                      status = TaskListItemStatus(Some(NotStarted.toTag)),
                      href = None
                    ),
                    section = None
                  ),
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName2")),
                      status = TaskListItemStatus(Some(NotStarted.toTag)),
                      href = None
                    ),
                    section = None
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName3")),
                      status = TaskListItemStatus(Some(NotStarted.toTag)),
                      href = None
                    ),
                    section = None
                  ),
                  TaskListItemViewModel(
                    row = TaskListItem(
                      title = TaskListItemTitle(Text("testName4")),
                      status = TaskListItemStatus(Some(NotStarted.toTag)),
                      href = None
                    ),
                    section = None
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListItemViewModel(
                  row = TaskListItem(
                    title = TaskListItemTitle(Text(messagesForLanguage.submit)),
                    status = TaskListItemStatus(Some(CannotStartYet.toTag)),
                    href = None
                  ),
                  section = None
                )
              )
            )
          }
          "when all sections are empty" in {
            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq()
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq()
              )
            )


            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListItemViewModel(
                  row = TaskListItem(
                    title = TaskListItemTitle(Text(messagesForLanguage.submit)),
                    status = TaskListItemStatus(Some(CannotStartYet.toTag)),
                    href = None
                  ),
                  section = None
                )
              )
            )
          }
          "when no sections are provided" in {
            val testSections: Seq[TaskListSection] = Seq()

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListItemViewModel(
                  row = TaskListItem(
                    title = TaskListItemTitle(Text(messagesForLanguage.submit)),
                    status = TaskListItemStatus(Some(CannotStartYet.toTag)),
                    href = None
                  ),
                  section = None
                )
              )
            )
          }
        }

        "must filter out sections which cannot be filled out for that user/destination type, even ifSome(Completed)" in {
          object TestSection extends Section[JsObject] {
            override def status(implicit request: DataRequest[_]): TaskListStatus = Completed

            override def canBeCompletedForTraderAndDestinationType(implicit request: DataRequest[_]): Boolean = false

            override val path: JsPath = JsPath
          }

          val testSections: Seq[TaskListSection] = Seq(
            TaskListSection(
              sectionHeading = "testHeading",
              rows = Seq(
                TaskListItemViewModel(
                  row = TaskListItem(
                    title = TaskListItemTitle(Text("testName")),
                    status = TaskListItemStatus(Some(Completed.toTag)),
                    href = None
                  ),
                  section = Some(TestSection)
                )
              )
            )
          )

          helper.submitSection(testSections) mustBe TaskListSection(
            sectionHeading = messagesForLanguage.submitSectionHeading,
            rows = Seq(
              TaskListItemViewModel(
                row = TaskListItem(
                  title = TaskListItemTitle(Text(messagesForLanguage.submit)),
                  status = TaskListItemStatus(Some(CannotStartYet.toTag)),
                  href = None
                ),
                section = None
              )
            )
          )
        }
      }

      "sections" - {
        "should return all expected sections" in {
          implicit val request: DataRequest[_] = dataRequest(
            request = FakeRequest(),
            answers = emptyUserAnswers.set(HowMovementTransportedPage, AirTransport)
          )
          helper.sections.map(_.sectionHeading) mustBe
            Seq(
              messagesForLanguage.movementSectionHeading,
              messagesForLanguage.deliverySectionHeading,
              messagesForLanguage.guarantorSectionHeading,
              messagesForLanguage.transportSectionHeading,
              messagesForLanguage.submitSectionHeading,
            )
        }
      }

      "validationFailureContent" - {
        "when errorType is 12 or 13" - {
          "must return the correct content for a validation failure" in {
            Seq(12, 13).foreach {
              errorType =>
                val failure = MovementValidationFailure(Some(errorType), Some("This is an error. Please amend your entry and resubmit."))
                val result = helper.validationFailureContent(Seq(failure))
                result mustBe HtmlContent(HtmlFormat.fill(Seq(
                  p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                  list(Seq(p()(Html("This is an error."))))
                )))
            }
          }
        }

        "when errorType is not 12 or 13" - {
          "must return the correct content for a validation failure" in {
            Seq(14, 9999, 123456).foreach {
              errorType =>
                val failure = MovementValidationFailure(Some(errorType), Some("This is an error. Please amend your entry and resubmit."))
                val result = helper.validationFailureContent(Seq(failure))
                result mustBe HtmlContent(HtmlFormat.fill(Seq(
                  p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                  list(Seq(p()(Html(s"errors.validation.notificationBanner.$errorType.content"))))
                )))
            }
          }
        }

        "when errorType is missing" - {
          "must return an empty list" in {
            val failure = MovementValidationFailure(None, Some("This is an error. Please amend your entry and resubmit."))
            val result = helper.validationFailureContent(Seq(failure))
            result mustBe HtmlContent(HtmlFormat.fill(Seq(
              p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
              list(Seq())
            )))
          }
        }

        "when errorReason is missing" - {
          "for errorType 12 or 13" - {
            "must return an empty list" in {
              Seq(12, 13).foreach {
                errorType =>
                  val failure = MovementValidationFailure(Some(errorType), None)
                  val result = helper.validationFailureContent(Seq(failure))
                  result mustBe HtmlContent(HtmlFormat.fill(Seq(
                    p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                    list(Seq())
                  )))
              }
            }
          }

          "for other errorTypes" - {
            "must return a non-empty list" in {
              Seq(14, 9999, 123456).foreach {
                errorType =>
                  val failure = MovementValidationFailure(Some(errorType), None)
                  val result = helper.validationFailureContent(Seq(failure))
                  result mustBe HtmlContent(HtmlFormat.fill(Seq(
                    p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                    list(Seq(p()(Html(s"errors.validation.notificationBanner.$errorType.content"))))
                  )))
              }
            }
          }

          "for all valid errors" - {
            "must return the correct content" in {
              val validationFailures = Seq(
                MovementValidationFailure(Some(8024), Some("beans")),
                MovementValidationFailure(Some(8601), Some("beans")),
                MovementValidationFailure(Some(12), Some("The transport arranger trader information is not required if they are the consignee or the consignor.")),
                MovementValidationFailure(Some(12), Some("The delivery place customs office is not required for an export.")),
                MovementValidationFailure(Some(13), Some("The transport arranger trader information is required if they are not the consignee or the consignor.")),
                MovementValidationFailure(Some(13), Some("The place of delivery information must be present where the destination is a Tax Warehouse, Direct Delivery, Certified consignee or Temporary certified consignee.")),
                MovementValidationFailure(Some(13), Some("The customs office must be present for an export.")),
                MovementValidationFailure(Some(13), Some("The Trader ID must be present where the destination is one of the following: Tax Warehouse, Registered Consignee, Temporary Registered Consignee, Direct Delivery, Certified Consignee, Temporary Certified Consignee, or Destination-Return to the place of dispatch of the consignor, for a Duty Paid B2B movement.")),
                MovementValidationFailure(Some(8020), Some("beans")),
                MovementValidationFailure(Some(8021), Some("beans")),
                MovementValidationFailure(Some(8022), Some("beans")),
                MovementValidationFailure(Some(8023), Some("beans")),
                MovementValidationFailure(Some(8025), Some("beans")),
                MovementValidationFailure(Some(8026), Some("beans")),
                MovementValidationFailure(Some(8027), Some("beans")),
                MovementValidationFailure(Some(8028), Some("beans")),
                MovementValidationFailure(Some(8029), Some("beans")),
                MovementValidationFailure(Some(8030), Some("beans")),
                MovementValidationFailure(Some(8032), Some("beans")),
                MovementValidationFailure(Some(8089), Some("beans")),
                MovementValidationFailure(Some(8092), Some("beans")),
                MovementValidationFailure(Some(8093), Some("beans")),
                MovementValidationFailure(Some(8094), Some("beans")),
                MovementValidationFailure(Some(8095), Some("beans")),
                MovementValidationFailure(Some(8138), Some("beans")),
                MovementValidationFailure(Some(8147), Some("beans")),
                MovementValidationFailure(Some(8148), Some("beans")),
                MovementValidationFailure(Some(8149), Some("beans")),
                MovementValidationFailure(Some(8150), Some("beans")),
                MovementValidationFailure(Some(8151), Some("beans")),
                MovementValidationFailure(Some(8163), Some("beans")),
                MovementValidationFailure(Some(8200), Some("beans")),
                MovementValidationFailure(Some(8201), Some("beans")),
                MovementValidationFailure(Some(8202), Some("beans")),
                MovementValidationFailure(Some(8203), Some("beans")),
                MovementValidationFailure(Some(8204), Some("beans")),
                MovementValidationFailure(Some(8205), Some("beans")),
                MovementValidationFailure(Some(8206), Some("beans")),
                MovementValidationFailure(Some(8207), Some("beans")),
                MovementValidationFailure(Some(8208), Some("beans")),
                MovementValidationFailure(Some(8209), Some("beans")),
                MovementValidationFailure(Some(8210), Some("beans")),
                MovementValidationFailure(Some(8211), Some("beans")),
                MovementValidationFailure(Some(8212), Some("beans")),
                MovementValidationFailure(Some(8213), Some("beans")),
                MovementValidationFailure(Some(8214), Some("beans")),
                MovementValidationFailure(Some(8215), Some("beans")),
                MovementValidationFailure(Some(8550), Some("beans")),
                MovementValidationFailure(Some(8551), Some("beans")),
                MovementValidationFailure(Some(8555), Some("beans")),
                MovementValidationFailure(Some(8556), Some("beans")),
                MovementValidationFailure(Some(8600), Some("beans")),
                MovementValidationFailure(Some(8602), Some("beans")),
                MovementValidationFailure(Some(8603), Some("beans")),
                MovementValidationFailure(Some(8604), Some("beans")),
                MovementValidationFailure(Some(8605), Some("beans")),
                MovementValidationFailure(Some(8606), Some("beans")),
                MovementValidationFailure(Some(8607), Some("beans")),
                MovementValidationFailure(Some(8608), Some("beans")),
                MovementValidationFailure(Some(8609), Some("beans"))
              )

              val result = helper.validationFailureContent(validationFailures)

              result mustBe HtmlContent(HtmlFormat.fill(Seq(
                p("govuk-notification-banner__heading")(Html(messagesForLanguage.notificationBannerValidationFailuresContent)),
                list(Seq(
                  p()(Html("You must either select ‘Tax Warehouse’ or ‘Export’ if the Trader ID begins with ‘GB’ or ‘XI’.")),
                  p()(Html("If the MessageRecipient in the header ends in GB or XI then a change of destination can only be to a consignee in Northern Ireland or Great Britain, or for export.")),
                  p()(Html("The transport arranger trader information is not required if they are the consignee or the consignor.")),
                  p()(Html("The delivery place customs office is not required for an export.")),
                  p()(Html("The transport arranger trader information is required if they are not the consignee or the consignor.")),
                  p()(Html("The place of delivery information must be present where the destination is a Tax Warehouse, Direct Delivery, Certified consignee or Temporary certified consignee.")),
                  p()(Html("The customs office must be present for an export.")),
                  p()(Html("The Trader ID must be present where the destination is one of the following: Tax Warehouse, Registered Consignee, Temporary Registered Consignee, Direct Delivery, Certified Consignee, Temporary Certified Consignee, or Destination-Return to the place of dispatch of the consignor, for a Duty Paid B2B movement.")),
                  p()(Html("The transport details can repeat up to a maximum of 30 times.")),
                  p()(Html("The date and time that has been submitted is not allowed.")),
                  p()(Html("The sequence number is not allowed.")),
                  p()(Html("You must select at least one of the following for the Destination Type: Trader Place of Delivery, Office Place of Delivery, or New Consignee details.")),
                  p()(Html("You must provide a valid Excise Number if the Destination is to any of the following: Tax Warehouse, Registered Consignee, Temporary Registered Consignee, Direct Delivery, Certified Consignee, Temporary Certified Consignee.")),
                  p()(Html("You must provide a Trader ID for the place of delivery if the destination is to a Tax Warehouse, Certified Consignee, or Temporary certified consignee.")),
                  p()(Html("You must provide a trader name for the selected movement type.")),
                  p()(Html("You must provide a Street Name for the selected movement type.")),
                  p()(Html("You must provide a Postcode for the selected movement type.")),
                  p()(Html("You must provide a City Name for the selected movement type.")),
                  p()(Html("A Trader ID is not required for Direct Deliveries.")),
                  p()(Html("You must provide the new transporter trader information.")),
                  p()(Html("The language code for the place of delivery on the Change of Destination must be provided.")),
                  p()(Html("A valid excise number must be present where the destination is a Tax Warehouse.")),
                  p()(Html("The Transport Unit information must be provided unless the movement is to an Exempted Consignee.")),
                  p()(Html("The Transport Unit information must not be provided if the movement is to an Exempted Consignee.")),
                  p()(Html("The destination type must be one of the following: Tax warehouse, Registered consignee, Temporary registered consignee, Direct Delivery, Export, Certified Consignee, Temporary Certified Consignee, Return to the place of dispatch of the consignor for a Duty Paid B2B movement.")),
                  p()(Html("Complementary information must be present if the transport code is other.")),
                  p()(Html("Complementary information must not be present if the transport code is one of the following: Sea Transport, Rail Transport, Road Transport, Air Transport, Postal Consignment, Fixed Transport Installation, or Inland Waterway Transport.")),
                  p()(Html("The place of delivery information must not be present where the destination is to an Unknown Destination.")),
                  p()(Html("The place of delivery information must not be present where the destination is for Export.")),
                  p()(Html("The place of delivery information must not be present where the destination is Return to the place of dispatch of the Consignor.")),
                  p()(Html("The consignee EORI number must only be present for an export movement.")),
                  p()(Html("A guarantor must be provided if the ‘transporter of goods’ or ‘the owner of goods’ has been selected.")),
                  p()(Html("Only one guarantor can be selected. Please amend your entry and resubmit.")),
                  p()(Html("If a guarantor is not required for a UK to UK movement then the destination must be to an authorised warehouse. Please amend your entry and resubmit.")),
                  p()(Html("If a guarantor is not required for a UK to UK movement then the Excise ID must start with ‘GB’ or ‘XI’.")),
                  p()(Html("The consignee Excise ID must start with ‘GB’ or ‘XI’.")),
                  p()(Html("The trader place of delivery Excise ID must start with ‘GB’ or ‘XI’.")),
                  p()(Html("If a guarantor is not required for qualifying UK to EU Movements then the mode of transport must be Sea Transport or Fixed Transport Installation.")),
                  p()(Html("You must provide a trading name if the trader Excise ID is absent.")),
                  p()(Html("You must provide a street name if the trader Excise ID is absent.")),
                  p()(Html("You must provide a city if the trader Excise ID is absent.")),
                  p()(Html("You must provide a postcode if the trader Excise ID is absent.")),
                  p()(Html("The language code must be provided if the street name is present.")),
                  p()(Html("The language code must be provided if the street number is present.")),
                  p()(Html("The language code must be provided if the postcode is present.")),
                  p()(Html("The language code must be provided if the city is present.")),
                  p()(Html("An Excise ID is not required where the guarantor is the consignor or a consignee, or for a qualifying movement.")),
                  p()(Html("The UK consignee Trader ID must start with GBWK or XIWK where the place of destination is a UK excise warehouse.")),
                  p()(Html("The consignee Trader ID must not start with GB or XI when the place of destination is a non-UK excise warehouse.")),
                  p()(Html("The UK place of delivery excise warehouse ID must start with GB00 where the place of destination is a UK excise warehouse.")),
                  p()(Html("The consignee delivery place trader ID must not start with GB or XI where the place of destination is either a non-UK excise warehouse or Temporary Registered Consignee.")),
                  p()(Html("If the country code in the Administrative Reference Code is GB then the MessageRecipient in the header must be either GB or XI.")),
                  p()(Html("The consignee trader ID must start with XIWK where the place of destination is a NI excise warehouse.")),
                  p()(Html("The consignee must not have a Northern Ireland postcode starting with ‘BT’ if the consignee Traders ID starts with GB.")),
                  p()(Html("The consignee must have a Northern Ireland postcode starting with ‘BT’ if the consignee Traders ID starts with XI.")),
                  p()(Html("The consignee delivery place Trader ID must start with XI00 where the place of destination is to an excise warehouse in Northern Ireland.")),
                  p()(Html("The consignee delivery place trader postcode must start with ‘BT’ where the place of destination is to an excise warehouse in Northern Ireland.")),
                  p()(Html("The consignee delivery place trader postcode must not start with ‘BT’ where the place of destination is to an excise warehouse in Great Britain.")),
                  p()(Html("If the country code in the Administrative Reference Code is GB then the delivery place customs office must also start with GB.")),
                  p()(Html("If the country code in the Administrative Reference Code is XI then the delivery place customs office must not start with GB."))
                ))
              )))
            }
          }
        }

        "removeAmendEntryMessageFromErrorReason" - {
          "must remove 'Please amend your entry and resubmit.' from the error reason" in {
            Seq(
              "This is an error. Please amend your entry and resubmit.",
              "This is an error.       Please amend your entry and resubmit.",
              "This is an error.Please amend your entry and resubmit.",
              "This is an error. Please amend your entry and resubmit",
              "This is an error.        Please amend your entry and resubmit",
              "This is an error.Please amend your entry and resubmit"
            ).foreach { errorMessage =>
              val result = helper.removeAmendEntryMessageFromErrorReason(errorMessage)
              result mustBe "This is an error."
            }
          }

          "must replace single quotes around 'Import', 'Tax Warehouse', 'Duty Paid', and 'Export', with smart quotes" in {
            Seq("Import", "Tax Warehouse", "Duty Paid", "Export").foreach {
              text =>
                val result = helper.removeAmendEntryMessageFromErrorReason(s"This is an error. '$text'.")
                result mustBe s"This is an error. ‘$text’."
            }
          }

          "must replace 'origin type code is 'Tax Warehouse'.' with 'origin type code is 'Tax Warehouse' or 'Duty Paid'.'" in {
            val result = helper.removeAmendEntryMessageFromErrorReason("This is an error. origin type code is 'Tax Warehouse'.")
            result mustBe "This is an error. origin type code is ‘Tax Warehouse’ or ‘Duty Paid’."
          }

          "must not modify the error reason if 'Please amend your entry and resubmit.' is not present, 'origin type code is 'Tax Warehouse'.' is not present, and there are no quotes to be replaced with smart quotes" in {
            val result = helper.removeAmendEntryMessageFromErrorReason("This is an error.")
            result mustBe "This is an error."
          }
        }
      }
    }
  }
}
