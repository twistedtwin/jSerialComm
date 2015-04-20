/*
 * SerialPortTest.java
 *
 *       Created on:  Feb 27, 2015
 *  Last Updated on:  Mar 12, 2015
 *           Author:  Will Hedgecock
 *
 * Copyright (C) 2012-2015 Fazecast, Inc.
 *
 * This file is part of jSerialComm.
 *
 * jSerialComm is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * jSerialComm is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with jSerialComm.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.fazecast.jSerialComm;

import java.io.InputStream;
import java.util.Scanner;

/**
 * This class provides a test case for the jSerialComm library.
 * 
 * @author Will Hedgecock &lt;will.hedgecock@gmail.com&gt;
 * @version 1.0
 * @see java.io.InputStream
 * @see java.io.OutputStream
 */
public class SerialPortTest
{
	private static final class PacketListener implements SerialPortPacketListener
	{
		@Override
		public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
		@Override
		public void serialEvent(SerialPortEvent event)
		{
			byte[] newData = event.getReceivedData();
			System.out.println("Received data of size: " + newData.length);
			for (int i = 0; i < newData.length; ++i)
				System.out.print((char)newData[i]);
			System.out.println("\n");
		}
		@Override
		public int getPacketSize() { return 100; }
	}
	
	static public void main(String[] args)
	{
		SerialPort[] ports = SerialPort.getCommPorts();
		System.out.println("\nAvailable Ports:\n");
		for (int i = 0; i < ports.length; ++i)
			System.out.println("   " + ports[i].getSystemPortName() + ": " + ports[i].getDescriptivePortName());
		System.out.print("\nChoose your desired serial port: ");
		int serialPortChoice = 0;
		try { serialPortChoice = (new Scanner(System.in)).nextInt(); } catch (Exception e) {}
		SerialPort ubxPort = ports[serialPortChoice];
		byte[] readBuffer = new byte[2048];
		
		System.out.println("\nOpening " + ubxPort.getDescriptivePortName() + ": " + ubxPort.openPort());
		System.out.println("Setting read timeout mode to non-blocking");
		ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1000, 0);
		try
		{
			for (int i = 0; i < 3; ++i)
			{
				System.out.println("\nReading #" + i);
				System.out.println("Available: " + ubxPort.bytesAvailable());
				int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		} catch (Exception e) { e.printStackTrace(); }
		System.out.println("\nSetting read timeout mode to semi-blocking with a timeout of 200ms");
		ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);
		try
		{
			for (int i = 0; i < 3; ++i)
			{
				System.out.println("\nReading #" + i);
				System.out.println("Available: " + ubxPort.bytesAvailable());
				int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		} catch (Exception e) { e.printStackTrace(); }
		System.out.println("\nSetting read timeout mode to semi-blocking with no timeout");
		ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		try
		{
			for (int i = 0; i < 3; ++i)
			{
				System.out.println("\nReading #" + i);
				System.out.println("Available: " + ubxPort.bytesAvailable());
				int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		} catch (Exception e) { e.printStackTrace(); }
		System.out.println("\nSetting read timeout mode to blocking with a timeout of 100ms");
		ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
		try
		{
			for (int i = 0; i < 3; ++i)
			{
				System.out.println("\nReading #" + i);
				System.out.println("Available: " + ubxPort.bytesAvailable());
				int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		} catch (Exception e) { e.printStackTrace(); }
		System.out.println("\nSetting read timeout mode to blocking with no timeout");
		ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);
		try
		{
			for (int i = 0; i < 3; ++i)
			{
				System.out.println("\nReading #" + i);
				System.out.println("Available: " + ubxPort.bytesAvailable());
				int numRead = ubxPort.readBytes(readBuffer, readBuffer.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		} catch (Exception e) { e.printStackTrace(); }
		System.out.println("\nSwitching over to event-based reading");
		System.out.println("\nListening for any amount of data available\n");
		ubxPort.addDataListener(new SerialPortDataListener() {
			@Override
			public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
			@Override
			public void serialEvent(SerialPortEvent event)
			{
				SerialPort comPort = event.getSerialPort();
				byte[] newData = new byte[comPort.bytesAvailable()];
				int numRead = comPort.readBytes(newData, newData.length);
				System.out.println("Read " + numRead + " bytes.");
			}
		});
		try { Thread.sleep(5000); } catch (Exception e) {}
		ubxPort.removeDataListener();
		System.out.println("\nNow listening for full 100-byte data packets\n");
		PacketListener listener = new PacketListener();
		ubxPort.addDataListener(listener);
		try { Thread.sleep(5000); } catch (Exception e) {}
		System.out.println("\n\nClosing " + ubxPort.getDescriptivePortName() + ": " + ubxPort.closePort());
		ubxPort.removeDataListener();
		try { Thread.sleep(1000); } catch (InterruptedException e1) { e1.printStackTrace(); }
		System.out.println("Reopening " + ubxPort.getDescriptivePortName() + ": " + ubxPort.openPort() + "\n");
		ubxPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
		InputStream in = ubxPort.getInputStream();
		try
		{
			for (int j = 0; j < 1000; ++j)
				System.out.print((char)in.read());
			in.close();
		} catch (Exception e) { e.printStackTrace(); }
		
		System.out.println("\n\nClosing " + ubxPort.getDescriptivePortName() + ": " + ubxPort.closePort());
	}
}