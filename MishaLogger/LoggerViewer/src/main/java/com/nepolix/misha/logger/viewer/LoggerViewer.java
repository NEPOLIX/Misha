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

package com.nepolix.misha.logger.viewer;

import com.nepolix.misha.commons.security.AES;
import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.db.cold.client.HealthChecker;
import com.nepolix.misha.json.JSONArray;
import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.logger.log.Log;
import com.nepolix.misha.logger.writer.LoggerFileWriter;
import com.nepolix.misha.socket.SocketChannel;
import com.nepolix.misha.socket.SocketChannelPool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Behrooz Shahriari
 * @since 10/31/17
 */
public
class LoggerViewer
{
	 
	 private List< SocketChannelPool > socketChannelPool;
	 
	 private static LoggerViewer LOGGER_VIEWER;
	 
	 private
	 LoggerViewer ( )
	 {
			
			init ( );
	 }
	 
	 public static
	 LoggerViewer getInstance ( )
	 {
			
			if ( LOGGER_VIEWER == null ) LOGGER_VIEWER = new LoggerViewer ( );
			return LOGGER_VIEWER;
	 }
	 
	 private
	 void init ( )
	 {
			
			socketChannelPool = new ArrayList<> ( );
			for ( int i = 0 ; i < HealthChecker.COLD_DB_NODES_ADDRESS.length ; ++i )
			{
				 socketChannelPool.add ( SocketChannelPool.build ( 1 , HealthChecker.COLD_DB_NODES_ADDRESS[ i ] , ColdDBConstants.getLoggerListenerServerPort ( ) , 0 ) );
			}
	 }
	 
	 public
	 void startTerminalView ( )
	 {
			
			socketChannelPool.forEach ( this :: initConnection );
	 }
	 
	 private
	 void initConnection ( SocketChannelPool socketChannelPool )
	 {
			
			
			SocketChannel socketChannel = socketChannelPool.getChannel ( );
			JSONObject    registerJSON  = new JSONObject ( );
			registerJSON.putOpt ( "METHOD" , "REGISTER" );
			try
			{
				 socketChannel.writeMessage ( registerJSON.toString ( ) );
				 System.out.println ( "init logger-viewer  " + socketChannel.readMessage ( ) );
				 startReceiver ( socketChannelPool , socketChannel );
			}
			catch ( Exception e )
			{
				 System.err.println ( "re-connection failed" );
			}
	 }
	 
	 private
	 void startReceiver ( SocketChannelPool socketChannelPool ,
												SocketChannel socketChannelX )
	 {
			
			final SocketChannel[] socketChannel = { socketChannelX };
			new Thread ( ( ) -> {
				 
				 while ( true )
				 {
						try
						{
							 String msg = socketChannel[ 0 ].readMessage ( );
							 socketChannel[ 0 ].writeMessage ( "OK" );
							 msg = AES.decrypt ( msg , AES.getGlobalAESKey ( ) );
							 try
							 {
									JSONArray array = new JSONArray ( msg );
									for ( int i = 0 ; i < array.length ( ) ; ++i )
									{
										 JSONObject data = array.optJSONObject ( i );
										 if ( data != null && data.has ( "ip" , "logs" , "logTime" ) )
										 {
												Log log = MJSON.toObject ( data , Log.class );
												log.$print ( LoggerFileWriter.MAP_SERVER_IPs );
												LoggerFileWriter.getInstance ( ).store ( log );
										 }
									}
							 }
							 catch ( JSONException ignored )
							 {
							 }
						}
						catch ( Exception e )
						{
							 e.printStackTrace ( );
							 try
							 {
									socketChannel[ 0 ].close ( );
							 }
							 catch ( Exception ignored )
							 {
							 }
							 try
							 {
									Thread.sleep ( 1000 );
							 }
							 catch ( InterruptedException ignored )
							 {
							 }
							 try
							 {
									initConnection ( socketChannelPool );
									socketChannel[ 0 ] = socketChannelPool.getChannel ( );
							 }
							 catch ( Exception ignored )
							 {
							 }
						}
				 }
			} ).start ( );
	 }
	 
	 public static
	 void main ( String[] args )
	 {
			
			LoggerViewer.getInstance ( ).startTerminalView ( );
	 }
}
