/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charlware.rocketlauncher;

import com.charlware.rocketlauncher.device.DreamCheeky;

/**
 *
 * @author charlvj
 */
public class RLTest {
    public static void main(String[] args) throws Exception {
        RocketLauncher rl = new RocketLauncher(new DreamCheeky());
        try {
            rl.open();
            rl.sendCommand(Command.DOWN);
            Thread.sleep(500);
            rl.sendCommand(Command.UP);
        }
        catch(Exception e) {
            System.out.println("Error: " + e);
        }
        finally {
            rl.close();
        }
    }
}
