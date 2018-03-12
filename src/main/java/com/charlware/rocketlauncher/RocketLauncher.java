/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.charlware.rocketlauncher;

import com.charlware.rocketlauncher.device.RocketLauncherDevice;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 *
 * @author charlvj
 */
public class RocketLauncher implements Closeable {

    private RocketLauncherDevice rocketLauncherDevice;
    private Context context;
    private Device device;
    private DeviceHandle deviceHandle;
    private boolean kernelDriverAttached = false;
    private Command currentCommand = Command.STOP;

    public RocketLauncher(RocketLauncherDevice rlDevice) {
        this.rocketLauncherDevice = rlDevice;
        this.context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Could not initialize LibUsb.", result);
        }
    }

    public boolean open() {
        device = findDevice();
        deviceHandle = new DeviceHandle();
        int result = LibUsb.open(device, deviceHandle);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Could not open device.", result);
        }
        claimInterface();
        return true;
    }

    public boolean isOpen() {
        return deviceHandle != null;
    }

    @Override
    public void close() throws IOException {
        int result = LibUsb.releaseInterface(deviceHandle, 0);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to release interface", result);
        }

        if (kernelDriverAttached) {
            result = LibUsb.attachKernelDriver(deviceHandle, 0);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to re-attach kernel driver", result);
            }
        }
        LibUsb.close(deviceHandle);
        deviceHandle = null;
    }

    private Device findDevice() {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                if (descriptor.idVendor() == rocketLauncherDevice.getVendorId()
                        && descriptor.idProduct() == rocketLauncherDevice.getProductId()) {
                    return device;
                }
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }

    private void claimInterface() {
//        kernelDriverAttached = LibUsb.hasCapability(LibUsb.CAP_SUPPORTS_DETACH_KERNEL_DRIVER)
//                && LibUsb.kernelDriverActive(deviceHandle, 0) > 0;
        kernelDriverAttached = LibUsb.kernelDriverActive(deviceHandle, 0) > 0;

        // Detach the kernel driver
        if (kernelDriverAttached) {
            int result = LibUsb.detachKernelDriver(deviceHandle, 0);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to detach kernel driver", result);
            }
        }

        int result = LibUsb.claimInterface(deviceHandle, 0);
        if (result != LibUsb.SUCCESS) {
            throw new LibUsbException("Unable to claim interface", result);
        }
    }

    public void sendCommand(Command command) {
        if(command == currentCommand) return;
        ByteBuffer buffer = rocketLauncherDevice.translateCommand(command);
        int transfered = LibUsb.controlTransfer(deviceHandle,
                (byte) (LibUsb.REQUEST_TYPE_CLASS | LibUsb.RECIPIENT_INTERFACE),
                (byte) 0x09, 
                (short) 0, 
                (short) 0, 
                buffer, 
                100);        
        if (transfered < 0) {
            throw new LibUsbException("Control transfer failed", transfered);
        }
        currentCommand = command;
        System.out.println(transfered + " bytes sent");
    }
}
