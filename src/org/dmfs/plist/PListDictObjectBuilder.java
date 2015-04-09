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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.builder.AbstractObjectBuilder;
import org.dmfs.xmlobjects.pull.ParserContext;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.dmfs.xmlobjects.serializer.SerializerContext;
import org.dmfs.xmlobjects.serializer.SerializerException;
import org.dmfs.xmlobjects.serializer.XmlObjectSerializer.IXmlChildWriter;


/**
 * A builder for plist dict elements.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PListDictObjectBuilder extends AbstractObjectBuilder<Map<String, ?>>
{

	public final static PListDictObjectBuilder INSTANCE = new PListDictObjectBuilder();

	/**
	 * Since <code>null</code> is not a valid key in PLIST dicts we can use it as key to the current key untile the value has been parsed.
	 */
	private final static String CURRENT_ELEMENT_KEY = null;


	private PListDictObjectBuilder()
	{
	}


	@Override
	public Map<String, ?> get(ElementDescriptor<Map<String, ?>> descriptor, Map<String, ?> recycle, ParserContext context) throws XmlObjectPullParserException
	{
		if (recycle != null)
		{
			recycle.clear();
			return recycle;
		}
		else
		{
			return new HashMap<String, Object>(16);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public <V> Map<String, ?> update(ElementDescriptor<Map<String, ?>> descriptor, Map<String, ?> object, ElementDescriptor<V> childDescriptor, V child,
		ParserContext context) throws XmlObjectPullParserException
	{
		if (childDescriptor == PList.KEY)
		{
			((Map<String, Object>) object).put(CURRENT_ELEMENT_KEY, child.toString());
		}
		else
		{
			String key = (String) object.remove(CURRENT_ELEMENT_KEY);
			if (key != null)
			{
				((Map<String, Object>) object).put(key, child);
			}
			else
			{
				throw new XmlObjectPullParserException("Found dict value without key");
			}
		}
		return object;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void writeChildren(ElementDescriptor<Map<String, ?>> descriptor, Map<String, ?> object, IXmlChildWriter childWriter, SerializerContext context)
		throws SerializerException, IOException
	{
		for (Entry<String, ?> element : object.entrySet())
		{
			String key = element.getKey();
			Object value = element.getValue();
			childWriter.writeChild(PList.KEY, key, context);

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
