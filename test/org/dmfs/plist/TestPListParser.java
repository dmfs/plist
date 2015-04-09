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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.dmfs.xmlobjects.pull.XmlObjectPullParserException;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;


/**
 * TODO: add more sophisticated tests with nested dicts and arrays.
 */
public class TestPListParser
{

	@Test
	public void test() throws XmlPullParserException, IOException, XmlObjectPullParserException
	{
		assertEquals("abc", parsePList("<?xml version='1.0' ?><plist version=\"1.0\"><string>abc</string></plist>").getString());
		assertEquals(123, (int) parsePList("<?xml version='1.0' ?><plist version=\"1.0\"><integer>123</integer></plist>").getInteger());
		assertEquals(123.456, (double) parsePList("<?xml version='1.0' ?><plist version=\"1.0\"><real>123.456</real></plist>").getReal(), 0.001);
		assertTrue(parsePList("<?xml version='1.0' ?><plist version=\"1.0\"><true /></plist>").getBoolean());
		assertFalse(parsePList("<?xml version='1.0' ?><plist version=\"1.0\"><false /></plist>").getBoolean());
		assertArrayEquals(new Object[] { "a", "b", "c" },
			parsePList("<?xml version='1.0' ?><plist version=\"1.0\"><array><string>a</string><string>b</string><string>c</string></array></plist>").getArray()
				.toArray());
		assertArrayEquals(
			new Object[] { "a", "b", "c", 1, 1.1, true },
			parsePList(
				"<?xml version='1.0' ?><plist version=\"1.0\"><array><string>a</string><string>b</string><string>c</string><integer>1</integer><real>1.1</real><true /></array></plist>")
				.getArray().toArray());

		Map<String, ?> map = parsePList("<?xml version='1.0' ?><plist version=\"1.0\"><dict><key>key1</key><string>abc</string><key>key2</key><integer>123</integer><key>key3</key><real>123.456</real><key>key4</key><false /></dict></plist>").dict;
		assertEquals(map.size(), 4);
		assertEquals("abc", map.get("key1"));
		assertEquals(123, map.get("key2"));
		assertEquals(123.456, (Double) map.get("key3"), 0.001);
		assertEquals(false, map.get("key4"));
	}


	private PList parsePList(String string) throws XmlPullParserException, IOException, XmlObjectPullParserException
	{
		return PList.read(new StringReader(string));
	}
}
