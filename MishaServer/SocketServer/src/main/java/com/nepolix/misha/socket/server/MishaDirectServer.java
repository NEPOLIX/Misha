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

package com.nepolix.misha.socket.server;

import com.nepolix.misha.socket.SocketChannel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Behrooz Shahriari
 * @since 11/29/17
 */
public
class MishaDirectServer
				implements Runnable
{
	 
	 private ServerSocket serverSocket;
	 
	 private int serverPort;
	 
	 private ISocketServerExchange serverExchange;
	 
	 public
	 MishaDirectServer ( int serverPort ,
											 ISocketServerExchange serverExchange )
					 throws
					 IOException
	 {
			
			this.serverPort = serverPort;
			this.serverExchange = serverExchange;
			init ( );
	 }
	 
	 private
	 void init ( )
	 {
			
			try
			{
				 serverSocket = new ServerSocket ( serverPort );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
	 }
	 
	 public
	 void start ( )
	 {
			
			new Thread ( this ).start ( );
	 }
	 
	 @Override
	 public
	 void run ( )
	 {
			
			while ( !serverSocket.isClosed ( ) )
			{
				 
				 Socket socket;
				 try
				 {
						socket = serverSocket.accept ( );
						new Thread ( ( ) -> {
							 SocketChannel socketChannel = null;
							 String        response;
							 try
							 {
									socketChannel = new SocketChannel ( socket , 10000 );
									response = serverExchange.handler ( socketChannel.readMessage ( ) , socketChannel.getInetAddress ( ) );
									socketChannel.writeMessage ( response );
							 }
							 catch ( Exception e )
							 {
									e.printStackTrace ( );
							 }
							 finally
							 {
									if ( socketChannel != null ) socketChannel.close ( );
							 }
						} ).start ( );
				 }
				 catch ( IOException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
}
