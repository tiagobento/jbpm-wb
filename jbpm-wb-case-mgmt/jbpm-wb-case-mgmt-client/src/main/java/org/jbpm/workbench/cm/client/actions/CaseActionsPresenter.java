/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.cm.client.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jbpm.workbench.cm.client.util.AbstractCaseInstancePresenter;
import org.jbpm.workbench.cm.model.CaseActionSummary;
import org.jbpm.workbench.cm.model.CaseInstanceSummary;
import org.jbpm.workbench.cm.model.CaseStageSummary;
import org.jbpm.workbench.cm.model.ProcessDefinitionSummary;
import org.jbpm.workbench.cm.predicate.HumanTaskNodePredicate;
import org.jbpm.workbench.cm.predicate.MilestoneNodePredicate;
import org.jbpm.workbench.cm.predicate.SubProcessNodePredicate;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseActionStatus;
import org.jbpm.workbench.cm.util.CaseActionType;
import org.jbpm.workbench.cm.util.CaseStageStatus;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static org.jbpm.workbench.cm.client.resources.i18n.Constants.*;
import static org.jbpm.workbench.cm.util.CaseActionType.*;

@Dependent
@WorkbenchScreen(identifier = CaseActionsPresenter.SCREEN_ID)
public class CaseActionsPresenter extends AbstractCaseInstancePresenter<CaseActionsPresenter.CaseActionsView> {

    public static final String SCREEN_ID = "Case Actions";

    private final Map<String, ProcessDefinitionSummary> processDefinitionSummaryMap = new HashMap<>();

    @Inject
    private User identity;

