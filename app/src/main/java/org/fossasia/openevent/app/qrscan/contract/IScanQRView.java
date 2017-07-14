package org.fossasia.openevent.app.qrscan.contract;

import org.fossasia.openevent.app.common.contract.view.Progressive;
import org.fossasia.openevent.app.data.models.Attendee;

import io.reactivex.annotations.NonNull;

public interface IScanQRView extends Progressive {

    boolean hasCameraPermission();

    void requestCameraPermission();

    void showPermissionError(String error);

    void onScannedAttendee(Attendee attendee);

    void showBarcodePanel(boolean show);

    void showBarcodeData(@NonNull String data);

    void loadCamera();

    void startScan();

    void stopScan();

}
