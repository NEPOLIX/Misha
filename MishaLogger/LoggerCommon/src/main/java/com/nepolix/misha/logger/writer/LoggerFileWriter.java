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

package com.nepolix.misha.logger.writer;

import com.nepolix.misha.logger.log.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Behrooz Shahriari
 * @since 11/7/17
 */
public
class LoggerFileWriter
{
	 
	 public static int NUMBER_DAYS_KEEP_LOGS = 10;
	 
	 private final static int LOG_LIMIT_PER_FILE = 100000;
	 
	 private final static File ROOT_LOG_PATH = new File ( System.getProperty ( "user.home" ) + File.separator + ".misha" + File.separator + "logs" );
	 
	 public final static Map< String, String > MAP_SERVER_IPs;
	 
	 static
	 {
			ROOT_LOG_PATH.mkdirs ( );
			MAP_SERVER_IPs = new HashMap<> ( );
			String[] serverIPs = new String[] {
			
			};
			String[] serviceIPs = new String[] {
			
			};
			String[] cacheNodeIPs = new String[] {
			
			};
			String server  = "Test Server";
			String service = "Test Service";
			int    i       = 0;
			for ( String k : serverIPs )
			{
				 i++;
				 MAP_SERVER_IPs.put ( k , server + " " + i );
			}
			i = 0;
			for ( String k : serviceIPs )
			{
				 i++;
				 MAP_SERVER_IPs.put ( k , service + " " + i );
			}
			i = 0;
			for ( String k : cacheNodeIPs )
			{
				 i++;
				 MAP_SERVER_IPs.put ( k , "Test Cache Node" + " " + i );
			}
	 }
	 
	 private final static LoggerFileWriter LOGGER_FILE_WRITER = new LoggerFileWriter ( );
	 
	 public static
	 LoggerFileWriter getInstance ( )
	 {
			
			return LOGGER_FILE_WRITER;
	 }
	 
	 private
	 LoggerFileWriter ( )
	 {
			
			deleteOldLogs ( );
			initCalendar = Calendar.getInstance ( );
			logs = new ArrayList<> ( );
	 }
	 
	 private List< Log > logs = new ArrayList<> ( );
	 
	 private int savedLogsNumber;
	 
	 private int logIdx = 0;
	 
	 private Calendar initCalendar;
	 
	 private File logFile = null;
	 
	 private FileWriter logFileWriter = null;
	 
	 public synchronized
	 void store ( Log log )
	 {
			
			synchronized ( LOGGER_FILE_WRITER )
			{
				 logs.add ( log );
				 save ( );
			}
	 }
	 
