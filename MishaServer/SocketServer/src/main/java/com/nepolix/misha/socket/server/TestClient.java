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

import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.socket.SocketChannel;
import com.nepolix.misha.socket.SocketChannelPool;

import java.io.IOException;

/**
 * @author Behrooz Shahriari
 * @since 11/17/17
 */
public
class TestClient
{
	 
	 static SocketChannelPool socketChannelPool = SocketChannelPool.build ( 2 , "localhost" , 23572 , 10000 );
	 
	 public static
	 void main ( String[] args )
					 throws
					 IOException,
					 InterruptedException
	 {
			
			int nc = 1;
			int x  = ( 1 << ( nc + 2 ) );
			System.out.println ( x );
			
			for ( int i = 0 ; i < 4 ; ++i ) test ( i );
			Thread.sleep ( 8000 );
			test ( 0 );
			test ( 0 );
	 }
	 
	 static
	 void test ( int ik )
	 {
			
			new Thread ( ( ) -> {
				 for ( int i = 0 ; i < 5 ; ++i )
				 {
						SocketChannel socketChannel = socketChannelPool.getChannel ( );
						try
						{
							 socketChannel.writeMessage ( "hi this is test  " + i );
							 System.out.println ( "" + ik + "   " + socketChannel.readMessage ( ) );
							 socketChannelPool.returnChannel ( socketChannel );
						}
						catch ( Exception e )
						{
							 e.printStackTrace ( );
						}
						try
						{
							 Thread.sleep ( Utils.getRandom ( ).nextInt ( 2000 * ik + 1 ) );
						}
						catch ( InterruptedException e )
						{
							 e.printStackTrace ( );
						}
				 }
			} ).start ( );
	 }
}
