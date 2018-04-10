/******************************************************************************
 * Copyright © 2016-7532 HEX, Inc. [7EPOLIX]-(Behrooz Shahriari)              *
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

import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.engine.chainer.Chainer;
import com.nepolix.misha.task.handler.core.engine.chainer.ChainerTask;
import com.nepolix.misha.task.handler.core.task.callback.Callback;

/**
 * @author Behrooz Shahriari
 * @since 10/29/16
 */
public
class TestChainer
{
	 
	 public static
	 void main ( String[] args )
					 throws
					 JSONException
	 {
			
			ITaskEngine           TASK_RUNNER_POOL = ITaskEngine.buildTaskEngine ( false );
			Chainer< JSONObject > chainer          = Chainer.buildChainer ( TASK_RUNNER_POOL );
			JSONObject            jsonObject       = new JSONObject ( );
//			jsonObject.put ( "ZERO", 0 );
			chainer.first ( jsonObject , new ChainerTask< JSONObject > ( )
			{
				 
				 @Override
				 protected
				 void task ( JSONObject previousResult ,
										 TaskListener listener )
								 throws
								 Exception
				 {
						
						System.out.println ( "FIRST" );
						JSONObject jsonObject = new JSONObject ( previousResult.toString ( ) );
						jsonObject.put ( "ONE" , previousResult.optInt ( "ZERO" ) + 1 );
						listener.setResult ( jsonObject );
						listener.finish ( );
				 }
			} ).chain ( new ChainerTask< JSONObject > ( )
			{
				 
				 @Override
				 protected
				 void task ( JSONObject previousResult ,
										 TaskListener listener )
								 throws
								 Exception
				 {
						
						System.out.println ( "CHAIN" );
						JSONObject jsonObject = new JSONObject ( previousResult.toString ( ) );
						jsonObject.put ( "TWO" , previousResult.getInt ( "ONE" ) + 1 );
						listener.setResult ( jsonObject );
						listener.finish ( );
				 }
			} ).run ( new Callback< JSONObject > ( )
			{
				 
				 @Override
				 public
				 void onResult ( JSONObject result )
				 {
						
						try
						{
							 System.out.println ( ">>  " + result.toString ( 2 ) );
						}
						catch ( JSONException e )
						{
							 e.printStackTrace ( );
						}
				 }
				 
				 @Override
				 public
				 void onError ( JSONObject e )
				 {
						
						try
						{
							 System.err.println ( e.toString ( 2 ) );
						}
						catch ( JSONException e1 )
						{
							 e1.printStackTrace ( );
						}
				 }
			} );
	 }
}
