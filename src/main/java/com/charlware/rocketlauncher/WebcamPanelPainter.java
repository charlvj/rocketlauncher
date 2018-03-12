/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charlware.rocketlauncher;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 *
 * @author charlvj
 */
public class WebcamPanelPainter implements WebcamPanel.Painter {

    private Webcam webcam;
    private WebcamPanel.Painter defaultPainter;
    private FaceTracker faceTracker;
    
    
    private static final Stroke STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 1.0f }, 0.0f);

    public WebcamPanelPainter(Webcam webcam, WebcamPanel panel, FaceTracker faceTracker) {
        this.defaultPainter = panel.getDefaultPainter();
        this.webcam = webcam; 
        this.faceTracker = faceTracker;
    }

    @Override
    public void paintPanel(WebcamPanel pnl, Graphics2D gd) {
        if (defaultPainter != null) {
            defaultPainter.paintPanel(pnl, gd);
        }
    }

    @Override
    public void paintImage(WebcamPanel panel, BufferedImage bi, Graphics2D gd) {
        if (defaultPainter != null) {
            defaultPainter.paintImage(panel, bi, gd);
        }

        List<DetectedFace> faces = faceTracker.getFaces();
        if(faces != null) {
            for (DetectedFace face : faces) {
                Rectangle bounds = face.getBounds();
                int dx = (int) (0.1 * bounds.width);
                int dy = (int) (0.2 * bounds.height);
                int x = (int) bounds.x - dx;
                int y = (int) bounds.y - dy;
                int w = (int) bounds.width + 2 * dx;
                int h = (int) bounds.height + dy;

                gd.setStroke(STROKE);
                gd.setColor(Color.RED);
                gd.drawRect(x, y, w, h);

            }
        }
    }
}