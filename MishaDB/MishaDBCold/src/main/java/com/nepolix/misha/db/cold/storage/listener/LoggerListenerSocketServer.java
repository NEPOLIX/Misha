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

package com.nepolix.misha.db.cold.storage.listener;

import com.nepolix.misha.commons.security.AES;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.socket.SocketChannel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Behrooz Shahriari
 * @since 10/30/17
 */
public
class LoggerListenerSocketServer
				implements ListenerServerBridge,
									 Runnable
{
	 
	 private int port;
	 
	 private ServerSocket serverSocket;
	 
	 private ConcurrentHashMap< String/*ip*/, SocketChannel > listeners;
	 
	 public
	 LoggerListenerSocketServer ( int loggerListenerServerPort )
	 {
			
			port = loggerListenerServerPort;
			listeners = new ConcurrentHashMap<> ( );
	 }
	 
	 @Override
	 public
	 void routeServerListener ( String jsonArrayObjects )
	 {
			
			String logMsg = AES.encrypt ( jsonArrayObjects , AES.getGlobalAESKey ( ) );
			listeners.values ( ).forEach ( s -> {
				 try
				 {
						s.writeMessage ( logMsg );
						s.readMessage ( );
				 }
				 catch ( Exception e )
				 {
						cleanSocketChannel ( s );
				 }
			} );
	 }
	 
	 private
	 void cleanSocketChannel ( SocketChannel socketChannel )
	 {
			
			listeners.entrySet ( ).removeIf ( e -> e.getValue ( ).equals ( socketChannel ) || e.getValue ( ) == socketChannel || e.getValue ( ) == null );
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
			
			init ( );
			while ( true )
			{
				 try
				 {
						Socket        socket        = serverSocket.accept ( );
						SocketChannel socketChannel = new SocketChannel ( socket , 0 );
						register ( socketChannel );
				 }
				 catch ( Exception e )
				 {
//						e.printStackTrace ( );
						System.exit ( -1 );
				 }
			}
	 }
	 
	 private
	 void register ( SocketChannel socketChannel )
	 {
			
			try
			{
				 String     msg     = socketChannel.readMessage ( );
				 JSONObject request = MJSON.toJSON ( msg );
				 if ( request.optString ( "METHOD" ).equals ( "REGISTER" ) )
				 {
						listeners.put ( socketChannel.getInetAddress ( ).getHostAddress ( ) , socketChannel );
						socketChannel.writeMessage ( "REGISTERED" );
				 }
				 else
				 {
						socketChannel.close ( );
				 }
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 socketChannel.close ( );
			}
	 }
	 
	 private
	 void init ( )
	 {
			
			try
			{
				 serverSocket = new ServerSocket ( port );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
			}
	 }
}