	 private
	 void save ( )
	 {
			
			ArrayList< Log > xLogs = new ArrayList<> ( logs );
			if ( xLogs.size ( ) > 10 )
			{
				 logs.clear ( );
				 try
				 {
						getLogFile ( );
						xLogs.forEach ( x -> {
							 try
							 {
									logFileWriter.write ( x.toString ( MAP_SERVER_IPs ) );
									logFileWriter.flush ( );
									savedLogsNumber++;
							 }
							 catch ( IOException e )
							 {
									e.printStackTrace ( );
							 }
						} );
				 }
				 catch ( IOException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 private
	 void getLogFile ( )
					 throws
					 IOException
	 {
			
			if ( logFile == null )
			{
				 String ymd[] = getYMD ( initCalendar );
				 logFile = new File ( ROOT_LOG_PATH.getAbsolutePath ( ) + File.separator + ymd[ 0 ] + "-" + ymd[ 1 ] + "-" + ymd[ 2 ] + "_" + getLogIdx ( ) + ".log" );
				 while ( logFile.exists ( ) )
				 {
						logIdx++;
						logFile = new File ( ROOT_LOG_PATH.getAbsolutePath ( ) + File.separator + ymd[ 0 ] + "-" + ymd[ 1 ] + "-" + ymd[ 2 ] + "_" + getLogIdx ( ) + ".log" );
				 }
				 logFileWriter = new FileWriter ( logFile );
			}
			else
			{
				 Calendar calendar = Calendar.getInstance ( );
				 long     dif      = 86400000;//24 hrs
				 int      y        = calendar.get ( Calendar.YEAR );
				 int      yi       = initCalendar.get ( Calendar.YEAR );
				 if ( y == yi )
				 {
						if ( calendar.getTimeInMillis ( ) - initCalendar.getTimeInMillis ( ) > dif )
						{
							 //A
							 aA ( calendar );
						}
						else
						{
							 if ( savedLogsNumber > LOG_LIMIT_PER_FILE )
							 {
									String ymd[] = getYMD ( initCalendar );
									logIdx++;
									logFile = new File ( ROOT_LOG_PATH.getAbsolutePath ( ) + File.separator + ymd[ 0 ] + "-" + ymd[ 1 ] + "-" + ymd[ 2 ] + "_" + getLogIdx ( ) + ".log" );
									try
									{
										 logFileWriter.close ( );
									}
									catch ( Exception ignored )
									{
									}
									logFileWriter = new FileWriter ( logFile );
									savedLogsNumber = 0;
							 }
						}
				 }
				 else
				 {
						//A
						aA ( calendar );
				 }
			}
	 }
	 
	 private
	 void aA ( Calendar calendar )
					 throws
					 IOException
	 {
			
			initCalendar.setTimeInMillis ( calendar.getTimeInMillis ( ) );
			logIdx = 0;
			String ymd[] = getYMD ( initCalendar );
			logIdx++;
			logFile = new File ( ROOT_LOG_PATH.getAbsolutePath ( ) + File.separator + ymd[ 0 ] + "-" + ymd[ 1 ] + "-" + ymd[ 2 ] + "_" + getLogIdx ( ) + ".log" );
			logFileWriter = new FileWriter ( logFile );
	 }
	 
	 
	 private
	 String getLogIdx ( )
	 {
			
			if ( logIdx > 100 ) return "" + logIdx;
			else
			{
				 if ( logIdx > 10 ) return "0" + logIdx;
				 else return "00" + logIdx;
			}
	 }
	 
	 private
	 void deleteOldLogs ( )
	 {
			
			Calendar calendar = Calendar.getInstance ( );
			calendar.add ( Calendar.DATE , -1 * NUMBER_DAYS_KEEP_LOGS );
			try
			{
				 File[] files = ROOT_LOG_PATH.listFiles ( );
				 int    l1;
				 do
				 {
						l1 = files.length;
						calendar.add ( Calendar.DATE , -1 );
						String ymd[] = getYMD ( calendar );
						for ( File file : files )
						{
							 String t = ymd[ 0 ] + "-" + ymd[ 1 ] + "-" + ymd[ 2 ];
							 if ( file.getName ( ).contains ( t ) ) file.delete ( );
						}
						files = ROOT_LOG_PATH.listFiles ( );
				 }
				 while ( l1 != files.length );
			}
			catch ( NullPointerException ignored )
			{
			}
	 }
	 
	 private
	 String[] getYMD ( Calendar calendar )
	 {
			
			String year     = calendar.get ( Calendar.YEAR ) > 9 ? "" + calendar.get ( Calendar.YEAR ) : "0" + calendar.get ( Calendar.YEAR );
			String month    = calendar.get ( Calendar.MONTH ) > 9 ? "" + calendar.get ( Calendar.MONTH ) : "0" + calendar.get ( Calendar.MONTH );
			String dayMonth = calendar.get ( Calendar.DAY_OF_MONTH ) > 9 ? "" + calendar.get ( Calendar.DAY_OF_MONTH ) : "0" + calendar.get ( Calendar.DAY_OF_MONTH );
			return new String[] {
							year ,
							month ,
							dayMonth
			};
	 }
}
