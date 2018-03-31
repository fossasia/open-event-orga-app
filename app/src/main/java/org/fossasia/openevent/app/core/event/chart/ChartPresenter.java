package org.fossasia.openevent.app.core.event.chart;

import org.fossasia.openevent.app.common.mvp.presenter.BaseDetailPresenter;
import org.fossasia.openevent.app.common.rx.Logger;
import org.fossasia.openevent.app.core.event.dashboard.analyser.ChartAnalyser;

import javax.inject.Inject;

import static org.fossasia.openevent.app.common.rx.ViewTransformers.disposeCompletable;
import static org.fossasia.openevent.app.common.rx.ViewTransformers.progressiveErroneousCompletable;

public class ChartPresenter extends BaseDetailPresenter<Long, IChartView> {

    private final ChartAnalyser chartAnalyser;

    @Inject
    public ChartPresenter(ChartAnalyser chartAnalyser) {
        this.chartAnalyser = chartAnalyser;
    }

    @Override
    public void start() {
        loadChart();
    }

    public void loadChart() {
        chartAnalyser.loadData(getId())
            .compose(disposeCompletable(getDisposable()))
            .compose(progressiveErroneousCompletable(getView()))
            .subscribe(() -> chartAnalyser.showChart(getView().getChartView()), Logger::logError);
    }
}
