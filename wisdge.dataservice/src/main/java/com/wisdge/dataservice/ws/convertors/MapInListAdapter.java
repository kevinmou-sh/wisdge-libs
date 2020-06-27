package com.wisdge.dataservice.ws.convertors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import com.wisdge.dataservice.ws.convertors.MapConvertor.MapEntry;

/**
 * Map适配器 for List
 * 
 * @author Kevin MOU
 *
 */
@XmlType(name = "MapInListAdapter")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapInListAdapter extends XmlAdapter<MapInListConvertor, List<Map<Object, Object>>> {

	@Override
	public MapInListConvertor marshal(List<Map<Object, Object>> list) throws Exception {
		MapInListConvertor convertor = new MapInListConvertor();
		for (Map<Object, Object> map : list) {
			MapConvertor mc = new MapConvertor();
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				MapConvertor.MapEntry e = new MapConvertor.MapEntry(entry);
				mc.addEntry(e);
			}
			convertor.addMapConvertor(mc);
		}
		return convertor;
	}

	@Override
	public List<Map<Object, Object>> unmarshal(MapInListConvertor convertor) throws Exception {
		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		for (MapConvertor mc : convertor.getMapConvertors()) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			for (MapEntry entry : mc.getEntries()) {
				map.put(entry.getKey(), entry.getValue());
			}
			list.add(map);
		}
		return list;
	}

}
