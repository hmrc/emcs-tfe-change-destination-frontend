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
import models.UserType._
import models.requests.DataRequest
import models.response.InvalidUserTypeException
import models.response.emcsTfe.GuarantorType.{NoGuarantor, Transporter}
import models.response.emcsTfe.{HeaderEadEsadModel, MovementGuaranteeModel, TransportModeModel}
import models.sections.ReviewAnswer.ChangeAnswers
import models.sections.info.ChangeType.{Consignee, Return}
import models.sections.info.DispatchPlace.{GreatBritain, NorthernIreland}
import models.sections.info.movementScenario.MovementScenario._
import models.sections.info.movementScenario.{DestinationType, MovementScenario}
import models.sections.info.{ChangeType, DispatchPlace}
import models.sections.journeyType.HowMovementTransported.{AirTransport, FixedTransportInstallations, RoadTransport}
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
import viewmodels.taskList._
import views.ViewUtils.titleNoForm

class TaskListHelperSpec extends SpecBase {

  lazy val helper = new TaskListHelper()

  Seq(TaskListMessages.English).foreach { messagesForLanguage =>
    s"when being rendered in lang code of ${messagesForLanguage.lang.code}" - {

      implicit val msgs: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(messagesForLanguage.lang))

      "heading" - {
        "when user answers are valid" - {
          "must return the taskList.heading.gbTaxWarehouseTo message" in {
            Seq[(String, MovementScenario)](
              ("GBWK123", GbTaxWarehouse)
            ).foreach {
              case (ern, movementScenario) =>
                implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = ern, answers = emptyUserAnswers.set(DestinationTypePage, movementScenario))

                val input1 = msgs(s"taskList.heading.$movementScenario")

                helper.heading mustBe messagesForLanguage.headingGbTaxWarehouseTo(input1)
                titleNoForm(helper.heading) mustBe messagesForLanguage.titleGbTaxWarehouseTo(input1)
            }
          }
          "must return the taskList.heading.dispatchPlaceTo message" in {
            Seq[(String, DispatchPlace, MovementScenario)](
              ("XIWK123", GreatBritain, GbTaxWarehouse),
              ("XIWK123", GreatBritain, EuTaxWarehouse),
              ("XIWK123", GreatBritain, RegisteredConsignee),
              ("XIWK123", GreatBritain, TemporaryRegisteredConsignee),
              ("XIWK123", GreatBritain, ExemptedOrganisation),
              ("XIWK123", GreatBritain, UnknownDestination),
              ("XIWK123", GreatBritain, DirectDelivery),
              ("XIWK123", NorthernIreland, GbTaxWarehouse),
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
              TaskListSectionRow(
                messagesForLanguage.movementDetails,
                "movementDetails",
                Some(controllers.sections.movement.routes.MovementIndexController.onPageLoad(testErn, testArc).url),
                Some(MovementSection),
                Some(Review)
              )
            )
          )
        }
      }

      "deliverySection" - {

        def consigneeRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.consignee,
          "consignee",
          Some(controllers.sections.consignee.routes.ConsigneeIndexController.onPageLoad(ern, testArc).url),
          Some(ConsigneeSection),
          Some(NotStarted)
        )

