package com.chatchat.opengl02;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenGLView view = new OpenGLView(this);
        setContentView(view);
    }
}
