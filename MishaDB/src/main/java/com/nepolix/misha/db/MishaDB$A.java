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

package com.nepolix.misha.db;


import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.db.model.MModelGen;
import com.nepolix.misha.id.client.MishaID;

/**
 * @author Behrooz Shahriari
 * @since 2/13/18
 */
public abstract
class MishaDB$A
				implements MishaDB
{
	 
	 private static MModelGen MODEL_GEN;
	 
	 protected
	 MishaDB$A ( MishaID mishaID )
	 {
			
			if ( MODEL_GEN == null ) MODEL_GEN = new MModelGen ( mishaID );
			init ( );
	 }
	 
	 protected
	 MishaDB$A ( )
	 {
			
			init ( );
	 }
	 
	 public
	 < T extends MModel > T buildMishaModel ( Class< T > clazz )
	 {
			
			return ( T ) MODEL_GEN.buildMishaModel ( clazz );
	 }
	 
	 protected abstract
	 void init ( );
}
