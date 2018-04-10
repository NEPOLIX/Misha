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

import com.nepolix.misha.commons.xstructures.ModelIDList;
import com.nepolix.misha.db.model.MModel;
import com.nepolix.misha.id.exception.MishaIDException;
import com.nepolix.misha.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Behrooz Shahriari
 * @since 2/13/18
 */
public
interface MishaDB
{
	 
	 < T extends MModel > T buildMishaModel ( Class< T > clazz );
	 
	 < T extends MModel > T findOne ( JSONObject query ,
																		Class< T > clazz );
	 
	 < T extends MModel > T findOne ( String mid ,
																		Class< T > clazz );
	 
	 public
	 JSONObject findOne ( String mid ,
												String collectionName );
	 
	 < T extends MModel > List< T > find ( JSONObject query ,
																				 int limit ,
																				 int offset ,
																				 Class< T > objectClass );
	 
	 List< JSONObject > find ( JSONObject query ,
														 int limit ,
														 int offset ,
														 String collectionName );
	 
	 < T extends MModel > Set< T > findObjects ( Collection< String > mids ,
																							 Class< T > clazz );
	 
	 < T extends MModel > MishaDB save ( Collection< T > objects );
	 
	 void save ( String collectionName ,
							 JSONObject object );
	 
	 void save ( MModel object )
					 throws
					 MishaIDException,
					 NullPointerException;
	 
	 < T extends MModel > int delete ( JSONObject query ,
																		 Class< T > clazz );
	 
	 int deleteObjects ( Collection< String > mids ,
											 String collectionName );
	 
	 < T extends MModel > ModelIDList getCollectionIDs ( Class< T > modelClass );
}
