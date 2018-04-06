/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.remoteableclasses;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.DeploymentsSectionPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionView;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.uberfire.client.promise.Promises;

@Dependent
public class DeploymentsRemoteableClassesPresenter extends Section<DeploymentDescriptorModel> {

    @Inject
    private DeploymentsRemoteableClassesView view;

    @Inject
    private RemoteableClassesListPresenter remoteableClassesListPresenter;

    @Inject
    private AddSingleValueModal addRemoteableClassModal;

    @Inject
    public DeploymentsRemoteableClassesPresenter(final Event<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent,
                                                 final MenuItem<DeploymentDescriptorModel> menuItem,
                                                 final Promises promises) {

        super(settingsSectionChangeEvent, menuItem, promises);
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    @Override
    public Promise<Void> setup(final DeploymentDescriptorModel model) {
        addRemoteableClassModal.setup(LibraryConstants.AddRemoteableClass, LibraryConstants.Class);

        if (model.getRemotableClasses() == null) {
            model.setRemotableClasses(new ArrayList<>());
        }

        remoteableClassesListPresenter.setup(
                view.getRemoteableClassesTable(),
                model.getRemotableClasses(),
                (clazz, presenter) -> presenter.setup(clazz, this));

        return promises.resolve();
    }

    public void openNewRemoteableClassModal() {
        addRemoteableClassModal.show(this::addRemoteableClass);
    }

    void addRemoteableClass(final String role) {
        remoteableClassesListPresenter.add(role);
        fireChangeEvent();
    }

    @Override
    public SectionView getView() {
        return view;
    }

    @Override
    public int currentHashCode() {
        return remoteableClassesListPresenter.getObjectsList().hashCode();
    }

    @Dependent
    public static class RemoteableClassesListPresenter extends ListPresenter<String, RemoteableClassListItemPresenter> {

        @Inject
        public RemoteableClassesListPresenter(final ManagedInstance<RemoteableClassListItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