    @Inject
    private NewActionView newActionView;

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format(CASE_ACTIONS);
    }

    Map<String, ProcessDefinitionSummary> getProcessDefinitionSummaryMap() {
        return processDefinitionSummaryMap;
    }

    @Override
    protected void clearCaseInstance() {
        view.removeAllTasks();
        newActionView.clearAllStages();
        newActionView.clearAllProcessDefinitions();
    }

    @Override
    protected void loadCaseInstance(final CaseInstanceSummary cis) {
        view.updateListHeaders();
        setCaseStagesList(cis.getStages());
        processDefinitionSummaryMap.clear();
        caseService.call(
                (List<ProcessDefinitionSummary> processDefinitionSummaries) -> {
                    final List<String> processDefinitionNames = new ArrayList<>();
                    for (ProcessDefinitionSummary processDefinitionSummary : processDefinitionSummaries) {
                        processDefinitionNames.add(processDefinitionSummary.getName());
                        processDefinitionSummaryMap.put(processDefinitionSummary.getName(),
                                                        processDefinitionSummary);
                    }
                    Collections.sort(processDefinitionNames);
                    newActionView.setProcessDefinitions(processDefinitionNames);
                }
        ).getProcessDefinitions(containerId);
        refreshData(true);
    }

    void setCaseStagesList(final List<CaseStageSummary> caseStagesList) {
        newActionView.addStages(caseStagesList.stream()
                                              .filter(s -> s.getStatus().equals(CaseStageStatus.ACTIVE.getStatus()))
                                              .collect(toList()));
    }


    protected void setNewDynamicAction(CaseActionType caseActionType) {
        switch (caseActionType) {
            case DYNAMIC_USER_TASK: {
                newActionView.show(caseActionType,
                                   () -> addDynamicUserTaskAction(
                                           newActionView.getTaskName(),
                                           newActionView.getDescription(),
                                           newActionView.getActors(),
                                           newActionView.getGroups(),
                                           newActionView.getStageId()));
                break;
            }
            case DYNAMIC_SUBPROCESS_TASK: {
                newActionView.show(caseActionType,
                                   () -> addDynamicSubprocessTaskAction(
                                           newActionView.getProcessDefinitionName(),
                                           newActionView.getStageId()));
                break;
            }
        }
    }

    protected void addDynamicUserTaskAction(final String taskName,
                                            final String taskDescription,
                                            String actors,
                                            String groups,
                                            String stageId) {
        if (isNullOrEmpty(stageId)) {
            caseService.call((r) -> refreshData(false)).addDynamicUserTask(containerId,
                                                                           caseId,
                                                                           taskName,
                                                                           taskDescription,
                                                                           actors,
                                                                           groups,
                                                                           null);
        } else {
            caseService.call((r) -> refreshData(false)).addDynamicUserTaskToStage(containerId,
                                                                                  caseId,
                                                                                  stageId,
                                                                                  taskName,
                                                                                  taskDescription,
                                                                                  actors,
                                                                                  groups,
                                                                                  null);
        }
    }

    protected void addDynamicSubprocessTaskAction(final String caseDefinitionName,
                                                  String stageId) {
        final ProcessDefinitionSummary processDefinitionSummary = processDefinitionSummaryMap.get(caseDefinitionName);
        if (isNullOrEmpty(stageId)) {
            caseService.call((r) -> refreshData(false)).addDynamicSubProcess(containerId,
                                                                             caseId,
                                                                             processDefinitionSummary.getId(),
                                                                             null);
        } else {
            caseService.call((r) -> refreshData(false)).addDynamicSubProcessToStage(containerId,
                                                                                    caseId,
                                                                                    stageId,
                                                                                    processDefinitionSummary.getId(),
                                                                                    null);
        }
    }

    protected void triggerAdHocAction(final String actionName) {
        caseService.call((r) -> refreshData(false)).triggerAdHocAction(containerId,
                                                                       caseId,
                                                                       actionName,
                                                                       null);
    }

    protected void triggerAdHocActionInStage(final String actionName,
                                             final String stageId) {
        caseService.call((r) -> refreshData(false)).triggerAdHocActionInStage(containerId,
                                                                              caseId,
                                                                              stageId,
                                                                              actionName,
                                                                              null);
    }

    protected void refreshData(final boolean refreshAvailableActions) {
        caseService.call((Actions actions) -> {
            if (refreshAvailableActions) {
                List<CaseActionSummary> availableActions = new ArrayList<>();
                availableActions.add(CaseActionSummary.builder()
                                             .name(translationService.getTranslation(NEW_USER_TASK))
                                             .actionType(DYNAMIC_USER_TASK)
                                             .actionStatus(CaseActionStatus.AVAILABLE)
                                             .build());
                availableActions.add(CaseActionSummary.builder()
                                             .name(translationService.getTranslation(NEW_PROCESS_TASK))
                                             .actionType(DYNAMIC_SUBPROCESS_TASK)
                                             .actionStatus(CaseActionStatus.AVAILABLE)
                                             .build());
                availableActions.addAll(actions.getAvailableActions());
                view.setAvailableActionsList(availableActions);
            }
            view.setInProgressActionsList(actions.getInProgressAction());
            view.setCompletedActionsList(actions.getCompleteActions());
        }).getCaseActions(containerId, caseId, identity.getIdentifier());
    }

    void setAction(final CaseActionItemView caseActionItem) {
        final CaseActionSummary caseActionItemModel = caseActionItem.getValue();
        switch (caseActionItemModel.getActionStatus()) {
            case AVAILABLE: {
                prepareAction(caseActionItem);
                break;
            }
            case IN_PROGRESS: {
                caseActionItem.addCreationDate();
                final String nodeLabel = getNodeTypeLabel(caseActionItemModel);
                final String actionOwner = caseActionItemModel.getActualOwner();
                if (isNullOrEmpty(actionOwner) == false && isNullOrEmpty(nodeLabel) == false) {
                    caseActionItem.addActionInfo(" ( " + nodeLabel + " - " + actionOwner + " ) ");
                } else if (isNullOrEmpty(nodeLabel) == false) {
                    caseActionItem.addActionInfo(" ( " + nodeLabel + " ) ");
                }
                break;
            }
            case COMPLETED: {
                final String nodeLabel = getNodeTypeLabel(caseActionItemModel);
                if(isNullOrEmpty(nodeLabel) == false){
                    caseActionItem.addActionInfo(" ( " + nodeLabel + " ) ");
                }
                caseActionItem.addCreationDate();
            }
        }
    }

    protected String getNodeTypeLabel(final CaseActionSummary action){
        if(new HumanTaskNodePredicate().test(action.getType())){
            return translationService.format(HUMAN_TASK);
        } else if(new MilestoneNodePredicate().test(action.getType())){
            return translationService.format(MILESTONE);
        } else if(new SubProcessNodePredicate().test(action.getType())){
            return translationService.format(SUB_PROCESS);
        } else {
            return null;
        }
    }

    void prepareAction(final CaseActionItemView caseActionItem) {
        final CaseActionSummary caseActionItemModel = caseActionItem.getValue();

        switch (caseActionItemModel.getActionType()) {
            case AD_HOC_TASK: {
                if (caseActionItemModel.getStage() == null) {
                    caseActionItem.addActionInfo(translationService.format(AVAILABLE_IN) + ": " + translationService.format(CASE));
                } else {
                    caseActionItem.addActionInfo(translationService.format(AVAILABLE_IN) + ": " + caseActionItemModel.getStage().getName());
                }
                caseActionItem.addAction(new CaseActionsPresenter.CaseActionAction() {
                    @Override
                    public String label() {
                        return translationService.format(ACTION_START);
                    }

                    @Override
                    public void execute() {
                        if (caseActionItemModel.getStage() == null) {
                            triggerAdHocAction(caseActionItemModel.getName());
                        } else {
                            triggerAdHocActionInStage(caseActionItemModel.getName(),
                                                      caseActionItemModel.getStage().getIdentifier());
                        }
                    }
                });
                break;
            }
            case DYNAMIC_SUBPROCESS_TASK:
            case DYNAMIC_USER_TASK: {
                caseActionItem.addActionInfo(translationService.format(DYMANIC));
                caseActionItem.addAction(new CaseActionsPresenter.CaseActionAction() {
                    @Override
                    public String label() {
                        return translationService.format(ACTION_START);
                    }

                    @Override
                    public void execute() {
                        setNewDynamicAction(caseActionItemModel.getActionType());
                    }
                });
            }
        }
    }

    public interface CaseActionsView extends UberElement<CaseActionsPresenter> {

        void removeAllTasks();

        void setAvailableActionsList(List<CaseActionSummary> caseActionList);

        void setInProgressActionsList(List<CaseActionSummary> caseActionList);

        void setCompletedActionsList(List<CaseActionSummary> caseActionList);

        void updateListHeaders();
    }

    public interface CaseActionsListView extends UberElement<CaseActionsPresenter> {

        void removeAllTasks();

        void setCaseActionList(List<CaseActionSummary> caseActionList);

        void updateActionsHeader(final String heatherText,
                                 final String... stylesClass);
    }

    public interface NewActionView extends UberElement<CaseActionsPresenter> {

        void show(CaseActionType caseActionType,
                  Command okCommand);

        void hide();

        String getTaskName();

        String getDescription();

        String getProcessDefinitionName();

        String getActors();

        String getGroups();

        void addStages(List<CaseStageSummary> caseStage);

        String getStageId();

        void clearAllStages();

        void setProcessDefinitions(List<String> processDefinitions);

        void clearAllProcessDefinitions();
    }

    public interface CaseActionAction extends Command {

        String label();
    }
}