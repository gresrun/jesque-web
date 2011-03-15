package net.greghaines.jesque.web.dao;

import net.greghaines.jesque.web.KeyInfo;

public interface KeysDAO
{	
	KeyInfo getKeyInfo(String key);
	
	KeyInfo getKeyInfo(String key, int offset, int count);
}
