package org.young.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.young.db.ConnectionPool;

import youngPackage.db.YoungRepository;
import youngPackage.exception.YoungException;
import youngPackage.model.YoungContent;
import youngPackage.model.YoungContentType;
import youngPackage.model.YoungDocument;
import youngPackage.model.YoungNode;
import youngPackage.model.YoungNodeType;
import youngPackage.model.YoungProperty;

/**
 * The utility class of young-platform
 * 
 * @author haoweige@126.com
 */
@SuppressWarnings("unchecked")
public class YoungHelper {

	private static Logger logger = Logger.getLogger(YoungHelper.class);

	/** -----------------YoungContent----------------- **/

	public static YoungContent find(String primaryKeyName) {
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			YoungRepository repository = pool.getConnection();
			Object object = repository.find(YoungContentType.class,
					primaryKeyName);
			if (object instanceof YoungContentType) {
				YoungContentType type = (YoungContentType) object;
				return new YoungContent(type);
			}
		} catch (YoungException e) {
			logger.error(e);
			return null;
		} catch (Exception e) {
			logger.error(e);
			return null;
		}
		return null;
	}

	/** -----------------YoungNode----------------- **/

	private static final String[] NODE_PROPERTY_ARRAY = new String[] { "order",
			"text" };
	private static final int NODE_CAPACITY_MAX = 2000;
	private static final int NODE_ORDER_START = 10000;

	public static YoungContent addChildNode(YoungContent youngContent,
			String childNodeTypeName, String text) {
		return addChildNode(youngContent, childNodeTypeName,
				NODE_PROPERTY_ARRAY, text);
	}

	public static String parseText(YoungContent youngContent,
			String childNodeTypeName) {
		if (youngContent == null || StringUtils.isEmpty(childNodeTypeName))
			return null;
		Vector nodes = youngContent.getChild(childNodeTypeName);
		if (nodes == null)
			return null;
		Comparator nodeComparator = new Comparator() {

			@Override
			public int compare(Object object1, Object object2) {
				YoungNode node1 = (YoungNode) object1;
				YoungNode node2 = (YoungNode) object2;
				String order1 = getChildNodeOrder(node1);
				String order2 = getChildNodeOrder(node2);
				return order1.compareTo(order2);
			}
		};
		/* step 1 */
		Collections.sort(nodes, nodeComparator);
		/* step 2 */
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < nodes.size(); i++) {
			YoungNode node = (YoungNode) nodes.elementAt(i);
			Object value = node.getProperty(NODE_PROPERTY_ARRAY[0]).getValue();
			buf.append(value.toString());
		}
		return buf.toString();
	}

	private static String getChildNodeOrder(YoungNode youngNode) {
		YoungProperty youngProperty = youngNode
				.getProperty(NODE_PROPERTY_ARRAY[0]);
		Object object = youngProperty.getValue();
		if (object != null)
			return object.toString();
		return null;
	}

	@Deprecated
	public static YoungContent addChildNode(YoungContent youngContent,
			String childNodeTypeName, String[] nodePropertyNames, String text) {
		if (StringUtils.isEmpty(text) || StringUtils.isEmpty(childNodeTypeName))
			return youngContent;
		Vector nodes = youngContent.getChild(childNodeTypeName);
		if (nodes == null)
			return youngContent;
		/* the child node type exists */
		if (text.length() < NODE_CAPACITY_MAX) {
			String[] nodeValues = { text, "1" };
			addChildNode(youngContent, childNodeTypeName, nodePropertyNames,
					nodeValues);
		} else {
			int order = NODE_ORDER_START;
			int count = text.length() / NODE_CAPACITY_MAX;
			for (int i = 0; i < count; i++) {
				order++;
				String[] nodeValues = { String.valueOf(order),
						text.substring(0, NODE_CAPACITY_MAX) };
				text = text.substring(NODE_CAPACITY_MAX, text.length());
				addChildNode(youngContent, childNodeTypeName,
						nodePropertyNames, nodeValues);
			}
			if (text.length() > 0) {
				String[] nodeValues = { String.valueOf(order), text };
				addChildNode(youngContent, childNodeTypeName,
						nodePropertyNames, nodeValues);
			}
		}
		return youngContent;
	}

	public static void addChildNode(YoungContent youngContent,
			String childNodeTypeName, String[] nodeProperties,
			String[] nodeValues) {
		YoungContentType contentType = youngContent.getContentType();
		YoungNodeType nodeType = contentType.getChild(childNodeTypeName);
		YoungNode childNode = new YoungNode(nodeType);
		for (int i = 0; i < nodeProperties.length; i++) {
			childNode.setProperty(nodeProperties[i], nodeValues[i]);
		}
		youngContent.addChild(childNode);
	}

	/** -----------------YoungDocument----------------- **/

	public static YoungDocument convertDocument(String documentContentTypeName,
			String documentName, String documentMimeType, long documentSize,
			InputStream documentInputStream) {
		if (StringUtils.isEmpty(documentContentTypeName)
				|| StringUtils.isEmpty(documentName)
				|| StringUtils.isEmpty(documentMimeType)
				|| documentInputStream == null)
			return null;
		YoungDocument youngDocument = new YoungDocument(documentContentTypeName);
		youngDocument.setAbsSourceFileName(documentName);
		youngDocument.setMimeType(documentMimeType);
		youngDocument.setSize(documentSize);
		youngDocument.setInputStream(documentInputStream);
		return youngDocument;
	}

	public static void updateDocument(YoungContent youngContent,
			YoungDocument youngDocument) throws YoungException {
		String documentTypeName = youngDocument.getDocumentTypeName();
		if (youngContent.hasDocument(documentTypeName)) {
			Vector documents = youngContent.getDocuments(documentTypeName);
			Iterator iterator = documents.iterator();
			boolean first = true;
			while (iterator.hasNext()) {
				YoungDocument next = (YoungDocument) iterator.next();
				if (first) {// only persist one
					next = copyDocument(next, youngDocument);
					youngContent.updateDocument(next);
					first = false;
				} else {
					youngContent.deleteDocument(next);
				}
			}
		} else {
			youngContent.addDocument(youngDocument);
		}
	}

	/**
	 * copy uploaded document into existent document
	 * 
	 * @param existentDocument
	 * @param uploadedDocument
	 * @return
	 */
	private static YoungDocument copyDocument(YoungDocument existentDocument,
			YoungDocument uploadedDocument) {
		existentDocument.setAbsSourceFileName(uploadedDocument
				.getAbsSourceFileName());
		existentDocument.setMimeType(uploadedDocument.getMimeType());
		existentDocument.setSize(uploadedDocument.getSize());
		existentDocument.setInputStream(uploadedDocument.getInputStream());
		return existentDocument;
	}
}
