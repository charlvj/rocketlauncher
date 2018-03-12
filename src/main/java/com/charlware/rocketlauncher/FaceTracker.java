/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charlware.rocketlauncher;

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 *
 * @author charlvj
 */
public class FaceTracker implements Runnable {
    private RocketLauncher rocketLauncher;
    private Webcam webcam;
    private AtomicReference<List<DetectedFace>> faces = new AtomicReference<>();
//    private AtomicReference<DetectedFace> lockedFace = new AtomicReference();
    private HaarCascadeDetector faceDetector = new HaarCascadeDetector();
    
    public FaceTracker(RocketLauncher rocketLauncher, Webcam webcam) {
        this.rocketLauncher = rocketLauncher;
        this.webcam = webcam;
        System.out.println("Face Tracker initialized");
    }
    
    public void start() {
        
    }
    
    public void stop() {
        
    }

    @Override
    public void run() {
        while(true) {
            if(Thread.interrupted()) return;
            if(!webcam.isOpen()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FaceTracker.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else {
//                System.out.println(".");
                faces.set(faceDetector.detectFaces(ImageUtilities.createFImage(webcam.getImage())));
                if(!faces.get().isEmpty()) {
                    gotoFace(faces.get().get(0));

                }
            }
        }
    }
    
    public List<DetectedFace> getFaces() {
        return faces.get();
    }
    
    private double getMiddle(double i, double j) {
        return i + (j - i) / 2;
    }
    
    private void gotoFace(DetectedFace face) {
        Rectangle bounds = face.getBounds();
        System.out.println("Bounds: " + bounds);
        bounds.scaleCentroid((float) 0.5);
        System.out.println("Scaled Bounds: " + bounds);
//        double x = getMiddle(bounds.x, bounds.maxX());
//        double y = getMiddle(bounds.y, bounds.maxY());
//        System.out.println("Face Middle: " + x + "," + y);
        Pair<Integer,Integer> viewCenter = getViewCenter();
        System.out.println("ViewCenter: " + viewCenter);
        int cmp = compare(bounds.x, bounds.x + bounds.width, viewCenter.getLeft());
        System.out.println("X Compare: " + cmp);
        if(cmp > 0) rocketLauncher.sendCommand(Command.LEFT);
        else if(cmp < 0) rocketLauncher.sendCommand(Command.RIGHT);
        else {
            rocketLauncher.sendCommand(Command.STOP);
       
            cmp = compare(bounds.y, bounds.y + bounds.height, viewCenter.getRight());
            System.out.println("Y Compare: " + cmp);
            if(cmp > 0) rocketLauncher.sendCommand(Command.UP);
            else if(cmp < 0) rocketLauncher.sendCommand(Command.DOWN);
            else rocketLauncher.sendCommand(Command.STOP);
        }
        
//        else if(y > viewCenterY) rocketLauncher.sendCommand(Command.DOWN);
//        else if(y < viewCenterY) rocketLauncher.sendCommand(Command.UP);
//        else rocketLauncher.sendCommand(Command.STOP);
//        try {
//            Thread.sleep(10);
//            rocketLauncher.sendCommand(Command.STOP);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(FaceTracker.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private Pair<Integer,Integer> getViewCenter() {
        Dimension viewDim = webcam.getViewSize();
        int viewCenterX = viewDim.width / 2;
        int viewCenterY = viewDim.height / 2;
        return Pair.of(viewCenterX, viewCenterY);
    }
    
    private int compare(float v1, float v2, int p) {
        if(p < v1) return -1;
        if(p > v2) return 1;
        return 0;
    }

}

