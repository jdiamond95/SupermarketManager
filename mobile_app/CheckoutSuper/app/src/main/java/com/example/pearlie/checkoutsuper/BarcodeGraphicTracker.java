package com.example.pearlie.checkoutsuper;

/**
 * Created by Pearlie on 21-Sep-17.
 */

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Generic tracker which is used for tracking or reading a barcode (and can really be used for
 * any type of item).  This is used to receive newly detected items, add a graphical representation
 * to an overlay, update the graphics as the item changes, and remove the graphics when the item
 * goes away.
 */
class BarcodeGraphicTracker extends Tracker<Barcode> {

    private GraphicOverlay<BarcodeGraphic> mOverlay;
    private BarcodeGraphic mGraphic;
    private BarcodeDetectorListener mBarcodeDetectorListener;

    BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic) {
        mOverlay = overlay;
        mGraphic = graphic;
    }

    public void setDetectorListener(BarcodeDetectorListener mBarcodeDetectorListener) {
        this.mBarcodeDetectorListener = mBarcodeDetectorListener;
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, Barcode item) {
        mGraphic.setId(id);
        if(mBarcodeDetectorListener == null) return;
        mBarcodeDetectorListener.onObjectDetected(item);
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }

    interface BarcodeDetectorListener{
        /**
         * Multiple events can be fired depending the number of barcodes identified,
         * So you may want to build a Map<K,V> to add the detected objects and finish the
         * activity when the user is satisfied. @see {@link java.util.Map}
         * <br/>
         *
         * @param data Barcode parsed object will contain different kinds of data depending
         *             on the scanned barcode content.
         */
        void onObjectDetected(Barcode data);
    }
}
