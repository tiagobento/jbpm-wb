/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.pr.client.editors.instance.diagram;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.IsWidget;
import org.jbpm.workbench.pr.model.NodeInstanceSummary;
import org.jbpm.workbench.pr.model.ProcessNodeSummary;
import org.jbpm.workbench.pr.model.TimerInstanceSummary;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

public interface ProcessInstanceDiagramView extends HasBusyIndicator,
                                                    IsWidget,
                                                    TakesValue<ProcessNodeSummary> {

    void displayImage(String svgContent);

    void displayMessage(String message);

    void setProcessNodes(List<ProcessNodeSummary> nodes);

    void setNodeInstances(List<NodeInstanceSummary> nodes);

    void setTimerInstances(List<TimerInstanceSummary> timers);

    void setOnProcessNodeSelectedCallback(Callback<String> callback);

    void setOnDiagramNodeSelectionCallback(Callback<String> callback);

    void hideNodeActions();

    void setNodeBadges(Map<String, Long> badges);

    void onShow();
}
