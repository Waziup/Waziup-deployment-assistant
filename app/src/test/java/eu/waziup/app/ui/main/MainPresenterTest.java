package eu.waziup.app.ui.main;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.ui.device.DevicesFragment;
import eu.waziup.app.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock
    MainMvpView mMockMainMvpView;

    @Mock
    DataManager mMockDataManager;

    private MainPresenter<MainMvpView> mMainPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);

        // presenter
        mMainPresenter = new MainPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mMainPresenter.onAttach(mMockMainMvpView);

    }

    @Test
    public void onLogOutClicked() throws Exception {
        mMainPresenter.onLogOutClicked();
        // have to check if loginActivity has been opened
        // todo have to check if userInformation has been cleared or not
        verify(mMockMainMvpView).openLoginActivity();
    }

    @Test
    public void onFabClicked() throws Exception {
        mMainPresenter.onFabClicked();
        // have to prove that RegistrationSensorFragment have been opened
        verify(mMockMainMvpView).openRegistrationSensor();
    }

    @Test
    public void onSensorItemClicked() throws Exception{
        // todo check if this is the right way of doing it.
        Sensor sensor = new Sensor(
                new Random(4).toString(), "APTD-123-asd", "Hawassa", "VISIBLE");
        mMainPresenter.onSensorItemClicked(sensor, DevicesFragment.TAG);

        // has to open selected Sensor detail fragment
        verify(mMockMainMvpView).openSensorDetailFragment(sensor, DevicesFragment.TAG);
    }

    @After
    public void tearDown() throws Exception {
        // need to remove the presenter
        mMainPresenter.onDetach();
    }
}