package eu.waziup.app.ui.sensor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.MvpView;
import eu.waziup.app.ui.main.MainMvpView;
import eu.waziup.app.ui.main.MainPresenter;
import eu.waziup.app.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.verify;

public class SensorPresenterTest {

    @Mock
    SensorMvpView mMockSensorMvpView;

    @Mock
    DataManager mMockDataManager;

    private SensorPresenter<SensorMvpView> mSensorPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);

        // presenter
        mSensorPresenter = new SensorPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mSensorPresenter.onAttach(mMockSensorMvpView);
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