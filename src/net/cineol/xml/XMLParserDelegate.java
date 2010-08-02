package net.cineol.xml;

public interface XMLParserDelegate {
	public void xmlParserDidFindAttribute(XMLParser parser, String attributeName, String attributeValue);
	public void xmlParserDidEndTag(XMLParser parser, String tag);
	public void xmlParserDidStartDocument(XMLParser parser);
	public void xmlParserDidEndDocument(XMLParser parser);
}
