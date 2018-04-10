/******************************************************************************
 * Copyright © 2015-7532 NOX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to NOX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.socket;

import com.nepolix.misha.commons.Base64;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Behrooz Shahriari
 * @since 5/27/17
 */
public
class SocketChannel
{
	 
	 /**
		* HEY PEOPLE DO NOT FUCKING TOUCH THIS MOTHER FUCKING VARIABLE.<br> YOU WILL BREAK THE PLATFORM.<br> AGAIN, ASSHOLES, DO NOT TOUCH IT. DO NOT EVEN THINK ABOUT IT.<br> YOU DO NOT NEED TO
		* UNDERSTAND
		* IT.<br>
		*/
	 private static final int TIME_OUT = 5000;
	 
	 private String ip;
	 
	 private int port;
	 
	 private BufferedReader inputStream;
	 
	 private BufferedWriter outputStream;

//	 private InputStream inputStream;
//
//	 private OutputStream outputStream;
	 
	 private Socket socket;
	 
	 private int timeOut;
	 
	 /**
		* is TRUE only if not from ServerSocket
		*/
	 private boolean tryReconnect;
	 
	 public
	 SocketChannel ( String ip ,
									 int port ,
									 int timeOut )
					 throws
					 ConnectException
	 {
			
			if ( timeOut < 0 ) timeOut = TIME_OUT;
			init1 ( ip , port , timeOut );
	 }
	 
	 /**
		* @param socket
		* @param timeOut
		* 				for timeOut < 0, it will be set to a default value of TIME_OUT = 5s
		*
		* @throws IOException
		*/
	 public
	 SocketChannel ( Socket socket ,
									 int timeOut )
					 throws
					 IOException
	 {
			
			if ( timeOut < 0 ) timeOut = TIME_OUT;
			init2 ( socket , timeOut );
	 }
	 
	 public
	 SocketChannel ( Socket socket )
					 throws
					 IOException
	 {
			
			init2 ( socket , TIME_OUT );
	 }
	 
	 private
	 void init1 ( String ip ,
								int port ,
								int timeOut )
					 throws
					 ConnectException
	 {
			
			tryReconnect = true;
			this.ip = ip;
			this.port = port;
			this.timeOut = timeOut;
			initChannel ( timeOut );
	 }
	 
	 private
	 void init2 ( Socket socket ,
								int timeOut )
					 throws
					 IOException
	 {
			
			this.timeOut = timeOut;
			tryReconnect = false;
			socket.setTcpNoDelay ( true );
			socket.setSoTimeout ( timeOut );
			this.socket = socket;
			ip = socket.getInetAddress ( ).getHostAddress ( );
			inputStream = new BufferedReader ( new InputStreamReader ( socket.getInputStream ( ) ) );
			outputStream = new BufferedWriter ( new OutputStreamWriter ( socket.getOutputStream ( ) ) );
//			inputStream = socket.getInputStream ( );
//			outputStream = socket.getOutputStream ( );
	 }
	 
	 private
	 void initChannel ( int timeOut )
					 throws
					 ConnectException
	 {
			
			if ( tryReconnect )
			{
				 try
				 {
						socket.close ( );
				 }
				 catch ( Exception ignored )
				 {
				 }
				 try
				 {
						inputStream.close ( );
				 }
				 catch ( Exception ignored )
				 {
				 }
				 try
				 {
						outputStream.close ( );
				 }
				 catch ( Exception ignored )
				 {
				 }
				 try
				 {
						long t = System.currentTimeMillis ( );
						this.socket = new Socket ( ip , port );
						socket.setTcpNoDelay ( true );
						socket.setSoTimeout ( timeOut );
						inputStream = new BufferedReader ( new InputStreamReader ( socket.getInputStream ( ) ) );
						outputStream = new BufferedWriter ( new OutputStreamWriter ( socket.getOutputStream ( ) ) );
//						inputStream = socket.getInputStream ( );
//						outputStream = socket.getOutputStream ( );
						System.out.println ( "Socket-Channel init:" + socket.getInetAddress ( ).toString ( ) + "   " + ( System.currentTimeMillis ( ) - t ) );
				 }
				 catch ( IOException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 public
	 InetAddress getInetAddress ( )
	 {
			
			return socket.getInetAddress ( );
	 }
	 
	 public synchronized
	 String readMessage ( )
					 throws
					 Exception
	 {
			
			synchronized ( this )
			{
//				 String message;
//				 try
//				 {
//						StringBuffer buffer = new StringBuffer ( );
//						int          d;
//						while ( ( d = inputStream.read ( ) ) != -1 )
//						{
//							 char c = ( char ) d;
//							 if ( c == '\n' ) break;
//							 else buffer.append ( c );
//						}
//						message = buffer.toString ( );
//						message = new String ( Base64.decodeBase64 ( message ) );
//				 }
//				 catch ( IOException e )
//				 {
//						throw e;
//				 }
//				 return message;
				 String message;
				 try
				 {
						message = inputStream.readLine ( );
						try
						{
							 message = new String ( Base64.decodeBase64 ( message ) );
						}
						catch ( NullPointerException np )
						{
							 return message;
						}
				 }
				 catch ( Exception e )
				 {
						throw e;
				 }
				 return message;
			}
	 }
	 
	 
	 public synchronized
	 void writeMessage ( String message )
					 throws
					 Exception
	 {
			
			synchronized ( this )
			{
//				 StringBuffer buffer = new StringBuffer ( Base64.toBase64 ( message.getBytes ( ) ) );
//				 buffer.append ( "\n" );
//				 byte[] bs = buffer.toString ( ).getBytes ( );
//				 try
//				 {
//						streamWrite ( bs );
//				 }
//				 catch ( Exception e )
//				 {
//						initChannel ( timeOut );
//						try
//						{
//							 streamWrite ( bs );
//						}
//						catch ( Exception e1 )
//						{
//							 throw e1;
//						}
//				 }
				 String x = Base64.toBase64 ( message.getBytes ( ) ) + "\n";
				 try
				 {
						outputStream.write ( x );
						outputStream.flush ( );
				 }
				 catch ( Exception e )
				 {
						try
						{
							 initChannel ( timeOut );
							 outputStream.write ( x );
							 outputStream.flush ( );
						}
						catch ( Exception e1 )
						{
							 throw e1;
						}
				 }
			}
	 }
	 
	 private
	 void streamWrite ( byte[] bytes )
					 throws
					 IOException
	 {
			
			int c = 0;
			for ( int i = 0 ; i < bytes.length ; ++i )
			{
				 outputStream.write ( bytes[ i ] );
				 c++;
				 if ( c == 1024 )
				 {
						outputStream.flush ( );
						c = 0;
				 }
			}
			outputStream.flush ( );
	 }
	 
	 
	 public
	 boolean isConnected ( )
	 {
			
			return socket.isConnected ( );
	 }
	 
	 public
	 void close ( )
	 {
			
			try
			{
				 inputStream.close ( );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
			try
			{
				 outputStream.close ( );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
			try
			{
				 socket.close ( );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 public
	 String getIp ( )
	 {
			
			return ip;
	 }
	 
	 public
	 int getPort ( )
	 {
			
			return port;
	 }
	 
	 public
	 void setTimeOut ( int timeOut )
	 {
			
			try
			{
				 int mTO = TIME_OUT + TIME_OUT / 3;
				 int to  = timeOut < 0 ? 200 : timeOut;
				 to = to > mTO ? mTO : to;
				 socket.setSoTimeout ( to );
			}
			catch ( SocketException e )
			{
				 e.printStackTrace ( );
			}
	 }
}
