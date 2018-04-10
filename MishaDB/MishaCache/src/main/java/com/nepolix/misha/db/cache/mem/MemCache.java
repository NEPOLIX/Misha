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

package com.nepolix.misha.db.cache.mem;

import com.nepolix.misha.commons.Constants;
import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.db.cache.server.CacheDBServer;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.json.serialization.MJSON;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.callback.Callback;
import com.nepolix.misha.task.handler.core.task.callback.condition.ConditionalTask;
import com.nepolix.misha.task.handler.core.task.callback.condition.TaskCondition;
import com.nepolix.misha.task.scheduler.ScheduleTask;
import com.nepolix.misha.task.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Behrooz Shahriari
 * @since 6/23/17
 */
public
class MemCache
{
	 
	 private static long MEMORY_BOUND = 3500000000L;

//	 private static long MAXIMUM_LOCAL_CACHE_SIZE_BYTE = 5 * 1024 * 1024 * 1024;//5 GB
	 
	 static
	 {
			com.sun.management.OperatingSystemMXBean os = ( com.sun.management.OperatingSystemMXBean ) java.lang.management.ManagementFactory.getOperatingSystemMXBean ( );
			MEMORY_BOUND = os.getTotalPhysicalMemorySize ( ) * 7;
			System.out.println ( "LocalCache >> MAXIMUM_LOCAL_CACHE_SIZE_BYTE= " + MEMORY_BOUND );
	 }
	 
	 private MSQL msql;
	 
	 private ConcurrentHashMap< String, CacheSet > memCache;
	 
	 private long memorySize;
	 
	 public
	 MemCache ( )
	 {
			
			msql = MSQL.getMSQL ( );
			memCache = new ConcurrentHashMap<> ( );
			Scheduler scheduler = CacheDBServer.getScheduler ( );
			System.out.println ( "Cache cleaner task has been added to scheduler" );
			scheduler.schedule ( cleanOldCaches ( ) );
	 }
	 
	 private
	 ScheduleTask cleanOldCaches ( )
	 {
			
			return new ScheduleTask ( )
			{
				 
				 @Override
				 public
				 int interval ( )
				 {
						
						return Constants.INTERVAL_12_HOURS;
				 }
				 
				 @Override
				 protected
				 void execute ( )
				 {
						
						System.out.println ( "cleaning old data" );
						msql.cleanObsoleteCaches ( );
						boundMemCache ( );
				 }
			};
	 }
	 
	 public
	 void save ( String archiveName ,
							 String key ,
							 String value ,
							 long expirationDuration )
	 {
			
			CacheSet cacheSet = memCache.computeIfAbsent ( archiveName + key , k -> new CacheSet ( ) );
			memorySize += cacheSet.save ( value , expirationDuration );
			memorySize += ( key.length ( ) + archiveName.length ( ) );
			msql.delete ( archiveName , key );
			msql.save ( archiveName , key , Utils.singletonList ( value ) , expirationDuration );
	 }
	 
	 public
	 void insert ( String archiveName ,
								 String key ,
								 List< String > values ,
								 long expirationDuration )
	 {
			
			CacheSet cacheSet = memCache.computeIfAbsent ( archiveName + key , k -> new CacheSet ( ) );
			memorySize += cacheSet.insert ( values , expirationDuration );
			memorySize += ( key.length ( ) + archiveName.length ( ) );
			CacheDBServer.getTaskEngine ( ).add ( ( ) -> msql.save ( archiveName , key , values , expirationDuration ) );
	 }
	 
	 public
	 void cleanSlate ( )
	 {
			
			msql.cleanSlate ( );
			memCache.clear ( );
			memorySize = 0;
			Utils.causeGC ( );
	 }
	 
	 public
	 void delete ( String archiveName ,
								 String key )
	 {
			
			delete ( archiveName + key );
			msql.delete ( archiveName , key );
			CacheDBServer.getTaskEngine ( ).add ( Utils:: causeGC );
	 }
	 
