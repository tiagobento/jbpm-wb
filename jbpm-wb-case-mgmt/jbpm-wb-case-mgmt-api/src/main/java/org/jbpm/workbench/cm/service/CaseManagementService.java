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

package org.jbpm.workbench.cm.service;

import java.util.List;
import java.util.Map;

import org.jboss.errai.bus.server.annotations.Remote;
import org.jbpm.workbench.cm.model.*;
import org.jbpm.workbench.cm.util.Actions;
import org.jbpm.workbench.cm.util.CaseInstanceSearchRequest;
import org.jbpm.workbench.cm.util.CaseMilestoneSearchRequest;

@Remote
public interface CaseManagementService {

    CaseDefinitionSummary getCaseDefinition(String containerId, String caseDefinitionId);

    List<CaseDefinitionSummary> getCaseDefinitions();

    String startCaseInstance(String containerId,
                             String caseDefinitionId,
                             String owner,
                             List<CaseRoleAssignmentSummary> roleAssignments);

    List<CaseInstanceSummary> getCaseInstances(CaseInstanceSearchRequest request);

    CaseInstanceSummary getCaseInstance(String containerId, String caseId);

    void cancelCaseInstance(String containerId, String caseId);

    void closeCaseInstance(String containerId,
                           String caseId,
                           String comment);

    List<CaseCommentSummary> getComments(String containerId,
                                         String caseId,
                                         Integer currentPage,
                                         Integer pageSize);

    void addComment(String containerId,
                    String caseId,
                    String author,
                    String text);

    void updateComment(String containerId,
                       String caseId,
                       String commentId,
                       String author,
                       String text);

    void removeComment(String containerId,
                       String caseId,
                       String commentId);

    void assignUserToRole(String containerId,
                          String caseId,
                          String roleName,
                          String user);

    void assignGroupToRole(String containerId,
                           String caseId,
                           String roleName,
                           String group);

    void removeUserFromRole(String containerId,
                            String caseId,
                            String roleName,
                            String user);

    void removeGroupFromRole(String containerId,
                             String caseId,
                             String roleName,
                             String group);

    List<CaseMilestoneSummary> getCaseMilestones(String containerId,
                                                 String caseId,
                                                 CaseMilestoneSearchRequest request);

    List<CaseStageSummary> getCaseStages(String containerId, String caseId);

    Actions getCaseActions(String container,
                           String caseId,
                           String userId);

    void addDynamicUserTask(String containerId,
                            String caseId,
                            String name,
                            String description,
                            String actors,
                            String groups,
                            Map<String, Object> data);

    void addDynamicUserTaskToStage(String containerId,
                                   String caseId,
                                   String stageId,
                                   String name,
                                   String description,
                                   String actors,
                                   String groups,
                                   Map<String, Object> data);

    void addDynamicSubProcess(String containerId,
                              String caseId,
                              String processId,
                              Map<String, Object> data);

    void addDynamicSubProcessToStage(String containerId,
                                     String caseId,
                                     String stageId,
                                     String processId,
                                     Map<String, Object> data);

    void triggerAdHocActionInStage(String containerId,
                                   String caseId,
                                   String stageId,
                                   String adHocName,
                                   Map<String, Object> data);

    void triggerAdHocAction(String containerId,
                            String caseId,
                            String adHocName,
                            Map<String, Object> data);

    List<ProcessDefinitionSummary> getProcessDefinitions(String containerId);
}