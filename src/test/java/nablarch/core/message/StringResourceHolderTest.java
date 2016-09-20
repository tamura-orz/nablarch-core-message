package nablarch.core.message;

import nablarch.core.cache.BasicStaticDataCache;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.repository.SystemRepository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.db.helper.DatabaseTestRunner;
import nablarch.test.support.db.helper.VariousDbTestHelper;

import java.sql.SQLException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

@RunWith(DatabaseTestRunner.class)
public class StringResourceHolderTest {

    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource(
            "nablarch/core/message/message-resource-ondemand-test.xml");

    @BeforeClass
    public static void classSetup() throws Exception {
    }

    @AfterClass
    public static void classDown() throws Exception {
    }

    @Test
    public void testGetMessageOnDemand() throws Exception {

        VariousDbTestHelper.createTable(TestMessage2.class);
        VariousDbTestHelper.setUpTable(
                new TestMessage2("10001", "ja", "メッセージ001"),
                new TestMessage2("10001", "en", "Message001"),
                new TestMessage2("10002", "ja", "メッセージ002"),
                new TestMessage2("10002", "en", "Message002")
        );

        BasicStringResourceLoader stringResourceLoader = repositoryResource.getComponentByType(
                BasicStringResourceLoader.class);
        stringResourceLoader.setDbManager((SimpleDbTransactionManager) repositoryResource.getComponent("dbManager"));
        BasicStaticDataCache<StringResource> cache = SystemRepository.get("stringResourceCache");
        cache.initialize();

        StringResourceHolder resource = repositoryResource.getComponentByType(StringResourceHolder.class);

        SimpleDbTransactionManager dbManager = repositoryResource.getComponent("dbManager");
        dbManager.beginTransaction();

        String enMsg1 = null;
        String jaMsg1 = null;
        String enMsg2 = null;
        String jaMsg2 = null;
        try {
            enMsg1 = resource.get("10001")
                    .getValue(Locale.ENGLISH);
            jaMsg1 = resource.get("10001")
                    .getValue(Locale.JAPANESE);

            enMsg2 = resource.get("10002")
                    .getValue(Locale.ENGLISH);
            jaMsg2 = resource.get("10002")
                    .getValue(Locale.JAPANESE);
        } finally {
            dbManager.endTransaction();
        }
        assertEquals("メッセージ001", jaMsg1);
        assertEquals("Message001", enMsg1);
        assertEquals("メッセージ002", jaMsg2);
        assertEquals("Message002", enMsg2);
    }

    @Test
    public void testGetMessageInitialLoad() throws Exception {

        VariousDbTestHelper.createTable(TestMessage2.class);
        VariousDbTestHelper.setUpTable(
                new TestMessage2("10001", "ja", "メッセージ001"),
                new TestMessage2("10001", "en", "Message001"),
                new TestMessage2("10002", "ja", "メッセージ002"),
                new TestMessage2("10002", "en", "Message002")
        );

        BasicStringResourceLoader stringResourceLoader = repositoryResource.getComponentByType(
                BasicStringResourceLoader.class);
        stringResourceLoader.setDbManager((SimpleDbTransactionManager) repositoryResource.getComponent("dbManager"));
        BasicStaticDataCache<StringResource> cache = SystemRepository.get("stringResourceCache");
        cache.initialize();

        StringResourceHolder resource = repositoryResource.getComponentByType(StringResourceHolder.class);

        SimpleDbTransactionManager dbManager = repositoryResource.getComponent(
                "dbManager");
        dbManager.beginTransaction();

        String enMsg1 = null;
        String jaMsg1 = null;
        String enMsg2 = null;
        String jaMsg2 = null;
        try {

            enMsg1 = resource.get("10001")
                    .getValue(Locale.ENGLISH);
            jaMsg1 = resource.get("10001")
                    .getValue(Locale.JAPANESE);

            enMsg2 = resource.get("10002")
                    .getValue(Locale.ENGLISH);
            jaMsg2 = resource.get("10002")
                    .getValue(Locale.JAPANESE);
        } finally {
            dbManager.endTransaction();
        }

        assertEquals("メッセージ001", jaMsg1);
        assertEquals("Message001", enMsg1);
        assertEquals("メッセージ002", jaMsg2);
        assertEquals("Message002", enMsg2);
    }


