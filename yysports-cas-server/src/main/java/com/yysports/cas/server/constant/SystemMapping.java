/**
 * 
 */
package com.yysports.cas.server.constant;

/**
 * @author RAY
 *
 */
public enum SystemMapping {

	MIDDLE(1), MEMBER(2), EC(3);

	private Integer key;

	private SystemMapping(Integer key) {
		this.key = key;
	}

	public static String getDesc(Integer key) {
		for (SystemMapping map : SystemMapping.values()) {
			if (map.key == key) {
				return map.name();
			}
		}
		throw new RuntimeException("enum key not defind");
	}
}
