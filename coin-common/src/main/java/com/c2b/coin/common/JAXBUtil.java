package com.c2b.coin.common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

/**
 * @author <a href="mailto:guo_xp@163.com">guoxinpeng</a>
 * @version 1.0 2016/11/18 16:00
 * @projectname repayment
 * @packname com.yingu.service.repayment.util
 */
public class JAXBUtil {
	/**
	 * JavaBean转换成xml
	 * 默认编码UTF-8
	 *
	 * @param obj
	 * @return
	 */
	public static String convertToXml(Object obj) {
		return convertToXml(obj, GlobalConstant.ENCODING_UTF8);
	}

	/**
	 * JavaBean转换成xml
	 *
	 * @param obj
	 * @param encoding
	 * @return
	 */
	public static String convertToXml(Object obj, String encoding) {
		String result = null;
		try {
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);
			result = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * xml转换成JavaBean
	 *
	 * @param xml
	 * @param c
	 * @return
	 */
	public static <T> T converyToBean(String xml, Class<T> c) {
		T t = null;
		try {
			JAXBContext context = JAXBContext.newInstance(c);
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes(GlobalConstant.ENCODING_UTF8));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StreamSource(byteArrayInputStream), c);
			t = jaxbElement.getValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return t;
	}
}
