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

package org.jbpm.workbench.wi.client.editors.deployment.descriptor;

import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.DeploymentsSections;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.eventlisteners.DeploymentsEventListenersPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.globals.DeploymentsGlobalsPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.marshallingstrategies.DeploymentsMarshallingStrategiesPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.remoteableclasses.DeploymentsRemoteableClassesPresenter;
import org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.requiredroles.DeploymentsRequiredRolesPresenter;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.jbpm.workbench.wi.dd.service.DDEditorService;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.sections.Section;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionManager;
import org.kie.workbench.common.screens.library.client.settings.sections.SectionView;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.client.promise.Promises;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.workbench.events.NotificationEvent.NotificationType.WARNING;

@Dependent
public class DeploymentsSectionPresenter extends Section<ProjectScreenModel> {

    private final View view;

    private final WorkspaceProjectContext projectContext;
    private final Caller<DDEditorService> ddEditorService;
    private final ManagedInstance<ObservablePath> observablePaths;
    private final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;
    private final Event<NotificationEvent> notificationEvent;

    private ObservablePath pathToDeploymentsXml;
    ObservablePath.OnConcurrentUpdateEvent concurrentDeploymentsXmlUpdateInfo;
    DeploymentDescriptorModel model;

    @Inject
    private SectionManager<DeploymentDescriptorModel> sectionManager;

    @Inject
    private DeploymentsSections deploymentsSections;

    public interface View extends SectionView<DeploymentsSectionPresenter> {

        String getConcurrentUpdateMessage();

        HTMLElement getMenuItemsContainer();

        HTMLElement getContentContainer();
    }

    @Inject
    public DeploymentsSectionPresenter(final View view,
                                       final Promises promises,
                                       final MenuItem<ProjectScreenModel> menuItem,
                                       final WorkspaceProjectContext projectContext,
                                       final Caller<DDEditorService> ddEditorService,
                                       final ManagedInstance<ObservablePath> observablePaths,
                                       final Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent,
                                       Event<NotificationEvent> notificationEvent) {

        super(settingsSectionChangeEvent, menuItem, promises);
        this.view = view;
        this.projectContext = projectContext;
        this.ddEditorService = ddEditorService;
        this.observablePaths = observablePaths;
        this.settingsSectionChangeEvent = settingsSectionChangeEvent;
        this.notificationEvent = notificationEvent;
    }

    @PostConstruct
    public void init() {

        //FIXME: urgh
        deploymentsSections.getList().forEach(s -> {

            if (s instanceof DeploymentsMarshallingStrategiesPresenter) {
                ((DeploymentsMarshallingStrategiesPresenter) s).setParentPresenter(this);
            }

            if (s instanceof DeploymentsGlobalsPresenter) {
                ((DeploymentsGlobalsPresenter) s).setParentPresenter(this);
            }

            if (s instanceof DeploymentsRequiredRolesPresenter) {
                ((DeploymentsRequiredRolesPresenter) s).setParentPresenter(this);
            }

            if (s instanceof DeploymentsEventListenersPresenter) {
                ((DeploymentsEventListenersPresenter) s).setParentPresenter(this);
            }

            if (s instanceof DeploymentsRemoteableClassesPresenter) {
                ((DeploymentsRemoteableClassesPresenter) s).setParentPresenter(this);
            }
        });

        sectionManager.init(deploymentsSections.getList(),
                            view.getMenuItemsContainer(),
                            view.getContentContainer());
    }

    @Override
    public Promise<Void> setup(final ProjectScreenModel ignore) {

        view.init(this);

        final String deploymentsXmlUri = projectContext.getActiveWorkspaceProject().get()
                .getRootPath().toURI() + "src/main/resources/META-INF/kie-deployment-descriptor.xml";

        pathToDeploymentsXml = observablePaths.get().wrap(PathFactory.newPath(
                "kie-deployment-descriptor.xml",
                deploymentsXmlUri));

        concurrentDeploymentsXmlUpdateInfo = null;
        pathToDeploymentsXml.onConcurrentUpdate(info -> concurrentDeploymentsXmlUpdateInfo = info);

        return createIfNotExists().then(i -> loadDeploymentDescriptor()).then(model -> {
            this.model = model;
            //FIXME: generics issue
            final Promise<Void> all = promises.all(deploymentsSections.getList(), (final Section<DeploymentDescriptorModel> section) -> section.setup(model));
            return all;
        }).then(i -> {
            sectionManager.resetAllDirtyIndicators();
            return sectionManager.goTo(sectionManager.getCurrentSection());
        });
    }

    Promise<DeploymentDescriptorModel> loadDeploymentDescriptor() {
        return promises.promisify(ddEditorService, s -> {
            s.load(pathToDeploymentsXml);
        });
    }

    Promise<Void> createIfNotExists() {
        return promises.promisify(ddEditorService, s -> {
            s.createIfNotExists(pathToDeploymentsXml);
        });
    }

    @Override
    public Promise<Void> save(final String comment,
                              final Supplier<Promise<Void>> chain) {

        if (concurrentDeploymentsXmlUpdateInfo != null) {
            notificationEvent.fire(new NotificationEvent(view.getConcurrentUpdateMessage(), WARNING));
            return setup(null);
        }

        return save(comment).then(i -> {
            sectionManager.resetAllDirtyIndicators();
            return promises.resolve();
        });
    }

    Promise<Void> save(final String comment) {
        return promises.promisify(ddEditorService, s -> {
            s.save(pathToDeploymentsXml, model, model.getOverview().getMetadata(), comment);
        });
    }

    public void onSectionChanged(@Observes final SettingsSectionChange<DeploymentDescriptorModel> settingsSectionChange) {

        if (!sectionManager.getSections().contains(settingsSectionChange.getSection())) {
            return;
        }

        sectionManager.updateDirtyIndicator(settingsSectionChange.getSection());
        fireChangeEvent();
    }

    @Override
    public int currentHashCode() {
        return model.hashCode();
    }

    @Override
    public SectionView getView() {
        return view;
    }
}
