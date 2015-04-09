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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.builder.AbstractObjectBuilder;
import org.dmfs.xmlobjects.builder.ListObjectBuilder;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.dmfs.xmlobjects.serializer.SerializerContext;
import org.dmfs.xmlobjects.serializer.SerializerException;
import org.dmfs.xmlobjects.serializer.XmlObjectSerializer.IXmlChildWriter;


/**
 * A builder for plist array elements. In contrast to {@link ListObjectBuilder} it accepts any child element type.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PListArrayObjectBuilder extends AbstractObjectBuilder<List<?>>
{

	public final static PListArrayObjectBuilder INSTANCE = new PListArrayObjectBuilder();


	private PListArrayObjectBuilder()
	{
	}


	@Override
	public List<?> get(ElementDescriptor<List<?>> descriptor, List<?> recycle, ParserContext context) throws XmlObjectPullParserException
	{
		if (recycle != null)
		{
			recycle.clear();
			return recycle;
		}
		else
		{
			return new ArrayList<Object>(16);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public <V> List<?> update(ElementDescriptor<List<?>> descriptor, List<?> object, ElementDescriptor<V> childDescriptor, V child, ParserContext context)
		throws XmlObjectPullParserException
	{
		((List<Object>) object).add(child);
		return object;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void writeChildren(ElementDescriptor<List<?>> descriptor, List<?> object, IXmlChildWriter childWriter, SerializerContext context)
		throws SerializerException, IOException
	{
		for (Object value : object)
		{
			if (value == null)
			{
				// null values are not allowed
				continue;
			}

			if (value instanceof List)
			{
				childWriter.writeChild(PList.ARRAY, (List<Object>) value, context);
			}
			else if (value instanceof Map)
			{
				childWriter.writeChild(PList.DICT, (Map<String, Object>) value, context);
			}
			else if (value instanceof String)
			{
				childWriter.writeChild(PList.STRING, (String) value, context);
			}
			else if (value instanceof Integer)
			{
				childWriter.writeChild(PList.INTEGER, (Integer) value, context);
			}
			else if (value instanceof Double)
			{
				childWriter.writeChild(PList.REAL, (Double) value, context);
			}
			else if (value instanceof Boolean)
			{
				if ((Boolean) value)
				{
					childWriter.writeChild(PList.TRUE, (Boolean) value, context);
				}
				else
				{
					childWriter.writeChild(PList.FALSE, (Boolean) value, context);
				}
			}
		}
	}
}
