package com.example.amangalore.drain;

import java.util.concurrent.atomic.AtomicReference;
import android.util.Log;
import android.os.Vibrator;


public class BallModel {

    private final float pixelsPerMeter = 10;

    private final int ballRadius;


    public float ballPixelX, ballPixelY;

    private int pixelWidth, pixelHeight;


    private float velocityX, velocityY;


    private float accelX, accelY;


    private static final float rebound = 0.6f;


    private static final float STOP_BOUNCING_VELOCITY = 2f;

    private volatile long lastTimeMs = -1;

    public final Object LOCK = new Object();

    private AtomicReference<Vibrator> vibratorRef =
            new AtomicReference<Vibrator>();

    public BallModel(int ballRadius) {
        this.ballRadius = ballRadius;
    }

    public void setAccel(float ax, float ay) {
        synchronized (LOCK) {
            this.accelX = ax;
            this.accelY = ay;
        }
    }

    public void setSize(int width, int height) {
        synchronized (LOCK) {
            this.pixelWidth = width;
            this.pixelHeight = height;
        }
    }

    public int getBallRadius() {
        return ballRadius;
    }


    public void moveBall(int ballX, int ballY) {
        synchronized (LOCK) {
            this.ballPixelX = ballX;
            this.ballPixelY = ballY;
            velocityX = 0;
            velocityY = 0;
        }
    }

    public void updatePhysics() {

        float lWidth, lHeight, lBallX, lBallY, lAx, lAy, lVx, lVy;
        synchronized (LOCK) {
            lWidth = pixelWidth;
            lHeight = pixelHeight;
            lBallX = ballPixelX;
            lBallY = ballPixelY;
            lVx = velocityX;
            lVy = velocityY;
            lAx = accelX;
            lAy = -accelY;
        }


        if (lWidth <= 0 || lHeight <= 0) {

            return;
        }


        long curTime = System.currentTimeMillis();
        if (lastTimeMs < 0) {
            lastTimeMs = curTime;
            return;
        }

        long elapsedMs = curTime - lastTimeMs;
        lastTimeMs = curTime;

        lVx += ((elapsedMs * lAx) / 1000) * pixelsPerMeter;
        lVy += ((elapsedMs * lAy) / 1000) * pixelsPerMeter;


        lBallX += ((lVx * elapsedMs) / 1000) * pixelsPerMeter;
        lBallY += ((lVy * elapsedMs) / 1000) * pixelsPerMeter;

        boolean bouncedX = false;
        boolean bouncedY = false;

        if ((lBallY > 1580) && (lBallY < 1620) && (lBallX > 530) && (lBallX < 570)){
            ballPixelX = 550;
            ballPixelY = 200;
            lBallX = ballPixelX;
            lBallY = ballPixelY;
            velocityX = 0;
            velocityY = 0;
            lVx = velocityX;
            lVy = velocityY;
        }

        if ((lBallY > 630) && (lBallY < 650) && (lBallX > 0) && (lBallX < 700)){
            lBallY = 630;
            lVy = -lVy * rebound;
            bouncedY = true;
        }

        if ((lBallY > 650) && (lBallY < 670) && (lBallX > 0) && (lBallX < 700)){
            lBallY = 670;
            lVy = -lVy * rebound;
            bouncedY = true;
        }

        if ((lBallY > 1230) && (lBallY < 1250) && (lBallX > 450) && (lBallX < 1100)){
            lBallY = 1230;
            lVy = -lVy * rebound;
            bouncedY = true;
        }

        if ((lBallY > 1250) && (lBallY < 1270) && (lBallX > 450) && (lBallX < 1100)){
            lBallY = 1270;
            lVy = -lVy * rebound;
            bouncedY = true;
        }

        if (lBallY - ballRadius < 0) {
            lBallY = ballRadius;
            lVy = -lVy * rebound;
            bouncedY = true;
        } else if (lBallY + ballRadius > lHeight) {
            lBallY = lHeight - ballRadius;
            lVy = -lVy * rebound;
            bouncedY = true;
        }
        if (bouncedY && Math.abs(lVy) < STOP_BOUNCING_VELOCITY) {
            lVy = 0;
            bouncedY = false;
        }

        if (lBallX - ballRadius < 0) {
            lBallX = ballRadius;
            lVx = -lVx * rebound;
            bouncedX = true;
        } else if (lBallX + ballRadius > lWidth) {
            lBallX = lWidth - ballRadius;
            lVx = -lVx * rebound;
            bouncedX = true;
        }
        if (bouncedX && Math.abs(lVx) < STOP_BOUNCING_VELOCITY) {
            lVx = 0;
            bouncedX = false;
        }

        synchronized (LOCK) {
            ballPixelX = lBallX;
            ballPixelY = lBallY;

            velocityX = lVx;
            velocityY = lVy;
        }

        if (bouncedX || bouncedY) {
            Vibrator v = vibratorRef.get();
            if (v != null) {
                v.vibrate(20L);
            }
        }
    }

    public void setVibrator(Vibrator v) {
        vibratorRef.set(v);
    }
}
