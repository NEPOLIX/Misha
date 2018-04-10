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

package com.nepolix.misha.web.server.rest.calls;


import com.nepolix.misha.web.server.rest.Request;
import com.nepolix.misha.web.server.rest.Response;

/**
 * @author Behrooz Shahriari
 * @since 9/18/16
 */
public
class RESTCall
{
	 
	 
	 private GetCall getCall;
	 
	 private PostCall postCall;
	 
	 private PutCall putCall;
	 
	 private DeleteCall deleteCall;
	 
	 
	 public
	 GetCall getGetCall ( )
	 {
			
			return getCall;
	 }
	 
	 public
	 void setGetCall ( GetCall getCall )
	 {
			
			this.getCall = getCall;
	 }
	 
	 public
	 PostCall getPostCall ( )
	 {
			
			return postCall;
	 }
	 
	 public
	 void setPostCall ( PostCall postCall )
	 {
			
			this.postCall = postCall;
	 }
	 
	 public
	 PutCall getPutCall ( )
	 {
			
			return putCall;
	 }
	 
	 public
	 void setPutCall ( PutCall putCall )
	 {
			
			this.putCall = putCall;
	 }
	 
	 public
	 DeleteCall getDeleteCall ( )
	 {
			
			return deleteCall;
	 }
	 
	 public
	 void setDeleteCall ( DeleteCall deleteCall )
	 {
			
			this.deleteCall = deleteCall;
	 }
	 
	 /****
		*
		*/
	 
	 private final static GetCall NULL_GET_CALL = new GetCall ( )
	 {
			
			@Override
			public
			void apiCall ( Request request ,
										 Response response )
			{
				 
			}
			
			@Override
			public
			String callPath ( )
			{
				 
				 return null;
			}
	 };
	 
	 private final static PostCall NULL_POST_CALL = new PostCall ( )
	 {
			
			@Override
			public
			void apiCall ( Request request ,
										 Response response )
			{
				 
			}
			
			@Override
			public
			String callPath ( )
			{
				 
				 return null;
			}
	 };
	 
	 
	 private final static PutCall NULL_OUT_CALL = new PutCall ( )
	 {
			
			@Override
			public
			void apiCall ( Request request ,
										 Response response )
			{
				 
			}
			
			@Override
			public
			String callPath ( )
			{
				 
				 return null;
			}
	 };
	 
	 
	 private final static DeleteCall NULL_DELETE_CALL = new DeleteCall ( )
	 {
			
			@Override
			public
			void apiCall ( Request request ,
										 Response response )
			{
				 
			}
			
			@Override
			public
			String callPath ( )
			{
				 
				 return null;
			}
	 };
	 
	 public static
	 GetCall getNullGetCall ( )
	 {
			
			return NULL_GET_CALL;
	 }
	 
	 public static
	 PostCall getNullPostCall ( )
	 {
			
			return NULL_POST_CALL;
	 }
	 
	 public static
	 PutCall getNullOutCall ( )
	 {
			
			return NULL_OUT_CALL;
	 }
	 
	 public static
	 DeleteCall getNullDeleteCall ( )
	 {
			
			return NULL_DELETE_CALL;
	 }
}
