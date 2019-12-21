package eu.waziup.app.ui.device;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.verify;

public class DevicePresenterTest {

    @Mock
    DevicesMvpView mMockDevicesMvpView;

    @Mock
    DataManager mMockDataManager;

    private DevicesPresenter<DevicesMvpView> mSensorPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);

        // presenter
        mSensorPresenter = new DevicesPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mSensorPresenter.onAttach(mMockDevicesMvpView);
    }

    @After
    public void tearDown() throws Exception {
        // need to remove the presenter
        mSensorPresenter.onDetach();
    }

    @Test
    public void onLogOutClicked() throws Exception{

    }

    @Test
    public void loadSensors() throws Exception{
    }
}