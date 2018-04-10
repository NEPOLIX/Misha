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

package com.nepolix.misha.task.handler.core.task.callback;


import com.nepolix.misha.json.JSONException;
import com.nepolix.misha.json.JSONObject;
import com.nepolix.misha.task.handler.core.engine.ITaskEngine;
import com.nepolix.misha.task.handler.core.engine.TaskListener;
import com.nepolix.misha.task.handler.core.task.Task;
import com.nepolix.misha.task.handler.core.task.TaskType;

/**
 * @author Behrooz Shahriari
 * @since 8/8/16
 */
public abstract
class CallbackTask < R >
				extends Task
{
	 
	 
	 private Callback< R > callback;
	 
	 protected R result;
	 
	 protected JSONObject error;
	 
	 public
	 CallbackTask ( Callback< R > callback )
	 {
			
			super ( TaskType.SINGLE );
			this.callback = callback;
	 }
	 
	 /**
		* not used NORMALLY but can be useful
		*
		* @param taskType
		* @param callback
		*/
	 public
	 CallbackTask ( TaskType taskType ,
									Callback< R > callback )
	 {
			
			super ( taskType );
			this.callback = callback;
	 }
	 
	 /**
		* not used NORMALLY but can be useful
		*
		* @param taskType
		* @param repetition
		* @param callback
		*/
	 public
	 CallbackTask ( TaskType taskType ,
									int repetition ,
									Callback< R > callback )
	 {
			
			super ( taskType , repetition );
			this.callback = callback;
	 }
	 
	 
	 public
	 void setCallback ( Callback< R > callback )
	 {
			
			this.callback = callback;
	 }
	 
	 @Override
	 public
	 void execute ( ITaskEngine iTaskEngine ,
									TaskListener listener )
	 {
			
			try
			{
				 TaskListener< R > inListener = new TaskListener< R > ( )
				 {
						
						@Override
						public
						void finish ( )
						{
							 
							 listener.finish ( );
						}
						
						@Override
						public
						void setResult ( R result_ )
						{
							 
							 result = result_;
							 listener.setResult ( result_ );
							 if ( callback != null )
							 {
									if ( result != null && result.getClass ( ).equals ( JSONObject.class ) && ( ( JSONObject ) result ).has ( "error" ) )
									{
										 callback.onError ( ( JSONObject ) result );
									}
									else
									{
										 callback.onResult ( result );
									}
							 }
						}
				 };
				 callBackExecute ( iTaskEngine , inListener );
				 
			}
			catch ( Exception e )
			{
//				 e.printStackTrace ( );
				 this.error = JSONException.exceptionToJSON ( e );
				 listener.setResult ( this.error );
				 listener.finish ( );
				 if ( callback != null ) callback.onError ( this.error );
			}
	 }
	 
	 protected abstract
	 < R > void callBackExecute ( ITaskEngine iTaskEngine ,
																final TaskListener< R > listener )
					 throws
					 Exception;
	 
	 public
	 R getResult ( )
	 {
			
			return result;
	 }
	 
	 public
	 JSONObject getError ( )
	 {
			
			return error;
	 }
	 
	 public
	 Callback< R > getCallback ( )
	 {
			
			return callback;
	 }
}
