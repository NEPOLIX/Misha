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

import com.nepolix.misha.task.scheduler.ScheduleTask;
import com.nepolix.misha.task.scheduler.Scheduler;

/**
 * @author Behrooz Shahriari
 * @since 11/1/16
 */
public
class TestSchduler
{
	 
	 public static
	 void main ( String[] args )
					 throws
					 InterruptedException
	 {
			
			Scheduler scheduler = Scheduler.getScheduler ( 3 );
			for ( int i = 0 ; i < 10 ; ++i )
			{
				 Task1 task1 = new Task1 ( );
//				 System.err.println ( i + "   " + task1.toString ( ) );
				 scheduler.schedule ( task1 );
			}
			for ( int i = 0 ; i < 4 ; ++i )
				 scheduler.schedule ( new Task2 ( ) );
			Thread.sleep ( 100000 );
			scheduler.terminate ( );
	 }
	 
	 static
	 class Task1
					 extends ScheduleTask
	 {
			
			long time = 0;
			
			@Override
			public
			int interval ( )
			{
				 
				 return 1000;
			}
			
			@Override
			protected
			void execute ( )
			{
				 
				 System.out.println ( "ZZZZZ		" + this.getClass ( )
																								.getCanonicalName ( ) + "  " + ( System.currentTimeMillis ( ) - time )
															+ "   INTERVAL=" + interval ( ) );
				 time = System.currentTimeMillis ( );
			}
			
			@Override
			public
			String toString ( )
			{
				 
				 return this.getClass ( )
										.getCanonicalName ( );
			}
	 }
	 
	 static
	 class Task2
					 extends ScheduleTask
	 {
			
			long time = 0;
			
			@Override
			public
			int interval ( )
			{
				 
				 return 3000;
			}
			
			@Override
			protected
			void execute ( )
			{
				 
				 System.out.println ( "ZZZZZ		" + this.getClass ( )
																								.getCanonicalName ( ) + "  " + ( System.currentTimeMillis ( ) - time )
															+ "   INTERVAL=" + interval ( ) );
				 time = System.currentTimeMillis ( );
			}
			
			@Override
			public
			String toString ( )
			{
				 
				 return this.getClass ( )
										.getCanonicalName ( );
			}
			
	 }
}
