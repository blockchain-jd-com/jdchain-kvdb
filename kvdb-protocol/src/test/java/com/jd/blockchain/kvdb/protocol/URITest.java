package com.jd.blockchain.kvdb.protocol;

import org.junit.Assert;
import org.junit.Test;

import java.net.UnknownHostException;

public class URITest {

    @Test
    public void testScheme() {
        boolean ok = true;
        try {
            new KVDBURI("kvdb://localhost:7078/test");
        } catch (Exception e) {
            ok = false;
        }
        Assert.assertTrue(ok);
        ok = true;
        try {
            new KVDBURI("http://localhost:7078/test");
        } catch (Exception e) {
            ok = false;
        }
        Assert.assertFalse(ok);

    }

    @Test
    public void testDatabase() {

        KVDBURI uri1 = new KVDBURI("kvdb://localhost:7078/test");
        Assert.assertTrue("test".equals(uri1.getDatabase()));
    }

    @Test
    public void testLocalhost() throws UnknownHostException {
        KVDBURI uri1 = new KVDBURI("kvdb://localhost:7078/test");
        KVDBURI uri2 = new KVDBURI("kvdb://127.0.0.1:7078/test");
        Assert.assertTrue(uri1.isLocalhost());
        Assert.assertTrue(uri2.isLocalhost());
//        Assert.assertTrue(URIUtils.isLocalhost(InetAddress.getLocalHost().getHostName()));
    }
}
