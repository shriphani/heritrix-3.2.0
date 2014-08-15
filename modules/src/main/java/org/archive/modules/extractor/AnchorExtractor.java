package org.archive.modules.extractor;
 
import org.archive.modules.CrawlURI;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
 
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 
 
import javax.xml.parsers.ParserConfigurationException;
 
/**
 * Created by shriphani on 3/14/14.
 *
 * Code to take an XPath, convert to an XML object and extract URLs
 * using XPath queries
 */
public class AnchorExtractor extends ExtractorHTML {
 
 
    @Override
    protected void extract(CrawlURI curi, CharSequence cs) {
 
        // process the html
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setPruneTags("script,style");
 
        TagNode node = cleaner.clean(cs.toString());
 
        // convert the HTML to an XML node
        try {
            Document document = new DomSerializer(new CleanerProperties()).createDOM(node);
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList nodes = (NodeList) xpath.evaluate(".//a", document, XPathConstants.NODESET);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                NamedNodeMap attributes = n.getAttributes();
 
                String href = null;
                try {
                    href = attributes.getNamedItem("href").getTextContent();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
 
                if (href != null && !href.isEmpty()) {
                    processEmbed(curi, href, elementContext(n.getNodeName(), "href"));
                }
            }
 
        } catch (ParserConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (XPathExpressionException e) {
            System.out.println(e.getMessage());
        }
    }
 
}
