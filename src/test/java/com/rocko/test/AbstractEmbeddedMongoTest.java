package com.rocko.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class AbstractEmbeddedMongoTest {

    public static final String HOST = "localhost";
    public static final int PORT = 12345;

    public static MongodExecutable mongodExecutable;

    @BeforeClass
    public static void setup() throws Exception {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(HOST, PORT, Network.localhostIsIPv6())).build();

        MongodExecutable mongodExecutable = null;
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
    }

    @AfterClass
    public static void cleanup() {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    public static MongoClient getMongoClient() {
        return new MongoClient(HOST, PORT);
    }

}
