/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workbench.cm.client.util;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jbpm.workbench.cm.client.events.CaseRefreshEvent;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.service.CaseManagementService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class AbstractCaseInstancePresenter<V extends UberElement> extends AbstractPresenter<V> {

    public static final String PARAMETER_CASE_ID = "caseId";
    public static final String PARAMETER_CONTAINER_ID = "containerId";

    protected PlaceRequest place;

    protected String caseId;

    protected String containerId;

    @Inject
    protected TranslationService translationService;

    protected Caller<CaseManagementService> caseService;

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
        this.caseId = place.getParameter(PARAMETER_CASE_ID,
                                         null);
        this.containerId = place.getParameter(PARAMETER_CONTAINER_ID,
                                              null);
        findCaseInstance();
    }

    protected abstract void loadCaseInstance(CaseInstanceSummary cis);

    protected abstract void clearCaseInstance();

    public void findCaseInstance() {
        clearCaseInstance();
        if (isCaseInstanceValid()) {
            caseService.call((CaseInstanceSummary cis) -> loadCaseInstance(cis)).getCaseInstance(containerId, caseId);
        }
    }

    protected boolean isCaseInstanceValid() {
        return  /* !isNullOrEmpty(serverTemplateId) //TODO: include this check when serverTemplateId starts to be used*/
                !isNullOrEmpty(containerId) && !isNullOrEmpty(caseId);
    }

    public void onCaseRefreshEvent(@Observes CaseRefreshEvent caseRefreshEvent) {
        findCaseInstance();
    }

    @Inject
    public void setCaseService(final Caller<CaseManagementService> caseService) {
        this.caseService = caseService;
    }
}