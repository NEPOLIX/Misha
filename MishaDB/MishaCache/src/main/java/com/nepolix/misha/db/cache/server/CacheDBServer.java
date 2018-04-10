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

package com.nepolix.misha.db.cache.server;

import com.nepolix.misha.db.cache.api.CacheDirectHandler;
import com.nepolix.misha.db.connection.DBConnectionPool;
import com.nepolix.misha.rest.client.WebClient;
import com.nepolix.misha.socket.server.MishaDirectServer;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.scheduler.Scheduler;

import java.io.IOException;


/**
 * @author Behrooz Shahriari
 * @since 5/24/17
 */
public
class CacheDBServer
{
	 
	 private static ITaskEngine taskEngine = null;
	 
	 private static Scheduler scheduler = null;
	 
	 public static
	 void main ( String[] args )
					 throws
					 IOException
	 {
			
			if ( args == null || args.length == 0 ) new CacheDBServer ( ).start ( );
	 }
	 
	 public static
	 int getPort ( )
	 {
			
			return CacheDBConstants.getDBCachePort ( );
	 }
	 
	 private
	 void start ( )
					 throws
					 IOException
	 {
			
			DBConnectionPool.setMaxConnection ( 15 );
			DBConnectionPool.setSingleSocketOnDemand ( false );
			taskEngine = ITaskEngine.buildTaskEngine ( true );
			scheduler = Scheduler.getScheduler ( 1 );
			WebClient.getInstance ( );
			MishaDirectServer mishaNIODirectServer = new MishaDirectServer ( CacheDBConstants.getDBCachePort ( ) , new CacheDirectHandler ( ) );
			mishaNIODirectServer.start ( );
	 }
	 
	 public static
	 ITaskEngine getTaskEngine ( )
	 {
			
			return taskEngine;
	 }
	 
	 public static
	 Scheduler getScheduler ( )
	 {
			
			return scheduler;
	 }
}
