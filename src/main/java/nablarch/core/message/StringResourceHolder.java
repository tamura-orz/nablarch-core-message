package nablarch.core.message;


import nablarch.core.cache.StaticDataCache;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;


/**
 * ユーザに通知するメッセージの元となる文字列リソースを保持するクラス。<br/>
 * 文字列リソースは静的データキャッシュに保持する。
 * 
 * @author Koichi Asano
 *
 */
@Published(tag = "architect")
public class StringResourceHolder {
    
    /**
     * 文字列リソースのキャッシュ。
     */
    private StaticDataCache<StringResource> stringResourceCache;
    
    /**
     * 文字列リソースのキャッシュを設定する。
     * 
     * @param stringResourceCache 文字列リソースのキャッシュ
     */
    public void setStringResourceCache(StaticDataCache<StringResource> stringResourceCache) {
        this.stringResourceCache = stringResourceCache;
    }

    /**
     * 文字列リソースを取得する。
     * 
     * @param messageId 取得する文字列リソースのメッセージID
     * @return メッセージIDに対応する文字列リソース
     * @throws MessageNotFoundException メッセージIDに対応するメッセージが存在しなかった場合
     */
    public StringResource get(String messageId) throws MessageNotFoundException {
        if (StringUtil.isNullOrEmpty(messageId)) {
            throw new MessageNotFoundException("null or empty message id was specified."
                    + " please set message id.");
        }
        StringResource message = stringResourceCache.getValue(messageId);
        if (message == null) {
            throw new MessageNotFoundException("message was not found."
                    + " message id = " + messageId);
        }
        return message;
    }
}
