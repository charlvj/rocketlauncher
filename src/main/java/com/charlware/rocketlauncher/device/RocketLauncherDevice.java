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
public interface RocketLauncherDevice {
    int getProductId();
    int getVendorId();
    ByteBuffer translateCommand(Command command);
    boolean supportsCommand(Command command);
}
