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

import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.callback.Callback;
import com.nepolix.misha.task.handler.core.task.callback.condition.ConditionalTask;
import com.nepolix.misha.task.handler.core.task.callback.condition.TaskCondition;

/**
 * @author Behrooz Shahriari
 * @since 10/31/16
 */
public
class TestConditionalTask
{
	 
	 public static
	 void main ( String[] args )
	 {
			
			ITaskEngine taskRunner = ITaskEngine.buildTaskEngine ( true );
			Callback callback = new Callback ( )
			{
				 
				 @Override
				 public
				 void onResult ( Object result )
				 {
						
						System.out.println ( "xxx" + result );
				 }
				 
				 @Override
				 public
				 void onError ( JSONObject e )
				 {
						
				 }
			};
			taskRunner.add ( new MyConditionalTask ( null , callback ) );
			
	 }
	 
	 private static
	 class MyConditionalTask
					 extends ConditionalTask
	 {
			
			private int count = 0;
			
			
			public
			MyConditionalTask ( Callback callback ,
													Callback finalCallback )
			{
				 
				 super ( callback , finalCallback );
				 taskCondition = new TaskCondition ( )
				 {
						
						@Override
						public
						boolean passCondition ( )
						{
							 
							 return count >= 10;
						}
				 };
			}
			
			@Override
			public
			TaskCondition taskCondition ( )
			{
				 
				 return taskCondition;
			}
			
			@Override
			protected
			void callBackExecute ( ITaskEngine iTaskEngine ,
														 TaskListener listener )
							throws
							Exception
			{
				 
				 count++;
				 System.out.println ( "step=" + count );
//				 listener.setResult ( "" + count );
				 listener.finish ( );
			}
	 }
	 
}
