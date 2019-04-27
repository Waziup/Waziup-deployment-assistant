package eu.waziup.app.ui.login;

import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.data.network.model.LoginRequest;
import eu.waziup.app.ui.main.MainPresenter;
import eu.waziup.app.utils.rx.TestSchedulerProvider;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest {

    @Mock
    LoginMvpView mMockLoginMvpView;

    @Mock
    DataManager mMockDataManager;

    private LoginPresenter<LoginMvpView> mLoginPresenter;
    private TestScheduler mTestScheduler;

    @BeforeClass
    public static void onlyOnce() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);

        // presenter
        mLoginPresenter = new LoginPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mLoginPresenter.onAttach(mMockLoginMvpView);

    }

    @Test
    public void testServerLoginSuccess() {

//        String username = "cdupont";
//        String password = "password";
//
//        doReturn(Single.just(""))
//                .when(mMockDataManager)
//                .serverLogin(new LoginRequest
//                        .ServerLoginRequest(username, password));
//
//        mLoginPresenter.onServerLoginClick(username, password);
//
//        mTestScheduler.triggerActions();
//
//        verify(mMockLoginMvpView).showLoading();
//        verify(mMockLoginMvpView).hideLoading();
//        verify(mMockLoginMvpView).openSensorActivity();

    }

    @After
    public void tearDown() throws Exception {
        // need to remove the presenter
        mLoginPresenter.onDetach();
    }
}