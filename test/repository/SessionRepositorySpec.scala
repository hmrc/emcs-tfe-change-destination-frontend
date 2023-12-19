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

package repository

import base.SpecBase
import models.UserAnswers
import org.scalatest.concurrent.IntegrationPatience
import pages.sections.info.DeferredMovementPage
import repositories.SessionRepositoryImpl
import uk.gov.hmrc.mongo.test.{CleanMongoCollectionSupport, PlayMongoRepositorySupport}

import scala.concurrent.ExecutionContext

class SessionRepositorySpec extends SpecBase with PlayMongoRepositorySupport[UserAnswers] with CleanMongoCollectionSupport with IntegrationPatience {

  implicit val ec: ExecutionContext = app.injector.instanceOf[ExecutionContext]

  lazy val repository: SessionRepositoryImpl = new SessionRepositoryImpl(
    mongoComponent = mongoComponent,
    appConfig = appConfig
  )

  val userAnswers = emptyUserAnswers
    .set(DeferredMovementPage(), true)


  ".get" - {
    "return None when the repository is empty" in {
      repository.get("ern", "sessionId").futureValue mustBe None
    }

    "return the correct record from the repository" in {
      repository.set(userAnswers).futureValue mustBe true

      val response = repository.get(testErn, testDraftId).futureValue
      response.isDefined mustBe true
      response.get.data mustBe userAnswers.data
      response.get.lastUpdated mustNot be(userAnswers.lastUpdated)
    }
  }

  ".set" - {
    "populate the repository correctly" in {
      repository.set(userAnswers).futureValue mustBe true

      val response = repository.get(testErn, testDraftId).futureValue
      response.isDefined mustBe true
      response.get.data mustBe userAnswers.data
      response.get.lastUpdated mustNot be(userAnswers.lastUpdated)
    }
  }

  ".clear" - {
    "clear the repository correctly" in {
      repository.set(userAnswers).futureValue mustBe true
      repository.clear(testErn, testDraftId).futureValue mustBe true
      repository.get(testErn, testDraftId).futureValue.isDefined mustBe false
    }
  }

}