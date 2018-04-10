/******************************************************************************
 * Copyright © 2015-7532 NEX, Inc. [NEPOLIX]-(Behrooz Shahriari)              *
 *           All rights reserved.                                             *
 *                                                                            *
 *     The source code, other & all material, and documentation               *
 *     contained herein are, and remains the property of HEX Inc.             *
 *     and its suppliers, if any. The intellectual and technical              *
 *     concepts contained herein are proprietary to HEX Inc. and its          *
 *     suppliers and may be covered by U.S. and Foreign Patents, patents      *
 *     in process, and are protected by trade secret or copyright law.        *
 *     Dissemination of the foregoing material or reproduction of this        *
 *     material is strictly forbidden forever.                                *
 ******************************************************************************/

package com.nepolix.misha.task.scheduler;

import com.nepolix.misha.commons.utils.Utils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Behrooz Shahriari
 * @since 11/1/16
 */
class SchedulerImp
				implements Scheduler
{
	 
	 private final static int NUM_CORES = Runtime.getRuntime ( ).availableProcessors ( ) - 1 > 0 ? Runtime.getRuntime ( ).availableProcessors ( ) - 1 : 1;
	 
	 private static Scheduler scheduler = null;
	 
	 private boolean running;
	 
	 private Worker[] workers;
	 
	 private Thread[] threads;
	 
	 private Set< Long > taskIDs;
	 
	 static
	 Scheduler getScheduler ( int numberWorkers )
	 {
			
			numberWorkers = numberWorkers > NUM_CORES ? NUM_CORES / 2 : numberWorkers;
			numberWorkers = numberWorkers <= 0 ? 1 : numberWorkers;
			if ( scheduler == null )
			{
				 System.out.println ( "SCHEDULER is initialized with " + numberWorkers + " workers" );
				 scheduler = new SchedulerImp ( numberWorkers );
			}
			return scheduler;
	 }
	 
	 private
	 SchedulerImp ( int numberWorkers )
	 {
			
			running = true;
			workers = new Worker[ numberWorkers ];
			threads = new Thread[ numberWorkers ];
			for ( int i = 0 ; i < workers.length ; ++i )
			{
				 workers[ i ] = new Worker ( );
				 threads[ i ] = new Thread ( workers[ i ] );
				 threads[ i ].start ( );
			}
			taskIDs = new HashSet<> ( );
	 }
	 
	 
	 @Override
	 public
	 Scheduler schedule ( ScheduleTask task )
	 {

//			System.err.println ( "VVVVVVVVxx		" + task.toString ( ) );
			scheduleID ( task );
//			System.err.println (
//							"VVVVVVVVyy		" + task.toString ( ) + "   " + worker.toString ( ) + "  " + worker.getQueueSize ( ) );
			return this;
	 }
	 
	 @Override
	 public
	 long scheduleID ( ScheduleTask task )
	 {
			
			long id = Utils.randomLong ( );
			while ( taskIDs.contains ( id ) )
			{
				 id = Utils.randomLong ( );
			}
			taskIDs.add ( id );
			Worker worker = findBestWorker ( );
			worker.addTask ( task , id );
			return id;
	 }
	 
	 @Override
	 public
	 void removeTask ( long taskId )
	 {
			
			taskIDs.remove ( taskId );
			for ( int i = 0 ; i < workers.length ; ++i )
			{
				 workers[ i ].removeTask ( taskId );
			}
	 }
	 
	 @Override
	 public
	 void terminate ( )
	 {
			
			running = false;
			for ( Worker worker : workers ) worker.stop ( );
			for ( Thread thread : threads ) thread.interrupt ( );
	 }
	 
	 private
	 Worker findBestWorker ( )
	 {
			
			long   aveTime = Integer.MAX_VALUE;
			Worker worker  = null;
			for ( Worker w : workers )
			{
				 long at = w.getQueueAverageInterval ( );
				 if ( at < aveTime )
				 {
						worker = w;
						aveTime = at;
				 }
			}
			return worker;
	 }
	 
	 private
	 class Worker
					 implements Runnable
	 {
			
			private HashMap< Long, ScheduleTask > mapIDTask;
			
			private CopyOnWriteArrayList< ScheduleTask > queue;
			
			private long sleepInterval = Long.MAX_VALUE;
			
			private HashMap< ScheduleTask, Long > lastRun;
			
			private final Object LOCK = new Object ( );
			
			Worker ( )
			{
				 
				 queue = new CopyOnWriteArrayList<> ( );
				 lastRun = new HashMap<> ( );
				 mapIDTask = new HashMap<> ( );
			}
			
			@Override
			public
			void run ( )
			{
				 
				 long internalSleepTime = sleepInterval;
				 while ( running )
				 {
						synchronized ( LOCK )
						{
							 try
							 {
									LOCK.wait ( internalSleepTime );
							 }
							 catch ( InterruptedException e )
							 {
									e.printStackTrace ( );
							 }
						}
						long                     t0           = System.currentTimeMillis ( );
						Iterator< ScheduleTask > taskIterator = queue.iterator ( );
						while ( taskIterator.hasNext ( ) )
						{
							 ScheduleTask task        = taskIterator.next ( );
							 long         lastRunTime = lastRun.get ( task );
							 long         time        = System.currentTimeMillis ( );
//							 System.out.println ( ">>>>  sleepTime=" + internalSleepTime + "   " + task.toString ( ) + "    " + task.interval ( ) );
							 if ( time - lastRunTime >= task.interval ( ) )
							 {
									task.execute ( );
									time = System.currentTimeMillis ( );
									lastRun.put ( task , time );
							 }
						}
						long te = System.currentTimeMillis ( );
						internalSleepTime = sleepInterval;
						internalSleepTime -= te - t0;
						if ( internalSleepTime < 0 ) internalSleepTime = 3L;
						
				 }
			}
			
			long getQueueAverageInterval ( )
			{
				 
				 long aveTime = 0;
				 int  n       = 0;
				 for ( ScheduleTask task : queue )
				 {
						aveTime += task.interval ( );
						n++;
						if ( n > 1000 ) break;
				 }
				 if ( n == 0 ) return 0L;
				 return aveTime / n;
			}
			
			
			void addTask ( ScheduleTask task ,
										 long id )
			{
				 
				 synchronized ( LOCK )
				 {
						if ( sleepInterval > task.interval ( ) ) sleepInterval = task.interval ( );
						queue.add ( task );
						Collections.sort ( queue );
						lastRun.put ( task , 0L );
						mapIDTask.put ( id , task );
						LOCK.notify ( );
				 }
			}
			
			void removeTask ( long id )
			{
				 
				 ScheduleTask task = mapIDTask.get ( id );
				 if ( task != null )
				 {
						mapIDTask.remove ( id );
						lastRun.remove ( task );
						queue.remove ( task );
				 }
			}
			
			public
			void stop ( )
			{
				 
				 queue.clear ( );
				 queue = null;
			}
	 }
}
