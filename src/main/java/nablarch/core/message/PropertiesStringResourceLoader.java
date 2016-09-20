package nablarch.core.message;

import nablarch.core.cache.StaticDataLoader;
import nablarch.core.util.FileUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


/**
 * 文字列リソースをプロパティファイルから取得するクラス。
 * 
 * @author kawasima
 * @author Kiyohito Itoh
 */
public class PropertiesStringResourceLoader implements StaticDataLoader<StringResource> {

    /** プロパティファイルからロードしたメッセージ一覧 */
    private final Map<String, Map<String, String>> messages = new HashMap<String, Map<String, String>>();

    /** プロパティファイルが配置されているディレクトリ */
    private String directory = "classpath:";

    /** プロパティファイル名 */
    private String fileName = "messages";

    /** デフォルトのロケール */
    private String defaultLocale = Locale.getDefault().getLanguage();

    /** ロケール一覧 */
    private Set<String> locales = new HashSet<String>();

    @Override
    public StringResource getValue(final Object key) {
        if (messages.isEmpty()) {
            load();
        }

        return key != null && messages.containsKey(key.toString())
                ? new BasicStringResource(key.toString(), messages.get(key.toString()))
                : null;
    }

    @Override
    public List<StringResource> loadAll() {
        if (messages.isEmpty()) {
            load();
        }
        final List<StringResource> resources = new ArrayList<StringResource>();
        for (Entry<String, Map<String, String>> entry : messages.entrySet()) {
            resources.add(new BasicStringResource(entry.getKey(), entry.getValue()));
        }
        return resources;
    }

    /**
     * プロパティファイルからメッセージをロードする。
     */
    private synchronized void load() {
        if (!messages.isEmpty()) {
            return;
        }
        load(defaultLocale, directory + fileName + ".properties");
        for (String locale : locales) {
            load(locale, directory + fileName + '_' + locale + ".properties");
        }
    }

    /**
     * プロパティファイルからメッセージをロードする。
     *
     * @param locale ロケール
     * @param path プロパティファイルのパス
     */
    private void load(final String locale, final String path) {

        final InputStream inStream = FileUtil.getResource(path);
        final Properties props = new Properties();
        Reader reader = null;
        try {
            reader = new InputStreamReader(inStream, "UTF-8");
            props.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException("failed to load the file. file path = [" + path + ']', e);
        } finally {
            FileUtil.closeQuietly(reader);
        }

        for (String name : props.stringPropertyNames()) {
            if (!messages.containsKey(name)) {
                messages.put(name, new HashMap<String, String>());
            }
            messages.get(name).put(locale, props.getProperty(name));
        }
    }

    @Override
    public List<StringResource> getValues(final String indexName, final Object key) {
        return null;
    }

    @Override
    public Object getId(final StringResource value) {
        return value.getId();
    }

    @Override
    public Object generateIndexKey(final String indexName, final StringResource value) {
        return null;
    }

    @Override
    public List<String> getIndexNames() {
        return null;
    }

    /**
     * ディレクトリを設定する。
     *
     * @param directory ディレクトリ
     */
    public void setDirectory(final String directory) {
        this.directory = directory;
    }

    /**
     * プロパティファイル名を設定する。
     *
     * @param fileName プロパティファイル名
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * デフォルトのロケールを設定する。
     *
     * @param defaultLocale デフォルトのロケール
     */
    public void setDefaultLocale(final String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * ロケール一覧を設定する。
     *
     * @param locales ロケール一覧
     */
    public void setLocales(final List<String> locales) {
        this.locales.addAll(locales);
    }
}
