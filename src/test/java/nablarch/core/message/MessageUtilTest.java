package nablarch.core.message;

import nablarch.core.ThreadContext;
import nablarch.core.cache.BasicStaticDataCache;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.repository.SystemRepository;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import nablarch.test.support.SystemRepositoryResource;
import nablarch.test.support.db.helper.DatabaseTestRunner;
import nablarch.test.support.db.helper.VariousDbTestHelper;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

@RunWith(DatabaseTestRunner.class)
public class MessageUtilTest {

    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource(
            "nablarch/core/message/message-resource-initialload-test.xml");

    @BeforeClass
    public static void classSetup() throws Exception {
        VariousDbTestHelper.createTable(TestMessage2.class);
    }

    @Test
    public void testGetMessageObject() throws Exception {

        VariousDbTestHelper.setUpTable(
                new TestMessage2("10001", "ja","メッセージ001"),
                new TestMessage2("10001", "en","Message001"),
                new TestMessage2("10002", "ja","メッセージ002"),
               new TestMessage2("10002", "en","Message002")
        );

        BasicStringResourceLoader stringResourceLoader = repositoryResource.getComponentByType(BasicStringResourceLoader.class);
        stringResourceLoader.setDbManager( (SimpleDbTransactionManager) repositoryResource.getComponent("dbManager"));
        BasicStaticDataCache<StringResource> cache = (BasicStaticDataCache<StringResource>) SystemRepository.getObject("stringResourceCache");
        cache.initialize();

        StringResource messageObject = MessageUtil.getStringResource("10001");
        assertEquals("10001", messageObject.getId());
        assertEquals("メッセージ001", messageObject.getValue(Locale.JAPANESE));
    }

    @Test
    public void testCreateResultMessage() throws Exception {

        VariousDbTestHelper.setUpTable(
                new TestMessage2("10001", "ja","メッセージ001"),
                new TestMessage2("10001", "en","Message001"),
                new TestMessage2("10002", "ja","メッセージ002"),
                new TestMessage2("10002", "en","Message002"),
                new TestMessage2("10003", "ja","メッセージ003"),
               new TestMessage2("10003", "en","Message003")
        );

        BasicStringResourceLoader stringResourceLoader = repositoryResource.getComponentByType(BasicStringResourceLoader.class);
        stringResourceLoader.setDbManager( (SimpleDbTransactionManager) repositoryResource.getComponent("dbManager"));
        BasicStaticDataCache<StringResource> cache = (BasicStaticDataCache<StringResource>) SystemRepository.getObject("stringResourceCache");
        cache.initialize();

        Message message3 = MessageUtil.createMessage(MessageLevel.INFO, "10003");
        Message message2 = MessageUtil.createMessage(MessageLevel.INFO, "10002", message3, "test2");

        assertEquals(MessageLevel.INFO, message2.getLevel());
        assertEquals("10002", message2.getMessageId());
        assertEquals("メッセージ002", message2.formatMessage(Locale.JAPANESE));
    }

    /**
     * スレッドコンテキストに言語が設定されていない場合、
     * VMのデフォルトロケールの言語が使用されること。
     */
    @Test
    public void testDefaultLocale() throws Exception {

        VariousDbTestHelper.setUpTable(
                new TestMessage2("10001", "ja","メッセージ001"),
                new TestMessage2("10001", "en","Message001"),
                new TestMessage2("10002", "ja","メッセージ002"),
                new TestMessage2("10002", "en","Message002"),
                new TestMessage2("10003", "ja","メッセージ003"),
                new TestMessage2("10003", "en","Message003")
        );

        BasicStringResourceLoader stringResourceLoader = repositoryResource.getComponentByType(BasicStringResourceLoader.class);
        stringResourceLoader.setDbManager( (SimpleDbTransactionManager) repositoryResource.getComponent("dbManager"));
        BasicStaticDataCache<StringResource> cache = (BasicStaticDataCache<StringResource>) SystemRepository.getObject("stringResourceCache");
        cache.initialize();

        Message message3 = MessageUtil.createMessage(MessageLevel.INFO, "10003");
        Message message2 = MessageUtil.createMessage(MessageLevel.INFO, "10002", message3, "test2");

        ThreadContext.setLanguage(null);

        assertEquals(MessageLevel.INFO, message2.getLevel());
        assertEquals("10002", message2.getMessageId());
        assertEquals("メッセージ002", message2.formatMessage());
    }

    @Test
    public void testDefaultProperties() throws Exception {
        SystemRepository.clear();

        Message message = MessageUtil.createMessage(MessageLevel.INFO, "default.key");

        assertEquals(MessageLevel.INFO, message.getLevel());
        assertEquals("default.key", message.getMessageId());
        assertEquals("デフォルト", message.formatMessage());

        message = MessageUtil.createMessage(MessageLevel.INFO, "load.all.key");

        assertEquals(MessageLevel.INFO, message.getLevel());
        assertEquals("load.all.key", message.getMessageId());
        assertEquals("loadAllValue", message.formatMessage());
    }
}
