package eu.waziup.app.ui.map;

import javax.inject.Inject;

import eu.waziup.app.data.DataManager;
import eu.waziup.app.ui.base.BasePresenter;
import eu.waziup.app.utils.CommonUtils;
import eu.waziup.app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class MapPresenter<V extends MapMvpView> extends BasePresenter<V>
        implements MapMvpPresenter<V> {

    private static final String TAG = "QRScanPresenter";

    @Inject
    public MapPresenter(DataManager dataManager,
                        SchedulerProvider schedulerProvider,
                        CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSensors() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager().fetchSensors(1000, 0)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(sensors -> {
                    if (!isViewAttached())
                        return;

                    getMvpView().showSensorsOnMap(sensors);

                }, throwable -> {

                    if (!isViewAttached())
                        return;

                    getMvpView().hideLoading();
                    getMvpView().onError(CommonUtils.getErrorMessage(throwable));
                }));
    }
}
