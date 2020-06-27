package com.wisdge.dataservice.ws.convertors;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Map适配器
 * 
 * @author Kevin MOU
 * @version 1.0.0
 */

@XmlType(name = "MapAdapter")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapAdapter extends XmlAdapter<MapConvertor, Map<Object, Object>> {

	@Override
	public MapConvertor marshal(Map<Object, Object> map) throws Exception {
		MapConvertor convertor = new MapConvertor();
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			MapConvertor.MapEntry e = new MapConvertor.MapEntry(entry);
			convertor.addEntry(e);
		}
		return convertor;
	}

	@Override
	public Map<Object, Object> unmarshal(MapConvertor map) throws Exception {
		Map<Object, Object> result = new HashMap<Object, Object>();
		for (MapConvertor.MapEntry e : map.getEntries()) {
			result.put(e.getKey(), e.getValue());
		}
		return result;
	}
}
