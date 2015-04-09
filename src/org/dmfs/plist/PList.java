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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.dmfs.xmlobjects.ElementDescriptor;
import org.dmfs.xmlobjects.QualifiedName;
import org.dmfs.xmlobjects.builder.DoubleObjectBuilder;
import org.dmfs.xmlobjects.builder.IntegerObjectBuilder;
import org.dmfs.xmlobjects.builder.StringObjectBuilder;
import org.dmfs.xmlobjects.pull.Recyclable;
import org.dmfs.xmlobjects.pull.XmlObjectPull;
import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.dmfs.xmlobjects.pull.XmlPath;
import org.dmfs.xmlobjects.serializer.SerializerContext;
import org.dmfs.xmlobjects.serializer.SerializerException;
import org.dmfs.xmlobjects.serializer.XmlObjectSerializer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * A simple Java implementation of Apple plists.
 * 
 * <p>
 * TODO: add support for base 64 data & date.
 * </p>
 * <p>
 * TODO: add a way to build plists easier
 * </p>
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class PList implements Recyclable
{
	private final static XmlPath EMPTY_PATH = new XmlPath();
	private final static String VERSION_1_0 = "1.0";

	final static ElementDescriptor<PList> PLIST = ElementDescriptor.register(QualifiedName.get("plist"), PListObjectBuilder.INSTANCE);
	final static ElementDescriptor<String> KEY = ElementDescriptor.register(QualifiedName.get("key"), StringObjectBuilder.INSTANCE);

	final static ElementDescriptor<String> STRING = ElementDescriptor.register(QualifiedName.get("string"), StringObjectBuilder.INSTANCE);
	final static ElementDescriptor<Integer> INTEGER = ElementDescriptor.register(QualifiedName.get("integer"), IntegerObjectBuilder.INSTANCE_STRICT);
	final static ElementDescriptor<Double> REAL = ElementDescriptor.register(QualifiedName.get("real"), DoubleObjectBuilder.INSTANCE_STRICT);
	final static ElementDescriptor<String> DATA = ElementDescriptor.register(QualifiedName.get("data"), StringObjectBuilder.INSTANCE);

	final static ElementDescriptor<Boolean> TRUE = ElementDescriptor.register(QualifiedName.get("true"), PListBooleanObjectBuilder.INSTANCE);
	final static ElementDescriptor<Boolean> FALSE = ElementDescriptor.register(QualifiedName.get("false"), PListBooleanObjectBuilder.INSTANCE);

	final static ElementDescriptor<List<?>> ARRAY = ElementDescriptor.register(QualifiedName.get("array"), PListArrayObjectBuilder.INSTANCE);
	final static ElementDescriptor<Map<String, ?>> DICT = ElementDescriptor.register(QualifiedName.get("dict"), PListDictObjectBuilder.INSTANCE);

	String mVersion;
	Map<String, ?> dict = null;
	List<?> array = null;
	String string = null;
	String data = null;
	Integer integer = null;
	Double real = null;
	Boolean bool = false;


	/**
	 * Read a {@link PList} from the given {@link InputStream}.
	 * 
	 * @param in
	 *            The {@link InputStream} to read from.
	 * @param charset
	 *            The character set the stream uses.
	 * @return A {@link PList} or <code>null</code> if there was no plist in the stream.
	 * @throws UnsupportedEncodingException
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws XmlObjectPullParserException
	 */
	public static PList read(InputStream in, String charset) throws UnsupportedEncodingException, XmlPullParserException, IOException,
		XmlObjectPullParserException
	{
		return read(new InputStreamReader(in, charset));
	}


	/**
	 * Read a {@link PList} from the given {@link Reader}.
	 * 
	 * @param in
	 *            The {@link Reader} to read from.
	 * @return A {@link PList} or <code>null</code> if there was no plist in the stream.
	 * @throws UnsupportedEncodingException
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws XmlObjectPullParserException
	 */
	public static PList read(Reader in) throws XmlPullParserException, IOException, XmlObjectPullParserException
	{
		XmlPullParserFactory ppfactory = XmlPullParserFactory.newInstance();
		XmlPullParser parser = ppfactory.newPullParser();
		parser.setInput(in);

		XmlObjectPull op = new XmlObjectPull(parser);

		if (op.moveToNext(PList.PLIST, EMPTY_PATH))
		{
			return op.pull(PList.PLIST, null, EMPTY_PATH);
		}
		return null;
	}


	PList()
	{
	}


	public PList(Map<String, Object> dict)
	{
		this.dict = dict;
		this.mVersion = VERSION_1_0;
	}


	public PList(List<?> array)
	{
		this.array = array;
		this.mVersion = VERSION_1_0;
	}


	public PList(String string)
	{
		this.string = string;
		this.mVersion = VERSION_1_0;
	}


	public PList(int integer)
	{
		this.integer = integer;
		this.mVersion = VERSION_1_0;
	}


	public PList(double real)
	{
		this.real = real;
		this.mVersion = VERSION_1_0;
	}


	public PList(boolean bool)
	{
		this.bool = bool;
		this.mVersion = VERSION_1_0;
	}


	public String getVersion()
	{
		return mVersion;
	}


	public Map<String, ?> getDict()
	{
		return dict;
	}


	public List<?> getArray()
	{
		return array;
	}


	public String getString()
	{
		return string;
	}


	public String getBase64Data()
	{
		return data;
	}


	public Integer getInteger()
	{
		return integer;
	}


	public Double getReal()
	{
		return real;
	}


	public Boolean getBoolean()
	{
		return bool;
	}


	/**
	 * Write the {@link PList} to the given {@link OutputStream} using the given charset.
	 * <p>
	 * <strong>Note:</strong> the output stream is not closed automatically, so it's up to the caller to do that.
	 * </p>
	 * 
	 * @param out
	 *            The {@link OutputStream} to write to.
	 * @param charset
	 *            The character set to use.
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws SerializerException
	 */
	public void write(OutputStream out, String charset) throws IOException, XmlPullParserException, SerializerException
	{
		SerializerContext serializerContext = new SerializerContext(null);
		XmlObjectSerializer o = new XmlObjectSerializer();
		o.setOutput(serializerContext, out, charset);
		o.serialize(serializerContext, PList.PLIST, this);
	}


	/**
	 * Write the {@link PList} to the given {@link Writer}.
	 * <p>
	 * <strong>Note:</strong> the writer is not closed automatically, so it's up to the caller to do that.
	 * </p>
	 * 
	 * @param out
	 *            The {@link Writer} to write to.
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws SerializerException
	 */
	public void write(Writer out) throws IOException, XmlPullParserException, SerializerException
	{
		SerializerContext serializerContext = new SerializerContext(null);
		XmlObjectSerializer o = new XmlObjectSerializer();
		o.setOutput(serializerContext, out);
		o.serialize(serializerContext, PList.PLIST, this);
	}


	@Override
	public String toString()
	{
		if (dict != null)
		{
			return "plist:version=" + mVersion + ",dict=" + dict.toString();
		}
		if (array != null)
		{
			return "plist:version=" + mVersion + ",array=" + array.toString();
		}
		if (string != null)
		{
			return "plist:version=" + mVersion + ",string=" + string;
		}
		if (data != null)
		{
			return "plist:version=" + mVersion + ",data=" + data;
		}
		if (integer != null)
		{
			return "plist:version=" + mVersion + ",integer=" + integer.toString();
		}
		if (real != null)
		{
			return "plist:version=" + mVersion + ",real=" + real.toString();
		}
		if (bool != null)
		{
			return "plist:version=" + mVersion + ",boolean=" + bool.toString();
		}
		return super.toString();
	}


	@Override
	public void recycle()
	{
		if (dict != null)
		{
			dict.clear();
		}
		if (array != null)
		{
			array.clear();
		}
		string = null;
		data = null;
		integer = null;
		real = null;
		bool = null;
		mVersion = null;
	}
}
