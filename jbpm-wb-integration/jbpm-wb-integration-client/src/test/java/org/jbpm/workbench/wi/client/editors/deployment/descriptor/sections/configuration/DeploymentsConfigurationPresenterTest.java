package org.jbpm.workbench.wi.client.editors.deployment.descriptor.sections.configuration;

import org.jbpm.workbench.wi.client.editors.deployment.descriptor.items.ItemObjectModelFactory;
import org.jbpm.workbench.wi.dd.model.DeploymentDescriptorModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.modal.doublevalue.AddDoubleValueModal;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentsConfigurationPresenterTest {

    @Mock
    private EventSourceMock<SettingsSectionChange<DeploymentDescriptorModel>> settingsSectionChangeEvent;

    @Mock
    private MenuItem<DeploymentDescriptorModel> menuItem;

    @Mock
    private DeploymentsConfigurationView view;

    @Mock
    private DeploymentsConfigurationPresenter.ConfigurationsListPresenter configurationsListPresenter;

    @Mock
    private AddDoubleValueModal addConfigurationModal;

    @Mock
    private ItemObjectModelFactory itemObjectModelFactory;

    private Promises promises = new SyncPromises();

    private DeploymentsConfigurationPresenter presenter;

    @Before
    public void before() {
        this.presenter = spy(new DeploymentsConfigurationPresenter(settingsSectionChangeEvent,
                                                                   menuItem,
                                                                   promises,
                                                                   view,
                                                                   configurationsListPresenter,
                                                                   addConfigurationModal,
                                                                   itemObjectModelFactory));
    }

    @Test
    public void testSetup() {
        final DeploymentDescriptorModel model = new DeploymentDescriptorModel();

        presenter.setup(model);

        assertNotNull(model.getConfiguration());
        verify(addConfigurationModal).setup(any(), any(), any());
        verify(configurationsListPresenter).setup(any(), eq(model.getConfiguration()), any());
    }

    @Test
    public void testOpenModal() {
        presenter.openNewConfigurationModal();
        verify(addConfigurationModal).show(any());
    }

    @Test
    public void testAdd() {
        presenter.addConfiguration("Name", "Value");
        verify(configurationsListPresenter).add(any());
        verify(presenter).fireChangeEvent();
    }
}