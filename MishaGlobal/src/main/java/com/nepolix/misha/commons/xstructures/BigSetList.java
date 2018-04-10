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

package com.nepolix.misha.commons.xstructures;

import java.util.*;

/**
 * @author Behrooz Shahriari
 * @since 5/3/17
 */
public
class BigSetList < E >
{
	 
	 private LinkedHashMap< Integer, TreeSet< E > > mapSetList;
	 
	 private int idx = 0;
	 
	 public
	 BigSetList ( )
	 {
			
			mapSetList = new LinkedHashMap<> ( );
	 }
	 
	 public
	 LinkedHashMap< Integer, TreeSet< E > > getMapSetList ( )
	 {
			
			return mapSetList;
	 }
	 
	 public
	 void setMapSetList ( LinkedHashMap< Integer, TreeSet< E > > mapSetList )
	 {
			
			this.mapSetList = mapSetList;
	 }
	 
	 public
	 int getIdx ( )
	 {
			
			return idx;
	 }
	 
	 /**
		* NEVER USE IT
		*
		* @param idx
		*/
	 public
	 void setIdx ( int idx )
	 {
			
			this.idx = idx;
	 }
	 
	 public
	 void $addAll ( BigSetList< E > bigSetList )
	 {
			
			if ( bigSetList != null )
			{
				 int ic = mapSetList.keySet ( ).size ( );
				 LinkedHashMap< Integer, TreeSet< E > > map = bigSetList.getMapSetList ( );
				 for ( int k : map.keySet ( ) )
				 {
						mapSetList.put ( ic , map.get ( k ) );
						ic++;
				 }
			}
	 }
	 
	 public
	 void $addElement ( E e )
	 {
			
			TreeSet< E > ids = mapSetList.computeIfAbsent ( idx , k -> new TreeSet<> ( ) );
			if ( ids.size ( ) > Integer.MAX_VALUE / 2 )
			{
				 idx++;
				 ids = new TreeSet<> ( );
				 mapSetList.put ( idx , ids );
			}
			ids.add ( e );
	 }
	 
	 public
	 long size ( )
	 {
			
			long count = 0;
			for ( Integer i : mapSetList.keySet ( ) )
			{
				 TreeSet set = mapSetList.get ( i );
				 count += set.size ( );
			}
			return count;
	 }
	 
	 public
	 Iterator< E > $getListIterator ( )
	 {
			
			return new Iterator< E > ( )
			{
				 
				 private int idx = -1;
				 
				 private Iterator< E > iterator;
				 
				 private long size = size ( );
				 
				 private long ic = 0;
				 
				 @Override
				 public
				 boolean hasNext ( )
				 {
						
						return ic < size;
				 }
				 
				 @Override
				 public
				 E next ( )
				 {
						
						if ( iterator == null )
						{
							 idx = 0;
							 iterator = getMapSetList ( ).get ( idx ).iterator ( );
						}
						if ( !iterator.hasNext ( ) )
						{
							 idx++;
							 iterator = getMapSetList ( ).get ( idx ).iterator ( );
						}
						ic++;
						return iterator.next ( );
				 }
				 
				 @Override
				 public
				 void remove ( )
				 {
						
						iterator.remove ( );
						size--;
						ic--;
				 }
			};
	 }
}
