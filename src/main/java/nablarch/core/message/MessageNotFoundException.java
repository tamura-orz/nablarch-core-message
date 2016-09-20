package nablarch.core.message;

import nablarch.core.util.annotation.Published;

/**
 * メッセージが存在しなかった場合に発生する例外。
 * 
 * @author Koichi Asano
 */
@Published
public class MessageNotFoundException extends RuntimeException {

    /**
     *  MessageNotFoundExceptionオブジェクトを生成する。
     * 
     * @param message 例外メッセージ
     */
    public MessageNotFoundException(String message) {
        super(message);
    }
}
