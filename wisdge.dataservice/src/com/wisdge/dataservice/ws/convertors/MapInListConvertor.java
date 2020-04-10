package com.wisdge.dataservice.ws.convertors;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Map转换器 for List
 * 
 * @author Kevin MOU
 *
 */
@XmlType(name = "MapInListConvertor")
@XmlAccessorType(XmlAccessType.FIELD)
public class MapInListConvertor {
	private List<MapConvertor> mapConvertors = new ArrayList<MapConvertor>();

	public void addMapConvertor(MapConvertor mapConvertor) {
		mapConvertors.add(mapConvertor);
	}

	public List<MapConvertor> getMapConvertors() {
		return mapConvertors;
	}

}
