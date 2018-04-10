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

import com.nepolix.misha.commons.Constants;

import java.net.ConnectException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Behrooz Shahriari
 * @since 6/23/17
 */
public final
class SocketChannelPool
{
	 
	 private ConcurrentLinkedQueue< SocketChannel > channelPool;
	 
	 private int size;
	 
	 private final String ip;
	 
	 private final int port;
	 
	 private int timeOut = -1;
	 
	 
	 private
	 SocketChannelPool ( int size ,
											 String ip ,
											 int port ,
											 int timeOut )
	 {
			
			this.size = size;
			this.ip = ip;
			this.port = port;
			this.timeOut = timeOut;
			channelPool = new ConcurrentLinkedQueue<> ( );
	 }
	 
	 public static
	 SocketChannelPool build ( int size ,
														 String ip ,
														 int port )
	 {
			
			return new SocketChannelPool ( size , ip , port , -1 );
	 }
	 
	 public static
	 SocketChannelPool build ( int size ,
														 String ip ,
														 int port ,
														 int timeOut )
	 {
			
			return new SocketChannelPool ( size , ip , port , timeOut );
	 }
	 
	 public
	 SocketChannel getChannel ( )
	 {

//			resetConnections ( );
//			SocketChannel socketChannel = channelPool.poll ( );
//			if ( socketChannel == null )
//			{
//				 for ( int i = 0 ; i < size ; ++i )
//				 {
//						try
//						{
//							 channelPool.add ( new SocketChannel ( ip , port , timeOut ) );
//						}
//						catch ( ConnectException e )
//						{
//							 e.printStackTrace ( );
//						}
//				 }
//				 socketChannel = channelPool.poll ( );
//			}
//			return socketChannel;
			try
			{
				 return new SocketChannel ( ip , port , timeOut );
			}
			catch ( ConnectException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
	 
	 public
	 void returnChannel ( SocketChannel channel )
	 {
			
			try
			{
				 channel.close ( );
			}
			catch ( Exception e )
			{
				 e.printStackTrace ( );
			}
//			channelPool.add ( channel );
//			if ( channelPool.size ( ) > 2 * size )
//			{
//				 SocketChannel socketChannel = getChannel ( );
//				 socketChannel.close ( );
//			}
	 }
	 
	 private long lastResetTime = -1;
	 
	 synchronized
	 void resetConnections ( )
	 {
			
			synchronized ( this )
			{
				 if ( lastResetTime < 0 )
				 {
						lastResetTime = System.currentTimeMillis ( );
						return;
				 }
				 if ( System.currentTimeMillis ( ) - lastResetTime > Constants.INTERVAL_1_HOUR )
				 {
						if ( channelPool != null )
						{
							 for ( SocketChannel socketChannel : channelPool )
							 {
									try
									{
										 socketChannel.close ( );
									}
									catch ( Exception e )
									{
										 e.printStackTrace ( );
									}
							 }
							 channelPool.clear ( );
							 lastResetTime = System.currentTimeMillis ( );
							 System.out.println ( "resetConnections  SocketChannel" );
						}
				 }
			}
	 }
}
