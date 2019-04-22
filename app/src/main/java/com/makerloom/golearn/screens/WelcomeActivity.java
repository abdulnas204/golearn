package com.makerloom.golearn.screens;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.makerloom.common.activity.MyPlainToolbarActivity;
import com.makerloom.golearn.R;
import com.makerloom.golearn.utils.Commons;
import com.makerloom.golearn.utils.NetworkUtils;

import androidx.annotation.Nullable;
import mehdi.sakout.fancybuttons.FancyButton;

public class WelcomeActivity extends MyPlainToolbarActivity {
    private TextView mottoTV;

    private FancyButton signInBtn;

    private FancyButton getInfoBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        signInBtn = findViewById(R.id.signin_btn);
        signInBtn.getTextViewObject().setTypeface(Typeface.DEFAULT_BOLD);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Commons.hasConnection(WelcomeActivity.this)) {
                    startActivity(new Intent(WelcomeActivity.this, AuthActivity.class));
                }
                else {
                    NetworkUtils.Companion.showConnectionErrorMessage(WelcomeActivity.this, "sign in");
                }
            }
        });
    }


}
