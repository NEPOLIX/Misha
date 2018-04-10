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

package com.nepolix.misha.db.model;

/**
 * @author Behrooz Shahriari
 * @since 2/26/18
 */
public abstract
class UMModel
				extends MModel
{
	 
	 private String uid;
	 
	 public
	 UMModel ( )
	 {
			
			super ( );
	 }
	 
	 public
	 String getUid ( )
	 {
			
			return uid;
	 }
	 
	 public
	 void setUid ( String uid )
	 {
			
			this.uid = uid;
	 }
	 
	 @Override
	 public
	 int hashCode ( )
	 {
			
			return ( uid + "_" + mid ).hashCode ( );
	 }
	 
	 @Override
	 public
	 boolean equals ( Object obj )
	 {
			
			if ( obj == null ) return false;
			UMModel model = ( UMModel ) obj;
			return model.mid.equals ( mid ) && model.uid.equals ( uid );
	 }
}
