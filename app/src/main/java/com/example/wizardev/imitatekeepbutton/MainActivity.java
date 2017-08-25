package com.example.wizardev.imitatekeepbutton;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.wizardev.imitatekeepbutton.view.ImitateKeepButton;

public class MainActivity extends AppCompatActivity {
    private ImitateKeepButton imitateKeepButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imitateKeepButton = (ImitateKeepButton) findViewById(R.id.imitateKeepButton);
        imitateKeepButton.setOnViewClick(new ImitateKeepButton.OnViewClick() {
            @Override
            public void onFinish(View view) {
                Toast.makeText(MainActivity.this,"签到完成！",Toast.LENGTH_SHORT).show();
            //    imitateKeepButton.setCircleColor(Color.GRAY);
                imitateKeepButton.setContentText("完成");
               // imitateKeepButton.setEnabled(false);
            }
        });
    }
}
