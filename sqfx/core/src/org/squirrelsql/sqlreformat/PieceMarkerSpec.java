package org.squirrelsql.sqlreformat;

import org.squirrelsql.settings.SQLKeyWord;

/*
 * Copyright (C) 2003 Gerd Wagner
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
public class PieceMarkerSpec
{
	public static final int TYPE_PIECE_MARKER_AT_BEGIN = 0;
	public static final int TYPE_PIECE_MARKER_AT_END = 1;
	public static final int TYPE_PIECE_MARKER_IN_OWN_PIECE = 2;

	private String _pieceMarker;
   private int _type;

	public PieceMarkerSpec(String pieceMarker, int type)
	{
		this._pieceMarker = pieceMarker;

		if(TYPE_PIECE_MARKER_AT_BEGIN != type && TYPE_PIECE_MARKER_AT_END != type && TYPE_PIECE_MARKER_IN_OWN_PIECE != type)
		{
			throw new IllegalArgumentException("Unknow type: " + type);
		}

		this._type = type;
	}

	public String getPieceMarker()
	{
		return _pieceMarker;
	}

	public int getType()
	{
		return _type;
	}

	public int getLengthRightSpaced()
	{
		if(1 == _pieceMarker.length())
		{
			return _pieceMarker.length();
		}
		else
		{
			return _pieceMarker.length() + 1;
		}
	}

	public String getLeftSpace()
	{
		if(1 == _pieceMarker.length())
		{
			return "";
		}
		else
		{
			return " ";
		}
	}

	public boolean needsSuroundingWhiteSpaces()
	{
		if(1 == _pieceMarker.length())
		{
			return false;
		}
		else
		{
			return true;
		}
	}


	public boolean is_FROM_begin()
	{
		return SQLKeyWord.FROM.name().equalsIgnoreCase(_pieceMarker);
	}

	public boolean is_FROM_end()
	{
		return    SQLKeyWord.WHERE.name().equalsIgnoreCase(_pieceMarker)
				 || SQLKeyWord.GROUP.name().equalsIgnoreCase(_pieceMarker)
				 || SQLKeyWord.ORDER.name().equalsIgnoreCase(_pieceMarker)
				 || SQLKeyWord.UNION.name().equalsIgnoreCase(_pieceMarker);
	}

	public boolean is_AND_or_OR()
	{
		return    SQLKeyWord.AND.name().equalsIgnoreCase(_pieceMarker)
				 || SQLKeyWord.OR.name().equalsIgnoreCase(_pieceMarker);
	}
}
