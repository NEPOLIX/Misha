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

import com.nepolix.misha.commons.utils.Utils;
import com.nepolix.misha.id.client.MishaID;

/**
 * @author Behrooz Shahriari
 * @since 11/14/16
 */
public
class MModelGen < T extends MModel >
{
	 
	 
	 private final MishaID mishaID;
	 
	 public
	 MModelGen ( MishaID mishaID )
	 {
			
			this.mishaID = mishaID;
	 }
	 
	 public 
	 T buildMishaModel ( Class< T > clazz )
	 {
			
			try
			{
				 T mModel = clazz.newInstance ( );
				 mModel.setMid ( mishaID.nextID ( mModel.modelName ( ) ) );
				 mModel.setCreatedDate ( Utils.getCurrentFormattedUTCTime ( ) );
				 mModel.setCreateTime ( Utils.getCurrentUTCTime ( ) );
				 return mModel;
			}
			catch ( ClassCastException | IllegalAccessException | InstantiationException e )
			{
				 e.printStackTrace ( );
			}
			return null;
	 }
}
