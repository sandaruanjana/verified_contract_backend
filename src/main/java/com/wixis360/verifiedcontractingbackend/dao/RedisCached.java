package com.wixis360.verifiedcontractingbackend.dao;

import java.util.Set;

public interface RedisCached {
    Set<String> getKeys(byte[] keys);
    Object getCached(final byte[] sessionId);
    String updateCached(final byte[] key, final byte[] session, final Long expireSec);
    String deleteCached(byte[] key);
}
