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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmfs.xmlobjects.serializer.SerializerException;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;


/**
 * TODO: add more sophisticed tests with netsted dicts and arrays.
 */
public class TestPListSerializer
{

	@Test
	public void testSimpe() throws IOException, XmlPullParserException, SerializerException
	{
		assertEquals("<?xml version='1.0' ?><plist version=\"1.0\"><integer>1234</integer></plist>", serializePList(new PList(1234)));
		assertEquals("<?xml version='1.0' ?><plist version=\"1.0\"><real>1234.456</real></plist>", serializePList(new PList(1234.456)));
		assertEquals("<?xml version='1.0' ?><plist version=\"1.0\"><string>abc1234</string></plist>", serializePList(new PList("abc1234")));
		assertEquals("<?xml version='1.0' ?><plist version=\"1.0\"><true /></plist>", serializePList(new PList(true)));
		assertEquals("<?xml version='1.0' ?><plist version=\"1.0\"><false /></plist>", serializePList(new PList(false)));

		List<?> array1 = Arrays.asList(new Object[] { "a", "b", "c" });
		assertEquals("<?xml version='1.0' ?><plist version=\"1.0\"><array><string>a</string><string>b</string><string>c</string></array></plist>",
			serializePList(new PList(array1)));

		List<?> array2 = Arrays.asList(new Object[] { "a", "b", "c", 1, 1.1, true });
		assertEquals(
			"<?xml version='1.0' ?><plist version=\"1.0\"><array><string>a</string><string>b</string><string>c</string><integer>1</integer><real>1.1</real><true /></array></plist>",
			serializePList(new PList(array2)));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", "abc");
		map.put("key2", 123);
		map.put("key3", 123.456);
		map.put("key4", false);

		String serialized = serializePList(new PList(map));
		serialized.startsWith("<?xml version='1.0' ?><plist version=\"1.0\"><dict><");
		serialized.endsWith("></dict></plist>");
		serialized.contains("><key>key1</key><string>abc</string><");
		serialized.contains("><key>key2</key><integer>123</integer><");
		serialized.contains("><key>key3</key><real>123.456</real><");
		serialized.contains("><key>key4</key><false /><");
		assertEquals(
			"<?xml version='1.0' ?><plist version=\"1.0\"><dict><key>key1</key><string>abc</string><key>key2</key><integer>123</integer><key>key3</key><real>123.456</real><key>key4</key><false /></dict></plist>"
				.length(), serialized.length());
	}


	private String serializePList(PList plist) throws IOException, XmlPullParserException, SerializerException
	{
		StringWriter result = new StringWriter(4096);
		plist.write(result);
		result.close();
		return result.toString();
	}
}
