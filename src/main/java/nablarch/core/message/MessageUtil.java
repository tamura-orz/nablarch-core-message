package nablarch.core.message;

import nablarch.core.cache.BasicStaticDataCache;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;

/**
 * アプリケーションがメッセージを取得する際に使用するユーティリティクラス。
 * <p/>
 * {@link SystemRepository}から{@link StringResourceHolder}を取得する。
 * 取得できなかった場合は、{@link PropertiesStringResourceLoader}でロードしたリソースキャッシュを持つ{@link StringResourceHolder}を取得する。
 *
 * @author Koichi Asano
 * @see SystemRepository
 * @see PropertiesStringResourceLoader
 * @see StringResourceHolder
 */
@Published
public final class MessageUtil {

    /**
     * メッセージリソースのコンポーネント名。
     */
    private static final String STRING_RESOURCE_HOLDER_NAME = "stringResourceHolder";

    /**
     * {@link StringResourceHolder}の初期値。
     */
    private static final StringResourceHolder DEFAULT_STRING_RESOURCE_HOLDER;

    static {
        BasicStaticDataCache cache = new BasicStaticDataCache();
        cache.setLoader(new PropertiesStringResourceLoader());
        cache.initialize();
        DEFAULT_STRING_RESOURCE_HOLDER = new StringResourceHolder();
        DEFAULT_STRING_RESOURCE_HOLDER.setStringResourceCache(cache);
    }

    /**
     * 隠蔽コンストラクタ。
     */
    private MessageUtil() {
    }

    /**
     * {@link StringResourceHolder}をリポジトリから取得する。
     * @return 取得したStringResourceHolder
     */
    private static StringResourceHolder getStringResourceHolder() {
        StringResourceHolder holder = (StringResourceHolder) SystemRepository.getObject(STRING_RESOURCE_HOLDER_NAME);
        return (holder == null) ? DEFAULT_STRING_RESOURCE_HOLDER : holder;
    }

    /**
     * メッセージを生成する。
     * テンプレート文字列が以下であるときの例を示す。<br/>
     * 「errors.maxLength={0}は{1}文字以下で入力してください。」<br/>
     * 例:
     * <pre>
     * {@code
     * Message message = MessageUtil.createMessage(MessageLevel.ERROR, "errors.maxLength", "sample", 2);
     * String str = message.formatMessage(); //--> sampleは2文字以下で入力してください。
     * }</pre>
     *
     * @param level メッセージレベル
     * @param messageId メッセージID
     * @param options メッセージフォーマットに使用するオプション引数
     * @return 生成した{@link Message}
     * @see MessageLevel
     * @see Message
     */
    public static Message createMessage(MessageLevel level, String messageId,
            Object... options) {
        return new Message(level, getStringResource(messageId), options);
    }

    /**
     * メッセージIDに対応する{@link StringResource}を取得する。
     * メッセージIDがnullである場合は、nullを返す。
     * <p/>
     * テンプレート文字列が以下であるときの例を示す。<br/>
     * 「errors.maxLength={0}は{1}文字以下で入力してください。」<br/>
     * 例:
     * <pre>
     * {@code
     * StringResource resource = MessageUtil.getStringResource("errors.maxLength"); //-->メッセージIDに対応する文字列リソースを取得。
     * }</pre>
     * 
     * @param messageId メッセージID
     * @return 取得したメッセージ
     */
    public static StringResource getStringResource(String messageId) {
        return getStringResourceHolder().get(messageId);
    }
}
