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

package config

import com.google.inject.AbstractModule
import connectors.emcsTfe.{UserAnswersConnector, UserAnswersConnectorImpl}
import connectors.referenceData._
import controllers.actions._
import controllers.actions.predraft._
import repositories.{SessionRepository, SessionRepositoryImpl}
import utils.{TimeMachine, TimeMachineImpl}

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[PreDraftDataRetrievalAction]).to(classOf[PreDraftDataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[PreDraftDataRequiredAction]).to(classOf[PreDraftDataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[AuthAction]).to(classOf[AuthActionImpl]).asEagerSingleton()
    bind(classOf[BetaAllowListAction]).to(classOf[BetaAllowListActionImpl]).asEagerSingleton()
    bind(classOf[MovementAction]).to(classOf[MovementActionImpl]).asEagerSingleton()
    bind(classOf[TimeMachine]).to(classOf[TimeMachineImpl]).asEagerSingleton()
    bind(classOf[SessionRepository]).to(classOf[SessionRepositoryImpl]).asEagerSingleton()
    bind(classOf[UserAnswersConnector]).to(classOf[UserAnswersConnectorImpl]).asEagerSingleton()
    bind(classOf[GetMemberStatesConnector]).to(classOf[GetMemberStatesConnectorImpl]).asEagerSingleton()
    bind(classOf[GetTraderKnownFactsConnector]).to(classOf[GetTraderKnownFactsConnectorImpl]).asEagerSingleton()

  }
}
