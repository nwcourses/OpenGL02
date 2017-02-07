package com.chatchat.opengl02;

/**
 * Created by whitelegg_n on 12/01/2016.
 */

import android.opengl.GLSurfaceView;
import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteOrder;


public class OpenGLView extends GLSurfaceView implements GLSurfaceView.Renderer {


    GPUInterface gpuInterface;
    FloatBuffer vbuf;
    float[] modelview, perspective;


    public OpenGLView (Context ctx) {
        super(ctx);
        setEGLContextClientVersion(2);
        setRenderer(this);
        modelview = new float[16];
        perspective = new float[16];
        Matrix.setIdentityM(modelview, 0);
        Matrix.setIdentityM(perspective, 0);
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);


        final String vertexShader =
                "attribute vec4 aVertex;\n" +
                        "uniform mat4 uPerspMtx, uMvMtx;\n" +
                        "void main(void)\n" +
                        "{\n"+
                        "gl_Position = uPerspMtx * uMvMtx * aVertex;\n" +
                        "}\n",
                fragmentShader =
                        "precision mediump float;\n" +
                                "uniform vec4 uColour;\n" +
                                "void main(void)\n"+
                                "{\n"+
                                "gl_FragColor = uColour;\n" +
                                "}\n";
        gpuInterface = new GPUInterface(vertexShader, fragmentShader);

        createShapes();
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        gpuInterface.select();




       // check shaders compiled properly - don't try to draw if not
       if(gpuInterface.isValid()) {

            Matrix.setIdentityM(modelview, 0);
            gpuInterface.sendMatrix(modelview, "uMvMtx");
            gpuInterface.sendMatrix(perspective, "uPerspMtx");
            gpuInterface.setUniform4fv("uColour", new float[]{1, 0, 0, 1});
            gpuInterface.drawBufferedData(vbuf, 12, "aVertex", 0, 3);
            gpuInterface.setUniform4fv("uColour", new float[]{1, 1, 0, 1});
            gpuInterface.drawBufferedData(vbuf, 12, "aVertex", 3, 3);


            Matrix.translateM(modelview, 0, -1, 0, 0);


            gpuInterface.sendMatrix(modelview, "uMvMtx");
            gpuInterface.setUniform4fv("uColour", new float[]{0, 0, 1, 1});
            gpuInterface.drawBufferedData(vbuf, 12, "aVertex", 3, 3);

           Matrix.rotateM(modelview, 0, 45, 0,0,1);

           gpuInterface.sendMatrix(modelview, "uMvMtx");
           gpuInterface.setUniform4fv("uColour", new float[]{0, 1, 0, 1});
           gpuInterface.drawBufferedData(vbuf, 12, "aVertex", 3, 3);
        }

    }

    public void onSurfaceChanged(GL10 unused, int w, int h) {


        float hFov = 40.0f;
        GLES20.glViewport(0, 0, w, h);
        float aspectRatio = (float) w / h;
        Matrix.perspectiveM(perspective, 0, hFov / aspectRatio, aspectRatio, 0.1f, 100);
    }

    private void createShapes() {

        // this seemed to crash first time in onDrawFrame(), now seems to work
        // better off going in onSurfaceCreated() anyway, more efficient
        float[] vertices = { 0,0,-3, 1,0,-3, 0.5f, 1, -3, -0.5f,0,-6, 0.5f,0,-6, 0, 1, -6 };


        ByteBuffer vbuf0 = ByteBuffer.allocateDirect(vertices.length * Float.SIZE);
        vbuf0.order(ByteOrder.nativeOrder());
        vbuf = vbuf0.asFloatBuffer();
        vbuf.put(vertices);
        vbuf.position(0);

    }
}
