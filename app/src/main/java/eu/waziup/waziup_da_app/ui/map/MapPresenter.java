package eu.waziup.waziup_da_app.ui.map;

import javax.inject.Inject;

import eu.waziup.waziup_da_app.data.DataManager;
import eu.waziup.waziup_da_app.ui.base.BasePresenter;
import eu.waziup.waziup_da_app.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by KidusMT.
 */

public class MapPresenter<V extends MapMvpView> extends BasePresenter<V>
        implements MapMvpPresenter<V> {

    private static final String TAG = "RegisterSensorPresenter";

    @Inject
    public MapPresenter(DataManager dataManager,
                        SchedulerProvider schedulerProvider,
                        CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

}
