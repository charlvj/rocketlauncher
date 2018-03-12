/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charlware.rocketlauncher.device;

import com.charlware.rocketlauncher.Command;
import java.nio.ByteBuffer;

/**
 *
 * @author charlvj
 */
public class DreamCheeky implements RocketLauncherDevice {

    @Override
    public int getProductId() {
        return 0x1010;
    }

    @Override
    public int getVendorId() {
        return 0x2123;
    }

    @Override
    public ByteBuffer translateCommand(Command command) {
        byte cmd = 0;
        switch(command) {
            case DOWN:  cmd = 0x01; break;
            case UP:    cmd = 0x02; break;
            case LEFT:  cmd = 0x04; break;
            case RIGHT: cmd = 0x08; break;
            case FIRE:  cmd = 0x10; break;
            case STOP:  cmd = 0x20; break;
        }
        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
        buffer.put(new byte[] {0x02, cmd, 0, 0, 0, 0, 0, 0});
        return buffer;
    }

    @Override
    public boolean supportsCommand(Command command) {
        return true;
    }
    
}
