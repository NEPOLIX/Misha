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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Behrooz Shahriari
 * @since 5/22/17
 */
final
class TaskRunner
				implements Runnable
{
	 
	 private final ITaskEngine iTaskEngine;
	 
	 private TaskExceptionListener exceptionListener;
	 
	 private BlockingQueue< ITask > tasksQueue;
	 
	 TaskRunner ( ITaskEngine iTaskEngine ,
								TaskExceptionListener exceptionListener )
	 {
			
			this.iTaskEngine = iTaskEngine;
			this.exceptionListener = exceptionListener;
			tasksQueue = new PriorityBlockingQueue<> ( new LinkedBlockingQueue<> ( ) );
	 }
	 
	 public
	 void setExceptionListener ( TaskExceptionListener exceptionListener )
	 {
			
			this.exceptionListener = exceptionListener;
	 }
	 
	 @Override
	 public
	 void run ( )
	 {
			
			while ( iTaskEngine.isRunning ( ) )
			{
				 
				 try
				 {
						for ( int i = 0 ; i < 3 ; ++i )
						{
							 executeTask ( iTaskEngine , tasksQueue.take ( ) , exceptionListener );
						}
				 }
				 catch ( InterruptedException e )
				 {
						e.printStackTrace ( );
				 }
			}
	 }
	 
	 private static
	 void executeTask ( final ITaskEngine iTaskEngine ,
											final ITask iTask ,
											TaskExceptionListener exceptionListener )
	 {
			
			try
			{
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
									if ( iTask != null ) TaskEngine.handleRecurrenceTask ( iTaskEngine , iTask );
									if ( iTask != null ) TaskEngine.handleConditionalTask ( iTaskEngine , iTask );
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
			catch ( Exception e )
			{
				 e.printStackTrace ( );
				 if ( exceptionListener != null ) exceptionListener.onException ( e );
			}
	 }

//	 private
//	 class InnerTaskListener
//					 implements TaskListener< Object >
//	 {
//
//			private final Object NLOCK = new Object ( );
//
//			private final boolean[] done = { false };
//
//			private final ITask iTask;
//
//			private final int handlerId;
//
//			InnerTaskListener ( ITask iTask,
//													int handlerId )
//			{
//
//				 this.iTask = iTask;
//				 this.handlerId = handlerId;
//			}
//
//			@Override
//			public
//			void finish ( )
//			{
//
//				 try
//				 {
//						if ( iTask != null ) handleRecurrenceTask ( iTask );
//						if ( iTask != null ) handleConditionalTask ( iTask );
//				 }
//				 catch ( NullPointerException ignored )
//				 {
//				 }
//				 ( ( TaskRunnerPool ) ITaskEngine ).taskCompleted ( handlerId );
//				 done[ 0 ] = true;
//				 synchronized ( NLOCK )
//				 {
//						NLOCK.notify ( );
//				 }
//			}
//
//			@Override
//			public
//			void setResult ( Object result )
//			{
//
//			}
//
//			synchronized
//			void waitLock ( )
//			{
//
//				 synchronized ( NLOCK )
//				 {
//						if ( !done[ 0 ] )
//						{
//							 try
//							 {
//									NLOCK.wait ( );
//							 }
//							 catch ( InterruptedException e )
//							 {
//									e.printStackTrace ( );
//							 }
//						}
//				 }
//			}
//	 }
//
	 
	 boolean newTask ( ITask ITask )
	 {
			
			return tasksQueue.offer ( ITask );
	 }
	 
	 void cancel ( )
	 {
			
			tasksQueue.clear ( );
	 }
	 
	 void stop ( )
	 {
			
			cancel ( );
	 }
	 
	 int getQueueSize ( )
	 {
			
			return tasksQueue.size ( );
	 }
}