        def destinationRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.destination,
          "destination",
          Some(controllers.sections.destination.routes.DestinationIndexController.onPageLoad(ern, testArc).url),
          Some(DestinationSection),
          Some(NotStarted)
        )

        def exportRow(ern: String) = TaskListSectionRow(
          messagesForLanguage.export,
          "export",
          Some(controllers.sections.exportInformation.routes.ExportInformationIndexController.onPageLoad(ern, testArc).url),
          Some(ExportInformationSection),
          Some(NotStarted)
        )

        "when ChangeType is ReturnToConsignorPlaceOfDispatch" - {

          "should return None for the whole section" in {
            implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(ChangeTypePage, Return))
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
                    answers = emptyUserAnswers.set(DestinationTypePage, scenario).set(ChangeTypePage, ChangeType.Consignee),
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
                GbTaxWarehouse,
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
                GbTaxWarehouse,
                EuTaxWarehouse,
                RegisteredConsignee,
                TemporaryRegisteredConsignee,
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
        "should render the Guarantor section when changed to exports" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn,
            answers = emptyUserAnswers
              .set(HowMovementTransportedPage, RoadTransport)
              .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
            movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(Transporter, None))
          )
          helper.guarantorSection mustBe Some(TaskListSection(
            messagesForLanguage.guarantorSectionHeading,
            Seq(
              TaskListSectionRow(
                messagesForLanguage.guarantor,
                "guarantor",
                Some(controllers.sections.guarantor.routes.GuarantorIndexController.onPageLoad(testErn, testArc).url),
                Some(GuarantorSection),
                Some(NotStarted)
              )
            )
          ))
        }

        "should NOT render the Guarantor section when already answered" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn,
            answers = emptyUserAnswers
              .set(HowMovementTransportedPage, RoadTransport)
              .set(DestinationTypePage, GbTaxWarehouse),
            movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
          )
          helper.guarantorSection mustBe None
        }
      }

      "transportSection" - {

        "should have the correct heading" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers)
          helper.transportSection.sectionHeading mustBe messagesForLanguage.transportSectionHeading
        }
        "should render the journeyType row" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(JourneyTypeReviewPage, ChangeAnswers))
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.journeyType,
            "journeyType",
            Some(controllers.sections.journeyType.routes.JourneyTypeIndexController.onPageLoad(testErn, testArc).url),
            Some(JourneyTypeSection),
            Some(InProgress) //Cannot be in 'Not started' state as a journey type answer either is in the user answers or 801
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
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.transportArranger,
            "transportArranger",
            Some(controllers.sections.transportArranger.routes.TransportArrangerIndexController.onPageLoad(testErn, testArc).url),
            Some(TransportArrangerSection),
            Some(InProgress) //Cannot be in 'Not started' state as a transport arranger answer either is in the user answers or 801
          ))
        }
        "should render the firstTransporter row" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(FirstTransporterReviewPage, ChangeAnswers),
            movementDetails = maxGetMovementResponse.copy(firstTransporterTrader = None))
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.firstTransporter,
            "firstTransporter",
            Some(controllers.sections.firstTransporter.routes.FirstTransporterIndexController.onPageLoad(testErn, testArc).url),
            Some(FirstTransporterSection),
            Some(NotStarted)
          ))
        }
        "should render the units row" in {
          implicit val request: DataRequest[_] = dataRequest(FakeRequest(), ern = testErn, answers = emptyUserAnswers.set(TransportUnitsReviewPage, ChangeAnswers),
            movementDetails = maxGetMovementResponse.copy(transportDetails = Seq.empty))
          helper.transportSection.rows must contain(TaskListSectionRow(
            messagesForLanguage.units,
            "units",
            Some(controllers.sections.transportUnit.routes.TransportUnitIndexController.onPageLoad(testErn, testArc).url),
            Some(TransportUnitsSection),
            Some(NotStarted)
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
                  TaskListSectionRow(
                    taskName = "testName1",
                    id = "testId1",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  ),
                  TaskListSectionRow(
                    taskName = "testName2",
                    id = "testId2",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName3",
                    id = "testId3",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  ),
                  TaskListSectionRow(
                    taskName = "testName4",
                    id = "testId4",
                    link = None,
                    section = Some(TestSection),
                    status = Some(Completed)
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.submit,
                  id = "submit",
                  link = Some(controllers.routes.DeclarationController.onPageLoad(request.ern, request.arc).url),
                  section = None,
                  status = None
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
                  TaskListSectionRow(
                    taskName = "testName1",
                    id = "testId1",
                    link = None,
                    section = None,
                    status = Some(InProgress)
                  ),
                  TaskListSectionRow(
                    taskName = "testName2",
                    id = "testId2",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName3",
                    id = "testId3",
                    link = None,
                    section = None,
                    status = Some(Completed)
                  ),
                  TaskListSectionRow(
                    taskName = "testName4",
                    id = "testId4",
                    link = None,
                    section = None,
                    status = Some(Completed)
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.submit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
                )
              )
            )
          }
          "when all sections are incomplete" in {
            val testSections: Seq[TaskListSection] = Seq(
              TaskListSection(
                sectionHeading = "testHeading1",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName1",
                    id = "testId1",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  ),
                  TaskListSectionRow(
                    taskName = "testName2",
                    id = "testId2",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  )
                )
              ),
              TaskListSection(
                sectionHeading = "testHeading2",
                rows = Seq(
                  TaskListSectionRow(
                    taskName = "testName3",
                    id = "testId3",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  ),
                  TaskListSectionRow(
                    taskName = "testName4",
                    id = "testId4",
                    link = None,
                    section = None,
                    status = Some(NotStarted)
                  )
                )
              )
            )

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.submit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
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
                TaskListSectionRow(
                  taskName = messagesForLanguage.submit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
                )
              )
            )
          }
          "when no sections are provided" in {
            val testSections: Seq[TaskListSection] = Seq()

            helper.submitSection(testSections) mustBe TaskListSection(
              sectionHeading = messagesForLanguage.submitSectionHeading,
              rows = Seq(
                TaskListSectionRow(
                  taskName = messagesForLanguage.submit,
                  id = "submit",
                  link = None,
                  section = None,
                  status = Some(CannotStartYet)
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
                TaskListSectionRow(
                  taskName = "testName",
                  id = "testid",
                  None,
                  Some(TestSection),
                  Some(Completed)
                )
              )
            )
          )

          helper.submitSection(testSections) mustBe TaskListSection(
            sectionHeading = messagesForLanguage.submitSectionHeading,
            rows = Seq(
              TaskListSectionRow(
                taskName = messagesForLanguage.submit,
                id = "submit",
                link = None,
                section = None,
                status = Some(CannotStartYet)
              )
            )
          )
        }
      }

      "sections" - {
        "when NO guarantor information already exists" - {
          "when DestinationType has changed to export logged in UK" - {
            "should return all sections including Guarantor" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = testErn,
                answers = emptyUserAnswers
                  .set(HowMovementTransportedPage, AirTransport)
                  .set(DestinationTypePage, ExportWithCustomsDeclarationLodgedInTheUk),
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
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

          "when DestinationType has NOT change to export logged in UK and is FixedTransport" - {
            "should return all sections (excluding Guarantor)" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = testErn,
                answers = emptyUserAnswers
                  .set(HowMovementTransportedPage, FixedTransportInstallations)
                  .set(DestinationTypePage, GbTaxWarehouse),
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
              )
              helper.sections.map(_.sectionHeading) mustBe
                Seq(
                  messagesForLanguage.movementSectionHeading,
                  messagesForLanguage.deliverySectionHeading,
                  messagesForLanguage.transportSectionHeading,
                  messagesForLanguage.submitSectionHeading,
                )
            }
          }

          "when transport type is NOT FixedTransportInstallations and movement is UktoEU" - {
            "should return all sections including Guarantor" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = testNorthernIrelandErn,
                answers = emptyUserAnswers
                  .set(HowMovementTransportedPage, RoadTransport)
                  .set(DestinationTypePage, EuTaxWarehouse),
                movementDetails = maxGetMovementResponse.copy(
                  movementGuarantee = MovementGuaranteeModel(NoGuarantor, None),
                  transportMode = TransportModeModel(FixedTransportInstallations.toString, None)
                )
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

          "when transport type is FixedTransportInstallations" - {
            "should return all sections excluding Guarantor" in {
              implicit val request: DataRequest[_] = dataRequest(
                request = FakeRequest(),
                ern = testNorthernIrelandErn,
                answers = emptyUserAnswers
                  .set(HowMovementTransportedPage, FixedTransportInstallations)
                  .set(DestinationTypePage, EuTaxWarehouse),
                movementDetails = maxGetMovementResponse.copy(movementGuarantee = MovementGuaranteeModel(NoGuarantor, None))
              )
              helper.sections.map(_.sectionHeading) mustBe
                Seq(
                  messagesForLanguage.movementSectionHeading,
                  messagesForLanguage.deliverySectionHeading,
                  messagesForLanguage.transportSectionHeading,
                  messagesForLanguage.submitSectionHeading,
                )
            }
          }
        }

        "when guarantor information already exists" - {

          "should return all sections excluding Guarantor" in {
            implicit val request: DataRequest[_] = dataRequest(
              request = FakeRequest(),
              ern = testErn,
              answers = emptyUserAnswers
            )
            helper.sections.map(_.sectionHeading) mustBe
              Seq(
                messagesForLanguage.movementSectionHeading,
                messagesForLanguage.deliverySectionHeading,
                messagesForLanguage.transportSectionHeading,
                messagesForLanguage.submitSectionHeading,
              )
          }
        }
      }
    }
  }
}