    @Test
    public void testGetMessageNotFoundOnDemand() throws Exception {
        String initFileName = "nablarch/core/message/message-resource-ondemand-test.xml";
        doTestGetMessageNotFound(initFileName);
    }


    @Test
    public void testGetMessageNotFoundInitialLoad() throws Exception {
        String initFileName = "nablarch/core/message/message-resource-initialload-test.xml";
        doTestGetMessageNotFound(initFileName);
    }

    private void doTestGetMessageNotFound(String initFileName) throws SQLException {

        VariousDbTestHelper.createTable(TestMessage2.class);
        VariousDbTestHelper.setUpTable(
                new TestMessage2("10001", "ja", "メッセージ001"),
                new TestMessage2("10001", "en", "Message001"),
                new TestMessage2("10002", "ja", "メッセージ002"),
                new TestMessage2("10002", "en", "Message002")
        );

        BasicStringResourceLoader stringResourceLoader = repositoryResource.getComponentByType(
                BasicStringResourceLoader.class);
        stringResourceLoader.setDbManager((SimpleDbTransactionManager) repositoryResource.getComponent("dbManager"));
        BasicStaticDataCache<StringResource> cache = SystemRepository.get("stringResourceCache");
        cache.initialize();

        StringResourceHolder resource = repositoryResource.getComponentByType(StringResourceHolder.class);

        SimpleDbTransactionManager dbManager = repositoryResource.getComponent("dbManager");
        dbManager.beginTransaction();

        try {
            try {
                // 存在しないメッセージだと、例外が発生するはず
                resource.get("10003");
                fail("例外が発生するはず");
            } catch (MessageNotFoundException e) {
                assertThat(e.getMessage(), containsString("message was not found"));
                assertThat(e.getMessage(), containsString("message id = 10003"));
            }
        } finally {
            dbManager.endTransaction();
        }
    }

    @Test
    public void testGetMessageIdIsNullInitialLoad() throws Exception {
        String initFileName = "nablarch/core/message/message-resource-initialload-test.xml";
        doTestGetMessageIdIsNull(initFileName);
    }

    @Test
    public void testGetMessageIdIsNullOnDemand() throws Exception {
        String initFileName = "nablarch/core/message/message-resource-ondemand-test.xml";
        doTestGetMessageIdIsNull(initFileName);
    }

    private void doTestGetMessageIdIsNull(String initFileName) throws SQLException {

        VariousDbTestHelper.createTable(TestMessage2.class);
        VariousDbTestHelper.setUpTable(
                new TestMessage2("10001", "ja", "メッセージ001"),
                new TestMessage2("10001", "en", "Message001"),
                new TestMessage2("10002", "ja", "メッセージ002"),
                new TestMessage2("10002", "en", "Message002")
        );

        BasicStringResourceLoader stringResourceLoader = repositoryResource.getComponentByType(
                BasicStringResourceLoader.class);
        stringResourceLoader.setDbManager((SimpleDbTransactionManager) repositoryResource.getComponent("dbManager"));
        BasicStaticDataCache<StringResource> cache = SystemRepository.get("stringResourceCache");
        cache.initialize();

        StringResourceHolder resource = repositoryResource.getComponentByType(StringResourceHolder.class);

        SimpleDbTransactionManager dbManager = repositoryResource.getComponent(
                "dbManager");
        dbManager.beginTransaction();

        try {
            try {
                // 存在しないメッセージだと、例外が発生するはず
                resource.get(null);
                fail("例外が発生するはず");
            } catch (MessageNotFoundException e) {
                assertThat(e.getMessage(), containsString("null or empty message id was specified"));
                assertThat(e.getMessage(), containsString("please set message id"));
            }
        } finally {
            dbManager.endTransaction();
        }
    }

}
