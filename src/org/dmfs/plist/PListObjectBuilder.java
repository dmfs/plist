/*
 * Copyright (C) 2014 Marten Gajda <marten@dmfs.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.dmfs.plist;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.builder.AbstractObjectBuilder;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.dmfs.xmlobjects.serializer.SerializerContext;
import org.dmfs.xmlobjects.serializer.SerializerException;
import org.dmfs.xmlobjects.serializer.XmlObjectSerializer.IXmlAttributeWriter;
import org.dmfs.xmlobjects.serializer.XmlObjectSerializer.IXmlChildWriter;


/**
 * A builder for List elements.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PListObjectBuilder extends AbstractObjectBuilder<PList>
{

	public final static QualifiedName VERSION = QualifiedName.get("version");

	public final static PListObjectBuilder INSTANCE = new PListObjectBuilder();


	@Override
	public PList get(ElementDescriptor<PList> descriptor, PList recycle, ParserContext context)
	{
		if (recycle != null)
		{
			recycle.recycle();
			return recycle;
		}
		PList result = new PList();
		return result;
	}


	@Override
	public PList update(ElementDescriptor<PList> descriptor, PList object, QualifiedName attribute, String value, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (VERSION == attribute)
		{
			object.mVersion = value;
		}
		return object;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <V> PList update(ElementDescriptor<PList> descriptor, PList object, ElementDescriptor<V> childDescriptor, V child, ParserContext context)
		throws XmlObjectPullParserException
	{
		if (childDescriptor == PList.DICT)
		{
			object.dict = (Map<String, Object>) child;
		}
		else if (childDescriptor == PList.ARRAY)
		{
			object.array = (List<Object>) child;
		}
		else if (childDescriptor == PList.STRING)
		{
			object.string = (String) child;
		}
		else if (childDescriptor == PList.DATA)
		{
			object.data = (String) child;
		}
		else if (childDescriptor == PList.INTEGER)
		{
			object.integer = (Integer) child;
		}
		else if (childDescriptor == PList.REAL)
		{
			object.real = (Double) child;
		}
		else if (childDescriptor == PList.TRUE || childDescriptor == PList.FALSE)
		{
			object.bool = (Boolean) child;
		}
		return object;
	}


	@Override
	public void writeAttributes(ElementDescriptor<PList> descriptor, PList object, IXmlAttributeWriter attributeWriter, SerializerContext context)
		throws SerializerException, IOException
	{
		if (object.mVersion != null)
		{
			attributeWriter.writeAttribute(PListObjectBuilder.VERSION, object.mVersion, context);
		}
	}


	@Override
	public void writeChildren(ElementDescriptor<PList> descriptor, PList object, IXmlChildWriter childWriter, SerializerContext context)
		throws SerializerException, IOException
	{
		if (object.array != null)
		{
			childWriter.writeChild(PList.ARRAY, object.array, context);
		}
		else if (object.dict != null)
		{
			childWriter.writeChild(PList.DICT, object.dict, context);
		}
		else if (object.string != null)
		{
			childWriter.writeChild(PList.STRING, object.string, context);
		}
		else if (object.integer != null)
		{
			childWriter.writeChild(PList.INTEGER, object.integer, context);
		}
		else if (object.real != null)
		{
			childWriter.writeChild(PList.REAL, object.real, context);
		}
		else if (object.bool != null)
		{
			if (object.bool)
			{
				childWriter.writeChild(PList.TRUE, object.bool, context);
			}
			else
			{
				childWriter.writeChild(PList.FALSE, object.bool, context);
			}
		}

	}
}
