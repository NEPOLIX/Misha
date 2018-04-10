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

package com.nepolix.misha.db.cold.client;

import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.socket.SocketChannel;
import com.nepolix.misha.socket.SocketChannelPool;

import static com.nepolix.misha.db.cold.client.HealthChecker.COLD_DB_NODES_ADDRESS;


/**
 * @author Behrooz Shahriari
 * @since 3/9/18
 */
public
class RestartCluster
{
	 
	 public static
	 void main ( String[] args )
	 {
			
			SocketChannelPool[] socketChannelPools = new SocketChannelPool[ COLD_DB_NODES_ADDRESS.length ];
			for ( int i = 0 ; i < socketChannelPools.length ; ++i )
			{
				 socketChannelPools[ i ] = SocketChannelPool.build ( 2 , COLD_DB_NODES_ADDRESS[ i ] , ColdDBConstants.getPort ( ) );
				 JSONObject body = new JSONObject ( );
				 body.putOpt ( "method" , "restart" );
				 
				 String        x          = body.toString ( );
				 int           cacheIndex = i;
				 SocketChannel channel    = socketChannelPools[ cacheIndex ].getChannel ( );
				 try
				 {
						channel.writeMessage ( x );
						System.out.println ( channel.readMessage ( ) );
						socketChannelPools[ cacheIndex ].returnChannel ( channel );
				 }
				 catch ( Exception e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
}
