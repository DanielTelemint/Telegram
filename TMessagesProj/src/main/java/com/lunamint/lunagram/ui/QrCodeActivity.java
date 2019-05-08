package com.lunamint.lunagram.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.lunamint.lunagram.R;
import com.lunamint.lunagram.ui.component.LunagramBaseActivity;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class QrCodeActivity extends LunagramBaseActivity {

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(LocaleController.getString("qrCode", R.string.qrCode));

        address = getIntent().getStringExtra("address");

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        setContentView(mainLayout);

        ImageView qrcodeImageview = new ImageView(this);
        mainLayout.addView(qrcodeImageview, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, 0, 52, 0, 0));

        Bitmap qrcodeImage = getQrcodeImage(AndroidUtilities.dp(156));
        if (qrcodeImage != null) qrcodeImageview.setImageBitmap(qrcodeImage);

        TextView addressTextview = new TextView(this);
        addressTextview.setGravity(Gravity.CENTER);
        addressTextview.setTextSize(0, AndroidUtilities.dp(14));
        addressTextview.setTextColor(ActivityCompat.getColor(this, R.color.charcoal));
        addressTextview.setText(address);
        mainLayout.addView(addressTextview, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 48, 24, 48, 0));
    }

    private Bitmap getQrcodeImage(int size) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(address, BarcodeFormat.QR_CODE, size, size);
        } catch (Exception e) {
            return null;
        }
    }

}