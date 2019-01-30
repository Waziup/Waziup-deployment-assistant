package eu.waziup.app.ui.main;

import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.sensor.Sensor;
import eu.waziup.app.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
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
    public void onLogOutClicked() {
        mMainPresenter.onLogOutClicked();
        // have to check if loginActivity has been opened
        // todo have to check if userInformation has been cleared or not
        verify(mMockMainMvpView).openLoginActivity();
    }

    @Test
    public void onFabClicked() {
        mMainPresenter.onFabClicked();
        // have to prove that RegistrationSensorFragment have been opened
        verify(mMockMainMvpView).openRegistrationSensor();
    }

    @Test
    public void onSensorItemClicked(Sensor sensor, String parent) {
        mMainPresenter.onSensorItemClicked(sensor, parent);
        //  has to open selected Sensor detail fragment
        verify(mMockMainMvpView).openSensorDetailFragment(sensor, parent);
    }

    @After
    public void tearDown() throws Exception {
        // need to remove the presenter
        mMainPresenter.onDetach();
    }
}