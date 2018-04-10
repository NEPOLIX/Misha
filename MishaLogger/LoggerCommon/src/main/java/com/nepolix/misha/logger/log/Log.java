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

package com.nepolix.misha.logger.log;


import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.db.MishaDB;
import com.nepolix.misha.db.model.IMigrationProtocol;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.logger.LoggerConstants;

import java.util.*;

/**
 * @author Behrooz Shahriari
 * @since 10/30/17
 */
public
class Log
				extends MModel
{
	 
	 private Set< String > topics;
	 
	 private String ip;
	 
	 List< LogData > logs;
	 
	 private LogTime logTime;
	 
	 public
	 Log ( )
	 {
			
			super ( );
			init ( );
	 }
	 
	 private
	 Log init ( )
	 {
			
			Calendar calendar = Utils.getUTCTime ( );
			logTime = new LogTime ( );
			logTime.setLogYear ( calendar.get ( Calendar.YEAR ) );
			logTime.setLogMonth ( calendar.get ( Calendar.MONTH ) );
			logTime.setLogDay ( calendar.get ( Calendar.DAY_OF_MONTH ) );
			logTime.setLogHour ( calendar.get ( Calendar.HOUR_OF_DAY ) );
			logTime.setLogMinute ( calendar.get ( Calendar.MINUTE ) );
			logTime.setLogSecond ( calendar.get ( Calendar.SECOND ) );
			logTime.setLogTime ( calendar.getTimeInMillis ( ) );
			return this;
	 }
	 
	 @Override
	 public
	 String modelName ( )
	 {
			
			return LoggerConstants.LOGGER_UUID_TAG;
	 }
	 
	 @Override
	 protected
	 IMigrationProtocol $getMigrationProtocol ( int version )
	 {
			
			return null;
	 }
	 
	 @Override
	 public
	 void $save ( )
	 {
		
	 }
	 
	 @Override
	 public
	 void $delete ( boolean keep )
	 {
		
	 }
	 
	 public
	 LogTime getLogTime ( )
	 {
			
			return logTime;
	 }
	 
	 public
	 void setLogTime ( LogTime logTime )
	 {
			
			this.logTime = logTime;
	 }
	 
	 public
	 Set< String > getTopics ( )
	 {
			
			if ( topics == null ) topics = new LinkedHashSet<> ( );
			return topics;
	 }
	 
	 public
	 void setTopics ( Set< String > topics )
	 {
			
			this.topics = topics;
	 }
	 
	 public
	 void $addTopics ( String... topics )
	 {
			
			getTopics ( ).add ( "LOG" );
			if ( topics != null ) for ( String x : topics ) getTopics ( ).add ( x );
	 }
	 
	 public
	 List< LogData > getLogs ( )
	 {
			
			if ( logs == null ) logs = new ArrayList<> ( );
			return logs;
	 }
	 
	 public
	 void setLogs ( List< LogData > logs )
	 {
			
			this.logs = logs;
	 }
	 
	 public
	 String getIp ( )
	 {
			
			return ip;
	 }
	 
	 public
	 void setIp ( String ip )
	 {
			
			this.ip = ip;
	 }
	 
	 public
	 String toString ( Map< String, String > mapServerIPs )
	 {
			
			StringBuilder builder = new StringBuilder ( );
			builder.append ( "\n" ).append ( "\n" ).append ( "Log-ID=" ).append ( mid ).append ( "\n" );
			String ipX = "" + ip;
			if ( mapServerIPs.containsKey ( ipX ) )
			{
				 ipX = ipX + "  [" + mapServerIPs.get ( ipX ) + "]";
			}
			builder.append ( "Log-IP= " ).append ( ipX ).append ( "\n" );
			builder.append ( "Log-Time= " ).append ( logTime.toString ( ) ).append ( "\n" );
			builder.append ( "Topics={" );
			List< String > list = new ArrayList<> ( topics );
			for ( int i = 0 ; i < list.size ( ) ; ++i )
			{
				 String x = list.get ( i );
				 builder.append ( "'" ).append ( x ).append ( "'" );
				 if ( i != list.size ( ) - 1 ) builder.append ( ", " );
			}
			builder.append ( "}\n" );
			
			builder.append ( "Logs=\n" );
			List< LogData > logData = getLogs ( );
			logData.sort ( Comparator.comparingLong ( o -> o.getLogTime ( ).getLogTime ( ) ) );
			for ( LogData data : logData )
			{
				 builder.append ( data );
			}
			builder.append ( "\n" );
			
			builder.append ( "\n" );
			return builder.toString ( );
	 }
	 
	 public
	 void $print ( Map< String, String > mapServerIPs )
	 {
			
			System.out.println ( toString ( mapServerIPs ) );
	 }
	 
	 public
	 Log $addLogData ( LogTag logTag ,
										 String text ,
										 JSONObject jsonObject )
	 {
			
			Thread  thread  = Thread.currentThread ( );
			LogData logData = new LogData ( );
			logData.setTag ( logTag );
			logData.setLogText ( text );
			logData.setLogJSON ( jsonObject );
			logData.$getCodeInfo ( thread.getStackTrace ( ) );
			getLogs ( ).add ( logData );
			return this;
	 }
	 
	 public static
	 Log $build ( MishaDB mishaDB ,
								String... topics )
	 {
			
			Log log = mishaDB != null ? mishaDB.buildMishaModel ( Log.class ).init ( ) : new Log ( );
			log.$addTopics ( topics );
			return log;
	 }
}
