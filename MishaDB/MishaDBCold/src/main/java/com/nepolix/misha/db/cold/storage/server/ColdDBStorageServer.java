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

package com.nepolix.misha.db.cold.storage.server;

import com.nepolix.misha.db.cold.ColdDBConstants;
import com.nepolix.misha.db.cold.storage.listener.LoggerListenerSocketServer;
import com.nepolix.misha.db.cold.storage.server.handlers.ColdDBHandler;
import com.nepolix.misha.db.cold.storage.server.handlers.files.ColdDBFileWriter;
import com.nepolix.misha.rest.client.WebClient;
import com.nepolix.misha.socket.server.MishaDirectServer;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.scheduler.Scheduler;

import java.io.IOException;

/**
 * @author Behrooz Shahriari
 * @since 2/19/18
 */
public
class ColdDBStorageServer
{
	 
	 private static ITaskEngine taskEngine = null;
	 
	 private static Scheduler scheduler = null;
	 
	 public static
	 void main ( String[] args )
	 {
			
			try
			{
				 new ColdDBStorageServer ( ).start ( );
			}
			catch ( IOException e )
			{
				 e.printStackTrace ( );
				 System.exit ( -1 );
			}
	 }
	 
	 private
	 void start ( )
					 throws
					 IOException
	 {
			
			taskEngine = ITaskEngine.buildTaskEngine ( true );
			scheduler = Scheduler.getScheduler ( 1 );
			WebClient.getInstance ( );
			
			ColdDBFileWriter.syncOldFiles ( );
			
			LoggerListenerSocketServer loggerListenerSocketServer = new LoggerListenerSocketServer ( ColdDBConstants.getLoggerListenerServerPort ( ) );
			loggerListenerSocketServer.start ( );
			MishaDirectServer mishaNIODirectServer = new MishaDirectServer ( ColdDBConstants.getPort ( ) , new ColdDBHandler ( loggerListenerSocketServer ) );
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