	 private
	 void delete ( String archiveName_Key )
	 {
			
			CacheSet cacheSet = memCache.computeIfAbsent ( archiveName_Key , k -> new CacheSet ( ) );
			long     x        = cacheSet.delete ( );
			memorySize -= x;
			if ( x != 0 ) memorySize -= ( archiveName_Key.length ( ) );
			memCache.remove ( archiveName_Key );
	 }
	 
	 public
	 void delete ( String archiveName ,
								 String key ,
								 List< String > values )
	 {
			
			CacheDBServer.getTaskEngine ( ).add ( ( ) -> {
				 
				 CacheSet cacheSet = memCache.computeIfAbsent ( archiveName + key , k -> new CacheSet ( ) );
				 memorySize -= cacheSet.delete ( values );
				 if ( cacheSet.getCacheSize ( ) < 2 )
				 {
						memCache.remove ( archiveName + key );
						memorySize -= ( key.length ( ) + archiveName.length ( ) );
				 }
				 msql.delete ( archiveName , key , values );
				 Utils.causeGC ( );
			} );
	 }
	 
	 public
	 List< String > fetch ( String archiveName ,
													String key )
	 {
			
			CacheSet       cacheSet = memCache.computeIfAbsent ( archiveName + key , k -> new CacheSet ( ) );
			List< String > values   = new ArrayList<> ( );
			memorySize += cacheSet.fetch ( values );
			if ( cacheSet.getCacheSize ( ) < 2 )
			{
				 memCache.remove ( archiveName + key );
				 memorySize -= ( key.length ( ) + archiveName.length ( ) );
			}
			long nowTime = Utils.getCurrentUTCTime ( );
			if ( values.isEmpty ( ) )
			{
				 List< CacheObject > objects = msql.fetch ( archiveName , key );
				 if ( objects != null )
				 {
						long expTime = 0;
						long n       = 0;
						for ( CacheObject object : objects )
						{
							 if ( object.getExpirationTime ( ) > nowTime )
							 {
									values.add ( object.getValue ( ) );
									expTime += object.getExpirationTime ( );
									n++;
							 }
						}
						if ( !values.isEmpty ( ) )
						{
							 expTime /= n;
							 insert ( archiveName , key , values , expTime );
						}
				 }
			}
			return values;
	 }
	 
	 private static boolean memoryBoundInProgress = false;
	 
	 private
	 void boundMemCache ( )
	 {
			
			if ( !memoryBoundInProgress )
			{
				 memoryBoundInProgress = true;
				 final Callback< Void > finalCallback = new Callback< Void > ( )
				 {
						
						@Override
						public
						void onResult ( Void result )
						{
							 
							 memoryBoundInProgress = false;
						}
						
						@Override
						public
						void onError ( JSONObject e )
						{
							 
							 System.out.println ( MJSON.toString ( e ) );
							 memoryBoundInProgress = false;
						}
				 };
				 ConditionalTask< Void > conditionalTask = new ConditionalTask< Void > ( null , finalCallback )
				 {
						
						@Override
						public
						TaskCondition taskCondition ( )
						{
							 
							 return ( ) -> memorySize < MEMORY_BOUND;
						}
						
						@Override
						protected
						void callBackExecute ( ITaskEngine iTaskEngine ,
																	 TaskListener listener )
										throws
										Exception
						{
							 
							 int c = 0;
							 for ( String k : memCache.keySet ( ) )
							 {
									
									if ( c > 20 )
									{
										 if ( Utils.getRandom ( ).nextDouble ( ) > 0.1d )
										 {
												break;
										 }
										 else
										 {
												delete ( k );
										 }
									}
									else
									{
										 delete ( k );
										 c++;
									}
							 }
						}
				 };
				 CacheDBServer.getTaskEngine ( ).add ( conditionalTask );
			}
	 }
}
