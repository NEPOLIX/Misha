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

package com.nepolix.misha.task.handler.core.engine;

import com.nepolix.misha.task.handler.core.task.ITask;
import com.nepolix.misha.task.handler.core.task.IxTask;
import com.nepolix.misha.task.handler.core.task.Task;
import com.nepolix.misha.task.handler.core.task.callback.condition.ConditionalTask;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import static com.nepolix.misha.task.handler.core.task.TaskType.INFINITE;
import static com.nepolix.misha.task.handler.core.task.TaskType.RECURRENT;

/**
 * @author Behrooz Shahriari
 * @since 5/21/17
 */
public
class TaskEngine
				implements Runnable,
									 ITaskEngine
{
	 
	 private final static int NUM_CORES = Runtime.getRuntime ( ).availableProcessors ( );
	 
	 final static int QUEUE_SIZE = 1 << 4;
	 
	 private final BlockingQueue< ITask > tasks;
	 
	 private TaskExceptionListener exceptionListener;
	 
	 private Thread[] threads;
	 
	 private TaskRunner runners[];
	 
	 private boolean engineRunning;
	 
	 TaskEngine ( boolean minimalThreads ,
								TaskExceptionListener exceptionListener )
	 {
			
			this.exceptionListener = exceptionListener;
			tasks = new PriorityBlockingQueue<> ( new LinkedBlockingQueue<> ( ) );
			engineRunning = true;
			int concurrency;
			if ( minimalThreads ) concurrency = NUM_CORES / 2;//comes from web-client
			else concurrency = NUM_CORES * 2;
			concurrency = concurrency < 1 ? NUM_CORES : concurrency;
			threads = new Thread[ concurrency ];
			runners = new TaskRunner[ concurrency ];
			System.out.println ( "Task Engine concurrency=" + concurrency );
			for ( int i = 0 ; i < concurrency ; ++i )
			{
				 runners[ i ] = new TaskRunner ( this , exceptionListener );
				 threads[ i ] = new Thread ( runners[ i ] );
				 threads[ i ].start ( );
			}
			new Thread ( this ).start ( );
	 }
	 
	 public
	 void setExceptionListener ( TaskExceptionListener exceptionListener )
	 {
			
			this.exceptionListener = exceptionListener;
			for ( TaskRunner x : runners )
				 x.setExceptionListener ( exceptionListener );
	 }
	 
	 @Override
	 public
	 void run ( )
	 {
			
			while ( engineRunning )
			{
				 try
				 {
						runners[ getIndexBestRunner ( ) ].newTask ( tasks.take ( ) );
				 }
				 catch ( InterruptedException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 private
	 int getIndexBestRunner ( )
	 {
			
			int idx      = 0;
			int bestSize = Integer.MAX_VALUE;
			for ( int i = 0 ; i < runners.length ; ++i )
			{
				 if ( runners[ i ].getQueueSize ( ) < bestSize )
				 {
						bestSize = runners[ i ].getQueueSize ( );
						idx = i;
				 }
			}
			return idx;
	 }
	 
	 @Override
	 public
	 ITaskEngine add ( ITask newITask )
	 {
			
			tasks.offer ( newITask );
			return this;
	 }
	 
	 @Override
	 public
	 ITaskEngine add ( IxTask task )
	 {
			
			add ( new Task ( )
			{
				 
				 @Override
				 public
				 void execute ( ITaskEngine iTaskEngine ,
												TaskListener listener )
				 {
						
						task.execute ( );
						listener.finish ( );
				 }
			} );
			return this;
	 }
	 
	 @Override
	 public
	 boolean isRunning ( )
	 {
			
			return engineRunning;
	 }
	 
	 @Override
	 public
	 void stop ( )
	 {
			
			engineRunning = false;
			for ( Thread thread : threads )
			{
				 thread.interrupt ( );
			}
	 }
	 
	 @Override
	 public
	 void clearTasks ( )
	 {
			
			tasks.clear ( );
			for ( TaskRunner taskRunner : runners )
			{
				 taskRunner.cancel ( );
			}
	 }
	 
	 @Override
	 public
	 int queueSize ( )
	 {
			
			return tasks.size ( );
	 }
	 
	 static
	 void handleRecurrenceTask ( ITaskEngine iTaskEngine ,
															 ITask ITask )
	 {
			
			if ( ITask.getTaskType ( ).equals ( INFINITE ) )
			{
				 iTaskEngine.add ( ITask );
			}
			else
			{
				 if ( ITask.getTaskType ( ).equals ( RECURRENT ) && ITask.getRepetition ( ) > 0 )
				 {
						Task abstractTask = ( Task ) ITask;
						abstractTask.tickRepetition ( );
						iTaskEngine.add ( ITask );
				 }
			}
	 }
	 
	 static
	 void handleConditionalTask ( ITaskEngine iTaskEngine ,
																ITask iTask )
	 {
			
			if ( iTask instanceof ConditionalTask )
			{
				 ConditionalTask task = ( ConditionalTask ) iTask;
				 if ( !task.taskCondition ( ).passCondition ( ) )
				 {
						iTaskEngine.add ( iTask );
				 }
				 else
				 {
						task.runFinalCallback ( task.getResult ( ) , task.getError ( ) );
				 }
			}
	 }
	 
	 private
	 class EmergencyInnerRunner
					 implements Runnable
	 {
			
			ITask iTask;
			
			public
			EmergencyInnerRunner ( ITask iTask )
			{
				 
				 this.iTask = iTask;
			}
			
			@Override
			public
			void run ( )
			{
				 
				 ITaskEngine iTaskEngine = TaskEngine.this;
				 TaskListener listener = new TaskListener ( )
				 {
						
						private boolean finished = false;
						
						@Override
						public
						void finish ( )
						{
							 
							 if ( !finished )
							 {
									finished = true;
									if ( iTask != null ) handleRecurrenceTask ( iTaskEngine , iTask );
									if ( iTask != null ) handleConditionalTask ( iTaskEngine , iTask );
							 }
						}
						
						@Override
						public
						void setResult ( Object result )
						{
							 
						}
				 };
				 iTask.execute ( iTaskEngine , listener );
				 listener.finish ( );
			}
	 }
}
